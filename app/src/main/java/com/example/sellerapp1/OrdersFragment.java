package com.example.sellerapp1;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sellerapp1.Order;
import com.example.sellerapp1.OrderAdapter;
import com.example.sellerapp1.R;

import java.util.ArrayList;
import java.util.List;

public class OrdersFragment extends Fragment {

    private RecyclerView recyclerView;
    private OrderAdapter orderAdapter;
    private List<Order> orderList;

    public OrdersFragment() {
        // Required empty public constructor
    }

    public static OrdersFragment newInstance() {
        return new OrdersFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_orders, container, false);

        recyclerView = view.findViewById(R.id.orderRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Create dummy data
        createDummyData();

        // Set up RecyclerView with dummy data
        orderAdapter = new OrderAdapter(orderList);
        recyclerView.setAdapter(orderAdapter);

        return view;
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
