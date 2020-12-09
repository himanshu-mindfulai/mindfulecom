package com.mindfulai.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mindfulai.Activites.AllProductsActivity;
import com.mindfulai.Activites.SeeAllCategoryActivity;
import com.mindfulai.Adapter.BrandsAdapter;
import com.mindfulai.Adapter.CategoriesAdapter;
import com.mindfulai.Adapter.MainSliderAdapter;
import com.mindfulai.Adapter.ProductsAdapter;
import com.mindfulai.Adapter.SubCategoriesAdapter;
import com.mindfulai.Models.BannerInfoData.BannerData;
import com.mindfulai.Models.BannerInfoData.BannerInfo;
import com.mindfulai.Models.Brand;
import com.mindfulai.Models.BrandModel;
import com.mindfulai.Models.DifferentVarients;
import com.mindfulai.Models.SubcategoryModel.SubcategoryModel;
import com.mindfulai.Models.categoryData.CategoryInfo;
import com.mindfulai.Models.categoryData.Datum;
import com.mindfulai.Models.varientsByCategory.Images;
import com.mindfulai.Models.varientsByCategory.OptionsAttribute;
import com.mindfulai.Models.varientsByCategory.VarientsByCategory;
import com.mindfulai.NetworkRetrofit.ApiService;
import com.mindfulai.NetworkRetrofit.ApiUtils;
import com.mindfulai.Utils.CommonUtils;
import com.mindfulai.Utils.SPData;
import com.mindfulai.customclass.PicassoImageLoadingService;
import com.mindfulai.ministore.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ss.com.bannerslider.Slider;
import ss.com.bannerslider.event.OnSlideClickListener;

import static android.app.Activity.RESULT_OK;
import static android.view.View.GONE;

public class HomeFragment extends Fragment {

    private Slider banner_slider1, banner_slider2;
    private MainSliderAdapter mainSliderAdapter;
    private List<BannerInfo> bannerImages_List = new ArrayList<>();
    private List<BannerInfo> bannerImages_List2 = new ArrayList<>();
    private List<Datum> categoryList = new ArrayList<>();
    private CategoriesAdapter categoriesAdapter;
    private TextView tvProductText, tvServicesText;
    private RecyclerView rv_categories;
    private RecyclerView productGrid;
    private RecyclerView recyclerViewBrand;
    private ShimmerFrameLayout shimmer_view_container, shimmerView2;
    private ProductsAdapter bestSellingProductAdpater;
    private List<com.mindfulai.Models.varientsByCategory.Datum> sublist;
    private ArrayList<ArrayList<OptionsAttribute>> alloptionsAttributeArrayList;
    private static final String TAG = "HomeFragment";
    private ArrayList<ArrayList<DifferentVarients>> alldifferentVarientList;
    private SwipeRefreshLayout swipeContainer;
    private FloatingActionButton floatingActionButton;
    private FrameLayout flfabSubmenu;
    private FloatingActionButton fabCall;
    private FloatingActionButton fabWhatsapp;
    private boolean fabExpanded = false;
    private ArrayList<Brand> brandArrayList;

    @SuppressLint("SetTextI18n")
    public View onCreateView(@NonNull LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        try {
            GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 1);
            gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            TextView textNearBy = root.findViewById(R.id.best_seeling_text);
            TextView seeAllCategory = root.findViewById(R.id.see_all_categories);
            tvProductText = root.findViewById(R.id.product_text);
            tvServicesText = root.findViewById(R.id.services_text);
            floatingActionButton = root.findViewById(R.id.floatingActionButton);
            flfabSubmenu = root.findViewById(R.id.fab_submenu);
            fabCall = root.findViewById(R.id.fab_call);
            fabWhatsapp = root.findViewById(R.id.fab_whatsapp);
            recyclerViewBrand = root.findViewById(R.id.topbrandsRecyclerView);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
            linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
            recyclerViewBrand.setLayoutManager(linearLayoutManager);
            hideFabSubmenu();
            if(SPData.showBottomNavMenu()){
                seeAllCategory.setVisibility(View.GONE);
            }else
                seeAllCategory.setVisibility(View.VISIBLE);
            if(SPData.hideContactBtn()){
                floatingActionButton.setVisibility(View.GONE);
            }else{
                floatingActionButton.setVisibility(View.VISIBLE);
            }
            floatingActionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //sendToWhatsApp();
                    if(fabExpanded){
                        hideFabSubmenu();
                    } else {
                        showFabSubmenu();
                    }
                }
            });
            fabCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendToPhone();
                }
            });
            fabWhatsapp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendToWhatsApp();
                }
            });
            seeAllCategory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivityForResult(new Intent(getActivity(), SeeAllCategoryActivity.class), 10);
                }
            });
            banner_slider1 = root.findViewById(R.id.banner_slider1);
            banner_slider2 = root.findViewById(R.id.banner_slider2);
            rv_categories = root.findViewById(R.id.rv_categories);
            shimmer_view_container = root.findViewById(R.id.shimmer_view_container);
            shimmerView2 = root.findViewById(R.id.shimmerView2);

            productGrid = root.findViewById(R.id.productGrid);
            sublist = new ArrayList<>();
            alloptionsAttributeArrayList = new ArrayList<>();
            alldifferentVarientList = new ArrayList<>();
            brandArrayList = new ArrayList<>();

            if (SPData.showServicesTab()){
                tvProductText.setTextColor(getResources().getColor(R.color.black));
                tvProductText.setTextSize(18);
                tvProductText.setTypeface(Typeface.DEFAULT_BOLD);
                tvServicesText.setTextColor(getResources().getColor(R.color.et_text_color_darkgrey));
                tvServicesText.setTextSize(14);
                tvServicesText.setTypeface(null);
                SPData.setProductsOrServices("PRODUCTS");
                tvProductText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        tvProductText.setTextColor(getResources().getColor(R.color.black));
                        tvProductText.setTextSize(18);
                        tvProductText.setTypeface(Typeface.DEFAULT_BOLD);
                        tvServicesText.setTextColor(getResources().getColor(R.color.et_text_color_darkgrey));
                        tvServicesText.setTextSize(14);
                        tvServicesText.setTypeface(null);
                        getProductCategories();
                        SPData.setProductsOrServices("PRODUCTS");
                    }
                });

                tvServicesText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        tvServicesText.setTextColor(getResources().getColor(R.color.black));
                        tvServicesText.setTextSize(18);
                        tvServicesText.setTypeface(Typeface.DEFAULT_BOLD);
                        tvProductText.setTextColor(getResources().getColor(R.color.et_text_color_darkgrey));
                        tvProductText.setTextSize(14);
                        tvProductText.setTypeface(null);
                        getServicesCategories();
                        SPData.setProductsOrServices("SERVICES");
                    }
                });
            } else {
                tvProductText.setText("Top categories");
                tvProductText.setTextColor(getResources().getColor(R.color.et_text_color_darkgrey));
                tvProductText.setTextSize(18);
                tvProductText.setTypeface(Typeface.DEFAULT_BOLD);
                tvServicesText.setVisibility(View.GONE);
            }

            if (SPData.showProductsAndCart()){
                tvProductText.setVisibility(View.VISIBLE);
                tvServicesText.setText("Services");
                SPData.setProductsOrServices("PRODUCTS");
                getProductCategories();
            } else {
                tvProductText.setVisibility(View.GONE);
                tvServicesText.setText("Top categories");
                SPData.setProductsOrServices("SERVICES");
                getServicesCategories();
            }

            productGrid.setLayoutManager(new CommonUtils(getActivity()).getProductGridLayoutManager());
            rv_categories.setLayoutManager(new GridLayoutManager(getActivity(), 3));
            Slider.init(new PicassoImageLoadingService(requireActivity().getApplicationContext()));
            getBrands();
            getBanners();
            //getProductCategories();
            getBanners2();
            if (!SPData.getShowVendor()) {
                getAllTrending();
            } else {
                shimmerView2.stopShimmerAnimation();
                shimmerView2.setVisibility(View.GONE);
            }
            swipeContainer = (SwipeRefreshLayout) root.findViewById(R.id.swipeContainer);
            swipeContainer.setEnabled(false);
            swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    try {
                        getProductCategories();
                        getAllTrending();
                    } catch (Exception e) {
                        Log.e(TAG, "onRefresh: " + e);
                    }
                }
            });
            swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                    android.R.color.holo_green_light,
                    android.R.color.holo_orange_light,
                    android.R.color.holo_red_light);
        } catch (Exception e) {
            Log.e(TAG, "onCreateView: " + e);
        }
        return root;
    }
    private void getBrands() {
        try {
            ApiService apiService = ApiUtils.getHeaderAPIService();

            apiService.getAllBrand().enqueue(new Callback<BrandModel>() {
                @Override
                public void onResponse(@NonNull Call<BrandModel> call, @NonNull Response<BrandModel> response) {

                    if (response.isSuccessful()) {
                        brandArrayList.clear();
                        BrandModel brandModel = response.body();
                        assert brandModel != null;
                        if (brandModel.getData().size() > 0) {
                            brandArrayList = brandModel.getData();
                            BrandsAdapter subCategoriesAdapter = new BrandsAdapter(getActivity(), brandArrayList);
                            recyclerViewBrand.setAdapter(subCategoriesAdapter);
                            subCategoriesAdapter.notifyDataSetChanged();
                        } else
                            recyclerViewBrand.setVisibility(GONE);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<BrandModel> call, @NonNull Throwable t) {
                    Log.e(TAG, call.toString());
                    Toast.makeText(getActivity(), "Failed to connedt", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void hideFabSubmenu() {
        flfabSubmenu.setVisibility(View.GONE);
        floatingActionButton.setImageResource(R.drawable.ic_baseline_contact_support_30);
        fabExpanded = false;
    }

    private void showFabSubmenu() {
        flfabSubmenu.setVisibility(View.VISIBLE);
        floatingActionButton.setImageResource(R.drawable.ic_baseline_close_24);
        fabExpanded = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (SPData.getAppPreferences().getPaymentSuccess()) {
            try {
                getAllTrending();
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "onResume: " + e);
            }
        }
    }

    private void sendToWhatsApp() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://api.whatsapp.com/send?phone=" + SPData.whatsAppNumber() + "&text=&source=&data="));
        if (intent.resolveActivity(requireActivity().getPackageManager()) != null)
            startActivity(intent);
    }

    private void sendToPhone() {
        startActivity(
                new Intent(
                        Intent.ACTION_DIAL,
                        Uri.parse("tel:" + SPData.supportCallNumber())
                )
        );
    }

    private void getBanners() {
        bannerImages_List.clear();
        banner_slider1.recyclerView.getRecycledViewPool().clear();
        ApiService apiService = ApiUtils.getHeaderAPIService();
        apiService.getBannerData().enqueue(new Callback<BannerData>() {
            @Override
            public void onResponse(@NonNull Call<BannerData> call, @NonNull Response<BannerData> response) {
                Log.e(TAG, "onResponse: " + response.body());
                if (response.isSuccessful()) {
                    BannerData bannerData = response.body();
                    assert bannerData != null;
                    bannerImages_List = bannerData.getData();
                    setupViews();
                }
            }

            @Override
            public void onFailure(@NonNull Call<BannerData> call, @NonNull Throwable t) {
                Log.e("fail", call.toString());
                Toast.makeText(SPData.getAppContext(), "Failed to connect", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getBanners2() {
        bannerImages_List2.clear();
        banner_slider2.recyclerView.getRecycledViewPool().clear();
        ApiService apiService = ApiUtils.getHeaderAPIService();
        apiService.getBannerData2().enqueue(new Callback<BannerData>() {
            @Override
            public void onResponse(@NonNull Call<BannerData> call, @NonNull Response<BannerData> response) {

                if (response.isSuccessful()) {
                    BannerData bannerData = response.body();
                    assert bannerData != null;
                    bannerImages_List2 = bannerData.getData();
                    setupViews2(bannerImages_List2);
                }

            }

            @Override
            public void onFailure(@NonNull Call<BannerData> call, @NonNull Throwable t) {
                Log.e("fail", call.toString());
                Toast.makeText(SPData.getAppContext(), "Failed to connect", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupViews2(final List<BannerInfo> bannerImages_list2) {
        mainSliderAdapter = new MainSliderAdapter(getActivity(), bannerImages_list2);
        banner_slider2.setAdapter(mainSliderAdapter);
        banner_slider2.setInterval(5000);
        banner_slider2.setOnSlideClickListener(new OnSlideClickListener() {
            @Override
            public void onSlideClick(int position) {
                if (position<0) position+=2;
                Log.i(TAG , "setupViews2 " + bannerImages_list2.size() + " " + position);
                try{
                    if (bannerImages_list2.get(position).getTarget() != null) {
                        if (bannerImages_list2.get(position).getType().equals("product_category")) {
                            Intent i = new Intent(getActivity(), AllProductsActivity.class);
                            i.putExtra("category_id", bannerImages_list2.get(position).getTarget().getId());
                            i.putExtra("categoryName", bannerImages_list2.get(position).getTarget().getFullName());
                            startActivityForResult(i, 2);
                        }
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    private void getServicesCategories() {
        categoryList.clear();
        rv_categories.getRecycledViewPool().clear();
        if (categoriesAdapter != null)
            categoriesAdapter.notifyDataSetChanged();
        shimmer_view_container.startShimmerAnimation();
        ApiService apiService = ApiUtils.getHeaderAPIService();
        apiService.getAllServiceCategory().enqueue(new Callback<CategoryInfo>() {
            @Override
            public void onResponse(@NonNull Call<CategoryInfo> call, @NonNull Response<CategoryInfo> response) {

                shimmer_view_container.stopShimmerAnimation();
                shimmer_view_container.setVisibility(View.GONE);
                rv_categories.setVisibility(View.VISIBLE);

                if (response.isSuccessful()) {
                    categoryList.clear();
                    CategoryInfo categoryInfo = response.body();
                    assert categoryInfo != null;
                    categoryList = categoryInfo.getData();
                    if (categoryList.size() > 0) {
                        if (categoryList.size() <=  SPData.noOfCategories())
                            categoriesAdapter = new CategoriesAdapter(getContext(), categoryList, "grid");
                        else
                            categoriesAdapter = new CategoriesAdapter(getContext(), categoryList.subList(0, SPData.noOfCategories()), "grid");
                        rv_categories.setAdapter(categoriesAdapter);
                        categoriesAdapter.notifyDataSetChanged();
                    }
                    if (swipeContainer != null)
                        swipeContainer.setRefreshing(false);

                } else {
                    if (swipeContainer != null)
                        swipeContainer.setRefreshing(false);
                    Toast.makeText(getContext(), "Something went wrong !!", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(@NonNull Call<CategoryInfo> call, @NonNull Throwable t) {
                Log.e("fail", call.toString());
                shimmer_view_container.stopShimmerAnimation();
                shimmer_view_container.setVisibility(View.GONE);
                if (swipeContainer != null)
                    swipeContainer.setRefreshing(false);
            }
        });
    }

    private void getProductCategories() {
        categoryList.clear();
        rv_categories.getRecycledViewPool().clear();
        if (categoriesAdapter != null)
            categoriesAdapter.notifyDataSetChanged();
        shimmer_view_container.startShimmerAnimation();
        ApiService apiService = ApiUtils.getHeaderAPIService();
        apiService.getAllProductCategory().enqueue(new Callback<CategoryInfo>() {
            @Override
            public void onResponse(@NonNull Call<CategoryInfo> call, @NonNull Response<CategoryInfo> response) {

                shimmer_view_container.stopShimmerAnimation();
                shimmer_view_container.setVisibility(View.GONE);
                rv_categories.setVisibility(View.VISIBLE);

                if (response.isSuccessful()) {
                    categoryList.clear();
                    CategoryInfo categoryInfo = response.body();
                    assert categoryInfo != null;
                    categoryList = categoryInfo.getData();
                    if (categoryList.size() > 0) {
                        if (categoryList.size() <=  SPData.noOfCategories())
                            categoriesAdapter = new CategoriesAdapter(getContext(), categoryList, "grid");
                        else
                            categoriesAdapter = new CategoriesAdapter(getContext(), categoryList.subList(0, SPData.noOfCategories()), "grid");
                        rv_categories.setAdapter(categoriesAdapter);
                        categoriesAdapter.notifyDataSetChanged();
                    }
                    if (swipeContainer != null)
                        swipeContainer.setRefreshing(false);

                } else {
                    if (swipeContainer != null)
                        swipeContainer.setRefreshing(false);
                    Toast.makeText(getContext(), "Something went wrong !!", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(@NonNull Call<CategoryInfo> call, @NonNull Throwable t) {
                Log.e("fail", call.toString());
                shimmer_view_container.stopShimmerAnimation();
                shimmer_view_container.setVisibility(View.GONE);
                if (swipeContainer != null)
                    swipeContainer.setRefreshing(false);
            }
        });
    }

    private void getAllTrending() {

        sublist.clear();
        productGrid.getRecycledViewPool().clear();
        if (bestSellingProductAdpater != null)
            bestSellingProductAdpater.notifyDataSetChanged();
        shimmerView2.startShimmerAnimation();
        ApiService apiService;
        if (!SPData.getAppPreferences().getUsertoken().equals(""))
            apiService = ApiUtils.getHeaderAPIService(SPData.getAppPreferences().getUsertoken());
        else
            apiService = ApiUtils.getAPIService();

        apiService.getAllTrending().enqueue(new Callback<VarientsByCategory>() {
            @Override
            public void onResponse(@NonNull Call<VarientsByCategory> call, @NonNull Response<VarientsByCategory> response) {

                if (response.isSuccessful()) {
                    shimmerView2.stopShimmerAnimation();
                    shimmerView2.setVisibility(View.GONE);
                    productGrid.setVisibility(View.VISIBLE);
                    VarientsByCategory productVarients = response.body();
                    if (productVarients != null && productVarients.getData().size() > 0) {
                        if (getActivity() != null) {
                            if (productVarients.getData().size() > 6) {
                                for (int i = 0; i < 6; i++) {
                                    sublist.add(productVarients.getData().get(i));
                                }
                                getAllOptionAndVarient(sublist);
                            } else {
                                getAllOptionAndVarient(productVarients.getData());
                            }
                        }
                    }
                } else {
                    Log.i("TAG", "onResponse: " + response);
                    if (swipeContainer != null)
                        swipeContainer.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(@NonNull Call<VarientsByCategory> call, @NonNull Throwable t) {
                Log.e("fail", call.toString());
                shimmerView2.stopShimmerAnimation();
                shimmerView2.setVisibility(View.GONE);
                if (swipeContainer != null)
                    swipeContainer.setRefreshing(false);
            }
        });

    }

    private void getAllOptionAndVarient(List<com.mindfulai.Models.varientsByCategory.Datum> sublist) {
        for (int position = 0; position < sublist.size(); position++) {
            ArrayList<OptionsAttribute> optionsAttributeArrayList = new ArrayList<>();
            for (int j = 0; j < sublist.get(position).getAttributes().size(); j++) {
                ArrayList<String> option_value = new ArrayList<>();
                for (int k = 0; k < sublist.get(position).getAttributes().get(j).getOption().size(); k++) {
                    option_value.add(k, sublist.get(position).getAttributes().get(j).getOption().get(k).getValue());
                }
                optionsAttributeArrayList.add(new OptionsAttribute(sublist.get(position).getAttributes().get(j).getAttribute().getName(), option_value));
            }
            alloptionsAttributeArrayList.add(optionsAttributeArrayList);
        }
        for (int position = 0; position < sublist.size(); position++) {
            ArrayList<DifferentVarients> differentVarientsArrayList = new ArrayList<>();
            for (int k = 0; k < sublist.get(position).getVarients().size(); k++) {
                ArrayList<String> varient_option_value = new ArrayList<>();
                for (int j = 0; j < sublist.get(position).getVarients().get(k).getAttributes().size(); j++)
                    varient_option_value.add(sublist.get(position).getVarients().get(k).getAttributes().get(j).getOption().getValue());
                String sellinPrice = "";
                if (sublist.get(position).getVarients().get(k).getSellingPrice() != 0.0)
                    sellinPrice = "" + sublist.get(position).getVarients().get(k).getSellingPrice();
                else
                    sellinPrice = "";
                int minQty = -1;
                if (sublist.get(position).getVarients().get(k).getMinOrderQuantity() != null)
                    minQty = sublist.get(position).getVarients().get(k).getMinOrderQuantity();
                ArrayList<String> listOfImages = new ArrayList<>();
                Images images = sublist.get(position).getVarients().get(k).getImages();
                if (images != null && images.getPrimary() != null)
                    listOfImages.add(images.getPrimary());
                if (images != null && images.getSecondary() != null)
                    listOfImages.add(images.getSecondary());
                differentVarientsArrayList.add(new DifferentVarients("" + sublist.get(position).getVarients().get(k).getPrice(), varient_option_value, sublist.get(position).getVarients().get(k).getId(), sublist.get(position).getVarients().get(k).getDescription(), "" + sublist.get(position).getVarients().get(k).getStock(), sellinPrice, minQty,listOfImages));

            }
            alldifferentVarientList.add(differentVarientsArrayList);
        }
        bestSellingProductAdpater = new ProductsAdapter(getContext(), sublist, null, "grid", alloptionsAttributeArrayList, alldifferentVarientList);
        productGrid.setAdapter(bestSellingProductAdpater);
        bestSellingProductAdpater.notifyDataSetChanged();
        if (swipeContainer != null)
            swipeContainer.setRefreshing(false);
    }


    private void setupViews() {
        try {
            mainSliderAdapter = new MainSliderAdapter(getActivity(), bannerImages_List);
            banner_slider1.setAdapter(mainSliderAdapter);
            banner_slider1.setInterval(3000);
            banner_slider1.setOnSlideClickListener(new OnSlideClickListener() {
                @Override
                public void onSlideClick(int position) {
                    if (position<0)
                        position+=2;
                    Log.i(TAG, "setupViews: " + bannerImages_List.size() + " " + position);
                    if (bannerImages_List.get(position).getTarget() != null) {
                        Log.i(TAG, "setupViews: " + position);
                        if (bannerImages_List.get(position).getType().equals("product_category")) {
                            Intent i = new Intent(getActivity(), AllProductsActivity.class);
                            i.putExtra("category_id", bannerImages_List.get(position).getTarget().getId());
                            i.putExtra("categoryName", bannerImages_List.get(position).getTarget().getFullName());
                            startActivityForResult(i, 2);
                        }
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "setupViews: " + e);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null && data.getStringExtra("payment_success") != null && data.getStringExtra("payment_success").equals("true")) {
            getAllTrending();
        }
    }
}