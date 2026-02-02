package com.example.inventorymanager.ui.home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.inventorymanager.R;
import com.example.inventorymanager.data.model.Product;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private final List<Product> lowStock = new ArrayList<>();
    private LowStockAdapter adapter;

    private View cardProducts;
    private View cardLowStock;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_home, container, false);

        RecyclerView rv = v.findViewById(R.id.rvLowStock);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new LowStockAdapter(lowStock);
        rv.setAdapter(adapter);

        cardProducts = v.findViewById(R.id.cardProducts);
        cardLowStock = v.findViewById(R.id.cardLowStock);


        loadData();
        setupMetrics(v);

        return v;
    }

    private void loadData() {

        FirebaseFirestore.getInstance()
                .collection("products")
                .addSnapshotListener((snap, e) -> {

                    if (snap == null) return;

                    lowStock.clear();

                    int totalProducts = 0;
                    int lowStockCount = 0;

                    for (DocumentSnapshot d : snap) {

                        Product p = d.toObject(Product.class);
                        if (p == null) continue;

                        totalProducts++;

                        boolean isLow =
                                p.getQuantity() < p.getMinStock()
                                        || p.getQuantity() <= 0;

                        if (isLow) {
                            lowStock.add(p);
                            lowStockCount++;
                        }
                    }

                    adapter.notifyDataSetChanged();

                    // 🔥 UPDATE METRICS
                    updateMetrics(totalProducts, lowStockCount);
                });
    }


    private void updateMetrics(int totalProducts, int lowStockCount) {

        setMetric(
                cardProducts,
                R.drawable.ic_box,
                "Products",
                String.valueOf(totalProducts)
        );

        setMetric(
                cardLowStock,
                R.drawable.ic_alert,
                "Low Stock",
                String.valueOf(lowStockCount)
        );
    }


    private void setupMetrics(View v) {

        setMetric(
                v.findViewById(R.id.cardSales),
                R.drawable.ic_sales,
                "Sales (7d)",
                "40"
        );

        setMetric(
                v.findViewById(R.id.cardRevenue),
                R.drawable.ic_price,
                "Revenue",
                "$1067"
        );
    }

    private void setMetric(View card, int icon, String title, String value) {

        ImageView iv = card.findViewById(R.id.icon);
        TextView tvTitle = card.findViewById(R.id.tvTitle);
        TextView tvValue = card.findViewById(R.id.tvValue);

        iv.setImageResource(icon);
        tvTitle.setText(title);
        tvValue.setText(value);
    }

}
