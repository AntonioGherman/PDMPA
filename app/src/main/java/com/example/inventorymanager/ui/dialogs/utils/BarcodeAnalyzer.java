package com.example.inventorymanager.ui.dialogs.utils;

import android.media.Image;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;

import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;

public class BarcodeAnalyzer implements ImageAnalysis.Analyzer {

    public interface Callback {
        void onDetected(String value);
    }

    private final BarcodeScanner scanner;
    private final Callback callback;
    private boolean locked = false;

    public BarcodeAnalyzer(Callback callback) {
        this.callback = callback;
        scanner = BarcodeScanning.getClient(
                new BarcodeScannerOptions.Builder()
                        .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
                        .build()
        );
    }

    @Override
    @OptIn(markerClass = ExperimentalGetImage.class)
    public void analyze(@NonNull ImageProxy imageProxy) {

        if (locked) {
            imageProxy.close();
            return;
        }

        Image img = imageProxy.getImage();
        if (img == null) {
            imageProxy.close();
            return;
        }

        InputImage image = InputImage.fromMediaImage(
                img,
                imageProxy.getImageInfo().getRotationDegrees()
        );

        scanner.process(image)
                .addOnSuccessListener(barcodes -> {
                    for (Barcode b : barcodes) {
                        if (b.getRawValue() != null && !locked) {
                            locked = true;
                            callback.onDetected(b.getRawValue());
                            break;
                        }
                    }
                })
                .addOnCompleteListener(task -> imageProxy.close());
    }
}


