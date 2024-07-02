package com.example.sellerapp1;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

    private List<Products> productList;
    private List<Products> productListFull; // Full list for filtering
    private Context context;

    public ProductAdapter(List<Products> productList, Context context) {
        this.productList = productList;
        this.productListFull = new ArrayList<>(productList);
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_layout, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Products product = productList.get(position);
        holder.txtProductName.setText(product.getName());
        holder.txtProductDescription.setText(product.getDescription());
        holder.txtProductPrice.setText(product.getRegular_price());

        if (product.getImages() != null && !product.getImages().isEmpty()) {
            Picasso.get().load(product.getImages().get(0).getSrc()).into(holder.tProductImage);
        } else {
            // Optionally, set a placeholder image if no image URL is provided
            holder.tProductImage.setImageResource(R.drawable.baseline_image_24);
        }

        holder.bind(product, context);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public void filter(String text) {
        productList.clear();
        if (text == null || text.isEmpty()) {
            productList.addAll(productListFull);
        } else {
            text = text.toLowerCase();
            for (Products product : productListFull) {
                if (product.getName().toLowerCase().contains(text) || product.getDescription().toLowerCase().contains(text)) {
                    productList.add(product);
                }
            }
        }
        notifyDataSetChanged();
    }

    public void updateFullList(List<Products> newProductList) {
        productListFull.clear();
        productListFull.addAll(newProductList);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtProductName;
        TextView txtProductDescription;
        TextView txtProductPrice;
        ImageView tProductImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtProductName = itemView.findViewById(R.id.productName);
            txtProductDescription = itemView.findViewById(R.id.productDescription);
            txtProductPrice = itemView.findViewById(R.id.productPrice);
            tProductImage = itemView.findViewById(R.id.productImage);
        }

        public void bind(Products product, Context context) {
            itemView.setOnClickListener(v -> onItemClick(product, context));
            itemView.setOnLongClickListener(v -> {
                showDeleteConfirmationDialog(product, context);
                return true;
            });
        }

        public void onItemClick(Products product, Context context) {
            String prodId = String.valueOf(product.getId());
            Intent i = new Intent(context, UpdateProductsActivity.class);
            i.putExtra("prodId", prodId);
            context.startActivity(i);
        }

        private void showDeleteConfirmationDialog(Products product, Context context) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Delete Product");
            builder.setMessage("Are you sure you want to delete this product?");

            builder.setPositiveButton("Delete", (dialog, which) -> {
                deleteProduct(product.getId(), context);
                dialog.dismiss();
            });

            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

            AlertDialog dialog = builder.create();
            dialog.show();
        }

        private void deleteProduct(int productId, Context context) {
            SharedPreferences sharedPref = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
            String consumerKey = sharedPref.getString("consumer_key", null);
            String consumerSecret = sharedPref.getString("consumer_secret", null);

            if (consumerKey == null || consumerSecret == null) {
                Toast.makeText(context, "Consumer Key or Consumer Secret not found", Toast.LENGTH_SHORT).show();
                return;
            }

            Retrofit retrofit = ApiClient.getClient();
            WooCommerceApiService apiService = retrofit.create(WooCommerceApiService.class);

            Call<Void> call = apiService.deleteProduct(productId, true);
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(context, "Product deleted successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Failed to delete product: " + response.message(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(context, "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
