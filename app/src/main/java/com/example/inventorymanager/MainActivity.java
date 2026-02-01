package com.example.inventorymanager;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.inventorymanager.ui.dialogs.add.AddProductDialog;
import com.example.inventorymanager.ui.dialogs.scan.ScanBarcodeDialog;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

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

        cameraExecutor = Executors.newSingleThreadExecutor();

        btnAdd.setOnClickListener(v -> {
           new AddProductDialog().show(getSupportFragmentManager(),"AddProductDialog");
        });

        btnScan.setOnClickListener(v -> {
             ScanBarcodeDialog.newInstance(false).show(
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
    private ExecutorService cameraExecutor;

    public ExecutorService getCameraExecutor() {
        return cameraExecutor;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cameraExecutor != null) {
            cameraExecutor.shutdown();
        }
    }

}
