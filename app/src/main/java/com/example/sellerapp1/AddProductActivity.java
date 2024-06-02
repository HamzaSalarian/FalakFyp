package com.example.sellerapp1;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AddProductActivity extends AppCompatActivity {

    private EditText etProductName, etDescription, etPrice;
    private Button btnSubmit;
    private int sellerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        etProductName = findViewById(R.id.etProductName);
        etDescription = findViewById(R.id.etDescription);
        etPrice = findViewById(R.id.etPrice);
        btnSubmit = findViewById(R.id.btnSubmit);

        // Assuming sellerId is passed as an intent extra
        sellerId = getIntent().getIntExtra("seller_id", -1);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etProductName.getText().toString();
                String description = etDescription.getText().toString();
                double price = Double.parseDouble(etPrice.getText().toString());
                addProduct( name, description, price);
            }
        });
    }

    private void addProduct( String name, String description, double price) {

        //Database initiation and getting reference
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference reference = firebaseDatabase.getReference();

        //Static User Id Just for testing purpose will be updated when we implement auth
        String uid = "1234567"; // In future we will get the current user here not the id the id will be set inside the if

        if(uid!=null){

            //Static User Id Just for testing purpose will be updated when we implement auth
            //String uid = "123456";
            //Generating product unique id
            String prodId = reference.child("products").child(uid).push().getKey();

            Products products = new Products(prodId, name,description,price);

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

        }









    }
}