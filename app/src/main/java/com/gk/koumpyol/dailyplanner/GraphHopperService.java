package com.gk.koumpyol.dailyplanner;

import com.google.gson.JsonObject;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GraphHopperService
{
    @GET("route")
    Call<GraphHopperData.GraphHopperResponse> getRoute(
            @Query("point") String startPoint,
            @Query("point") String destPoint,
            @Query("profile") String profile,
            @Query("key") String apiKey
    );
}
