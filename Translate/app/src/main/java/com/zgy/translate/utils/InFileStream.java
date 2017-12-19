package com.zgy.translate.utils;

import android.app.Activity;
import android.util.Log;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by fujiayi on 2017/6/20.
 */

public class InFileStream {

    private static Activity context;

    private static final String TAG = "InFileStream";

    public static void setContext(Activity context){
        InFileStream.context = context;
    }
    private static InputStream inputStream;

    public static void setInputStream(String path){
        InputStream is = null;
        try {
            is = new FileInputStream(new File(path));
            Log.i(TAG,"create input stream ok" + is.available());
        } catch (Exception e) {
            e.printStackTrace();
        }
        inputStream = is;
    }

    public static InputStream create16kStream(){
        return inputStream;
    }
}