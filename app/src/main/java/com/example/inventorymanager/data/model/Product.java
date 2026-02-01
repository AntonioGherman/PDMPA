package com.example.inventorymanager.data.model;

import com.example.inventorymanager.ui.alerts.StockStatus;

import java.io.Serializable;

public class Product implements Serializable {

    private String id;          // Firestore document ID
    private String name;
    private String sku;
    private String barcode;
    private String category;
    private double price;
    private int quantity;
    private int minStock;
    private String supplierId;
    private String supplierName;
    private long createdAt;

    private String stockStatus;

    // 🔹 OBLIGATORIU pentru Firestore
    public Product() {
    }

    public Product(
            String name,
            String sku,
            String barcode,
            String category,
            double price,
            int quantity,
            int minStock,
            String supplierId,
            String supplierName
    ) {
        this.name = name;
        this.sku = sku;
        this.barcode = barcode;
        this.category = category;
        this.price = price;
        this.quantity = quantity;
        this.minStock = minStock;
        this.supplierId = supplierId;
        this.supplierName = supplierName;
        this.createdAt = System.currentTimeMillis();

        this.stockStatus = calculateStatus(this).toString();
    }

    // ===== GETTERS & SETTERS =====

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getSku() {
        return sku;
    }

    public String getBarcode() {
        return barcode;
    }

    public String getCategory() {
        return category;
    }

    public double getPrice() {
        return price;
    }

    public int getMinStock() {
        return minStock;
    }

    public String getSupplierId() {
        return supplierId;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getStockStatus() {return stockStatus;}

    @Override
    public String toString() {
        return "Product{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", sku='" + sku + '\'' +
                ", barcode='" + barcode + '\'' +
                ", category='" + category + '\'' +
                ", price=" + price +
                ", quantity=" + quantity +
                ", minStock=" + minStock +
                ", supplierId='" + supplierId + '\'' +
                ", supplierName='" + supplierName + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }

    private StockStatus calculateStatus(Product p) {

        if (p.getQuantity() <= p.getMinStock()) {
            return StockStatus.CRITICAL;
        }

        if (p.getQuantity() <= 10 + p.getMinStock()) {
            return StockStatus.WARNING;
        }

        return StockStatus.OK;
    }

}
