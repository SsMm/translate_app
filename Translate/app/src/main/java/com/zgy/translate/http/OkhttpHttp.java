package com.zgy.translate.http;


import android.content.Context;
import android.text.TextUtils;
import android.util.Log;


import com.zgy.translate.domains.dtos.UserInfoDTO;
import com.zgy.translate.managers.CacheManager;
import com.zgy.translate.managers.UserMessageManager;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Authenticator;
import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import okhttp3.logging.HttpLoggingInterceptor;


/**
 * Created by zhou on 2017/4/27.
 */

public class OkhttpHttp {

    private static OkHttpClient client;
    private static File cacheFile;
    private static Interceptor interceptor;

    public static OkHttpClient getOkHttpClient(Context context){
        if(client == null){
            synchronized (OkhttpHttp.class){
                if(client == null){
                    client = new OkHttpClient.Builder()
                            .addInterceptor(getHttpLoggingInterceptor())
                            .addNetworkInterceptor(getInterceptor(context))
                            .connectTimeout(20000, TimeUnit.SECONDS)
                            .cache(getCache(context))
                            .build();
                }
            }
        }
        return client;
    }

    private static HttpLoggingInterceptor getHttpLoggingInterceptor(){
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                //Log.i("message",message);
            }
        });
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        return httpLoggingInterceptor;
    }

    private static Interceptor getInterceptor(Context context){
            if(interceptor == null){
                interceptor = new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        String token = "";
                        UserInfoDTO userInfoDTO = UserMessageManager.getUserInfo(context);
                        if(userInfoDTO != null && userInfoDTO.getAppKey() != null){
                            token = userInfoDTO.getAppKey();
                        }
                        Log.i("token--", token);
                        Request request = chain.request();
                        if(TextUtils.isEmpty(token)){
                            return chain.proceed(request);
                        }
                        Request finalRequest = request.newBuilder()
                                .header("AppKey", token)
                                .cacheControl(getCacheControl())
                                .build();
                        return chain.proceed(finalRequest);
                    }
                };
            }

        return interceptor;
    }

    private static CacheControl getCacheControl(){
        CacheControl.Builder builder = new CacheControl.Builder();
        builder.noCache();
        builder.noStore();
        return builder.build();
    }

    private static Cache getCache(Context context){
        cacheFile = new File(CacheManager.getDiskCacheDir(context),"net");
        if(!cacheFile.exists()){
            cacheFile.mkdirs();
        }
        return new Cache(cacheFile,1024);
    }

    private static Authenticator getAuthenticator(){
         return new Authenticator() {
            @Override
            public Request authenticate(Route route, Response response) throws IOException {

                return null;
            }
        };
    }

    public static OkHttpClient getClient(){
        if(client != null){
            return client;
        }else{
            return null;
        }
    }

    public static void clearRequest(){
        if(cacheFile != null && cacheFile.exists()){
            cacheFile.delete();
        }

    }

    public static void onMyDestroy(){
        interceptor = null;
    }

}
