package com.mindfulai.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mindfulai.Models.AllOrderHistory.DatumModel;
import com.mindfulai.Models.orderDetailInfo.Attribute;
import com.mindfulai.Models.orderDetailInfo.Product;
import com.mindfulai.NetworkRetrofit.ApiService;
import com.mindfulai.NetworkRetrofit.ApiUtils;
import com.mindfulai.Utils.CommonUtils;
import com.mindfulai.Utils.CustomProgressDialog;
import com.mindfulai.Utils.SPData;
import com.mindfulai.ministore.R;
import com.mindfulai.Utils.GlobalEnum;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class OrderHistoryDetailsCartAdapter extends RecyclerView.Adapter<OrderHistoryDetailsCartAdapter.MyViewHolder> {


    private Context context;
    private List<com.mindfulai.Models.orderDetailInfo.Product> orderHistoryDetailsList;
    private static final String TAG = "OrderHistoryDetailsAdapter";
    private String orderId="";

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name, selling_price, discount;
        ImageView prod_image;


        public MyViewHolder(View view) {
            super(view);

            name = view.findViewById(R.id.name);
            selling_price = view.findViewById(R.id.selling_price);
            prod_image = view.findViewById(R.id.prod_image);
            discount = view.findViewById(R.id.discount);
        }
    }

    public OrderHistoryDetailsCartAdapter(Context context, List<Product> orderHistoryDetailsList) {

        this.context = context;
        this.orderHistoryDetailsList = orderHistoryDetailsList;

    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_details_products_layout, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {

            StringBuilder sb = new StringBuilder();
            if (orderHistoryDetailsList.get(position).getProduct() != null) {
                if (orderHistoryDetailsList.get(position).getProduct().getAttributes() != null) {
                    List<Attribute> attributeList = orderHistoryDetailsList.get(position).getProduct().getAttributes();
                    for (int i = 0; i < attributeList.size(); i++) {
                        sb.append(attributeList.get(i).getOption().getValue());
                        sb.append(" ");
                    }
                }

                holder.name.setText(orderHistoryDetailsList.get(position).getProduct().getProduct().getName() + " " + sb.toString());
                DecimalFormat df = new DecimalFormat("#.##");
                String discount = df.format(orderHistoryDetailsList.get(position).getDiscountAmount());

                if(orderHistoryDetailsList.get(position).getDiscountAmount() != 0){
                    holder.discount.setVisibility(View.VISIBLE);
                    holder.discount.setText("Saved " + context.getResources().getString(R.string.rs)  + Double.parseDouble(discount) * orderHistoryDetailsList.get(position).getQuantity());
                } else {
                    holder.discount.setVisibility(View.GONE);
                }
                if (orderHistoryDetailsList.get(position).getProduct().getImages() != null) {
                    Glide.with(context).load(GlobalEnum.AMAZON_URL + orderHistoryDetailsList.get(position)
                            .getProduct().getImages().getPrimary()).into(holder.prod_image);
                } else {
                    Glide.with(context).load(R.drawable.noimage).into(holder.prod_image);
                }
                Log.e("OrderHistoryDetails", "sellingPrice: " + orderHistoryDetailsList.get(position).getProduct().getSellingPrice());
                if(orderHistoryDetailsList.get(position).getProduct().getSellingPrice()!= 0
                        && orderHistoryDetailsList.get(position).getDiscountAmount() != 0.0) {

                    holder.selling_price.setText(context.getResources().getString(R.string.rs) + (orderHistoryDetailsList.get(position).getSellingPrice() - Double.parseDouble(discount)) + " x " + orderHistoryDetailsList.get(position).getQuantity());
                } else if (orderHistoryDetailsList.get(position).getDiscountAmount() != 0.0){

                    holder.selling_price.setText(context.getResources().getString(R.string.rs)  + (orderHistoryDetailsList.get(position).getProduct().getPrice() - Double.parseDouble(discount)) + " x " + orderHistoryDetailsList.get(position).getQuantity());
                } else {
                    Log.e("OrderHistoryDetails", "sellingPrice: " + orderHistoryDetailsList.get(position).getSellingPrice());
                    Log.e("OrderHistoryDetails", "price: " + orderHistoryDetailsList.get(position).getProduct().getPrice());
                    Log.e("OrderHistoryDetails", "discount: " + orderHistoryDetailsList.get(position).getDiscountAmount());
                    if (orderHistoryDetailsList.get(position).getSellingPrice() != 0.0) {
                        holder.selling_price.setText(context.getResources().getString(R.string.rs) + orderHistoryDetailsList.get(position).getSellingPrice() + " x " + orderHistoryDetailsList.get(position).getQuantity());
                    } else {

                    }
                }

            }
    }

    @Override
    public int getItemCount() {
        return orderHistoryDetailsList.size();
    }


}
