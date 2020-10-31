package com.mindfulai.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mindfulai.Activites.SeeAllCategoryActivity;
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

public class AllCategoriesFragment extends Fragment {
    private RecyclerView rv_categories;
    private ProgressBar progressBar;
    private List<Datum> categoryList;
    private CategoriesAdapter categoriesAdapter;
    private String currentType = "grid";
    private MenuItem list, grid;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_see_all_category, container, false);
        progressBar = view.findViewById(R.id.progressAllCategories);
        categoryList = new ArrayList<>();
        rv_categories = view.findViewById(R.id.rv_categories);
        rv_categories.setLayoutManager(new CommonUtils(getActivity()).getCategoriesGridLayoutManager());
        try{
            if(SPData.getProductsOrServices().equals("PRODUCTS"))
                getCategories();
            else
                getServiceCategories();
        } catch(Exception e){
            Log.e("SeeAllCatAct", e.getMessage());
            getCategories();
        }
        return view;
    }
    private void showListView() {

        categoriesAdapter = new CategoriesAdapter(getActivity(), categoryList, "list");
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(RecyclerView.VERTICAL);
        rv_categories.setLayoutManager(manager);
        rv_categories.setAdapter(categoriesAdapter);
        currentType = "list";
    }

    private void showGridView() {
        categoriesAdapter = new CategoriesAdapter(getActivity(), categoryList, "grid");
        rv_categories.setLayoutManager(new CommonUtils(getActivity()).getCategoriesGridLayoutManager());
        rv_categories.setAdapter(categoriesAdapter);
        currentType = "grid";
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
                    categoriesAdapter = new CategoriesAdapter(getActivity(), categoryList, "grid");
                    rv_categories.setAdapter(categoriesAdapter);
                    categoriesAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getActivity(), "Something went wrong !!", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(@NonNull Call<CategoryInfo> call, @NonNull Throwable t) {
                Toast.makeText(getActivity(), "Failed to connect", Toast.LENGTH_SHORT).show();
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
                    categoriesAdapter = new CategoriesAdapter(getActivity(), categoryList, "grid");
                    rv_categories.setAdapter(categoriesAdapter);
                    categoriesAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getActivity(), "Something went wrong !!", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(@NonNull Call<CategoryInfo> call, @NonNull Throwable t) {
                Toast.makeText(getActivity(), "Failed to connect", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }
}
