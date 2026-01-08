package com.example.hackathon_2026;

public class PriceEngine {

    public static class BestDeal {
        public String storeName;
        public double price;

        public BestDeal(String storeName, double price) {
            this.storeName = storeName;
            this.price = price;
        }
    }

    public static BestDeal calculateCheapest(Product product) {
        if (product == null || product.prices == null) return null;

        double cheapest = Double.MAX_VALUE;
        String cheapestStore = "לא נמצא";

        // בדיקה לשופרסל
        if (product.prices.shufersalDeal != null && product.prices.shufersalDeal < cheapest) {
            cheapest = product.prices.shufersalDeal;
            cheapestStore = "שופרסל דיל";
        }

        // בדיקה לרמי לוי
        if (product.prices.ramiLevi != null && product.prices.ramiLevi < cheapest) {
            cheapest = product.prices.ramiLevi;
            cheapestStore = "רמי לוי";
        }

        // בדיקה ליוחננוף
        if (product.prices.yohananof != null && product.prices.yohananof < cheapest) {
            cheapest = product.prices.yohananof;
            cheapestStore = "יוחננוף";
        }

        if (cheapest == Double.MAX_VALUE) return null;

        return new BestDeal(cheapestStore, cheapest);
    }
}