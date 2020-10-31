package com.mindfulai.Activites;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.mindfulai.ministore.R;

public class OrderPlacedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_placed);
        getSupportActionBar().hide();
        TextView orderNumber = findViewById(R.id.order_number);
        TextView orderAmount = findViewById(R.id.order_amount);
        TextView orderPaymentMethod = findViewById(R.id.payment_method);
        String order_number = getIntent().getStringExtra("order_id");
        String order_amount = getIntent().getStringExtra("payment_amount");
        String payment_method = getIntent().getStringExtra("payment_method") + "";
        orderNumber.setText(order_number);
        orderAmount.setText(getString(R.string.rs) + order_amount);
        if (!payment_method.equals("null"))
            orderPaymentMethod.setText(payment_method);
        else
            orderPaymentMethod.setText("");
        TextView done = findViewById(R.id.done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(OrderPlacedActivity.this, MainActivity.class);
// set the new task and clear flags
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(OrderPlacedActivity.this, MainActivity.class);
// set the new task and clear flags
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }
}