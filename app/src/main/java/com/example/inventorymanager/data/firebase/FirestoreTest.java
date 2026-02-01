package com.example.inventorymanager.data.firebase;

import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class FirestoreTest {

    public static void test() {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> data = new HashMap<>();
        data.put("test", "Firestore works");
        data.put("timestamp", System.currentTimeMillis());

        db.collection("test")
                .add(data)
                .addOnSuccessListener(doc ->
                        Log.d("FIRESTORE", "SUCCESS: " + doc.getId()))
                .addOnFailureListener(e ->
                        Log.e("FIRESTORE", "ERROR", e));
    }
}
