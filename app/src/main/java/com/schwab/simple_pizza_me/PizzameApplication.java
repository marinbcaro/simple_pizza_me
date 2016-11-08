package com.schwab.simple_pizza_me;

import android.app.Application;
import android.util.Log;

import com.schwab.simple_pizza_me.network.YQL;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by adam.fitzgerald on 10/31/16.
 */
public class PizzameApplication extends Application {


    private YQL yql;

    @Override
    public void onCreate() {
        super.onCreate();


        OkHttpClient okhttpClient = new OkHttpClient.Builder()
                .addNetworkInterceptor(new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
                    @Override
                    public void log(String message) {
                        Log.i("LOG", message);
                    }
                }).setLevel(HttpLoggingInterceptor.Level.BODY))
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://query.yahooapis.com")
                .addConverterFactory(GsonConverterFactory.create())
                .client(okhttpClient)
                .build();

        yql = retrofit.create(YQL.class);

    }


    public YQL getYql() {
        return yql;
    }
}
