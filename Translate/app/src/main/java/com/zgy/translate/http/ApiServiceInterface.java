package com.zgy.translate.http;


import com.zgy.translate.domains.request.CommonRequest;
import com.zgy.translate.domains.response.CommonResponse;


import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Created by zhouguangyue on 2017/8/11.
 */

public interface ApiServiceInterface {

    String USERS = "users";

    @FormUrlEncoded
    @POST(USERS + "/send_phone_code")
    Call<CommonResponse> send_code(@FieldMap Map<String, String> request);

    @FormUrlEncoded
    @POST(USERS + "/register")
    Call<CommonResponse> registe(@FieldMap Map<String, String> request);

    @FormUrlEncoded
    @POST(USERS + "/login")
    Call<CommonResponse> login(@FieldMap Map<String, String> request);

    @FormUrlEncoded
    @POST(USERS + "/logout")
    Call<CommonResponse> logout(@FieldMap Map<String, String> request);

    @FormUrlEncoded
    @POST(USERS + "/profile")
    Call<CommonResponse> profile(@FieldMap Map<String, String> request); //用户信息修改

    @FormUrlEncoded
    @POST(USERS + "/password")
    Call<CommonResponse> password(@FieldMap Map<String, String> request); //用户修改密码

    @FormUrlEncoded
    @POST(USERS + "/send_reset_password_code")
    Call<CommonResponse> send_reset_password_code(@FieldMap Map<String, String> request); //用户找回密码短信

    @FormUrlEncoded
    @POST(USERS + "/reset_password")
    Call<CommonResponse> reset_password(@FieldMap Map<String, String> request); //用户重制密码

    @FormUrlEncoded
    @POST(USERS + "/change_icon")
    Call<CommonResponse> change_icon(@FieldMap Map<String, String> request); //修改用户头像




}
