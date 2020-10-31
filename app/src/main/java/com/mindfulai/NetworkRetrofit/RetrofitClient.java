package com.mindfulai.NetworkRetrofit;
import androidx.annotation.Nullable;

import com.mindfulai.ministore.BuildConfig;
import com.mindfulai.Utils.GlobalEnum;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Authenticator;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    public static final String TAG = RetrofitClient.class.getSimpleName();

    private static Retrofit retrofit = null;
    public static OkHttpClient client;
    public static final int DEFAULT_TIMEOUT_SEC = 60;

    public static Retrofit getClient(String baseUrl) {

        OkHttpClient.Builder httpClient =   new OkHttpClient.Builder();
        if (BuildConfig.DEBUG) {
            httpClient.addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY));
        }


        client = httpClient.build();

        retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        return retrofit;
    }

    public static Retrofit getClientWithHeader(String baseUrl, final String token) {


        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient = new OkHttpClient.Builder();

        if (BuildConfig.DEBUG) {
            httpClient.addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY));
            httpClient.addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.HEADERS));
        }

        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();

                Request request = original.newBuilder()
                        .header(GlobalEnum.PREFS_OAUTH_KEY, token)
                        .build();

                return chain.proceed(request);


            }
        });


        httpClient.authenticator(new TokenAuthenticator());

        client = httpClient.build();

        retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        return retrofit;
    }
   static class TokenAuthenticator implements Authenticator {


        @Nullable
        @Override
        public Request authenticate(Route route, Response response) throws IOException {

            return null;
        }
    }


    public static Retrofit getImageClient(String baseUrl, final String token) {

        OkHttpClient.Builder httpClient =   new OkHttpClient.Builder();
        if (BuildConfig.DEBUG) {
            httpClient.addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY));

        }

        Interceptor nwIntercepter = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                Response response = chain.proceed(request);

                return response;
            }
        };

        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();


                Request request = original.newBuilder()
                        .header(GlobalEnum.PREFS_OAUTH_KEY, token)
                        .build();

                return chain.proceed(request);


            }
        });


        httpClient.addInterceptor(nwIntercepter);


        httpClient.readTimeout(20, TimeUnit.MINUTES)
                .connectTimeout(20, TimeUnit.MINUTES);

        client = httpClient.build();

        retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        return retrofit;
    }


}
