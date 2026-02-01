package com.example.inventorymanager.ui.alerts;

import android.content.Context;

import com.example.inventorymanager.data.firebase.NotificationUtil;
import com.example.inventorymanager.data.model.Product;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

public class StockAlertListener {

    private static ListenerRegistration registration;

    public static void start(Context context) {

        registration = FirebaseFirestore.getInstance()
                .collection("products")
                .addSnapshotListener((snap, e) -> {

                    if (snap == null) return;

                    for (DocumentChange dc : snap.getDocumentChanges()) {

                        if (dc.getType() != DocumentChange.Type.MODIFIED)
                            continue;

                        Product p = dc.getDocument().toObject(Product.class);

                        StockStatus status = calculateStatus(p);

                        if (status == StockStatus.CRITICAL) {
                            NotificationUtil.show(
                                    context,
                                    dc.getDocument().getId().hashCode(),
                                    "🚨 Critical stock alert",
                                    p.getName() + " is out of stock!"
                            );
                        }

                        if (status == StockStatus.WARNING) {
                            NotificationUtil.show(
                                    context,
                                    dc.getDocument().getId().hashCode(),
                                    "⚠️ Stock running low",
                                    p.getName() + " is below minimum stock."
                            );
                        }
                    }
                });
    }

    private static StockStatus calculateStatus(Product p) {

        if (p.getQuantity() <= p.getMinStock()) {
            return StockStatus.CRITICAL;
        }

        if (p.getQuantity() <=  10 + p.getMinStock()) {
            return StockStatus.WARNING;
        }

        return StockStatus.OK;
    }

    public static void stop() {
        if (registration != null) {
            registration.remove();
        }
    }
}
