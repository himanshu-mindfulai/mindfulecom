package com.mindfulai.Activites;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.mindfulai.Adapter.CategoryBannerSliderAdapter;
import com.mindfulai.Adapter.MainSliderAdapter;
import com.mindfulai.Adapter.ProductsAdapter;
import com.mindfulai.Adapter.SubCategoriesAdapter;
import com.mindfulai.Models.BannerInfoData.BannerCategoryData;
import com.mindfulai.Models.BannerInfoData.BannerData;
import com.mindfulai.Models.BannerInfoData.BannerInfo;
import com.mindfulai.Models.BannerInfoData.CategoryBannerData;
import com.mindfulai.Models.DifferentVarients;
import com.mindfulai.Models.SubcategoryModel.Datum;
import com.mindfulai.Models.SubcategoryModel.SubcategoryModel;
import com.mindfulai.Models.varientsByCategory.Images;
import com.mindfulai.Models.varientsByCategory.OptionsAttribute;
import com.mindfulai.Models.varientsByCategory.VarientsByCategory;
import com.mindfulai.NetworkRetrofit.ApiService;
import com.mindfulai.NetworkRetrofit.ApiUtils;
import com.mindfulai.Utils.SPData;
import com.mindfulai.ministore.R;
import com.mindfulai.ui.RangeSeekBar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.nikartm.support.ImageBadgeView;
import ss.com.bannerslider.Slider;
import ss.com.bannerslider.event.OnSlideClickListener;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.mindfulai.Utils.CommonUtils.capitalizeWord;

public class AllProductsActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private static final String TAG = "AllProductsActivity";
    private String CATEGORY_ID;
    private String VENDOR_ID;
    private List<Datum> subcategoryList = new ArrayList<>();
    private RecyclerView products_grid;
    private LinearLayout no_products;
    private ProductsAdapter productAdapter;
    private ShimmerFrameLayout shimmerView2;
    private List<com.mindfulai.Models.varientsByCategory.Datum> varientList;
    private ArrayList<ArrayList<OptionsAttribute>> alloptionsAttributeArrayList;
    private ArrayList<ArrayList<DifferentVarients>> alldifferentVarientList;
    private VarientsByCategory productVarients;
    private ImageBadgeView cartBadge;
    private RecyclerView subCategoriesRecyclerView;
    private int no_of_products = 6;
    private int scrollPosition = 0;
    private SwipeRefreshLayout swipeContainer;
    boolean checkScrollingUp = false;
    private int level;
    private Slider banner_slider;
    private List<CategoryBannerData> bannerImages_List = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_products);
        try {
            level = getIntent().getIntExtra("level", 1);
            CATEGORY_ID = getIntent().getStringExtra("category_id");
            String category_name = getIntent().getStringExtra("categoryName");
            VENDOR_ID = getIntent().getStringExtra("vendor_id");
            Toolbar toolbar = findViewById(R.id.all_product_toolbar);
            subCategoriesRecyclerView = findViewById(R.id.subcategoriesLyout);
            banner_slider = findViewById(R.id.banner_slider);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(AllProductsActivity.this);
            linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
            subCategoriesRecyclerView.setLayoutManager(linearLayoutManager);
            setSupportActionBar(toolbar);
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_white);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            assert category_name != null;
            toolbar.setTitle(capitalizeWord(category_name));
            toolbar.setTitleTextColor(getResources().getColor(R.color.colorWhite));
            shimmerView2 = findViewById(R.id.shimmerView2);
            products_grid = findViewById(R.id.products_grid);
            no_products = findViewById(R.id.no_products);
            varientList = new ArrayList<>();
            alloptionsAttributeArrayList = new ArrayList<>();
            alldifferentVarientList = new ArrayList<>();
            getSubCategories(CATEGORY_ID);

            if(SPData.showAllProductsBanner())
                   getBanner();
            else
                banner_slider.setVisibility(GONE);
            products_grid.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                }

                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    if (dy > 0) {
                        if(checkScrollingUp) {
                            checkScrollingUp = false;
                        }
                    } else {
                        if(!checkScrollingUp ) {
                            checkScrollingUp = true;

                        }
                    }
                }
            });
            swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
            swipeContainer.setEnabled(false);
            swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    getProductsVarients();
                    ValueAnimator anim = ValueAnimator.ofInt(subCategoriesRecyclerView.getMeasuredHeight(), 250);
                    anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator valueAnimator) {
                            int val = (Integer) valueAnimator.getAnimatedValue();
                            ViewGroup.LayoutParams layoutParams = subCategoriesRecyclerView.getLayoutParams();
                            layoutParams.height = val;
                            subCategoriesRecyclerView.setLayoutParams(layoutParams);
                        }
                    });
                    anim.setDuration(100);
                    anim.start();
                }
            });
            swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                    android.R.color.holo_green_light,
                    android.R.color.holo_orange_light,
                    android.R.color.holo_red_light);
        } catch (Exception e) {
            Log.e("TAG", "onCreate: " + e);
            e.printStackTrace();
        }
    }

    private void getBanner() {
        bannerImages_List.clear();
        banner_slider.recyclerView.getRecycledViewPool().clear();
        ApiService apiService = ApiUtils.getHeaderAPIService();
        apiService.getCategoryBannerData(CATEGORY_ID).enqueue(new Callback<BannerCategoryData>() {
            @Override
            public void onResponse(@NonNull Call<BannerCategoryData> call, @NonNull Response<BannerCategoryData> response) {
                if (response.isSuccessful()&&response.body()!=null&&response.body().getData().size()>0) {
                    BannerCategoryData bannerData = response.body();
                    assert bannerData != null;
                    bannerImages_List = bannerData.getData();
                    setupViews();
                }else {
                    banner_slider.setVisibility(GONE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<BannerCategoryData> call, @NonNull Throwable t) {
                Log.e("fail", call.toString());
                banner_slider.setVisibility(GONE);
                Toast.makeText(SPData.getAppContext(), "Failed to connect", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void setupViews() {
        try {
            CategoryBannerSliderAdapter categoryBannerSliderAdapter = new CategoryBannerSliderAdapter(AllProductsActivity.this, bannerImages_List);
            banner_slider.setAdapter(categoryBannerSliderAdapter);
            banner_slider.setInterval(3000);
            banner_slider.setOnSlideClickListener(new OnSlideClickListener() {
                @Override
                public void onSlideClick(int position) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "setupViews: " + e);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        int total = SPData.getAppPreferences().getTotalCartCount();
        if(cartBadge!= null)
            cartBadge.setBadgeValue(total);
    }


    public void addBadge(String count) {
        cartBadge.setBadgeValue(Integer.parseInt(count));
    }

    public void removeBadge() {
        cartBadge.setBadgeValue(0);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        if(item.getItemId() == R.id.sort){
            View dialogView = getLayoutInflater().inflate(R.layout.bottom_sheet_sort, null);
            final BottomSheetDialog dialog = new BottomSheetDialog(AllProductsActivity.this);
            dialog.setContentView(dialogView);

            TextView sortPriceHtoL, sortPriceLtoH;

            sortPriceHtoL = dialogView.findViewById(R.id.tv_sort_price_h_to_l);
            sortPriceLtoH = dialogView.findViewById(R.id.tv_sort_price_l_to_h);

            sortPriceHtoL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Collections.sort(varientList, new Comparator<com.mindfulai.Models.varientsByCategory.Datum>() {
                        @Override
                        public int compare(com.mindfulai.Models.varientsByCategory.Datum o1, com.mindfulai.Models.varientsByCategory.Datum o2) {
                            return (int) (o2.getVarients().get(0).getPrice() - o1.getVarients().get(0).getPrice());
                        }
                    });
                    Log.e(TAG, "onCreate: " + Arrays.toString(varientList.toArray()));
                    String type="grid";
                    if(!SPData.showGridView())
                        type="list";
                    productAdapter = new ProductsAdapter(AllProductsActivity.this, varientList, null, type, alloptionsAttributeArrayList, alldifferentVarientList);
                    products_grid.setAdapter(productAdapter);
                    productAdapter.notifyDataSetChanged();
                    dialog.cancel();
                }
            });

            sortPriceLtoH.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Collections.sort(varientList, new Comparator<com.mindfulai.Models.varientsByCategory.Datum>() {
                        @Override
                        public int compare(com.mindfulai.Models.varientsByCategory.Datum o1, com.mindfulai.Models.varientsByCategory.Datum o2) {
                            return (int) (o1.getVarients().get(0).getPrice() - o2.getVarients().get(0).getPrice());
                        }
                    });
                    Log.e(TAG, "onCreate: " + Arrays.toString(varientList.toArray()));
                    String type="grid";
                    if(!SPData.showGridView())
                        type="list";
                    productAdapter = new ProductsAdapter(AllProductsActivity.this, varientList, null, type, alloptionsAttributeArrayList, alldifferentVarientList);
                    products_grid.setAdapter(productAdapter);
                    productAdapter.notifyDataSetChanged();
                    dialog.cancel();
                }
            });

            dialog.show();
        }
        return super.onOptionsItemSelected(item);
    }


    private void filter(String text) {
        List<com.mindfulai.Models.varientsByCategory.Datum> filteredList = new ArrayList<>();
        for (com.mindfulai.Models.varientsByCategory.Datum varientList : varientList) {
            if (varientList.getProduct().getName().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(varientList);
            }
        }

     ArrayList<ArrayList<OptionsAttribute>> filteralloptionsAttributeArrayList = new ArrayList<>();
     ArrayList<ArrayList<DifferentVarients>> filteralldifferentVarientList= new ArrayList<>();

        for (int position = 0; position < filteredList.size(); position++) {
            ArrayList<OptionsAttribute> optionsAttributeArrayList = new ArrayList<>();
            for (int j = 0; j < filteredList.get(position).getAttributes().size(); j++) {
                ArrayList<String> option_value = new ArrayList<>();
                for (int k = 0; k < filteredList.get(position).getAttributes().get(j).getOption().size(); k++) {
                    option_value.add(k, filteredList.get(position).getAttributes().get(j).getOption().get(k).getValue());
                }
                optionsAttributeArrayList.add(j, new OptionsAttribute(filteredList.get(position).getAttributes().get(j).getAttribute().getName(), option_value));
            }
            filteralloptionsAttributeArrayList.add(position, optionsAttributeArrayList);
        }

        for (int position = 0; position < filteredList.size(); position++) {
            ArrayList<DifferentVarients> differentVarientsArrayList = new ArrayList<>();
            for (int k = 0; k < filteredList.get(position).getVarients().size(); k++) {
                ArrayList<String> varient_option_value = new ArrayList<>();
                for (int j = 0; j < filteredList.get(position).getVarients().get(k).getAttributes().size(); j++)
                    varient_option_value.add(j, filteredList.get(position).getVarients().get(k).getAttributes().get(j).getOption().getValue());
                String sellinPrice="";
                if (filteredList.get(position).getVarients().get(k).getSellingPrice() != 0.0)
                    sellinPrice = "" + filteredList.get(position).getVarients().get(k).getSellingPrice();

                Integer minQty;
                if (filteredList.get(position).getVarients().get(k).getMinOrderQuantity() != null)
                    minQty = filteredList.get(position).getVarients().get(k).getMinOrderQuantity();
                else
                    minQty = -1;
                ArrayList<String> listOfImages = new ArrayList<>();
                Images images = filteredList.get(position).getVarients().get(k).getImages();
                if (images != null && images.getPrimary() != null)
                    listOfImages.add(images.getPrimary());
                if (images != null && images.getSecondary() != null)
                    listOfImages.add(images.getSecondary());

                differentVarientsArrayList.add(k, new DifferentVarients("" + filteredList.get(position).getVarients().get(k).getPrice(), varient_option_value, filteredList.get(position).getVarients().get(k).getId(), filteredList.get(position).getVarients().get(k).getDescription(), "" + filteredList.get(position).getVarients().get(k).getStock(), sellinPrice, minQty,listOfImages, filteredList.get(position).getVarients().get(k).getInCart()));
            }
            filteralldifferentVarientList.add(position, differentVarientsArrayList);
        }
        productAdapter.filterList(filteredList,filteralloptionsAttributeArrayList,filteralldifferentVarientList);
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.item_notification);
        cartBadge = item.getActionView().findViewById(R.id.notification_badge);
        if (!SPData.showProductsAndCart()){
            cartBadge.setVisibility(GONE);
        }
        int total = SPData.getAppPreferences().getTotalCartCount();
        cartBadge.setBadgeValue(total);
        cartBadge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(AllProductsActivity.this, CommonActivity.class).putExtra("show", "cart"), 10);
            }
        });

        MenuItem list = menu.findItem(R.id.navigation_list_view);
        MenuItem grid = menu.findItem(R.id.navigation_grid_view);
        list.setVisible(false);
        grid.setVisible(false);
        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.browse_menu, menu);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.search));
        searchView.setOnQueryTextListener(this);
        return true;
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        filter(newText);
        return true;
    }

    private void getSubCategories(final String catID) {
        try {
            ApiService apiService = ApiUtils.getHeaderAPIService();

            apiService.getAllSubCategory(catID).enqueue(new Callback<SubcategoryModel>() {
                @Override
                public void onResponse(@NonNull Call<SubcategoryModel> call, @NonNull Response<SubcategoryModel> response) {

                    if (response.isSuccessful()) {
                        subcategoryList.clear();
                        SubcategoryModel subCategoryDetails = response.body();
                        assert subCategoryDetails != null;
                        if (subCategoryDetails.getData().size() > 0) {
                            subcategoryList = subCategoryDetails.getData();
                            Log.e(TAG, "level: " + level);
                            SubCategoriesAdapter subCategoriesAdapter = new SubCategoriesAdapter(AllProductsActivity.this, subcategoryList, level);
                            subCategoriesRecyclerView.setAdapter(subCategoriesAdapter);
                            subCategoriesAdapter.notifyDataSetChanged();
                        } else
                            subCategoriesRecyclerView.setVisibility(GONE);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<SubcategoryModel> call, @NonNull Throwable t) {
                    Log.e(TAG, call.toString());
                    Toast.makeText(AllProductsActivity.this, "Failed to connedt", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
            getProductsVarients();
    }

    private void getProductsVarients() {
        try {
            if (varientList != null)
                varientList.clear();
            products_grid.getRecycledViewPool().clear();
            if (productAdapter != null)
                productAdapter.notifyDataSetChanged();
            shimmerView2.setVisibility(VISIBLE);
            shimmerView2.startShimmerAnimation();
            ApiService apiService;
            if (!SPData.getAppPreferences().getUsertoken().equals(""))
                apiService = ApiUtils.getHeaderAPIService(SPData.getAppPreferences().getUsertoken());
            else
                apiService = ApiUtils.getAPIService();
            if (VENDOR_ID != null && !VENDOR_ID.isEmpty())
                apiService.getAllProductsVarients(CATEGORY_ID, VENDOR_ID).enqueue(new Callback<VarientsByCategory>() {
                    @Override
                    public void onResponse(@NonNull Call<VarientsByCategory> call, @NonNull Response<VarientsByCategory> response) {
                        Log.e("TAG", "onResponse: "+response );
                        showProductsFromResponse(response);
                    }

                    @Override
                    public void onFailure(@NonNull Call<VarientsByCategory> call, @NonNull Throwable t) {
                        Log.e("TAG", "onFailure: " + t.getMessage());
                        Toast.makeText(AllProductsActivity.this, "Failed to connect", Toast.LENGTH_SHORT).show();
                        shimmerView2.stopShimmerAnimation();
                        shimmerView2.setVisibility(GONE);
                        no_products.setVisibility(VISIBLE);
                        products_grid.setVisibility(GONE);
                        if (swipeContainer != null)
                            swipeContainer.setRefreshing(false);
                    }
                });
            else {
                String type = "product";
                if(SPData.showServicesTab() && SPData.getProductsOrServices().equals("SERVICES")){
                    type = "service";
                }
                apiService.getAllProductsVarients(CATEGORY_ID, type).enqueue(new Callback<VarientsByCategory>() {
                    @Override
                    public void onResponse(@NonNull Call<VarientsByCategory> call, @NonNull Response<VarientsByCategory> response) {
                        showProductsFromResponse(response);

                    }

                    @Override
                    public void onFailure(@NonNull Call<VarientsByCategory> call, @NonNull Throwable t) {
                        Log.e(TAG, "onFailure: " + t.getMessage());
                        Toast.makeText(AllProductsActivity.this, "Failed to connect", Toast.LENGTH_SHORT).show();
                        shimmerView2.stopShimmerAnimation();
                        no_products.setVisibility(VISIBLE);
                        shimmerView2.setVisibility(GONE);
                        products_grid.setVisibility(GONE);
                        if (swipeContainer != null)
                            swipeContainer.setRefreshing(false);
                    }
                });
            }
        } catch (Exception e) {
            Log.e(TAG, "getProductsVarients: " + e);
        }
    }

    private void loadMore() {
        varientList.add(null);
        productAdapter.notifyItemInserted(varientList.size() - 1);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                varientList.remove(varientList.size() - 1);
                scrollPosition = varientList.size();
                productAdapter.notifyItemRemoved(scrollPosition);
                int currentSize = scrollPosition;
                int nextLimit = currentSize + no_of_products;
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
                getAllOptionsVarient(false);
            }
        }, 2000);

    }

    private void showProductsFromResponse(Response<VarientsByCategory> response) {
        try {
            if (response.isSuccessful()) {
                productVarients = response.body();
                assert productVarients != null;
                try {
                    for (int i = 0; i < productVarients.getData().size(); i++)
                        varientList.add(i, productVarients.getData().get(i));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (varientList == null || varientList.size() == 0) {
                    shimmerView2.stopShimmerAnimation();
                    shimmerView2.setVisibility(GONE);
                    products_grid.setVisibility(GONE);
                    no_products.setVisibility(VISIBLE);
                    if (swipeContainer != null)
                        swipeContainer.setRefreshing(false);
                } else {
                    getAllOptionsVarient(true);
                }
            } else {
                shimmerView2.stopShimmerAnimation();
                shimmerView2.setVisibility(GONE);
                products_grid.setVisibility(GONE);
                if (swipeContainer != null)
                    swipeContainer.setRefreshing(false);
                Log.e("TAG", "onResponse: " + response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "showProductsFromResponse: " + e);
        }
    }

    private void getAllOptionsVarient(boolean b) {

        for (int position = 0; position < varientList.size(); position++) {
            ArrayList<OptionsAttribute> optionsAttributeArrayList = new ArrayList<>();
            for (int j = 0; j < varientList.get(position).getAttributes().size(); j++) {
                ArrayList<String> option_value = new ArrayList<>();
                for (int k = 0; k < varientList.get(position).getAttributes().get(j).getOption().size(); k++) {
                    option_value.add(k, varientList.get(position).getAttributes().get(j).getOption().get(k).getValue());
                }
                optionsAttributeArrayList.add(j, new OptionsAttribute(varientList.get(position).getAttributes().get(j).getAttribute().getName(), option_value));
            }
            alloptionsAttributeArrayList.add(position, optionsAttributeArrayList);
        }

        for (int position = 0; position < varientList.size(); position++) {
            ArrayList<DifferentVarients> differentVarientsArrayList = new ArrayList<>();
            for (int k = 0; k < varientList.get(position).getVarients().size(); k++) {
                ArrayList<String> varient_option_value = new ArrayList<>();
                for (int j = 0; j < varientList.get(position).getVarients().get(k).getAttributes().size(); j++)
                    varient_option_value.add(j, varientList.get(position).getVarients().get(k).getAttributes().get(j).getOption().getValue());
                String sellinPrice="";
                if (varientList.get(position).getVarients().get(k).getSellingPrice() != 0.0)
                    sellinPrice = "" + varientList.get(position).getVarients().get(k).getSellingPrice();

                Integer minQty;
                if (varientList.get(position).getVarients().get(k).getMinOrderQuantity() != null)
                    minQty = varientList.get(position).getVarients().get(k).getMinOrderQuantity();
                else
                    minQty = -1;
                ArrayList<String> listOfImages = new ArrayList<>();
                Images images = varientList.get(position).getVarients().get(k).getImages();
                if (images != null && images.getPrimary() != null)
                    listOfImages.add(images.getPrimary());
                if (images != null && images.getSecondary() != null)
                    listOfImages.add(images.getSecondary());

                differentVarientsArrayList.add(k, new DifferentVarients("" + varientList.get(position).getVarients().get(k).getPrice(), varient_option_value, varientList.get(position).getVarients().get(k).getId(), varientList.get(position).getVarients().get(k).getDescription(), "" + varientList.get(position).getVarients().get(k).getStock(), sellinPrice, minQty,listOfImages, varientList.get(position).getVarients().get(k).getInCart()));
            }
            alldifferentVarientList.add(position, differentVarientsArrayList);
        }
        shimmerView2.stopShimmerAnimation();
        shimmerView2.setVisibility(GONE);
        products_grid.setVisibility(VISIBLE);
        if (b) {
            String type="grid";
            if(!SPData.showGridView())
                type="list";
            productAdapter = new ProductsAdapter(AllProductsActivity.this, varientList, null, type, alloptionsAttributeArrayList, alldifferentVarientList);
            products_grid.setAdapter(productAdapter);
        }
        productAdapter.notifyDataSetChanged();
        boolean isLoading = false;
        if (swipeContainer != null)
            swipeContainer.setRefreshing(false);
    }
}