package com.mindfulai.Activites;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.mindfulai.Adapter.ProductsAdapter;
import com.mindfulai.Models.DifferentVarients;
import com.mindfulai.Models.varientsByCategory.Datum;
import com.mindfulai.Models.varientsByCategory.Images;
import com.mindfulai.Models.varientsByCategory.OptionsAttribute;
import com.mindfulai.Models.varientsByCategory.VarientsByCategory;
import com.mindfulai.NetworkRetrofit.ApiService;
import com.mindfulai.NetworkRetrofit.ApiUtils;
import com.mindfulai.Utils.CommonUtils;
import com.mindfulai.Utils.SPData;
import com.mindfulai.ministore.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchPrdouctActivity extends AppCompatActivity {
    private RecyclerView products_grid;
    private LinearLayout no_products;
    private ProductsAdapter productAdapter;
    private ShimmerFrameLayout shimmerView2;
    private List<Datum> varientList;
    private ArrayList<ArrayList<OptionsAttribute>> alloptionsAttributeArrayList;
    private ArrayList<ArrayList<DifferentVarients>> alldifferentVarientList;
    private boolean isLoading = false;
    private VarientsByCategory productVarients;
    private Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_prdouct);

        Toolbar toolbar = findViewById(R.id.all_product_toolbar);
        EditText searchProductsEditText = findViewById(R.id.search_products);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_white);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle("Search Products");
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorWhite));
        intent = new Intent();
        shimmerView2 = findViewById(R.id.shimmerView2);
        shimmerView2.setVisibility(View.GONE);

        products_grid = findViewById(R.id.products_grid);

        no_products = findViewById(R.id.no_products);
        varientList = new ArrayList<>();

        alloptionsAttributeArrayList = new ArrayList<>();
        alldifferentVarientList = new ArrayList<>();
        initScrollListener();
        searchProductsEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                varientList.clear();
                no_products.setVisibility(View.GONE);
                if (s.toString().replaceAll(" ", "").isEmpty()) {
                    shimmerView2.setVisibility(View.GONE);
                } else {
                    shimmerView2.setVisibility(View.VISIBLE);
                    shimmerView2.startShimmerAnimation();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

                if (!s.toString().replaceAll(" ", "").isEmpty()) {
                    getProducts(s.toString());
                } else
                    no_products.setVisibility(View.GONE);
            }
        });
    }

    private void initScrollListener() {
        products_grid.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                GridLayoutManager linearLayoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
                if (!isLoading) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == varientList.size() - 1) {
                        if (varientList.size() < productVarients.getData().size()) {
                            loadMore();
                            isLoading = true;
                        }
                    }
                }
            }
        });
    }

    private void loadMore() {
        varientList.add(null);
        productAdapter.notifyItemInserted(varientList.size() - 1);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                varientList.remove(varientList.size() - 1);
                int scrollPosition = varientList.size();
                productAdapter.notifyItemRemoved(scrollPosition);
                int currentSize = scrollPosition;
                int nextLimit = currentSize + 6;
                if (productVarients.getData().size() > nextLimit)
                    while (currentSize < nextLimit) {
                        varientList.add(productVarients.getData().get(currentSize));
                        currentSize++;
                    }
                else {
                    while (productVarients.getData().size() > currentSize) {
                        varientList.add(productVarients.getData().get(currentSize));
                        currentSize++;
                    }
                }
                getAllOptionsVarient();
                productAdapter.notifyDataSetChanged();
                isLoading = false;
            }
        }, 2000);
    }

    private void getAllOptionsVarient() {
        for (int position = 0; position < varientList.size(); position++) {
            ArrayList<OptionsAttribute> optionsAttributeArrayList = new ArrayList<>();
            for (int j = 0; j < varientList.get(position).getAttributes().size(); j++) {
                ArrayList<String> option_value = new ArrayList<>();
                for (int k = 0; k < varientList.get(position).getAttributes().get(j).getOption().size(); k++) {
                    option_value.add(k, varientList.get(position).getAttributes().get(j).getOption().get(k).getValue());
                }
                optionsAttributeArrayList.add(new OptionsAttribute(varientList.get(position).getAttributes().get(j).getAttribute().getName(), option_value));
            }
            alloptionsAttributeArrayList.add(optionsAttributeArrayList);
        }
        for (int position = 0; position < varientList.size(); position++) {
            ArrayList<DifferentVarients> differentVarientsArrayList = new ArrayList<>();
            for (int k = 0; k < varientList.get(position).getVarients().size(); k++) {
                ArrayList<String> varient_option_value = new ArrayList<>();
                for (int j = 0; j < varientList.get(position).getVarients().get(k).getAttributes().size(); j++)
                    varient_option_value.add(varientList.get(position).getVarients().get(k).getAttributes().get(j).getOption().getValue());
                String sellinPrice = "";
                if (varientList.get(position).getVarients().get(k).getSellingPrice() != 0.0)
                    sellinPrice = "" + varientList.get(position).getVarients().get(k).getSellingPrice();
                else
                    sellinPrice = "";
                int minQty = -1;
                if (varientList.get(position).getVarients().get(k).getMinOrderQuantity() != null)
                    minQty = varientList.get(position).getVarients().get(k).getMinOrderQuantity();
                ArrayList<String> listOfImages = new ArrayList<>();
                Images images = varientList.get(position).getVarients().get(k).getImages();
                if (images != null && images.getPrimary() != null)
                    listOfImages.add(images.getPrimary());
                if (images != null && images.getSecondary() != null)
                    listOfImages.add(images.getSecondary());
                differentVarientsArrayList.add(new DifferentVarients("" + varientList.get(position).getVarients().get(k).getPrice(), varient_option_value, varientList.get(position).getVarients().get(k).getId(), varientList.get(position).getVarients().get(k).getDescription(), "" + varientList.get(position).getVarients().get(k).getStock(), sellinPrice, minQty,listOfImages));

            }
            alldifferentVarientList.add(differentVarientsArrayList);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            int menuid = data.getIntExtra("fragment", R.id.navigation_cart);
            checkFragmentClick(menuid);
        }
    }

    private void checkFragmentClick(int itemId) {
        switch (itemId) {
            case R.id.navigation_home:
                intent.putExtra("fragment", R.id.navigation_home);
                setResult(RESULT_OK, intent);
                finish();
            case R.id.navigation_wishlist:
                intent.putExtra("fragment", R.id.navigation_wishlist);
                setResult(RESULT_OK, intent);
                finish();
            case R.id.navigation_cart:
                intent.putExtra("fragment", R.id.navigation_cart);
                setResult(RESULT_OK, intent);
                finish();
            case R.id.navigation_notifications:
                intent.putExtra("fragment", R.id.navigation_notifications);
                setResult(RESULT_OK, intent);
                finish();
            case R.id.navigation_account:
                intent.putExtra("fragment", R.id.navigation_account);
                setResult(RESULT_OK, intent);
                finish();
        }
    }

    private void getProducts(String query) {
        try {
            ApiService apiService;
            if (!SPData.getAppPreferences().getUsertoken().equals(""))
                apiService = ApiUtils.getHeaderAPIService(SPData.getAppPreferences().getUsertoken());
            else
                apiService = ApiUtils.getAPIService();
            apiService.getSearchProducts(query).enqueue(new Callback<VarientsByCategory>() {
                @Override
                public void onResponse(@NonNull Call<VarientsByCategory> call, @NonNull Response<VarientsByCategory> response) {
                    if (response.isSuccessful()) {
                        shimmerView2.stopShimmerAnimation();
                        shimmerView2.setVisibility(View.GONE);
                        products_grid.setVisibility(View.VISIBLE);
                        productVarients = response.body();
                        varientList = Objects.requireNonNull(productVarients).getData();
                        if (varientList == null || varientList.size() == 0) {
                            no_products.setVisibility(View.VISIBLE);
                            products_grid.setVisibility(View.GONE);
                        } else {
                            if (productVarients.getData().size() > 6)
                                for (int i = 0; i < 6; i++)
                                    varientList.add(productVarients.getData().get(i));
                            else
                                varientList = productVarients.getData();
                            getAllOptionsVarient();
                            no_products.setVisibility(View.GONE);
                            products_grid.setVisibility(View.VISIBLE);
                            products_grid.setLayoutManager(new CommonUtils(SearchPrdouctActivity.this).getProductGridLayoutManager());
                            productAdapter = new ProductsAdapter(SearchPrdouctActivity.this, varientList, null, "grid", alloptionsAttributeArrayList, alldifferentVarientList);
                            products_grid.setAdapter(productAdapter);
                            productAdapter.notifyDataSetChanged();
                        }

                    } else {
                        Log.e("TAG", "onResponse: " + response);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<VarientsByCategory> call, @NonNull Throwable t) {
                    shimmerView2.stopShimmerAnimation();
                    shimmerView2.setVisibility(View.GONE);
                    no_products.setVisibility(View.VISIBLE);
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
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
