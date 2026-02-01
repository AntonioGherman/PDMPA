package com.example.inventorymanager.data.repository;

import androidx.annotation.NonNull;

import com.example.inventorymanager.data.model.Product;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProductRepository {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void addProduct(
            @NonNull Product product,
            @NonNull Runnable onSuccess,
            @NonNull Runnable onError
    ) {
        db.collection("products")
                .add(product)
                .addOnSuccessListener(docRef -> {
                    product.setId(docRef.getId());
                    onSuccess.run();
                })
                .addOnFailureListener(e -> onError.run());
    }
}
