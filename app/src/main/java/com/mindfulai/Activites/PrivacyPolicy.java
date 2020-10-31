package com.mindfulai.Activites;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.mindfulai.NetworkRetrofit.ApiService;
import com.mindfulai.ministore.R;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PrivacyPolicy extends AppCompatActivity {
    private static final String TAG = "PrivacyPolicy";
    private Response response;
    private TextView textView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_privacy_policy);
            textView = findViewById(R.id.privacy_policy);
            if (getIntent().getStringExtra("type").equals(ApiService.RETURN_POLICY))
                Objects.requireNonNull(getSupportActionBar()).setTitle("Return Policy");
            else if(getIntent().getStringExtra("type").equals(ApiService.TNC))
                Objects.requireNonNull(getSupportActionBar()).setTitle("Terms and Condition");
            else
                Objects.requireNonNull(getSupportActionBar()).setTitle("Privacy Policy");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            progressBar = findViewById(R.id.progressBarPrivacy);
            new Privacy().execute(getIntent().getStringExtra("type"));
        } catch (Exception e) {
            Log.e(TAG, "onCreate: " + e);
        }
    }

    @SuppressLint("StaticFieldLeak")
    class Privacy extends AsyncTask<String, Void, Response> {

        @Override
        protected okhttp3.Response doInBackground(String... strings) {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(strings[0])
                    .get()
                    .addHeader("Content-Type", "application/json;charset=utf-8")
                    .build();
            try {
                response = client.newCall(request).execute();
            } catch (IOException e) {
                Log.e("TAG", "doInBackground: " + e);
            }
            return response;
        }

        @Override
        protected void onCancelled(okhttp3.Response response) {
            super.onCancelled(response);
            Log.e("TAG", "onCancelled: ");
        }

        @Override
        protected void onPostExecute(okhttp3.Response response) {
            super.onPostExecute(response);
            progressBar.setVisibility(View.GONE);
            String jsonData;
            try {
                if (response != null && response.body() != null) {
                    jsonData = response.body().string();
                    Log.e("TAG", "onPostExecute: "+jsonData);
                    JSONObject jsonObject = new JSONObject(jsonData);
                    int status = jsonObject.getInt("status");
                    if (status == 200) {
                        String para = jsonObject.getJSONObject("data").getString("paragraph");
                        textView.setText(para);
                    } else {
                        Log.e("TAG", "onPostExecute: " + jsonObject.getString("message"));
                    }
                }
            } catch (Exception e) {
                Log.e("TAG", "onPostExecute: " + e);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
