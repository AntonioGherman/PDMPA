package com.example.inventorymanager.ui.dialogs.scan;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.OptIn;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import com.example.inventorymanager.MainActivity;
import com.example.inventorymanager.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ScanBarcodeDialog extends BottomSheetDialogFragment {

    private ProgressBar progressCamera;

    private PreviewView previewView;

    private OnBarcodeScannedListener listener;

    private ProcessCameraProvider cameraProvider;
    private ExecutorService cameraExecutor;
    private boolean isScanning = false;


    public void setOnBarcodeScannedListener(OnBarcodeScannedListener listener) {
        this.listener = listener;
    }



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cameraExecutor = Executors.newSingleThreadExecutor();
        if (getArguments() != null) {
            simpleScan = getArguments().getBoolean(ARG_SIMPLE_SCAN, false);
        }
    }

    private static final String ARG_SIMPLE_SCAN = "arg_simple_scan";
    private boolean simpleScan;

    public static ScanBarcodeDialog newInstance(boolean simpleScan) {
        ScanBarcodeDialog dialog = new ScanBarcodeDialog();
        Bundle args = new Bundle();
        args.putBoolean(ARG_SIMPLE_SCAN, simpleScan);
        dialog.setArguments(args);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dialog_scan_barcode, container, false);
        progressCamera = view.findViewById(R.id.progressCamera);
        previewView = view.findViewById(R.id.previewView);

        if(simpleScan)
        {
            view.findViewById(R.id.searchContainer).setVisibility(View.GONE);
        }

        progressCamera.setVisibility(View.VISIBLE);
        previewView.setVisibility(View.INVISIBLE);

        ImageButton btnClose = view.findViewById(R.id.btnClose);
        btnClose.setOnClickListener(v -> {
            dismiss();
        });
        return view;
    }

    private void onCameraReady() {
        if (getView() == null) return;

        progressCamera.setVisibility(View.GONE);
        previewView.setVisibility(View.VISIBLE);
        previewView.setImplementationMode(
                PreviewView.ImplementationMode.COMPATIBLE
        );
    }


    @Override
    public void onStart() {
        super.onStart();
        startCamera();
    }


    @OptIn(markerClass = ExperimentalGetImage.class)
    private void startCamera() {

        ListenableFuture<ProcessCameraProvider> future =
                ProcessCameraProvider.getInstance(requireContext());

        future.addListener(() -> {
            try {
                cameraProvider = future.get();

                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                ImageAnalysis imageAnalysis =
                        new ImageAnalysis.Builder()
                                .setBackpressureStrategy(
                                        ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST
                                )
                                .build();

                imageAnalysis.setAnalyzer(cameraExecutor, image -> {

                    if (isScanning || image.getImage() == null) {
                        image.close();
                        return;
                    }

                    InputImage inputImage = InputImage.fromMediaImage(
                            image.getImage(),
                            image.getImageInfo().getRotationDegrees()
                    );

                    BarcodeScanner scanner = BarcodeScanning.getClient();

                    scanner.process(inputImage)
                            .addOnSuccessListener(barcodes -> {
                                for (Barcode barcode : barcodes) {
                                    if (barcode.getRawValue() != null) {
                                        isScanning = true;

                                        if (listener != null) {
                                            listener.onBarcodeScanned(
                                                    barcode.getRawValue()
                                            );
                                        }

                                        dismiss();
                                        break;
                                    }
                                }
                            })
                            .addOnCompleteListener(task -> image.close());
                });

                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(
                        this, // 🔥 CHEIA
                        CameraSelector.DEFAULT_BACK_CAMERA,
                        preview,
                        imageAnalysis
                );

                requireActivity().runOnUiThread(this::onCameraReady);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(requireContext()));
    }


    @OptIn(markerClass = ExperimentalGetImage.class)
    private void bindBarcodeAnalyzer(ImageAnalysis imageAnalysis) {

        BarcodeScanner scanner = BarcodeScanning.getClient();

        imageAnalysis.setAnalyzer(
                ((MainActivity) requireActivity()).getCameraExecutor(),
                image -> {
                    if (image.getImage() == null) {
                        image.close();
                        return;
                    }

                    InputImage inputImage = InputImage.fromMediaImage(
                            image.getImage(),
                            image.getImageInfo().getRotationDegrees()
                    );

                    scanner.process(inputImage)
                            .addOnSuccessListener(barcodes -> {
                                for (Barcode barcode : barcodes) {
                                    if (barcode.getRawValue() != null && listener != null) {

                                        listener.onBarcodeScanned(barcode.getRawValue());

//                                        // oprește camera & închide dialog
//                                        if (getActivity() instanceof MainActivity) {
//                                            ((MainActivity) getActivity()).stopCamera();
//                                        }

                                        dismiss();
                                        break;
                                    }
                                }
                            })
                            .addOnCompleteListener(task -> image.close());
                }
        );
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        if (cameraProvider != null) {
            cameraProvider.unbindAll();
        }
        if (cameraExecutor != null) {
            cameraExecutor.shutdown();
        }
    }

}
