package com.example.inventorymanager;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

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
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.example.inventorymanager.data.firebase.NotificationUtil;
import com.example.inventorymanager.data.model.Product;
import com.example.inventorymanager.ui.alerts.StockAlertListener;
import com.example.inventorymanager.ui.alerts.StockAlertWorker;
import com.example.inventorymanager.ui.dialogs.add.AddProductDialog;
import com.example.inventorymanager.ui.dialogs.scan.ScanBarcodeDialog;
import com.example.inventorymanager.ui.dialogs.scan.ScannedProductDialog;
import com.example.inventorymanager.util.ScanType;
import com.example.inventorymanager.util.ToastUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
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
            ScanBarcodeDialog dialog = ScanBarcodeDialog.newInstance(false);

            dialog.setOnBarcodeScannedListener(this::onBarcodeScanned);

            dialog.show(getSupportFragmentManager(), "ScanBarDialog");
        });


        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);

        NavHostFragment navHostFragment =
                (NavHostFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.nav_host_fragment);

        NavController navController = navHostFragment.getNavController();

        NavigationUI.setupWithNavController(bottomNav, navController);
        StockAlertListener.start(this);

    }
    private ExecutorService cameraExecutor;

    public ExecutorService getCameraExecutor() {
        return cameraExecutor;
    }

    private void findProductByBarcode(String barcode) {

        FirebaseFirestore.getInstance()
                .collection("products")
                .whereEqualTo("barcode", barcode)
                .limit(1)
                .get()
                .addOnSuccessListener(snapshot -> {

                    if (!snapshot.isEmpty()) {
                        String productId = snapshot.getDocuments().get(0).getId();

                        showScannedProductDialog(productId);
                    } else {
                        Toast.makeText(
                                this,
                                "Product not found",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }

    private void showScannedProductDialog(String productId) {

        ScannedProductDialog dialog =
                ScannedProductDialog.newInstance(productId);

        dialog.setOnScanAgainListener(() -> {
            openScanDialog();
        });

        dialog.show(
                getSupportFragmentManager(),
                "ScannedProductDialog"
        );
    }

    private void openScanDialog() {
        ScanBarcodeDialog dialog = ScanBarcodeDialog.newInstance(false);
        dialog.setOnBarcodeScannedListener(this::onBarcodeScanned);
        dialog.show(getSupportFragmentManager(), "ScanBarDialog");
    }


    private void onBarcodeScanned(String value, ScanType type) {

        if (type == ScanType.BARCODE) {
            findProductByBarcode(value);
        } else if (type == ScanType.SKU) {
            findProductBySku(value);
        }
    }

    private void findProductBySku(String sku) {

        FirebaseFirestore.getInstance()
                .collection("products")
                .whereEqualTo("sku", sku)
                .limit(1)
                .get()
                .addOnSuccessListener(snapshot -> {

                    if (!snapshot.isEmpty()) {
                        String productId = snapshot.getDocuments().get(0).getId();
                        showScannedProductDialog(productId);
                    } else {
                        ToastUtils.showError(
                                this,
                                "Product not found"
                        );
                    }
                });
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cameraExecutor != null) {
            cameraExecutor.shutdown();
        }
    }

}
