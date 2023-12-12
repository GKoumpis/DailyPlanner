package com.gk.koumpyol.dailyplanner;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GraphHopperApiClient
{
    public static final String BASE_URL = "https://graphhopper.com/api/1/";

    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
