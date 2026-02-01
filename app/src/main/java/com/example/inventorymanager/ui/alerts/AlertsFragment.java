package com.example.inventorymanager.ui.alerts;

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

public class AlertsFragment extends Fragment {

    private RecyclerView rvCritical, rvWarning;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_alerts, container, false);

        setupSection(
                v.findViewById(R.id.sectionCritical),
                "Critical – Immediate Action",
                R.drawable.ic_alert_r,
                StockStatus.CRITICAL
        );

        setupSection(
                v.findViewById(R.id.sectionWarning),
                "Warning – Stock Running Low",
                R.drawable.ic_warning_o,
                StockStatus.WARNING
        );

        return v;
    }

    private void setupSection(
            View section,
            String title,
            int icon,
            StockStatus status) {

        TextView tvTitle = section.findViewById(R.id.tvTitle);
        ImageView ivIcon = section.findViewById(R.id.ivIcon);
        ImageView ivChevron = section.findViewById(R.id.ivChevron);
        RecyclerView rv = section.findViewById(R.id.rvAlerts);

        tvTitle.setText(title);
        ivIcon.setImageResource(icon);

        rv.setLayoutManager(new LinearLayoutManager(requireContext()));

        rv.setVisibility(View.VISIBLE);

        section.findViewById(R.id.header).setOnClickListener(v -> {
            boolean visible = rv.getVisibility() == View.VISIBLE;
            rv.setVisibility(visible ? View.GONE : View.VISIBLE);
            ivChevron.setRotation(visible ? 0 : 180);
        });

        loadAlerts(rv, status);
    }

    private void loadAlerts(RecyclerView rv, StockStatus status) {

        FirebaseFirestore.getInstance()
                .collection("products")
                .get()
                .addOnSuccessListener(snapshot -> {

                    List<Product> list = new ArrayList<>();

                    for (DocumentSnapshot doc : snapshot) {
                        Product p = doc.toObject(Product.class);

                        if (p == null) continue;

                        if (status == StockStatus.CRITICAL &&
                                p.getQuantity() <= p.getMinStock()) {
                            list.add(p);
                        }

                        if (status == StockStatus.WARNING &&
                                p.getQuantity() > p.getMinStock() &&
                                p.getQuantity() <= 10 + p.getMinStock()) {
                            list.add(p);
                        }
                    }

                    rv.setAdapter(new AlertsAdapter(list, status));
                });
    }
}