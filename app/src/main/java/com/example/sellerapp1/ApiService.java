package com.example.sellerapp1;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {
    @POST("addProduct")
    Call<DefaultResponse> addProduct(@Query("sellerId") int sellerId, @Query("name") String name, @Query("description") String description, @Query("price") double price);

    @GET("getProducts")
    Call<List<Products>> getProducts();

    @GET("searchProducts")
    Call<List<Products>> searchProducts(@Query("query") String query);
}
