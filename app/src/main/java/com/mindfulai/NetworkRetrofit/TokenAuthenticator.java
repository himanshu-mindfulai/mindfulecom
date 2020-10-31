package com.mindfulai.NetworkRetrofit;


import androidx.annotation.Nullable;

import java.io.IOException;

import okhttp3.Authenticator;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

public class TokenAuthenticator implements Authenticator {


    @Nullable
    @Override
    public Request authenticate(Route route, Response response) throws IOException {

        return null;
    }
}
