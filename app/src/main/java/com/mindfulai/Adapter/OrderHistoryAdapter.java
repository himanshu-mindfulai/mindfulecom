package com.mindfulai.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.mindfulai.Activites.OrderHistoryDetailsActivity;
import com.mindfulai.Models.AllOrderHistory.Datum;
import com.mindfulai.Models.AllOrderHistory.Product;
import com.mindfulai.ministore.R;
import com.shuhart.stepview.StepView;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.OrderHistoryViewHolder> {

    private Context mContext;
    private List<Datum> mInfo;
    private String orderDate="";

    public OrderHistoryAdapter(Context context, List<Datum> datumList) {
        this.mContext = context;
        this.mInfo = datumList;
    }

    @NonNull
    @Override
    public OrderHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.orderhistory_layout, parent, false);
        return new OrderHistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderHistoryViewHolder holder, int position) {

        holder.order_id.setText("Order id- " + mInfo.get(position).getOrderId());
        String date = mInfo.get(position).getOrderDate();
        final String[] date1 = date.split("T");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date_original = sdf.parse(date1[0]);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, dd MMM YYYY");
            orderDate = simpleDateFormat.format(date_original);
            Log.e("TAG", "onBindViewHolder: "+orderDate);
            holder.order_date.setText("Placed On " + orderDate);
        } catch (ParseException e) {
            e.printStackTrace();
            Log.e("TAG", "onBindViewHolder: "+e );
        }
        if(mInfo.get(position).getStatus().equals("Cancelled")){
            holder.linearLayoutCancelled.setVisibility(View.VISIBLE);
            holder.linearLayoutSteps.setVisibility(View.GONE);
        }else{
            holder.linearLayoutSteps.setVisibility(View.VISIBLE);
            holder.linearLayoutCancelled.setVisibility(View.GONE);
        }
        if(mInfo.get(position).getStatus().toLowerCase().contains("placed")){
            holder.textViewPlaced.setBackground(mContext.getDrawable(R.drawable.step_done));
        }else if(mInfo.get(position).getStatus().toLowerCase().contains("dispatch")){
            holder.textViewPlaced.setBackground(mContext.getDrawable(R.drawable.step_done));
            holder.textViewDispatch.setText("");
            holder.textViewDispatch.setBackground(mContext.getDrawable(R.drawable.step_done));
        }else if(mInfo.get(position).getStatus().toLowerCase().contains("delivered")){
            holder.textViewDispatch.setText("");
            holder.textViewDelivered.setText("");
            holder.textViewPlaced.setBackground(mContext.getDrawable(R.drawable.step_done));
            holder.textViewDispatch.setBackground(mContext.getDrawable(R.drawable.step_done));
            holder.textViewDelivered.setBackground(mContext.getDrawable(R.drawable.step_done));
        }

        holder.order_price.setText(mContext.getResources().getString(R.string.rs) + mInfo.get(position).getAmount());
        holder.delivery_charge.setText(mContext.getResources().getString(R.string.rs)  + mInfo.get(position).getDeliveryCharge());
        int qty=0;
        for (int i=0;i<mInfo.get(position).getProducts().size();i++){
            qty = qty+ mInfo.get(position).getProducts().get(i).getQuantity();
        }
        holder.order_quantity.setText("Qty " + qty);
        holder.viewDetail.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onClick(View view) {
                List<Product> productsList = mInfo.get(position).getProducts();
                Intent i = new Intent(mContext, OrderHistoryDetailsActivity.class);
                i.putParcelableArrayListExtra("orderHistoryData", (ArrayList<? extends Parcelable>) productsList);
                i.putExtra("order_id_", ""+mInfo.get(position).getId());
                i.putExtra("order_id", "Order id- " + mInfo.get(position).getOrderId());
                i.putExtra("order_date", holder.order_date.getText());
                i.putExtra("order_amount", ""+ mInfo.get(position).getAmount());
                i.putExtra("order_address", mInfo.get(position).getAddress());
                i.putExtra("order_delivery_slot", mInfo.get(position).getDeliverySlot());
                i.putExtra("order_type", mInfo.get(position).getOrderType());
                i.putExtra("order_payment_method", mInfo.get(position).getPaymentMethod());
                i.putExtra("order_paid_from_wallet", mInfo.get(position).getPaidFromWallet());
                i.putExtra("order_status", mInfo.get(position).getStatus());
                i.putExtra("order_carrybag_charge",mInfo.get(position).getCarryBagCharge());
                i.putExtra("position",position);
                i.putExtra("order_delivery_charge", "" + mInfo.get(position).getDeliveryCharge());
                if(mInfo.get(position).getCoupon()!=null){
                    i.putExtra("coupon_discount",""+mInfo.get(position).getCoupon().getDiscountAmt());
                }
                i.putExtra("amount_paid", mContext.getResources().getString(R.string.rs) + new DecimalFormat("#.##").format(mInfo.get(position).getAmount()-mInfo.get(position).getPaidFromWallet()));
                ((Activity) mContext).startActivityForResult(i,3);
            }
        });

    }
    public void cancelledOrder(int position){
        if(position!=-1) {
            mInfo.get(position).setStatus("Cancelled");
            notifyItemChanged(position);
        }
    }

    @Override
    public int getItemCount() {
        return mInfo.size();
    }

    class OrderHistoryViewHolder extends RecyclerView.ViewHolder {
        private TextView order_id;

        private TextView order_date;


        private TextView order_price;

        private CardView card_view;


        //   private TextView order_status;

//    @View(R.id.order_name)
//    private TextView order_name;

        private TextView order_quantity;
        private TextView delivery_charge;
        private Button viewDetail;
        private TextView textViewPlaced;
        private TextView textViewPacked;
        private TextView textViewDispatch;
        private LinearLayout linearLayoutSteps;
        private LinearLayout linearLayoutCancelled;
        private TextView textViewDelivered;


        public OrderHistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            order_id = itemView.findViewById(R.id.order_id);
            order_date = itemView.findViewById(R.id.order_date);
            order_price = itemView.findViewById(R.id.order_price);
            card_view = itemView.findViewById(R.id.card_view);
            //       order_status = itemView.findViewById(R.id.order_status);
            order_quantity = itemView.findViewById(R.id.order_quantity);
            delivery_charge = itemView.findViewById(R.id.dv_charge);
            linearLayoutSteps = itemView.findViewById(R.id.step_layout);
            linearLayoutCancelled = itemView.findViewById(R.id.cancelled_layout);
            viewDetail = itemView.findViewById(R.id.view_detail);
            textViewPlaced = itemView.findViewById(R.id.status_placed);
            textViewDelivered=itemView.findViewById(R.id.status_delivered);
            textViewDispatch = itemView.findViewById(R.id.status_dispatch);
            textViewPacked = itemView.findViewById(R.id.status_packed);
        }
    }
}
