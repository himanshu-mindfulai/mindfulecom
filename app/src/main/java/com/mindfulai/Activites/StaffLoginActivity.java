package com.mindfulai.Activites;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mindfulai.AppPrefrences.AppPreferences;
import com.mindfulai.Models.LoginData.Data;
import com.mindfulai.Models.LoginData.User_;
import com.mindfulai.NetworkRetrofit.ApiService;
import com.mindfulai.NetworkRetrofit.ApiUtils;
import com.mindfulai.ministore.R;
import com.mindfulai.Utils.CommonUtils;
import com.mindfulai.Utils.CustomProgressDialog;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StaffLoginActivity extends AppCompatActivity {
    EditText editTextEmail;
    EditText editTextPassword;
    private AppPreferences appPreferences;
    TextView editTextLoginButton;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_email_login);
            editTextEmail = findViewById(R.id.et_email);
            editTextPassword = findViewById(R.id.et_password);
            editTextLoginButton = findViewById(R.id.login);
            getSupportActionBar().setTitle("Staff Login");
            intent=new Intent();

            appPreferences = new AppPreferences(this);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            editTextLoginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!editTextEmail.getText().toString().replaceAll(" ", "").isEmpty() && !editTextPassword.getText().toString().replaceAll(" ", "").isEmpty())
                        verifyEmailPassword();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void verifyEmailPassword() {
        try {
            final CustomProgressDialog customProgressDialog = CommonUtils.showProgressDialog(StaffLoginActivity.this,
                    getString(R.string.please_wait_verifying_otp));
            ApiService apiService = ApiUtils.getAPIService();

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("email", editTextEmail.getText().toString());
            jsonObject.addProperty("password", editTextPassword.getText().toString());
            apiService.loginemailPassword(jsonObject).enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    try {
                        CommonUtils.hideProgressDialog(customProgressDialog);
                        if (response.isSuccessful()) {
                            String reponse_status = String.valueOf(response.body().get("status"));
                            Log.e("TAG", "onResponse: " + response.body());
                            if (reponse_status.matches("200")) {
                                Toast.makeText(StaffLoginActivity.this, response.body().get("message").toString(), Toast.LENGTH_SHORT).show();
                                Data data = new Gson().fromJson(response.body().get("data").getAsJsonObject().toString(), Data.class);
                                User_ user_ = data.getUser();
                                user_.getMobileNumber();
                                String token = data.getToken();
                                appPreferences.setUser_mobile_no(user_.getMobileNumber());
                                appPreferences.setUserId(user_.getId());
                                appPreferences.setUserProfilePic(user_.getProfile_picture());
                                appPreferences.setUserName(user_.getFullName());
                                appPreferences.setUsertoken(token);
                                appPreferences.setAddress("");
                                appPreferences.setUserType("staff");
                                intent.putExtra("staff","true");
                                setResult(RESULT_OK,intent);
                                finish();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                    Log.e("fail", call.toString());
                    CommonUtils.hideProgressDialog(customProgressDialog);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
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