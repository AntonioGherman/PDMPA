package com.example.inventorymanager;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.inventorymanager.ui.dialogs.scan.ScanBarcodeDialog;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.common.util.concurrent.ListenableFuture;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View topBar = findViewById(R.id.top_bar);

        TextView title = topBar.findViewById(R.id.tvTitle);
        TextView subtitle = topBar.findViewById(R.id.tvSubtitle);
        ImageButton btnAdd = topBar.findViewById(R.id.btnAdd);
        ImageButton btnScan = topBar.findViewById(R.id.btnFilter);

        btnAdd.setOnClickListener(v -> {
            // acțiune globală (ex: adăugare produs)
        });

        btnScan.setOnClickListener(v -> {
            new ScanBarcodeDialog().show(
                    getSupportFragmentManager(),
                    "ScanBarcodeDialog"
            );
        });

        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);

        NavHostFragment navHostFragment =
                (NavHostFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.nav_host_fragment);

        NavController navController = navHostFragment.getNavController();

        NavigationUI.setupWithNavController(bottomNav, navController);
    }

    private ProcessCameraProvider cameraProvider;
    private Preview preview;

    public void startCamera(PreviewView previewView, Runnable onCameraReady) {
        if (cameraProvider != null) return;

        ListenableFuture<ProcessCameraProvider> future =
                ProcessCameraProvider.getInstance(this);

        future.addListener(() -> {
            try {
                cameraProvider = future.get();

                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(
                        this,
                        CameraSelector.DEFAULT_BACK_CAMERA,
                        preview
                );

                // 🔔 CAMERA ESTE GATA
                runOnUiThread(onCameraReady);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(this));
    }


    public void stopCamera() {
        if (cameraProvider != null) {
            cameraProvider.unbindAll();
            cameraProvider = null;
        }
    }
}
