package com.example.sellerapp1;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {

    private static final String TAG = "OrderAdapter";
    private List<Order> orderList;
    private Retrofit retrofit;
    private WooCommerceApiService apiService;
    private Context context;

    public OrderAdapter(List<Order> orderList, Context context) {
        this.orderList = orderList;
        this.context = context;


    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Order order = orderList.get(position);
        holder.txtOrderId.setText("Order ID: " + order.getId());
        holder.txtCustomerName.setText("Customer: " + order.getBilling().getFirstName() + " " + order.getBilling().getLastName());
        holder.txtOrderTotal.setText("Total: " + order.getTotal());

        if (order.getStatus().equals("completed")) {
            holder.compBtn.setText(order.getStatus());
            holder.compBtn.setEnabled(false);
            holder.compBtn.setBackgroundColor(ContextCompat.getColor(context, R.color.completed_color));
        } else {
            holder.compBtn.setText(order.getStatus());
            holder.compBtn.setEnabled(true);
            holder.compBtn.setBackgroundColor(ContextCompat.getColor(context, R.color.default_color));
        }

        holder.compBtn.setOnClickListener(v -> markOrderAsCompleted(order, holder.compBtn));
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    private void markOrderAsCompleted(Order order, Button button) {
        Log.d(TAG, "Marking order as completed: " + order.getId());
        Order orderUpdate = new Order("completed");

        retrofit = ApiClient.getClient();
        apiService = retrofit.create(WooCommerceApiService.class);

        Call<Order> call = apiService.updateOrder(Integer.parseInt(order.getId()), orderUpdate);
        call.enqueue(new Callback<Order>() {
            @Override
            public void onResponse(Call<Order> call, Response<Order> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Order marked as completed successfully: " + order.getId());
                    Toast.makeText(button.getContext(), "Order marked as completed", Toast.LENGTH_SHORT).show();
                    button.setText("COMPLETED");
                    button.setEnabled(false);
                } else {
                    Log.e(TAG, "Failed to update order: " + response.code() + " - " + response.message());
                    try {
                        Log.e(TAG, "Error body: " + response.errorBody().string());
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing error body", e);
                    }
                    Toast.makeText(button.getContext(), "Failed to update order", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Order> call, Throwable t) {
                Log.e(TAG, "Network Error: " + t.getMessage(), t);
                Toast.makeText(button.getContext(), "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtOrderId;
        TextView txtCustomerName;
        TextView txtOrderTotal;
        Button compBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtOrderId = itemView.findViewById(R.id.orderIdTextView);
            txtCustomerName = itemView.findViewById(R.id.customerNameTextView);
            txtOrderTotal = itemView.findViewById(R.id.totalPriceTextView);
            compBtn = itemView.findViewById(R.id.completedBtn);
        }
    }
}
