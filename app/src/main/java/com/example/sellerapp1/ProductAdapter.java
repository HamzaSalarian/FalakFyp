package com.example.sellerapp1;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

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
        holder.txtProductPrice.setText(String.format("%.2f", product.getPrice()));
        if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()){
            Picasso.get().load(product.getImageUrl()).into(holder.tProductImage);
        }else {
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

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClick(product,context);
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    showDeleteConfirmationDialog(product, context);
                    return true;
                }
            });
        }

        public void onItemClick(Products product, Context context){

            String prodId = product.getProductId();
            Intent i = new Intent(context, UpdateProductsActivity.class);
            i.putExtra("prodId", prodId);
            //Toast.makeText(context, prodId,Toast.LENGTH_SHORT).show();
            context.startActivity(i);

        }

        private void showDeleteConfirmationDialog(Products product, Context context) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Delete Product");
            builder.setMessage("Are you sure you want to delete this product?");

            builder.setPositiveButton("Delete", (dialog, which) -> {
                deleteProduct(product.getProductId(), context);
                dialog.dismiss();
            });

            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

            AlertDialog dialog = builder.create();
            dialog.show();
        }

        private void deleteProduct(String productId, Context context) {

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if(user!=null){
                String uid = user.getUid();
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("products").child(uid).child(productId);

                databaseReference.removeValue().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(context, "Product deleted successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Failed to delete product", Toast.LENGTH_SHORT).show();
                    }
                });
            }else {
                Toast.makeText(context, "No User Found", Toast.LENGTH_SHORT).show();
            }


        }
    }
}
