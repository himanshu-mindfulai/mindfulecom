package com.developndesign.salonvendor.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.icu.util.LocaleData;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.developndesign.salonvendor.R;
import com.developndesign.salonvendor.adapter.BillingAdapter;
import com.developndesign.salonvendor.adapter.BookingServiceAdapter;
import com.developndesign.salonvendor.customclass.LocalData;
import com.developndesign.salonvendor.customclass.MongoDB;
import com.developndesign.salonvendor.model.BookingBaseModel;
import com.developndesign.salonvendor.model.BookingDataModel;
import com.developndesign.salonvendor.model.billing.BillingBaseModel;
import com.developndesign.salonvendor.model.billing.BillingDataModel;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.razorpay.PaymentData;
import com.razorpay.PaymentResultWithDataListener;
import com.valdesekamdem.library.mdtoast.MDToast;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class BillingActivity extends AppCompatActivity implements PaymentResultWithDataListener {

    private ProgressBar progressBar;
    private LocalData localData;
    private ArrayList<BillingDataModel> billingDataModelArrayList;
    private RecyclerView recyclerView;
    public  int position;
    private ProgressDialog progressDialog;
    private BillingAdapter billingAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_billing);
        recyclerView = findViewById(R.id.recyclerview_billings);
        progressBar = findViewById(R.id.progress_bar);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.ic_baseline_arrow_back_24));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        localData = new LocalData(this);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        new GetBilling().execute(MongoDB.BILLS_BYSALON+localData.getSalonId());
    }
    @Override
    public void onPaymentSuccess(String s, PaymentData paymentData) {
        Toast.makeText(this, "Payment Success", Toast.LENGTH_SHORT).show();
        verifyPayment(paymentData);
    }
    private void verifyPayment(PaymentData paymentData) {
        try {
            JsonObject jsonObject = new JsonObject();
            Log.e("TAG", "verifyPayment: " + paymentData.getPaymentId());
            Log.e("TAG", "verifyPayment: " + paymentData.getOrderId());
            Log.e("TAG", "verifyPayment: "+paymentData.getSignature());
            jsonObject.addProperty("paymentId", paymentData.getPaymentId());
            jsonObject.addProperty("orderId", paymentData.getOrderId());
            jsonObject.addProperty("signature", paymentData.getSignature());
            progressDialog = new ProgressDialog(BillingActivity.this);
            progressDialog.setMessage("Please wait...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
            new VerifyPayment().execute(MongoDB.VERIFY_PAYMENT,paymentData.getPaymentId(),paymentData.getOrderId(),paymentData.getSignature());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("StaticFieldLeak")
    class VerifyPayment extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            OkHttpClient client = new OkHttpClient();
            MediaType mediaType = MediaType.parse("application/json;charset=utf-8");
            JSONObject params = new JSONObject();
            String jsonData="";
            try {
                params.put("paymentId",strings[1]);
                params.put("orderId",strings[2]);
                params.put("signature",strings[3]);
                RequestBody body = RequestBody.create(mediaType, params.toString());
                Request request = new Request.Builder()
                        .url(strings[0])
                        .post(body)
                        .addHeader("Content-Type", "application/json;charset=utf-8")
                        .addHeader("token", localData.getToken())
                        .build();
                Response response = client.newCall(request).execute();
                jsonData = response.body().string();
                Log.e("TAG", "doInBackground: on payment sucss "+jsonData);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return jsonData;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            progressDialog.cancel();
            try {
                if(response!=null){
                JSONObject jsonObject = new JSONObject(response);
                boolean errors = jsonObject.getBoolean("errors");
                if(!errors){
                    MDToast.makeText(BillingActivity.this, "" + jsonObject.getString("message"), Toast.LENGTH_SHORT,MDToast.TYPE_SUCCESS).show();
                    billingDataModelArrayList.get(position).setStatus("Paid");
                    billingAdapter.notifyItemChanged(position);
                }else{
                    MDToast.makeText(BillingActivity.this, "" + jsonObject.getString("message"), Toast.LENGTH_SHORT,MDToast.TYPE_ERROR).show();
                }
                }else{
                    Toast.makeText(BillingActivity.this, "Could not connect to "+MongoDB.SERVER_URL, Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(BillingActivity.this, ""+e, Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public void onPaymentError(int i, String s, PaymentData paymentData) {

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }
    @SuppressLint("StaticFieldLeak")
    class GetBilling extends AsyncTask<String, Void, ArrayList<BillingDataModel>> {
        @Override
        protected ArrayList<BillingDataModel> doInBackground(String... strings) {
            try {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(strings[0])
                        .get()
                        .addHeader("token", localData.getToken())
                        .build();
                Response response = client.newCall(request).execute();
                assert response.body() != null;
                String jsondata = response.body().string();
                billingDataModelArrayList = new Gson().fromJson(jsondata, BillingBaseModel.class).getData();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return billingDataModelArrayList;
        }

        @Override
        protected void onPostExecute(ArrayList<BillingDataModel> response) {
            super.onPostExecute(response);
            try {
                progressBar.setVisibility(View.GONE);
                if (response != null) {
                    billingAdapter = new BillingAdapter(BillingActivity.this, response);
                    recyclerView.setAdapter(billingAdapter);
                    billingAdapter.notifyDataSetChanged();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}