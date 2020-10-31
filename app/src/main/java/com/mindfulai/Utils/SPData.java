package com.mindfulai.Utils;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.gson.JsonObject;
import com.mindfulai.AppPrefrences.AppPreferences;
import com.mindfulai.NetworkRetrofit.ApiService;
import com.mindfulai.NetworkRetrofit.ApiUtils;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SPData extends Application {

    @SuppressLint("StaticFieldLeak")
    private static Context mContext;
    private static AppPreferences appPreferences;
    private static String PRODUCTS_OR_SERVICES;

    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        Fresco.initialize(this);
        appPreferences = new AppPreferences(this);
    }

    public static Context getAppContext() {
        return mContext;
    }

    public static AppPreferences getAppPreferences() {
        return appPreferences;
    }

    public static boolean getShowVendor() {
        return false;
    }

    public static boolean showCod(){
        return true;
    }

    public static boolean showOnlinePay() { return true; }

    public static String whatsAppNumber(){
        return "+918357904406";
    }

    public static String supportCallNumber() {
        return whatsAppNumber();
    }

    public static String emailAddress(){
        return "info@iamsuperstore.in";
    }

    public static String getShareDomain() {
        return "iamsuperstore.com";
    }

    public static String recommendedText() {
        return "Recommended";
    }

    public static String certifiedText() {
        return "Certified";
    }

    public static boolean showCertifiedText() {
        return false;
    }

    public static boolean showServicesTab() {
        return false;
    }

    public static boolean showProductsAndCart() { return true; }

    public static void setProductsOrServices(String string){
        PRODUCTS_OR_SERVICES = string;
    }

    public static String getProductsOrServices(){
        return "PRODUCTS";
    }
    public static int noOfCategories(){
        return 9;
    }

    public static boolean showBrandOrVendorOnProductList() {
        return true;
    }

    public static String getShowBrandOrVendor() {
        return "BRAND";
    }

    public static boolean showBrand() {
        return false;
    }

    public static boolean showTimeSlotPicker() {
        return true;
    }

    public static boolean showReturnReplace() {
        return true;
    }

    public static String getAppShareText() {
        return "Download this super awesome grocery buying app "; // <App Link>
    }

    public static String getRazorPayKey(){
        return "rzp_test_8ZUQyQ5x2EiFM4";
    }

    public static String getRazorPaySecret(){
        return "okcGFzgWBwvbeQVDSoeK25wh";
    }

    public static String getRazorPayScreenTitle() {
        return "Ecomm";
    }

    public static String getRazorPayScreenSubtitle() {
        return "Order payment";
    }
    public static boolean showStaffLogin(){ return false;}

    public static boolean showVarientDropdown() {return true;}

    public static boolean showPickUp() {return true;}

    public static boolean showCaryBag() {return false;}

    public static boolean showAllProductsBanner() { return false;}

    public static boolean showBottomNavMenu(){
        return true;
    }

    public static boolean showGridView(){return false;}

    public  static boolean hideContactBtn(){return true;}

}