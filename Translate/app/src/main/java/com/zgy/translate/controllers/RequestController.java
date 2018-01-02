package com.zgy.translate.controllers;

import android.content.Context;
import android.util.Log;

import com.zgy.translate.base.BaseResponseObject;
import com.zgy.translate.domains.request.CommonRequest;
import com.zgy.translate.domains.response.CommonResponse;
import com.zgy.translate.http.ApiServiceInterface;
import com.zgy.translate.http.RetrofitHttp;
import com.zgy.translate.managers.GsonManager;
import com.zgy.translate.utils.ConfigUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
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
    public static final int GET_PROFILE = 9; //获取用户信息
    public static final int CHANGE_PHONE_CODE = 10; //发送更改手机号验证码
    public static final int CHANGE_PHONE = 11; //绑定新手机号


    private static final String KEY_PHONE = "Phone";
    private static final String KEY_PHONE_CODE = "PhoneCode";
    private static final String KEY_PASSWORD = "Password";
    private static final String KEY_PASSWPRD_REPEAT = "PasswordRepeat";
    private static final String KEY_APP_ID = "AppID";
    private static final String KEY_DEVICE = "Device";
    private static final String KEY_SEX = "Sex";
    private static final String KEY_ICON = "Icon";
    private static final String KEY_BIRTHDAY = "Birthday";
    private static final String KEY_SIGNATURE = "Signature";
    private static final String KEY_NAME = "Name";
    private static final String KEY_FILE = "file";


    private static RequestController requestController;
    private Call<CommonResponse> callResponse;
    private RequestCallInterface callInterface;
    private Context mContext;
    private ApiServiceInterface apiServiceInterface;
    private Map<String, String> requestMap;

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

    public RequestController addRequest(int tag, CommonRequest request){
        if(request == null){
            ConfigUtil.showToask(mContext, "请求参数不能为空");
            return null;
        }
        clearRequestMap();
        requestMap = new HashMap<>();
        if(tag == SEND_CODE || tag == REGISTER || tag == LOGIN || tag == SEND_PASSWORD_CODE || tag == RESET_PASSWORD
                || tag == CHANGE_PHONE_CODE || tag == CHANGE_PHONE){
            requestMap.put(KEY_PHONE, request.getPhone());
        }
        if(tag == LOGIN){
            requestMap.put(KEY_APP_ID, request.getAppId());
        }
        if(tag == LOGIN){
            requestMap.put(KEY_DEVICE, request.getDevice());
        }
        if(tag == REGISTER || tag == LOGIN || tag == PASSWORD || tag == RESET_PASSWORD){
            requestMap.put(KEY_PASSWORD, request.getPassword());
        }
        if(tag == REGISTER || tag == PASSWORD || tag == RESET_PASSWORD){
            requestMap.put(KEY_PASSWPRD_REPEAT, request.getPasswrodRepeat());
        }
        if(tag == REGISTER || tag == RESET_PASSWORD || tag == CHANGE_PHONE){
            requestMap.put(KEY_PHONE_CODE, request.getPhoneCode());
        }
        if(tag == PROFILE && request.getSex() != null){
            requestMap.put(KEY_SEX, request.getSex());
        }
        if(tag == PROFILE && request.getSignature() != null){
            requestMap.put(KEY_SIGNATURE, request.getSignature());
        }
        if(tag == PROFILE && request.getName() != null){
            requestMap.put(KEY_NAME, request.getName());
        }
        if(tag == PROFILE && request.getIcon() != null){
            requestMap.put(KEY_ICON, request.getIcon());
        }
        if(tag == PROFILE && request.getBirthday() != null){
            requestMap.put(KEY_BIRTHDAY, request.getBirthday());
        }
        if(tag == LOGOUT){
            requestMap.put(KEY_PHONE, request.getPhone());
        }

        Log.i("remap--", requestMap.size() + "");
        switch (tag){
           case SEND_CODE:
               callResponse = apiServiceInterface.send_code(requestMap);
               break;
           case REGISTER:
               callResponse = apiServiceInterface.registe(requestMap);
               break;
           case LOGIN:
               callResponse = apiServiceInterface.login(requestMap);
               break;
           case LOGOUT:
              callResponse = apiServiceInterface.logout(requestMap);
               break;
            case GET_PROFILE:
                callResponse = apiServiceInterface.profil();
           case PROFILE:
              callResponse = apiServiceInterface.profile(requestMap);
               break;
           case PASSWORD:
               callResponse = apiServiceInterface.password(requestMap);
               break;
           case SEND_PASSWORD_CODE:
               callResponse = apiServiceInterface.send_reset_password_code(requestMap);
               break;
           case RESET_PASSWORD:
               callResponse = apiServiceInterface.reset_password(requestMap);
               break;
           case CHANGE_ICON:
               RequestBody body;
               if(request.getFile().getName().contains(".jpg")){
                   body = RequestBody.create(MediaType.parse("image/jpg"), request.getFile());
               }else if(request.getFile().getName().contains(".jpeg")){
                   body = RequestBody.create(MediaType.parse("image/jpeg"), request.getFile());
               }else if(request.getFile().getName().contains(".png")){
                   body = RequestBody.create(MediaType.parse("image/png"), request.getFile());
               }else if(request.getFile().getName().contains(".bmp")){
                   body = RequestBody.create(MediaType.parse("image/bmp"), request.getFile());
               }else if(request.getFile().getName().contains(".gif")){
                    body = RequestBody.create(MediaType.parse("image/gif"), request.getFile());
                }else{
                   body = RequestBody.create(MediaType.parse("multipart/form-data"), request.getFile());
               }
               MultipartBody.Part part = MultipartBody.Part.createFormData("file", request.getFile().getName(), body);
               callResponse = apiServiceInterface.change_icon(part);
               break;
            case CHANGE_PHONE_CODE:
                callResponse = apiServiceInterface.change_phone_code(requestMap);
                break;
            case CHANGE_PHONE:
                callResponse = apiServiceInterface.change_phone(requestMap);
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
                    callInterface.success(response.body());
                }else {
                    try {
                        String errors = response.errorBody().string();
                        CommonResponse re = GsonManager.getInstance().fromJson(errors, CommonResponse.class);
                        if(re != null && re.getErrors() != null){
                            callInterface.error(re);
                            ConfigUtil.showToask(mContext, re.getErrors().get(0).getMessage());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<CommonResponse> call, Throwable t) {
                callInterface.fail(t.toString());
                ConfigUtil.showToask(mContext, t.toString());
            }
        });
    }

    private void clearRequestMap(){
        if(requestMap != null){
            requestMap.clear();
            requestMap = null;
        }
    }



    public interface RequestCallInterface{
        void success(CommonResponse response);
        void error(CommonResponse response);
        void fail(String error);
    }

}
