package com.mindfulai.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.JsonObject;
import com.mindfulai.AppPrefrences.AppPreferences;
import com.mindfulai.Models.CartInformation.Attribute;
import com.mindfulai.Models.CartInformation.CartDetailsInformation;
import com.mindfulai.Models.CartInformation.Product;
import com.mindfulai.NetworkRetrofit.ApiService;
import com.mindfulai.NetworkRetrofit.ApiUtils;
import com.mindfulai.Utils.CommonUtils;
import com.mindfulai.Utils.CustomProgressDialog;
import com.mindfulai.Utils.GlobalEnum;
import com.mindfulai.Utils.SPData;
import com.mindfulai.customclass.Constants;
import com.mindfulai.customclass.ExpandableRecyclerView;
import com.mindfulai.ministore.R;
import com.mindfulai.ui.CartFragment;

import java.text.DecimalFormat;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartAdapater extends RecyclerView.Adapter<CartAdapater.MyViewHolder> {


    private AppPreferences appPreferences;
    private Context context;
    private List<Product> cartDataArrayList;
    private List<Attribute> attributeList;
    private View layout_empty;
    private LinearLayout layout_items;
    private LinearLayout layout_payment, payment_bottom;
    private TextView tv_total_price;
    private static final String TAG = "CartAdapter";
    private TextView deliveryFee, cartTotal;
    float cartt;
    long conOrderValue;
    long conAboveOrSameValue;
    TextView deliveryNotificationText;
    CardView deliveryNotification;
    float walletAmt;
    private CartFragment cartFragment;

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView image_cartlist;
        TextView product_name, product_description, product_price, product_minQty, product_discount;
        private TextView no_ofQuantity;
        Button increase, decrease;

        public MyViewHolder(View view) {
            super(view);

            image_cartlist = view.findViewById(R.id.image_cartlist);
            product_name = view.findViewById(R.id.product_name);
            product_description = view.findViewById(R.id.product_description);
            product_price = view.findViewById(R.id.product_price);
            increase = view.findViewById(R.id.increase);
            decrease = view.findViewById(R.id.decrease);
            no_ofQuantity = view.findViewById(R.id.no_of_quantity);
            product_minQty = view.findViewById(R.id.product_qty);
            product_discount = view.findViewById(R.id.cart_discount);
        }
    }

    public CartAdapater(
            Context context,
            List<Product> cartDataArrayList,
            String cartAmount, String cartItemCount, View view,
            LinearLayout linearLayoutItem, LinearLayout linearLayoutPayment,
            LinearLayout paymentBottom, TextView tv_total_amount, TextView cartTotal,
            TextView deliveryFee,
            float cartt, long conOrderValue, long conAboveOrSameValue,
            TextView deliveryFeeNotificationText, CardView deliveryNotification, float walletAmt, CartFragment cartFragment) {

        this.context = context;
        this.cartDataArrayList = cartDataArrayList;
        this.layout_empty = view;
        this.layout_items = linearLayoutItem;
        this.layout_payment = linearLayoutPayment;
        this.tv_total_price = tv_total_amount;
        this.deliveryFee = deliveryFee;
        this.cartTotal = cartTotal;
        this.payment_bottom = paymentBottom;
        this.cartt = cartt;
        this.conOrderValue = conOrderValue;
        this.conAboveOrSameValue = conAboveOrSameValue;
        this.deliveryNotificationText = deliveryFeeNotificationText;
        this.deliveryNotification = deliveryNotification;
        this.walletAmt = walletAmt;
        this.cartFragment = cartFragment;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_cartlist_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        try {
            appPreferences = new AppPreferences(context);

            if (cartDataArrayList.get(position).getProduct().getAttributes() != null) {
                attributeList = cartDataArrayList.get(position).getProduct().getAttributes();
            }
            if (attributeList.size() >= 1) {
                StringBuffer sb = new StringBuffer();
                for (int i = 0; i < attributeList.size() - 1; i++) {
                    sb.append(attributeList.get(i).getOption().getValue());
                    sb.append(", ");
                }
                sb.append(attributeList.get(attributeList.size() - 1).getOption().getValue());
                holder.product_description.setText(CommonUtils.capitalizeWord("" + sb.toString()));
            } else
                holder.product_description.setVisibility(View.GONE);

            holder.product_name.setText(CommonUtils.capitalizeWord(cartDataArrayList.get(position).getProduct().getProduct().getName()));
            holder.no_ofQuantity.setText("" + cartDataArrayList.get(position).getQuantity());
            if (cartDataArrayList.get(position).getProduct().getSellingPrice() != null)
                holder.product_price.setText(context.getResources().getString(R.string.rs) + cartDataArrayList.get(position).getProduct().getSellingPrice() + " x " + cartDataArrayList.get(position).getQuantity());
            else
                holder.product_price.setText(context.getResources().getString(R.string.rs) + cartDataArrayList.get(position).getProduct().getPrice() + " x " + cartDataArrayList.get(position).getQuantity());

            if (cartDataArrayList.get(position).getProduct().getImages() != null) {
                Glide.with(context).load(GlobalEnum.AMAZON_URL +
                        cartDataArrayList.get(position).getProduct().getImages().getPrimary()).into(holder.image_cartlist);
            } else {
                Glide.with(context).load(context.getResources().getDrawable(R.drawable.noimage)).into(holder.image_cartlist);
            }
            if (cartDataArrayList.get(position).getProduct().getMinOrderQuantity() != null)
                holder.product_minQty.setText("Min qty-" + cartDataArrayList.get(position).getProduct().getMinOrderQuantity());
            else {
                holder.product_minQty.setText("Min qty-" + "-1");
                holder.product_minQty.setVisibility(View.GONE);
            }
            DecimalFormat df = new DecimalFormat("#.##");
            String discount = df.format(cartDataArrayList.get(position).getDiscountAmount());
            Log.e(TAG, "discount: " + discount);
            if (cartDataArrayList.get(position).getDiscountAmount() != 0) {
                holder.product_discount.setVisibility(View.VISIBLE);
                holder.product_discount.setText(
                        "Saved " +
                                context.getResources().getString(R.string.rs) +
                                Double.parseDouble(discount) * cartDataArrayList.get(position).getQuantity()
                );
            } else {
                holder.product_discount.setVisibility(View.GONE);
            }

            holder.decrease.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int minOrderQuantity = -1;
                    if (cartDataArrayList.get(holder.getAdapterPosition()).getProduct().getMinOrderQuantity() != null)
                        minOrderQuantity = cartDataArrayList.get(holder.getAdapterPosition()).getProduct().getMinOrderQuantity();
                    if (minOrderQuantity != -1) {
                        if (Integer.parseInt(holder.no_ofQuantity.getText().toString()) > minOrderQuantity) {
                            updateCartItem(holder, holder.getAdapterPosition());
                        } else {
                            deleteItem(holder, holder.getAdapterPosition());
                        }
                    } else if (Integer.parseInt(holder.no_ofQuantity.getText().toString()) > 1) {
                        updateCartItem(holder, holder.getAdapterPosition());
                    } else {
                        deleteItem(holder, holder.getAdapterPosition());
                    }
                }
            });
            holder.increase.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int stock = 0;
                    if (cartDataArrayList.get(holder.getAdapterPosition()).getProduct().getStock() != null)
                        stock = cartDataArrayList.get(holder.getAdapterPosition()).getProduct().getStock();
                    int minQty = Integer.parseInt(holder.product_minQty.getText().toString().split("qty-")[1]);
                    int currentQty = Integer.parseInt(holder.no_ofQuantity.getText().toString());
                    if (minQty != -1)
                        currentQty = currentQty + minQty;
                    String varientID = cartDataArrayList.get(holder.getAdapterPosition()).getProduct().getId();
                    if (currentQty <= stock) {
                        int quantity;
                        if (minQty != -1)
                            quantity = (Integer.parseInt(holder.no_ofQuantity.getText().toString())) + minQty;
                        else
                            quantity = (Integer.parseInt(holder.no_ofQuantity.getText().toString())) + 1;
                        updateItem(varientID, quantity, holder, holder.getAdapterPosition());
                    } else {
                        new CommonUtils(context).showErrorMessage("Current stock is less than quantity " + "( current stock is " + stock + " )");
                    }
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "onBindViewHolder: " + e);
            Toast.makeText(context, "" + e, Toast.LENGTH_SHORT).show();
        }
    }

    private void updateCartItem(MyViewHolder holder, int position) {
        String varientID = cartDataArrayList.get(position).getProduct().getId();
        int quantity;
        int minQty = Integer.parseInt(holder.product_minQty.getText().toString().split("qty-")[1]);
        if (minQty != -1)
            quantity = (Integer.parseInt(holder.no_ofQuantity.getText().toString())) - minQty;
        else
            quantity = (Integer.parseInt(holder.no_ofQuantity.getText().toString())) - 1;
        updateItem(varientID, quantity, holder, position);
    }

    private void deleteItem(MyViewHolder holder, int position) {
        if (cartDataArrayList.get(holder.getAdapterPosition()).getProduct().getMinOrderQuantity() != null) {
            int minOrderQuantity = cartDataArrayList.get(holder.getAdapterPosition()).getProduct().getMinOrderQuantity();
            Toast.makeText(context, "Min order quantity is " + minOrderQuantity, Toast.LENGTH_SHORT).show();
        }
        removeProductFromList(cartDataArrayList.get(holder.getAdapterPosition()).getProduct().getId());
        holder.itemView.setVisibility(View.GONE);
        cartDataArrayList.remove(holder.getAdapterPosition());
        notifyItemRemoved(holder.getAdapterPosition());
        if (cartDataArrayList.size() == 0) {
            layout_empty.setVisibility(View.VISIBLE);
            layout_items.setVisibility(View.GONE);
            layout_payment.setVisibility(View.GONE);
            payment_bottom.setVisibility(View.GONE);
            deliveryNotification.setVisibility(View.GONE);
        } else {
            layout_empty.setVisibility(View.GONE);
            layout_items.setVisibility(View.VISIBLE);
            layout_payment.setVisibility(View.VISIBLE);
            payment_bottom.setVisibility(View.VISIBLE);
            deliveryNotification.setVisibility(View.VISIBLE);
        }
    }

    public void getTotalPrice() {

        ApiService apiService = ApiUtils.getHeaderAPIService(SPData.getAppPreferences().getUsertoken());
        apiService.showCartItems().enqueue(new Callback<CartDetailsInformation>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<CartDetailsInformation> call, Response<CartDetailsInformation> response) {
                try {
                    if (response.isSuccessful()) {
                        CartDetailsInformation cartDetailsInfo = response.body();
                        float totalAmt = cartDetailsInfo.getData().getTotal();
                        if (cartFragment.isCarryBagAdded) {
                            totalAmt = totalAmt + Constants.CARY_BAG_PRICE;
                        }
                        if(cartFragment.discountApplied){
                            totalAmt =  totalAmt - cartFragment.discountAmt;
                        }
                        Log.e("TAG", "onResponse: total amt "+totalAmt );
                        Log.e("TAG", "onResponse: "+cartFragment.walletAmt );
                        if (cartFragment.walletAmt >= totalAmt)
                            tv_total_price.setText("Payable Amt - " + context.getResources().getString(R.string.rs) + 0.0);
                        else
                            tv_total_price.setText("Payable Amt - " + context.getResources().getString(R.string.rs)+ new DecimalFormat("#.#").format(totalAmt - cartFragment.walletAmt));
                        cartTotal.setText(context.getResources().getString(R.string.rs) +
                                new DecimalFormat("#.#").format((cartDetailsInfo.getData().getTotal() - cartDetailsInfo.getData().getDeliveryFee())));
                        deliveryFee.setText("+ " + context.getResources().getString(R.string.rs) + cartDetailsInfo.getData().getDeliveryFee());
                        float cartt = Float.parseFloat(
                                new DecimalFormat("#.#")
                                        .format((cartDetailsInfo.getData().getTotal() - cartDetailsInfo.getData().getDeliveryFee())));

                        if (cartt < conOrderValue) {
                            if (!cartDataArrayList.isEmpty()) {
                                deliveryNotification.setVisibility(View.VISIBLE);
                            }
                            deliveryNotificationText.setText(
                                    "Shop for " +
                                            context.getResources().getString(R.string.rs) +
                                            new DecimalFormat("#.#").format(conOrderValue - cartt) +
                                            " more and get this order delivered for " +
                                            context.getResources().getString(R.string.rs) +
                                            conAboveOrSameValue);
                        } else {
                            deliveryNotification.setVisibility(View.GONE);
                        }

                        cartDataArrayList = cartDetailsInfo.getData().getProducts();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(context, "" + e, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<CartDetailsInformation> call, @NonNull Throwable t) {
                Log.e("fail", call.toString());
                layout_items.setVisibility(View.GONE);
                layout_payment.setVisibility(View.GONE);
                payment_bottom.setVisibility(View.GONE);
            }
        });

    }

    @Override
    public int getItemCount() {
        if (cartDataArrayList != null)
            return cartDataArrayList.size();
        else return 0;
    }


    private void removeProductFromList(String id) {
        ApiService apiService = ApiUtils.getHeaderAPIService(appPreferences.getUsertoken());
        apiService.removeItemFromCart(id).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {
                    if (response.isSuccessful()) {
                        Log.e(TAG, "onResponse: " + response);
                        String reponse_status = String.valueOf(response.body().get("status"));
                        if (reponse_status.matches("200")) {
                            getTotalPrice();
                            SPData.getAppPreferences().setTotalCartCount(cartDataArrayList.size());
                            Toast.makeText(context, "Removed from cart !!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e("TAG", "onResponse: " + response);
                        Toast.makeText(context, response.code() + " " + response.message(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.e(TAG, "onResponse: " + e);
                    Toast.makeText(context, "" + e, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(context, "Failed to connect", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void updateItem(String id, final int quantity, final MyViewHolder holder, final int position) {
        try {
            ApiService apiService = ApiUtils.getHeaderAPIService(appPreferences.getUsertoken());
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("quantity", quantity);

            apiService.updateCartItem(id, jsonObject).enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                    if (response.isSuccessful()) {
                        String reponse_status = String.valueOf(response.body().get("status"));
                        if (reponse_status.matches("200")) {
                            getTotalPrice();
                            if (cartDataArrayList.get(position).getProduct().getSellingPrice() != null)
                                holder.product_price.setText(context.getResources().getString(R.string.rs) + cartDataArrayList.get(position).getProduct().getSellingPrice() + " x " + quantity);
                            else
                                holder.product_price.setText(context.getResources().getString(R.string.rs) + cartDataArrayList.get(position).getProduct().getPrice() + " x " + quantity);
                            holder.no_ofQuantity.setText("" + quantity);
                            Toast.makeText(context, "Quantity Updated!!", Toast.LENGTH_SHORT).show();

                        }
                    } else {
                        Toast.makeText(context, response.code() + " " + response.message(), Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    Log.e("TAG", call.toString());
                    Toast.makeText(context, "Failed to connect", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Log.e("TAG", "updateItem: " + e);
            Toast.makeText(context, "" + e, Toast.LENGTH_SHORT).show();
        }
    }

}
