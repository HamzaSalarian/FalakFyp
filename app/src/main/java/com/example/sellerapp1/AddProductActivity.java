package com.example.sellerapp1;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AddProductActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;

    EditText etProductName, etDescription, etPrice;
    Button btnSubmit,btnSelectImage;
    ImageView imageView ;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        etProductName = findViewById(R.id.etProductName);
        etDescription = findViewById(R.id.etDescription);
        etPrice = findViewById(R.id.etPrice);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnSelectImage = findViewById(R.id.btnSelectImage);
        imageView = findViewById(R.id.imageView);
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        btnSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();

            }
        });



        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etProductName.getText().toString();
                String description = etDescription.getText().toString();
                double price = Double.parseDouble(etPrice.getText().toString());
                if (imageUri != null) {
                    uploadImageAndAddProduct(name, description, price);
                } else {
                    Toast.makeText(AddProductActivity.this, "Please select an image", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void uploadImageAndAddProduct(String name, String description, double price) {
        Toast.makeText(AddProductActivity.this, "UploadImageAndAddProduct called", Toast.LENGTH_SHORT).show();
        StorageReference fileReference = storageReference.child("products/" + UUID.randomUUID().toString());

        fileReference.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                    String imageUrl = uri.toString();
                    Toast.makeText(AddProductActivity.this, "Image url called", Toast.LENGTH_SHORT).show();
                    addProduct(name,description,price,imageUrl);
                }));
    }

    private void openFileChooser() {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);

    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            // You can display the selected image using an ImageView or perform other operations with the URI
            imageView.setImageURI(imageUri);
        }
    }

    private void addProduct( String name, String description, double price, String imageUrl) {

        //Database initiation and getting reference
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference reference = firebaseDatabase.getReference();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user!=null){

            String uid = user.getUid();
            //Generating product unique id
            String prodId = reference.child("products").child(uid).push().getKey();

            Products products = new Products(prodId, name,description,price,imageUrl);

            if(prodId!= null){
                reference.child("products").child(uid).child(prodId).setValue(products)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()){
                                Toast.makeText(AddProductActivity.this, "Product added", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                            else {
                                Toast.makeText(AddProductActivity.this, "Failed to add product", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(e -> {
                            Toast.makeText(AddProductActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
                        });
            }

        }else {
            Toast.makeText(AddProductActivity.this,"No User Found", Toast.LENGTH_SHORT).show();
        }









    }
}