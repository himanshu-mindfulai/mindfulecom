package com.mindfulai.Activites;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.JsonObject;
import com.mindfulai.Models.CartInformation.CartDetailsInformation;
import com.mindfulai.NetworkRetrofit.ApiService;
import com.mindfulai.NetworkRetrofit.ApiUtils;
import com.mindfulai.Utils.CommonUtils;
import com.mindfulai.Utils.CustomProgressDialog;
import com.mindfulai.Utils.SPData;
import com.mindfulai.ministore.R;
import com.mindfulai.ui.PriceDetailsBottomSheet;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.mindfulai.customclass.Constants.CARY_BAG_PRICE;

public class PromoCodeActivity extends AppCompatActivity implements TextWatcher {
    private TextInputLayout tilCoupon;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promo_code);
        getSupportActionBar().setTitle("Apply Promo Code");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        tilCoupon = findViewById(R.id.til_coupon);
        tilCoupon.getEditText().addTextChangedListener(this);
        intent = new Intent();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            setResult(1,intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        setResult(1,intent);
        finish();
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (s.toString().isEmpty()){
            tilCoupon.setError(null);
            tilCoupon.setHelperTextEnabled(false);
            tilCoupon.setEndIconMode(TextInputLayout.END_ICON_NONE);
        } else {
            tilCoupon.setError(null);
            tilCoupon.setHelperTextEnabled(false);
            tilCoupon.setEndIconMode(TextInputLayout.END_ICON_CUSTOM);
            tilCoupon.setEndIconDrawable(R.drawable.ic_send);
            tilCoupon.setEndIconOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    applyCoupon();
                }
            });
        }
    }
    private void applyCoupon() {
        CustomProgressDialog customProgressDialog = new CustomProgressDialog(
                PromoCodeActivity.this,"Applying coupon..."
        );
        customProgressDialog.show();
        ApiService service = ApiUtils.getHeaderAPIService(
                SPData.getAppPreferences().getUsertoken()
        );
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("coupon", tilCoupon.getEditText().getText().toString());
        service.applyCoupon(jsonObject).enqueue(new Callback<CartDetailsInformation>() {
            @Override
            public void onResponse(Call<CartDetailsInformation> call, Response<CartDetailsInformation> response) {
                if (response.isSuccessful()){
                    CartDetailsInformation cartDetailsInformation = response.body();
                    tilCoupon.setError(null);
                    tilCoupon.setHelperTextEnabled(true);
                    intent.putExtra("discount",cartDetailsInformation.getData().getCoupon().getDiscountAmt());
                    intent.putExtra("code",""+tilCoupon.getEditText().getText().toString().toUpperCase());
                    Log.e("TAG", "onResponse: "+ cartDetailsInformation.getData().getTotal());
                    intent.putExtra("total_amount",""+cartDetailsInformation.getData().getTotal());
                    tilCoupon.setHelperText("Coupon applied successfully.\nTotal discount: " + getString(R.string.rs)+cartDetailsInformation.getData().getCoupon().getDiscountAmt());
                    tilCoupon.setHelperTextColor(ColorStateList.valueOf(
                            getResources().getColor(R.color.colorOrange)
                    ));
                    tilCoupon.setEndIconMode(TextInputLayout.END_ICON_CUSTOM);
                    tilCoupon.getEditText().setEnabled(false);
                    intent.putExtra("coupon_applied",true);
                    tilCoupon.setEndIconDrawable(R.drawable.ic_close_black_24dp);
                    tilCoupon.setEndIconOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            tilCoupon.getEditText().setText("");
                            tilCoupon.getEditText().setEnabled(true);
                            intent.putExtra("coupon_applied",false);
                            tilCoupon.setError("Coupon remove successfully");
                        }
                    });

                } else {
                    intent.putExtra("coupon_applied",false);
                    tilCoupon.setError("Coupon is either invalid or non applicable");
                    //Toast.makeText(getContext(), "Coupon is either invalid or non applicable", Toast.LENGTH_SHORT).show();
                }
                CommonUtils.hideProgressDialog(customProgressDialog);
            }

            @Override
            public void onFailure(Call<CartDetailsInformation> call, Throwable t) {
                intent.putExtra("coupon_applied",false);
                CommonUtils.hideProgressDialog(customProgressDialog);
            }
        });
    }
}