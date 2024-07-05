package com.example.sellerapp1;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ProductsFragment extends Fragment {

    private static final String TAG = "ProductsFragment";
    OnFragmentInteractionListener mListener;
    ImageView backbtn;
    RecyclerView productRecyclerView;
    ProductAdapter productAdapter;
    List<Products> productList;
    Button btnAddProduct;
    EditText productSearchView;
    Retrofit retrofit;
    WooCommerceApiService apiService;

    public ProductsFragment() {
        // Required empty public constructor
    }

    @Override
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
        productSearchView = view.findViewById(R.id.productSearchView);

        btnAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddProductActivity();
            }
        });

        productRecyclerView = view.findViewById(R.id.productRecyclerView);
        productList = new ArrayList<>();
        productAdapter = new ProductAdapter(productList, getContext());
        productRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        productRecyclerView.setAdapter(productAdapter);
        backbtn = view.findViewById(R.id.backbtn);

        productSearchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
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

        loadProducts(1);

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
        loadProducts(1);
    }

    private void openAddProductActivity() {
        Intent intent = new Intent(getActivity(), AddProductActivity.class);
        startActivity(intent);
    }

    private void loadProducts(int page) {
        final int perPage = 50;

        retrofit = ApiClient.getClient();
        apiService = retrofit.create(WooCommerceApiService.class);

        int userId = SessionManager.getInstance().getUserId();

        if (userId != -1) {
            ProductRequest request = new ProductRequest(userId);
            Call<List<Products>> call = apiService.getProducts(request, page, perPage);

            call.enqueue(new Callback<List<Products>>() {
                @Override
                public void onResponse(Call<List<Products>> call, Response<List<Products>> response) {
                    if (response.isSuccessful() && response.body() != null) {

                        Gson gson = new Gson();
                        String jsonResponse = gson.toJson(response.body());
                        Log.d(TAG, "Response Body: " + jsonResponse);
                        productList.clear();
                        productList.addAll(response.body());
                        if (productList.size() == perPage) {
                            loadProducts(page + 1);
                        }
                        productAdapter.updateFullList(productList);
                        productAdapter.notifyDataSetChanged();
                    } else {
                        try {
                            Log.e(TAG, "Failed to load products: " + response.code() + " - " + response.message());
                            Log.e(TAG, "Error body: " + response.errorBody().string());
                        } catch (IOException e) {
                            Log.e(TAG, "Failed to read error body", e);
                        }
                        Toast.makeText(getContext(), "Failed to load products: " + response.message(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<List<Products>> call, Throwable t) {
                    Toast.makeText(getContext(), "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Network Error: " + t.getMessage(), t);
                }
            });
        } else {
            Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
        }
    }




}
