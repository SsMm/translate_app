package com.zgy.translate.http;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zgy.translate.managers.NullOnEmptyConverterFactory;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by zhou on 2017/4/27.
 */

public class RetrofitHttp {


    public static final String IP = "api.toppers.com.cn";
    private static final String Http = "https://"+IP+"/";
    private static Retrofit retrofit;


    private RetrofitHttp(){}

    public static ApiServiceInterface getApiService(Context context){
        if(retrofit == null){
            synchronized (RetrofitHttp.class){
                if(retrofit == null){
                    Gson gson = new GsonBuilder().setLenient().create();
                    retrofit = new Retrofit.Builder()
                            .baseUrl(Http)
                            .addConverterFactory(new NullOnEmptyConverterFactory())
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
