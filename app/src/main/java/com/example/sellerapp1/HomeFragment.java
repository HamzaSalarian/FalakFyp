package com.example.sellerapp1;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";
    private TextView totalProducts;
    private TextView totalOrders;

    private Retrofit retrofit;
    private WooCommerceApiService apiService;
    int userId = SessionManager.getInstance().getUserId();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        totalProducts = view.findViewById(R.id.totalProducts);
        totalOrders = view.findViewById(R.id.orderCount);


        retrofit = ApiClient.getClient();
        apiService = retrofit.create(WooCommerceApiService.class);

        getTotalProducts(1);
        getTotalOrders();

        return view;
    }

    private void getTotalOrders() {

        String vendorName = SessionManager.getInstance().getVendorName();

        if (vendorName!=null){
            OrderRequest orderRequest = new OrderRequest(vendorName);

            Call<OrderResponse> call = apiService.getOrdersId(orderRequest); //custom plugin endpoint used = custom/v1/vendor-orders
            call.enqueue(new Callback<OrderResponse>() {
                @Override
                public void onResponse(Call<OrderResponse> call, Response<OrderResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        OrderResponse orderResponse = response.body();
                        totalOrders.setText(String.valueOf(orderResponse.getOrderCount()));
                        OrderManager.getInstance().setOrderIds(orderResponse.getOrderIds());
                        Log.d(TAG, "Total Orders: " + orderResponse.getOrderCount());
                    } else {
                        Log.e(TAG, "Failed to load order count: " + response.code() + " - " + response.message());
                        try {
                            Log.e(TAG, "Error body: " + response.errorBody().string());
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing error body", e);
                        }
                        Toast.makeText(getContext(), "Failed to load order count", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<OrderResponse> call, Throwable t) {
                    Log.e(TAG, "Network Error: " + t.getMessage(), t);
                    Toast.makeText(getContext(), "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(getContext(), "Vendor name not found", Toast.LENGTH_SHORT).show();
        }
    }

    private void getTotalProducts(int page) {
        final int perPage = 50;

        if (userId != -1) {
            ProductRequest request = new ProductRequest(userId);
            Call<List<Products>> call = apiService.getProducts(request, page, perPage); //custom plugin used = custom/v1/product

            call.enqueue(new Callback<List<Products>>() {
                @Override
                public void onResponse(Call<List<Products>> call, Response<List<Products>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        long totalProductCount = 0;
                        totalProductCount += response.body().size();
                        if (response.body().size() == perPage) {
                            getTotalProducts(page + 1);
                        } else {
                            totalProducts.setText(String.valueOf(totalProductCount));
                            Log.d(TAG, "Total Products: " + totalProductCount);
                        }
                    } else {
                        Log.e(TAG, "Failed to load product count: " + response.code() + " - " + response.message());
                        try {
                            Log.e(TAG, "Error body: " + response.errorBody().string());
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing error body", e);
                        }
                        Toast.makeText(getContext(), "Failed to load product count", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<List<Products>> call, Throwable t) {
                    Log.e(TAG, "Network Error: " + t.getMessage(), t);
                    Toast.makeText(getContext(), "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
        }
    }
}


/*  My Advice

    Go to WooCommerceApiService.java class to view all the routes for the endpoint Login Activity, Products addition and updation,
    Getting the orders by vendor and their management  are all done using the Custom plugins
    Run the app once and then read the code according to the flow the app ran with this will make it easy to understand

    Best Of Luck for your Exam

    Happy coding

    Hamza Shafiq*/