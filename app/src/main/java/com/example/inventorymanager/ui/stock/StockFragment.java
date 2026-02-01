package com.example.inventorymanager.ui.stock;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inventorymanager.R;
import com.example.inventorymanager.data.model.Product;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StockFragment extends Fragment {

    private List<Product> allProducts = new ArrayList<>();
    private List<Product> filteredProducts = new ArrayList<>();
    private StockAdapter adapter;

    private AutoCompleteTextView etCategory;
    private EditText etSearch;
    private View emptyState;

    private String selectedCategory = "All Categories";

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {

        View v = inflater.inflate(R.layout.fragment_stock, container, false);

        // ---------- RecyclerView ----------
        RecyclerView rv = v.findViewById(R.id.rvProducts);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapter = new StockAdapter(filteredProducts);
        rv.setAdapter(adapter);

        emptyState = v.findViewById(R.id.emptyState);

        // ---------- Search ----------
        etSearch = v.findViewById(R.id.etSearch);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterProducts();
            }

            @Override public void afterTextChanged(Editable s) {}
        });

        // ---------- Category ----------
        etCategory = v.findViewById(R.id.etCategory);
        etCategory.setOnItemClickListener((parent, view, position, id) -> {
            selectedCategory = etCategory.getText().toString();
            filterProducts();
        });

        // ---------- Firebase ----------
        FirebaseFirestore.getInstance()
                .collection("products")
                .addSnapshotListener((snap, e) -> {

                    allProducts.clear();

                    if (snap != null) {
                        for (DocumentSnapshot d : snap) {
                            Product p = d.toObject(Product.class);
                            if (p != null) {
                                allProducts.add(p);
                            }
                        }
                    }

                    setupCategoriesFromProducts();
                    filterProducts();
                });

        return v;
    }

    // ================= CATEGORIES =================
    private void setupCategoriesFromProducts() {

        Set<String> categorySet = new HashSet<>();

        for (Product p : allProducts) {
            if (p.getCategory() != null && !p.getCategory().trim().isEmpty()) {
                categorySet.add(p.getCategory());
            }
        }

        List<String> categories = new ArrayList<>();
        categories.add("All Categories");
        categories.addAll(categorySet);
        Collections.sort(categories.subList(1, categories.size()));

        ArrayAdapter<String> adapterCategory = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_list_item_1,
                categories
        );

        etCategory.setAdapter(adapterCategory);

        // păstrează selecția dacă există
        if (!categories.contains(selectedCategory)) {
            selectedCategory = "All Categories";
        }

        etCategory.setText(selectedCategory, false);
    }

    // ================= FILTER =================
    private void filterProducts() {

        filteredProducts.clear();

        String query = etSearch.getText().toString().toLowerCase().trim();

        for (Product p : allProducts) {

            boolean matchName =
                    p.getName() != null &&
                            p.getName().toLowerCase().contains(query);

            boolean matchSku =
                    p.getSku() != null &&
                            p.getSku().toLowerCase().contains(query);

            boolean matchCategory =
                    selectedCategory.equals("All Categories") ||
                            (p.getCategory() != null &&
                                    p.getCategory().equals(selectedCategory));

            if ((query.isEmpty() || matchName || matchSku) && matchCategory) {
                filteredProducts.add(p);
            }
        }

        adapter.notifyDataSetChanged();

        // empty state
        emptyState.setVisibility(
                filteredProducts.isEmpty() ? View.VISIBLE : View.GONE
        );
    }
}
