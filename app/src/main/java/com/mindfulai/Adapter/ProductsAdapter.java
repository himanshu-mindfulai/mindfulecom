package com.mindfulai.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.JsonObject;
import com.mindfulai.Activites.AllProductsActivity;
import com.mindfulai.Activites.CommonActivity;
import com.mindfulai.Activites.LoginActivity;
import com.mindfulai.Activites.MainActivity;
import com.mindfulai.Activites.ProductDetailsActivity;
import com.mindfulai.Models.DifferentVarients;
import com.mindfulai.Models.varientsByCategory.Datum;
import com.mindfulai.Models.varientsByCategory.OptionsAttribute;
import com.mindfulai.NetworkRetrofit.ApiService;
import com.mindfulai.NetworkRetrofit.ApiUtils;
import com.mindfulai.Utils.CommonUtils;
import com.mindfulai.Utils.CustomProgressDialog;
import com.mindfulai.Utils.GlobalEnum;
import com.mindfulai.Utils.SPData;
import com.mindfulai.ministore.R;
import com.valdesekamdem.library.mdtoast.MDToast;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.mindfulai.Utils.CommonUtils.capitalizeWord;

public class ProductsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<Datum> varientList;
    private ArrayList<ArrayList<OptionsAttribute>> alloptionsAttributeArrayList;
    private ArrayList<ArrayList<DifferentVarients>> alldifferentVarientsArrayList;
    private String type;
    private LinearLayout linearLayout;
    private View itemView;
    private final int VIEW_TYPE_ITEM = 0;
    private static final String TAG = "ProductsAdapter";

    public void filterList(List<Datum> filteredList, ArrayList<ArrayList<OptionsAttribute>> filteralloptionsAttributeArrayList, ArrayList<ArrayList<DifferentVarients>> filteralldifferentVarientList) {
        this.varientList = filteredList;
        this.alldifferentVarientsArrayList = filteralldifferentVarientList;
        this.alloptionsAttributeArrayList = filteralloptionsAttributeArrayList;
        notifyDataSetChanged();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView product_discount, product_recommended_text, product_name, product_price, product_minQty, add_to_cart, product_rating, mrp_price, product_varient_id, product_stock, product_available;
        private LinearLayout linearLayoutNoCartitems;
        public ImageView image;
        RelativeLayout cardView;
        RecyclerView recycler_options;
        private TextView no_ofQuantity, product_mrp, product_selling_price, buy_now, vendorName;
        Button increase, decrease;
        private LinearLayout layoutItems;
        CardView product_recommended, product_discount_layout;
        Button removeFromWishlist;

        MyViewHolder(View view) {
            super(view);
            removeFromWishlist = view.findViewById(R.id.remove_from_wishlist);
            product_name = view.findViewById(R.id.product_name);
            image = view.findViewById(R.id.image);
            no_ofQuantity = view.findViewById(R.id.no_of_quantity);
            linearLayoutNoCartitems = view.findViewById(R.id.linearLayout);
            cardView = view.findViewById(R.id.cardview);
            add_to_cart = view.findViewById(R.id.add_to_cart);
            product_rating = view.findViewById(R.id.no_of_rating);
            recycler_options = view.findViewById(R.id.recyclerview_option_values);
            increase = view.findViewById(R.id.increase);
            decrease = view.findViewById(R.id.decrease);
            product_varient_id = view.findViewById(R.id.product_varient_id);
            product_stock = view.findViewById(R.id.product_stock);
            product_available = view.findViewById(R.id.product_available);
            layoutItems = view.findViewById(R.id.layout_item);
            product_minQty = view.findViewById(R.id.product_qty);
            this.product_mrp = view.findViewById(R.id.product_mrp);
            this.product_selling_price = view.findViewById(R.id.product_price);
            product_recommended = view.findViewById(R.id.product_recommended);
            product_recommended_text = view.findViewById(R.id.product_recommended_text);
            product_discount = view.findViewById(R.id.product_discount);
            buy_now = view.findViewById(R.id.buy_now);
            vendorName = view.findViewById(R.id.vendor_name);
            product_discount_layout = view.findViewById(R.id.product_discount_layout);
        }
    }

    public ProductsAdapter(final Context context, List<Datum> varientList, LinearLayout signinButton, String type, ArrayList<ArrayList<OptionsAttribute>> optionsAttributeArrayList, ArrayList<ArrayList<DifferentVarients>> alldifferentVarientList) {
        try {
            this.context = context;
            this.varientList = varientList;
            this.alloptionsAttributeArrayList = optionsAttributeArrayList;
            this.alldifferentVarientsArrayList = alldifferentVarientList;
            this.type = type;
            this.linearLayout = signinButton;
        } catch (Exception e) {
            Log.e(TAG, "ProductsAdapter: " + e);
        }

    }

    @Override
    public int getItemViewType(int position) {
        int VIEW_TYPE_LOADING = 1;
        return varientList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    private static class LoadingViewHolder extends RecyclerView.ViewHolder {
        ProgressBar progressBar;

        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }

    private void showLoadingView(LoadingViewHolder viewHolder, int position) {
        //ProgressBar would be displayed

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            if (type.equals("grid"))
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.custom_product_layout, parent, false);
            else if (type.equals("list"))
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.custom_product_layout_list, parent, false);
            return new MyViewHolder(itemView);
        } else {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_loading, parent, false);
            return new LoadingViewHolder(itemView);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {

        if (holder instanceof MyViewHolder) {
            showProductItem((MyViewHolder) holder, position);
        } else if (holder instanceof LoadingViewHolder) {
            showLoadingView((LoadingViewHolder) holder, position);
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("SetTextI18n")
    private void showProductItem(final MyViewHolder holder, final int position) {
        try {
            if (SPData.showVarientDropdown())
                holder.recycler_options.setVisibility(View.VISIBLE);
            else
                holder.recycler_options.setVisibility(View.GONE);

            String desc = "";
            if (!((Activity) context instanceof CommonActivity)) {
                holder.removeFromWishlist.setVisibility(View.GONE);
            } else {
                holder.removeFromWishlist.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        removeFromWishlist(position);
                    }
                });
            }
            if (alloptionsAttributeArrayList != null && alldifferentVarientsArrayList != null) {
                ArrayList<String> images = new ArrayList<>();
                if (varientList.get(position).getProduct().getImages() != null) {
                    if (varientList.get(position).getProduct().getImages().getPrimary() != null) {
                        images.add(varientList.get(position).getProduct().getImages().getPrimary());
                    }
                    if (varientList.get(position).getProduct().getImages().getSecondary() != null) {
                        images.add(varientList.get(position).getProduct().getImages().getSecondary());
                    }
                }
                if (varientList.get(position).getVarients().get(0).getImages() != null) {
                    if (varientList.get(position).getVarients().get(0).getImages().getPrimary() != null) {
                        images.add(varientList.get(position).getVarients().get(0).getImages().getPrimary());
                    }
                    if (varientList.get(position).getVarients().get(0).getImages().getSecondary() != null) {
                        images.add(varientList.get(position).getVarients().get(0).getImages().getSecondary());
                    }
                }
                ArrayList<String> productImages = new ArrayList<>();
                try {
                    if (images.get(0).startsWith("products/")) {
                        productImages.add(images.get(0));
                        if (images.get(1).startsWith("products/")) {
                            productImages.add(images.get(1));
                        }
                    }
                } catch (IndexOutOfBoundsException e) {
                    //if it gets index out of bound then that means there aren't any
                    //images of the product available. In that case don't change anything!
                }
                OptionViewAdapter adapter = new OptionViewAdapter(context, alloptionsAttributeArrayList.get(position), alldifferentVarientsArrayList.get(position), itemView, null, productImages);
                if (type.equals("list")) {
                    GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 2);
                    holder.recycler_options.setLayoutManager(gridLayoutManager);
                } else {
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
                    holder.recycler_options.setLayoutManager(linearLayoutManager);
                }
                holder.recycler_options.setAdapter(adapter);
            }

            if (holder.product_rating != null) {
                DecimalFormat df = new DecimalFormat("#.##");
                holder.product_rating.setText("(" + df.format(varientList.get(position).getVarients().get(0).getReviews().getRating()) + ")");
            }
            float sellingPrice = varientList.get(position).getVarients().get(0).getSellingPrice();
            float productPrice = varientList.get(position).getVarients().get(0).getPrice();
            if (sellingPrice != 0.0 && sellingPrice != productPrice) {
                float selling = varientList.get(position).getVarients().get(0).getSellingPrice();
                float mrp = varientList.get(position).getVarients().get(0).getPrice();
                int discount = (int) (((mrp - selling) / mrp) * 100);
                holder.product_mrp.setText("\u20B9" + varientList.get(position).getVarients().get(0).getPrice());
                holder.product_mrp.setVisibility(View.VISIBLE);
                holder.product_discount.setText(discount + "% off");
                holder.product_discount_layout.setVisibility(View.VISIBLE);
                holder.product_discount.setVisibility(View.VISIBLE);
                holder.product_selling_price.setText("\u20B9" + varientList.get(position).getVarients().get(0).getSellingPrice());

            } else {
                holder.product_discount_layout.setVisibility(View.INVISIBLE);
                holder.product_discount.setVisibility(View.INVISIBLE);
                holder.product_selling_price.setText("\u20B9" + varientList.get(position).getVarients().get(0).getPrice());
                holder.product_mrp.setVisibility(View.GONE);
            }
            int minQty = -1;
            if (varientList.get(position).getVarients().get(0).getMinOrderQuantity() != null)
                minQty = varientList.get(position).getVarients().get(0).getMinOrderQuantity();
            if (minQty != -1)
                holder.product_minQty.setText("Min qty-" + minQty);
            else {
                holder.product_minQty.setText("Min qty-" + minQty);
                holder.product_minQty.setVisibility(View.GONE);
            }
            if (SPData.showGridView()) {
                if (varientList.get(position).getProduct().getIsRecommended() != null
                        && varientList.get(position).getProduct().getIsRecommended()) {
                    holder.product_recommended.setVisibility(View.VISIBLE);
                    holder.product_recommended_text.setText(SPData.recommendedText());
                } else if (SPData.showCertifiedText()) {
                    holder.product_recommended.setCardBackgroundColor(context.getColor(R.color.colorInfo));
                    holder.product_recommended.setVisibility(View.VISIBLE);
                    holder.product_recommended_text.setText(SPData.certifiedText());
                } else {
                    holder.product_recommended_text.setText("");
                    holder.product_recommended.setVisibility(View.GONE);
                }
            } else {
                holder.product_recommended.setVisibility(View.GONE);
            }
            Log.i("TAG", "productsAdapter: " + varientList.get(position).getProduct().getName());
            holder.product_name.setText(capitalizeWord(varientList.get(position).getProduct().getName()));
            holder.product_varient_id.setText(varientList.get(position).getVarients().get(0).getId());
            holder.product_available.setText("true");
            holder.product_stock.setText("" + varientList.get(position).getVarients().get(0).getStock());
            if (SPData.showBrandOrVendorOnProductList()
                    && SPData.getShowBrandOrVendor().equals("BRAND")
                    && varientList.get(position).getProduct().getBrand() != null) {
                Log.e(TAG, "showProductItem::" + varientList.get(position).getProduct().getBrand().getName());
                holder.vendorName.setText("By " + CommonUtils.capitalizeWord(varientList.get(position).getProduct().getBrand().getName()));
            } else if (SPData.showBrandOrVendorOnProductList()
                    && SPData.getShowBrandOrVendor().equals("VENDOR")
                    && varientList.get(position).getProduct().getCreatedBy() != null) {
                Log.e(TAG, "showProductItem::" + varientList.get(position).getProduct().getCreatedBy().getName());
                holder.vendorName.setText("By " + CommonUtils.capitalizeWord(varientList.get(position).getProduct().getCreatedBy().getName()));
            } else {
                holder.vendorName.setVisibility(View.GONE);
            }

            for (int j = 0; j < varientList.get(position).getAttributes().size(); j++) {
                for (int k = 0; k < varientList.get(position).getAttributes().get(j).getOption().size(); k++)
                    desc = desc.concat(varientList.get(position).getAttributes().get(j).getOption().get(k).getValue() + ",");
            }
            if (SPData.showServicesTab() && SPData.getProductsOrServices().equals("SERVICES")) {
                holder.add_to_cart.setVisibility(View.GONE);
                holder.linearLayoutNoCartitems.setVisibility(View.GONE);
            } else {
                holder.add_to_cart.setVisibility(View.VISIBLE);
                holder.linearLayoutNoCartitems.setVisibility(View.GONE);
            }
            SharedPreferences prefs = context.getSharedPreferences("cart", Context.MODE_PRIVATE);
            if (Integer.parseInt(holder.product_stock.getText().toString()) <= 0) {
                holder.add_to_cart.setText("Out of stock");
                holder.add_to_cart.setOnClickListener(null);
            } else {
                holder.add_to_cart.setText("Add");
                holder.add_to_cart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!SPData.getAppPreferences().getUsertoken().equals("")) {
                            String available = holder.product_available.getText().toString();
                            int total_stock = Integer.parseInt(holder.product_stock.getText().toString());
                            if (available.equals("true") && total_stock > 0) {
                                String id = holder.product_varient_id.getText().toString();
                                int minQty = Integer.parseInt(holder.product_minQty.getText().toString().split("qty-")[1]);
                                if (minQty != -1 && total_stock >= minQty) {
                                    addItem(id, holder.no_ofQuantity, minQty, holder);
//                                    prefs.edit().putInt(
//                                            varientList.get(position).getProduct().getId(),
//                                            prefs.getInt(varientList.get(position).getProduct().getId(), 0) + 1).apply();
                                } else if (minQty == -1) {
                                    addItem(id, holder.no_ofQuantity, 1, holder);
//                                    prefs.edit().putInt(
//                                            varientList.get(position).getProduct().getId(),
//                                            prefs.getInt(varientList.get(position).getProduct().getId(), 0) + 1).apply();
                                } else
                                    Toast.makeText(context, "Current stock is " + total_stock, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            context.startActivity(new Intent(context, LoginActivity.class));
                        }
                    }
                });
            }
//            int counter = prefs.getInt(varientList.get(position).getProduct().getId(), 0);
//            Log.e("ProductsAcdapter", counter+"");
//            for(int i=0; i<counter; i++) {
//                holder.add_to_cart.performClick();
//            }
            if (holder.decrease != null)
                holder.decrease.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int minQty = Integer.parseInt(holder.product_minQty.getText().toString().split("qty-")[1]);
                        if (minQty != -1) {
                            if (Integer.parseInt(holder.no_ofQuantity.getText().toString()) > minQty) {
                                String id = holder.product_varient_id.getText().toString();
                                int quantity = (Integer.parseInt(holder.no_ofQuantity.getText().toString())) - minQty;
                                updateItem(id, quantity, holder);
                            } else {
                                removeProductFromList(holder.product_varient_id.getText().toString(), holder);
                            }
                        } else if (Integer.parseInt(holder.no_ofQuantity.getText().toString()) > 1) {
                            String id = holder.product_varient_id.getText().toString();
                            int quantity = (Integer.parseInt(holder.no_ofQuantity.getText().toString())) - 1;
                            updateItem(id, quantity, holder);
                        } else {
                            removeProductFromList(holder.product_varient_id.getText().toString(), holder);
                        }
                    }
                });

            if (holder.increase != null)
                holder.increase.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        checkItemToAdded(holder.no_ofQuantity, holder, false);
                    }

                });
            if (varientList.get(position).getVarients().get(0).getInCart() != null) {
                if (varientList.get(position).getVarients().get(0).getInCart() > 0) {
                    holder.add_to_cart.setVisibility(View.GONE);
                    holder.linearLayoutNoCartitems.setVisibility(View.VISIBLE);
                    if (SPData.getAppPreferences().getTotalCartCount() != -1) {
//                    int cartItem = SPData.getAppPreferences().getTotalCartCount() + 1;
//                    SPData.getAppPreferences().setTotalCartCount(cartItem);
//                    if (context instanceof MainActivity)
//                        ((MainActivity) context).addBadge("" + cartItem);
//                    if (context instanceof AllProductsActivity)
//                        ((AllProductsActivity) context).addBadge("" + cartItem);

                    }
                    holder.no_ofQuantity.setText("" + varientList.get(position).getVarients().get(0).getInCart());
                }
            }
            if (varientList.get(position).getVarients().get(0).getImages() != null) {
                String pri = varientList.get(position).getVarients().get(0).getImages().getPrimary();
                String sec = varientList.get(position).getVarients().get(0).getImages().getSecondary();
                if (pri != null && !pri.isEmpty())
                    Glide.with(context).load(GlobalEnum.AMAZON_URL + pri).into(holder.image);
                else if (sec != null && !sec.isEmpty())
                    Glide.with(context).load(GlobalEnum.AMAZON_URL + sec).into(holder.image);
                else if (varientList.get(position).getProduct().getImages().getPrimary() != null
                        && !varientList.get(position).getProduct().getImages().getPrimary().isEmpty()) {
                    Glide.with(context)
                            .load(GlobalEnum.AMAZON_URL + varientList.get(position).getProduct().getImages().getPrimary())
                            .into(holder.image);
                } else if (varientList.get(position).getProduct().getImages().getSecondary() != null
                        && !varientList.get(position).getProduct().getImages().getSecondary().isEmpty()) {
                    Glide.with(context)
                            .load(GlobalEnum.AMAZON_URL + varientList.get(position).getProduct().getImages().getSecondary())
                            .into(holder.image);
                } else
                    Glide.with(context).load(context.getResources().getDrawable(R.drawable.noimage)).into(holder.image);
            } else if (varientList.get(position).getProduct().getImages() != null) {
                String pri = varientList.get(position).getProduct().getImages().getPrimary();
                String sec = varientList.get(position).getProduct().getImages().getSecondary();
                if (pri != null && !pri.isEmpty())
                    Glide.with(context).load(GlobalEnum.AMAZON_URL + pri).into(holder.image);
                else if (sec != null && !sec.isEmpty())
                    Glide.with(context).load(GlobalEnum.AMAZON_URL + varientList.get(position).getProduct().getImages().getPrimary()).into(holder.image);
                else
                    Glide.with(context).load(context.getResources().getDrawable(R.drawable.noimage)).into(holder.image);
            } else {
                Glide.with(context).load(context.getResources().getDrawable(R.drawable.noimage)).into(holder.image);
            }

            holder.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openProductDetail(position);
                }
            });
            holder.layoutItems.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openProductDetail(position);
                }
            });
            if (linearLayout != null)
                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        AlertDialog.Builder alert = new AlertDialog.Builder(context);
                        alert.setMessage("Remove item from WishList ?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int which) {
                                        holder.cardView.setVisibility(View.GONE);
                                        varientList.remove(holder.getAdapterPosition());
                                        notifyItemRemoved(holder.getAdapterPosition());
                                        if (varientList.size() == 0) {
                                            linearLayout.setVisibility(View.VISIBLE);
                                        } else {
                                            linearLayout.setVisibility(View.GONE);
                                        }
                                    }
                                }).setNegativeButton("Cancel", null);
                        AlertDialog alert1 = alert.create();
                        alert1.show();

                        return true;
                    }
                });
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("TAG", "showProductItem: " + e);
        }
    }

    private void removeFromWishlist(int position) {
        try {
            final CustomProgressDialog customProgressDialog = CommonUtils.showProgressDialog(context,
                    "Removing from wishlist ... ");
            ApiService apiService = ApiUtils.getHeaderAPIService(SPData.getAppPreferences().getUsertoken());

            apiService.removeItemFromWishlist(varientList.get(position).getVarients().get(0).getId()).enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                    CommonUtils.hideProgressDialog(customProgressDialog);
                    if (response.isSuccessful()) {
                        String reponse_status = String.valueOf(response.body().get("status"));
                        if (reponse_status.matches("200")) {
                            varientList.remove(position);
                            notifyDataSetChanged();
                            Toast.makeText(context, "Item remove from wishlist!!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        CommonUtils.hideProgressDialog(customProgressDialog);
                        Toast.makeText(context, response.code() + " " + response.message(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                    Log.e("fail", call.toString());
                    CommonUtils.hideProgressDialog(customProgressDialog);
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "removeItemFromWishList: " + e);
        }
    }

    private void openProductDetail(int position) {
        Intent i = new Intent(context, ProductDetailsActivity.class);
        if (varientList.get(position).getProduct().getCreatedBy() != null) {
            i.putExtra("vendor_name", varientList.get(position).getProduct().getCreatedBy().getName());
            i.putExtra("vendor_id", varientList.get(position).getProduct().getCreatedBy().getId());
        } else
            i.putExtra("vendor_name", "");
        i.putExtra("returnable", varientList.get(position).getProduct().isReturnable());
        i.putExtra("product_name", varientList.get(position).getProduct().getName());
        i.putExtra("varient_id", varientList.get(position).getVarients().get(0).getId());
        i.putExtra("category_id", varientList.get(position).getProduct().getCategory().getId());
        i.putExtra("product_id", varientList.get(position).getProduct().getId());
        i.putExtra("product_description", varientList.get(position).getProduct().getDetails());
        i.putExtra("reviews_count", varientList.get(position).getVarients().get(0).getReviews().getTotal());
        i.putExtra("rating", varientList.get(position).getVarients().get(0).getReviews().getRating());
        i.putExtra("product_stock", varientList.get(position).getVarients().get(0).getStock());
        i.putExtra("product_price", varientList.get(position).getVarients().get(0).getPrice());
        i.putExtra("codAvailable", varientList.get(position).getProduct().isCodAvailable());
        if (varientList.get(position).getProduct().getBrand() != null && varientList.get(position).getProduct().getBrand().getName() != null)
            i.putExtra("brand_name", varientList.get(position).getProduct().getBrand().getName());
        else
            i.putExtra("brand_name", "");

        i.putExtra("product_selling_price", varientList.get(position).getVarients().get(0).getSellingPrice());
        i.putExtra("product_minQty", "" + varientList.get(position).getVarients().get(0).getMinOrderQuantity());
        if (context instanceof CommonActivity)
            i.putExtra("isInWishlist", true);
        else if (varientList.get(position).getVarients().get(0).getWishlist() != null)
            i.putExtra("isInWishlist", varientList.get(position).getVarients().get(0).getWishlist());
        else
            i.putExtra("isInWishlist", false);
        i.putExtra("description", varientList.get(position).getProduct().getDetails());
        if (varientList.get(position).getProduct().getIsRecommended() != null) {
            i.putExtra("recommended", varientList.get(position).getProduct().getIsRecommended());
        } else {
            i.putExtra("recommended", false);
        }
        Bundle b = new Bundle();
        b.putParcelableArrayList("categories", alloptionsAttributeArrayList.get(position));
        i.putExtra("differentVarientsArrayList", alldifferentVarientsArrayList.get(position));
        i.putExtras(b);
        if (varientList.get(position).getProduct().getVideo() != null && !varientList.get(position).getProduct().getVideo().isEmpty()) {
            i.putExtra("video", varientList.get(position).getProduct().getVideo());
        }
        ArrayList<String> images = new ArrayList<>();
        if (varientList.get(position).getProduct().getImages() != null) {
            if (varientList.get(position).getProduct().getImages().getPrimary() != null) {
                images.add(varientList.get(position).getProduct().getImages().getPrimary());
            }
            if (varientList.get(position).getProduct().getImages().getSecondary() != null) {
                images.add(varientList.get(position).getProduct().getImages().getSecondary());
            }
            if (varientList.get(position).getProduct().getImages().getImage1() != null) {
                images.add(varientList.get(position).getProduct().getImages().getImage1());
            }
            if (varientList.get(position).getProduct().getImages().getImage2() != null) {
                images.add(varientList.get(position).getProduct().getImages().getImage2());
            }
            if (varientList.get(position).getProduct().getImages().getImage3() != null) {
                images.add(varientList.get(position).getProduct().getImages().getImage3());
            }
        }
        if (varientList.get(position).getVarients().get(0).getImages() != null) {
            if (varientList.get(position).getVarients().get(0).getImages().getPrimary() != null) {
                images.add(varientList.get(position).getVarients().get(0).getImages().getPrimary());
            }
            if (varientList.get(position).getVarients().get(0).getImages().getSecondary() != null) {
                images.add(varientList.get(position).getVarients().get(0).getImages().getSecondary());
            }
            if (varientList.get(position).getVarients().get(0).getImages().getImage1() != null) {
                images.add(varientList.get(position).getVarients().get(0).getImages().getImage1());
            }
            if (varientList.get(position).getVarients().get(0).getImages().getImage2() != null) {
                images.add(varientList.get(position).getVarients().get(0).getImages().getImage2());
            }
            if (varientList.get(position).getVarients().get(0).getImages().getImage3() != null) {
                images.add(varientList.get(position).getVarients().get(0).getImages().getImage3());
            }
        }
        i.putStringArrayListExtra("images", images);
        ((Activity) context).startActivityForResult(i, 10);
    }

    private void removeProductFromList(String id, final MyViewHolder holder) {
        ApiService apiService = ApiUtils.getHeaderAPIService(SPData.getAppPreferences().getUsertoken());
        apiService.removeItemFromCart(id).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {
                    if (response.isSuccessful()) {
                        assert response.body() != null;
                        String reponse_status = String.valueOf(response.body().get("status"));
                        if (reponse_status.matches("200")) {
                            holder.add_to_cart.setVisibility(View.VISIBLE);
                            holder.linearLayoutNoCartitems.setVisibility(View.GONE);
                            if (SPData.getAppPreferences().getTotalCartCount() != -1) {
                                int total_cart = SPData.getAppPreferences().getTotalCartCount() - 1;
                                if (total_cart > 0) {
                                    SPData.getAppPreferences().setTotalCartCount(total_cart);
                                    if (context instanceof MainActivity)
                                        ((MainActivity) context).addBadge("" + total_cart);
                                    if (context instanceof AllProductsActivity)
                                        ((AllProductsActivity) context).addBadge("" + total_cart);

                                } else {
                                    SPData.getAppPreferences().setTotalCartCount(0);
                                    if (context instanceof MainActivity)
                                        ((MainActivity) context).removeBadge();
                                    if (context instanceof AllProductsActivity)
                                        ((AllProductsActivity) context).removeBadge();

                                }
                            }
                            Toast.makeText(context, "Removed from cart !!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(context, response.code() + " " + response.message(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.e(TAG, "onResponse: " + e);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(context, "Failed to connect", Toast.LENGTH_SHORT).show();
            }
        });

    }


    private void checkItemToAdded(TextView no_ofQuantity, MyViewHolder holder, boolean b) {
        int total_stock = Integer.parseInt(holder.product_stock.getText().toString());
        String available = holder.product_available.getText().toString();
        int minQty = Integer.parseInt(holder.product_minQty.getText().toString().split("qty-")[1]);
        int currentQty = Integer.parseInt(no_ofQuantity.getText().toString());
        if (minQty != -1)
            currentQty = currentQty + minQty;
        if (available.equals("true") && currentQty <= total_stock) {
            if (!SPData.getAppPreferences().getUsertoken().equals("")) {
                String id = holder.product_varient_id.getText().toString();
                int quantity;
                if (minQty != -1)
                    quantity = (Integer.parseInt(holder.no_ofQuantity.getText().toString())) + minQty;
                else
                    quantity = (Integer.parseInt(holder.no_ofQuantity.getText().toString())) + 1;
                updateItem(id, quantity, holder);
            } else
                context.startActivity(new Intent(context, LoginActivity.class));
        } else if (available.equals("false")) {
            MDToast.makeText(context, "Item not available", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
        } else {
            Toast.makeText(context, "Current stock is less than quantity " + "( current stock is " + total_stock + " )", Toast.LENGTH_SHORT).show();
        }
    }

    private void addItem(String id, final TextView no_ofQuantity, final int quantity, final MyViewHolder holder) {
        Log.e(TAG, "addItem: " + quantity);
        ApiService apiService = ApiUtils.getHeaderAPIService(SPData.getAppPreferences().getUsertoken());
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("product", id);
        jsonObject.addProperty("quantity", quantity);
        apiService.addItemToCart(jsonObject).enqueue(new Callback<JsonObject>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    Log.e(TAG, "onResponse: " + response);
                    String reponse_status = String.valueOf(response.body().get("status"));
                    if (reponse_status.matches("200")) {
                        holder.add_to_cart.setVisibility(View.GONE);
                        holder.linearLayoutNoCartitems.setVisibility(View.VISIBLE);
                        if (SPData.getAppPreferences().getTotalCartCount() != -1) {
                            int cartItem = SPData.getAppPreferences().getTotalCartCount() + 1;
                            SPData.getAppPreferences().setTotalCartCount(cartItem);
                            if (context instanceof MainActivity)
                                ((MainActivity) context).addBadge("" + cartItem);
                            if (context instanceof AllProductsActivity)
                                ((AllProductsActivity) context).addBadge("" + cartItem);

                        }
                        no_ofQuantity.setText("" + quantity);
                        MDToast.makeText(context, "Item added to cart !!", MDToast.LENGTH_SHORT, MDToast.TYPE_SUCCESS).show();
                    }
                } else {
                    Log.e(TAG, "onResponse: " + response);
                    MDToast.makeText(context, "" + response.body(), MDToast.TYPE_INFO, MDToast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                Log.e("fail", call.toString());
            }
        });

    }

    private void updateItem(String id, final int quantity, final MyViewHolder holder) {
        try {

            ApiService apiService = ApiUtils.getHeaderAPIService(SPData.getAppPreferences().getUsertoken());
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("quantity", quantity);
            apiService.updateCartItem(id, jsonObject).enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (response.isSuccessful()) {
                        String reponse_status = String.valueOf(response.body().get("status"));
                        if (reponse_status.matches("200")) {
                            holder.no_ofQuantity.setText("" + quantity);
                            Toast.makeText(context, "Quantity Updated!!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(context, response.code() + " " + response.message(), Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    Log.e("fail", call.toString());
                    Toast.makeText(context, "Failed to connect", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "updateItem: " + e);
        }
    }


    @Override
    public int getItemCount() {
        if (varientList != null)
            return varientList.size();
        else
            return 0;
    }

}