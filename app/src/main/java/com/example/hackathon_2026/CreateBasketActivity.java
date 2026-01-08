package com.example.hackathon_2026;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CreateBasketActivity extends AppCompatActivity {

    private List<Product> allProducts;
    private List<Product> filteredProducts = new ArrayList<>();
    private List<Product> basketProducts = new ArrayList<>();

    private ProductAdapter adapter;
    private TextView tvBasketCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_basket);

        // Load all products (assuming lightweight for now, otherwise should be async or passed)
        allProducts = DataLoader.loadProducts(this);
        if (allProducts == null) allProducts = new ArrayList<>();
        filteredProducts.addAll(allProducts);

        EditText etSearch = findViewById(R.id.etSearchProduct);
        RecyclerView rvProducts = findViewById(R.id.rvProducts);
        tvBasketCount = findViewById(R.id.tvBasketCount);
        Button btnSave = findViewById(R.id.btnSaveBasket);

        rvProducts.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ProductAdapter();
        rvProducts.setAdapter(adapter);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterProducts(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnSave.setOnClickListener(v -> showSaveDialog());
    }

    private void filterProducts(String query) {
        filteredProducts.clear();
        if (query.isEmpty()) {
            filteredProducts.addAll(allProducts);
        } else {
            for (Product p : allProducts) {
                if (p.name.toLowerCase().startsWith(query.toLowerCase())) {
                    filteredProducts.add(p);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void showSaveDialog() {
        if (basketProducts.isEmpty()) {
            Toast.makeText(this, "Basket is empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Name your basket");

        final EditText input = new EditText(this);
        input.setHint("Basket Name");
        builder.setView(input);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String name = input.getText().toString().trim();
            if (name.isEmpty()) name = "Basket " + System.currentTimeMillis();

            saveBasket(name);
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void saveBasket(String name) {
        Basket basket = new Basket(UUID.randomUUID().toString(), name, basketProducts);
        BasketRepository repo = new BasketRepository(this);
        repo.saveBasket(basket);

        Toast.makeText(this, "Basket saved!", Toast.LENGTH_SHORT).show();
        finish();
    }

    private class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_product_search, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Product product = filteredProducts.get(position);
            holder.tvName.setText(product.name);
            holder.btnAdd.setOnClickListener(v -> {
                basketProducts.add(product);
                tvBasketCount.setText("Items in basket: " + basketProducts.size());
                Toast.makeText(CreateBasketActivity.this, "Added " + product.name, Toast.LENGTH_SHORT).show();
            });
        }

        @Override
        public int getItemCount() {
            return filteredProducts.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvName;
            Button btnAdd;

            ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvName = itemView.findViewById(R.id.tvProductName);
                btnAdd = itemView.findViewById(R.id.btnAddProduct);
            }
        }
    }
}
