package com.mindfulai.Utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mindfulai.Models.MyError;
import com.mindfulai.NetworkRetrofit.ApiService;
import com.mindfulai.NetworkRetrofit.ApiUtils;
import com.mindfulai.ministore.R;
import com.valdesekamdem.library.mdtoast.MDToast;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class CommonUtils {
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private Context context;
    private List<Address> addresses;
    private final String TAG = "CommonUtils";

    public static int calculateNoOfColumns(Context context, float columnWidthDp) { // For example columnWidthdp=180
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float screenWidthDp = displayMetrics.widthPixels / displayMetrics.density;
        int noOfColumns = (int) (screenWidthDp / columnWidthDp + 0.5); // +0.5 for correct rounding to int.
        return noOfColumns;
    }

    public void showSuccessMessage(String message) {
        MDToast.makeText(context, message, MDToast.LENGTH_SHORT, MDToast.TYPE_SUCCESS).show();
    }

    public void showErrorMessage(String message) {
        MDToast.makeText(context, message, MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
    }

    public void showInfoMessage(String message) {
        MDToast.makeText(context, message, MDToast.LENGTH_SHORT, MDToast.TYPE_INFO).show();
    }


    public GridLayoutManager getProductGridLayoutManager() {
        int prod_columns = calculateNoOfColumns(context, 200);
        return new GridLayoutManager(context, prod_columns);
    }

    public GridLayoutManager getCategoriesGridLayoutManager() {
        int cat_columns = CommonUtils.calculateNoOfColumns(context, 130);
        return new GridLayoutManager(context, cat_columns);
    }

    public void setCartItems(Integer items) {
        int numberOfItems = getCartItems();
        context.getSharedPreferences("cart", MODE_PRIVATE).edit().putInt("number", numberOfItems + items).apply();

    }

    public Integer getCartItems() {
        return context.getSharedPreferences("cart", MODE_PRIVATE).getInt("number", 0);
    }

    public CommonUtils(Context contextactivity) {
        context = contextactivity;

    }

    public static String capitalizeWord(String str) {
        String words[] = str.split("\\s");
        Log.i("CapitalizeWord:", Arrays.toString(words));
        String capitalizeWord = "";
        for (String w : words) {
            if (!w.isEmpty()) {
                String first = w.substring(0, 1);
                String afterfirst = w.substring(1);
                capitalizeWord += first.toUpperCase() + afterfirst + " ";
            }
        }
        return capitalizeWord.trim();
    }

    public void addItemToWishList(String id) {
        try {
            final CustomProgressDialog customProgressDialog = CommonUtils.showProgressDialog(context,
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
                        if (reponse_status.matches("200")) {
                            showSuccessMessage("Item added to wishlist !!");
                        }
                    } else {
                        showErrorMessage("" + response.message());
                    }

                }

                @Override
                public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                    Log.e("fail", call.toString());
                    CommonUtils.hideProgressDialog(customProgressDialog);
                }
            });
        } catch (Exception e) {
            Log.e("TAG", "addItemToWishList: " + e);
        }
    }

    public void removeItemFromWishList(String id) {
        try {
            final CustomProgressDialog customProgressDialog = CommonUtils.showProgressDialog(context,
                    "Removing from wishlist ... ");
            ApiService apiService = ApiUtils.getHeaderAPIService(SPData.getAppPreferences().getUsertoken());

            apiService.removeItemFromWishlist(id).enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                    CommonUtils.hideProgressDialog(customProgressDialog);
                    if (response.isSuccessful()) {
                        assert response.body() != null;
                        String reponse_status = String.valueOf(response.body().get("status"));
                        if (reponse_status.matches("200")) {
                            showSuccessMessage("Item remove from wishlist!!");
                        }
                    } else {
                        CommonUtils.hideProgressDialog(customProgressDialog);
                        showInfoMessage("" + response.message());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                    Log.e("fail", call.toString());
                    CommonUtils.hideProgressDialog(customProgressDialog);
                }
            });
        } catch (Exception e) {
            Log.e("TAG", "removeItemFromWishList: " + e);
        }
    }

    public double[] getCurrentLatAndLng() {
        final double[] res = new double[2];
        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
        if (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)) {
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    res[0] = task.getResult().getLongitude();
                    res[1] = task.getResult().getLongitude();
                }
            });
        }
        return res;
    }

    public List<Address> getAddress(final TextView textViewLocation) {
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        if (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)) {
            if (SPData.getAppPreferences().getLongitude().equals(""))
                fusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.getResult() != null) {
                            double longitude = Objects.requireNonNull(task.getResult()).getLongitude();
                            double latitude = task.getResult().getLatitude();
                            SPData.getAppPreferences().setLatitude("" + latitude);
                            SPData.getAppPreferences().setLongitude("" + longitude);
                            setCurrentAddress(longitude, latitude, textViewLocation);
                        } else {
                            statusCheck();
                            textViewLocation.setText("-");
                            MDToast.makeText(context, "Unable to access location ", MDToast.LENGTH_SHORT, MDToast.TYPE_INFO).show();
                        }
                    }
                });
            else {
                double longitude = Double.parseDouble(SPData.getAppPreferences().getLongitude());
                double latitude = Double.parseDouble(SPData.getAppPreferences().getLatitude());
                setCurrentAddress(longitude, latitude, textViewLocation);
            }
        } else
            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
        return addresses;
    }

    public void statusCheck() {
        final LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        }
    }

    private void setCurrentAddress(double longitude, double latitude, TextView textViewLocation) {

        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            String city = addresses.get(0).getLocality();
            String subLocality = addresses.get(0).getSubLocality();
            textViewLocation.setText(subLocality + "," + " " + city);
        } catch (Exception e) {
            e.printStackTrace();
            textViewLocation.setText("Add address");
            Log.e(TAG, "onComplete: " + e);
        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                        ((Activity) context).startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    public void sendDebugReport(String TAG, String e) {
//        CollectionReference collectionReference = FirebaseFirestore.getInstance().collection("debug");
//        HashMap<String, String> hashMap = new HashMap<>();
//        hashMap.put("exception", "" + e);
//        hashMap.put("where", TAG);
//        collectionReference.add(hashMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentReference> task) {
//
//            }
//        });
    }

    public static CustomProgressDialog showProgressDialog(Context context, String message) {
        CustomProgressDialog mCustomeProgressDialog = new CustomProgressDialog(context, message);
        mCustomeProgressDialog.setCancelable();
        mCustomeProgressDialog.show();
        return mCustomeProgressDialog;
    }

    public static void hideProgressDialog(CustomProgressDialog mCustomeProgressDialog) {
        if (mCustomeProgressDialog != null && mCustomeProgressDialog.isShowing()) {
            mCustomeProgressDialog.dismiss();
        }
    }

    public static void unsuccessfull(ResponseBody errorBody, int resultCode, Context context) {

        if (resultCode == 400 || resultCode == 500 || resultCode == 403) {
            try {
                if (errorBody != null) {

                    MyError error = new Gson().fromJson(errorBody.string(), MyError.class);

                    Toast.makeText(context, error.getError(), Toast.LENGTH_SHORT).show();

                    /*JSONObject jsonObject=new JSONObject(errorBody.string());
                    if (jsonObject.has("message")){
                        Toast.makeText(context,jsonObject.getString("message"),Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(context,context.getString(R.string.something_went_wrong),Toast.LENGTH_SHORT).show();
                    }*/
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (resultCode == 401) {
            Toast.makeText(context, context.getString(R.string.token_expired), Toast.LENGTH_SHORT).show();
        } else if (resultCode == 404) {
            CommonUtils.isConnectedToInternet(context, true);
        } else {
            CommonUtils.showToast(context, context.getString(R.string.server_down));
        }
    }

    public static boolean isConnectedToInternet(Context context, Boolean isShowWarning) {

        Boolean isConnected = false;
        try {
            ConnectivityManager connectivity = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = connectivity.getActiveNetworkInfo();
            isConnected = activeNetwork != null &&
                    activeNetwork.isConnected();

        } catch (Exception e) {
            // do nothing
        }

        if (!isConnected && isShowWarning) {
            Toast.makeText(context, context.getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
        }

        return isConnected;
    }

    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }


    @NotNull
    public static LatLng getCurrentLocation(Context context) {
        final LatLng[] latLng = new LatLng[1];
        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
        if (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)) {
            LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            latLng[0] = new LatLng(location.getLatitude(), location.getLongitude());
            return latLng[0];

        } else {
            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
        }
        return latLng[0];
    }

}
