package com.gk.koumpyol.dailyplanner;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NominatimService
{
    @GET("reverse")
    Call<NominatimResponse> reverseGeocode(
            @Query("format") String format,
            @Query("lat") double latitude,
            @Query("lon") double longitude,
            @Query("zoom") int zoom
    );
}
