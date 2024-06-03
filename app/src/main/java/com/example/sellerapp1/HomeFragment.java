package com.example.sellerapp1;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class HomeFragment extends Fragment {

    TextView totalProducts;
    DatabaseReference databaseReference;
    FirebaseUser user;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        user = FirebaseAuth.getInstance().getCurrentUser();
        totalProducts = view.findViewById(R.id.totalProducts);
        if(user!=null){
            String uid = user.getUid();
            databaseReference = FirebaseDatabase.getInstance().getReference().child("products").child(uid);
            getTotalProducts();
        }else{
            Toast.makeText(getContext(),"No Product Found", Toast.LENGTH_SHORT).show();
        }



        return view;


    }

    private void getTotalProducts() {

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long totalProductCount = snapshot.getChildrenCount();
                totalProducts.setText(String.valueOf(totalProductCount));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load product count", Toast.LENGTH_SHORT).show();
            }
        });
    }
}