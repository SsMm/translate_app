package com.zgy.translate.global;

import java.util.concurrent.ExecutorService;


/**
 * Created by zhouguangyue on 2017/11/28.
 */

public class GlobalParams {

    public static ExecutorService bltConnectExecutorService;  //与蓝牙耳机建立连接
    public static ExecutorService bltInputStreamExecutorService;  //监听蓝牙耳机输入信息

    public static String RECORDER_PATH; //蓝牙耳机录音地址
    public static String PLAY_PATH; //蓝牙耳机播放地址


}
