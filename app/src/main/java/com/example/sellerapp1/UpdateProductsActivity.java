package com.example.sellerapp1;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.UUID;

public class UpdateProductsActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;

    EditText etDescription,etProductName,etPrice;
    String prodId;
    Button btnUpdate, btnImageUpload;
    ImageView imageView;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;

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
        btnImageUpload = findViewById(R.id.btnSelectImage);
        imageView = findViewById(R.id.imageView);

        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();


        if(prodId!= null){
            fetchProductDetails(prodId);
        }
        else {
            Toast.makeText(UpdateProductsActivity.this, "No Product Specified", Toast.LENGTH_SHORT).show();
        }

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String updatedName = etProductName.getText().toString().trim();
                String updatedDescription = etDescription.getText().toString().trim();
                double updatedPrice;



                try {
                    updatedPrice = Double.parseDouble(etPrice.getText().toString().trim());
                }catch (NumberFormatException e){
                    Toast.makeText(UpdateProductsActivity.this, "Invalid price format", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (imageUri!= null){
                    uploadImage(updatedName,updatedDescription,updatedPrice);
                }
            }
        });

        btnImageUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });


    }

    private void openFileChooser() {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(i,PICK_IMAGE_REQUEST);
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            imageView.setImageURI(imageUri);

        }
    }

    private void uploadImage(String updatedName, String updatedDescription, double updatedPrice) {
        if (imageUri!=null){
            StorageReference fileRef = storageReference.child("products/" + UUID.randomUUID().toString());
            fileRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String imageUrl = uri.toString();
                        updateProducts(updatedName,updatedDescription,updatedPrice,imageUrl);
                    }))
                    .addOnFailureListener(e -> {
                        Toast.makeText(UpdateProductsActivity.this, "Image upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }



    private void updateProducts(String updatedName, String updatedDescription, double updatedPrice, String updatedImageUrl) {


        Products updatedProduct = new Products(prodId, updatedName,updatedDescription,updatedPrice,updatedImageUrl);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null){
            String uid = user.getUid();

            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference databaseReference = firebaseDatabase.getReference().child("products").child(uid).child(prodId);


            databaseReference.setValue(updatedProduct)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(UpdateProductsActivity.this, "Product updated successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(UpdateProductsActivity.this, "Failed to update product", Toast.LENGTH_SHORT).show();
                        }
                    });

        }else {
            Toast.makeText(this, "No Product Found", Toast.LENGTH_SHORT).show();
        }




    }

    private void fetchProductDetails(String prodId) {


        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null) {
            String uid = user.getUid();
            DatabaseReference databaseReference = firebaseDatabase.getReference().child("products").child(uid).child(prodId);

            databaseReference.addValueEventListener(new ValueEventListener() {
                @SuppressLint("DefaultLocale")
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Products product = snapshot.getValue(Products.class);
                    if (product!= null){
                        etProductName.setText(product.getName());
                        etDescription.setText(product.getDescription());
                        etPrice.setText(String.format("%.2f", product.getPrice()));
                        if(product.getImageUrl()!= null){
                            Picasso.get().load(product.getImageUrl()).into(imageView);
                        }else {
                            Toast.makeText(UpdateProductsActivity.this, "No Product found", Toast.LENGTH_SHORT).show();
                        }
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
        }else {
            Toast.makeText(this, "No Product Found", Toast.LENGTH_SHORT).show();
        }


    }
}