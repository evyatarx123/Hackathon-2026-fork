package com.example.hackathon_2026;

import android.content.Context;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.List;

public class DataLoader {
    public static List<Product> loadProducts(Context context) {
        try {
            InputStream is = context.getResources().openRawResource(R.raw.essential_products_final);
            InputStreamReader reader = new InputStreamReader(is, "UTF-8");

            Gson gson = new Gson();
            Type listType = new TypeToken<List<Product>>(){}.getType();
            return gson.fromJson(reader, listType);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}