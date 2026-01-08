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

    public static final String EXTRA_BASKET_ID = "basket_id";

    private List<Product> allProducts;
    private List<Product> filteredProducts = new ArrayList<>();
    private List<Product> basketProducts = new ArrayList<>();

    private ProductAdapter searchAdapter;
    private BasketItemAdapter basketAdapter;
    private TextView tvBasketCount;
    private TextView tvTitle;

    private String editingBasketId = null;
    private String editingBasketName = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_basket);

        // Load all products
        allProducts = DataLoader.loadProducts(this);
        if (allProducts == null) allProducts = new ArrayList<>();
        filteredProducts.addAll(allProducts);

        tvTitle = findViewById(R.id.tvTitle);
        EditText etSearch = findViewById(R.id.etSearchProduct);
        RecyclerView rvProducts = findViewById(R.id.rvProducts);
        RecyclerView rvBasketItems = findViewById(R.id.rvBasketItems);
        tvBasketCount = findViewById(R.id.tvBasketCount);
        Button btnSave = findViewById(R.id.btnSaveBasket);

        // Setup Search Adapter
        rvProducts.setLayoutManager(new LinearLayoutManager(this));
        searchAdapter = new ProductAdapter();
        rvProducts.setAdapter(searchAdapter);

        // Setup Basket Adapter
        rvBasketItems.setLayoutManager(new LinearLayoutManager(this));
        basketAdapter = new BasketItemAdapter();
        rvBasketItems.setAdapter(basketAdapter);

        // Check Intent for Edit Mode
        if (getIntent().hasExtra(EXTRA_BASKET_ID)) {
            editingBasketId = getIntent().getStringExtra(EXTRA_BASKET_ID);
            loadBasketForEditing(editingBasketId);
        }

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

        updateBasketCount();
    }

    private void loadBasketForEditing(String basketId) {
        BasketRepository repo = new BasketRepository(this);
        Basket basket = repo.getBasket(basketId);
        if (basket != null) {
            editingBasketName = basket.name;
            if (basket.products != null) {
                basketProducts.addAll(basket.products);
            }
            tvTitle.setText("Edit Basket: " + editingBasketName);
            basketAdapter.notifyDataSetChanged();
            updateBasketCount();
        } else {
            Toast.makeText(this, "Error loading basket", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void updateBasketCount() {
        tvBasketCount.setText("Items in basket: " + basketProducts.size());
    }

    private void filterProducts(String query) {
        filteredProducts.clear();
        if (query.isEmpty()) {
            filteredProducts.addAll(allProducts);
        } else {
            for (Product p : allProducts) {
                if (p.name.toLowerCase().contains(query.toLowerCase())) {
                    filteredProducts.add(p);
                }
            }
        }
        searchAdapter.notifyDataSetChanged();
    }

    private void showSaveDialog() {
        if (basketProducts.isEmpty()) {
            Toast.makeText(this, "Basket is empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(editingBasketId != null ? "Update Basket" : "Name your basket");

        final EditText input = new EditText(this);
        input.setHint("Basket Name");
        if (editingBasketName != null) {
            input.setText(editingBasketName);
        }
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
        String id = (editingBasketId != null) ? editingBasketId : UUID.randomUUID().toString();

        Basket basket = new Basket(id, name, basketProducts);
        BasketRepository repo = new BasketRepository(this);
        repo.saveBasket(basket);

        Toast.makeText(this, "Basket saved!", Toast.LENGTH_SHORT).show();

        // Go back to main activity (clearing stack to refresh nicely, or just finish)
        // Since MainActivity uses onResume to load, finish() is sufficient.
        finish();
    }

    // --- Search Result Adapter ---
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
                basketAdapter.notifyDataSetChanged(); // Refresh basket list
                updateBasketCount();
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

    // --- Basket Item Adapter (New) ---
    private class BasketItemAdapter extends RecyclerView.Adapter<BasketItemAdapter.ViewHolder> {

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // We can reuse the same layout if we change the button text programmatically
            // or create a new layout. For simplicity, reusing item_product_search but changing button.
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_product_search, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Product product = basketProducts.get(position);
            holder.tvName.setText(product.name);

            // Change button to "Remove" or "X"
            holder.btnAdd.setText("Remove");
            holder.btnAdd.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));

            holder.btnAdd.setOnClickListener(v -> {
                basketProducts.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, basketProducts.size());
                updateBasketCount();
            });
        }

        @Override
        public int getItemCount() {
            return basketProducts.size();
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
