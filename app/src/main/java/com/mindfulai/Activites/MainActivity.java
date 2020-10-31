package com.mindfulai.Activites;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.mindfulai.Adapter.ExpandableListAdapter;
import com.mindfulai.Models.UserBaseAddress;
import com.mindfulai.Models.UserDataAddress;
import com.mindfulai.Models.categoryData.CategoryInfo;
import com.mindfulai.Models.categoryData.Datum;
import com.mindfulai.Models.categoryData.Subcategory;
import com.mindfulai.NetworkRetrofit.ApiService;
import com.mindfulai.NetworkRetrofit.ApiUtils;
import com.mindfulai.Utils.CommonUtils;
import com.mindfulai.Utils.GlobalEnum;
import com.mindfulai.Utils.SPData;
import com.mindfulai.dao.AppDatabase;
import com.mindfulai.ministore.R;
import com.mindfulai.ui.AllCategoriesFragment;
import com.mindfulai.ui.HomeFragment;
import com.mindfulai.ui.LocationBottomSheet;
import com.mindfulai.ui.MoreFragment;
import com.mindfulai.ui.NotificationFragment;
import com.mindfulai.ui.OrderHistoryFragment;
import com.mindfulai.ui.ProfileFragment;
import com.mindfulai.ui.ShareAppBottomSheet;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.nikartm.support.ImageBadgeView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "Main";
    private TextView textViewLocation;
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private DrawerLayout drawerLayout;
    private Fragment homeFrgmant;
    private AllCategoriesFragment allCategoriesFragment;
    private OrderHistoryFragment orderHistoryFragment;
    private NotificationFragment notificationFragment;
    private MoreFragment moreFragment;
    ImageBadgeView cartBadge;
    NavigationView navigationView;
    private static final int PLACE_PICKER_CODE = 100;
    List<Place.Field> userLocationFields;
    private List<Datum> categoryList;
    private int SELECT_ADDRESS_CODE = 435;
    private ExpandableListView categoriesListView;
    private String flashBannerId = "";
    private int[] tabIcons = {
            R.drawable.ic_home_black_24dp,
            R.drawable.ic_view_module_black_24dp,
            R.drawable.ic_history,
            R.drawable.ic_notifications_black_24dp,
            R.drawable.ic_baseline_more_horiz_24
    };
    private TabLayout tabLayout;
    private Toolbar toolbar;
    private ImageView imageViewLocation;
    private ImageView imageViewWishList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Places.initialize(getApplicationContext(), "AIzaSyBKZejrZNZpLlemrH28Nc46XzHsRSVRxKI");
        try {
            setContentView(R.layout.activity_drawer);
            SPData.getAppPreferences().setPaymentSuccess(false);
            setFindViewById();
            setSupportActionBar(toolbar);
            homeFrgmant = new HomeFragment();

            setToolBarAndDrawerLayout();
            categoryList = new ArrayList<>();
            categoriesListView.setVisibility(View.GONE);
            TextView selectedLocationText = findViewById(R.id.home_selected_location);
            Log.e("TAG", "onCreate: " + SPData.getAppPreferences().getUsertoken());
            userLocationFields = Arrays.asList(Place.Field.ID, Place.Field.NAME);
            selectedLocationText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    locationPicker();
                }
            });
            textViewLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    locationPicker();
                }
            });
            cartBadge = findViewById(R.id.notification_badge);
            if (!SPData.showProductsAndCart()) {
                cartBadge.setVisibility(View.GONE);
            }
            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    if (tab.getPosition() == 0) {
                        setFragment(homeFrgmant);
                    } else if (tab.getPosition() == 1) {
                        setFragment(allCategoriesFragment);
                    } else if (tab.getPosition() == 2) {
                        setFragment(orderHistoryFragment);
                    } else if(tab.getPosition() == 3){
                        setFragment(notificationFragment);
                    }else if(tab.getPosition() == 4){
                        setFragment(moreFragment);
                    }
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });
            setFragment(homeFrgmant);
            setCurrentAddress();
            cartBadge.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivityForResult(new Intent(MainActivity.this, CommonActivity.class).putExtra("show", "cart"), 10);
                }
            });
            imageViewWishList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(MainActivity.this, CommonActivity.class).putExtra("show", "wish"));
                }
            });
            new FlashBanner().execute(ApiService.FLASH_BANNER);
        } catch (Exception e) {
            Log.e("TAG", "onCreate: " + e);
            FirebaseCrashlytics.getInstance().recordException(new Throwable(e));
        }
    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.framelayout_container, fragment).commit();
    }

    private void showFlashDeal() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_flash_deal, null);
        ImageView close = view.findViewById(R.id.close);
        ImageView imgDiscount = view.findViewById(R.id.img_discount);
        AlertDialog dialog = builder.create();
        Glide.with(MainActivity.this).load(GlobalEnum.AMAZON_URL + flashBannerId).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                Log.e("TAG", "onLoadFailed: ");
                dialog.dismiss();
                return true;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                Log.e("TAG", "onResourceReady: ");
                close.setVisibility(View.VISIBLE);
                builder.setView(view);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                dialog.show();
                return false;
            }
        }).into(imgDiscount);
        dialog.setCanceledOnTouchOutside(true);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    private void locationPicker() {
        if (SPData.getAppPreferences().getUsertoken().equals("")) {
            startActivity(
                    new Intent(MainActivity.this, LoginActivity.class)
            );
        } else {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            boolean gps = false;
            boolean network = false;
            try {
                gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (!gps && !network) {
                LocationBottomSheet bottomSheet = new LocationBottomSheet();
                bottomSheet.show(
                        getSupportFragmentManager(),
                        "LocationBS"
                );
            } else {
                openSelectAddressActivity();
            }
        }
    }

    public void openSelectAddressActivity() {
        startActivityForResult(
                new Intent(MainActivity.this, AddressSelectorActivity.class),
                SELECT_ADDRESS_CODE
        );
    }

    private void getServicesCategories() {
        categoryList.clear();
        ApiService apiService = ApiUtils.getHeaderAPIService();
        apiService.getAllServiceCategory().enqueue(new Callback<CategoryInfo>() {
            @Override
            public void onResponse(@NonNull Call<CategoryInfo> call, @NonNull Response<CategoryInfo> response) {
                if (response.isSuccessful()) {
                    categoryList.clear();
                    CategoryInfo categoryInfo = response.body();
                    assert categoryInfo != null;
                    categoryList = categoryInfo.getData();
                    navigationView.getMenu().clear();
                    navigationView.inflateMenu(R.menu.main_drawer_categories_sub_menu);
                    Menu navMenu = navigationView.getMenu();
                    int counter = 123;
                    navMenu.add(0, counter++, Menu.NONE, "Back")
                            .setIcon(R.drawable.leku_ic_back)
                            .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem item) {
                                    navigationView.getMenu().clear();
                                    navigationView.inflateMenu(R.menu.activity_main_drawer);
                                    navigationView.getMenu().findItem(R.id.nav_product_categories).setVisible(SPData.showProductsAndCart());
                                    navigationView.getMenu().findItem(R.id.nav_services_categories).setVisible(SPData.showServicesTab());
                                    return true;
                                }
                            });
                    for (Datum data : categoryList) {
                        navMenu.add(0, counter++, Menu.NONE, CommonUtils.capitalizeWord(data.getName()))
                                .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                                    @Override
                                    public boolean onMenuItemClick(MenuItem item) {
                                        Intent i = new Intent(getApplicationContext(), AllProductsActivity.class);
                                        i.putExtra("level", 1);
                                        i.putExtra("category_id", data.getId());
                                        i.putExtra("categoryName", data.getName());
                                        startActivity(i);
                                        return true;
                                    }
                                });
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Can't get categories", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<CategoryInfo> call, @NonNull Throwable t) {
                Toast.makeText(getApplicationContext(), "Failed: Can't get categories", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getProductCategories() {
        categoryList.clear();
        ApiService apiService = ApiUtils.getHeaderAPIService();
        apiService.getAllProductCategory().enqueue(new Callback<CategoryInfo>() {
            @Override
            public void onResponse(@NonNull Call<CategoryInfo> call, @NonNull Response<CategoryInfo> response) {
                if (response.isSuccessful()) {
                    categoryList.clear();
                    CategoryInfo categoryInfo = response.body();
                    assert categoryInfo != null;
                    categoryList = categoryInfo.getData();
                    navigationView.getMenu().clear();
                    navigationView.inflateMenu(R.menu.main_drawer_categories_sub_menu);
                    Menu navMenu = navigationView.getMenu();
                    int counter = 789;
                    navMenu.add(0, counter++, Menu.NONE, "Back")
                            .setIcon(R.drawable.leku_ic_back)
                            .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem item) {
                                    item.getIcon().setVisible(false, false);
                                    navigationView.getMenu().clear();
                                    categoriesListView.setVisibility(View.GONE);
                                    navigationView.inflateMenu(R.menu.activity_main_drawer);
                                    navigationView.getMenu().findItem(R.id.nav_product_categories).setVisible(SPData.showProductsAndCart());
                                    navigationView.getMenu().findItem(R.id.nav_services_categories).setVisible(SPData.showServicesTab());
                                    return true;
                                }
                            });
                    HashMap<Datum, List<Subcategory>> children = new HashMap<>();
                    for (Datum parent : categoryList) {
                        children.put(parent, parent.getSubcategory());
                    }
                    categoriesListView.setVisibility(View.VISIBLE);
                    categoriesListView.setAdapter(
                            new ExpandableListAdapter(MainActivity.this, categoryList, children)
                    );
                } else {
                    Toast.makeText(getApplicationContext(), "Can't get categories", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<CategoryInfo> call, @NonNull Throwable t) {
                Toast.makeText(getApplicationContext(), "Failed: Can't get categories", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        try {
            super.onResume();
            if (!SPData.showBottomNavMenu()) {
                Menu menu = navigationView.getMenu();
                TextView name = navigationView.getHeaderView(0).findViewById(R.id.uname);
                TextView phone = navigationView.getHeaderView(0).findViewById(R.id.uphone);
                phone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (SPData.getAppPreferences().getUsertoken().isEmpty())
                            startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    }
                });
                if (!SPData.getAppPreferences().getUsertoken().equals("")) {
                    int total = SPData.getAppPreferences().getTotalCartCount();
                    if (total != -1) {
                        cartBadge.setBadgeValue(Math.max(total, 0));
                    }
                    phone.setText(SPData.getAppPreferences().getMobileNumber());
                    menu.findItem(R.id.nav_logout).setVisible(true);
                    menu.findItem(R.id.nav_wallet).setVisible(true);
                    menu.findItem(R.id.nav_home).setVisible(true);
                    menu.findItem(R.id.nav_order_history).setVisible(true);
                    menu.findItem(R.id.nav_wishlist).setVisible(true);
                    menu.findItem(R.id.nav_share).setVisible(true);
                } else {
                    phone.setText("Login");
                    menu.findItem(R.id.nav_logout).setVisible(false);
                    menu.findItem(R.id.nav_wallet).setVisible(false);
                    menu.findItem(R.id.nav_home).setVisible(false);
                    menu.findItem(R.id.nav_order_history).setVisible(false);
                    menu.findItem(R.id.nav_wishlist).setVisible(false);
                    menu.findItem(R.id.nav_share).setVisible(false);

                }
                CircleImageView profile = navigationView.getHeaderView(0).findViewById(R.id.userthumbimage);
                menu.findItem(R.id.nav_product_categories).setVisible(SPData.showProductsAndCart());
                menu.findItem(R.id.nav_services_categories).setVisible(SPData.showServicesTab());
                if (!SPData.getAppPreferences().getUserProfilePic().equals("")) {
                    Glide.with(MainActivity.this).load(GlobalEnum.AMAZON_URL + SPData.getAppPreferences().getUserProfilePic()).into(profile);
                } else
                    profile.setImageDrawable(getResources().getDrawable(R.drawable.user));

                if (SPData.getAppPreferences().getUserName().equals(""))
                    name.setText("User" + String.format("%04d", new Random().nextInt(10000)));
                else
                    name.setText(CommonUtils.capitalizeWord(SPData.getAppPreferences().getUserName()));
            }else{
                if (!SPData.getAppPreferences().getUsertoken().equals("")) {
                    int total = SPData.getAppPreferences().getTotalCartCount();
                    if (total != -1) {
                        cartBadge.setBadgeValue(Math.max(total, 0));
                    }
                }
            }
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(new Throwable(e));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == 0) {
                homeFrgmant.onActivityResult(requestCode, resultCode, data);
                setCurrentAddress();
            } else if (requestCode == PLACE_PICKER_CODE) {
                if (resultCode == RESULT_OK) {
                    String address = data.getStringExtra("address");
                    textViewLocation.setText(address);
                }

            } else if (requestCode == SELECT_ADDRESS_CODE) {
                String address = data.getStringExtra("address");
                textViewLocation.setText(address);

            }else if(requestCode == 3){
                orderHistoryFragment.onActivityResult(requestCode,resultCode,data);
            }

        } catch (Exception e) {
            e.printStackTrace();
            FirebaseCrashlytics.getInstance().recordException(new Throwable(e));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION) {
            if (permissions.length > 0 && permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION) &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setCurrentAddress();
            }
        }
    }

    private void setToolBarAndDrawerLayout() {
        if (!SPData.showBottomNavMenu()) {
            drawerLayout = findViewById(R.id.drawer_layout);
            navigationView = (NavigationView) findViewById(R.id.navation_view);
            navigationView.setNavigationItemSelectedListener(this);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.closed);
            drawerLayout.addDrawerListener(toggle);
            toggle.syncState();
            tabLayout.setVisibility(View.GONE);
            imageViewWishList.setVisibility(View.GONE);
            imageViewLocation.setVisibility(View.GONE);
        } else {
            allCategoriesFragment = new AllCategoriesFragment();
            orderHistoryFragment = new OrderHistoryFragment();
            notificationFragment = new NotificationFragment();
            moreFragment = new MoreFragment();
            tabLayout.addTab(tabLayout.newTab().setIcon(tabIcons[0]).setText("Home"));
            tabLayout.addTab(tabLayout.newTab().setIcon(tabIcons[1]).setText("Category"));
            tabLayout.addTab(tabLayout.newTab().setIcon(tabIcons[2]).setText("Orders"));
            tabLayout.addTab(tabLayout.newTab().setIcon(tabIcons[3]).setText("Notification"));
            tabLayout.addTab(tabLayout.newTab().setIcon(tabIcons[4]).setText("More"));
        }
    }

    private void setFindViewById() {
        textViewLocation = findViewById(R.id.home_user_location);
        toolbar = findViewById(R.id.app_bar);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        categoriesListView = findViewById(R.id.nav_categories_list);
        imageViewLocation = findViewById(R.id.img_location);
        imageViewWishList = findViewById(R.id.img_wishlist);
    }

    public void addBadge(String count) {
        cartBadge.setBadgeValue(Integer.parseInt(count));
    }

    public void removeBadge() {
        cartBadge.setBadgeValue(0);
    }

    @SuppressLint("StaticFieldLeak")
    class FlashBanner extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            OkHttpClient client = new OkHttpClient();
            try {
                Request request = new Request.Builder()
                        .url(strings[0])
                        .get()
                        .addHeader("Content-Type", "application/json;charset=utf-8")
                        .build();
                okhttp3.Response response = client.newCall(request).execute();
                String jsonData = response.body().string();
                JSONObject jsonObject = new JSONObject(jsonData);
                boolean errors = jsonObject.getBoolean("errors");
                if (!errors) {
                    JSONObject jsonObject1 = (JSONObject) jsonObject.getJSONArray("data").get(0);
                    flashBannerId = jsonObject1.getString("image");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return flashBannerId;
        }


        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            if (!flashBannerId.isEmpty() && getIntent().getBooleanExtra("show", false)) {

                showFlashDeal();
            } else {
                Log.e("TAG", "onPostExecute: ");
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private void setCurrentAddress() {
        new CommonUtils(MainActivity.this).getAddress(textViewLocation);
        ApiService apiService = ApiUtils.getHeaderAPIService(SPData.getAppPreferences().getUsertoken());
        apiService.getUserBaseAddress().enqueue(new Callback<UserBaseAddress>() {
            @Override
            public void onResponse(Call<UserBaseAddress> call, Response<UserBaseAddress> response) {
                if (response.isSuccessful()) {
                    ArrayList<UserDataAddress> dataList = response.body().getData();
                    if (dataList.isEmpty()) {
                        new CommonUtils(MainActivity.this).getAddress(textViewLocation);
                    } else {
                        textViewLocation.setText(dataList.get(0).getAddressLine1() + " " + dataList.get(0).getAddressLine2());
                    }
                }
            }

            @Override
            public void onFailure(Call<UserBaseAddress> call, Throwable t) {

            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();
        switch (id) {
            case R.id.nav_home:
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                break;
            case R.id.nav_product_categories:
                SPData.setProductsOrServices("PRODUCTS");
                getProductCategories();
                break;
            case R.id.nav_services_categories:
                SPData.setProductsOrServices("SERVICES");
                getServicesCategories();
                break;
            case R.id.nav_order_history:
                startActivity(new Intent(MainActivity.this, OrderHistoryActivity.class));
                break;
            case R.id.nav_wallet:
                startActivity(new Intent(MainActivity.this, WalletActivity.class));
                break;
            case R.id.nav_wishlist:
                startActivity(new Intent(MainActivity.this, CommonActivity.class).putExtra("show", "wish"));
                break;
            case R.id.nav_notification:
                startActivity(new Intent(MainActivity.this, CommonActivity.class).putExtra("show", "notification"));
                break;
            case R.id.nav_about:
                startActivity(new Intent(MainActivity.this, AboutActivity.class));
                break;
            case R.id.nav_share:
                generateShareLink();
                break;
            case R.id.nav_rate:
                final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
                break;
            case R.id.nav_contact:
                sendToGmail("Info/Help regarding " + getString(R.string.app_name), SPData.emailAddress());
                break;
            case R.id.nav_faq:
                startActivity(new Intent(MainActivity.this, FAQActivity.class));
                break;
            case R.id.report_problem:
                sendToGmail("Bug/Issue in Ecom", SPData.emailAddress());
                break;
            case R.id.nav_privacy:
                startActivity(new Intent(MainActivity.this, PrivacyPolicy.class).putExtra("type",ApiService.PRIVACY));
                break;
            case R.id.nav_return_privacy:
                startActivity(new Intent(MainActivity.this, PrivacyPolicy.class).putExtra("type", ApiService.RETURN_POLICY));
                break;
            case R.id.nav_logout:

                showPopup();
                break;
        }
        return true;
    }

    private void generateShareLink() {
        drawerLayout.closeDrawers();
        ShareAppBottomSheet bottomSheet = new ShareAppBottomSheet();
        bottomSheet.show(getSupportFragmentManager(), "ShareBS");
    }

    private void showPopup() {
        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
        alert.setMessage("Are you sure you wnat to Logout?")
                .setPositiveButton("Logout", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {

                        logout(); // Last step. Logout function

                    }
                }).setNegativeButton("Cancel", null);

        AlertDialog alert1 = alert.create();
        alert1.show();
    }

    private void logout() {
        SPData.getAppPreferences().clearAppPreference();
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                AppDatabase.Companion.getDatabase(getApplicationContext()).notificationDao().deleteAllNotifications();
            }
        });
        startActivity(new Intent(MainActivity.this, LoginActivity.class).putExtra("from", "logout"));
    }

    private void sendToGmail(String subject, String email) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.CATEGORY_APP_EMAIL, true);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
        final PackageManager pm = getPackageManager();
        final List<ResolveInfo> matches = pm.queryIntentActivities(intent, 0);
        ResolveInfo best = null;
        for (final ResolveInfo info : matches)
            if (info.activityInfo.packageName.endsWith(".gm") || info.activityInfo.name.toLowerCase().contains("gmail"))
                best = info;
        if (best != null)
            intent.setClassName(best.activityInfo.packageName, best.activityInfo.name);
        startActivity(intent);
    }

    public void openSearchActivity(View view) {
        startActivityForResult(new Intent(MainActivity.this, SearchPrdouctActivity.class), 10);
    }
}