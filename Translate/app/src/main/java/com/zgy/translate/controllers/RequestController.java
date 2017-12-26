package com.zgy.translate.controllers;

import android.content.Context;

import com.zgy.translate.base.BaseResponseObject;
import com.zgy.translate.domains.request.CommonRequest;
import com.zgy.translate.domains.response.CommonResponse;
import com.zgy.translate.http.ApiServiceInterface;
import com.zgy.translate.http.RetrofitHttp;
import com.zgy.translate.utils.ConfigUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by zhouguangyue on 2017/11/20.
 */

public class RequestController {

    public static final int SEND_CODE = 0;
    public static final int REGISTER = 1;
    public static final int LOGIN = 2;
    public static final int LOGOUT = 3;
    public static final int PROFILE = 4; //用户信息修改
    public static final int PASSWORD = 5; //用户修改密码
    public static final int SEND_PASSWORD_CODE = 6; //用户找回密码短信
    public static final int RESET_PASSWORD = 7; //用户重制密码
    public static final int CHANGE_ICON = 8; //修改用户头像


    private static RequestController requestController;
    private Call<CommonResponse> callResponse;
    private RequestCallInterface callInterface;
    private Context mContext;
    private ApiServiceInterface apiServiceInterface;

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

    public RequestController init(Context context){
        mContext = context;
        apiServiceInterface = RetrofitHttp.getApiService(context);
        return this;
    }

    public RequestController addRequest(int tag, CommonRequest request, String phone){
       switch (tag){
           case SEND_CODE:
               callResponse = apiServiceInterface.send_code(phone);
               break;
           case REGISTER:
               callResponse = apiServiceInterface.registe(request);
               break;
           case LOGIN:
               callResponse = apiServiceInterface.login(request);
               break;
           case LOGOUT:
               callResponse = apiServiceInterface.logout(request);
               break;
           case PROFILE:
               callResponse = apiServiceInterface.profile(request);
               break;
           case PASSWORD:
               callResponse = apiServiceInterface.password(request);
               break;
           case SEND_PASSWORD_CODE:
               callResponse = apiServiceInterface.send_reset_password_code(request);
               break;
           case RESET_PASSWORD:
               callResponse = apiServiceInterface.reset_password(request);
               break;
           case CHANGE_ICON:
               callResponse = apiServiceInterface.change_icon(request);
               break;
       }
        return this;
    }

    public RequestController addCallInterface(RequestCallInterface callInterface){
        this.callInterface = callInterface;
        return this;
    }

    public void build(){
        if(callResponse == null){
            ConfigUtil.showToask(mContext, "请求参数不能为空");
            return;
        }
        callResponse.enqueue(new Callback<CommonResponse>() {
            @Override
            public void onResponse(Call<CommonResponse> call, Response<CommonResponse> response) {
                if(response.code() == 200){
                    if(response.body() != null){
                        callInterface.success(response.body());
                    }
                }else {
                    callInterface.error(response.body());
                }
            }

            @Override
            public void onFailure(Call<CommonResponse> call, Throwable t) {
                callInterface.fail(t.toString());
            }
        });
    }




    public interface RequestCallInterface{
        void success(CommonResponse response);
        void error(CommonResponse response);
        void fail(String error);
    }

}
