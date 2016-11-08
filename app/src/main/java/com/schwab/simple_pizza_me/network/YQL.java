package com.schwab.simple_pizza_me.network;


import com.schwab.simple_pizza_me.network.model.QueryResult;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by adam on 7/31/16.
 */
public interface YQL {

    @GET("/v1/public/yql")
    Call<QueryResult> getPlaces(@Query("q") String query, @Query("format") String format);

}
