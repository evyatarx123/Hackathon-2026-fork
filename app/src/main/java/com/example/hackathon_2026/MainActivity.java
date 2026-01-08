package com.example.hackathon_2026;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<Product> allProducts;
    private static final String TAG = "HACKATHON_LOG";

    public static Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 1. טעינת הנתונים מה-JSON לזיכרון
        allProducts = DataLoader.loadProducts(this);

        // 2. בדיקה שהנתונים נטענו ושהמנוע עובד (מופיע ב-Logcat)
        if (allProducts != null && !allProducts.isEmpty()) {
            Log.d(TAG, "SUCCESS: Loaded " + allProducts.size() + " products.");

            // הרצת בדיקה אוטומטית על המנוע כדי לראות שהוא מחשב נכון
            runPriceEngineTest();
        } else {
            Log.e(TAG, "ERROR: Could not load data.");
        }
    }

    /**
     * פונקציה שבודקת שה-PriceEngine באמת מצליח לחשב מחירים מהרשימה שנטענה
     */
    private void runPriceEngineTest() {
        // ניקח מוצר לדוגמה מהרשימה (למשל המוצר העשירי)
        Product testProduct = allProducts.get(10);

        // נריץ עליו את המנוע
        PriceEngine.BestDeal deal = PriceEngine.calculateCheapest(testProduct);

        if (deal != null) {
            Log.d(TAG, "--- PriceEngine Test Result ---");
            Log.d(TAG, "Product Name: " + testProduct.name);
            Log.d(TAG, "Cheapest Store: " + deal.storeName);
            Log.d(TAG, "Cheapest Price: " + deal.price + " ₪");
            Log.d(TAG, "-------------------------------");
        } else {
            Log.e(TAG, "PriceEngine returned null for product: " + testProduct.name);
        }
    }
}