package com.mindfulai.Activites;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.mindfulai.Adapter.VendorByCategoryAdapter;
import com.mindfulai.Models.VendorBase;
import com.mindfulai.Models.VendorChild;
import com.mindfulai.NetworkRetrofit.ApiService;
import com.mindfulai.NetworkRetrofit.ApiUtils;
import com.mindfulai.ministore.R;
import com.mindfulai.Utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShowVendorByCategoryActivity extends AppCompatActivity {
    private String CATEGORY_ID, SUBCATEGORY_ID, category_name, subcategory_name;
    private RecyclerView products_grid;
    private LinearLayout no_products;
    private ShimmerFrameLayout shimmerView2;
    private VendorByCategoryAdapter vendorByCategoryAdapter;
    private List<VendorChild> vendorBaseList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_vendor_by_category);
        CATEGORY_ID = getIntent().getStringExtra("category_id");
        category_name = getIntent().getStringExtra("categoryName");
        SUBCATEGORY_ID = getIntent().getStringExtra("subcategory_id");
        subcategory_name = getIntent().getStringExtra("subcategoryName");
        Toolbar toolbar = findViewById(R.id.all_product_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_white);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle(CommonUtils.capitalizeWord(category_name));
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorWhite));
        shimmerView2 = findViewById(R.id.shimmerView2);
        shimmerView2.startShimmerAnimation();
        products_grid = findViewById(R.id.products_grid);
        no_products = findViewById(R.id.no_products);
        vendorBaseList = new ArrayList<>();
        getVendors();

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void getVendors() {
        ApiService apiService = ApiUtils.getHeaderAPIService();
        apiService.getVendorByCategoryId(CATEGORY_ID).enqueue(new Callback<VendorBase>() {
            @Override
            public void onResponse(@NonNull Call<VendorBase> call, @NonNull Response<VendorBase> response) {
                if (response.isSuccessful()) {
                    shimmerView2.stopShimmerAnimation();
                    shimmerView2.setVisibility(View.GONE);
                    products_grid.setVisibility(View.VISIBLE);
                    assert response.body() != null;
                    vendorBaseList = response.body().getData();
                    if (vendorBaseList == null || vendorBaseList.size() == 0) {
                        no_products.setVisibility(View.VISIBLE);
                    }
                    Log.e("TAG", "onResponse: "+response);
                    //GridLayoutManager gridLayoutManager1 = new GridLayoutManager(ShowVendorByCategoryActivity.this, 2);
                    LinearLayoutManager manager = new LinearLayoutManager(ShowVendorByCategoryActivity.this);
                    products_grid.setLayoutManager(manager);
                    if (SUBCATEGORY_ID != null && !SUBCATEGORY_ID.isEmpty())
                        vendorByCategoryAdapter = new VendorByCategoryAdapter(ShowVendorByCategoryActivity.this, vendorBaseList, SUBCATEGORY_ID, subcategory_name);
                    else
                        vendorByCategoryAdapter = new VendorByCategoryAdapter(ShowVendorByCategoryActivity.this, vendorBaseList, CATEGORY_ID, category_name);
                    products_grid.setAdapter(vendorByCategoryAdapter);
                    vendorByCategoryAdapter.notifyDataSetChanged();
                } else {
                    Log.e("TAG", "onResponse: " + response);
                }
            }

            @Override
            public void onFailure(@NonNull Call<VendorBase> call, @NonNull Throwable t) {
                Log.e("TAG", "onFailure: " + t.getMessage());
                shimmerView2.stopShimmerAnimation();
                shimmerView2.setVisibility(View.GONE);
                Toast.makeText(ShowVendorByCategoryActivity.this, "" + "Failed to connect", Toast.LENGTH_SHORT).show();
            }
        });
    }
}