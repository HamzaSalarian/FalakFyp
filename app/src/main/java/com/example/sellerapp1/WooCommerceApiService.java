package com.example.sellerapp1;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Header;

import retrofit2.http.POST;
import retrofit2.http.GET;
import retrofit2.http.PUT;

import retrofit2.http.Path;
import retrofit2.http.Query;

import java.util.List;

public interface WooCommerceApiService {
    @POST("custom/v1/products")
    Call<List<Products>> getProducts(@Body ProductRequest productRequest, @Query("page") int page, @Query("per_page") int perPage);


    @POST("wc/v3/products")
        Call<Products> addProduct(@Body Products product);

        @GET("wc/v3/products/{id}")
        Call<Products> getProduct(@Path("id") int id);

        @PUT("wc/v3/products/{id}")
        Call<Products> updateProduct(@Path("id") int id, @Body Products product);


    @DELETE("wc/v3/products/{id}")
    Call<Void> deleteProduct(@Path("id") int id, @Header("force") boolean force);

    @PUT("wc/v3/orders/{id}")
    Call<Order> updateOrder(@Path("id") int id, @Body Order orderUpdate);

    @POST("custom/v1/login")
    Call<LoginResponse> loginUser(@Body LoginRequest loginRequest);

    @POST("custom/v1/product")
    Call<Products> addOrUpdateProduct(@Body Products product);



    @POST("custom/v1/vendor-orders")
    Call<OrderResponse> getOrdersId(@Body OrderRequest orderRequest);

    @POST("custom/v1/retrieve-orders")
    Call<List<Order>> getOrders(@Body OrderRequest orderRequest, @Query("page") int page, @Query("per_page") int perPage);


}
