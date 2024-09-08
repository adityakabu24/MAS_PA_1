package com.example.mobileappsec;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NationalizeService {
    @GET("/")
    Call<ApiResponse> getNationalities(@Query("name") String name);
}