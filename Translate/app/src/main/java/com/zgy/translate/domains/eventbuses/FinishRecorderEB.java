package com.zgy.translate.domains.eventbuses;

import android.widget.ImageView;

/**
 * Created by zhou on 2017/5/22.
 */

public class FinishRecorderEB {

    public final static String RECORDER_OK = "r_ok"; //录音成功
    public final static String RECORDER_FAIL = "r_fail"; //录音失败
    public final static String RECORDER_NO_TIME = "r_no_time"; //录音时间小于3s
    public final static String PLAYER_OK = "p_ok"; //播放成功
    public final static String PLAYER_FINISH = "p_finish"; //播放完成
    public final static String PLAYER_FAIL = "p_fail"; //播放失败

    private String filePath; //发送录音路径
    private String recorderTag; //录音标识
    private String playerTag; //播放标识
    private int recorderTime; //录音时间
    private ImageView view; //播放控件

    public FinishRecorderEB(String recorderTag, String playerTag){
        this.recorderTag = recorderTag;
        this.playerTag = playerTag;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getRecorderTag() {
        return recorderTag;
    }

    public void setRecorderTag(String recorderTag) {
        this.recorderTag = recorderTag;
    }

    public String getPlayerTag() {
        return playerTag;
    }

    public void setPlayerTag(String playerTag) {
        this.playerTag = playerTag;
    }

    public void setRecorderTime(int recorderTime) {
        this.recorderTime = recorderTime;
    }

    public int getRecorderTime() {
        return recorderTime;
    }

    public void setView(ImageView view) {
        this.view = view;
    }

    public ImageView getView() {
        return view;
    }
}
