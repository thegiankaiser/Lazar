package com.lazar.mobile;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public final class ApiClient {
    // Android emulator reaches the host computer through 10.0.2.2.
    // Replace with your deployed HTTPS URL for production.
    private static final String BASE_URL = "http://10.0.2.2:8080/";
    private static final LazarApi API = new Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(LazarApi.class);

    private ApiClient() {}
    public static LazarApi api() { return API; }
}
