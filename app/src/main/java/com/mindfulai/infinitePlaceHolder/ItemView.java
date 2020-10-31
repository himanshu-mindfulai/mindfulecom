package com.mindfulai.infinitePlaceHolder;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.util.Log;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.mindfulai.Activites.OrderHistoryDetailsActivity;
import com.mindfulai.Models.AllOrderHistory.Datum;
import com.mindfulai.Models.AllOrderHistory.Product;
import com.mindfulai.ministore.R;
import com.mindorks.placeholderview.annotations.Click;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.View;
import com.shuhart.stepview.StepView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Layout(R.layout.orderhistory_layout)
public class ItemView {

    private static final String TAG = "Itemview";
    @View(R.id.order_id)
    private TextView order_id;

    @View(R.id.order_date)
    private TextView order_date;


    @View(R.id.order_price)
    private TextView order_price;

    @View(R.id.card_view)
    private CardView card_view;

//    @View(R.id.order_status)
//    private TextView order_status;

//    @View(R.id.order_name)
//    private TextView order_name;

    @View(R.id.order_quantity)
    private TextView order_quantity;


    private Datum mInfo;
    private Context mContext;

    public ItemView(Context context, Datum info) {
        mContext = context;
        mInfo = info;

    }


    @Click(R.id.card_view)
    public void onCardClick() {


        List<Product> productsList = mInfo.getProducts();


        Intent i = new Intent(mContext, OrderHistoryDetailsActivity.class);
        i.putParcelableArrayListExtra("orderHistoryData", (ArrayList<? extends Parcelable>) productsList);
        i.putExtra("order_id_", mInfo.getId());
        i.putExtra("order_id", "# " + mInfo.getOrderId());
        String date = mInfo.getOrderDate();
        final String[] date1 = date.split("T");
        //date1[0]: 2020-06-18
        String[] date2 = date1[0].split("-");
        String strDate = date2[1] + "-" + date2[2] + "-" + date2[0];
        i.putExtra("order_date", strDate);
        i.putExtra("order_amount", mContext.getResources().getString(R.string.rs)  + mInfo.getAmount());
        i.putExtra("order_address", mInfo.getAddress());
        i.putExtra("order_delivery_slot",mInfo.getDeliverySlot());
        i.putExtra("order_type", mInfo.getOrderType());
        i.putExtra("order_payment_method", mInfo.getPaymentMethod());
        i.putExtra("order_paid_from_wallet", mInfo.getPaidFromWallet());
        i.putExtra("order_status", mInfo.getStatus());
        mContext.startActivity(i);

    }

    @Resolve
    private void onResolved() {

        order_id.setText(mInfo.getOrderId());
        String date = mInfo.getOrderDate();
        final String[] date1 = date.split("T");
        order_date.setText("" + date1[0]);
        order_price.setText(mContext.getResources().getString(R.string.rs)  + mInfo.getAmount());
        //order_status.setText(mInfo.getStatus());
        order_quantity.setText("Qty " + mInfo.getProducts().get(0).getQuantity());
    }
}