package com.mindfulai.AppPrefrences;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by ankur on 12/23/2016.
 */

public class AppPreferences {

    private SharedPreferences mPreferences;
    private static SharedPreferences.Editor mEditor;

    enum SharedPrefrencesKey {
        UserId,
        Usertoken,
        UserType,
        UserAddress,
        UserCart,
        UserName,
        UserEmail,
        User_mobile_no,
        Latitude,
        Longitude,
        UserProfilePic,
        varient_available,
        varient_stock,
        varient_id,
        varient_minQty,
        paymentSuccess
    }

    public String getEmail(){
        return mPreferences.getString(SharedPrefrencesKey.UserEmail.toString(), "");
    }

    public void setEmail(String email){
        mEditor.putString(SharedPrefrencesKey.UserEmail.toString(), email).apply();
    }

    public void setPaymentSuccess(boolean payment){
        mEditor.putBoolean(SharedPrefrencesKey.paymentSuccess.toString(),payment);
        mEditor.apply();
    }

    public boolean getPaymentSuccess(){
        return mPreferences.getBoolean(SharedPrefrencesKey.paymentSuccess.toString(),false);
    }

    public void setVarientMinQty(int minqty){
        mEditor.putInt(SharedPrefrencesKey.varient_minQty.toString(), minqty);
        mEditor.apply();
    }

    public int getVarientMinQty(){
        return mPreferences.getInt(SharedPrefrencesKey.varient_minQty.toString(), 0);
    }

    public void setVarientId(String id){
        mEditor.putString(SharedPrefrencesKey.varient_id.toString(), id);
        mEditor.apply();
    }

    public String getVarientId(){
        return mPreferences.getString(SharedPrefrencesKey.varient_id.toString(), "");
    }


    public void setVarientStock(int stock) {
        mEditor.putInt(SharedPrefrencesKey.varient_stock.toString(), stock);
        mEditor.apply();
    }

    public int getVarientStock() {
        return mPreferences.getInt(SharedPrefrencesKey.varient_stock.toString(), 0);
    }


    public void setVarientAvailable(String available) {
        mEditor.putString(SharedPrefrencesKey.varient_available.toString(), available);
        mEditor.apply();
    }

    public String getVarientAvailable() {
        return mPreferences.getString(SharedPrefrencesKey.varient_available.toString(), "");
    }

    public void setLatitude(String latitude) {
        mEditor.putString(SharedPrefrencesKey.Latitude.toString(), latitude);
        mEditor.commit();
    }

    public String getLatitude() {
        return mPreferences.getString(SharedPrefrencesKey.Latitude.toString(), "");
    }

    public void setLongitude(String longitude) {
        mEditor.putString(SharedPrefrencesKey.Longitude.toString(), longitude);
        mEditor.commit();
    }

    public String getLongitude() {
        return mPreferences.getString(SharedPrefrencesKey.Longitude.toString(), "");
    }

    public void setUser_mobile_no(String user_mobile_no) {
        mEditor.putString(SharedPrefrencesKey.User_mobile_no.toString(), user_mobile_no);
        mEditor.commit();
    }

    public String getMobileNumber() {
        return mPreferences.getString(SharedPrefrencesKey.User_mobile_no.toString(), "");
    }

    public void setAddress(String add) {
        mEditor.putString(SharedPrefrencesKey.UserAddress.toString(), add);
        mEditor.apply();
    }

    public String getAddress() {
        return mPreferences.getString(SharedPrefrencesKey.UserAddress.toString(), "");
    }

    public void setUserId(String userId) {
        mEditor.putString(SharedPrefrencesKey.UserId.toString(), userId);
        mEditor.apply();
    }

    public String getUserId() {
        return mPreferences.getString(SharedPrefrencesKey.UserId.toString(), "");
    }

    public void setUsertoken(String usertoken) {
        mEditor.putString(SharedPrefrencesKey.Usertoken.toString(), usertoken);
        mEditor.apply();
    }

    public String getUsertoken() {
        return mPreferences.getString(SharedPrefrencesKey.Usertoken.toString(), "");
    }

    public void setTotalCartCount(int count) {
        mEditor.putInt(SharedPrefrencesKey.UserCart.toString(), count);
        mEditor.apply();
    }

    public int getTotalCartCount() {
        return mPreferences.getInt(SharedPrefrencesKey.UserCart.toString(), -1);
    }

    public void setUserType(String userType) {
        mEditor.putString(SharedPrefrencesKey.UserType.toString(), userType);
        mEditor.apply();
    }

    public void setUserName(String name) {
        mEditor.putString(SharedPrefrencesKey.UserName.toString(), name);
        mEditor.apply();
    }

    public String getUserName() {
        return mPreferences.getString(SharedPrefrencesKey.UserName.toString(), "");
    }

    public String getUserType() {
        return mPreferences.getString(SharedPrefrencesKey.UserType.toString(), "");
    }

    public void setUserProfilePic(String userProfilePic) {
        mEditor.putString(SharedPrefrencesKey.UserProfilePic.toString(), userProfilePic);
        mEditor.apply();
    }

    public String getUserProfilePic(){
        return mPreferences.getString(SharedPrefrencesKey.UserProfilePic.toString(),"");
    }

    @SuppressLint("CommitPrefEdits")
    public AppPreferences(Context context) {
        mPreferences = context.getSharedPreferences(PreferenceID.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        mEditor = mPreferences.edit();
    }

    public void clearAppPreference() {
        if (mPreferences != null) {
            mEditor.clear().commit();
        }
    }
}
