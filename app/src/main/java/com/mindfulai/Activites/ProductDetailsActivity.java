package com.mindfulai.Activites;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.TextViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.gson.JsonObject;
import com.mindfulai.Adapter.OptionViewAdapter;
import com.mindfulai.Adapter.ProductsAdapter;
import com.mindfulai.Adapter.ReviewsAdapter;
import com.mindfulai.Adapter.SliderAdapterExample;
import com.mindfulai.Adapter.TimePickerSlotsAdapter;
import com.mindfulai.Adapter.UserAddressesAdapter;
import com.mindfulai.Models.DifferentVarients;
import com.mindfulai.Models.ReviewData.Datum;
import com.mindfulai.Models.ReviewData.ReviewData;
import com.mindfulai.Models.SlotModelBase;
import com.mindfulai.Models.SlotModelData;
import com.mindfulai.Models.UserBaseAddress;
import com.mindfulai.Models.UserDataAddress;
import com.mindfulai.Models.VarientById.VarientByIdResponse;
import com.mindfulai.Models.VarientById.VarientByIdResponseData;
import com.mindfulai.Models.VendorBase;
import com.mindfulai.Models.orderDetailInfo.OrderDetailInfo;
import com.mindfulai.Models.varientsByCategory.Images;
import com.mindfulai.Models.varientsByCategory.OptionsAttribute;
import com.mindfulai.Models.varientsByCategory.Varient;
import com.mindfulai.Models.varientsByCategory.VarientsByCategory;
import com.mindfulai.NetworkRetrofit.ApiService;
import com.mindfulai.NetworkRetrofit.ApiUtils;
import com.mindfulai.Utils.CommonUtils;
import com.mindfulai.Utils.CustomProgressDialog;
import com.mindfulai.Utils.SPData;
import com.mindfulai.ministore.R;
import com.mingle.sweetpick.CustomDelegate;
import com.mingle.sweetpick.SweetSheet;
import com.razorpay.PaymentData;
import com.razorpay.PaymentResultWithDataListener;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.valdesekamdem.library.mdtoast.MDToast;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.nikartm.support.ImageBadgeView;

import static com.mindfulai.Utils.CommonUtils.capitalizeWord;

public class ProductDetailsActivity extends AppCompatActivity implements PaymentResultWithDataListener {

    private static final String TAG = "ProductDetails";
    private static final String JWT_SECRET = "GrannyProductEncodeJwt";
    private TextView product_name;
    private TextView description;
    private RecyclerView rv_reviews;
    private String productName;
    private String brand;
    private String vendor;
    private String video;
    private String varient_id;
    private String category_id;
    private String description_;
    private int reviews_count;
    private TextView no_ofQuantity;
    private float ratingvalue = 0f;
    private ReviewsAdapter reviewsAdapter;
    private List<Datum> reviewList = new ArrayList<>();
    private ArrayList<OptionsAttribute> allattributes;
    private ArrayList<DifferentVarients> differentVarientsArrayList;
    private ArrayList<ArrayList<OptionsAttribute>> alloptionsAttributeArrayList = new ArrayList<>();
    private ArrayList<ArrayList<DifferentVarients>> alldifferentVarientList = new ArrayList<>();
    private ProductsAdapter productAdapter;
    private Button add_to_cart;
    private Intent intent;
    private LinearLayout linearLayoutNoCartitems;
    private boolean isInWishlist;
    private ImageView wishListIcon;
    private Response<VendorBase> vendorData;
    private ImageBadgeView cartBadge;
    private TextView product_minQty, product_detail_selling_price, product_detail_mrp_price;
    private TextView brand_name, text_ratings, product_reviews;
    private List<com.mindfulai.Models.varientsByCategory.Datum> varientList = new ArrayList<>();
    private ShimmerFrameLayout shimmerRelatedProducts;
    private SliderView sliderView;
    private RecyclerView rvRelatedProducts;
    private float sellingPrice, ftproductPrice;
    private boolean isRecommended, codAvailable;
    private String productMinQty;
    private int stock;
    private ArrayList<String> images;
    private double average_rating;
    private AlertDialog alertDialog1;
    private TextView product_detail_discount;
    private TextView timeSlot;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_product_details);
            if(SPData.showServicesTab() && SPData.getProductsOrServices().equals("SERVICES")){
                setTitle("Service Details");
            } else {
                setTitle("Product Details");
            }
            category_id = getIntent().getStringExtra("category_id");
            shimmerRelatedProducts = findViewById(R.id.shimmer_related_products);
            rvRelatedProducts = findViewById(R.id.related_products);
            rvRelatedProducts.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
            rvRelatedProducts.setHasFixedSize(true);
            getRealtedProducts();
            brand_name = findViewById(R.id.vendor_name_detail);
            intent = new Intent();
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
            RecyclerView recycler_options = findViewById(R.id.recyclerview_option_values);
            sliderView = findViewById(R.id.showSalonImageSlider);
            TextView share = findViewById(R.id.share);
            linearLayoutNoCartitems = findViewById(R.id.linearLayout);
            share.setOnClickListener(v -> {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "MindFul Ecom");
                Map<String, Object> claims = new HashMap<>();
                claims.put("varientId", varient_id);
                claims.put("stock", stock);
                String compactProductData = Jwts.builder()
                        .setClaims(claims)
                        .signWith(SignatureAlgorithm.HS256, JWT_SECRET.getBytes())
                        .compact();
                String shareMessage = "Let me recommend you this product: https://" + SPData.getShareDomain() + "/product/" + compactProductData;
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                startActivity(Intent.createChooser(shareIntent, "choose one"));
            });
            Button increase = findViewById(R.id.increase);
            Button decrease = findViewById(R.id.decrease);
            TextView textReturnable = findViewById(R.id.text_returnable);
            no_ofQuantity = findViewById(R.id.no_of_quantity);
            product_name = findViewById(R.id.product_name);
            product_detail_selling_price = findViewById(R.id.product_price);
            product_detail_mrp_price = findViewById(R.id.product_mrp);
            product_minQty = findViewById(R.id.product_detail_qty);
            product_detail_discount = findViewById(R.id.product_discount);

            product_reviews = findViewById(R.id.product_reviews);
            wishListIcon = findViewById(R.id.ic_wishlist);
            text_ratings = findViewById(R.id.text_ratings);
            TextView add_review = findViewById(R.id.add_review);

            description = findViewById(R.id.description);
            add_to_cart = findViewById(R.id.add_to_cart);
            Button btBuyNow = findViewById(R.id.buy_now);
            rv_reviews = findViewById(R.id.rv_reviews);

            if(SPData.showServicesTab() && SPData.getProductsOrServices().equals("SERVICES")){
                btBuyNow.setVisibility(View.VISIBLE);
                add_to_cart.setVisibility(View.GONE);
            } else {
                btBuyNow.setVisibility(View.GONE);
                add_to_cart.setVisibility(View.VISIBLE);
            }

            wishListIcon.setOnClickListener(v -> {
                if (!SPData.getAppPreferences().getUsertoken().equals(""))
                    if (isInWishlist) {
                        isInWishlist = false;
                        removeItemFromWishList(varient_id);
                        wishListIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_heartsease));
                    } else {
                        isInWishlist = true;
                        addItemToWishList(varient_id);
                        wishListIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_heart_fill));
                    }
                else {
                    startActivity(new Intent(ProductDetailsActivity.this, LoginActivity.class));
                }
            });

            increase.setOnClickListener(v -> checkItemToAdded());

            decrease.setOnClickListener(v -> {
                int minQty = Integer.parseInt(product_minQty.getText().toString().split("qty-")[1]);
                String id = SPData.getAppPreferences().getVarientId();
                if (minQty != -1) {
                    if (Integer.parseInt(no_ofQuantity.getText().toString()) > minQty) {
                        int quantity = (Integer.parseInt(no_ofQuantity.getText().toString())) - minQty;
                        updateItem(id, quantity);
                    } else {
                        removeProductFromList(id);
                    }
                } else if (Integer.parseInt(no_ofQuantity.getText().toString()) > 1) {
                    int quantity = (Integer.parseInt(no_ofQuantity.getText().toString())) - 1;
                    updateItem(id, quantity);
                } else {
                    add_to_cart.setVisibility(View.VISIBLE);
                    linearLayoutNoCartitems.setVisibility(View.GONE);
                    removeProductFromList(id);
                }
            });

            if (getIntent() != null) {
                varient_id = getIntent().getStringExtra("varient_id");
                video = getIntent().getStringExtra("video");
                productName = getIntent().getStringExtra("product_name");
                category_id = getIntent().getStringExtra("category_id");
                brand = getIntent().getStringExtra("brand_name");
                vendor = getIntent().getStringExtra("vendor_name");
                isRecommended = getIntent().getBooleanExtra("recommended", false);
                images = getIntent().getStringArrayListExtra("images");
                ftproductPrice = getIntent().getFloatExtra("product_price", 0);
                sellingPrice = getIntent().getFloatExtra("product_selling_price", 0);
                productMinQty = getIntent().getStringExtra("product_minQty");

                description_ = getIntent().getStringExtra("description");
                isInWishlist = getIntent().getBooleanExtra("isInWishlist", false);
                codAvailable = getIntent().getBooleanExtra("codAvailable", true);

                stock = getIntent().getIntExtra("product_stock", 0);

                boolean returnable = getIntent().getBooleanExtra("returnable",false);
                if(returnable){
                    textReturnable.setText("Returnable");
                    textReturnable.setTextColor(getResources().getColor(R.color.colorGreen));
                }else{
                    textReturnable.setText("Non Returnable");
                    textReturnable.setTextColor(getResources().getColor(R.color.colorError));
                }
                TextView tvStock = findViewById(R.id.product_stock);
                tvStock.setText(stock+"");
                SPData.getAppPreferences().setVarientStock(stock);
                reviews_count = getIntent().getIntExtra("reviews_count", 0);
                average_rating = getIntent().getDoubleExtra("rating", 0);
                Bundle b = getIntent().getExtras();
                if (b != null) {
                    allattributes = b.getParcelableArrayList("categories");
                    differentVarientsArrayList = (ArrayList<DifferentVarients>) b.getSerializable("differentVarientsArrayList");
                    assert allattributes != null;
                } else
                    Log.e("TAG", "onCreate: " + " b is null");
            }


            add_to_cart.setOnClickListener(v -> {

                if (!SPData.getAppPreferences().getUsertoken().equals("")) {
                    String available = SPData.getAppPreferences().getVarientAvailable();
                    int total_stock = SPData.getAppPreferences().getVarientStock();
                    if (available.equals("true") && total_stock > 0) {
                        String id = SPData.getAppPreferences().getVarientId();
                        int minQty = SPData.getAppPreferences().getVarientMinQty();
                        if (minQty != -1 && total_stock >= minQty) {
                            addItem(id, minQty);
                        } else if (minQty == -1)
                            addItem(id, 1);
                        else
                            Toast.makeText(ProductDetailsActivity.this, "Current stock is " + total_stock, Toast.LENGTH_SHORT).show();
                    } else
                        MDToast.makeText(ProductDetailsActivity.this, "Product varient not available", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
                } else {
                    startActivity(new Intent(ProductDetailsActivity.this, LoginActivity.class));
                }
            });

            btBuyNow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!SPData.getAppPreferences().getUsertoken().equals("")) {
                        promptDialog();
                    } else {
                        startActivity(new Intent(ProductDetailsActivity.this, LoginActivity.class));
                    }
                }
            });

            add_review.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!SPData.getAppPreferences().getUsertoken().equals("")) {
                        final Dialog dialog = new Dialog(ProductDetailsActivity.this);
                        dialog.setContentView(R.layout.custom_rating_dialog);
                        dialog.setCancelable(true);
                        dialog.setCanceledOnTouchOutside(true);
                        dialog.show();
                        final EditText et_comment = dialog.findViewById(R.id.et_comment);
                        final RatingBar ratingBar = dialog.findViewById(R.id.rating_bar);
                        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                            @Override
                            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                                ratingvalue = rating;
                            }
                        });
                        Button btn_save = dialog.findViewById(R.id.save);
                        TextView btn_cancel = dialog.findViewById(R.id.cancel);

                        btn_save.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (TextUtils.isEmpty(et_comment.getText().toString())) {
                                    et_comment.setError("Please enter comment");
                                    Toast.makeText(ProductDetailsActivity.this, "Please enter comment", Toast.LENGTH_SHORT).show();
                                    et_comment.setFocusable(true);
                                } else if (ratingvalue == 0f) {
                                    Toast.makeText(ProductDetailsActivity.this, "Please select rating.", Toast.LENGTH_SHORT).show();
                                } else if (!SPData.getAppPreferences().getUserName().equals("")) {
                                    postReview(varient_id, dialog, et_comment.getText().toString(), ratingvalue);
                                } else {
                                    MDToast.makeText(ProductDetailsActivity.this, "Please provide your name", MDToast.LENGTH_SHORT, MDToast.TYPE_INFO).show();
                                    startActivity(new Intent(ProductDetailsActivity.this, ProfileActivity.class));
                                }
                            }
                        });

                        btn_cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                    } else {
                        startActivity(new Intent(ProductDetailsActivity.this, LoginActivity.class));
                    }
                }
            });
            handleIntent();
            setData();
            setBrandData();
            setRecommendedText();
            setPriceData();
            setImagesData();
            getReview();
            setReviewsAndRating();
            setUpWishlistIcon();
            setUpRecyclerViews(recycler_options);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("TAG", "onCreate: " + e.getMessage());
        }

    }

    private void getVarientDetails() {
        getRealtedProducts();
        SPData.getAppPreferences().setVarientId(varient_id);
        SPData.getAppPreferences().setVarientStock(stock);
        CustomProgressDialog customProgressDialog = CommonUtils.showProgressDialog(ProductDetailsActivity.this,
                "Getting product...");
        ApiService apiService = ApiUtils.getHeaderAPIService(SPData.getAppPreferences().getUsertoken());
        apiService.getVarientById(varient_id).enqueue(new Callback<VarientByIdResponse>() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onResponse(Call<VarientByIdResponse> call, Response<VarientByIdResponse> response) {
                CommonUtils.hideProgressDialog(customProgressDialog);
                if(response.isSuccessful()){
                    com.mindfulai.Models.varientsByCategory.Datum data = response.body().getData();
                    video = data.getProduct().getVideo();
                    productName = data.getProduct().getName();
                    category_id = data.getProduct().getCategory().getId();
                    SPData.getAppPreferences().setVarientId(varient_id);
                    brand = data.getProduct().getBrand().getName();
                    isRecommended = data.getProduct().getIsRecommended();
                    images.add(data.getProduct().getImages().getPrimary());
                    images.add(data.getProduct().getImages().getSecondary());

                    description_ = data.getProduct().getDetails();
                    setData();
                    setBrandData();
                    setRecommendedText();
                    setPriceData();
                    setImagesData();
                } else {
                    MDToast.makeText(ProductDetailsActivity.this, "Failed to get product!", MDToast.TYPE_ERROR).show();
                }
            }

            @Override
            public void onFailure(Call<VarientByIdResponse> call, Throwable t) {
                CommonUtils.hideProgressDialog(customProgressDialog);
                t.printStackTrace();
                MDToast.makeText(ProductDetailsActivity.this, "Failed to get product!", MDToast.TYPE_ERROR).show();
            }
        });

    }

    RecyclerView recyclerViewAddress;
    public void promptDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_pickk_address, null);
        recyclerViewAddress = view.findViewById(R.id.recycler_view_address);
        TextView timeSlottext = view.findViewById(R.id.select_time_text);
        TextView addAddress = view.findViewById(R.id.add_address);
        timeSlot = view.findViewById(R.id.time_slot);
        ImageView close = view.findViewById(R.id.close);

        if (SPData.showTimeSlotPicker()) {
            timeSlottext.setText("Select time slot and address");
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
                startActivityForResult(new Intent(ProductDetailsActivity.this, AddAddressActivity.class).putExtra("title", "Add Address"), 1);
            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ProductDetailsActivity.this);
        recyclerViewAddress.setLayoutManager(linearLayoutManager);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ProductDetailsActivity.this);
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
        DatePickerDialog datePickerDialog = new DatePickerDialog(ProductDetailsActivity.this, (view, year, month, dayOfMonth) -> {
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
        AlertDialog.Builder builder = new AlertDialog.Builder(ProductDetailsActivity.this);
        View dialogView = getLayoutInflater().inflate(R.layout.time_picker_custom_layout, null);
        builder.setView(dialogView);
        AlertDialog alertDialog = builder.create();
        Date currentDate = Calendar.getInstance().getTime();
        ArrayList<String> stringArrayList = new ArrayList<>();
        stringArrayList.add("9:00 am - 1:00 pm");
        stringArrayList.add("1:00 pm - 5:00 pm");
        stringArrayList.add("5:00 pm - 9:00 pm");
        String[] timeSlots = {"9:00 am","1:00 pm","5:00 pm","9:00 pm"};
        if(currentDate.equals(date)){
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
                Log.e("TAG", "pickTime: ");
            }catch (Exception e){
                Log.e("TAG", "pickTime: "+e);
            }
        }

        RecyclerView recyclerView = dialogView.findViewById(R.id.time_picker_recycler_layout);
        recyclerView.setLayoutManager(new LinearLayoutManager(ProductDetailsActivity.this));
        SimpleDateFormat sdf = new SimpleDateFormat(("dd-MM-YYYY EEEE"));
        String dateString = sdf.format(date);
        Log.e("TAG", "pickTime: datestring "+dateString);
        TimePickerSlotsAdapter slotsAdapter = new TimePickerSlotsAdapter(ProductDetailsActivity.this, stringArrayList, timeSlot, dateString, alertDialog);
        recyclerView.setAdapter(slotsAdapter);
        alertDialog.show();
    }
    
//    public void promptDialog() {
//        View view = getLayoutInflater().inflate(R.layout.dialog_pickk_address, null);
//        recyclerViewAddress = view.findViewById(R.id.recycler_view_address);
//        TextView addAddress = view.findViewById(R.id.add_address);
//        TextView timeSlottext = view.findViewById(R.id.select_time_text);
//        timeSlot = view.findViewById(R.id.time_slot);
//        ImageView close = view.findViewById(R.id.close);
//        Date date = new Date(new Date().getTime() + 1000*60*60*6);
//        String dayOfWeek = new SimpleDateFormat("EEEE").format(date);
//        String month = new SimpleDateFormat("MMMM").format(date);
//        int day = Integer.parseInt(new SimpleDateFormat("dd").format(date));
//        int hour = Integer.parseInt(new SimpleDateFormat("hh").format(date));
//        String ampm = new SimpleDateFormat("a").format(date);
//        if (ampm.equals("pm")){
//            hour+=12;
//        }
//        if (hour>21){
//            day+=1;
//        }
//        if (hour<9 || hour >21){
//            hour = 9;
//        }
//        if (SPData.showTimeSlotPicker()) {
//            timeSlottext.setText("Select time slot and address");
//            timeSlot.setText(dayOfWeek + " " + month + " " + day + ", " + hour + ":00 - " + (hour + 4) + ":00 " + " (Change)");
//            timeSlot.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    pickDate(timeSlot);
//                }
//            });
//        } else {
//            timeSlottext.setText("Select address");
//            timeSlot.setVisibility(View.GONE);
//        }
//        addAddress.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivityForResult(new Intent(ProductDetailsActivity.this, AddAddressActivity.class).putExtra("title", "Add Address"), 1);
//            }
//        });
//
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ProductDetailsActivity.this);
//        recyclerViewAddress.setLayoutManager(linearLayoutManager);
//        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ProductDetailsActivity.this);
//        alertDialog.setView(view);
//        alertDialog1 = alertDialog.create();
//        alertDialog1.setCanceledOnTouchOutside(false);
//        alertDialog1.show();
//        close.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                alertDialog1.dismiss();
//            }
//        });
//        getAllAddress(recyclerViewAddress);
//    }

//    private void pickDate(TextView timeSlot) {
//        String ret="";
//        DatePickerDialog datePickerDialog = new DatePickerDialog(ProductDetailsActivity.this, new DatePickerDialog.OnDateSetListener() {
//            @Override
//            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
//                Calendar calendar = Calendar.getInstance();
//                calendar.set(year, month, dayOfMonth);
//                SimpleDateFormat sdf = new SimpleDateFormat("EEEE MMMM dd");
//                int day = Integer.parseInt(new SimpleDateFormat("dd").format(calendar.getTime()));
//                int today = Integer.parseInt(new SimpleDateFormat("dd").format(new Date()));
//                if (day<=today){
//                    day = today+1;
//                }
//                try {
//                    pickTime(
//                            timeSlot,
//                            new SimpleDateFormat("EEEE").format(calendar.getTime()),
//                            new SimpleDateFormat("MMMM").format(calendar.getTime()),
//                            day);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
//        datePickerDialog.show();
//    }

//    private void pickTime(TextView timeSlot, String eeee, String mmmm, int dd) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(ProductDetailsActivity.this);
//        View dialogView = getLayoutInflater().inflate(R.layout.time_picker_custom_layout, null);
//        builder.setView(dialogView);
//        AlertDialog alertDialog = builder.create();
//
//        String[] timeSlots = {
//                "9:00 am - 1:00 pm", "1:00 pm - 5:00 pm", "5:00 pm - 9:00 pm"};
//        RecyclerView recyclerView = dialogView.findViewById(R.id.time_picker_recycler_layout);
//        recyclerView.setLayoutManager(new LinearLayoutManager(ProductDetailsActivity.this));
//        String dateString = eeee + " " + mmmm + " " + dd + ", ";
//        TimePickerSlotsAdapter slotsAdapter = new TimePickerSlotsAdapter(ProductDetailsActivity.this, timeSlots, timeSlot, dateString, alertDialog);
//        recyclerView.setAdapter(slotsAdapter);
//        alertDialog.show();
//    }

    private void getAllAddress(final RecyclerView recyclerViewAddress) {
        try {
            final CustomProgressDialog customProgressDialog = CommonUtils.showProgressDialog(ProductDetailsActivity.this,
                    "Getting addresses ... ");
            ApiService apiService = ApiUtils.getHeaderAPIService(SPData.getAppPreferences().getUsertoken());
            apiService.getUserBaseAddress().enqueue(new Callback<UserBaseAddress>() {
                @Override
                public void onResponse(@NonNull Call<UserBaseAddress> call, @NonNull Response<UserBaseAddress> response) {
                    if (response.isSuccessful()) {
                        try {
                            CommonUtils.hideProgressDialog(customProgressDialog);
                            assert response.body() != null;
                            List<UserDataAddress> userDataAddressArrayList = response.body().getData();
                            Log.e(TAG, "getAllAddresses: sellingPrice: " + sellingPrice);
                            if (sellingPrice == 0.0 ){
                                sellingPrice = ftproductPrice;
                            }
                            Log.e(TAG, "getAllAddresses: sellingPrice: " + sellingPrice);
                            UserAddressesAdapter addressesAdapter =
                                    new UserAddressesAdapter(
                                            ProductDetailsActivity.this,
                                            userDataAddressArrayList,
                                            null,
                                            alertDialog1,
                                            codAvailable,
                                            sellingPrice+"",
                                            "SERVICE",
                                            varient_id,
                                            timeSlot,
                                            null,false);
                            recyclerViewAddress.setAdapter(addressesAdapter);
                            addressesAdapter.notifyDataSetChanged();
                        }catch (Exception e){
                          //Toast.makeText(ProductDetailsActivity.this, ""+e, Toast.LENGTH_SHORT).show();
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
            Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "getAllAddress: " + e);
        }
    }

    @Override
    public void onPaymentSuccess(String s, PaymentData paymentData) {
        try {
            String sid = this.getSharedPreferences("order", Context.MODE_PRIVATE).getString("sid", "");
            String aid = this.getSharedPreferences("order", Context.MODE_PRIVATE).getString("aid", "");
            verifyPayment(aid, sid, paymentData);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, ""+e, Toast.LENGTH_SHORT).show();
            Log.e("TAG", "onPaymentSuccess: " + e);
        }
    }

    @Override
    public void onPaymentError(int i, String s, PaymentData paymentData) {

    }

    private void verifyPayment(String aid, String sid, PaymentData paymentData) {
        try {
            final CustomProgressDialog customProgressDialog = CommonUtils.showProgressDialog(ProductDetailsActivity.this,
                    "Verifying payment...");
            ApiService apiService = ApiUtils.getHeaderAPIService(SPData.getAppPreferences().getUsertoken());
            JsonObject jsonObject = new JsonObject();
            Log.i("verifyPayment", "paymentId::" + paymentData.getPaymentId());
            Log.i("verifyPayment", "orderId::" + paymentData.getOrderId());
            Log.i("verifyPayment", "rzorsignature::" + paymentData.getSignature());
            jsonObject.addProperty("paymentId", paymentData.getPaymentId());
            jsonObject.addProperty("orderId", paymentData.getOrderId());
            jsonObject.addProperty("signature", paymentData.getSignature());
            apiService.verifyOnlinePayment(jsonObject).enqueue(new Callback<OrderDetailInfo>() {
                @Override
                public void onResponse(Call<OrderDetailInfo> call, Response<OrderDetailInfo> response) {
                    CommonUtils.hideProgressDialog(customProgressDialog);
                    if (response.isSuccessful()) {
                        try {
                            SPData.getAppPreferences().setPaymentSuccess(true);
                            alertDialog1.dismiss();
                            SPData.getAppPreferences().setTotalCartCount(0);
                            OrderDetailInfo placeOrder = response.body();
                            String order_id = placeOrder.getData().getOrder().getOrderId();
                            String payment_method = placeOrder.getData().getOrder().getPaymentMethod();
                            String payment_amount = ""+placeOrder.getData().getOrder().getAmount();
                            Intent intent =new Intent(ProductDetailsActivity.this,OrderPlacedActivity.class);
                            intent.putExtra("order_id",""+order_id);
                            intent.putExtra("payment_method",""+payment_method);
                            intent.putExtra("payment_amount",""+payment_amount);
                            startActivity(intent);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(ProductDetailsActivity.this, ""+e, Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "onResponse: " + e);
                        }
                    } else {
                        Log.e(TAG, "onResponse: " + response);
                        Toast.makeText(ProductDetailsActivity.this, ""+response.message(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<OrderDetailInfo> call, Throwable t) {

                }
            });
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setRecommendedText() {

        LinearLayout linearLayout = findViewById(R.id.product_recommended);
        if(SPData.showGridView()){
        ImageView iv = findViewById(R.id.product_recommended_image);
        TextView tv = findViewById(R.id.product_recommended_text);
        TextViewCompat.setAutoSizeTextTypeWithDefaults(tv, TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM);
        if (isRecommended){
            linearLayout.setVisibility(View.VISIBLE);
            iv.setImageResource(R.drawable.check_orange);
            tv.setText(SPData.recommendedText());
            tv.setTextColor(getColor(R.color.orange));
        } else if(SPData.showCertifiedText()){
            linearLayout.setVisibility(View.VISIBLE);
            iv.setImageResource(R.drawable.check_blue);
            tv.setText(SPData.certifiedText());
            tv.setTextColor(getColor(R.color.colorInfo));
        } else {
            linearLayout.setVisibility(View.GONE);
        }
        }else{
            linearLayout.setVisibility(View.GONE);
        }
    }

    private void setUpRecyclerViews(RecyclerView recycler_options) {
        LinearLayoutManager verticalLayoutManager
                = new LinearLayoutManager(ProductDetailsActivity.this, LinearLayoutManager.VERTICAL, true);

        rv_reviews.setLayoutManager(verticalLayoutManager);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        ArrayList<String> productImages = new ArrayList<>();
        if (images.get(0).startsWith("products/")){
            productImages.add(images.get(0));
            if (images.get(1).startsWith("products/")){
                productImages.add(images.get(1));
            }
        }
        OptionViewAdapter adapter = new OptionViewAdapter(
                ProductDetailsActivity.this,
                allattributes,
                differentVarientsArrayList,
                null,
                (ProductDetailsActivity.this),
                productImages);
        recycler_options.setLayoutManager(layoutManager);
        recycler_options.setHasFixedSize(true);
        recycler_options.setAdapter(adapter);
    }

    private void setUpWishlistIcon() {
        if (isInWishlist)
            wishListIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_heart_fill));
        else
            wishListIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_heartsease));
    }

    private void setReviewsAndRating() {
        DecimalFormat df = new DecimalFormat("#.##");
        String formatted_rating = df.format(average_rating);
        product_reviews.setText(reviews_count + " Reviews");
        text_ratings.setText(formatted_rating + " *");
    }

    private void setImagesData() {

        if (video != null) {
            images.add(video);
        }
        Log.e("SliderItem1", ""+images.size());
        sliderView.setSliderAdapter(new SliderAdapterExample(this, images));
        sliderView.setIndicatorAnimation(IndicatorAnimations.WORM);
        sliderView.setAutoCycle(false);
        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
    }

    private void setPriceData() {
        if (productMinQty != null && !productMinQty.equals("null")) {
            product_minQty.setVisibility(View.VISIBLE);
            product_minQty.setText("Min qty-" + productMinQty);
        } else
            product_minQty.setVisibility(View.GONE);

        product_detail_mrp_price.setText("\u20B9" + ftproductPrice);
        if (sellingPrice != 0.0) {
            product_detail_selling_price.setText("\u20B9" + sellingPrice);
            int discount = (int) (((ftproductPrice - sellingPrice)/ftproductPrice)*100);
            product_detail_discount.setText(discount + " % off");
        } else {
            product_detail_selling_price.setText("\u20B9" + ftproductPrice);
            product_detail_mrp_price.setVisibility(View.GONE);
            product_detail_discount.setVisibility(View.GONE);
        }
        if (sellingPrice != ftproductPrice) {
            float selling = sellingPrice;
            float mrp = ftproductPrice;
            int discount = (int) (((mrp - selling)/mrp)*100);
            product_detail_mrp_price.setText("\u20B9" + ftproductPrice);
            product_detail_discount.setText(discount + "% off");
            product_detail_selling_price.setText("\u20B9" + sellingPrice);
        } else {
            product_detail_discount.setVisibility(View.GONE);
            product_detail_selling_price.setText("\u20B9" + sellingPrice);
            product_detail_mrp_price.setVisibility(View.GONE);
        }
    }

    private void setBrandData() {
        if (SPData.showBrand()) {
            if (brand != null && !brand.isEmpty())
                brand_name.setText(brand);
            else
                brand_name.setVisibility(View.GONE);
        } else {
            if (vendor!= null &&!vendor.isEmpty()) {
                brand_name.setText(vendor);
                getVendorProfile();
                brand_name.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!SPData.showBrand())
                            showVendorProfile();
                    }
                });

            } else
                brand_name.setVisibility(View.GONE);
        }
    }

    private void handleIntent() {
        Intent appLinkIntent = getIntent();
        Uri appLinkData = appLinkIntent.getData();
        if(appLinkData == null){
            return;
        }
        String token = appLinkData.getLastPathSegment();
        Claims claims = Jwts.parser().setSigningKey(JWT_SECRET.getBytes()).parseClaimsJws(token).getBody();
        varient_id = Objects.requireNonNull(claims.get("varientId")).toString();
        stock = (int) Objects.requireNonNull(claims.get("stock"));
        getVarientDetails();
    }

    private void getRealtedProducts() {


        try {
            if (varientList != null)
                varientList.clear();
            rvRelatedProducts.getRecycledViewPool().clear();
            if (productAdapter != null)
               productAdapter.notifyDataSetChanged();
            shimmerRelatedProducts.setVisibility(View.VISIBLE);
            shimmerRelatedProducts.startShimmerAnimation();
            ApiService apiService;
            apiService = ApiUtils.getAPIService();
            String type = "product";
            if(SPData.showServicesTab() && SPData.getProductsOrServices().equals("SERVICES")){
                type = "service";
            }

            apiService.getAllProductsVarients(category_id, type).enqueue(new Callback<VarientsByCategory>() {
                @Override
                public void onResponse(@NonNull Call<VarientsByCategory> call, @NonNull Response<VarientsByCategory> response) {
                    showRelatedProductsFromResponse(response);
                    Log.i(TAG, "getRelatedProducts: Success");
                }

                @Override
                public void onFailure(@NonNull Call<VarientsByCategory> call, @NonNull Throwable t) {
                    Log.e(TAG, "onFailure: " + t.getMessage());

                }
            });

        } catch (Exception e) {
            Log.e(TAG, "getProductsVarients: " + e);
        }
    }

    private void showRelatedProductsFromResponse(Response<VarientsByCategory> response) {
        try {
            if (response.isSuccessful()) {
                VarientsByCategory productVarients = response.body();
                assert productVarients != null;
                try {
                    for (int i = 0; i < productVarients.getData().size(); i++)
                        varientList.add(i, productVarients.getData().get(i));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (varientList == null || varientList.size() == 0) {
                    shimmerRelatedProducts.stopShimmerAnimation();
                    shimmerRelatedProducts.setVisibility(View.GONE);
                    rvRelatedProducts.setVisibility(View.GONE);

                } else {
                    getAllOptionsVarient(true);
                }
            } else {
                shimmerRelatedProducts.stopShimmerAnimation();
                shimmerRelatedProducts.setVisibility(View.GONE);
                rvRelatedProducts.setVisibility(View.GONE);

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

                differentVarientsArrayList.add(k, new DifferentVarients("" + varientList.get(position).getVarients().get(k).getPrice(), varient_option_value, varientList.get(position).getVarients().get(k).getId(), varientList.get(position).getVarients().get(k).getDescription(), "" + varientList.get(position).getVarients().get(k).getStock(), sellinPrice, minQty,listOfImages));
            }
            alldifferentVarientList.add(position, differentVarientsArrayList);
        }
        shimmerRelatedProducts.stopShimmerAnimation();
        shimmerRelatedProducts.setVisibility(View.GONE);
        rvRelatedProducts.setVisibility(View.VISIBLE);
        if (b) {
            productAdapter = new ProductsAdapter(ProductDetailsActivity.this, varientList, null, "grid", alloptionsAttributeArrayList, alldifferentVarientList);
            rvRelatedProducts.setAdapter(productAdapter);
        }
        productAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (SPData.getAppPreferences().getPaymentSuccess()) {
            add_to_cart.setVisibility(View.VISIBLE);
            linearLayoutNoCartitems.setVisibility(View.GONE);
            no_ofQuantity.setText("0");
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.item_notification);
        cartBadge = item.getActionView().findViewById(R.id.notification_badge);
        if (!SPData.showProductsAndCart()){
            cartBadge.setVisibility(View.GONE);
        }
        int total = SPData.getAppPreferences().getTotalCartCount();
        cartBadge.setBadgeValue(total);
        cartBadge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(ProductDetailsActivity.this, CommonActivity.class).putExtra("show", "cart"), 10);
            }
        });

        MenuItem list = menu.findItem(R.id.navigation_list_view);
        MenuItem grid = menu.findItem(R.id.navigation_grid_view);
        MenuItem search = menu.findItem(R.id.search);
        list.setVisible(false);
        grid.setVisible(false);
        search.setVisible(false);
        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.browse_menu, menu);
        return true;
    }

    public void addBadge(String count) {
        cartBadge.setBadgeValue(Integer.parseInt(count));
    }

    public void removeBadge() {
        cartBadge.setBadgeValue(0);
    }

    private void removeProductFromList(String id) {
        ApiService apiService = ApiUtils.getHeaderAPIService(SPData.getAppPreferences().getUsertoken());
        apiService.removeItemFromCart(id).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {
                    if (response.isSuccessful()) {
                        assert response.body() != null;
                        String reponse_status = String.valueOf(response.body().get("status"));
                        if (reponse_status.matches("200")) {
                            add_to_cart.setVisibility(View.VISIBLE);
                            linearLayoutNoCartitems.setVisibility(View.GONE);
                            if (SPData.getAppPreferences().getTotalCartCount() > 0) {
                                int total_cart = SPData.getAppPreferences().getTotalCartCount() - 1;
                                if (total_cart > 0) {
                                    SPData.getAppPreferences().setTotalCartCount(total_cart);
                                    addBadge("" + total_cart);
                                } else {
                                    SPData.getAppPreferences().setTotalCartCount(0);
                                    removeBadge();
                                }
                            }
                            Toast.makeText(ProductDetailsActivity.this, "Removed from cart !!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(ProductDetailsActivity.this, response.code() + " " + response.message(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.e(TAG, "onResponse: " + e);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(ProductDetailsActivity.this, "Failed to connect", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void checkItemToAdded() {
        int total_stock = SPData.getAppPreferences().getVarientStock();
        String available = SPData.getAppPreferences().getVarientAvailable();
        int minQty = Integer.parseInt(product_minQty.getText().toString().split("qty-")[1]);
        int currentQty = Integer.parseInt(no_ofQuantity.getText().toString());
        if (minQty != -1)
            currentQty = currentQty + minQty;
        if (available.equals("true") && currentQty <= total_stock) {
            if (!SPData.getAppPreferences().getUsertoken().equals("")) {
                String id = SPData.getAppPreferences().getVarientId();
                int quantity;
                if (minQty != -1)
                    quantity = (Integer.parseInt(no_ofQuantity.getText().toString())) + minQty;
                else
                    quantity = (Integer.parseInt(no_ofQuantity.getText().toString())) + 1;
                updateItem(id, quantity);
            } else
                startActivity(new Intent(ProductDetailsActivity.this, LoginActivity.class));
        } else if (available.equals("false")) {
            MDToast.makeText(ProductDetailsActivity.this, "Item not available", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
        } else {
            Toast.makeText(ProductDetailsActivity.this, "Current stock is less than quantity " + "( current stock is " + total_stock + " )", Toast.LENGTH_SHORT).show();
        }
    }


    private void updateItem(String id, final int quantity) {
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
                            no_ofQuantity.setText("" + quantity);
                            Toast.makeText(ProductDetailsActivity.this, "Quantity Updated!!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(ProductDetailsActivity.this, response.code() + " " + response.message(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    Log.e("fail", call.toString());
                    Toast.makeText(ProductDetailsActivity.this, "Failed to connect", Toast.LENGTH_SHORT).show();

                }
            });
        } catch (Exception e) {
            Log.e(TAG, "updateItem: " + e);
        }
    }

    private void addItemToWishList(String id) {
        try {
            final CustomProgressDialog customProgressDialog = CommonUtils.showProgressDialog(ProductDetailsActivity.this,
                    "Adding to wishlist ... ");
            ApiService apiService = ApiUtils.getHeaderAPIService(SPData.getAppPreferences().getUsertoken());
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("product", id);

            apiService.addItemToWishlist(jsonObject).enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                    CommonUtils.hideProgressDialog(customProgressDialog);
                    if (response.isSuccessful()) {
                        String reponse_status = String.valueOf(response.body().get("status"));
                        Log.e(TAG, "onResponse: " + response);
                        if (reponse_status.matches("200")) {
                            Toast.makeText(ProductDetailsActivity.this, "Item added to wishlist !!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(ProductDetailsActivity.this, response.code() + " " + response.message(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                    Log.e("fail", call.toString());
                    CommonUtils.hideProgressDialog(customProgressDialog);
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "addItemToWishList: " + e);
        }
    }

    private void removeItemFromWishList(String id) {
        try {
            final CustomProgressDialog customProgressDialog = CommonUtils.showProgressDialog(ProductDetailsActivity.this,
                    "Removing from wishlist ... ");
            ApiService apiService = ApiUtils.getHeaderAPIService(SPData.getAppPreferences().getUsertoken());

            apiService.removeItemFromWishlist(id).enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                    CommonUtils.hideProgressDialog(customProgressDialog);
                    if (response.isSuccessful()) {
                        String reponse_status = String.valueOf(response.body().get("status"));
                        if (reponse_status.matches("200")) {
                            intent.putExtra("fav", "false");
                            setResult(RESULT_CANCELED, intent);
                            Toast.makeText(ProductDetailsActivity.this, "Item remove from wishlist!!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        CommonUtils.hideProgressDialog(customProgressDialog);
                        Toast.makeText(ProductDetailsActivity.this, response.code() + " " + response.message(), Toast.LENGTH_SHORT).show();
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


    private void getVendorProfile() {
        ApiService apiService = ApiUtils.getHeaderAPIService();
        apiService.getVendorProfile(varient_id).enqueue(new Callback<VendorBase>() {
            @Override
            public void onResponse(@NonNull Call<VendorBase> call, @NonNull Response<VendorBase> response) {
                if (response.isSuccessful()) {
                    vendorData = response;
                } else {
                    Log.e("TAG", "onResponse: " + response);
                }
            }

            @Override
            public void onFailure(Call<VendorBase> call, Throwable t) {
                Log.e("TAG", "onFailure: " + "Failed to connect");
            }
        });
    }

    private void showVendorProfile() {

        RelativeLayout rl = findViewById(R.id.rl);
        SweetSheet mSweetSheet = new SweetSheet(rl);
        CustomDelegate customDelegate = new CustomDelegate(true,
                CustomDelegate.AnimationType.DuangLayoutAnimation);
        final View view = LayoutInflater.from(ProductDetailsActivity.this).inflate(R.layout.vendor_profile_view, null, false);
        getVendorProfile();
        TextView name = view.findViewById(R.id.vendor_name);
        TextView email = view.findViewById(R.id.vendor_email);
        TextView address = view.findViewById(R.id.vendor_address);
        TextView phone = view.findViewById(R.id.vendor_phone);
        if (vendorData != null) {
            name.setText(vendorData.body().getData().get(0).getFull_name());
            address.setText(vendorData.body().getData().get(0).getAddress());
            phone.setText(vendorData.body().getData().get(0).getMobile_number());
            email.setText(vendorData.body().getData().get(0).getEmail());
            customDelegate.setCustomView(view);
            mSweetSheet.setDelegate(customDelegate);
            mSweetSheet.toggle();
        }

    }

    @SuppressLint("SetTextI18n")
    private void setData() {
        Log.i(TAG, "setData: " + productName + " " + description_);
        product_name.setText(capitalizeWord(productName));
        description.setText(description_);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            setResult(RESULT_CANCELED, intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void postReview(final String varient_id, final Dialog dialog, String comment, final float ratingvalue) {

        final CustomProgressDialog customProgressDialog = CommonUtils.showProgressDialog(ProductDetailsActivity.this,
                "Posting ... ");
        if (SPData.getAppPreferences().getUsertoken() != null && !SPData.getAppPreferences().getUsertoken().isEmpty()) {
            ApiService apiService = ApiUtils.getHeaderAPIService(SPData.getAppPreferences().getUsertoken());
            JsonObject jsonObject = new JsonObject();
            Log.e("TAG", "postReview: "+varient_id );
            jsonObject.addProperty("varient", varient_id);
            jsonObject.addProperty("comment", comment);
            jsonObject.addProperty("rating", ratingvalue);
            apiService.addReview(jsonObject).enqueue(new Callback<JsonObject>() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                    CommonUtils.hideProgressDialog(customProgressDialog);
                    Log.e("TAG", "onResponse: "+response );
                    if (response.isSuccessful()) {
                        intent.putExtra("update", "true");
                        setResult(RESULT_CANCELED, intent);
                        Toast.makeText(ProductDetailsActivity.this, "Review added successfully.", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        getReview();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                    Log.e("TAG", "" + t.toString());
                    Toast.makeText(ProductDetailsActivity.this, "Failed to connect", Toast.LENGTH_SHORT).show();
                    CommonUtils.hideProgressDialog(customProgressDialog);
                }
            });
        } else {
            startActivity(new Intent(ProductDetailsActivity.this, LoginActivity.class));
        }

    }


    private void getReview() {

        ApiService apiService = ApiUtils.getHeaderAPIService();
        Log.e("TAG", "getReview: "+varient_id);
        apiService.getReview(varient_id).enqueue(new Callback<ReviewData>() {
            @Override
            public void onResponse(@NonNull Call<ReviewData> call, @NonNull Response<ReviewData> response) {
                Log.e("TAG", "onResponse: "+response );
                if (response.isSuccessful()) {
                    ReviewData reviewData = response.body();
                    assert reviewData != null;
                    reviewList = reviewData.getData();
                    Log.e("TAG", "onResponse: "+reviewList.size() );
                    if (reviewList.size() > 0) {
                        reviewsAdapter = new ReviewsAdapter(ProductDetailsActivity.this, reviewList);
                        rv_reviews.setAdapter(reviewsAdapter);
                        reviewsAdapter.notifyDataSetChanged();
                    }
                }

            }

            @Override
            public void onFailure(Call<ReviewData> call, Throwable t) {
                Log.e("fail", call.toString());
                Toast.makeText(ProductDetailsActivity.this, "Failed to connect", Toast.LENGTH_SHORT).show();

            }
        });


    }


    private void addItem(String id, final int qty) {

        ApiService apiService = ApiUtils.getHeaderAPIService(SPData.getAppPreferences().getUsertoken());
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("product", id);
        jsonObject.addProperty("quantity", qty);

        apiService.addItemToCart(jsonObject).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {

                if (response.isSuccessful()) {
                    String reponse_status = String.valueOf(response.body().get("status"));
                    if (reponse_status.matches("200")) {
                        if (SPData.getAppPreferences().getTotalCartCount() != -1) {
                            int cartItem = SPData.getAppPreferences().getTotalCartCount() + 1;
                            SPData.getAppPreferences().setTotalCartCount(cartItem);
                            addBadge("" + cartItem);
                        }
                        add_to_cart.setVisibility(View.GONE);
                        linearLayoutNoCartitems.setVisibility(View.VISIBLE);
                        no_ofQuantity.setText("" + qty);
                        Toast.makeText(ProductDetailsActivity.this, "Item added to cart !!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ProductDetailsActivity.this, "" + response.body(), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                Log.e("fail", call.toString());
            }
        });
    }
}