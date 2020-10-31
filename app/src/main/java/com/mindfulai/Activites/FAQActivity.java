package com.mindfulai.Activites;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.mindfulai.NetworkRetrofit.ApiService;
import com.mindfulai.ministore.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FAQActivity extends AppCompatActivity {
    private static final String TAG = "FAQActivity";
    private TextView textView;
    private ProgressBar progressBar;
    private JSONArray jsonArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_faq);
            Objects.requireNonNull(getSupportActionBar()).setTitle("FAQ");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            progressBar = findViewById(R.id.progressBarFAQ);
            textView = findViewById(R.id.faq);
            new AboutUs().execute(ApiService.FAQ);
        } catch (Exception e) {
            Log.e(TAG, "onCreate: " + e);
        }
    }

    @SuppressLint("StaticFieldLeak")
    class AboutUs extends AsyncTask<String, Void, JSONArray> {

        @Override
        protected JSONArray doInBackground(String... strings) {
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
                        jsonArray = jsonObject.getJSONArray("data");
                    } else {
                        Log.e("TAG", "onPostExecute: " + jsonObject.getString("message"));
                    }
                }
            } catch (Exception e) {
                Log.e("TAG", "doInBackground: " + e);
            }
            return jsonArray;
        }

        @Override
        protected void onCancelled(JSONArray response) {
            super.onCancelled(response);
            Log.e("TAG", "onCancelled: ");
        }

        @Override
        protected void onPostExecute(JSONArray response) {
            super.onPostExecute(response);
            try {
                progressBar.setVisibility(View.GONE);
                if (response != null && response.length() >= 1) {
                    textView.setText("");
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            textView.append(Html.fromHtml("<font><b>Q" + (i + 1) + ". " + response.getJSONObject(i).getString("question") +"</b></font>"));
                            textView.append("\n"+response.getJSONObject(i).getString("answer") + "\n\n\n");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "onPostExecute: " + e);
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
