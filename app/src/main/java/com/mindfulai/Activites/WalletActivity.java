package com.mindfulai.Activites;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.mindfulai.Models.CustomerInfo.CustomerData;
import com.mindfulai.Models.WalletRechargeModel.WalletRechargeModel;
import com.mindfulai.Models.orderDetailInfo.OrderDetailInfo;
import com.mindfulai.NetworkRetrofit.ApiService;
import com.mindfulai.NetworkRetrofit.ApiUtils;
import com.mindfulai.Utils.CommonUtils;
import com.mindfulai.Utils.CustomProgressDialog;
import com.mindfulai.Utils.SPData;
import com.mindfulai.ministore.R;
import com.razorpay.Checkout;
import com.razorpay.PaymentData;
import com.razorpay.PaymentResultWithDataListener;
import com.razorpay.RazorpayClient;
import com.valdesekamdem.library.mdtoast.MDToast;

import org.json.JSONObject;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WalletActivity
        extends AppCompatActivity
        implements TextWatcher, View.OnClickListener, PaymentResultWithDataListener {

    TextView tvBalance, tvSuggestedAmount1, tvSuggestedAmount2, tvSuggestedAmount3, tvSuggestedAmount4;;
    EditText etEntereedAmount;
    CardView cvSuggestedAmount1, cvSuggestedAmount2, cvSuggestedAmount3, cvSuggestedAmount4;
    Button btnPay;
    long SA1, SA2, SA3, SA4;
    private String orderID;
    Checkout checkout;
    RazorpayClient razorpayClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            if(SPData.getAppPreferences().getUsertoken().isEmpty()){
                startActivity(new Intent(WalletActivity.this, LoginActivity.class));
            }
            setContentView(R.layout.activity_wallet);
            Objects.requireNonNull(getSupportActionBar()).setTitle("Wallet");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            tvBalance = findViewById(R.id.tv_wallet_balance);
            etEntereedAmount = findViewById(R.id.et_enter_amount);
            cvSuggestedAmount1 = findViewById(R.id.cv_suggested_amount_1);
            cvSuggestedAmount2 = findViewById(R.id.cv_suggested_amount_2);
            cvSuggestedAmount3 = findViewById(R.id.cv_suggested_amount_3);
            cvSuggestedAmount4 = findViewById(R.id.cv_suggested_amount_4);
            tvSuggestedAmount1 = findViewById(R.id.tv_suggested_amount_1);
            tvSuggestedAmount2 = findViewById(R.id.tv_suggested_amount_2);
            tvSuggestedAmount3 = findViewById(R.id.tv_suggested_amount_3);
            tvSuggestedAmount4 = findViewById(R.id.tv_suggested_amount_4);
            btnPay = findViewById(R.id.btn_pay);
            etEntereedAmount.addTextChangedListener(this);
            btnPay.setOnClickListener(this);
            cvSuggestedAmount1.setOnClickListener(this);
            cvSuggestedAmount2.setOnClickListener(this);
            cvSuggestedAmount3.setOnClickListener(this);
            cvSuggestedAmount4.setOnClickListener(this);
            getWalletBalance();

        } catch (Exception e){
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void getWalletBalance() {
        final CustomProgressDialog customProgressDialog = CommonUtils.showProgressDialog(WalletActivity.this,
                "Getting latest wallet...");
        ApiService apiService = ApiUtils.getHeaderAPIService(SPData.getAppPreferences().getUsertoken());
        apiService.getProfileDetails().enqueue(new Callback<CustomerData>() {
            @Override
            public void onResponse(Call<CustomerData> call, Response<CustomerData> response) {
                if (response.isSuccessful() && response.body() != null) {
                    CustomerData customerDataResponse = response.body();
                    tvBalance.setText("\u20B9" + customerDataResponse.getData().getUser().getWallet());
                    CommonUtils.hideProgressDialog(customProgressDialog);
                }
            }

            @Override
            public void onFailure(Call<CustomerData> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if(s.toString().isEmpty()){
            SA1 = 10;
            SA2 = 50;
            SA3 = 100;
            SA4 = 500;
        } else {
            long enteredAmount = Long.parseLong(s.toString());
            if(enteredAmount <= 1000) {
                SA1 = enteredAmount * 10;
                SA2 = enteredAmount * 20;
                SA3 = enteredAmount * 50;
                SA4 = enteredAmount * 75;
            } else {
                SA1 = enteredAmount + 100;
                SA2 = enteredAmount + 200;
                SA3 = enteredAmount + 500;
                SA4 = enteredAmount + 1000;
            }
        }

        tvSuggestedAmount1.setText("\u20B9 " + SA1);
        tvSuggestedAmount2.setText("\u20B9 " + SA2);
        tvSuggestedAmount3.setText("\u20B9 " + SA3);
        tvSuggestedAmount4.setText("\u20B9 " + SA4);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_pay: {
                if (etEntereedAmount.getText().toString().isEmpty()){
                    MDToast.makeText(WalletActivity.this, "Enter an amount", MDToast.TYPE_INFO).show();
                    return;
                }
                checkout = new Checkout();
                checkout.setImage(R.mipmap.ic_launcher);
                checkout.setKeyID("rzp_test_8ZUQyQ5x2EiFM4");
                JSONObject options = new JSONObject();
                try {
                    razorpayClient = new RazorpayClient("rzp_test_8ZUQyQ5x2EiFM4", "okcGFzgWBwvbeQVDSoeK25wh");
                    options.put("currency", "INR");
                    options.put("receipt", "");
                    options.put("payment_capture", true);
                    generateOrderId(options);
                } catch (Exception e){
                    Log.e("WalletActivity", e.getMessage());
                }finally {
                    break;
                }
            }
            case R.id.cv_suggested_amount_1: {
                etEntereedAmount.setText(tvSuggestedAmount1.getText().toString().split(" ")[1]);
                break;
            }
            case R.id.cv_suggested_amount_2: {
                etEntereedAmount.setText(tvSuggestedAmount2.getText().toString().split(" ")[1]);
                break;
            }
            case R.id.cv_suggested_amount_3: {
                etEntereedAmount.setText(tvSuggestedAmount3.getText().toString().split(" ")[1]);
                break;
            }
            case R.id.cv_suggested_amount_4: {
                etEntereedAmount.setText(tvSuggestedAmount4.getText().toString().split(" ")[1]);
                break;
            }
        }
    }

    private void generateOrderId(JSONObject options) {
        final CustomProgressDialog customProgressDialog = CommonUtils.showProgressDialog(WalletActivity.this,
                "Generating order...");
        ApiService apiService = ApiUtils.getHeaderAPIService(SPData.getAppPreferences().getUsertoken());
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("amount", etEntereedAmount.getText().toString());
        apiService.walletRecharge(jsonObject).enqueue(new Callback<WalletRechargeModel>() {
            @Override
            public void onResponse(Call<WalletRechargeModel> call, Response<WalletRechargeModel> response) {
                if (response.isSuccessful()){
                    WalletRechargeModel responseModel = response.body();
                    orderID = responseModel.getData().getOrderId();
                    Log.e("TAG", "onResponse: "+orderID );
                    CommonUtils.hideProgressDialog(customProgressDialog);
                    new DoPayment().execute(options);
                }
            }

            @Override
            public void onFailure(Call<WalletRechargeModel> call, Throwable t) {
                CommonUtils.hideProgressDialog(customProgressDialog);
            }
        });
    }

    @Override
    public void onPaymentSuccess(String s, PaymentData paymentData) {
        verifyPayment(paymentData);
    }

    private void verifyPayment(PaymentData paymentData) {
        final CustomProgressDialog customProgressDialog = CommonUtils.showProgressDialog(WalletActivity.this,
                "Verifying payment..");
        ApiService apiService = ApiUtils.getHeaderAPIService(SPData.getAppPreferences().getUsertoken());
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("paymentId", paymentData.getPaymentId());
        jsonObject.addProperty("orderId", paymentData.getOrderId());
        jsonObject.addProperty("signature", paymentData.getSignature());
        apiService.verifyOnlinePayment(jsonObject).enqueue(new Callback<OrderDetailInfo>() {
            @Override
            public void onResponse(Call<OrderDetailInfo> call, Response<OrderDetailInfo> response) {
                CommonUtils.hideProgressDialog(customProgressDialog);
                if(response.isSuccessful()){
                    etEntereedAmount.setText("");
                    getWalletBalance();
                } else {
                    MDToast.makeText(WalletActivity.this, "Payment failed!", MDToast.TYPE_ERROR).show();
                }
            }

            @Override
            public void onFailure(Call<OrderDetailInfo> call, Throwable t) {
                CommonUtils.hideProgressDialog(customProgressDialog);
            }
        });
    }

    @Override
    public void onPaymentError(int i, String s, PaymentData paymentData) {
        MDToast.makeText(this, "Payment failed!", MDToast.TYPE_ERROR).show();
    }

    @SuppressLint("StaticFieldLeak")
    class DoPayment extends AsyncTask<JSONObject, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(JSONObject... options) {
            try {
                //Order order = razorpayClient.Orders.create(options[0]);
                Log.i("WalletActivity", "DoPayment:: Execute:: ");
                options[0].put("name", "Multi E-Com");
                options[0].put("description", "Order Payment");
                options[0].put("order_id", orderID);
                //options[0].put("order_id", new JSONObject(order.toString()).getString("id"));
                //options[0].put("callback_url", ServerURL.SERVER_URL + "/api/payment/verify");
                checkout.open(WalletActivity.this, options[0]);
                return options[0];
            } catch (Exception e) {
                Log.e("ViewTask", "doInBackground: " + e.toString());
                e.printStackTrace();
            }
            return options[0];
        }

        @Override
        protected void onPostExecute(JSONObject options) {
            super.onPostExecute(options);
        }
    }
}