package com.developndesign.salonvendor.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.icu.util.LocaleData;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.developndesign.salonvendor.R;
import com.developndesign.salonvendor.activity.BillingActivity;
import com.developndesign.salonvendor.activity.BookingDescriptionActivity;
import com.developndesign.salonvendor.activity.MainActivity;
import com.developndesign.salonvendor.activity.SigninActivity;
import com.developndesign.salonvendor.customclass.CommonUtils;
import com.developndesign.salonvendor.customclass.LocalData;
import com.developndesign.salonvendor.customclass.MongoDB;
import com.developndesign.salonvendor.model.BookingDataModel;
import com.developndesign.salonvendor.model.billing.BillingDataModel;
import com.razorpay.Checkout;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.valdesekamdem.library.mdtoast.MDToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.developndesign.salonvendor.customclass.CommonUtils.gmttoLocalDate;

public class BillingAdapter extends RecyclerView.Adapter<BillingAdapter.ViewHolder> {
    private Context context;
    private ArrayList<BillingDataModel> bookingDataModelList;
    private Checkout checkout;
    private RazorpayClient razorpayClient;
    private LocalData localData;
    private ProgressDialog progressDialog;
    private JSONObject options;
    private String orderId;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.billing_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        try {
            holder.amount.setText("Amount - " + context.getString(R.string.rs) + bookingDataModelList.get(position).getAmount());
            holder.commission.setText("Commission - " + context.getString(R.string.rs) + bookingDataModelList.get(position).getCommission());
            holder.statusBilling.setText(bookingDataModelList.get(position).getStatus());
            SimpleDateFormat simpleDateFormatTo = new SimpleDateFormat("dd-MMM-YYYY:hh:mm a");
            SimpleDateFormat iso = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            holder.from.setText("From: " + simpleDateFormatTo.format(gmttoLocalDate(iso.parse(bookingDataModelList.get(position).getFrom()))));
            holder.to.setText("To: " + simpleDateFormatTo.format(gmttoLocalDate(iso.parse(bookingDataModelList.get(position).getTo()))));
            if (!bookingDataModelList.get(position).getStatus().contains("Pending")) {
                holder.pay.setVisibility(View.GONE);
            } else {
                holder.pay.setVisibility(View.VISIBLE);
            }
            holder.pay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    progressDialog.setMessage("Please wait...");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();
                    ((BillingActivity)context).position = position;
                    takePayment(bookingDataModelList.get(position).get_id());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "" + e, Toast.LENGTH_SHORT).show();
            Log.e("TAG", "onBindViewHolder: booking " + e);
        }
    }

    private void takePayment(String billId) {
        checkout = new Checkout();
        checkout.setImage(R.mipmap.ic_launcher);
        checkout.setKeyID(context.getString(R.string.razorpay_key));
        options = new JSONObject();
        try {
            razorpayClient = new RazorpayClient(context.getString(R.string.razorpay_key), context.getString(R.string.razorpay_secret));
            options.put("currency", "INR");
            options.put("receipt", "");
            options.put("payment_capture", true);
            new PAYBILL().execute(MongoDB.PAY_BILL + billId);
        } catch (Exception e) {
            Log.e("TAG", "takePayment: " + e);
        }
    }

    @SuppressLint("StaticFieldLeak")
    class PAYBILL extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            OkHttpClient client = new OkHttpClient();
            MediaType mediaType = MediaType.parse("application/json;charset=utf-8");
            JSONObject params = new JSONObject();
            String jsonData = "";
            try {
                RequestBody body = RequestBody.create(mediaType, "");
                Request request = new Request.Builder()
                        .url(strings[0])
                        .post(body)
                        .addHeader("Content-Type", "application/json;charset=utf-8")
                        .addHeader("token", localData.getToken())
                        .build();
                Response response = client.newCall(request).execute();
                jsonData = response.body().string();
                Log.e("TAG", "doInBackground: "+jsonData);
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
                if (response != null) {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean errors = jsonObject.getBoolean("errors");
                    if (errors) {
                        MDToast.makeText(context, "" + jsonObject.getString("message"), Toast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
                    } else {
                        orderId = jsonObject.getJSONObject("data").getString("order_id");
                        new DoPayment().execute(options);
                    }
                } else {
                    Toast.makeText(context, "Could not connect to " + MongoDB.SERVER_URL, Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(context, "" + e, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    class DoPayment extends AsyncTask<JSONObject, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(JSONObject... options) {
            try {
                options[0].put("name", context.getString(R.string.razorpay_title));
                options[0].put("description", context.getString(R.string.razorpay_description));
                options[0].put("order_id", orderId);
                Log.e("TAG", "doInBackground: " + options.toString());
                checkout.open((BillingActivity) context, options[0]);
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

    @Override
    public int getItemCount() {
        if (bookingDataModelList != null)
            return bookingDataModelList.size();
        else
            return 0;
    }

    public BillingAdapter(Context context, ArrayList<BillingDataModel> response) {
        this.context = context;
        this.bookingDataModelList = response;
        localData = new LocalData(context);
        progressDialog = new ProgressDialog(context);
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView commission;
        private TextView amount;
        private TextView from;
        private TextView to;
        private TextView pay;
        private TextView statusBilling;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            commission = itemView.findViewById(R.id.commission);
            amount = itemView.findViewById(R.id.amount);
            from = itemView.findViewById(R.id.from);
            to = itemView.findViewById(R.id.to);
            statusBilling = itemView.findViewById(R.id.status_billing);
            pay = itemView.findViewById(R.id.pay);
        }
    }
}
