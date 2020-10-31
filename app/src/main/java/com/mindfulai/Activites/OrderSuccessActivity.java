package com.mindfulai.Activites;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.mindfulai.Adapter.OrderSuccessAdapter;
import com.mindfulai.Models.orderDetailInfo.Product;
import com.mindfulai.ministore.R;

import java.util.ArrayList;
import java.util.List;

public class OrderSuccessActivity extends AppCompatActivity {


    private static final String TAG = "OrderSuccess";
    List<Product> productsList = new ArrayList<>();
    private String order_id;
    private String order_price;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimary));
            setContentView(R.layout.activity_order_success);

            if (getIntent() != null) {
                productsList = getIntent().getParcelableArrayListExtra("orderHistoryData");
                order_id = getIntent().getStringExtra("order_id");
                order_price = getIntent().getStringExtra("order_amount");
            }



            TextView tv_continue = findViewById(R.id.tv_continue);
            TextView order_total = findViewById(R.id.order_total);
            order_total.setText(getResources().getString(R.string.rs) + order_price);
            TextView tv_order_id = findViewById(R.id.order_id);
            tv_order_id.setText("# " + order_id);
            RecyclerView rv_products = findViewById(R.id.rv_products);
            LinearLayoutManager verticalManager
                    = new LinearLayoutManager(OrderSuccessActivity.this, LinearLayoutManager.VERTICAL, false);
            rv_products.setLayoutManager(verticalManager);


            tv_continue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent i = new Intent(OrderSuccessActivity.this, MainActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                    finish();
                }
            });

            OrderSuccessAdapter orderSuccessAdapter = new OrderSuccessAdapter(OrderSuccessActivity.this, productsList);
            rv_products.setAdapter(orderSuccessAdapter);
        }catch (Exception e){
            Log.e(TAG, "onCreate: "+e );
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(OrderSuccessActivity.this,MainActivity.class)
        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
    }
}
