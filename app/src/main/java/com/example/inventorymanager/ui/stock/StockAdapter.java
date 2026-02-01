package com.example.inventorymanager.ui.stock;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inventorymanager.R;
import com.example.inventorymanager.data.model.Product;

import java.util.List;

public class StockAdapter extends RecyclerView.Adapter<StockAdapter.ViewHolder> {

    private List<Product> products;

    public StockAdapter(List<Product> products) {
        this.products = products;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_stock_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int pos) {

        Product p = products.get(pos);

        h.tvName.setText(p.getName());
        h.tvSku.setText(p.getSku());

        h.tvYourStore.setText("Your Store: " + p.getQuantity());
        h.tvTotal.setText("Total: " + p.getQuantity());
        h.tvPrice.setText("Price: $" + p.getPrice());

        h.tvCategorySupplier.setText(
                p.getCategory() + " • " + "#PLACEHOLDER_SUPPLIER"
        );

        applyBadge(h.tvBadge, p.getQuantity(), p.getMinStock());
    }

    private StockStatus getStockStatus(int quantity, int minStock) {

        if (quantity == 0) return StockStatus.OUT_OF_STOCK;

        if (quantity < minStock) return StockStatus.LOW_STOCK;

        if (quantity < minStock + 20) return StockStatus.MEDIUM_STOCK;

        return StockStatus.IN_STOCK;
    }


    private void applyBadge(TextView badge, int qty, int min) {

        StockStatus status = getStockStatus(qty, min);

        int color;
        String text;

        switch (status) {

            case IN_STOCK:
                text = "In Stock";
                color = Color.parseColor("#2E7D32"); // green
                break;

            case MEDIUM_STOCK:
                text = "Medium";
                color = Color.parseColor("#F9A825"); // yellow
                break;

            case LOW_STOCK:
                text = "Low Stock";
                color = Color.parseColor("#D32F2F"); // red
                break;

            case OUT_OF_STOCK:
                text = "Out of Stock";
                color = Color.parseColor("#B71C1C"); // dark red
                break;

            default:
                return;
        }

        badge.setText(text);
        badge.getBackground().setTint(color);
    }


    @Override
    public int getItemCount() {
        return products.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvSku, tvBadge,
                tvYourStore, tvTotal, tvPrice, tvCategorySupplier;

        ViewHolder(View v) {
            super(v);
            tvName = v.findViewById(R.id.tvName);
            tvSku = v.findViewById(R.id.tvSku);
            tvBadge = v.findViewById(R.id.tvBadge);
            tvYourStore = v.findViewById(R.id.tvYourStore);
            tvTotal = v.findViewById(R.id.tvTotal);
            tvPrice = v.findViewById(R.id.tvPrice);
            tvCategorySupplier = v.findViewById(R.id.tvCategorySupplier);
        }
    }
}
