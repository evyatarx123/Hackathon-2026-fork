package com.example.hackathon_2026;

import java.util.List;
import java.util.Map;

public class PriceEngine {

    // מחלקת עזר פנימית לתוצאה
    public static class ComparisonResult {
        public String cheapestStore;
        public double cheapestPrice;
        public Map<String, Double> allPrices;

        public ComparisonResult(String cheapestStore, double cheapestPrice, Map<String, Double> allPrices) {
            this.cheapestStore = cheapestStore;
            this.cheapestPrice = cheapestPrice;
            this.allPrices = allPrices;
        }
    }

    // הפונקציה המרכזית שתחפש את המחיר
    public static ComparisonResult findCheapestStore(String targetBarcode, List<Product> productsList) {
        Product foundProduct = null;

        for (Product p : productsList) {
            if (p.barcode.equals(targetBarcode)) {
                foundProduct = p;
                break;
            }
        }

        if (foundProduct == null) return null;

        double minPrice = Double.MAX_VALUE;
        String bestStore = "";

        for (Map.Entry<String, Double> entry : foundProduct.prices.entrySet()) {
            String storeName = entry.getKey();
            Double price = entry.getValue();

            if (price != null && price < minPrice) {
                minPrice = price;
                bestStore = storeName;
            }
        }

        if (bestStore.isEmpty()) return null;

        return new ComparisonResult(bestStore, minPrice, foundProduct.prices);
    }
}