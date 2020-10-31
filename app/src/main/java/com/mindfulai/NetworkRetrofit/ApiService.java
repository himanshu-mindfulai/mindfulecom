package com.mindfulai.NetworkRetrofit;

import com.google.gson.JsonObject;
import com.mindfulai.Models.AllOrderHistory.Datum;
import com.mindfulai.Models.AllOrderHistory.DatumModel;
import com.mindfulai.Models.AllOrderHistory.OrderHistory;
import com.mindfulai.Models.BannerInfoData.BannerCategoryData;
import com.mindfulai.Models.BannerInfoData.BannerData;
import com.mindfulai.Models.BrandModel;
import com.mindfulai.Models.CartInformation.CartDetailsInformation;
import com.mindfulai.Models.CustomerInfo.CustomerData;
import com.mindfulai.Models.ProductVarients.ProductVarients;
import com.mindfulai.Models.ReviewData.ReviewData;
import com.mindfulai.Models.SlotModelBase;
import com.mindfulai.Models.SubcategoryModel.SubcategoryModel;
import com.mindfulai.Models.UserBaseAddress;
import com.mindfulai.Models.VarientById.VarientByIdResponse;
import com.mindfulai.Models.VendorBase;
import com.mindfulai.Models.WalletRechargeModel.WalletRechargeModel;
import com.mindfulai.Models.categoryData.CategoryInfo;
import com.mindfulai.Models.config.ConfigResponse;
import com.mindfulai.Models.orderDetailInfo.OrderDetailInfo;
import com.mindfulai.Models.varientsByCategory.VarientsByCategory;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;


public interface ApiService {

    @POST("api/auth/login/mobile")
    Call<JsonObject> loginmobile(@Body JsonObject jsonObject);

    @POST("api/auth/resendotp")
    Call<JsonObject> resendOtp(@Body JsonObject jsonObject);

    @POST("api/auth/login")
    Call<JsonObject> loginemailPassword(@Body JsonObject jsonObject);

    @POST("api/oauth/authenticate/google/appcallback")
    Call<JsonObject> googleLogin(@Body JsonObject jsonObject);

    @POST("api/auth/verifyotp/customer")
    Call<JsonObject> verifyOtp(@Query("rc") String referralCode, @Body JsonObject jsonObject);

    @POST("api/product/review")
    Call<JsonObject> addReview(@Body JsonObject jsonObject);

    @GET("api/product/review/byvarient/{id}")
    Call<ReviewData> getReview(@Path("id") String id);

    @POST("api/cart")
    Call<JsonObject> addItemToCart(@Body JsonObject jsonObject);

    @DELETE("api/cart/{id}")
    Call<JsonObject> removeItemFromCart(@Path("id") String id);

    @GET("api/cart")
    Call<CartDetailsInformation> showCartItems();

    @POST("api/cart/applycoupon")
    Call<CartDetailsInformation> applyCoupon(@Body JsonObject jsonObject);

    @POST("api/orders/wishlist")
    Call<JsonObject> addItemToWishlist(@Body JsonObject jsonObject);

    @DELETE("api/orders/wishlist/{id}")
    Call<JsonObject> removeItemFromWishlist(@Path("id") String id);


    @GET("api/auth/me")
    Call<CustomerData> getProfileDetails();


    @PUT("api/cart/{id}")
    Call<JsonObject> updateCartItem(@Path("id") String id, @Body JsonObject jsonObject);

    @POST("api/cart/placeorder")
    Call<OrderDetailInfo> PlaceOrder(@Body JsonObject jsonObject);

    @POST("api/cart/placeorder/service")
    Call<OrderDetailInfo> PlaceOrderService(@Body JsonObject jsonObject);


    @GET("api/corder")
    Call<OrderHistory> getOrderHistory();


    @GET("api/banner")
    Call<BannerData> getBannerData();

    @GET("api/banner/category/{id}")
    Call<BannerCategoryData> getCategoryBannerData(@Path("id") String id);

    @GET("api/banner/slider2")
    Call<BannerData> getBannerData2();


    @Multipart
    @POST("api/user/update/me")
    Call<JsonObject> uploadFile(@Part MultipartBody.Part photo,
                                @Part("full_name") RequestBody name,
                                @Part("email") RequestBody email,
                                @Part("mobile_number") RequestBody mobileNumber
    );

    @GET("api/product/category/all/product")
    Call<CategoryInfo> getAllProductCategory();

    @GET("api/product/category/all/service")
    Call<CategoryInfo> getAllServiceCategory();

    @GET("api/public/varients/trending")
    Call<VarientsByCategory> getAllTrending(@Query("id") String id);

    @GET("api/public/varients/trending")
    Call<VarientsByCategory> getAllTrending();

    //api/public/varients/trending/byvendor/{id}

    @GET("api/orders/wishlist")
    Call<VarientsByCategory> getAllWishlist();


    @GET("api/product/category/subcat/all/{id}")
    Call<SubcategoryModel> getAllSubCategory(@Path("id") String id);

    @GET("api/product/brand")
    Call<BrandModel> getAllBrand();

//    @GET("api/product/varients/btid/{id}")
//    Call<>

    @GET("api/product/varients/byid/{id}")
    Call<VarientByIdResponse> getVarientById(@Path("id") String cid);

    @GET("api/product/varients/bycategory/{id}")
    Call<VarientsByCategory> getAllProductsVarients(@Path("id") String cid, @Query("type") String type);

    @GET("api/product/varients/find")
    Call<VarientsByCategory> getSearchProducts(@Query("query") String query);

    @GET("api/user/vendor/id/{id}")
    Call<VendorBase> getVendorProfile(@Path("id") String id);

    @GET("/api/user/vendor/bycategory/{id}")
    Call<VendorBase> getVendorByCategoryId(@Path("id") String id);

    @POST("api/product/varients/related")
    Call<ProductVarients> getAllrelatedVarients(@Body JsonObject jsonObject);

    @GET("api/address/")
    Call<UserBaseAddress> getUserBaseAddress();

    @GET("api/deliveryslot")
    Call<SlotModelBase> getSlot();

    @POST("api/payment/verify")
    Call<OrderDetailInfo> verifyOnlinePayment(@Body JsonObject jsonObject);

    @POST("api/payment/walletrecharge")
    Call<WalletRechargeModel> walletRecharge(@Body JsonObject jsonObject);

    @POST("api/corder/action")
    Call<DatumModel> orderAction(@Body JsonObject jsonObject);

    @PUT("api/corder/status/{id}")
    Call<DatumModel> cancelOrder(@Path("id") String id, @Body JsonObject jsonObject);


    @POST("api/firebase/notification/add")
    Call<JsonObject> addFCMToken(@Body JsonObject jsonObject);

    @GET("api/config")
    Call<ConfigResponse> getConfig();

    String ABOUTUS = ServerURL.SERVER_URL + "/api/aboutus";
    String GET_ADDRESS = ServerURL.SERVER_URL + "/api/address";
    String UPDATE_ADDRESS = ServerURL.SERVER_URL + "/api/address/id/";
    String ADD_CUSTOMER = ServerURL.SERVER_URL + "/api/user/add/customer";
    String FAQ = ServerURL.SERVER_URL + "/api/faq";
    String PRIVACY = ServerURL.SERVER_URL + "/api/privacypolicy";
    String RETURN_POLICY = ServerURL.SERVER_URL + "/api/returnpolicy";
    String TNC = ServerURL.SERVER_URL +"/api/tnc";
    String FLASH_BANNER = ServerURL.SERVER_URL +"/api/flashbanner";
}