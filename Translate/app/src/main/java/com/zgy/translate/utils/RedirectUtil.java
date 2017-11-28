package com.zgy.translate.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import java.lang.ref.WeakReference;

/**
 * Created by zhouguangyue on 2017/8/9.
 */

public class RedirectUtil {

    private static Intent redirectIntent;

    public static void redirect(Context context, Class<?> targetActivity) {
        redirect(context, targetActivity, 0, null);
    }

    public static void redirect(Context context, Class<?> targetActivity, boolean isClearTop) {
        if (isClearTop) {
            redirect(context, targetActivity, Intent.FLAG_ACTIVITY_CLEAR_TOP, null);
        } else {
            redirect(context, targetActivity, 0, null);
        }
    }

    public static void redirect(Context context, Class<?> targetActivity, int flags) {
        redirect(context, targetActivity, flags, null);
    }

    public static void redirect(Context context, Class<?> targetActivity, Bundle extras) {
        redirect(context, targetActivity, 0, extras);
    }

    private static void redirect(Context context, Class<?> targetActivityClass, int flags, Bundle extras) {
        WeakReference<Context> contextWeakReference = new WeakReference<Context>(context);
        Context mContext = contextWeakReference.get();
        redirectIntent = new Intent();
        redirectIntent.setClass(mContext, targetActivityClass);
        if (flags != 0) {
            redirectIntent.setFlags(flags);
        }
        if (null != extras) {
            redirectIntent.putExtras(extras);
        }
        mContext.startActivity(redirectIntent);
        if (mContext instanceof Activity) {
            Activity act = (Activity) mContext;
            // act.overridePendingTransition(R.anim.push_left_in, R.anim.push_right_out);
        }
    }

}
