package com.mindfulai.NetworkRetrofit;


public class ApiUtils {


    public static ApiService getAPIService() {

        return RetrofitClient
                .getClient(ServerURL.SERVER_URL + "/")
                .create(ApiService.class);
    }

    public static ApiService getHeaderAPIService(String token) {

        return RetrofitClient
                .getClientWithHeader(ServerURL.SERVER_URL + "/", token)
                .create(ApiService.class);
    }
    public static ApiService getHeaderAPIService() {

        return RetrofitClient
                .getClient(ServerURL.SERVER_URL+ "/")
                .create(ApiService.class);
    }

    public static ApiService getImageAPIService(String token) {

        return RetrofitClient
                .getImageClient(ServerURL.SERVER_URL + "/",token)
                .create(ApiService.class);
    }


}
