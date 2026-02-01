package com.example.inventorymanager.ui.dialogs.add;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.inventorymanager.R;
import com.example.inventorymanager.data.model.Product;
import com.example.inventorymanager.data.repository.ProductRepository;
import com.example.inventorymanager.ui.dialogs.scan.ScanBarcodeDialog;
import com.example.inventorymanager.util.ToastUtils;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;

import java.util.Arrays;
import java.util.List;

public class AddProductDialog extends BottomSheetDialogFragment {

    // ================= UI =================
    private EditText etName;
    private EditText etSku;
    private EditText etBarcode;
    private EditText etPrice;
    private EditText etMinStock;
    private  EditText etQuantity;
    private AutoCompleteTextView  etCategory;
    private AutoCompleteTextView etSupplier;
    private AutoCompleteTextView etStore;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dialog_add_product, container, false);

        // ================= FIND VIEWS =================
        etName = view.findViewById(R.id.etName);
        etSku = view.findViewById(R.id.etSku);
        etBarcode = view.findViewById(R.id.etBarcode);
        etCategory = view.findViewById(R.id.etCategory);
        etPrice = view.findViewById(R.id.etPrice);
        etMinStock = view.findViewById(R.id.etMinStock);

        etSupplier = view.findViewById(R.id.etSupplier);
        etStore = view.findViewById(R.id.etStore);

        etQuantity = view.findViewById(R.id.etQuantity);

        ImageButton btnClose = view.findViewById(R.id.btnClose);
        MaterialButton btnAdd = view.findViewById(R.id.btnAdd);
        ImageButton btnScanBarcode = view.findViewById(R.id.btnScanBarcode);


        Button btnCancel = view.findViewById(R.id.btnCancel);
        btnClose.setOnClickListener(v -> dismiss());
        btnCancel.setOnClickListener(v -> dismiss());

        setupDropdowns();
        setupCategoryDropdown();

        btnAdd.setOnClickListener(v -> saveProduct());

        btnScanBarcode.setOnClickListener(v -> {

            ScanBarcodeDialog dialog = ScanBarcodeDialog.newInstance(true);

            dialog.setOnBarcodeScannedListener(barcode -> {
                etBarcode.setText(barcode);
                etBarcode.setSelection(barcode.length());
            });

            dialog.show(
                    getParentFragmentManager(),
                    "ScanBarDialog"
            );
        });



        return view;
    }

    // ================= DROPDOWNS =================
    private void setupDropdowns() {

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

        ArrayAdapter<String> supplierAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_list_item_1,
                suppliers
        );

        ArrayAdapter<String> storeAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_list_item_1,
                stores
        );

        etSupplier.setAdapter(supplierAdapter);
        etStore.setAdapter(storeAdapter);
    }

    // ================= SAVE PRODUCT =================
    private void saveProduct() {

        String name = etName.getText().toString().trim();
        String sku = etSku.getText().toString().trim();

        if (name.isEmpty()) {
            etName.setError("Required");
            return;
        }

        if (sku.isEmpty()) {
            etSku.setError("Required");
            return;
        }

        double price;
        int minStock;
        int quantity;

        try {
            price = Double.parseDouble(etPrice.getText().toString());
            minStock = Integer.parseInt(etMinStock.getText().toString());
            System.out.println("Quantity: {" +etQuantity.getText().toString()+ "}");
            quantity = Integer.parseInt(etQuantity.getText().toString());
            System.out.println("Int Quantity: " + quantity);
        } catch (Exception e) {
            etPrice.setError("Invalid");
            return;
        }

        Product product = new Product(
                name,
                sku,
                etBarcode.getText().toString(),
                etCategory.getText().toString().trim(),
                price,
                quantity,
                minStock,
                "supplier_temp_id",
                etSupplier.getText().toString()
        );

        System.out.println(product.toString());

        new ProductRepository().addProduct(
                product,
                () -> {
                    ToastUtils.showSuccess(
                            requireContext(),
                            "Product added successfully"
                    );
                    dismiss();
                },
                () -> ToastUtils.showError(
                        requireContext(),
                        "Failed to add product"
                )
        );
    }

    // ================= BOTTOM SHEET FULL =================
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

    private void setupCategoryDropdown() {

        List<String> categories = Arrays.asList(
                "Beverages",
                "Dairy",
                "Snacks",
                "Cleaning",
                "Frozen"
        );

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_list_item_1,
                categories
        );

        etCategory.setAdapter(adapter);
    }

}
