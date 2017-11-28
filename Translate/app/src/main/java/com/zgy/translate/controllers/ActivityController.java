package com.zgy.translate.controllers;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhou on 2017/4/27.
 */

public class ActivityController {


    public static List<Activity> activityList = new ArrayList<>();

    public static List<Activity> getActivityList() {
        return activityList;
    }

    /**
     * 添加
     * */
    public static void addActivity(Activity activity){
        activityList.add(activity);
    }

    /**
     * 移除
     * */
    public static void removeActivity(Activity activity){
        activityList.remove(activity);
    }

    /**
     * 清空
     * */
    public static void finishActivity(){
        for (Activity activity : activityList){
            if(!activity.isFinishing()){
                activity.finish();
            }
        }
    }

}
