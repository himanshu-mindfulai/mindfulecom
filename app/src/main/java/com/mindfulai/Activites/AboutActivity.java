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

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AboutActivity extends AppCompatActivity {
    private TextView textView;
    private ProgressBar progressBar;
    private String para="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_about);
            getSupportActionBar().setTitle("About " + getResources().getString(R.string.app_name));
            progressBar = findViewById(R.id.progressBarAbout);
            textView = findViewById(R.id.about);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            new AboutUs().execute(ApiService.ABOUTUS);
        } catch (Exception e) {
            Log.e("TAG", "onCreate: " + e);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("StaticFieldLeak")
    class AboutUs extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            try {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(strings[0])
                        .get()
                        .addHeader("Content-Type", "application/json;charset=utf-8")
                        .build();
                Response response = client.newCall(request).execute();
                String jsonData;
                if (response.body() != null) {
                    jsonData = response.body().string();
                    JSONObject jsonObject = new JSONObject(jsonData);
                    int status = jsonObject.getInt("status");
                    if (status == 200) {
                        para = jsonObject.getJSONObject("data").getString("paragraph");
                    } else {
                        Log.e("TAG", "onPostExecute: " + jsonObject.getString("message"));
                    }
                }
            } catch (Exception e) {
                Log.e("TAG", "doInBackground: " + e);
            }
            return para;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            try {
                progressBar.setVisibility(View.GONE);
                textView.setText(para);
            } catch (Exception e) {
                Log.e("TAG", "onPostExecute: " + e);
            }
        }
    }
}