package com.zgy.translate.controllers;

import com.zgy.translate.domains.CommonResponseObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by zhouguangyue on 2017/11/20.
 */

public class RequestController {

    private static RequestController requestController;
    //private Call<CommonResponseObject<T>> callResponse;


    public static RequestController getInstance() {
        if(requestController == null){
            synchronized (RequestController.class){
                if(requestController == null){
                    requestController = new RequestController();
                }
            }
        }
        return requestController;
    }

   /* public RequestController addCallResponse(Call<CommonResponseObject<T>> response){
        callResponse = response;
        return this;
    }*/

    public RequestController addCallInterface(){

        return this;
    }


  /*  public void request(){
        Call<CommonResponseObject<T>> call = callResponse;
        call.enqueue(new Callback<CommonResponseObject<T>>() {
            @Override
            public void onResponse(Call<CommonResponseObject<T>> call, Response<CommonResponseObject<T>> response) {

            }

            @Override
            public void onFailure(Call<CommonResponseObject<T>> call, Throwable t) {

            }
        });
    }*/



}
