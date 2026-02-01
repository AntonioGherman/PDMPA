package com.example.inventorymanager.ui.dialogs.scan;

import com.example.inventorymanager.util.ScanType;

public interface OnBarcodeScannedListener {
    void onBarcodeScanned(String barcode, ScanType type);
}
