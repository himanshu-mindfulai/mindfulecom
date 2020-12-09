package com.mindfulai.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.JsonObject;
import com.mindfulai.Activites.AddAddressActivity;
import com.mindfulai.Activites.CommonActivity;
import com.mindfulai.Activites.CustomerAddActivity;
import com.mindfulai.Activites.OrderHistoryDetailsActivity;
import com.mindfulai.Activites.OrderPlacedActivity;
import com.mindfulai.Activites.PromoCodeActivity;
import com.mindfulai.Adapter.CartAdapater;
import com.mindfulai.Adapter.SearchCustomerAdapter;
import com.mindfulai.Adapter.TimePickerSlotsAdapter;
import com.mindfulai.Adapter.UserAddressesAdapter;
import com.mindfulai.AppPrefrences.AppPreferences;
import com.mindfulai.Models.CartInformation.CartDetailsInformation;
import com.mindfulai.Models.CartInformation.Product;
import com.mindfulai.Models.CustomerInfo.CustomerData;
import com.mindfulai.Models.CustomerInfo.User;
import com.mindfulai.Models.SlotModelBase;
import com.mindfulai.Models.SlotModelData;
import com.mindfulai.Models.UserBaseAddress;
import com.mindfulai.Models.UserDataAddress;
import com.mindfulai.Models.config.ConfigResponse;
import com.mindfulai.Models.orderDetailInfo.OrderDetailInfo;
import com.mindfulai.NetworkRetrofit.ApiService;
import com.mindfulai.NetworkRetrofit.ApiUtils;
import com.mindfulai.NetworkRetrofit.ServerURL;
import com.mindfulai.Utils.CommonUtils;
import com.mindfulai.Utils.CustomProgressDialog;
import com.mindfulai.Utils.SPData;
import com.mindfulai.customclass.Constants;
import com.mindfulai.ministore.R;
import com.razorpay.Checkout;
import com.razorpay.PaymentData;
import com.razorpay.PaymentResultWithDataListener;
import com.valdesekamdem.library.mdtoast.MDToast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.mindfulai.customclass.Constants.CARY_BAG_PRICE;

/**
 * A simple {@link Fragment} subclass.
 */
public class CartFragment extends Fragment implements PaymentResultWithDataListener {
    private LinearLayout layout_payment, layout_items;
    private TextView tv_total_amount;
    private TextView cartTotal;
    private List<Product> cartDataArrayList;
    private ArrayList<UserDataAddress> userDataAddressArrayList;
    private RecyclerView rv_cart;
    private CartAdapater cartAdapater;
    private String cart_itemcount1, cart_amount1;
    private ShimmerFrameLayout shimmerFrameLayout;
    private LinearLayout signinButton;
    private RecyclerView recyclerViewCustomerList;
    private ArrayList<String> allCustomer;
    private static final String TAG = "CartFragment";
    private SearchCustomerAdapter adapter;
    private RecyclerView recyclerViewAddress;
    private UserAddressesAdapter addressesAdapter;
    private AlertDialog alertDialog1;
    private AppPreferences appPreferences;
    private EditText editTextSearchCustomer;
    private FloatingActionButton floatingActionButtonAddCustomer;
    private TextView tv_payment;
    private Intent intentResult;
    private TextView timeSlot;
    private String coupon="";
    private LinearLayout payment_bottom;
    private long conOrderValue, conAboveOrSameCharge;
    private CardView deliveryNotification;
    private TextView deliveryNotificationText;
    private CheckBox checkBox;
    private int resultCode = -1;
    private TextView applyCoupon;
    private RelativeLayout subtotalRv;
    private TextView subtotalTxt;
    private TextView deliveryCharge;
    private RelativeLayout deliveryChargeLayout;
    private TextView caryBagCharge;
    private RelativeLayout caryBagChargeLayout;
    private TextView couponDiscount;
    private RelativeLayout couponDiscountLayout;
    private TextView walletBalance;
    public float walletAmt;
    private RelativeLayout rvWalletLayout;
    private CustomProgressDialog customProgressDialog;
    public boolean isCarryBagAdded;
    public float discountAmt;
    public boolean discountApplied;

    public CartFragment() {
        // Required empty public constructor
    }


    @SuppressLint("RestrictedApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cart, container, false);
        try {
            appPreferences = new AppPreferences(requireActivity());
            signinButton = view.findViewById(R.id.no_products);
            tv_payment = view.findViewById(R.id.tv_payment);
            floatingActionButtonAddCustomer = view.findViewById(R.id.add_customer_btn);
            checkBox = view.findViewById(R.id.checkbox_cary_bag);
            applyCoupon = view.findViewById(R.id.apply_coupon);
            walletBalance = view.findViewById(R.id.tv_wallet_balance);
            floatingActionButtonAddCustomer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getActivity(), CustomerAddActivity.class));
                }
            });
            deliveryNotification = view.findViewById(R.id.delivery_notification);
            deliveryNotificationText = view.findViewById(R.id.delivery_notification_text);
            editTextSearchCustomer = view.findViewById(R.id.search_customer);
            recyclerViewCustomerList = view.findViewById(R.id.search_customer_list);
            shimmerFrameLayout = view.findViewById(R.id.shimmer_view_container);
            layout_payment = view.findViewById(R.id.layout_payment);
            payment_bottom = view.findViewById(R.id.payment_bottom);
            rv_cart = view.findViewById(R.id.rv_cart);
            LinearLayoutManager verticalLayoutManager
                    = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, true);
            rv_cart.setLayoutManager(verticalLayoutManager);
            layout_items = view.findViewById(R.id.layout_items);
            tv_total_amount = view.findViewById(R.id.tv_total_amount);
            cartTotal = view.findViewById(R.id.cart_total);
            subtotalTxt = view.findViewById(R.id.subtotal_amount);
            deliveryCharge = view.findViewById(R.id.delivery_fee);
            caryBagCharge = view.findViewById(R.id.carybag_charge);
            couponDiscount = view.findViewById(R.id.coupon_discount);

            subtotalRv = view.findViewById(R.id.subtotal_rv);
            deliveryChargeLayout = view.findViewById(R.id.dv_charge_layout);
            caryBagChargeLayout = view.findViewById(R.id.carybag_layout);
            couponDiscountLayout = view.findViewById(R.id.coupon_discount_layout);
            rvWalletLayout = view.findViewById(R.id.wallet_layout);
            subtotalRv.setVisibility(View.GONE);
            couponDiscountLayout.setVisibility(View.GONE);
            caryBagChargeLayout.setVisibility(View.GONE);
            deliveryChargeLayout.setVisibility(View.GONE);
            rvWalletLayout.setVisibility(View.GONE);

            allCustomer = new ArrayList<>();
            cartDataArrayList = new ArrayList<>();
            userDataAddressArrayList = new ArrayList<>();
            intentResult = new Intent();
            Checkout.preload(getActivity());
            Checkout.clearUserData(getActivity());
            if(SPData.showCaryBag()){
                checkBox.setVisibility(View.VISIBLE);
            }else
                checkBox.setVisibility(View.GONE);

            editTextSearchCustomer.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.toString().isEmpty()) {
                        recyclerViewCustomerList.setVisibility(View.GONE);
                    } else
                        recyclerViewCustomerList.setVisibility(View.VISIBLE);
                }

                @Override
                public void afterTextChanged(Editable s) {
                    filter(s.toString());
                }
            });

            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    isCarryBagAdded = b;
                    getTotalPrice();
                    if (b) {
                        caryBagChargeLayout.setVisibility(View.VISIBLE);
                        caryBagCharge.setText("+ "+getString(R.string.rs)+CARY_BAG_PRICE);
                        caryBagCharge.setTextColor(getResources().getColor(R.color.colorError));
                    } else {
                        caryBagChargeLayout.setVisibility(View.GONE);
                    }
                }
            });
            tv_payment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(SPData.showPickUp()){
                    String[] items = {"Delivery","Pickup"};

                    final int[] checked = {0};
                    new MaterialAlertDialogBuilder(getContext())
                            .setTitle("Delivery or pickup?")
                            .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (checked[0] == 1) {
                                        new UserAddressesAdapter(getContext(),coupon,checkBox.isChecked()).showPaymentMode();
                                    } else {
                                        promptDialog();
                                    }
                                }
                            })
                            .setSingleChoiceItems(items, checked[0], new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    checked[0] = which;
                                }
                            }).show();
                    }else{
                        new UserAddressesAdapter(getContext(),coupon,checkBox.isChecked()).showPaymentMode();
                    }

                }
            });
            applyCoupon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   startActivityForResult(new Intent(getActivity(), PromoCodeActivity.class), 0);
                }
            });
        } catch (Exception e) {
            Toast.makeText(getActivity(), "" + e, Toast.LENGTH_SHORT).show();
            Log.e(TAG, "onCreateView: " + e);
        }
        return view;
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
                        if (isCarryBagAdded) {
                            totalAmt = totalAmt + CARY_BAG_PRICE;
                        }
                        if(discountApplied){
                            totalAmt =  totalAmt - discountAmt;
                        }
                        if (walletAmt >= totalAmt)
                            tv_total_amount.setText("Payable Amt - " + getResources().getString(R.string.rs) + 0);
                        else
                            tv_total_amount.setText("Payable Amt - " + getResources().getString(R.string.rs)+new DecimalFormat("#.#").format(totalAmt - walletAmt));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "" + e, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<CartDetailsInformation> call, @NonNull Throwable t) {
                Log.e("fail", call.toString());
            }
        });

    }
    private void getWallet(CartDetailsInformation cartDetailsInfo) {
        ApiService apiService = ApiUtils.getHeaderAPIService(SPData.getAppPreferences().getUsertoken());
        apiService.getProfileDetails().enqueue(new Callback<CustomerData>() {
            @Override
            public void onResponse(Call<CustomerData> call, Response<CustomerData> response) {
                if (response.isSuccessful()){
                    walletAmt = response.body().getData().getUser().getWallet();
                    walletBalance.setText(getResources().getString(R.string.rs) + response.body().getData().getUser().getWallet());
                    float totalAmt = cartDetailsInfo.getData().getTotal() ;
                    if (walletAmt >= totalAmt)
                        tv_total_amount.setText("Payable Amt - " + getResources().getString(R.string.rs) + 0);
                    else
                        tv_total_amount.setText("Payable Amt - " + getResources().getString(R.string.rs)+new DecimalFormat("#.#").format(totalAmt - walletAmt));

                }
            }

            @Override
            public void onFailure(Call<CustomerData> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
    public void promptDialog() {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_pickk_address, null);
        recyclerViewAddress = view.findViewById(R.id.recycler_view_address);
        TextView timeSlottext = view.findViewById(R.id.select_time_text);
        TextView addAddress = view.findViewById(R.id.add_address);
        timeSlot = view.findViewById(R.id.time_slot);
        ImageView close = view.findViewById(R.id.close);

        if (SPData.showTimeSlotPicker()) {
            timeSlottext.setText("Select time slot and address");
            timeSlot.setText("Pick time");
           timeSlot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pickDate(timeSlot);
                }
            });
        } else {
            timeSlottext.setText("Select address");
            timeSlot.setVisibility(View.GONE);
        }
        addAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getActivity(), AddAddressActivity.class).putExtra("title", "Add Address"), 1);
            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerViewAddress.setLayoutManager(linearLayoutManager);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(requireActivity());
        alertDialog.setView(view);
        alertDialog1 = alertDialog.create();
        alertDialog1.setCanceledOnTouchOutside(false);
        alertDialog1.show();
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog1.dismiss();
            }
        });
        getAllAddress(recyclerViewAddress);
    }

    private void pickDate(TextView timeSlot) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), (view, year, month, dayOfMonth) -> {
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, dayOfMonth);
            Date date = calendar.getTime();
            try {
                pickTime(timeSlot, date);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void pickTime(TextView timeSlot, Date date) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = getLayoutInflater().inflate(R.layout.time_picker_custom_layout, null);
        builder.setView(dialogView);
        AlertDialog alertDialog = builder.create();
        Date currentDate = new Date();
        SimpleDateFormat sDFormart = new SimpleDateFormat("dd-MM-YYYY");
        ArrayList<String> stringArrayList = new ArrayList<>();
        stringArrayList.add("9:00 am - 1:00 pm");
        stringArrayList.add("1:00 pm - 5:00 pm");
        stringArrayList.add("5:00 pm - 9:00 pm");
        String[] timeSlots = {"9:00 am","1:00 pm","5:00 pm","9:00 pm"};
        Log.e("TAG", "pickTime: 1 "+sDFormart.format(currentDate));
        Log.e("TAG", "pickTime: 2 "+sDFormart.format(date) );
        if(sDFormart.format(currentDate).equals(sDFormart.format(date))){
            Log.e("TAG", "pickTime: equal" );
            try {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a");
                Date currenttime = simpleDateFormat.parse(simpleDateFormat.format(date));
                for (String time : timeSlots) {
                    Date time1 = simpleDateFormat.parse(time);
                    if (currenttime.before(time1) && time.equals(timeSlots[0])) {
                        stringArrayList.clear();
                        break;
                    } else if (currenttime.before(time1) && time.equals(timeSlots[1])) {
                        stringArrayList.remove(0);
                        break;
                    } else if (currenttime.before(time1) && time.equals(timeSlots[2])) {
                        stringArrayList.remove(0);
                        stringArrayList.remove(1);
                        break;
                    } else if (currenttime.before(time1) && time.equals(timeSlots[3]) || currenttime.after(time1) && time.equals(timeSlots[3])) {
                        stringArrayList.clear();
                        break;
                    }
                }
            }catch (Exception e){
                Log.e("TAG", "pickTime: "+e);
            }
        }
      if(stringArrayList.size()>0){
        RecyclerView recyclerView = dialogView.findViewById(R.id.time_picker_recycler_layout);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        SimpleDateFormat sdf = new SimpleDateFormat(("dd-MM-YYYY EEEE"));
        String dateString = sdf.format(date);
        TimePickerSlotsAdapter slotsAdapter = new TimePickerSlotsAdapter(getContext(), stringArrayList, timeSlot, dateString, alertDialog);
        recyclerView.setAdapter(slotsAdapter);
        alertDialog.show();
      }else{
          Toast.makeText(getActivity(), "No time found for selected date", Toast.LENGTH_SHORT).show();
      }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        this.resultCode = resultCode;
        if (resultCode != 1)
            getAllAddress(recyclerViewAddress);
        else {
            boolean couponApplied = data.getBooleanExtra("coupon_applied", false);
            if (couponApplied)
                applyCouponResult(data);

        }
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onResume() {
        super.onResume();
        try {
            Log.e("TAG", "onResume: " );
            if (resultCode != 1){
                if (!SPData.getAppPreferences().getUsertoken().isEmpty()) {
                    cartDataArrayList.clear();
                    shimmerFrameLayout.startShimmerAnimation();
                    getConfig();
                    //getAllCart();
                } else {
                    signinButton.setVisibility(View.VISIBLE);
                    shimmerFrameLayout.setVisibility(View.GONE);
                }
            if (appPreferences.getUserType().equals("staff") && !appPreferences.getUserType().equals("")) {
                floatingActionButtonAddCustomer.setVisibility(View.VISIBLE);
                allCustomer.clear();
                new SearchCustomer().execute(ServerURL.SERVER_URL + "/api/user/customer");
            } else {
                floatingActionButtonAddCustomer.setVisibility(View.GONE);
                editTextSearchCustomer.setVisibility(View.GONE);
            }
            }

        } catch (Exception e) {
            Toast.makeText(getActivity(), "" + e, Toast.LENGTH_SHORT).show();
            Log.e("TAG", "onResume: " + e);
        }
    }

    private void getConfig() {
        ApiService apiService = ApiUtils.getHeaderAPIService(
                SPData.getAppPreferences().getUsertoken()
        );
        apiService.getConfig().enqueue(new Callback<ConfigResponse>() {
            @Override
            public void onResponse(Call<ConfigResponse> call, Response<ConfigResponse> response) {
                try {
                    if (response.isSuccessful()) {
                        conOrderValue = response.body().getData().getDeliveryCharges().getOrderValue();
                        conAboveOrSameCharge = response.body().getData().getDeliveryCharges().getAboveOrSameValueCharge();
                        CARY_BAG_PRICE = response.body().getData().getCarryBagPrice();
                    } else {
                        Log.e("CartConfig", response.message());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    Log.e("TAG", "onResponse: " + resultCode);

                        getAllCart();
                }
            }

            @Override
            public void onFailure(Call<ConfigResponse> call, Throwable t) {
                t.printStackTrace();
                    getAllCart();
            }
        });
    }

    private void filter(String text) {
        try {
            ArrayList<String> filterdNames = new ArrayList<>();
            for (String s : allCustomer) {
                if (s.toLowerCase().contains(text.toLowerCase())) {
                    filterdNames.add(s);
                }
            }
            if (adapter != null)
                adapter.filterList(filterdNames);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "" + e, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPaymentSuccess(String s, PaymentData paymentData) {
        try {
            Log.e("TAG", "onPaymentSuccess: " );
            String sid = getActivity().getSharedPreferences("order", Context.MODE_PRIVATE).getString("sid", "");
            String aid = getActivity().getSharedPreferences("order", Context.MODE_PRIVATE).getString("aid", "");
            verifyPayment(aid, sid, paymentData);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "" + e, Toast.LENGTH_SHORT).show();
            Log.e("TAG", "onPaymentSuccess: " + e);
        }
    }

    private static final String HMAC_SHA256_ALGORITHM = "HmacSHA256";

    private void verifyPayment(String aid, String sid, PaymentData paymentData) {
        try {
            final CustomProgressDialog customProgressDialog = CommonUtils.showProgressDialog(getActivity(),
                    "Verifying payment...");
            ApiService apiService = ApiUtils.getHeaderAPIService(SPData.getAppPreferences().getUsertoken());
            JsonObject jsonObject = new JsonObject();
            SecretKeySpec signingKey = new SecretKeySpec("okcGFzgWBwvbeQVDSoeK25wh".getBytes(), HMAC_SHA256_ALGORITHM);
            Mac mac = Mac.getInstance(HMAC_SHA256_ALGORITHM);
            mac.init(signingKey);
            String data = paymentData.getOrderId() + "|" + paymentData.getPaymentId();
            byte[] rawHMaC = mac.doFinal(data.getBytes());
            String signature = DatatypeConverter.printHexBinary(rawHMaC).toLowerCase();
            Log.e("TAG", "verifyPayment: "+paymentData.getPaymentId() );
            Log.e("TAG", "verifyPayment: "+paymentData.getOrderId() );

            jsonObject.addProperty("paymentId", paymentData.getPaymentId());
            jsonObject.addProperty("orderId", paymentData.getOrderId());
            jsonObject.addProperty("signature", signature);
            apiService.verifyOnlinePayment(jsonObject).enqueue(new Callback<OrderDetailInfo>() {
                @Override
                public void onResponse(Call<OrderDetailInfo> call, Response<OrderDetailInfo> response) {
                    CommonUtils.hideProgressDialog(customProgressDialog);

                    if (response.isSuccessful()) {
                        try {
                            SPData.getAppPreferences().setPaymentSuccess(true);
                            SPData.getAppPreferences().setTotalCartCount(0);
                            OrderDetailInfo placeOrder = response.body();
                            String order_id = placeOrder.getData().getOrderId();
                            String payment_method = placeOrder.getData().getPaymentMethod();
                            String payment_amount = ""+placeOrder.getData().getAmount();
                            Intent intent =new Intent(getActivity(),OrderPlacedActivity.class);
                            intent.putExtra("order_id",""+order_id);
                            intent.putExtra("payment_method",""+payment_method);
                            intent.putExtra("payment_amount",""+payment_amount);
                            startActivity(intent);

                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(), "" + e, Toast.LENGTH_SHORT).show();
                            Log.e("TAG", "onResponse: " + e);
                        }
                    } else {
                        Log.e("TAG", "onResponse: " + response);
                        Toast.makeText(getActivity(), "" + response.message(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<OrderDetailInfo> call, Throwable t) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onPaymentError(int i, String s, PaymentData paymentData) {
        Log.e(TAG, "onPaymentError:: response:: " + s);
    }



    private void applyCouponResult(Intent data) {
        discountApplied = true;
        discountAmt = data.getFloatExtra("discount",0);
        coupon = data.getStringExtra("code");
        applyCoupon.setText("Coupon applied successfully.\nTotal discount: " + getContext().getResources().getString(R.string.rs) + discountAmt);
        applyCoupon.setTextColor(getContext().getResources().getColor(R.color.colorGreen));
        couponDiscountLayout.setVisibility(View.VISIBLE);
        couponDiscount.setText( "- "+getContext().getResources().getString(R.string.rs) + discountAmt);
        couponDiscount.setTextColor(getContext().getResources().getColor(R.color.colorGreen));
        getTotalPrice();
    }

    @SuppressLint("StaticFieldLeak")
    class SearchCustomer extends AsyncTask<String, Void, ArrayList<String>> {

        @Override
        protected ArrayList<String> doInBackground(String... strings) {
            try {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(strings[0])
                        .get()
                        .addHeader("Content-Type", "application/json;charset=utf-8")
                        .addHeader("token", SPData.getAppPreferences().getUsertoken())
                        .build();
                okhttp3.Response response = client.newCall(request).execute();
                String json = response.body().string();
                JSONObject jsonObject1 = new JSONObject(json);
                JSONArray jsonArray = jsonObject1.getJSONArray("data");
                for (int i = 0; i < jsonArray.length(); i++)
                    allCustomer.add(jsonArray.getJSONObject(i).getString("full_name"));

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "" + e, Toast.LENGTH_SHORT).show();
                Log.e("TAG", "doInBackground: " + e);
            }

            return allCustomer;
        }

        @Override
        protected void onPostExecute(ArrayList<String> s) {
            super.onPostExecute(s);
            try {
                recyclerViewCustomerList.setHasFixedSize(true);
                recyclerViewCustomerList.setLayoutManager(new LinearLayoutManager(getActivity()));
                adapter = new SearchCustomerAdapter(getActivity(), s, CartFragment.this);
                recyclerViewCustomerList.setAdapter(adapter);
                recyclerViewCustomerList.setVisibility(View.GONE);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "" + e, Toast.LENGTH_SHORT).show();
            }

        }
    }

    private void getAllAddress(final RecyclerView recyclerViewAddress) {
        try {
            final CustomProgressDialog customProgressDialog = CommonUtils.showProgressDialog(getActivity(),
                    "Getting addresses ... ");
            ApiService apiService = ApiUtils.getHeaderAPIService(SPData.getAppPreferences().getUsertoken());
            apiService.getUserBaseAddress().enqueue(new Callback<UserBaseAddress>() {
                @Override
                public void onResponse(@NonNull Call<UserBaseAddress> call, @NonNull Response<UserBaseAddress> response) {
                    if (response.isSuccessful()) {
                        try {
                            CommonUtils.hideProgressDialog(customProgressDialog);
                            assert response.body() != null;
                            userDataAddressArrayList = response.body().getData();
                            addressesAdapter = new UserAddressesAdapter(
                                    requireActivity(),
                                    userDataAddressArrayList,
                                    CartFragment.this,
                                    alertDialog1,
                                    true,
                                    tv_total_amount.getText().toString(),
                                    "PRODUCT",
                                    null,
                                    timeSlot,
                                    coupon,
                                    checkBox.isChecked()
                            );

                            recyclerViewAddress.setAdapter(addressesAdapter);
                            addressesAdapter.notifyDataSetChanged();
                        } catch (Exception e) {
                            Toast.makeText(getActivity(), "" + e, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        CommonUtils.hideProgressDialog(customProgressDialog);
                        Log.e(TAG, "onResponse: " + response);
                    }
                }

                @Override
                public void onFailure(Call<UserBaseAddress> call, Throwable t) {
                    CommonUtils.hideProgressDialog(customProgressDialog);
                    Log.e(TAG, "onFailure: " + t.getMessage());
                }
            });
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "getAllAddress: " + e);
        }
    }


    private void getAllCart() {
        try {
            customProgressDialog = CommonUtils.showProgressDialog(getActivity(),
                    "Please wait ... ");
            ApiService apiService = ApiUtils.getHeaderAPIService(SPData.getAppPreferences().getUsertoken());

            apiService.showCartItems().enqueue(new Callback<CartDetailsInformation>() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onResponse(Call<CartDetailsInformation> call, Response<CartDetailsInformation> response) {

                    try {
                        CommonUtils.hideProgressDialog(customProgressDialog);
                        shimmerFrameLayout.stopShimmerAnimation();
                        shimmerFrameLayout.setVisibility(View.GONE);
                        if (response.isSuccessful()) {
                            CartDetailsInformation cartDetailsInfo = response.body();
                            cartDataArrayList = cartDetailsInfo.getData().getProducts();
                            if (cartDataArrayList.size() == 0) {
                                signinButton.setVisibility(View.VISIBLE);
                                layout_items.setVisibility(View.GONE);
                                layout_payment.setVisibility(View.GONE);
                                payment_bottom.setVisibility(View.GONE);
                                deliveryNotification.setVisibility(View.GONE);
                                if (appPreferences.getUserType().equals("staff"))
                                    editTextSearchCustomer.setVisibility(View.GONE);
                            } else {
                                getWallet(cartDetailsInfo);
                                signinButton.setVisibility(View.GONE);
                                layout_items.setVisibility(View.VISIBLE);
                                payment_bottom.setVisibility(View.VISIBLE);
                                layout_payment.setVisibility(View.VISIBLE);

                                subtotalRv.setVisibility(View.VISIBLE);
                                deliveryChargeLayout.setVisibility(View.VISIBLE);
                                rvWalletLayout.setVisibility(View.VISIBLE);

                                if (appPreferences.getUserType().equals("staff")) {
                                    editTextSearchCustomer.setVisibility(View.VISIBLE);
                                    tv_payment.setVisibility(View.GONE);
                                } else
                                    tv_payment.setVisibility(View.VISIBLE);
                                float cartt = Float.parseFloat(
                                        new DecimalFormat("#.#")
                                                .format((cartDetailsInfo.getData().getTotal() - cartDetailsInfo.getData().getDeliveryFee())));
                                if (conOrderValue == 0) {
                                    deliveryNotification.setVisibility(View.GONE);
                                } else {
                                    deliveryNotification.setVisibility(View.VISIBLE);
                                    if (cartt < conOrderValue) {
                                        deliveryNotification.setVisibility(View.VISIBLE);
                                        deliveryNotificationText.setText(
                                                "Shop for " +
                                                        getContext().getResources().getString(R.string.rs) +
                                                        new DecimalFormat("#.#").format(conOrderValue - cartt) +
                                                        " more and get this order delivered for " +
                                                        getContext().getResources().getString(R.string.rs) +
                                                        conAboveOrSameCharge);
                                    } else {
                                        deliveryNotification.setVisibility(View.GONE);
                                    }
                                }
                                deliveryCharge.setText( "+ "+getContext().getResources().getString(R.string.rs) + cartDetailsInfo.getData().getDeliveryFee());
                                int items_count = 0;
                                for (int i = 0; i < cartDataArrayList.size(); i++) {
                                    items_count = items_count + cartDataArrayList.get(i).getQuantity();
                                    cart_itemcount1 = String.valueOf(items_count);
                                }

                                subtotalTxt.setText(getResources().getString(R.string.rs) +
                                        new DecimalFormat("#.#").format((cartDetailsInfo.getData().getTotal() - cartDetailsInfo.getData().getDeliveryFee())));

                                cart_amount1 = String.valueOf(cartDetailsInfo.getData().getTotal());
                                cartAdapater = new CartAdapater(
                                        getActivity(),
                                        cartDataArrayList,
                                        cart_amount1,
                                        cart_itemcount1,
                                        signinButton,
                                        layout_items,
                                        layout_payment,
                                        payment_bottom,
                                        tv_total_amount,
                                        subtotalTxt,
                                        deliveryCharge,
                                        cartt,
                                        conOrderValue,
                                        conAboveOrSameCharge,
                                        deliveryNotificationText,
                                        deliveryNotification,
                                        walletAmt,
                                        CartFragment.this
                                );
                                rv_cart.setAdapter(cartAdapater);
                            }
                        } else {
                            handleNoCartItem();
                        }
                    } catch (Exception e) {
                        Toast.makeText(getActivity(), ""+e, Toast.LENGTH_SHORT).show();
                        handleNoCartItem();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<CartDetailsInformation> call, @NonNull Throwable t) {
                    handleNoCartItem();
                }
            });
        } catch (Exception e) {
            Log.e("TAG", "getAllCart: " + e);
            Toast.makeText(getActivity(), ""+e, Toast.LENGTH_SHORT).show();
            handleNoCartItem();
        }
    }

    private void handleNoCartItem() {
        CommonUtils.hideProgressDialog(customProgressDialog);
        shimmerFrameLayout.stopShimmerAnimation();
        shimmerFrameLayout.setVisibility(View.GONE);
        signinButton.setVisibility(View.VISIBLE);
        layout_items.setVisibility(View.GONE);
        layout_payment.setVisibility(View.GONE);
        payment_bottom.setVisibility(View.GONE);
        deliveryNotification.setVisibility(View.GONE);
        if (appPreferences.getUserType().equals("staff"))
            editTextSearchCustomer.setVisibility(View.GONE);
    }
}