package com.example.sellerapp1;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UpdateProductsActivity extends AppCompatActivity {

    EditText etDescription,etProductName,etPrice;
    String prodId;
    Button btnUpdate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_update_products);

        prodId = getIntent().getStringExtra("prodId");
        Toast.makeText(UpdateProductsActivity.this, prodId, Toast.LENGTH_SHORT).show();
        etProductName = findViewById(R.id.etProductName);
        etPrice = findViewById(R.id.etPrice);
        etDescription = findViewById(R.id.etDescription);
        btnUpdate = findViewById(R.id.btnUpdate);


        if(prodId!= null){
            fetchProductDetails(prodId);
        }
        else {
            Toast.makeText(UpdateProductsActivity.this, "No Product Specified", Toast.LENGTH_SHORT).show();
        }

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProducts();
            }
        });


    }

    private void updateProducts() {
        String updatedName = etProductName.getText().toString().trim();
        String updatedDescription = etDescription.getText().toString().trim();
        double updatedPrice;

        try {
            updatedPrice = Double.parseDouble(etPrice.getText().toString().trim());
        }catch (NumberFormatException e){
            Toast.makeText(UpdateProductsActivity.this, "Invalid price format", Toast.LENGTH_SHORT).show();
            return;
        }

        Products updatedProduct = new Products(prodId, updatedName,updatedDescription,updatedPrice);

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference().child("products").child("1234567").child(prodId);

        databaseReference.setValue(updatedProduct)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(UpdateProductsActivity.this, "Product updated successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(UpdateProductsActivity.this, "Failed to update product", Toast.LENGTH_SHORT).show();
                    }
                });


    }

    private void fetchProductDetails(String prodId) {


        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference().child("products").child("1234567").child(prodId);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Products product = snapshot.getValue(Products.class);
                if (product!= null){
                    etProductName.setText(product.getName());
                    etDescription.setText(product.getDescription());
                    etPrice.setText(String.format("%.2f", product.getPrice()));
                }
                else{
                    Toast.makeText(UpdateProductsActivity.this, "No Product found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(UpdateProductsActivity.this, "Failed to load product details", Toast.LENGTH_SHORT).show();
            }
        });

    }
}