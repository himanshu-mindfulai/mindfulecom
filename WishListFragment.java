package com.mindfulai.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.mindfulai.Adapter.ProductsAdapter;
import com.mindfulai.Models.DifferentVarients;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WishListFragment extends Fragment {

    public WishListFragment() {
        // Required empty public constructor
    }

    private RecyclerView products_grid;
    private ShimmerFrameLayout shimmerView2;
    private LinearLayout signinButton;
    private ProductsAdapter wishListProductAdpater;
    private ArrayList<ArrayList<OptionsAttribute>> alloptionsAttributeArrayList;
    private ArrayList<ArrayList<DifferentVarients>> alldifferentVarientList;
    private static final String TAG = "WishListFragment";
    private String sellinPrice = "";
    private List<com.mindfulai.Models.varientsByCategory.Datum> bestSellingDataList;
    private int minQty = -1;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_wishlist, container, false);
        try {
            signinButton = view.findViewById(R.id.no_products);
            shimmerView2 = view.findViewById(R.id.shimmerView2);
            alloptionsAttributeArrayList = new ArrayList<>();
            alldifferentVarientList = new ArrayList<>();
            bestSellingDataList = new ArrayList<>();
            products_grid = view.findViewById(R.id.products_grid);
            products_grid.setLayoutManager(new CommonUtils(getActivity()).getProductGridLayoutManager());
        } catch (Exception e) {
            Log.e(TAG, "onCreateView: " + e);
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (SPData.getAppPreferences().getUsertoken() != null && !SPData.getAppPreferences().getUsertoken().isEmpty()) {
            shimmerView2.startShimmerAnimation();
            alloptionsAttributeArrayList.clear();
            alldifferentVarientList.clear();
            getAllWishlist();
        } else {
            signinButton.setVisibility(View.VISIBLE);
            shimmerView2.setVisibility(View.GONE);
        }
    }

    private void getAllWishlist() {

        ApiService apiService = ApiUtils.getHeaderAPIService(SPData.getAppPreferences().getUsertoken());
        apiService.getAllWishlist().enqueue(new Callback<VarientsByCategory>() {
            @Override
            public void onResponse(@NonNull Call<VarientsByCategory> call, @NonNull Response<VarientsByCategory> response) {

                if (response.isSuccessful()) {
                    assert response.body() != null;
                    VarientsByCategory varientsByCategory = response.body();
                    bestSellingDataList.clear();
                    for (int i = 0; i < varientsByCategory.getData().size(); i++) {
                        bestSellingDataList.add(i, varientsByCategory.getData().get(i));
                    }
                    if (bestSellingDataList.size() > 0) {
                        shimmerView2.setVisibility(View.GONE);
                        products_grid.setVisibility(View.VISIBLE);
                        signinButton.setVisibility(View.GONE);
                        if (getActivity() != null) {
                            for (int position = 0; position < bestSellingDataList.size(); position++) {
                                ArrayList<OptionsAttribute> optionsAttributeArrayList = new ArrayList<>();
                                for (int j = 0; j < bestSellingDataList.get(position).getAttributes().size(); j++) {
                                    ArrayList<String> option_value = new ArrayList<>();
                                    for (int k = 0; k < bestSellingDataList.get(position).getAttributes().get(j).getOption().size(); k++) {
                                        option_value.add(k, bestSellingDataList.get(position).getAttributes().get(j).getOption().get(k).getValue());
                                    }
                                    optionsAttributeArrayList.add(j, new OptionsAttribute(bestSellingDataList.get(position).getAttributes().get(j).getAttribute().getName(), option_value));
                                }
                                alloptionsAttributeArrayList.add(position, optionsAttributeArrayList);
                            }
                            for (int position = 0; position < bestSellingDataList.size(); position++) {
                                ArrayList<DifferentVarients> differentVarientsArrayList = new ArrayList<>();
                                for (int k = 0; k < bestSellingDataList.get(position).getVarients().size(); k++) {
                                    ArrayList<String> varient_option_value = new ArrayList<>();
                                    for (int j = 0; j < bestSellingDataList.get(position).getVarients().get(k).getAttributes().size(); j++)
                                        varient_option_value.add(j, bestSellingDataList.get(position).getVarients().get(k).getAttributes().get(j).getOption().getValue());
                                    if (bestSellingDataList.get(position).getVarients().get(k).getSellingPrice() != 0.0)
                                        sellinPrice = "" + bestSellingDataList.get(position).getVarients().get(k).getSellingPrice();
                                    else
                                        sellinPrice = "";
                                    if (bestSellingDataList.get(position).getVarients().get(k).getMinOrderQuantity() != null)
                                        minQty = bestSellingDataList.get(position).getVarients().get(k).getMinOrderQuantity();
                                    else
                                        minQty = -1;
                                    ArrayList<String> listOfImages = new ArrayList<>();
                                    Images images = bestSellingDataList.get(position).getVarients().get(k).getImages();
                                    if (images != null && images.getPrimary() != null)
                                        listOfImages.add(images.getPrimary());
                                    if (images != null && images.getSecondary() != null)
                                        listOfImages.add(images.getSecondary());
                                    differentVarientsArrayList.add(k, new DifferentVarients("" + bestSellingDataList.get(position).getVarients().get(k).getPrice(), varient_option_value, bestSellingDataList.get(position).getVarients().get(k).getId(), bestSellingDataList.get(position).getVarients().get(k).getDescription(), "" + bestSellingDataList.get(position).getVarients().get(k).getStock(), sellinPrice, minQty,listOfImages));
                                }
                                alldifferentVarientList.add(position, differentVarientsArrayList);
                            }
                            wishListProductAdpater = new ProductsAdapter(getContext(), bestSellingDataList, signinButton, "grid", alloptionsAttributeArrayList, alldifferentVarientList);
                            products_grid.setAdapter(wishListProductAdpater);
                            wishListProductAdpater.notifyDataSetChanged();
                        }

                    } else {
                        signinButton.setVisibility(View.VISIBLE);
                        shimmerView2.setVisibility(View.GONE);
                    }
                } else {
                    signinButton.setVisibility(View.VISIBLE);
                    shimmerView2.setVisibility(View.GONE);
                }

            }

            @Override
            public void onFailure(@NonNull Call<VarientsByCategory> call, @NonNull Throwable t) {
                signinButton.setVisibility(View.VISIBLE);
                shimmerView2.setVisibility(View.GONE);
            }
        });

    }
}
