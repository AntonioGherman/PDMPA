package com.example.inventorymanager.ui.dialogs.utils;

import android.app.Activity;
import android.media.Image;

import androidx.annotation.OptIn;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageProxy;
import androidx.fragment.app.FragmentActivity;

import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;

import java.util.function.Consumer;

public class ProcessImage {

    private boolean scanning = false;
    private final Activity activity;
    private final Consumer<String> resultCallback;

    public ProcessImage(Activity activity, Consumer<String> resultCallback) {
        this.activity = activity;
        this.resultCallback = resultCallback;
    }

    @OptIn(markerClass = ExperimentalGetImage.class)
    public void process(
            BarcodeScanner scanner,
            ImageProxy imageProxy
    ) {
        if (scanning) {
            imageProxy.close();
            return;
        }

        Image mediaImage = imageProxy.getImage();
        if (mediaImage == null) {
            imageProxy.close();
            return;
        }

        InputImage image = InputImage.fromMediaImage(
                mediaImage,
                imageProxy.getImageInfo().getRotationDegrees()
        );

        scanner.process(image)
                .addOnSuccessListener(barcodes -> {
                    for (Barcode barcode : barcodes) {
                        String value = barcode.getRawValue();
                        if (value != null && !scanning) {
                            scanning = true;
                            activity.runOnUiThread(() ->
                                    resultCallback.accept(value)
                            );
                            break;
                        }
                    }
                })
                .addOnFailureListener(Throwable::printStackTrace)
                .addOnCompleteListener(task -> {
                    imageProxy.close(); // 🔥 OBLIGATORIU
                });
    }
}

