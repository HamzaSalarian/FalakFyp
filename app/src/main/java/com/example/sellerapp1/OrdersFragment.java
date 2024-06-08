package com.example.sellerapp1;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sellerapp1.Order;
import com.example.sellerapp1.OrderAdapter;
import com.example.sellerapp1.R;

import java.util.ArrayList;
import java.util.List;

public class OrdersFragment extends Fragment {

    OnFragmentInteractionListener mListener;
    ImageView backbtn;
    private RecyclerView recyclerView;
    private OrderAdapter orderAdapter;
    private List<Order> orderList;

    public OrdersFragment() {
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

    public static OrdersFragment newInstance() {
        return new OrdersFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_orders, container, false);

        recyclerView = view.findViewById(R.id.orderRecyclerView);
        backbtn = view.findViewById(R.id.backbtn);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Create dummy data
        createDummyData();

        // Set up RecyclerView with dummy data
        orderAdapter = new OrderAdapter(orderList);
        recyclerView.setAdapter(orderAdapter);


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



    private void createDummyData() {
        orderList = new ArrayList<>();
        orderList.add(new Order("1", "Shiza Raza", "5000RS"));
        orderList.add(new Order("2", "Ali Ahmed", "1300RS"));
        orderList.add(new Order("3", "Ayesha Imran", "600RS"));
        orderList.add(new Order("4", "Amna Salman", "900RS"));
        // Add more dummy data as needed
    }
}
