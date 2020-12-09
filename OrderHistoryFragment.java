package com.mindfulai.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.mindfulai.Activites.OrderHistoryActivity;
import com.mindfulai.Adapter.OrderHistoryAdapter;
import com.mindfulai.Models.AllOrderHistory.Datum;
import com.mindfulai.Models.AllOrderHistory.OrderHistory;
import com.mindfulai.NetworkRetrofit.ApiService;
import com.mindfulai.NetworkRetrofit.ApiUtils;
import com.mindfulai.Utils.CommonUtils;
import com.mindfulai.Utils.CustomProgressDialog;
import com.mindfulai.Utils.SPData;
import com.mindfulai.ministore.R;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

public class OrderHistoryFragment extends Fragment {
    private List<Datum> orderHistoryDetailsList;
    private LinearLayout no_order_layout;
    private ShimmerFrameLayout shimmer_view_container;
    private RecyclerView recyclerView;
    private OrderHistoryAdapter orderHistoryAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_order_history, container, false);
        shimmer_view_container = view.findViewById(R.id.shimmer_view_container);
        shimmer_view_container.startShimmerAnimation();
        no_order_layout = view.findViewById(R.id.no_order_layout);
        LinearLayoutManager horizontalLayoutManagaer
                = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView = view.findViewById(R.id.recycler_view_orders);
        recyclerView.setLayoutManager(horizontalLayoutManagaer);
        if (SPData.getAppPreferences().getUsertoken() != null && !SPData.getAppPreferences().getUsertoken().isEmpty()) {
            no_order_layout.setVisibility(View.GONE);
            getOrderHistory();
        } else {
            no_order_layout.setVisibility(View.VISIBLE);
            shimmer_view_container.setVisibility(View.GONE);
        }
        return view;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("TAG", "onActivityResult: "+resultCode);
        if (resultCode == RESULT_OK && data != null) {
            orderHistoryAdapter.cancelledOrder(data.getIntExtra("position", -1));
        }
    }

    private void getOrderHistory() {
        final CustomProgressDialog customProgressDialog = CommonUtils.showProgressDialog(getActivity(),
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
                        orderHistoryAdapter = new OrderHistoryAdapter(getActivity(), orderHistoryDetailsList);
                        recyclerView.setAdapter(orderHistoryAdapter);
                        orderHistoryAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<OrderHistory> call, @NonNull Throwable t) {
                Log.e("TAG", t.toString());
                Toast.makeText(getActivity(), "Failed to connect", Toast.LENGTH_SHORT).show();
                shimmer_view_container.stopShimmerAnimation();
                shimmer_view_container.setVisibility(View.GONE);
                no_order_layout.setVisibility(View.VISIBLE);
            }
        });
    }
}
