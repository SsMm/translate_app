package com.zgy.translate.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.Vibrator;
import android.provider.Settings;
import android.util.Log;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.lang.ref.WeakReference;

/**
 * Created by zhouguangyue on 2017/7/26.
 */

public class ConfigUtil {


    /**
     * Toask
     * */
    public static void showToask(Context context, String content){
        Toast.makeText(context.getApplicationContext(),content,Toast.LENGTH_LONG).show();
    }

    /**progress*/
    public static ProgressDialog showProDialog(Context context, String message){
        WeakReference<Context> contextWeakReference = new WeakReference<Context>(context);
        ProgressDialog progressDialog = new ProgressDialog(contextWeakReference.get());
        WeakReference<ProgressDialog> weakReference = new WeakReference<ProgressDialog>(progressDialog);
        //设置进度条风格，风格为圆形，旋转的
        weakReference.get().setProgressStyle(ProgressDialog.STYLE_SPINNER);
        //设置ProgressDialog 提示信息
        weakReference.get().setMessage(message);
        //设置ProgressDialog 的进度条是否不明确
        weakReference.get().setIndeterminate(false);
        //设置ProgressDialog 是否可以按退回按键取消
        weakReference.get().setCancelable(false);
        return weakReference.get();
    }

    /**alert*/
    public static void showAlertDialog(Context context, String title, String message, final AlertDialogInterface dialogInterface){
        WeakReference<Context> weakReference = new WeakReference<Context>(context);
        AlertDialog.Builder builder = new AlertDialog.Builder(weakReference.get());
        WeakReference<AlertDialog.Builder> builderWeakReference = new WeakReference<AlertDialog.Builder>(builder);
        builderWeakReference.get().setTitle(title);
        builderWeakReference.get().setMessage(message);
        builderWeakReference.get().setCancelable(false);
        builderWeakReference.get().setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(dialogInterface != null){
                    dialogInterface.confirmDialog();
                }
            }
        });
        builderWeakReference.get().setNegativeButton("取消",new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(dialogInterface != null){
                    dialogInterface.cancelDialog();
                }
            }
        });
        builderWeakReference.get().show();
    }

    /**
     * 是否联网
     * @param context
     * @return
     */
    public static boolean isNetWorkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable() && mNetworkInfo.isConnected();
            }
        }
        return false;
    }

    /**
     * 是否存在sd卡
     *
     * @return
     */
    public static boolean isSdcardExist() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }
    /**
     * 获取SD卡路径
     *
     * @return
     */
    public static String getSDCardDirectory() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return Environment.getExternalStorageDirectory().getAbsolutePath();
        } else {
            return Environment.getRootDirectory().getAbsolutePath();
        }
    }

    /**
     * 隐藏键盘
     * */
    public static void hideKeyboard(InputMethodManager inputMethodManager, Activity activity) {
        if (activity.getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (activity.getCurrentFocus() != null)
                inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public static String phoneMsg(Context context){
        String model = Build.MODEL;
        String androidId = Settings.Secure.getString(context.getApplicationContext().getContentResolver()
        , Settings.Secure.ANDROID_ID);

        Log.i("shoujixinx", model + androidId);
        return model + androidId;
    }

    /**
     * 再按一次退出
     * */
    private static long lostCloseTime = 0;
    public static void againExit(Context context){
        WeakReference<Context> reference = new WeakReference<Context>(context);
        if(lostCloseTime == 0){
            showToask(context,"再按一次退出");
            lostCloseTime = System.currentTimeMillis();
        }else {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lostCloseTime < 2000) {
                //((Activity) reference.get()).finish(); //退出此activity
                ((Activity) reference.get()).moveTaskToBack(true); // 不在退出此activity，放到后台隐藏
            } else {
                lostCloseTime = 0;
            }
        }
    }


    /**接口*/
    public interface AlertDialogInterface{
        void cancelDialog();
        void confirmDialog();
    }

}
