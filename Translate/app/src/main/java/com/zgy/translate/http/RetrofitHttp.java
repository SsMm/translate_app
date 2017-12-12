package com.zgy.translate.http;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by zhou on 2017/4/27.
 */

public class RetrofitHttp {

    //public static final String IP = "101.201.209.61:8080";//
    public static final String IP = "182.92.111.7";
    //public static final String IP = "192.168.2.137";
    private static final String Http = "http://"+IP+"/";
    private static Retrofit retrofit;


    private RetrofitHttp(){}

    public static ApiServiceInterface getApiService(Context context){
        if(retrofit == null){
            synchronized (RetrofitHttp.class){
                if(retrofit == null){
                    Gson gson = new GsonBuilder().setLenient().create();
                    retrofit = new Retrofit.Builder()
                            .baseUrl(Http)
                            .addConverterFactory(GsonConverterFactory.create(gson))
                            .addConverterFactory(ScalarsConverterFactory.create())
                            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                            .client(OkhttpHttp.getOkHttpClient(context))
                            .build();
                }
            }
        }

        return retrofit.create(ApiServiceInterface.class);
    }

}
