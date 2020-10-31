package com.mindfulai.Activites;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mindfulai.Adapter.OrderHistoryDetailsAdapter;
import com.mindfulai.Adapter.OrderHistoryDetailsCartAdapter;
import com.mindfulai.Models.AllOrderHistory.DatumModel;
import com.mindfulai.Models.AllOrderHistory.Product;
import com.mindfulai.Models.UserDataAddress;
import com.mindfulai.NetworkRetrofit.ApiService;
import com.mindfulai.NetworkRetrofit.ApiUtils;
import com.mindfulai.NetworkRetrofit.ServerURL;
import com.mindfulai.Utils.CommonUtils;
import com.mindfulai.Utils.SPData;
import com.mindfulai.ministore.R;
import com.valdesekamdem.library.mdtoast.MDToast;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderHistoryDetailsActivity extends AppCompatActivity {

    private static final String TAG = "OrderHistoryDetails";
    List<Product> productsList = new ArrayList<>();
    List<com.mindfulai.Models.orderDetailInfo.Product> products = new ArrayList<>();
    private String id, orderId, orderDate, orderSlot, orderAmount, payment_method, order_delivery_charge, amount_paid = "";
    public String orderStatus;
    UserDataAddress userDataAddress;
    OrderHistoryDetailsAdapter orderHistoryDetailsAdapter;
    OrderHistoryDetailsCartAdapter orderHistoryDetailsCartAdapter;
    FloatingActionButton fab;
    private FrameLayout flfabSubmenu;
    private String orderType;
    private boolean fabExpanded = false;
    private float paidFromWallet;
    private Button cancelOrder;
    private float carryBagCharge;
    private JSONObject jsonObject;
    private ProgressDialog progressDialog;
    private TextView return_product;
    private TextView replace_product;
    private TextView txtOrderStatus;
    private TextView order_slot;
    private RecyclerView products_recyclerView;
    private String strcouponDiscount = "";


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history_details);
        TextView order_id = findViewById(R.id.order_id);
        TextView order_subtotal = findViewById(R.id.subtotal_amount);
        TextView order_amount = findViewById(R.id.total_amount);
        TextView order_addres = findViewById(R.id.order_address);
        TextView order_payment_mehod = findViewById(R.id.order_payment_1);
        TextView order_total_paid = findViewById(R.id.total_paid);
        TextView order_wallet = findViewById(R.id.wallet_amount);
        TextView couponDiscount = findViewById(R.id.coupon_discount);
        order_slot = findViewById(R.id.delivery_slot);
        TextView order_dc = findViewById(R.id.dv_charge);
        TextView placedOn = findViewById(R.id.placed_on);
        txtOrderStatus = findViewById(R.id.order_status);
        TextView carrybagChargeTxt = findViewById(R.id.carybag_charge);
        products_recyclerView = findViewById(R.id.products_recyclerView);
        return_product = findViewById(R.id.return_product);
        replace_product = findViewById(R.id.replace_product);
        cancelOrder = findViewById(R.id.cancel_order);

        if (getIntent() != null) {
            id = getIntent().getStringExtra("order_id_");
            orderId = getIntent().getStringExtra("order_id");
            orderDate = getIntent().getStringExtra("order_date");
            orderAmount = getIntent().getStringExtra("order_amount");
            orderStatus = getIntent().getStringExtra("order_status");
            orderSlot = getIntent().getStringExtra("order_delivery_slot");
            userDataAddress = getIntent().getExtras().getParcelable("order_address");
            payment_method = getIntent().getStringExtra("order_payment_method");
            orderType = getIntent().getStringExtra("order_type");
            if (orderType == null) {
                orderType = "delivery";
            }
            carryBagCharge = getIntent().getFloatExtra("order_carrybag_charge", 0);
            paidFromWallet = getIntent().getFloatExtra("order_paid_from_wallet", 0);
            order_delivery_charge = "" + getIntent().getStringExtra("order_delivery_charge");
            amount_paid = getIntent().getStringExtra("amount_paid");
            if (getIntent().getStringExtra("coupon_discount") != null)
                strcouponDiscount = getIntent().getStringExtra("coupon_discount");
        }

        if (getIntent().getStringExtra("cart") != null && getIntent().getStringExtra("cart").equals("true")) {
            findViewById(R.id.back_to_home).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                    startActivity(new Intent(
                            OrderHistoryDetailsActivity.this,
                            MainActivity.class
                    ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                }
            });
            getSupportActionBar().hide();
            return_product.setVisibility(View.GONE);
            replace_product.setVisibility(View.GONE);
        } else {
            findViewById(R.id.back_to_home).setVisibility(View.GONE);
            findViewById(R.id.order_success_anim).setVisibility(View.GONE);
            findViewById(R.id.order_success_text).setVisibility(View.GONE);
        }
        try {
            if (showButton()) {
                return_product.setVisibility(View.VISIBLE);
                replace_product.setVisibility(View.VISIBLE);
                cancelOrder.setVisibility(View.VISIBLE);
                order_slot.setVisibility(View.VISIBLE);
            } else {
                return_product.setVisibility(View.GONE);
                replace_product.setVisibility(View.GONE);
                cancelOrder.setVisibility(View.GONE);
                order_slot.setVisibility(View.GONE);
            }
            if (orderStatus.equals("Cancelled")) {
                handleCancelled();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        cancelOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog = new ProgressDialog(OrderHistoryDetailsActivity.this);
                progressDialog.setMessage("Please wait...");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();
                new CancelBooking().execute(ServerURL.SERVER_URL + "/api/corder/status/" + id);
            }
        });
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        txtOrderStatus.setText(orderStatus);

        fab = findViewById(R.id.floatingActionButton);
        flfabSubmenu = findViewById(R.id.fab_submenu);
        FloatingActionButton fabCall = findViewById(R.id.fab_call);
        FloatingActionButton fabWhatsapp = findViewById(R.id.fab_whatsapp);
        hideFabSubmenu();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fabExpanded) {
                    hideFabSubmenu();
                } else {
                    showFabSubmenu();
                }
            }
        });
        fabCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToPhone();
            }
        });
        fabWhatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToWhatsApp();
            }
        });
        products_recyclerView.setHasFixedSize(true);
        products_recyclerView.setLayoutManager(new LinearLayoutManager(this));
        products_recyclerView.setItemAnimator(new DefaultItemAnimator());
        if (getIntent().getStringExtra("cart") != null && getIntent().getStringExtra("cart").equals("true")) {
            products = getIntent().getParcelableArrayListExtra("orderHistoryData");
            orderHistoryDetailsCartAdapter = new OrderHistoryDetailsCartAdapter(OrderHistoryDetailsActivity.this, products);
            products_recyclerView.setAdapter(orderHistoryDetailsCartAdapter);
        } else {
            productsList = getIntent().getParcelableArrayListExtra("orderHistoryData");
            return_product.setVisibility(View.GONE);
            replace_product.setVisibility(View.GONE);
            for (Product p : productsList) {
                if (p.getStatus() == null || p.getStatus().isEmpty()) {
                    if (showButton()) {
                        return_product.setVisibility(View.VISIBLE);
                        replace_product.setVisibility(View.VISIBLE);
                    }
                    break;
                }
            }
            if (!SPData.showReturnReplace()) {
                return_product.setVisibility(View.GONE);
                replace_product.setVisibility(View.GONE);
            }
            orderHistoryDetailsAdapter = new OrderHistoryDetailsAdapter(OrderHistoryDetailsActivity.this, productsList, "no_action", id);
            products_recyclerView.setAdapter(orderHistoryDetailsAdapter);

            Intent i = new Intent(this, ReturnOrderHistoryActivity.class);
            i.putExtra("id", id);
            i.putParcelableArrayListExtra("products_List", (ArrayList<? extends Parcelable>) productsList);
            return_product.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    i.putExtra("return_action", "return");
                    startActivity(i);
                }
            });
            replace_product.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    i.putExtra("return_action", "replace");
                    startActivity(i);
                }
            });

        }
        order_id.setText(orderId);
        order_amount.setText(getResources().getString(R.string.rs)+orderAmount);
        placedOn.setText(orderDate);
        carrybagChargeTxt.setText(getString(R.string.rs) + carryBagCharge);

        if (!order_delivery_charge.equals("null"))
            order_dc.setText(getString(R.string.rs) + order_delivery_charge);
        else
            order_dc.setText(getString(R.string.rs) + " 0.0");
        if(strcouponDiscount.isEmpty()){
            couponDiscount.setText(getString(R.string.rs)+"0.0");
        }else
            couponDiscount.setText(getResources().getString(R.string.rs)+strcouponDiscount);

        order_wallet.setText(getResources().getString(R.string.rs) + paidFromWallet);
        order_total_paid.setText(amount_paid);
        double subtotal = 0.0f,ordercouponDiscount = 0.0,orderDeliveryCharge=0.0;
         if(!order_delivery_charge.isEmpty())
         orderDeliveryCharge = Double.parseDouble(order_delivery_charge);
        double orderTotalAmt = Double.parseDouble(orderAmount);
        if(!strcouponDiscount.isEmpty())
        ordercouponDiscount = Double.parseDouble(strcouponDiscount);
        subtotal = orderTotalAmt - ((carryBagCharge+orderDeliveryCharge) - ordercouponDiscount);
        order_subtotal.setText(getString(R.string.rs) + new DecimalFormat("#.##").format(subtotal));

        if (paidFromWallet == 0) {
            if (payment_method != null && !payment_method.equals("null"))
                order_payment_mehod.setText(payment_method);
            else
                order_payment_mehod.setText("");
        } else {
            order_payment_mehod.setText(payment_method + "+ Wallet");
        }
        if (orderType.equals("delivery")) {
            if (!orderStatus.equals("cancelled")) {
                try {
                    order_slot.setText("Your order will be delivered on " + orderSlot.split(",")[0] + " between " + orderSlot.split(",")[1]);
                } catch (Exception e) {
                    order_slot.setText("Your order will be delivered between " + orderSlot);
                }
            }
            try {
                if (userDataAddress.getName() != null && userDataAddress.getMobile_number() != null)
                    order_addres.setText(
                            CommonUtils.capitalizeWord(userDataAddress.getName()) + "\n" +
                                    CommonUtils.capitalizeWord(userDataAddress.getAddressLine1()) + "\n" +
                                    CommonUtils.capitalizeWord(userDataAddress.getAddressLine2()) + ", " +
                                    CommonUtils.capitalizeWord(userDataAddress.getCity()) + "\n" +
                                    CommonUtils.capitalizeWord(userDataAddress.getState()) + ", " +
                                    userDataAddress.getPincode() + "\n" + userDataAddress.getMobile_number());
                else
                    order_addres.setText(
                            CommonUtils.capitalizeWord(userDataAddress.getAddressLine1()) + "\n" +
                                    CommonUtils.capitalizeWord(userDataAddress.getAddressLine2()) + ", " +
                                    CommonUtils.capitalizeWord(userDataAddress.getCity()) + "\n" +
                                    CommonUtils.capitalizeWord(userDataAddress.getState()) + ", " +
                                    CommonUtils.capitalizeWord(userDataAddress.getPincode()));
            } catch (Exception e) {
                Log.i("TAAAG", "@@@@@@@@@@@@: " + e.getStackTrace());
            }
        } else if (orderType.equals("pickup")) {
            order_slot.setText("Pickup Order");
            order_addres.setText("PICKUP ORDER");
        }

    }

    private boolean showButton() {
        return !orderStatus.equals("Delivered") && !orderStatus.equals("Cancelled");
    }


    @SuppressLint("StaticFieldLeak")
    class CancelBooking extends AsyncTask<String, Void, DatumModel> {
        DatumModel datumModel;

        @Override
        protected DatumModel doInBackground(String... strings) {
            try {
                MediaType mediaType = MediaType.parse("application/json;charset=utf-8");
                JSONObject params = new JSONObject();
                params.put("status", "Cancelled");
                RequestBody body = RequestBody.create(mediaType, params.toString());
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(strings[0])
                        .put(body)
                        .addHeader("Content-Type", "application/json;charset=utf-8")
                        .addHeader("token", SPData.getAppPreferences().getUsertoken())
                        .build();
                okhttp3.Response response = client.newCall(request).execute();
                assert response.body() != null;
                String jsondata = response.body().string();
                jsonObject = new JSONObject(jsondata);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("TAG", "doInBackground: " + e);
            }
            return datumModel;
        }

        @Override
        protected void onPostExecute(DatumModel bookingDataModel) {
            super.onPostExecute(bookingDataModel);
            try {
                progressDialog.cancel();
                if (!jsonObject.getBoolean("errors")) {
                    handleCancelled();
                    setResult(RESULT_OK);
                    MDToast.makeText(OrderHistoryDetailsActivity.this, "Order Cancelled Successfully", Toast.LENGTH_SHORT, MDToast.TYPE_SUCCESS).show();
                    orderStatus = "Cancelled";
                    products_recyclerView.setAdapter(null);
                    orderHistoryDetailsAdapter = new OrderHistoryDetailsAdapter(OrderHistoryDetailsActivity.this, productsList, "no_action", id);
                    products_recyclerView.setAdapter(orderHistoryDetailsAdapter);
                } else {
                    Toast.makeText(OrderHistoryDetailsActivity.this, "" + jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("TAG", "onPostExecute: " + e);
            }
        }
    }


    private void handleCancelled() {
        return_product.setVisibility(View.GONE);
        replace_product.setVisibility(View.GONE);
        txtOrderStatus.setText("Cancelled");
        order_slot.setVisibility(View.GONE);
        cancelOrder.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        if( orderStatus.equals("Cancelled"))
        setResult(RESULT_OK,getIntent());
        finish();
    }

    private void hideFabSubmenu() {
        flfabSubmenu.setVisibility(View.GONE);
        fab.setImageResource(R.drawable.ic_baseline_contact_support_30);
        fabExpanded = false;
    }

    private void showFabSubmenu() {
        flfabSubmenu.setVisibility(View.VISIBLE);
        fab.setImageResource(R.drawable.ic_baseline_close_24);
        fabExpanded = true;
    }

    private void sendToWhatsApp() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://api.whatsapp.com/send?phone=" + SPData.whatsAppNumber() + "&text=&source=&data="));
        startActivity(intent);
    }

    private void sendToPhone() {
        startActivity(
                new Intent(
                        Intent.ACTION_DIAL,
                        Uri.parse("tel:" + SPData.supportCallNumber())
                )
        );
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
