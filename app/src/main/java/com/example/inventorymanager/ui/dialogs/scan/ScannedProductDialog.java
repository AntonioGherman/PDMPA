package com.example.inventorymanager.ui.dialogs.scan;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.inventorymanager.R;
import com.example.inventorymanager.data.model.Product;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.firestore.FirebaseFirestore;

public class ScannedProductDialog extends BottomSheetDialogFragment {

    private static final String ARG_PRODUCT_ID = "arg_product_id";
    private String productId;

    private OnScanAgainListener scanAgainListener;

    public void setOnScanAgainListener(OnScanAgainListener listener) {
        this.scanAgainListener = listener;
    }


    public static ScannedProductDialog newInstance(String productId) {
        ScannedProductDialog dialog = new ScannedProductDialog();
        Bundle b = new Bundle();
        b.putString(ARG_PRODUCT_ID, productId);
        dialog.setArguments(b);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            productId = getArguments().getString(ARG_PRODUCT_ID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_scanned_product, container, false);
       loadProduct(v);

        v.findViewById(R.id.btnScanAgain).setOnClickListener(btn -> {

            if (scanAgainListener != null) {
                scanAgainListener.onScanAgain();
            }

            dismissAllowingStateLoss();
        });

        loadProduct(v);
        return v;
    }


    private void loadProduct(View v) {

        FirebaseFirestore.getInstance()
                .collection("products")
                .document(productId)
                .get()
                .addOnSuccessListener(doc -> {
                    Product product = doc.toObject(Product.class);
                    if (product != null) {
                        bindProduct(v, product);
                    }
                });
    }

    private void bindProduct(View v, Product product) {

        ((TextView) v.findViewById(R.id.tvProductName))
                .setText(product.getName());

        ((TextView) v.findViewById(R.id.tvCategory))
                .setText(product.getCategory());

        ((TextView) v.findViewById(R.id.tvSku))
                .setText(product.getSku());

        ((TextView) v.findViewById(R.id.tvBarcode))
                .setText(product.getBarcode());

        ((TextView) v.findViewById(R.id.tvSupplier))
                .setText("#PLACEHOLDER.SUPPLIER");

        ((TextView) v.findViewById(R.id.tvQuantity))
                .setText(product.getQuantity() + " units");

        ((TextView) v.findViewById(R.id.tvPrice))
                .setText("$" + product.getPrice());
    }
}
