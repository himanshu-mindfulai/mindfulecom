package com.mindfulai.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mindfulai.Activites.OrderHistoryDetailsActivity;
import com.mindfulai.Activites.ReturnOrderHistoryActivity;
import com.mindfulai.Models.AllOrderHistory.Attribute;
import com.mindfulai.Models.AllOrderHistory.DatumModel;
import com.mindfulai.Models.AllOrderHistory.Product;
import com.mindfulai.NetworkRetrofit.ApiService;
import com.mindfulai.NetworkRetrofit.ApiUtils;
import com.mindfulai.Utils.CommonUtils;
import com.mindfulai.Utils.CustomProgressDialog;
import com.mindfulai.Utils.GlobalEnum;
import com.mindfulai.Utils.SPData;
import com.mindfulai.ministore.R;

import java.text.DecimalFormat;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class OrderHistoryDetailsAdapter extends RecyclerView.Adapter<OrderHistoryDetailsAdapter.MyViewHolder> {


    private Context context;
    private List<Product> orderHistoryDetailsList;
    private String action;
    private String id;

    private static final String TAG = "OrderHistoryDetailsAdapter";



    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name,  selling_price, discount, tvProductReturn;
        ImageView prod_image;
        ImageButton radioOn,radioOff;
        CardView cvOrderItem;
        TextView noOfItems;
        TextView varients;
        LinearLayout linearLayout;
        ImageView cancelItem;
        TextView status;

        public MyViewHolder(View view) {
            super(view);
            varients = view.findViewById(R.id.varients);
            name = view.findViewById(R.id.name);
            selling_price = view.findViewById(R.id.selling_price);
            prod_image = view.findViewById(R.id.prod_image);
            discount = view.findViewById(R.id.discount);
            radioOff = view.findViewById(R.id.radio_off);
            radioOn = view.findViewById(R.id.radio_on);
            cvOrderItem = view.findViewById(R.id.cv_order_item);
            tvProductReturn = view.findViewById(R.id.tv_product_return);
            noOfItems = view.findViewById(R.id.no_items);
            cancelItem = view.findViewById(R.id.cancel_item);
            linearLayout = view.findViewById(R.id.ll);
            status = view.findViewById(R.id.status);
        }
    }

    public OrderHistoryDetailsAdapter(Context context, List<Product> orderHistoryDetailsList, String action,String id) {

        this.context = context;
        this.orderHistoryDetailsList = orderHistoryDetailsList;
        this.action = action;
        this.id=id;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_details_products_layout, parent, false);

        return new MyViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        try {

            StringBuilder sb = new StringBuilder();
            if (orderHistoryDetailsList.get(position).getProduct() != null) {
                if (orderHistoryDetailsList.get(position).getProduct().getAttributes() != null) {
                    List<Attribute> attributeList = orderHistoryDetailsList.get(position).getProduct().getAttributes();
                    for (int i = 0; i < attributeList.size(); i++) {
                        sb.append(attributeList.get(i).getOption().getValue());
                        sb.append(" ");
                    }
                }
                DecimalFormat df = new DecimalFormat("#.##");
                String discount = df.format(orderHistoryDetailsList.get(position).getDiscountAmount());
                if(orderHistoryDetailsList.get(position).getDiscountAmount() != 0){
                    holder.discount.setVisibility(View.VISIBLE);
                    holder.discount.setText("Saved " + context.getResources().getString(R.string.rs)  + Double.parseDouble(discount) * orderHistoryDetailsList.get(position).getQuantity());
                } else {
                    holder.discount.setVisibility(View.GONE);
                }
                holder.name.setText(orderHistoryDetailsList.get(position).getProduct().getProduct().getName());
                holder.varients.setText(sb);
                RequestOptions requestOptions = new RequestOptions();
                requestOptions.placeholder(R.drawable.noimage);
                requestOptions.error(R.drawable.noimage);
                try{
                    Glide.with(context)
                            .applyDefaultRequestOptions(requestOptions)
                            .load(GlobalEnum.AMAZON_URL + orderHistoryDetailsList.get(position).getProduct().getImages().getPrimary())
                            .into(holder.prod_image);
                } catch (Exception e){
                    e.printStackTrace();
                }

                if(orderHistoryDetailsList.get(position).getSellingPrice()!=null&& orderHistoryDetailsList.get(position).getDiscountAmount() != 0.0){
                    holder.selling_price.setText(context.getResources().getString(R.string.rs)  + (orderHistoryDetailsList.get(position).getSellingPrice() - Double.parseDouble(discount)));
                }else if(orderHistoryDetailsList.get(position).getSellingPrice()!=null){
                    holder.selling_price.setText(context.getResources().getString(R.string.rs)  + (orderHistoryDetailsList.get(position).getSellingPrice()));
                } else if(orderHistoryDetailsList.get(position).getDiscountAmount() != 0.0){
                    holder.selling_price.setText(context.getResources().getString(R.string.rs) + (orderHistoryDetailsList.get(position).getProduct().getPrice()- Double.parseDouble(discount)));
                }else{
                    holder.selling_price.setText(context.getResources().getString(R.string.rs)  + (orderHistoryDetailsList.get(position).getProduct().getPrice()));
                }

                holder.noOfItems.setText(orderHistoryDetailsList.get(position).getQuantity()+" item");
                if(orderHistoryDetailsList.get(position).getStatus()!=null&&orderHistoryDetailsList.get(position).getStatus().equals("Cancelled")){
                    holder.cvOrderItem.setEnabled(false);
                    holder.cancelItem.setVisibility(View.GONE);
                    holder.status.setText("Cancelled");
                    holder.status.setVisibility(View.VISIBLE);
                    holder.cvOrderItem.setBackgroundColor(context.getResources().getColor(R.color.colorAdd));
                }
                if(id.isEmpty())
                    holder.cancelItem.setVisibility(View.GONE);
                String order  =  ((OrderHistoryDetailsActivity)context).orderStatus;
                if(order.equals("Delivered")|| order.equals("Cancelled")){
                    holder.cvOrderItem.setEnabled(false);
                    holder.cvOrderItem.setBackgroundColor(context.getResources().getColor(R.color.colorAdd));
                    holder.status.setText(""+order);
                    holder.status.setVisibility(View.VISIBLE);
                    holder.cancelItem.setVisibility(View.GONE);
                }

                holder.cancelItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        final CustomProgressDialog customProgressDialog = CommonUtils.showProgressDialog(context,
                                "Please wait...");

                        ApiService apiService = ApiUtils.getHeaderAPIService(SPData.getAppPreferences().getUsertoken());
                        JsonObject jsonObject = new JsonObject();
                        jsonObject.addProperty("action", "cancel");
                        jsonObject.addProperty("order", id);
                        JsonArray products = new JsonArray();
                        products.add(orderHistoryDetailsList.get(position).getProduct().getId());
                        jsonObject.add("products", products);
                        apiService.orderAction(jsonObject).enqueue(new Callback<DatumModel>() {
                            @Override
                            public void onResponse(Call<DatumModel> call, Response<DatumModel> response) {
                                CommonUtils.hideProgressDialog(customProgressDialog);
                                if(response.isSuccessful()){
                                    holder.cvOrderItem.setEnabled(false);
                                    holder.cvOrderItem.setBackgroundColor(context.getResources().getColor(R.color.colorAdd));
                                    holder.status.setText("Cancelled");
                                    holder.status.setVisibility(View.VISIBLE);
                                    holder.cancelItem.setVisibility(View.GONE);
                                    Toast.makeText(context, "Item cancel", Toast.LENGTH_SHORT).show();
                                }else
                                    Toast.makeText(context, ""+response.body().getMessage(), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(Call<DatumModel> call, Throwable t) {

                            }
                        });
                    }
                });
            }
        } catch (Exception e) {
            Log.e("TAG", "onBindViewHolder: " + e);
        }

        String status = orderHistoryDetailsList.get(position).getStatus();
        if(status != null){
            Log.i("TAAAAAAAAAAAAG","*********************:  "+orderHistoryDetailsList);

            if(status.equals("Returned") || status.equals("Replaced")){
                holder.tvProductReturn.setVisibility(View.VISIBLE);
                int color = status.equals("Returned")?context.getResources().getColor(R.color.red):context.getResources().getColor(R.color.colorGold);
                holder.tvProductReturn.setText(status);
                holder.tvProductReturn.setTextColor(color);
            } else {
                holder.tvProductReturn.setVisibility(View.GONE);
            }
        }

        if(action.equals("no_action")){
            holder.radioOn.setVisibility(View.GONE);
            holder.radioOff.setVisibility(View.GONE);
        } else{
            holder.radioOff.setVisibility(View.VISIBLE);
            holder.cvOrderItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.i("TAAAAG","product id return adapter: "+ orderHistoryDetailsList.get(position).getProduct().getId());
                    if(holder.radioOff.getVisibility() == View.VISIBLE) {
                        holder.radioOn.setVisibility(View.VISIBLE);
                        holder.radioOff.setVisibility(View.GONE);
                        ((ReturnOrderHistoryActivity)context).add(orderHistoryDetailsList.get(position).getProduct().getId());
                    } else {
                        holder.radioOn.setVisibility(View.GONE);
                        holder.radioOff.setVisibility(View.VISIBLE);
                        ((ReturnOrderHistoryActivity)context).remove(orderHistoryDetailsList.get(position).getProduct().getId());
                    }
                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return orderHistoryDetailsList.size();
    }


}
