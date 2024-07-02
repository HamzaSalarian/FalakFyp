package com.example.sellerapp1;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class UpdateProductsActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private static final String TAG = "UpdateProductsActivity";
    private Retrofit retrofit;
    private WooCommerceApiService wooCommerceApiService;
    private int id;
    private int imageId;
    private String imageName, imageAlt;

    private EditText etDescription, etProductName, etPrice;
    private String prodId;
    private Button btnUpdate, btnImageUpload;
    private ImageView imageView;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_products);

        gson = new GsonBuilder().create();

        etProductName = findViewById(R.id.etProductName);
        etPrice = findViewById(R.id.etPrice);
        etDescription = findViewById(R.id.etDescription);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnImageUpload = findViewById(R.id.btnSelectImage);
        imageView = findViewById(R.id.imageView);
        Intent i = getIntent();
        prodId = i.getStringExtra("prodId");
        id = Integer.parseInt(prodId);

        Toast.makeText(UpdateProductsActivity.this, prodId, Toast.LENGTH_SHORT).show();

        SharedPreferences sharedPref = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        String consumerKey = sharedPref.getString("consumer_key", null);
        String consumerSecret = sharedPref.getString("consumer_secret", null);

        if (consumerKey == null || consumerSecret == null) {
            Log.e(TAG, "Consumer Key or Consumer Secret not found in SharedPreferences");
            Toast.makeText(this, "Consumer Key or Consumer Secret not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        retrofit = ApiClient.getClient();
        wooCommerceApiService = retrofit.create(WooCommerceApiService.class);

        fetchProductFromWooCommerce();

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        btnUpdate.setOnClickListener(v -> {
            if (imageUri != null) {
                uploadImageToFirebase();
            } else {
                updateProductInWooCommerce(null);
            }
        });

        btnImageUpload.setOnClickListener(v -> openFileChooser());
    }

    private void openFileChooser() {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(i, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            imageView.setImageURI(imageUri);
        }
    }

    private void uploadImageToFirebase() {
        if (imageUri != null) {
            String fileName = UUID.randomUUID().toString() + ".jpg";
            StorageReference fileRef = storageReference.child("images/" + fileName);
            UploadTask uploadTask = fileRef.putFile(imageUri);

            uploadTask.addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String imageUrl = uri.toString();
                Log.d(TAG, "Image uploaded successfully: " + imageUrl);
                Toast.makeText(UpdateProductsActivity.this, "Image URL: " + imageUrl, Toast.LENGTH_LONG).show();
                updateProductInWooCommerce(imageUrl);
            })).addOnFailureListener(e -> {
                Log.d(TAG, "Error uploading image: " + e.getMessage());
                Toast.makeText(UpdateProductsActivity.this, "Upload Failed", Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void updateProductInWooCommerce(String imageUrl) {
        String updatedName = etProductName.getText().toString().trim();
        String updatedDescription = etDescription.getText().toString().trim();
        String updatedPrice = etPrice.getText().toString().trim();

        Products product;
        if (imageUrl != null) {
            Products.Image image = new Products.Image(imageUrl);
            product = new Products(id, updatedName, updatedDescription, updatedPrice, Collections.singletonList(image));
        } else {
            product = new Products(id, updatedName, updatedDescription, updatedPrice, null);
        }

        String productJson = gson.toJson(product);
        Log.d(TAG, "Updating product with ID: " + id);
        Log.d(TAG, "Product JSON: " + productJson);

        Call<Products> call = wooCommerceApiService.updateProduct(id, product);
        call.enqueue(new Callback<Products>() {
            @Override
            public void onResponse(Call<Products> call, Response<Products> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Product updated successfully: " + response.body().toString());
                    Toast.makeText(UpdateProductsActivity.this, "Product Updated Successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e(TAG, "Failed to update product: " + response.code() + " - " + response.message());
                    try {
                        Log.e(TAG, "Error body: " + response.errorBody().string());
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing error body", e);
                    }
                }
            }

            @Override
            public void onFailure(Call<Products> call, Throwable t) {
                Log.e(TAG, "Network Error: " + t.getMessage(), t);
                Toast.makeText(UpdateProductsActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchProductFromWooCommerce() {
        Call<Products> call = wooCommerceApiService.getProduct(id);
        call.enqueue(new Callback<Products>() {
            @Override
            public void onResponse(Call<Products> call, Response<Products> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Products product = response.body();
                    Log.d(TAG, "Product fetched successfully: " + product.getName());
                    etProductName.setText(product.getName());
                    etDescription.setText(product.getDescription());
                    etPrice.setText(product.getRegular_price());

                    if (product.getImages() != null && !product.getImages().isEmpty()) {
                        String imageUrl = product.getImages().get(0).getSrc();
                        imageId = product.getImages().get(0).getId();
                        imageName = product.getImages().get(0).getName();
                        imageAlt = product.getImages().get(0).getAlt();
                        Log.d(TAG, "onResponse: "+ imageAlt);
                        Picasso.get().load(imageUrl).into(imageView);
                    } else {
                        Log.d(TAG, "No images found for this product.");
                        Toast.makeText(UpdateProductsActivity.this, "No images available for this product", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e(TAG, "Failed to fetch product: " + response.code() + " - " + response.message());
                    try {
                        Log.e(TAG, "Error body: " + response.errorBody().string());
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing error body", e);
                    }
                }
            }

            @Override
            public void onFailure(Call<Products> call, Throwable t) {
                Log.e(TAG, "Network Error: " + t.getMessage(), t);
                Toast.makeText(UpdateProductsActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
