package com.mindfulai.Adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.JsonObject;
import com.mindfulai.Activites.AddAddressActivity;
import com.mindfulai.Activites.CommonActivity;
import com.mindfulai.Activites.OrderHistoryDetailsActivity;
import com.mindfulai.Activites.OrderPlacedActivity;
import com.mindfulai.Activites.ProductDetailsActivity;
import com.mindfulai.Models.UserDataAddress;
import com.mindfulai.Models.orderDetailInfo.OrderDetailInfo;
import com.mindfulai.NetworkRetrofit.ApiService;
import com.mindfulai.NetworkRetrofit.ApiUtils;
import com.mindfulai.NetworkRetrofit.ServerURL;
import com.mindfulai.Utils.CommonUtils;
import com.mindfulai.Utils.CustomProgressDialog;
import com.mindfulai.Utils.SPData;
import com.mindfulai.ministore.R;
import com.mindfulai.ui.CartFragment;
import com.razorpay.Checkout;
import com.razorpay.RazorpayClient;
import com.valdesekamdem.library.mdtoast.MDToast;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Callback;

public class UserAddressesAdapter extends RecyclerView.Adapter<UserAddressesAdapter.MyViewHolder> {

    private final boolean codAvailable;
    private List<UserDataAddress> userDataAddressList;
    private static final String TAG = "UserAddressAdapter";
    private Context context;
    private CartFragment cartFragment;
    private CustomProgressDialog customProgressDialog;
    private String message;
    private RazorpayClient razorpayClient;
    private Checkout checkout;
    private String aid, sellingPrice;
    private androidx.appcompat.app.AlertDialog alertDialog;
    private final String DELETE_ADDRESS = ServerURL.SERVER_URL + "/api/address/";
    private int index = 0;
    private String orderId;
    private String TYPE;
    private String varient_id;
    private TextView timslot;
    private String couponCode;
    private String OrderType;
    private boolean isCarryBagChecked;


    static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView address_location;
        TextView address_type;
        FloatingActionButton address_edit;
        FloatingActionButton address_delete;
        TextView address_deliver_here;

        MyViewHolder(View view) {
            super(view);

            address_location = view.findViewById(R.id.complete_address);
            address_type = view.findViewById(R.id.address_type);
            address_edit = view.findViewById(R.id.edit);
            address_delete = view.findViewById(R.id.delete);
            address_deliver_here = view.findViewById(R.id.deliver_here);

        }
    }

    public UserAddressesAdapter(
            Context context,
            List<UserDataAddress> address,
            CartFragment cartFragment,
            androidx.appcompat.app.AlertDialog alertDialog1,
            boolean codAvailable,
            String price,
            String TYPE,
            String varient_id, TextView sid, String couponCode,boolean isCarryBagChecked) {

        this.userDataAddressList = address;
        this.context = context;
        this.cartFragment = cartFragment;
        this.alertDialog = alertDialog1;
        this.codAvailable = codAvailable;
        this.sellingPrice = price;
        this.TYPE = TYPE;
        this.varient_id = varient_id;
        this.timslot = sid;
        this.couponCode = couponCode;
        this.OrderType = "delivery";
        this.isCarryBagChecked =isCarryBagChecked;
    }

    public UserAddressesAdapter(Context context, String couponCode,boolean isCarryBagChecked){
        codAvailable = SPData.showCod();
        this.context = context;
        TYPE = "PRODUCT";
        OrderType = "pickup";
        this.couponCode = couponCode;
        this.isCarryBagChecked =isCarryBagChecked;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.address_view, parent, false);

        return new MyViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        try {
            holder.address_type.setText("Address " + (position + 1));
            if (userDataAddressList.get(position).getName() != null && userDataAddressList.get(position).getMobile_number() != null)
                holder.address_location.setText(userDataAddressList.get(position).getName() + "\n" + userDataAddressList.get(position).getAddressLine1() + "\n" + userDataAddressList.get(position).getAddressLine2() + ", " + userDataAddressList.get(position).getCity() + "\n" + userDataAddressList.get(position).getState() + ", " + userDataAddressList.get(position).getPincode() + "\n" + userDataAddressList.get(position).getMobile_number());
            else
                holder.address_location.setText(userDataAddressList.get(position).getAddressLine1() + "\n" + userDataAddressList.get(position).getAddressLine2() + ", " + userDataAddressList.get(position).getCity() + "\n" + userDataAddressList.get(position).getState() + ", " + userDataAddressList.get(position).getPincode());
            holder.address_edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, AddAddressActivity.class);
                    intent.putExtra("title", "Update Address");
                    intent.putExtra("id", userDataAddressList.get(position).get_id());
                    intent.putExtra("houseno", userDataAddressList.get(position).getAddressLine1());
                    intent.putExtra("locality", userDataAddressList.get(position).getAddressLine2());
                    intent.putExtra("city", userDataAddressList.get(position).getCity());
                    intent.putExtra("state", userDataAddressList.get(position).getState());
                    intent.putExtra("pincode", userDataAddressList.get(position).getPincode());
                    if (cartFragment != null)
                        cartFragment.startActivityForResult(intent, 2);
                }
            });

            holder.address_deliver_here.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    aid = userDataAddressList.get(position).get_id();
                    context.getSharedPreferences("order", Context.MODE_PRIVATE).edit().putString("aid", aid).apply();
                    showPaymentMode();
                }
            });

            holder.address_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showPopup(userDataAddressList.get(position).get_id(), holder);
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "onBindViewHolder: " + e);
        }

    }

    public void showPaymentMode() {
        final AlertDialog.Builder alert = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.select_payment_mode_view, null);
        alert.setView(view);
        final RadioButton cod = view.findViewById(R.id.order_payment_1);
        final RadioButton razor = view.findViewById(R.id.order_payment_2);
        if (SPData.showCod() && codAvailable) {
            cod.setVisibility(View.VISIBLE);
        } else {
            cod.setVisibility(View.GONE);
            razor.setChecked(true);
            index = 1;
        }

        if (!SPData.showOnlinePay()){
            razor.setVisibility(View.GONE);
        }

        TextView cancel = view.findViewById(R.id.cancel);
        Button save = view.findViewById(R.id.save);

        cod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                index = 0;
                razor.setBackground(null);
            }
        });
        razor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                index = 1;
                cod.setBackground(null);
            }
        });


        final AlertDialog alertDialog = alert.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        save.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.e(TAG, "onClick: " + index);
                        if (index == 1) {
                            takePayment();
//                            if (sellingPrice!= null && SPData.showServicesTab() && SPData.getProductsOrServices().equals("SERVICES")){
//                                takePayment(sellingPrice);
//                                return;
//                            }
//                            customProgressDialog = CommonUtils.showProgressDialog(context,
//                                    "Please wait ... ");
//                            ApiService apiService = ApiUtils.getHeaderAPIService(SPData.getAppPreferences().getUsertoken());
//                            apiService.showCartItems().enqueue(new Callback<CartDetailsInformation>() {
//                                @Override
//                                public void onResponse(@NonNull Call<CartDetailsInformation> call, @NonNull retrofit2.Response<CartDetailsInformation> response) {
//                                    if (response.isSuccessful()) {
//                                        alertDialog.dismiss();
//                                        Log.e(TAG, "onResponse: " + response);
//                                        CartDetailsInformation cartDetailsInfo = response.body();
//                                        assert cartDetailsInfo != null;
//                                        Integer price = cartDetailsInfo.getData().getTotal();
//                                        Log.e(TAG, "onResponse: " + price);
//                                        takePayment("" + price);
//                                    } else
//                                        MDToast.makeText(context, "" + response.message(), MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
//                                }
//
//                                @Override
//                                public void onFailure(@NonNull Call<CartDetailsInformation> call, @NonNull Throwable t) {
//                                    CommonUtils.hideProgressDialog(customProgressDialog);
//                                    MDToast.makeText(context, "" + t.getMessage(), MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
//                                }
//                            });
                        } else {
                            alertDialog.dismiss();
                            if (TYPE.equals("PRODUCT")) {
                                placeOrderCOD();
                            } else {
                                placeOrderServiceCOD();
                            }
                        }
                    }
                }
        );

    }

    private void showPopup(final String id, final MyViewHolder holder) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setMessage("Are you sure you want to delete this address")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        customProgressDialog = CommonUtils.showProgressDialog(context, "Please wait.. ");
                        String UPDATED_DELETE_URL = DELETE_ADDRESS + id;
                        Log.e(TAG, "onClick: " + UPDATED_DELETE_URL);
                        new DeleteAddress().execute(UPDATED_DELETE_URL);
                        holder.itemView.setVisibility(View.GONE);
                        userDataAddressList.remove(holder.getAdapterPosition());
                        notifyItemRemoved(holder.getAdapterPosition());
                    }
                }).setNegativeButton("Cancel", null);

        AlertDialog alert1 = alert.create();
        alert1.show();
    }

    private void takePayment() {
        checkout = new Checkout();
        checkout.setImage(R.mipmap.ic_launcher);
        checkout.setKeyID(SPData.getRazorPayKey());
        JSONObject options = new JSONObject();
        try {
            razorpayClient = new RazorpayClient(SPData.getRazorPayKey(), SPData.getRazorPaySecret());
            options.put("currency", "INR");
            options.put("receipt", "");
            options.put("payment_capture", true);
            if(TYPE.equals("SERVICE")){
                generateOrderIdForService(options);
            } else {
                generateOrderId(options);
            }
            //new DoPayment().execute(options);
        } catch (Exception e) {
            Log.e(TAG, "takePayment: " + e);
        }
    }

    private void placeOrderServiceCOD() {
        try {
            final CustomProgressDialog customProgressDialog = CommonUtils.showProgressDialog(context,
                    "Please wait...");
            ApiService apiService = ApiUtils.getHeaderAPIService(SPData.getAppPreferences().getUsertoken());
            JsonObject jsonObject1 = new JsonObject();
            jsonObject1.addProperty("varient", varient_id);
            jsonObject1.addProperty("address", aid);
            jsonObject1.addProperty("deliverySlot", timslot.getText().toString().split("\\(")[0]);
            jsonObject1.addProperty("paymentMethod", "COD");

            apiService.PlaceOrderService(jsonObject1).enqueue(new Callback<OrderDetailInfo>() {
                @Override
                public void onResponse(@NonNull Call<OrderDetailInfo> call, @NonNull retrofit2.Response<OrderDetailInfo> response) {
                    CommonUtils.hideProgressDialog(customProgressDialog);
                    if (response.isSuccessful()) {
                        try {
                            alertDialog.dismiss();
                            OrderDetailInfo placeOrder = response.body();
                            Log.e("Blab", "vdgsvs");
                            SPData.getAppPreferences().setTotalCartCount(0);
                            String order_id = placeOrder.getData().getOrder().getOrderId();
                            String order_date = placeOrder.getData().getOrder().getOrderDate();
                            String[] datenew = order_date.split("T");
                            order_date = datenew[0];
                            String order_price = String.valueOf(placeOrder.getData().getOrder().getAmount());
                            List<com.mindfulai.Models.orderDetailInfo.Product> productsListnew = placeOrder.getData().getOrder().getProducts();
                            Intent i = new Intent(context, OrderHistoryDetailsActivity.class);
                            i.putParcelableArrayListExtra("orderHistoryData", (ArrayList<? extends Parcelable>) productsListnew);
                            i.putExtra("order_id", "# " + order_id);
                            String date = order_date;
                            final String[] date1 = date.split("T");
                            i.putExtra("order_date", "" + date1[0]);
                            i.putExtra("cart", "true");
                            i.putExtra("order_type", placeOrder.getData().getOrderType());
                            i.putExtra("order_amount", context.getResources().getString(R.string.rs) + order_price);
                            i.putExtra("order_address", placeOrder.getData().getOrder().getAddress());
                            i.putExtra("order_delivery_slot", placeOrder.getData().getOrder().getDeliverySlot());
                            i.putExtra("order_payment_method", placeOrder.getData().getOrder().getPaymentMethod());
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            context.startActivity(i);
                        } catch (Exception e) {
                            e.printStackTrace();
                            orderId = "";
                            Log.e(TAG, "onResponse: " + e);
                            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e("TAG", "onResponse: " + response);
                        Toast.makeText(context, "Something went wrong !!", Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onFailure(@NonNull Call<OrderDetailInfo> call, @NonNull Throwable t) {
                    CommonUtils.hideProgressDialog(customProgressDialog);
                    t.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void placeOrderCOD() {
        try {
            final CustomProgressDialog customProgressDialog = CommonUtils.showProgressDialog(context,
                    "Please wait...");
            ApiService apiService = ApiUtils.getHeaderAPIService(SPData.getAppPreferences().getUsertoken());
            JsonObject jsonObject1 = new JsonObject();
            if (OrderType.equals("delivery")){
                jsonObject1.addProperty("address", aid);
                jsonObject1.addProperty("deliverySlot", timslot.getText().toString().split("\\(")[0]);
                jsonObject1.addProperty("deliveryType", OrderType);
            } else if (OrderType.equals("pickup")){
                jsonObject1.addProperty("deliveryType", OrderType);
            }
            jsonObject1.addProperty("carryBag",isCarryBagChecked);
            if (couponCode!=null && !couponCode.isEmpty()){
                jsonObject1.addProperty("coupon", couponCode);
            }
            jsonObject1.addProperty("paymentMethod", "COD");
            apiService.PlaceOrder(jsonObject1).enqueue(new Callback<OrderDetailInfo>() {
                @Override
                public void onResponse(@NonNull Call<OrderDetailInfo> call, @NonNull retrofit2.Response<OrderDetailInfo> response) {
                    CommonUtils.hideProgressDialog(customProgressDialog);
                    Log.e("TAG", "onResponse: "+response );
                    if (response.isSuccessful()) {
                        try {
                          //  alertDialog.dismiss();
                            OrderDetailInfo placeOrder = response.body();
                            SPData.getAppPreferences().setTotalCartCount(0);
                            String order_id = placeOrder.getData().getOrder().getOrderId();
                            String payment_method = placeOrder.getData().getOrder().getPaymentMethod();
                            String payment_amount = ""+placeOrder.getData().getOrder().getAmount();
                            Log.e("TAG", "onResponse: "+payment_method );
                            Intent intent =new Intent(context,OrderPlacedActivity.class);
                            intent.putExtra("order_id",""+order_id);
                            intent.putExtra("payment_method",""+payment_method);
                            intent.putExtra("payment_amount",""+payment_amount);
                            context.startActivity(intent);

//                            String order_date = placeOrder.getData().getOrder().getOrderDate();
//                            String[] datenew = order_date.split("T");
//                            order_date = datenew[0];
//                            String order_price = String.valueOf(placeOrder.getData().getOrder().getAmount());
//                            List<com.mindfulai.Models.orderDetailInfo.Product> productsListnew = placeOrder.getData().getOrder().getProducts();
//                            Intent i = new Intent(context, OrderPlacedActivity.class);
//                            i.putParcelableArrayListExtra("orderHistoryData", (ArrayList<? extends Parcelable>) productsListnew);
//                            i.putExtra("order_id", "# " + order_id);
//                            String date = order_date;
//                            final String[] date1 = date.split("T");
//                            i.putExtra("order_date", "" + date1[0]);
//                            i.putExtra("order_type", placeOrder.getData().getOrderType());
//                            i.putExtra("cart", "true");
//                            i.putExtra("order_amount", context.getResources().getString(R.string.rs) + " " + order_price);
//                            i.putExtra("order_address", placeOrder.getData().getOrder().getAddress());
//                            i.putExtra("order_delivery_slot", placeOrder.getData().getOrder().getDeliverySlot());
//                            i.putExtra("order_payment_method", placeOrder.getData().getOrder().getPaymentMethod());
//                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                            context.startActivity(i);
                        } catch (Exception e) {
                            e.printStackTrace();
                            orderId = "";
                            Log.e(TAG, "onResponse: " + e);
                            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e("TAG", "onResponse: " + response);
                        Toast.makeText(context, "Something went wrong !!", Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onFailure(@NonNull Call<OrderDetailInfo> call, @NonNull Throwable t) {
                    CommonUtils.hideProgressDialog(customProgressDialog);
                    t.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void generateOrderIdForService(final JSONObject options) {
        try {
            final CustomProgressDialog customProgressDialog = CommonUtils.showProgressDialog(context,
                    "Please wait...");
            ApiService apiService = ApiUtils.getHeaderAPIService(SPData.getAppPreferences().getUsertoken());
            JsonObject jsonObject1 = new JsonObject();
            jsonObject1.addProperty("address", aid);
                jsonObject1.addProperty("deliverySlot", timslot.getText().toString().split("\\(")[0]);
            jsonObject1.addProperty("varient", varient_id);
            jsonObject1.addProperty("paymentMethod", "Online");
            apiService.PlaceOrderService(jsonObject1).enqueue(new Callback<OrderDetailInfo>() {
                @Override
                public void onResponse(@NonNull Call<OrderDetailInfo> call, @NonNull retrofit2.Response<OrderDetailInfo> response) {
                    CommonUtils.hideProgressDialog(customProgressDialog);
                    if (response.isSuccessful()) {
                        try {
                            alertDialog.dismiss();
                            OrderDetailInfo placeOrder = response.body();
                            String action = placeOrder.getData().getAction();
                            Log.e("Action", action);
                            if(action.equals("razorpay")){
                                orderId = placeOrder.getData().getOrderId();
                                new DoPayment().execute(options);
                            } else if (action.equals("order")){
                                try{
                                    Log.e("Blab", "vdgsvs");
                                    SPData.getAppPreferences().setTotalCartCount(0);
                                    String order_id = placeOrder.getData().getOrder().getOrderId();
                                    String order_date = placeOrder.getData().getOrder().getOrderDate();
                                    String[] datenew = order_date.split("T");
                                    order_date = datenew[0];
                                    String order_price = String.valueOf(placeOrder.getData().getOrder().getAmount());
                                    List<com.mindfulai.Models.orderDetailInfo.Product> productsListnew = placeOrder.getData().getOrder().getProducts();
                                    Intent i = new Intent(context, OrderHistoryDetailsActivity.class);
                                    i.putParcelableArrayListExtra("orderHistoryData", (ArrayList<? extends Parcelable>) productsListnew);
                                    i.putExtra("order_id", "# " + order_id);
                                    String date = order_date;
                                    final String[] date1 = date.split("T");
                                    i.putExtra("order_date", "" + date1[0]);
                                    i.putExtra("cart", "true");
                                    i.putExtra("order_amount", context.getResources().getString(R.string.rs)+ order_price);
                                    i.putExtra("order_type", placeOrder.getData().getOrderType());
                                    i.putExtra("order_address", placeOrder.getData().getOrder().getAddress());
                                    i.putExtra("order_delivery_slot", placeOrder.getData().getOrder().getDeliverySlot());
                                    i.putExtra("order_payment_method", placeOrder.getData().getOrder().getPaymentMethod());
                                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    context.startActivity(i);
                                } catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            orderId = "";
                            Log.e(TAG, "onResponse: " + e);
                            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e("TAG", "onResponse: " + response);
                        Toast.makeText(context, "Something went wrong !!", Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onFailure(@NonNull Call<OrderDetailInfo> call, @NonNull Throwable t) {
                    CommonUtils.hideProgressDialog(customProgressDialog);
                    t.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void generateOrderId(final JSONObject options) {
        try {
            final CustomProgressDialog customProgressDialog = CommonUtils.showProgressDialog(context,
                    "Please wait...");
            ApiService apiService = ApiUtils.getHeaderAPIService(SPData.getAppPreferences().getUsertoken());
            JsonObject jsonObject1 = new JsonObject();
            if (OrderType.equals("delivery")){
                jsonObject1.addProperty("address", aid);
                jsonObject1.addProperty("deliverySlot", timslot.getText().toString().split("\\(")[0]);
                jsonObject1.addProperty("deliveryType", OrderType);
            } else if (OrderType.equals("pickup")){
                jsonObject1.addProperty("deliveryType", OrderType);
            }
            jsonObject1.addProperty("paymentMethod", "Online");
            if (couponCode!=null && !couponCode.isEmpty()){
                jsonObject1.addProperty("coupon", couponCode);
            }
            jsonObject1.addProperty("carryBag",isCarryBagChecked);
            apiService.PlaceOrder(jsonObject1).enqueue(new Callback<OrderDetailInfo>() {
                @Override
                public void onResponse(@NonNull Call<OrderDetailInfo> call, @NonNull retrofit2.Response<OrderDetailInfo> response) {
                    CommonUtils.hideProgressDialog(customProgressDialog);
                    if (response.isSuccessful()) {
                        try {
//                            alertDialog.dismiss();
                            OrderDetailInfo placeOrder = response.body();
                            String action = placeOrder.getData().getAction();

                            if(action.equals("razorpay")){
                                orderId = placeOrder.getData().getOrderId();
                                new DoPayment().execute(options);
                            } else if (action.equals("order")){
                                try{
                                    Log.e("Blab", "vdgsvs");
                                    SPData.getAppPreferences().setTotalCartCount(0);
                                    SPData.getAppPreferences().setTotalCartCount(0);
                                    String order_id = placeOrder.getData().getOrder().getOrderId();
                                    String payment_method = placeOrder.getData().getOrder().getPaymentMethod();
                                    String payment_amount = ""+placeOrder.getData().getOrder().getAmount();
                                    Intent intent =new Intent(context,OrderPlacedActivity.class);
                                    intent.putExtra("order_id",""+order_id);
                                    intent.putExtra("payment_method",""+payment_method);
                                    intent.putExtra("payment_amount",""+payment_amount);
                                    context.startActivity(intent);

//                                    String order_id = placeOrder.getData().getOrder().getOrderId();
//                                    String order_date = placeOrder.getData().getOrder().getOrderDate();
//                                    String[] datenew = order_date.split("T");
//                                    order_date = datenew[0];
//                                    String order_price = String.valueOf(placeOrder.getData().getOrder().getAmount());
//                                    List<com.mindfulai.Models.orderDetailInfo.Product> productsListnew = placeOrder.getData().getOrder().getProducts();
//                                    Intent i = new Intent(context, OrderHistoryDetailsActivity.class);
//                                    i.putParcelableArrayListExtra("orderHistoryData", (ArrayList<? extends Parcelable>) productsListnew);
//                                    i.putExtra("order_id", "# " + order_id);
//                                    String date = order_date;
//                                    final String[] date1 = date.split("T");
//                                    i.putExtra("order_date", "" + date1[0]);
//                                    i.putExtra("cart", "true");
//                                    i.putExtra("order_type", placeOrder.getData().getOrderType());
//                                    i.putExtra("order_amount", context.getResources().getString(R.string.rs)  + order_price);
//                                    i.putExtra("order_address", placeOrder.getData().getOrder().getAddress());
//                                    i.putExtra("order_delivery_slot", placeOrder.getData().getOrder().getDeliverySlot());
//                                    i.putExtra("order_payment_method", placeOrder.getData().getOrder().getPaymentMethod());
//                                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                    context.startActivity(i);
                                } catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            orderId = "";
                            Log.e(TAG, "onResponse: " + e);
                            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e("TAG", "onResponse: " + response);
                        Toast.makeText(context, "Something went wrong !!", Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onFailure(@NonNull Call<OrderDetailInfo> call, @NonNull Throwable t) {
                    CommonUtils.hideProgressDialog(customProgressDialog);
                    t.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("StaticFieldLeak")
    class DoPayment extends AsyncTask<JSONObject, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(JSONObject... options) {
            try {
                //Order order = razorpayClient.Orders.create(options[0]);
                Log.e("TAG", "DoPayment:: Execute:: ");
                options[0].put("name", SPData.getRazorPayScreenTitle());
                options[0].put("description", SPData.getRazorPayScreenSubtitle());
                //options[0].put("aid", aid);
                //options[0].put("sid", sid);
                Log.e("TAG", "doInBackground: "+orderId);
                options[0].put("order_id", orderId);
                //options[0].put("order_id", new JSONObject(order.toString()).getString("id"));
                //options[0].put("callback_url", ServerURL.SERVER_URL + "/api/payment/verify");
                Log.e("TAG", "doInBackground: "+options.toString());
                if(SPData.showServicesTab() && SPData.getProductsOrServices().equals("SERVICES")){
                    checkout.open((ProductDetailsActivity)context, options[0]);
                } else {
                    checkout.open((CommonActivity) context, options[0]);
                }
                CommonUtils.hideProgressDialog(customProgressDialog);
                return options[0];
            } catch (Exception e) {
                Log.e("TAG", "doInBackground: " + e.toString());
                e.printStackTrace();
            }
            return options[0];
        }

        @Override
        protected void onPostExecute(JSONObject options) {
            super.onPostExecute(options);
        }
    }


//    private void placeOrder(final String aid, final String sid) {
//        try {
//            final CustomProgressDialog customProgressDialog = CommonUtils.showProgressDialog(context,
//                    "Placing order ...");
//            ApiService apiService = ApiUtils.getHeaderAPIService(SPData.getAppPreferences().getUsertoken());
//            JsonObject jsonObject1 = new JsonObject();
//            jsonObject1.addProperty("address", aid);
//            if (!sid.isEmpty())
//                jsonObject1.addProperty("deliverySlot", sid);
//            apiService.PlaceOrder(jsonObject1).enqueue(new Callback<OrderDetailInfo>() {
//                @Override
//                public void onResponse(@NonNull Call<OrderDetailInfo> call, @NonNull retrofit2.Response<OrderDetailInfo> response) {
//                    CommonUtils.hideProgressDialog(customProgressDialog);
//                    if (response.isSuccessful()) {
//                        try {
//                            alertDialog.dismiss();
//                            SPData.getAppPreferences().setTotalCartCount(0);
//                            OrderDetailInfo placeOrder = response.body();
//                            String order_id = placeOrder.getData().getOrderId();
//                            String order_date = placeOrder.getData().getOrderDate();
//                            String[] datenew = order_date.split("T");
//                            order_date = datenew[0];
//                            String order_price = String.valueOf(placeOrder.getData().getAmount());
//                            List<Product> productsListnew = placeOrder.getData().getProducts();
//                            Intent i = new Intent(context, OrderHistoryDetailsActivity.class);
//                            i.putParcelableArrayListExtra("orderHistoryData", (ArrayList<? extends Parcelable>) productsListnew);
//                            i.putExtra("order_id", "# " + order_id);
//                            String date = order_date;
//                            final String[] date1 = date.split("T");
//                            i.putExtra("order_date", "" + date1[0]);
//                            i.putExtra("cart", "true");
//                            i.putExtra("order_amount", order_price);
//                            i.putExtra("order_address", placeOrder.getData().getAddress());
//                            i.putExtra("order_delivery_slot", placeOrder.getData().getDeliverySlot());
//                            context.startActivity(i);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                            Log.e(TAG, "onResponse: " + e);
//                        }
//                    } else {
//                        Log.e("TAG", "onResponse: " + response);
//                        Toast.makeText(context, "Something went wrong !!", Toast.LENGTH_SHORT).show();
//                    }
//
//                }
//
//                @Override
//                public void onFailure(@NonNull Call<OrderDetailInfo> call, @NonNull Throwable t) {
//                    CommonUtils.hideProgressDialog(customProgressDialog);
//                }
//            });
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    @SuppressLint("StaticFieldLeak")
    class DeleteAddress extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            OkHttpClient client = new OkHttpClient();
            try {
                Request request = new Request.Builder()
                        .url(strings[0])
                        .delete()
                        .addHeader("Content-Type", "application/json;charset=utf-8")
                        .addHeader("token", SPData.getAppPreferences().getUsertoken())
                        .build();
                Response response = client.newCall(request).execute();
                message = response.message();
                Log.e("TAG", "doInBackground: " + message);
            } catch (Exception e) {
                MDToast.makeText(context, "" + e, Toast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
                e.printStackTrace();
            }
            return message;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            CommonUtils.hideProgressDialog(customProgressDialog);
            if (s.equals("OK"))
                Toast.makeText(context, "Address Deleted", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(context, "Try Again", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getItemCount() {
        return userDataAddressList.size();
    }

}
