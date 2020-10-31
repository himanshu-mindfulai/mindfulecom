package com.mindfulai.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mindfulai.Activites.AllProductsActivity;
import com.mindfulai.Activites.MainActivity;
import com.mindfulai.Activites.ProductDetailsActivity;
import com.mindfulai.Models.DifferentVarients;
import com.mindfulai.Models.varientsByCategory.OptionsAttribute;
import com.mindfulai.Utils.GlobalEnum;
import com.mindfulai.Utils.SPData;
import com.mindfulai.ministore.R;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;

public class OptionViewAdapter extends RecyclerView.Adapter<OptionViewAdapter.OptionViewHolder> {
    private ArrayList<OptionsAttribute> optionsAttributeArrayList;
    private Context context;
    private TextView product_selling_price, product_minimum_qty, product_detail_selling_price, product_stock, product_available, product_no_ofQuantity;
    private ArrayList<DifferentVarients> differentVarientsArrayList;
    private ArrayList<String> listforchecking;
    private TextView product_detail_description;
    private TextView product_varient_id;
    private TextView product_mrp, product_detail_mrp_price, product_add_to_cart, product_detail_minqty, productName;
    private LinearLayout product_linearLayoutNoCartitems;
    private static final String TAG = "ProfileActivity";
    private ImageView productimageView;
    private TextView product_discount;
    private CardView product_discount_layout;
    private SliderView sliderView;
    private ArrayList<String> productImages;

    public OptionViewAdapter(Context context, ArrayList<OptionsAttribute> allattributes, ArrayList<DifferentVarients> differentVarientsArrayList, View view, ProductDetailsActivity productDetailsActivity, ArrayList<String> arrayList) {
        this.context = context;
        optionsAttributeArrayList = allattributes;
        this.differentVarientsArrayList = differentVarientsArrayList;
        this.listforchecking = new ArrayList<>();
        this.productImages = arrayList;
        if (productDetailsActivity != null) {
            this.product_detail_description = productDetailsActivity.findViewById(R.id.description);
            this.product_detail_selling_price = productDetailsActivity.findViewById(R.id.product_price);
            this.product_detail_mrp_price = productDetailsActivity.findViewById(R.id.product_mrp);
            this.product_detail_minqty = productDetailsActivity.findViewById(R.id.product_detail_qty);
            this.product_linearLayoutNoCartitems = productDetailsActivity.findViewById(R.id.linearLayout);
            this.product_add_to_cart = productDetailsActivity.findViewById(R.id.add_to_cart);
            this.product_no_ofQuantity = productDetailsActivity.findViewById(R.id.no_of_quantity);
            this.productName = productDetailsActivity.findViewById(R.id.product_name);
            this.product_discount = productDetailsActivity.findViewById(R.id.product_discount);
            this.product_stock = productDetailsActivity.findViewById(R.id.product_stock);
            this.product_discount_layout = productDetailsActivity.findViewById(R.id.product_discount_layout);
            sliderView = productDetailsActivity.findViewById(R.id.showSalonImageSlider);
        }
        if (view != null) {
            this.product_mrp = view.findViewById(R.id.product_mrp);
            this.product_varient_id = view.findViewById(R.id.product_varient_id);
            this.product_stock = view.findViewById(R.id.product_stock);
            this.product_available = view.findViewById(R.id.product_available);
            this.product_selling_price = view.findViewById(R.id.product_price);
            this.product_minimum_qty = view.findViewById(R.id.product_qty);
            this.product_linearLayoutNoCartitems = view.findViewById(R.id.linearLayout);
            this.product_add_to_cart = view.findViewById(R.id.add_to_cart);
            this.product_no_ofQuantity = view.findViewById(R.id.no_of_quantity);
            this.productimageView = view.findViewById(R.id.image);
            this.product_discount = view.findViewById(R.id.product_discount);
            this.product_discount_layout = view.findViewById(R.id.product_discount_layout);
        }
        assert view != null;
    }

    @NonNull
    @Override
    public OptionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.option_value_item_base, parent, false);
        return new OptionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final OptionViewHolder holder, final int positionBase) {
        try {
            showVarientsOnDropDown(holder, positionBase);

            if (positionBase > 1 && !(context instanceof ProductDetailsActivity)) {
                showVarientsOnDropDown(holder, positionBase);
                holder.linearLayoutSpinner.setVisibility(View.GONE);
                holder.linearLayoutSpinner.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showVarientsOnDropDown(final OptionViewHolder holder, int positionBase) throws Exception {
        final ArrayList<String> available = optionsAttributeArrayList.get(positionBase).getValue();
        for (int i = 0; i < available.size(); i++)
            if (differentVarientsArrayList.size() > 0
                    && differentVarientsArrayList.get(0).getOption_value().size() > 0
                    && available.get(i)
                    .equals(differentVarientsArrayList.get(0).getOption_value().get(positionBase))) {
                String price = "" + differentVarientsArrayList.get(0).getPrice();
                String sellingPrice = "" + differentVarientsArrayList.get(0).getSellingPrice();
                listforchecking.add(optionsAttributeArrayList.get(positionBase).getValue().get(i));

//                if (!sellingPrice.equals("null") && !sellingPrice.isEmpty()) {
//                    listforchecking.add(optionsAttributeArrayList.get(positionBase).getValue().get(i) + " - " + context.getString(R.string.rs) + sellingPrice);
//                } else if (!price.isEmpty() && !price.equals("null"))
//                    listforchecking.add(optionsAttributeArrayList.get(positionBase).getValue().get(i) + " - " + context.getString(R.string.rs) + price);
//                else
//                    listforchecking.add(optionsAttributeArrayList.get(positionBase).getValue().get(i) + " - " + context.getString(R.string.rs) + "N/A");
            }
        if (listforchecking.size() > 0) {
            available.remove(listforchecking.get(positionBase));
            available.add(0, listforchecking.get(positionBase));
        }

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(context, R.layout.spinner_item_view, R.id.textSpinner1);
        spinnerAdapter.addAll(available);
        spinnerAdapter.setDropDownViewResource(R.layout.spinner_drop_down_resource);
        holder.recyclerViewValues.setAdapter(spinnerAdapter);
        holder.recyclerViewValues.setPadding(20, 20, 20, 20);
        if (!(context instanceof ProductDetailsActivity))
            holder.recyclerViewValues.setSelection(0, false);
        holder.recyclerViewValues.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                holder.recyclerViewValues.setPadding(22, 22, 22, 22);

                product_add_to_cart.setVisibility(View.VISIBLE);
                product_linearLayoutNoCartitems.setVisibility(View.GONE);
                product_no_ofQuantity.setText("0");

                if (available.size() > 0) {
                    String item = available.get(position);
                    if (!listforchecking.contains(item)) {
                        for (int k = 0; k < optionsAttributeArrayList.size(); k++) {
                            if (optionsAttributeArrayList.get(k).getValue().contains(item)) {
                                if (listforchecking.size() > 0) {
                                    listforchecking.remove(k);
                                    listforchecking.add(k, item);
                                    break;
                                }
                            }
                        }
                    }

                    for (int i = 0; i < differentVarientsArrayList.size(); i++) {
                        if (Integer.parseInt(product_stock.getText().toString()) <= 0) {
                            product_add_to_cart.setText("Out of stock");
                            product_add_to_cart.setOnClickListener(null);
                        } else {
                            if (differentVarientsArrayList.get(i).getInCart() > 0) {
                                product_add_to_cart.setVisibility(View.GONE);
                                product_linearLayoutNoCartitems.setVisibility(View.VISIBLE);
                                if (SPData.getAppPreferences().getTotalCartCount() != -1) {
//                                int cartItem = SPData.getAppPreferences().getTotalCartCount() + 1;
//                                SPData.getAppPreferences().setTotalCartCount(cartItem);
//                                if (context instanceof MainActivity)
//                                    ((MainActivity) context).addBadge("" + cartItem);
//                                if (context instanceof AllProductsActivity)
//                                    ((AllProductsActivity) context).addBadge("" + cartItem);

                                }
                                product_no_ofQuantity.setText("" + differentVarientsArrayList.get(i).getInCart());
                            }
                        }
                        if (differentVarientsArrayList.get(i).getOption_value().containsAll(listforchecking)) {
                            if (product_mrp != null) {
                                String sellingPrice = "" + differentVarientsArrayList.get(i).getSellingPrice();
                                if (!sellingPrice.equals("null") &&
                                        !differentVarientsArrayList.get(i).getSellingPrice().equals(
                                                differentVarientsArrayList.get(i).getPrice()
                                        ) && !sellingPrice.isEmpty()) {
                                    float selling = Float.parseFloat(differentVarientsArrayList.get(i).getSellingPrice());
                                    float mrp = Float.parseFloat(differentVarientsArrayList.get(i).getPrice());
                                    int discount = (int) (((mrp - selling) / mrp) * 100);
                                    product_mrp.setText("\u20B9" + differentVarientsArrayList.get(i).getPrice());
                                    product_discount.setText(discount + "% off");
                                    product_selling_price.setText("\u20B9" + differentVarientsArrayList.get(i).getSellingPrice());
                                } else {
                                    product_discount_layout.setVisibility(View.INVISIBLE);
                                    product_discount.setVisibility(View.INVISIBLE);
                                    product_selling_price.setText("\u20B9" + differentVarientsArrayList.get(i).getPrice());
                                    product_mrp.setVisibility(View.GONE);
                                }
                                if (!differentVarientsArrayList.get(i).getPrice().equals("") && !differentVarientsArrayList.get(i).getPrice().equals("null"))
                                    product_mrp.setText("\u20B9" + differentVarientsArrayList.get(i).getPrice());
                                else
                                    product_mrp.setVisibility(View.GONE);

                                if (!differentVarientsArrayList.get(i).getSellingPrice().equals("")) {
                                    product_selling_price.setText("\u20B9 " + differentVarientsArrayList.get(i).getSellingPrice());
                                } else {
                                    product_selling_price.setText("\u20B9 " + differentVarientsArrayList.get(i).getPrice());
                                    product_mrp.setVisibility(View.GONE);
                                }

                                if (differentVarientsArrayList.get(i).getMinQty() != -1) {
                                    product_minimum_qty.setText("Min qty-" + differentVarientsArrayList.get(i).getMinQty());
                                } else {
                                    product_minimum_qty.setText("Min qty-" + differentVarientsArrayList.get(i).getMinQty());
                                    product_minimum_qty.setVisibility(View.GONE);
                                }
                                ArrayList<String> images = differentVarientsArrayList.get(i).getImagesList();
                                //images.addAll(productImages);
                                try {
                                    if (images != null && images.get(0) != null && !images.get(0).isEmpty())
                                        Glide.with(context).load(GlobalEnum.AMAZON_URL + images.get(0)).into(productimageView);
                                } catch (Exception e) {
                                    Log.e(TAG, "showVarientsOnDropdown:: onItemSelected: Variant image not present. " + e.getMessage());
                                }
                            } else {
                                try {
                                    String sellingPrice = "" + differentVarientsArrayList.get(i).getSellingPrice();
                                    if (!sellingPrice.equals("null") &&
                                            !differentVarientsArrayList.get(i).getSellingPrice().equals(
                                                    differentVarientsArrayList.get(i).getPrice()
                                            )) {
                                        float selling = Float.parseFloat(differentVarientsArrayList.get(i).getSellingPrice());
                                        float mrp = Float.parseFloat(differentVarientsArrayList.get(i).getPrice());
                                        int discount = (int) (((mrp - selling) / mrp) * 100);
                                        product_detail_mrp_price.setText("\u20B9" + differentVarientsArrayList.get(i).getPrice());
                                        product_discount.setText(discount + "% off");
                                        product_detail_selling_price.setText("\u20B9" + differentVarientsArrayList.get(i).getSellingPrice());
                                    } else {
                                        product_discount.setVisibility(View.GONE);
                                        product_detail_selling_price.setText("\u20B9" + differentVarientsArrayList.get(i).getPrice());
                                        product_detail_mrp_price.setVisibility(View.GONE);
                                    }
                                    if (!differentVarientsArrayList.get(i).getPrice().equals("") && !differentVarientsArrayList.get(i).getPrice().equals("null"))
                                        product_detail_mrp_price.setText("\u20B9" + differentVarientsArrayList.get(i).getPrice());
                                    else
                                        product_detail_mrp_price.setVisibility(View.GONE);
                                    if (!differentVarientsArrayList.get(i).getSellingPrice().equals(""))
                                        product_detail_selling_price.setText("\u20B9 " + differentVarientsArrayList.get(i).getSellingPrice());
                                    else {
                                        product_detail_selling_price.setText("\u20B9 " + differentVarientsArrayList.get(i).getPrice());
                                        product_detail_mrp_price.setVisibility(View.GONE);
                                    }
                                    if (differentVarientsArrayList.get(i).getMinQty() != -1) {
                                        product_detail_minqty.setText("Min qty-" + differentVarientsArrayList.get(i).getMinQty());
                                    } else {
                                        product_detail_minqty.setText("Min qty-" + differentVarientsArrayList.get(i).getMinQty());
                                        product_detail_minqty.setVisibility(View.GONE);
                                    }
                                    ArrayList<String> imagelist = differentVarientsArrayList.get(i).getImagesList();
                                    //imagelist.addAll(productImages);
                                    if (imagelist != null) {
                                        sliderView.setSliderAdapter(new SliderAdapterExample(context, imagelist));
                                        sliderView.setIndicatorAnimation(IndicatorAnimations.WORM);
                                        sliderView.setAutoCycle(false);
                                        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            SPData.getAppPreferences().setVarientMinQty(differentVarientsArrayList.get(i).getMinQty());
//                            Show the product description only for all varients
//                            if (product_detail_description != null)
//                                product_detail_description.setText(differentVarientsArrayList.get(i).getDescription());
                            if (product_varient_id != null)
                                product_varient_id.setText(differentVarientsArrayList.get(i).getId());
//
//                            if (productName != null)
//                                productName.setText(differentVarientsArrayList.get(i).getOption_value().get(0));
                            if (product_available != null)
                                product_available.setText("true");
                            SPData.getAppPreferences().setVarientAvailable("true");
                            if (!differentVarientsArrayList.get(i).getStock().equals("null")) {
                                if (product_stock != null)
                                    product_stock.setText("" + Integer.parseInt(differentVarientsArrayList.get(i).getStock()));
                                SPData.getAppPreferences().setVarientStock(Integer.parseInt(differentVarientsArrayList.get(i).getStock()));
                            } else {
                                SPData.getAppPreferences().setVarientStock(0);
                                if (product_stock != null)
                                    product_stock.setText("0");
                            }

                            SPData.getAppPreferences().setVarientId(differentVarientsArrayList.get(i).getId());
                            break;
                        } else {
                            if (product_varient_id != null)
                                product_varient_id.setText("");
                            if (product_available != null)
                                product_available.setText("false");
                            SPData.getAppPreferences().setVarientAvailable("false");
                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public int getItemCount() {
        if (optionsAttributeArrayList != null)
            return optionsAttributeArrayList.size();
        else
            return 0;
    }

    static class OptionViewHolder extends RecyclerView.ViewHolder {
        private Spinner recyclerViewValues;
        private LinearLayout linearLayoutSpinner;

        OptionViewHolder(@NonNull View itemView) {
            super(itemView);
            recyclerViewValues = itemView.findViewById(R.id.value_name_spinner);
            linearLayoutSpinner = itemView.findViewById(R.id.linear_layout_spinner);
        }
    }
}
