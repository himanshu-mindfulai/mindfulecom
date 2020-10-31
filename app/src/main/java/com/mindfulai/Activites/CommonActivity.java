package com.mindfulai.Activites;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.mindfulai.Models.CustomerInfo.CustomerData;
import com.mindfulai.NetworkRetrofit.ApiService;
import com.mindfulai.NetworkRetrofit.ApiUtils;
import com.mindfulai.Utils.SPData;
import com.mindfulai.ministore.R;
import com.mindfulai.ui.CartFragment;
import com.mindfulai.ui.NotificationFragment;
import com.mindfulai.ui.WishListFragment;
import com.razorpay.PaymentData;
import com.razorpay.PaymentResultWithDataListener;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommonActivity extends AppCompatActivity implements PaymentResultWithDataListener {
    private String show;
    CartFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        show = getIntent().getStringExtra("show");
        if (show.equals("cart")) {
            setContentView(R.layout.activity_common);
            getSupportActionBar().setTitle("Cart");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            FragmentManager fm = getSupportFragmentManager();
            fragment = new CartFragment();
            fm.beginTransaction().replace(R.id.nav_host_fragment, fragment).commit();
        } else if (show.equals("wish")) {
            setContentView(R.layout.activity_common);
            getSupportActionBar().setTitle("WishList");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            FragmentManager fm = getSupportFragmentManager();
            WishListFragment fragment = new WishListFragment();
            fm.beginTransaction().replace(R.id.nav_host_fragment, fragment).commit();
        } else {
            setContentView(R.layout.activity_common);
            getSupportActionBar().setTitle("Notification");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            FragmentManager fm = getSupportFragmentManager();
            NotificationFragment fragment = new NotificationFragment();
            fm.beginTransaction().replace(R.id.nav_host_fragment, fragment).commit();
        }
    }




    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPaymentSuccess(String s, PaymentData paymentData) {

        ((CartFragment) fragment).onPaymentSuccess(s, paymentData);
    }

    @Override
    public void onPaymentError(int i, String s, PaymentData paymentData) {

    }

    public void finishAllActivity(View view) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("EXIT", true);
        startActivity(intent);
    }
}