package com.example.inventorymanager.ui.dialogs.scan;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.view.PreviewView;

import com.example.inventorymanager.MainActivity;
import com.example.inventorymanager.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class ScanBarcodeDialog extends BottomSheetDialogFragment {
    private ProgressBar progressCamera;

    private PreviewView previewView;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dialog_scan_barcode, container, false);
        progressCamera = view.findViewById(R.id.progressCamera);
        previewView = view.findViewById(R.id.previewView);

        // inițial: arătăm loading
        progressCamera.setVisibility(View.VISIBLE);
        previewView.setVisibility(View.INVISIBLE);

        ImageButton btnClose = view.findViewById(R.id.btnClose);
        btnClose.setOnClickListener(v -> {
            //  Oprește camera
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).stopCamera();
            }

            // Închide dialogul
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

        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).startCamera(
                    previewView,
                    this::onCameraReady
            );
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        ((MainActivity) requireActivity()).stopCamera();
    }
}
