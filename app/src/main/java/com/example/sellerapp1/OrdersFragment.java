package com.example.sellerapp1;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class OrdersFragment extends Fragment {

    private static final String TAG = "OrdersFragment";

    OnFragmentInteractionListener mListener;
    ImageView backbtn;
    private RecyclerView recyclerView;
    private OrderAdapter orderAdapter;
    private List<Order> orderList;
    private Retrofit retrofit;
    private WooCommerceApiService apiService;
    private Gson gson;

    public OrdersFragment() {
        // Required empty public constructor
    }

    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    public static OrdersFragment newInstance() {
        return new OrdersFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_orders, container, false);

        recyclerView = view.findViewById(R.id.orderRecyclerView);
        backbtn = view.findViewById(R.id.backbtn);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        orderList = new ArrayList<>();
        orderAdapter = new OrderAdapter(orderList, getContext());
        recyclerView.setAdapter(orderAdapter);

        gson = new GsonBuilder().setPrettyPrinting().create();

        backbtn.setOnClickListener(v -> {
            HomeFragment homeFragment = new HomeFragment();
            replaceFragment(homeFragment);

            if (mListener != null) {
                mListener.onFragmentInteraction(R.id.home);
            }
        });

        fetchOrders(1);

        return view;
    }

    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame, fragment);
        fragmentTransaction.addToBackStack(null); // Optional: if you want to add the transaction to the back stack
        fragmentTransaction.commit();
    }

    private void fetchOrders(int page) {
        final int perPage = 50;

        retrofit = ApiClient.getClient();
        apiService = retrofit.create(WooCommerceApiService.class);

        String vendorName = SessionManager.getInstance().getVendorName();
        List<String> orderIds = OrderManager.getInstance().getOrderIds();
        String ck = SessionManager.getInstance().getConsumerKey();
        String cs = SessionManager.getInstance().getConsumerSecret();

        if (vendorName != null) {
            Log.d(TAG, "Fetching orders for vendor: " + vendorName);

            OrderRequest request = new OrderRequest(orderIds, ck, cs);
            Call<List<Order>> call = apiService.getOrders(request, page, perPage); // Custom plugin used endpoint use = custom/v1/retrieve-orders

            call.enqueue(new Callback<List<Order>>() {
                @Override
                public void onResponse(Call<List<Order>> call, Response<List<Order>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Log.d(TAG, "Orders fetched");
                        Log.d(TAG, "Response Body: " + gson.toJson(response.body()));

                        orderList.clear();
                        orderList.addAll(response.body());

                        if (orderList.size() == perPage) {
                            fetchOrders(page + 1);
                        }

                        orderAdapter.notifyDataSetChanged();
                    } else {
                        try {
                            String errorBody = gson.toJson(response.errorBody());
                            Log.e(TAG, "Failed to load orders: " + response.code() + " - " + response.message() + " - " + errorBody);
                            Toast.makeText(getContext(), "Failed to load orders: " + response.message(), Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Log.e(TAG, "Exception while handling response error", e);
                            Toast.makeText(getContext(), "Failed to load orders", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<List<Order>> call, Throwable t) {
                    Log.e(TAG, "Network Error: " + t.getMessage(), t);
                    Toast.makeText(getContext(), "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Log.e(TAG, "User not logged in: Vendor Name is null");
            Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
        }
    }
}
