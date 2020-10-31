package com.mindfulai.Activites;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.CredentialPickerConfig;
import com.google.android.gms.auth.api.credentials.HintRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mindfulai.AppPrefrences.AppPreferences;
import com.mindfulai.Models.CartInformation.CartDetailsInformation;
import com.mindfulai.Models.CartInformation.Product;
import com.mindfulai.Models.LoginData.Data;
import com.mindfulai.Models.LoginData.User_;
import com.mindfulai.Models.UserBaseAddress;
import com.mindfulai.Models.UserDataAddress;
import com.mindfulai.NetworkRetrofit.ApiService;
import com.mindfulai.NetworkRetrofit.ApiUtils;
import com.mindfulai.Utils.CommonUtils;
import com.mindfulai.Utils.CustomProgressDialog;
import com.mindfulai.Utils.SPData;
import com.mindfulai.ministore.R;
import com.mindfulai.otp.AutoDetectOTP;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText et_mobile_no, et_otp;
    private String token;
    private TextView send_otp;
    private AppPreferences appPreferences;
    private static final int RESOLVE_HINT = 1000;
    private AutoDetectOTP autoDetectOTP;
    private String referralCode = "";
    private TextView resend, skip;
    private String firebasetoken="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            Window window = this.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimary));
            setContentView(R.layout.activity_login);
            autoDetectOTP = new AutoDetectOTP(this);
            requestHint();
            appPreferences = new AppPreferences(this);
            et_mobile_no = findViewById(R.id.et_mobile_no);
            et_otp = findViewById(R.id.et_otp);
            resend = findViewById(R.id.resend);
            resend.setVisibility(View.GONE);
            send_otp = findViewById(R.id.send_otp);
            handleReferralsIfAny();
            skip = findViewById(R.id.skip);
            final CheckBox checkBox = findViewById(R.id.checkbox);
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if(b){
                        send_otp.setEnabled(true);
                        send_otp.setBackground(getDrawable(R.drawable.save_profile));
                    }else{
                        send_otp.setEnabled(false);
                        send_otp.setBackground(getDrawable(R.drawable.square));
                    }
                }
            });
            skip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getIntent().getStringExtra("from") == null)
                        finish();
                    else {
                        startActivity(new Intent(LoginActivity.this, MainActivity.class).putExtra("from",true));
                        finish();
                    }
                }
            });
            TextView staffLogin = findViewById(R.id.staff_login);
            if(SPData.showStaffLogin()){
                staffLogin.setVisibility(View.VISIBLE);
            }else
                staffLogin.setVisibility(View.GONE);
            staffLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivityForResult(new Intent(LoginActivity.this, StaffLoginActivity.class), 2);
                }
            });
            et_mobile_no.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    send_otp.setText("SEND OTP");
                }
            });
            et_otp.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (send_otp.getText().toString().length() == 10)
                        send_otp.setText("VERIFY OTP");
                }
            });

            send_otp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (send_otp.getText().toString().matches("SEND OTP")) {

                        if (et_mobile_no.getText().toString().isEmpty() || et_mobile_no.getText().length() < 10) {
                            et_mobile_no.setError("Please enter valid mobile number");
                            et_mobile_no.setFocusable(true);
                            et_mobile_no.requestFocus();
                        } else {
                            sendOtp();

                        }
                    } else if (send_otp.getText().toString().matches("VERIFY OTP") || et_otp.getText().length() < 4) {
                        if (TextUtils.isEmpty(et_otp.getText().toString())) {
                            et_otp.setError("Please enter OTP");
                            et_otp.setFocusable(true);
                            et_otp.requestFocus();
                        } else {
                            verifyOtp();

                        }

                    }
                }
            });

            resend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    et_otp.setText("");
                    if (TextUtils.isEmpty(et_mobile_no.getText().toString()) || et_mobile_no.getText().length() < 10) {
                        et_mobile_no.setError("Please enter valid mobile number");
                        et_mobile_no.setFocusable(true);
                        et_mobile_no.requestFocus();
                    } else {
                        sendOtp();

                    }

                }
            });
        } catch (Exception e) {
            Log.e("TAG", "onCreate: " + e);
        }

    }

    private void resendOtp(){
        AlertDialog dialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Resend otp");
        builder.setMessage("How would you like to receive the otp?");
        builder.setPositiveButton("Via call", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                resendOtp("Voice");
            }
        });
        builder.setNegativeButton("Via text", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                resendOtp("text");
            }
        });
        dialog = builder.create();
        dialog.show();
    }

    private void resendOtp(String type){
        CustomProgressDialog progressDialog = new CustomProgressDialog(this, "Please wait...");
        progressDialog.show();
        ApiService service = ApiUtils.getAPIService();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("mobile_number", et_mobile_no.getText().toString());
        jsonObject.addProperty("retryType", type);
        service.resendOtp(jsonObject).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()){
                    Toast.makeText(LoginActivity.this, "OTP Send to : " + et_mobile_no.getText().toString(), Toast.LENGTH_SHORT).show();
                    appPreferences.setUser_mobile_no(et_mobile_no.getText().toString());
                    et_otp.setEnabled(true);
                    send_otp.setText("VERIFY OTP");
                } else {
                    Log.e("LoginActivity", "resendOtp: " + response.message());
                    Toast.makeText(LoginActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
                CommonUtils.hideProgressDialog(progressDialog);
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                t.printStackTrace();
                CommonUtils.hideProgressDialog(progressDialog);
                Toast.makeText(LoginActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleReferralsIfAny() {
        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        Uri deepLink = null;
                        if (pendingDynamicLinkData != null){
                            deepLink = pendingDynamicLinkData.getLink();
                        }
                        if (SPData.getAppPreferences().getUsertoken() == null || SPData.getAppPreferences().getUsertoken().isEmpty() && deepLink != null){
                            referralCode = deepLink.getQueryParameter("invitedby");
                            //Toast.makeText(LoginActivity.this, deepLink.toString(), Toast.LENGTH_SHORT).show();
                            Log.i("Refer", "code: " + referralCode);
                        }
                    }
                });
    }


    public void getAllCart() {
        CustomProgressDialog dialog = new CustomProgressDialog(this, "Loading...");
        dialog.show();
        ApiService apiService = ApiUtils.getHeaderAPIService(SPData.getAppPreferences().getUsertoken());
        apiService.showCartItems().enqueue(new Callback<CartDetailsInformation>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<CartDetailsInformation> call, @NonNull Response<CartDetailsInformation> response) {
                CommonUtils.hideProgressDialog(dialog);
                try {
                    if (response.isSuccessful()) {
                        CartDetailsInformation cartDetailsInfo = response.body();
                        assert cartDetailsInfo != null;
                        List<Product> cartDataArrayList = cartDetailsInfo.getData().getProducts();
                        SPData.getAppPreferences().setTotalCartCount(cartDataArrayList.size());
                        getAddresses();
                    }else{
                        Toast.makeText(LoginActivity.this, "Try again", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.e("TAG", "onResponse: " + e);
                }
            }

            @Override
            public void onFailure(@NonNull Call<CartDetailsInformation> call, @NonNull Throwable t) {
                Toast.makeText(LoginActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getAddresses(){
        CustomProgressDialog dialog = new CustomProgressDialog(this, "Loading...");
        dialog.show();
        ApiService apiService = ApiUtils.getHeaderAPIService(SPData.getAppPreferences().getUsertoken());
        apiService.getUserBaseAddress().enqueue(new Callback<UserBaseAddress>() {
            @Override
            public void onResponse(Call<UserBaseAddress> call, Response<UserBaseAddress> response) {
                if (response.isSuccessful()){
                    CommonUtils.hideProgressDialog(dialog);
                    ArrayList<UserDataAddress> dataList = response.body().getData();
                    if (dataList.isEmpty()){
//                        new CommonUtils(MainActivity.this).getAddress(textViewLocation);
                        finish();
                        startActivity(new Intent(
                                LoginActivity.this, AddAddressActivity.class
                        ).putExtra("title", "Add Address"));
                    } else {
                        if (getIntent().getStringExtra("from") == null)
                            finish();
                        else {
                            startActivity(new Intent(LoginActivity.this, MainActivity.class).putExtra("from",true));
                            finish();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<UserBaseAddress> call, Throwable t) {
                CommonUtils.hideProgressDialog(dialog);
            }
        });
    }

    public void autoDetectOTP() {
        final CustomProgressDialog customProgressDialog = CommonUtils.showProgressDialog(LoginActivity.this,
                "Verifying OTP");
        autoDetectOTP.startSmsRetriver(new AutoDetectOTP.SmsCallback() {
            @Override
            public void connectionfailed(String e) {
                CommonUtils.hideProgressDialog(customProgressDialog);
                Toast.makeText(LoginActivity.this, "Something went wrong in auto detection", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void connectionSuccess(Void aVoid) {
            }

            @Override
            public void smsCallback(String sms) {
                if (sms.contains(":") && sms.contains(".")) {
                    CommonUtils.hideProgressDialog(customProgressDialog);
                    String otp = sms.substring(sms.indexOf(":") + 1, sms.indexOf(".")).trim();
                    et_otp.setText(otp);
                    verifyOtp();
                }
            }
        });
    }

    private void sendOtp() {

        final CustomProgressDialog customProgressDialog = CommonUtils.showProgressDialog(LoginActivity.this,
                getString(R.string.please_wait));
        ApiService apiService = ApiUtils.getAPIService();

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("mobile_number", et_mobile_no.getText().toString());
        //   jsonObject.addProperty("hash",AutoDetectOTP.getHashCode(this));
        apiService.loginmobile(jsonObject).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                CommonUtils.hideProgressDialog(customProgressDialog);
                if (response.isSuccessful()) {
                    String reponse_status = String.valueOf(response.body().get("status"));
                    if (reponse_status.matches("200")) {
                        Toast.makeText(LoginActivity.this, "OTP Send to : " + et_mobile_no.getText().toString(), Toast.LENGTH_SHORT).show();
                        appPreferences.setUser_mobile_no(et_mobile_no.getText().toString());
                        et_otp.setEnabled(true);
                        send_otp.setText("VERIFY OTP");
                        otpSent();
                        //    autoDetectOTP();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                Log.e("fail", call.toString());
                Toast.makeText(LoginActivity.this, "Failed to connect", Toast.LENGTH_SHORT).show();
                CommonUtils.hideProgressDialog(customProgressDialog);
            }
        });
    }

    private void otpSent(){
        resend.setVisibility(View.VISIBLE);
        CountDownTimer timer = new CountDownTimer(30000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                resend.setOnClickListener(null);
                resend.setText("Resend OTP in " + millisUntilFinished/1000 + "sec(s)");
            }

            @Override
            public void onFinish() {
                resend.setText("Resend OTP");
                resend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        et_otp.setText("");
                        if (TextUtils.isEmpty(et_mobile_no.getText().toString()) || et_mobile_no.getText().length() < 10) {
                            et_mobile_no.setError("Please enter valid mobile number");
                            et_mobile_no.setFocusable(true);
                            et_mobile_no.requestFocus();
                        } else {
                            sendOtp();
                        }
                    }
                });
            }
        };
        timer.start();
    }

    private void verifyOtp() {
        final CustomProgressDialog customProgressDialog = CommonUtils.showProgressDialog(LoginActivity.this,
                getString(R.string.please_wait_verifying_otp));
        final ApiService apiService = ApiUtils.getAPIService();

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("mobile_number", et_mobile_no.getText().toString());
        jsonObject.addProperty("otp", et_otp.getText().toString());
        apiService.verifyOtp(referralCode, jsonObject).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                CommonUtils.hideProgressDialog(customProgressDialog);
                if (response.isSuccessful()) {
                    String reponse_status = String.valueOf(response.body().get("status"));
                    if (reponse_status.matches("200")) {
                        Data data = new Gson().fromJson(response.body().get("data").getAsJsonObject().toString(), Data.class);
                        User_ user_ = data.getUser();
                        user_.getMobileNumber();
                        token = data.getToken();
                        Log.e("TAG", "onResponse: "+token );
                        appPreferences.setUser_mobile_no(et_mobile_no.getText().toString());
                        appPreferences.setUserId(user_.getId());
                        appPreferences.setUserProfilePic(user_.getProfile_picture());
                        appPreferences.setUserName(user_.getFullName());
                        SPData.getAppPreferences().setUsertoken(token);
                        appPreferences.setAddress("");
                        appPreferences.setUserType("customer");
                        registerFCMToken();
                    } else
                        Toast.makeText(LoginActivity.this, "" + reponse_status, Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("TAG", "onResponse: " + response);
                    Toast.makeText(LoginActivity.this, "Response unsuccessful "+response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                Log.e("fail", call.toString());
                CommonUtils.hideProgressDialog(customProgressDialog);
                Toast.makeText(LoginActivity.this, ""+t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void registerFCMToken(){
       CustomProgressDialog dialog = new CustomProgressDialog(this, "Loading...");
        dialog.show();
                FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                    @Override
                    public void onSuccess(InstanceIdResult instanceIdResult) {
                            firebasetoken = instanceIdResult.getToken();
                        Log.e("TAG", "registerFCMToken: "+SPData.getAppPreferences().getUsertoken());
                        ApiService apiService = ApiUtils.getHeaderAPIService(SPData.getAppPreferences().getUsertoken());

                        JsonObject jsonObject = new JsonObject();
                        jsonObject.addProperty("registrationToken", firebasetoken);
                        apiService.addFCMToken(jsonObject).enqueue(new Callback<JsonObject>() {
                            @Override
                            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                                CommonUtils.hideProgressDialog(dialog);
                                Log.e("TAG", "onResponse: "+response );
                                if (response.isSuccessful()){
                                    getAllCart();
                                } else {
                                    SPData.getAppPreferences().setUsertoken("");
                                    Log.e("TAG", "onResponse: "+response.message());
                                    Toast.makeText(LoginActivity.this, ""+response.message(), Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<JsonObject> call, Throwable t) {
                                SPData.getAppPreferences().setUsertoken("");
                                CommonUtils.hideProgressDialog(dialog);
                                t.printStackTrace();
                                Toast.makeText(LoginActivity.this, ""+t.getMessage(), Toast.LENGTH_SHORT).show();
                                //getAllCart();
                            }
                        });
                        Log.e("TAG", "onSuccess: "+firebasetoken );
                    }
                });


    }


    private void requestHint() {
        GoogleApiClient apiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                    }
                })
                .addApi(Auth.CREDENTIALS_API)
                .build();
        HintRequest hintRequest = new HintRequest.Builder()
                .setHintPickerConfig(new CredentialPickerConfig.Builder()
                        .setShowCancelButton(true)
                        .build())
                .setPhoneNumberIdentifierSupported(true)
                .build();

        PendingIntent intent = Auth.CredentialsApi.getHintPickerIntent(
                apiClient, hintRequest);
        try {
            startIntentSenderForResult(intent.getIntentSender(),
                    RESOLVE_HINT, null, 0, 0, 0);
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
    }

    // Obtain the phone number from the result
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESOLVE_HINT) {
            if (resultCode == RESULT_OK) {
                Credential credential = data.getParcelableExtra(Credential.EXTRA_KEY);
                assert credential != null;
                et_mobile_no.setText(credential.getId().replace("+91", ""));
            }
        }
        if (resultCode == RESULT_OK && data.getStringExtra("staff") != null && data.getStringExtra("staff").equals("true"))
            finish();
    }

    public void openTermsCondition(View view) {
        startActivity(new Intent(LoginActivity.this, PrivacyPolicy.class).putExtra("type",ApiService.TNC));
    }

    public void openPrivacyPolicy(View view) {
        startActivity(new Intent(LoginActivity.this, PrivacyPolicy.class).putExtra("type",ApiService.PRIVACY));
    }
}