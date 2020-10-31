package com.mindfulai.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mindfulai.AppPrefrences.AppPreferences;
import com.mindfulai.Models.orderDetailInfo.Product;
import com.mindfulai.ministore.R;
import com.mindfulai.Utils.GlobalEnum;

import java.util.ArrayList;
import java.util.List;

public class OrderSuccessAdapter extends RecyclerView.Adapter<OrderSuccessAdapter.MyViewHolder> {


    private Context context;
    List<Product> productsList;

    private static final String TAG = "OrderSuccessAdapter";

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView prod_image;
        TextView name, quantity, selling_price;

        public MyViewHolder(View view) {
            super(view);

            prod_image = view.findViewById(R.id.prod_image);
            name = view.findViewById(R.id.name);
            quantity = view.findViewById(R.id.quantity);
            selling_price = view.findViewById(R.id.selling_price);


        }
    }

    public OrderSuccessAdapter(Context context, List<Product> productsList) {

        this.context = context;
        this.productsList = productsList;

    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_details_products_layout, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        try {

            if (productsList.get(position).getProduct() != null) {
                holder.name.setText(productsList.get(position).getProduct().getProduct().getName().toUpperCase());
                holder.quantity.setText(productsList.get(position).getQuantity() + "");
                holder.selling_price.setText(context.getResources().getString(R.string.rs)  + productsList.get(position).getProduct().getPrice());

                if (productsList.get(position).getProduct().getImages() != null) {
                    Glide.with(context).load(GlobalEnum.AMAZON_URL + productsList.get(position)
                            .getProduct().getImages().getPrimary()).into(holder.prod_image);
                } else {
                    Glide.with(context).load(context.getResources().getDrawable(R.drawable.noimage)).into(holder.prod_image);
                }

            }
        } catch (Exception e) {
            Log.e(TAG, "onBindViewHolder: " + e);
        }
    }

    @Override
    public int getItemCount() {
        return productsList.size();
    }


}
