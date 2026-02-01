package com.example.inventorymanager.ui.alerts;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.inventorymanager.R;
import com.example.inventorymanager.data.firebase.NotificationUtil;
import com.example.inventorymanager.data.model.Product;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class StockAlertWorker extends Worker {

    public StockAlertWorker(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.e("WORKER_DEBUG", "doWork() STARTED");

        try {
            QuerySnapshot snapshot = Tasks.await(
                    FirebaseFirestore.getInstance()
                            .collection("products")
                            .get()
            );
            Log.e("WORKER_DEBUG", "Firestore fetched");

            for (DocumentSnapshot doc : snapshot) {

                Product p = doc.toObject(Product.class);
                if (p == null) continue;

                StockStatus newStatus = calculateStatus(p);

                String oldStatusStr = doc.getString("stockStatus");
                StockStatus oldStatus =
                        oldStatusStr == null
                                ? StockStatus.OK
                                : StockStatus.valueOf(oldStatusStr);

                if (newStatus != oldStatus) {

                    sendNotification(p, newStatus);

                    doc.getReference()
                            .update("stockStatus", newStatus.name());
                }
                Log.e("WORKER_DEBUG", "STATUS CHANGED for " + p.getName());

            }

            return Result.success();

        } catch (Exception e) {
            Log.e("StockAlertWorker", "Worker failed", e);
            return Result.retry();
        }
    }

    private StockStatus calculateStatus(Product p) {

        if (p.getQuantity() <= 0) {
            return StockStatus.CRITICAL;
        }

        if (p.getQuantity() < p.getMinStock()) {
            return StockStatus.WARNING;
        }

        return StockStatus.OK;
    }

    private void sendNotification(Product p, StockStatus status) {

        String title;
        String text;

        if (status == StockStatus.CRITICAL) {
            title = "Critical stock alert";
            text = p.getName() + " is out of stock!";
        } else {
            title = "Stock running low";
            text = p.getName() + " is below minimum stock.";
        }

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(
                        getApplicationContext(),
                        NotificationUtil.CHANNEL_ALERTS
                )
                        .setSmallIcon(R.drawable.ic_alert)
                        .setContentTitle(title)
                        .setContentText(text)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setAutoCancel(true);

        if (ActivityCompat.checkSelfPermission(
                getApplicationContext(),
                Manifest.permission.POST_NOTIFICATIONS
        ) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        int notificationId;

        if (p.getId() != null) {
            notificationId = p.getId().hashCode();
        } else {
            notificationId = (int) System.currentTimeMillis();
        }

        NotificationManagerCompat.from(getApplicationContext())
                .notify(notificationId, builder.build());

    }
}
