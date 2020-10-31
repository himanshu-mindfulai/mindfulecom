package com.mindfulai.Activites;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mindfulai.Adapter.CategoriesAdapter;
import com.mindfulai.Models.categoryData.CategoryInfo;
import com.mindfulai.Models.categoryData.Datum;
import com.mindfulai.NetworkRetrofit.ApiService;
import com.mindfulai.NetworkRetrofit.ApiUtils;
import com.mindfulai.Utils.CommonUtils;
import com.mindfulai.Utils.SPData;
import com.mindfulai.ministore.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SeeAllCategoryActivity extends AppCompatActivity {
    private RecyclerView rv_categories;
    private ProgressBar progressBar;
    private List<Datum> categoryList;
    private CategoriesAdapter categoriesAdapter;
    private Intent intent;
    private String currentType = "grid";
    private MenuItem list, grid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_all_category);
        getSupportActionBar().setTitle("All Categories");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        intent = new Intent();
        progressBar = findViewById(R.id.progressAllCategories);
        categoryList = new ArrayList<>();
        rv_categories = findViewById(R.id.rv_categories);
        rv_categories.setLayoutManager(new CommonUtils(SeeAllCategoryActivity.this).getCategoriesGridLayoutManager());
        try{
            if(SPData.getProductsOrServices().equals("PRODUCTS"))
                getCategories();
            else
                getServiceCategories();
        } catch(Exception e){
            Log.e("SeeAllCatAct", e.getMessage());
            getCategories();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.browse_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
//        MenuItem item = menu.findItem(R.id.item_notification);
        list = menu.findItem(R.id.navigation_list_view);
        grid = menu.findItem(R.id.navigation_grid_view);
        menu.findItem(R.id.search).setVisible(false);
        menu.findItem(R.id.item_notification).setVisible(false);
        menu.findItem(R.id.sort).setVisible(false);
//        View cartBadge = item.getActionView().findViewById(R.id.notification_badge);
//        cartBadge.setVisibility(View.GONE);
        if (currentType.equals("grid")) {
            list.setVisible(true);
            grid.setVisible(false);
        } else {
            grid.setVisible(true);
            list.setVisible(false);
        }
        return true;
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

    private void showListView() {

        categoriesAdapter = new CategoriesAdapter(SeeAllCategoryActivity.this, categoryList, "list");
        LinearLayoutManager manager = new LinearLayoutManager(SeeAllCategoryActivity.this);
        manager.setOrientation(RecyclerView.VERTICAL);
        rv_categories.setLayoutManager(manager);
        rv_categories.setAdapter(categoriesAdapter);
        currentType = "list";
    }

    private void showGridView() {
        categoriesAdapter = new CategoriesAdapter(SeeAllCategoryActivity.this, categoryList, "grid");
        rv_categories.setLayoutManager(new CommonUtils(SeeAllCategoryActivity.this).getCategoriesGridLayoutManager());
        rv_categories.setAdapter(categoriesAdapter);
        currentType = "grid";
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else if (item.getItemId() == R.id.navigation_list_view) {
            showListView();
            list.setVisible(false);
            grid.setVisible(true);

        } else if (item.getItemId() == R.id.navigation_grid_view) {
            showGridView();
            grid.setVisible(false);
            list.setVisible(true);
        }
        return super.onOptionsItemSelected(item);
    }

    private void getServiceCategories() {
        ApiService apiService = ApiUtils.getHeaderAPIService();

        apiService.getAllServiceCategory().enqueue(new Callback<CategoryInfo>() {
            @Override
            public void onResponse(@NonNull Call<CategoryInfo> call, @NonNull Response<CategoryInfo> response) {
                progressBar.setVisibility(View.GONE);
                rv_categories.setVisibility(View.VISIBLE);
                if (response.isSuccessful()) {
                    categoryList.clear();
                    CategoryInfo categoryInfo = response.body();
                    assert categoryInfo != null;
                    categoryList = categoryInfo.getData();
                    categoriesAdapter = new CategoriesAdapter(SeeAllCategoryActivity.this, categoryList, "grid");
                    rv_categories.setAdapter(categoriesAdapter);
                    categoriesAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(SeeAllCategoryActivity.this, "Something went wrong !!", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(@NonNull Call<CategoryInfo> call, @NonNull Throwable t) {
                Toast.makeText(SeeAllCategoryActivity.this, "Failed to connect", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void getCategories() {
        ApiService apiService = ApiUtils.getHeaderAPIService();

        apiService.getAllProductCategory().enqueue(new Callback<CategoryInfo>() {
            @Override
            public void onResponse(@NonNull Call<CategoryInfo> call, @NonNull Response<CategoryInfo> response) {
                progressBar.setVisibility(View.GONE);
                rv_categories.setVisibility(View.VISIBLE);
                if (response.isSuccessful()) {
                    categoryList.clear();
                    CategoryInfo categoryInfo = response.body();
                    assert categoryInfo != null;
                    categoryList = categoryInfo.getData();
                    categoriesAdapter = new CategoriesAdapter(SeeAllCategoryActivity.this, categoryList, "grid");
                    rv_categories.setAdapter(categoriesAdapter);
                    categoriesAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(SeeAllCategoryActivity.this, "Something went wrong !!", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(@NonNull Call<CategoryInfo> call, @NonNull Throwable t) {
                Toast.makeText(SeeAllCategoryActivity.this, "Failed to connect", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }
}