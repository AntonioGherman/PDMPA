package com.example.inventorymanager.ui.dialogs.add;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.inventorymanager.R;
import com.example.inventorymanager.ui.dialogs.scan.ScanBarcodeDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.Arrays;
import java.util.List;

public class AddProductDialog extends BottomSheetDialogFragment {

    private EditText etBarcode;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dialog_add_product, container, false);

        //etBarcode = view.findViewById(R.id.etBarcode);
        //TextView btnScanBarcode = view.findViewById(R.id.btnScanBarcode);
        ImageButton btnClose = view.findViewById(R.id.btnClose);

        btnClose.setOnClickListener(v -> dismiss());

        AutoCompleteTextView etSupplier = view.findViewById(R.id.etSupplier);
        AutoCompleteTextView etStores = view.findViewById(R.id.etStore);

// 🔹 hardcoded values (temporar)
        List<String> suppliers = Arrays.asList(
                "Supplier A",
                "Supplier B",
                "Supplier C"
        );

        List<String> stores = Arrays.asList(
                "Store 1",
                "Store 2",
                "Store 3"
        );

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_list_item_1,
                suppliers
        );

        etSupplier.setAdapter(adapter);

       adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_list_item_1,
                stores
        );
        etStores.setAdapter(adapter);



        //btnScanBarcode.setOnClickListener(v -> openScanDialog());

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        View view = getView();
        if (view == null) return;

        View parent = (View) view.getParent();
        if (parent == null) return;

        com.google.android.material.bottomsheet.BottomSheetBehavior<View> behavior =
                com.google.android.material.bottomsheet.BottomSheetBehavior.from(parent);

        behavior.setState(
                com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
        );

        behavior.setSkipCollapsed(true);
    }


    private void openScanDialog() {
//        ScanBarcodeDialog dialog = new ScanBarcodeDialog();
////        dialog.setOnBarcodeScannedListener(barcode -> {
////            etBarcode.setText(barcode);
////        });
//        dialog.show(getParentFragmentManager(), "ScanBarcodeDialog");
    }
}
