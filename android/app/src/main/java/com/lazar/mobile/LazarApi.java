package com.lazar.mobile;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface LazarApi {
    @POST("api/auth/register") Call<ApiModels.AuthResponse> register(@Body ApiModels.AuthRequest request);
    @POST("api/auth/login") Call<ApiModels.AuthResponse> login(@Body ApiModels.AuthRequest request);
    @POST("api/chat") Call<ApiModels.ChatResponse> chat(@Header("Authorization") String bearerToken, @Body ApiModels.ChatRequest request);
    @GET("api/chat/history") Call<java.util.List<ApiModels.ChatResponse>> history(@Header("Authorization") String bearerToken);
}
