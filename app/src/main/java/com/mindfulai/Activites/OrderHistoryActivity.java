package com.mindfulai.Activites;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.mindfulai.Adapter.OrderHistoryAdapter;
import com.mindfulai.Models.AllOrderHistory.Datum;
import com.mindfulai.Models.AllOrderHistory.OrderHistory;
import com.mindfulai.NetworkRetrofit.ApiService;
import com.mindfulai.NetworkRetrofit.ApiUtils;
import com.mindfulai.ministore.R;
import com.mindfulai.Utils.CommonUtils;
import com.mindfulai.Utils.CustomProgressDialog;
import com.mindfulai.Utils.SPData;
import com.mindfulai.infinitePlaceHolder.ItemView;
import com.mindfulai.infinitePlaceHolder.LoadMoreView;
import com.mindorks.placeholderview.InfinitePlaceHolderView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderHistoryActivity extends AppCompatActivity {
    private static final String TAG = "OrderHistoryActivity";
    private List<Datum> orderHistoryDetailsList;
    private LinearLayout no_order_layout;
    private ShimmerFrameLayout shimmer_view_container;
    private RecyclerView recyclerView;
    private OrderHistoryAdapter orderHistoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_order_history);

            Objects.requireNonNull(getSupportActionBar()).setTitle("Order History");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            shimmer_view_container = findViewById(R.id.shimmer_view_container);
            shimmer_view_container.startShimmerAnimation();
            no_order_layout = findViewById(R.id.no_order_layout);
            LinearLayoutManager horizontalLayoutManagaer
                    = new LinearLayoutManager(OrderHistoryActivity.this, LinearLayoutManager.VERTICAL, false);
            recyclerView = findViewById(R.id.recycler_view_orders);

            recyclerView.setLayoutManager(horizontalLayoutManagaer);
            if (SPData.getAppPreferences().getUsertoken() != null && !SPData.getAppPreferences().getUsertoken().isEmpty()) {
                no_order_layout.setVisibility(View.GONE);
                getOrderHistory();
            } else {
                no_order_layout.setVisibility(View.VISIBLE);
                shimmer_view_container.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            Log.e(TAG, "onCreate: " + e);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK&&data!=null){
            orderHistoryAdapter.cancelledOrder(data.getIntExtra("position",-1));
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void getOrderHistory() {
        final CustomProgressDialog customProgressDialog = CommonUtils.showProgressDialog(OrderHistoryActivity.this,
                "Getting orders ...");
        ApiService apiService = ApiUtils.getHeaderAPIService(SPData.getAppPreferences().getUsertoken());
        apiService.getOrderHistory().enqueue(new Callback<OrderHistory>() {
            @Override
            public void onResponse(@NonNull Call<OrderHistory> call, @NonNull Response<OrderHistory> response) {
                CommonUtils.hideProgressDialog(customProgressDialog);
                shimmer_view_container.stopShimmerAnimation();
                shimmer_view_container.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    OrderHistory orderHistory = response.body();
                    assert orderHistory != null;
                    orderHistoryDetailsList = orderHistory.getData();
                   if (orderHistoryDetailsList.size() == 0) {
                        no_order_layout.setVisibility(View.VISIBLE);
                    } else {
                       orderHistoryAdapter = new OrderHistoryAdapter(OrderHistoryActivity.this,orderHistoryDetailsList);
                        recyclerView.setAdapter(orderHistoryAdapter);
                        orderHistoryAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<OrderHistory> call, @NonNull Throwable t) {
                Log.e("TAG", t.toString());
                Toast.makeText(OrderHistoryActivity.this, "Failed to connect", Toast.LENGTH_SHORT).show();
                shimmer_view_container.stopShimmerAnimation();
                shimmer_view_container.setVisibility(View.GONE);
                no_order_layout.setVisibility(View.VISIBLE);
            }
        });
    }

}