package com.zgy.translate.http;


import com.zgy.translate.domains.request.CommonRequest;
import com.zgy.translate.domains.response.CommonResponse;


import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
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
    Call<CommonResponse> send_code(@Field("Phone") String Phone);

    @POST(USERS + "/register")
    Call<CommonResponse> registe(@Body CommonRequest request);

    @POST(USERS + "/login")
    Call<CommonResponse> login(@Body CommonRequest request);

    @POST(USERS + "/logout")
    Call<CommonResponse> logout(@Body CommonRequest request);

    @POST(USERS + "/profile")
    Call<CommonResponse> profile(@Body CommonRequest request); //用户信息修改

    @POST(USERS + "/password")
    Call<CommonResponse> password(@Body CommonRequest request); //用户修改密码

    @POST(USERS + "/send_reset_password_code")
    Call<CommonResponse> send_reset_password_code(@Body CommonRequest request); //用户找回密码短信

    @POST(USERS + "/reset_password")
    Call<CommonResponse> reset_password(@Body CommonRequest request); //用户重制密码

    @POST(USERS + "/change_icon")
    Call<CommonResponse> change_icon(@Body CommonRequest request); //修改用户头像




}
