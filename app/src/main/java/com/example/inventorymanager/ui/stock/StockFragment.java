package com.example.inventorymanager.ui.stock;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inventorymanager.R;
import com.example.inventorymanager.data.model.Product;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;


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

    private ChipGroup chipGroupStock;
    private Chip chipInStock, chipMedium, chipLow;


    private String selectedCategory = "All Categories";

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {

        View v = inflater.inflate(R.layout.fragment_stock, container, false);

        RecyclerView rv = v.findViewById(R.id.rvProducts);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapter = new StockAdapter(filteredProducts, this::confirmDelete);
        rv.setAdapter(adapter);

        emptyState = v.findViewById(R.id.emptyState);

        etSearch = v.findViewById(R.id.etSearch);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterProducts();
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        etCategory = v.findViewById(R.id.etCategory);
        etCategory.setOnItemClickListener((parent, view, position, id) -> {
            selectedCategory = etCategory.getText().toString();
            filterProducts();
        });

        FirebaseFirestore.getInstance()
                .collection("products")
                .addSnapshotListener((snap, e) -> {

                    allProducts.clear();

                    if (snap != null) {
                        for (DocumentSnapshot d : snap) {
                            Product p = d.toObject(Product.class);
                            if (p != null) {
                                p.setId(d.getId()); // IMPORTANT
                                allProducts.add(p);
                            }
                        }
                    }

                    setupCategoriesFromProducts();
                    filterProducts();
                });

        chipGroupStock = v.findViewById(R.id.chipGroupStock);
        chipInStock = v.findViewById(R.id.chipInStock);
        chipMedium = v.findViewById(R.id.chipMedium);
        chipLow = v.findViewById(R.id.chipLow);

        ChipGroup.OnCheckedChangeListener chipListener =
                (group, checkedId) -> filterProducts();

        chipInStock.setOnCheckedChangeListener((b, c) -> filterProducts());
        chipMedium.setOnCheckedChangeListener((b, c) -> filterProducts());
        chipLow.setOnCheckedChangeListener((b, c) -> filterProducts());


        return v;
    }

    private void confirmDelete(Product product) {

        new AlertDialog.Builder(requireContext())
                .setTitle("Delete product")
                .setMessage("Are you sure you want to delete \"" + product.getName() + "\"?")
                .setPositiveButton("Delete", (d, w) -> deleteProduct(product))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteProduct(Product product) {

        FirebaseFirestore.getInstance()
                .collection("products")
                .document(product.getId())
                .delete()
                .addOnSuccessListener(unused ->
                        Toast.makeText(requireContext(),
                                "Product deleted",
                                Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(requireContext(),
                                "Delete failed",
                                Toast.LENGTH_SHORT).show());
    }

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

        if (!categories.contains(selectedCategory)) {
            selectedCategory = "All Categories";
        }

        etCategory.setText(selectedCategory, false);
    }

    private void filterProducts() {

        filteredProducts.clear();

        String query = etSearch.getText().toString().toLowerCase().trim();

        boolean filterInStock = chipInStock.isChecked();
        boolean filterMedium = chipMedium.isChecked();
        boolean filterLow = chipLow.isChecked();

        boolean anyChipChecked = filterInStock || filterMedium || filterLow;

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

            if (!(query.isEmpty() || matchName || matchSku)) continue;
            if (!matchCategory) continue;

            StockStatus status = getStatus(p);

            boolean matchStock =
                    !anyChipChecked ||
                            (filterInStock && status == StockStatus.IN_STOCK) ||
                            (filterMedium && status == StockStatus.MEDIUM_STOCK) ||
                            (filterLow && status == StockStatus.LOW_STOCK) ||
                            (filterLow && status == StockStatus.OUT_OF_STOCK);

            if (matchStock) {
                filteredProducts.add(p);
            }
        }

        // 🔥 dacă NU e selectat niciun chip → sortăm descrescător după stock
        if (!anyChipChecked) {
            Collections.sort(filteredProducts,
                    (a, b) -> Integer.compare(b.getQuantity(), a.getQuantity()));
        }

        adapter.notifyDataSetChanged();

        emptyState.setVisibility(
                filteredProducts.isEmpty() ? View.VISIBLE : View.GONE
        );
    }


    private StockStatus getStatus(Product p) {

        if (p.getQuantity() == 0)
            return StockStatus.OUT_OF_STOCK;

        if (p.getQuantity() < p.getMinStock())
            return StockStatus.LOW_STOCK;

        if (p.getQuantity() < p.getMinStock() + 10)
            return StockStatus.MEDIUM_STOCK;

        return StockStatus.IN_STOCK;
    }

}
