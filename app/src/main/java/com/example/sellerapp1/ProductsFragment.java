package com.example.sellerapp1;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ProductsFragment extends Fragment  {

    OnFragmentInteractionListener mListener;
    ImageView backbtn;
    RecyclerView productRecyclerView;
    ProductAdapter productAdapter;
    List<Products> productList;
    Button btnAddProduct;

    EditText productSearchView;

    FirebaseUser user;



    public ProductsFragment() {
        // Required empty public constructor
    }

    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_products, container, false);

        btnAddProduct = view.findViewById(R.id.btnAddProduct);
        productSearchView = view.findViewById(R.id.productSearchView); // Ensure this ID exists in XML

        btnAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddProductActivity();
            }
        });

        productRecyclerView = view.findViewById(R.id.productRecyclerView);
        productList = new ArrayList<>();
        productAdapter = new ProductAdapter(productList,  getContext());
        productRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        productRecyclerView.setAdapter(productAdapter);
        user = FirebaseAuth.getInstance().getCurrentUser();
        backbtn = view.findViewById(R.id.backbtn);


        productSearchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Display all products before text changes
                productAdapter.filter("");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                productAdapter.filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Do nothing after text is changed
            }
        });
        if(user!=null){
            String uid = user.getUid();
            loadProducts(uid);

        }else {
            Toast.makeText(getContext(), "No User Found", Toast.LENGTH_SHORT).show();
        }

         // Load products when fragment is created


        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeFragment homeFragment = new HomeFragment();
                replaceFragment(homeFragment);

                if (mListener != null) {
                    mListener.onFragmentInteraction(R.id.home);
                }
            }
        });

        return view;

    }

    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame, fragment);
        fragmentTransaction.addToBackStack(null); // Optional: if you want to add the transaction to the back stack
        fragmentTransaction.commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh data if necessary
        if(user!=null){
            String uid = user.getUid();
            loadProducts(uid);

        }else {
            Toast.makeText(getContext(), "No User Found", Toast.LENGTH_SHORT).show();
        }
    }

    private void openAddProductActivity() {
        Intent intent = new Intent(getActivity(), AddProductActivity.class);
        startActivity(intent);
    }

    private void loadProducts(String uid) {
        // Get a reference to the products node in the Firebase Realtime Database
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("products").child(uid);

        // Add a listener to retrieve data from the database
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                productList.clear();
                for (DataSnapshot productSnapshot : dataSnapshot.getChildren()) {
                    Products product = productSnapshot.getValue(Products.class);
                    if (product != null) {
                        productList.add(product);

                    } else {
                        Toast.makeText(getContext(),"No products", Toast.LENGTH_SHORT).show();
                    }
                }
                productAdapter.updateFullList(productList);
                productAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to load products", Toast.LENGTH_SHORT).show();
            }
        });





    }




}
