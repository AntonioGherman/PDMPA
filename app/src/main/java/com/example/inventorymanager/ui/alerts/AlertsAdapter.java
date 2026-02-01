package com.example.inventorymanager.ui.alerts;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inventorymanager.R;
import com.example.inventorymanager.data.model.Product;
import com.example.inventorymanager.ui.alerts.StockStatus;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class AlertsAdapter extends RecyclerView.Adapter<AlertsAdapter.VH> {

    private List<Product> products;
    private StockStatus status;

    public AlertsAdapter(List<Product> products, StockStatus status) {
        this.products = products;
        this.status = status;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_alert_product, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        Product p = products.get(pos);

        h.tvName.setText(p.getName());
        h.tvSku.setText(p.getSku());

        h.tvBadge.setText(
                status == StockStatus.CRITICAL ? "Critical" : "Low"
        );

        h.tvLocations.setText(
                "Available at " + 1 + " other locations"
        );

        if (status == StockStatus.CRITICAL) {
            h.btnRestock.setText("Restock Now");
            h.btnRestock.setBackgroundTintList(
                    ColorStateList.valueOf(Color.BLACK));
            h.btnRestock.setTextColor(Color.WHITE);
            h.tvBadge.getBackground().setTint(Color.rgb(240,45,45));

        } else {
            h.btnRestock.setText("Restock");
            h.btnRestock.setBackgroundTintList(
                    ColorStateList.valueOf(Color.WHITE));
            h.btnRestock.setTextColor(ColorStateList.valueOf(Color.BLACK));
            h.tvBadge.getBackground().setTint(Color.rgb(240,45,45));
            h.btnRestock.setStrokeWidth(1);
        }

        // YOUR STORE
        h.tvYourStoreKey.setText("Your Store:");
        h.tvYourStoreValue.setText(p.getQuantity() + " units");

         // MIN REQUIRED
        h.tvMinKey.setText("Min Required:");
        h.tvMinValue.setText(String.valueOf(p.getMinStock()) + " units");

       // SUPPLIER
        h.tvSupplierKey.setText("Supplier:");
        h.tvSupplierValue.setText("#PLACEHOLDER.SUPPLIER");

    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    static class VH extends RecyclerView.ViewHolder {

        TextView tvName, tvSku, tvBadge, tvLocations;
        MaterialButton btnRestock;

        TextView tvYourStoreKey, tvYourStoreValue;
        TextView tvMinKey, tvMinValue;
        TextView tvSupplierKey, tvSupplierValue;

        VH(View v) {
            super(v);

            tvName = v.findViewById(R.id.tvName);
            tvSku = v.findViewById(R.id.tvSku);
            tvBadge = v.findViewById(R.id.tvBadge);
            tvLocations = v.findViewById(R.id.tvLocations);
            btnRestock = v.findViewById(R.id.btnRestock);

            // YOUR STORE
            View rowYourStore = v.findViewById(R.id.rowYourStore);
            tvYourStoreKey = rowYourStore.findViewById(R.id.tvKey);
            tvYourStoreValue = rowYourStore.findViewById(R.id.tvValue);

            // MIN REQUIRED
            View rowMin = v.findViewById(R.id.rowMinRequired);
            tvMinKey = rowMin.findViewById(R.id.tvKey);
            tvMinValue = rowMin.findViewById(R.id.tvValue);

            // SUPPLIER
            View rowSupplier = v.findViewById(R.id.rowSupplier);
            tvSupplierKey = rowSupplier.findViewById(R.id.tvKey);
            tvSupplierValue = rowSupplier.findViewById(R.id.tvValue);
        }
    }

}
