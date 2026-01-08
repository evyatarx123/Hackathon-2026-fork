package com.example.hackathon_2026;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class PriceEngineTest {

    @Test
    public void testBasketCalculation() {
        // Mock Products
        Product p1 = new Product();
        p1.name = "Milk";
        p1.prices = new Product.Prices();
        p1.prices.shufersalDeal = 5.0;
        p1.prices.ramiLevi = 4.5;
        p1.prices.yohananof = 6.0;

        Product p2 = new Product();
        p2.name = "Bread";
        p2.prices = new Product.Prices();
        p2.prices.shufersalDeal = 7.0;
        p2.prices.ramiLevi = 8.0;
        p2.prices.yohananof = 6.5;

        List<Product> basket = new ArrayList<>();
        basket.add(p1);
        basket.add(p2);

        // Expected Totals:
        // Shufersal: 12.0
        // Rami Levi: 12.5
        // Yohananof: 12.5

        PriceEngine.BasketResult result = PriceEngine.calculateBasketBestDeal(basket);

        // Shufersal should be cheapest (12.0)
        assertEquals("שופרסל דיל", result.storeName);
        assertEquals(12.0, result.totalPrice, 0.01);
    }

    @Test
    public void testMissingItems() {
        Product p1 = new Product();
        p1.name = "Rare Item";
        p1.prices = new Product.Prices();
        p1.prices.shufersalDeal = 10.0;
        p1.prices.ramiLevi = null; // Missing
        p1.prices.yohananof = 20.0;

        List<Product> basket = new ArrayList<>();
        basket.add(p1);

        PriceEngine.BasketResult result = PriceEngine.calculateBasketBestDeal(basket);

        // Logic check: Rami Levi is missing the item.
        // Shufersal (10.0) has the item.
        // Yohananof (20.0) has the item.

        // The engine prioritizes stores with FEWER missing items.
        // Rami Levi (1 missing) vs Shufersal (0 missing).
        // Shufersal should win despite Rami Levi having a total of 0 (or undefined).

        assertEquals("שופרסל דיל", result.storeName);
        assertEquals(10.0, result.totalPrice, 0.01);
    }
}
