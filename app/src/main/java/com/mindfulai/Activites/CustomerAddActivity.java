package com.mindfulai.Activites;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.mindfulai.NetworkRetrofit.ApiService;
import com.mindfulai.Utils.SPData;
import com.mindfulai.ministore.R;

import org.json.JSONObject;

import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CustomerAddActivity extends AppCompatActivity {
    private EditText name;
    private EditText mobileNumber;
    private String message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_add);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Add Customer");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        name = findViewById(R.id.edit_user_name);
        mobileNumber = findViewById(R.id.edit_mobile_number);
        Button button = findViewById(R.id.edit_button_done);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nameUser = name.getText().toString();
                String mobileUser = mobileNumber.getText().toString();
                if (!nameUser.replaceAll(" ", "").isEmpty() && !mobileUser.replaceAll(" ", "").isEmpty()) {
                    new AddCustomer().execute(ApiService.ADD_CUSTOMER, mobileUser, nameUser);
                }
            }
        });

    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("StaticFieldLeak")
    class AddCustomer extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            try {
                OkHttpClient client = new OkHttpClient();
                MediaType mediaType = MediaType.parse("application/json;charset=utf-8");
                JSONObject params = new JSONObject();
                params.put("mobile_number", strings[1]);
                params.put("full_name", strings[2]);
                RequestBody body = RequestBody.create(mediaType, params.toString());
                Request request = new Request.Builder()
                        .url(strings[0])
                        .post(body)
                        .addHeader("Content-Type", "application/json;charset=utf-8")
                        .addHeader("token", SPData.getAppPreferences().getUsertoken())
                        .build();
                Response response = client.newCall(request).execute();
                assert response.body() != null;
                String json = response.body().string();
                JSONObject jsonObject1 = new JSONObject(json);
                message = jsonObject1.getString("message");
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("TAG", "doInBackground: " + e);
            }

            return message;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Toast.makeText(CustomerAddActivity.this, "" + s, Toast.LENGTH_SHORT).show();
            name.setText("");
            mobileNumber.setText("");
        }
    }
}
