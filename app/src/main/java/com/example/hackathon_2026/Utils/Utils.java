package com.example.hackathon_2026.Utils;

import com.example.hackathon_2026.DataLoader;
import com.example.hackathon_2026.MainActivity;
import com.example.hackathon_2026.Product;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Utils {
    private static JSONObject getJsonObjectOfProducts(){
        return null;
    }
    public static void getBarcodeByName(String name){

    }
    public static ArrayList<Product> getListOfProductsByName(String name){
        List<Product> productsList =  DataLoader.loadProducts(MainActivity.context);
        ArrayList<Product> result = new ArrayList<>();
        for (int i = 0; i < productsList.size(); i++){
            if (productsList.get(i) != null){
                Product p = productsList.get(i);
                if (name.equals(p.name.substring(0, name.length())))
                    result.add(p);
            }
        }
        return result;
    }
}
