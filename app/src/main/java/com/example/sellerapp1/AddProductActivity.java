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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Collections;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class AddProductActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final String TAG = "AddProductActivity";
    private Uri imageUri;

    Button btnSubmit, btnSelectImage;
    ImageView imageView;

    FirebaseStorage storage;
    StorageReference storageReference;
    EditText etName, etDescription, etPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        btnSubmit = findViewById(R.id.btnSubmit);
        btnSelectImage = findViewById(R.id.btnSelectImage);
        imageView = findViewById(R.id.imageView);
        etName = findViewById(R.id.etProductName);
        etDescription = findViewById(R.id.etDescription);
        etPrice = findViewById(R.id.etPrice);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        btnSelectImage.setOnClickListener(v -> openFileChooser());

        btnSubmit.setOnClickListener(v -> {
            if (imageUri != null) {
                Log.d(TAG, "Image URI: " + imageUri.toString());
                uploadImageToFirebase();
            } else {
                Toast.makeText(AddProductActivity.this, "Please select an image", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            imageView.setImageURI(imageUri);
            Log.d(TAG, "Image selected: " + imageUri.toString());
        } else {
            Log.d(TAG, "Image selection canceled or failed");
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
                Toast.makeText(AddProductActivity.this, "Image URL: " + imageUrl, Toast.LENGTH_LONG).show();
                createProductInWooCommerce(imageUrl);
            })).addOnFailureListener(e -> {
                Log.d(TAG, "Error uploading image: " + e.getMessage());
                Toast.makeText(AddProductActivity.this, "Upload Failed", Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void createProductInWooCommerce(String imageUrl){
        SharedPreferences sharedPref = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        int vendorId = sharedPref.getInt("user_id", -1);
        String ck = sharedPref.getString("consumer_key", null);
        String cs = sharedPref.getString("consumer_secret", null);

        if (vendorId == -1) {
            Log.e(TAG, "Vendor ID not found in SharedPreferences");
            Toast.makeText(this, "Vendor ID not found", Toast.LENGTH_SHORT).show();
            return;
        }

        String name = etName.getText().toString();
        String desc = etDescription.getText().toString();
        String price = etPrice.getText().toString();

        Products.Image image = new Products.Image(imageUrl);
        Products product = new Products(name, desc, price, Collections.singletonList(image), vendorId);

        Retrofit retrofit = ApiClient.getClient();
        WooCommerceApiService wooCommerceApiService = retrofit.create(WooCommerceApiService.class);

        Call<Products> call = wooCommerceApiService.addOrUpdateProduct(product);
        call.enqueue(new Callback<Products>() {
            @Override
            public void onResponse(Call<Products> call, Response<Products> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Product created successfully: " + response.body().toString());
                    Toast.makeText(AddProductActivity.this, "Product Created Successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e(TAG, "Failed to create product: " + response.code() + " - " + response.message());
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
                Toast.makeText(AddProductActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
