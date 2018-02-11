package com.zgy.translate.activitys;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SynthesizerListener;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.zgy.translate.R;

import com.zgy.translate.adapters.VoiceTranslateAdapter;
import com.zgy.translate.adapters.interfaces.VoiceTranslateAdapterInterface;
import com.zgy.translate.base.BaseActivity;
import com.zgy.translate.domains.dtos.UserInfoDTO;
import com.zgy.translate.domains.dtos.VoiceTransDTO;
import com.zgy.translate.domains.response.TransResultResponse;
import com.zgy.translate.global.GlobalConstants;
import com.zgy.translate.global.GlobalParams;
import com.zgy.translate.http.HttpGet;
import com.zgy.translate.managers.CacheManager;
import com.zgy.translate.managers.UserMessageManager;
import com.zgy.translate.managers.inst.CreateBlueManager;
import com.zgy.translate.managers.inst.CreateGattManager;
import com.zgy.translate.managers.GsonManager;
import com.zgy.translate.managers.inst.inter.CreateGattManagerInterface;
import com.zgy.translate.managers.sing.TransManager;
import com.zgy.translate.utils.ConfigUtil;
import com.zgy.translate.utils.JsonParser;
import com.zgy.translate.utils.RedirectUtil;
import com.zgy.translate.utils.StringUtil;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class VoiceTranslateActivity extends BaseActivity implements VoiceTranslateAdapterInterface,
        CreateGattManagerInterface, View.OnTouchListener, RecognizerListener{

    private static final String UTTERANCE_ID = "appolo";
    private static boolean FROM_PHONE_MIC = true; //默认从手机麦克风出
    private static final String BLUETOOTH_OFF = "off"; //蓝牙关闭
    private static final String A2DP_DISCONNECTED = "dis"; //耳机断开连接
    private static final String A2DP_CONNECTED = "a2dp_con"; //耳机连接成功
    private static final String NO_FIND_DEVICE = "no_find"; //蓝牙没有连接设备
    private static final String NO_REQUEST_DEVICE = "no_requ"; //不是要求设备
    private static final String BLE_CONNECTED = "coned"; //ble连接成功
    private static final String BLE_DISCONNECTED = "ble_dis"; //ble断开连接

    @BindView(R.id.avt_tv_tranLeft) TextView tv_tranLeft; //翻译左语言
    @BindView(R.id.avt_tv_tranRight) TextView tv_tranRight; //翻译右语言
    @BindView(R.id.avt_rv_tranContent) RecyclerView rv_tran; //显示翻译内容
    @BindView(R.id.avt_iv_voice) ImageView iv_phoneVoic; //手机录音显示
    @BindView(R.id.avt_ll_showConState) LinearLayout ll_showConState; //连接状态
    @BindView(R.id.avt_iv_showCon_icon) ImageView iv_showConIcon;
    @BindView(R.id.avt_tv_showConText) TextView tv_showConText;
    @BindView(R.id.avt_vs_netCon) ViewStub vs_unableConn; //无网络
    @BindView(R.id.avt_ll_wlv) LinearLayout ll_showWlv; //显示波浪
    @BindView(R.id.avt_iv_microVolume) ImageView iv_microVolume;
    @BindView(R.id.avt_tv_showInputType) TextView tv_showInputType;
    @BindView(R.id.avt_ll_noFindDevice) LinearLayout ll_noFindDevice; //没有找到蓝牙设备
    @BindView(R.id.avt_tv_noFindDeviceText) TextView tv_noFindDeviceText; //
    @BindView(R.id.avt_iv_noFindDeviceIcon) ImageView iv_noFindDeviceIcon;
    @BindView(R.id.avt_iv_guide) ImageView iv_guide; //引导用户使用



    private SpeechRecognizer mIat; //科大讯飞识别
    private com.iflytek.cloud.SpeechSynthesizer mTts; //科大讯飞合成

    // 用HashMap存储听写结果
    private HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();
    private AudioManager mAudioManager;

    private ScheduledExecutorService executorService;

    private volatile VoiceTranslateAdapter voiceTranslateAdapter;
    private List<VoiceTransDTO> voiceTransDTOList;
    private volatile boolean isPhone = true; //判断是否从手机入
    private boolean isSpeech = false; //是否在输入录音
    private boolean isLeftLangCN = true; //左翻译语言是中文

    private CreateGattManager createGattManager;
    private CreateBlueManager createBlueManager;
    private BluetoothAdapter mBluetoothAdapter;
    private BroadcastReceiver scoReceiver;
    private volatile AnimationDrawable animationDrawable;
    private volatile ImageView currPlayImage;

    private volatile boolean isClick = false; //false是录完音自动播放，true是点击在此播放
    private boolean isNet = true; //网络连接情况
    private boolean isBluetoothConned = false; //蓝牙连接
    private int i = 0; //引导点击次数

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_translate);
        ButterKnife.bind(this);
        super.init();
    }

    @Override
    public void initView() {
        baseInit();
    }

    @Override
    public void initEvent() {
    }

    @Override
    public void initData() {
        //String dir = getApplicationInfo().nativeLibraryDir;
        //Log.w("dir------", dir);
    }

    /**
     * 蓝牙控制
     * */
    @Override
    public void blueOff() {
        deviceConState(BLUETOOTH_OFF);
    }

    @Override
    public void disConnected() {
        //耳机蓝牙断开连接
        if(createGattManager != null){
            createGattManager.disconnectGatt();
        }
        if(createBlueManager != null){
            createBlueManager.closeSocket();
        }

        isBluetoothConned = false;
        deviceConState(A2DP_DISCONNECTED);
        showVolmn(false);
    }

    @Override
    public void connected() {
        //耳机蓝牙连接成功
        deviceConState(A2DP_CONNECTED);
        isBluetoothConned = true;
        if(createGattManager != null){
            createGattManager.nextGetProfile();
        }

        if(createBlueManager != null){
            createBlueManager.nextGetProfile();
        }

    }

    @Override
    public void disNetConnected() {
        checkNetState(false);
    }

    @Override
    public void netConnected() {
        checkNetState(true);
    }

    /**初始化*/
    private void baseInit(){
        /*if(!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)){
            ConfigUtil.showToask(this, GlobalConstants.NO_BLE);
            finish();
        }*/

        mAudioManager = (AudioManager) getApplicationContext().getSystemService(AUDIO_SERVICE);

        final BluetoothManager bluetoothManager = (BluetoothManager)getApplicationContext().getSystemService(BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        if(mBluetoothAdapter == null){
            ConfigUtil.showToask(this, GlobalConstants.NO_BLUETOOTH);
            finish();
        }

        showPermission();

        initSpeech();
        initTTs();

        //与gatt建立联系
        //createGattManager = new CreateGattManager(this, this);
        //createGattManager.setParams(mBluetoothAdapter).init();

        createBlueManager = new CreateBlueManager(this, this);
        createBlueManager.init();

        voiceTransDTOList = new ArrayList<>();
        voiceTranslateAdapter = new VoiceTranslateAdapter(this, voiceTransDTOList, this);
        rv_tran.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        rv_tran.setAdapter(voiceTranslateAdapter);

        iv_phoneVoic.setOnTouchListener(this);

    }

    /**初始化语音识别*/
    private void initSpeech(){
        mIat = SpeechRecognizer.createRecognizer(this, initListener);
    }

    private InitListener initListener = new InitListener() {
        @Override
        public void onInit(int i) {
            //Log.i("initlistercoid--", i +"");
            if(i != ErrorCode.SUCCESS){
                ConfigUtil.showToask(VoiceTranslateActivity.this, "初始化失败，错误码：" + i );
            }
        }
    };

    private void initTTs(){
        mTts = com.iflytek.cloud.SpeechSynthesizer.createSynthesizer(this, initListener);
        mTts.setParameter(com.iflytek.cloud.SpeechConstant.PARAMS, null);
        mTts.setParameter(com.iflytek.cloud.SpeechConstant.ENGINE_TYPE, com.iflytek.cloud.SpeechConstant.TYPE_CLOUD);
        // 设置在线合成发音人
        mTts.setParameter(com.iflytek.cloud.SpeechConstant.VOICE_NAME, "xiaoyan");
        //设置合成语速
        mTts.setParameter(com.iflytek.cloud.SpeechConstant.SPEED, "40");
        //设置合成音调
        mTts.setParameter(com.iflytek.cloud.SpeechConstant.PITCH, "40");
        //设置合成音量
        //mTts.setParameter(com.iflytek.cloud.SpeechConstant.VOLUME, "50");
        // 设置播放合成音频打断音乐播放，默认为true
        mTts.setParameter(com.iflytek.cloud.SpeechConstant.KEY_REQUEST_FOCUS, "true");
    }

    @Override
    protected void onResume() {
        super.onResume();
        //获取用户手机输出位置
        UserInfoDTO userInfoDTO;
        if(GlobalParams.userInfoDTO != null){
           userInfoDTO = GlobalParams.userInfoDTO;
        }else{
            userInfoDTO = UserMessageManager.getUserInfo(this);
        }

        //默认从手机麦克风出
        FROM_PHONE_MIC = userInfoDTO.isMic();

        if(!ConfigUtil.isNetWorkConnected(this)){
            checkNetState(false);
        }else{
            checkNetState(true);
        }

        if(!mBluetoothAdapter.isEnabled()){
            deviceConState(BLUETOOTH_OFF);
        }

        if(UserMessageManager.readLoginUser(this) == null){
            iv_guide.setBackground(getResources().getDrawable(R.mipmap.first_1));
            iv_guide.setVisibility(View.VISIBLE);
            iv_guide.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    i++;
                    if(i != 2){
                        iv_guide.setBackground(getResources().getDrawable(R.mipmap.first_2));
                    }else {
                        iv_guide.setVisibility(View.GONE);
                        UserMessageManager.saveLoginUser(VoiceTranslateActivity.this, "1");
                        i = 0;
                    }
                }
            });
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mAudioManager != null){
            mAudioManager.setMode(AudioManager.MODE_NORMAL);
        }
    }

    /**
     * 科大讯飞识别
     * */
    @Override
    public void onVolumeChanged(int percent, byte[] bytes) {
        if(percent <= 5){
            iv_microVolume.setImageResource(R.drawable.microphone1);
        }else if(percent > 5 && percent <= 10){
            iv_microVolume.setImageResource(R.drawable.microphone2);
        }else if(percent > 10 && percent <= 15){
            iv_microVolume.setImageResource(R.drawable.microphone3);
        }else if(percent > 15 && percent <= 20){
            iv_microVolume.setImageResource(R.drawable.microphone4);
        }else{
            iv_microVolume.setImageResource(R.drawable.microphone5);
        }
    }

    @Override
    public void onBeginOfSpeech() {
        //ConfigUtil.showToask(VoiceTranslateActivity.this, "开始讲话");
    }

    @Override
    public void onEndOfSpeech() {
        showVolmn(false);
        isSpeech = false;
        if(!isPhone && mAudioManager.isBluetoothScoOn()){
            mAudioManager.stopBluetoothSco();
            mAudioManager.setBluetoothScoOn(false);
        }
        if(scoReceiver != null){
            unregisterReceiver(scoReceiver);
            scoReceiver = null;
        }
        stopSpeech();

        //ConfigUtil.showToask(VoiceTranslateActivity.this, "停止说话");
    }

    @Override
    public void onResult(RecognizerResult recognizerResult, boolean b) {
        //Log.i("rescoidjs--", recognizerResult.getResultString());
        printResult(recognizerResult);
    }

    @Override
    public void onError(com.iflytek.cloud.SpeechError speechError) {
        stopSpeech();
        if(speechError != null){
            if(speechError.getPlainDescription(true).contains("20006")){
                if(!isPhone){
                    mAudioManager.stopBluetoothSco();
                    mAudioManager.setBluetoothScoOn(false);
                }
                unregisterSCO();
                if(!isLeftLangCN){
                    toCNSpeech(true);
                }else{
                    toENSpeech(true);
                }
            }else{
                isSpeech = false;
                showVolmn(false);
                ConfigUtil.showToask(this, speechError.getPlainDescription(true));
            }

        }
    }

    @Override
    public void onEvent(int i, int i1, int i2, Bundle bundle) {

    }

    private void printResult(RecognizerResult results) {
        String text = JsonParser.parseIatResult(results.getResultString());

        String sn = null;
        JSONObject resultJson = null;
        // 读取json结果中的sn字段
        try {
            resultJson = new JSONObject(results.getResultString());
            sn = resultJson.optString("sn");

            mIatResults.put(sn, text);

            if(!resultJson.getBoolean("ls")){
                return;
            }

            StringBuilder builder = new StringBuilder();
            for (String key : mIatResults.keySet()) {
                builder.append(mIatResults.get(key));
            }
            speechToTransAndSynt(builder.toString());
            if(mIatResults != null){
                mIatResults.clear();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**语音识别后自动翻译合成*/
    private void speechToTransAndSynt(String result){
        super.progressDialog.show();
        checkPoolState();
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                String trans = null;
                if(isPhone){
                    if(isLeftLangCN){
                        trans = HttpGet.get(TransManager.getInstance()
                                .params(result, GlobalConstants.CH, GlobalConstants.EN)
                                .build());
                    }else{
                        trans = HttpGet.get(TransManager.getInstance()
                                .params(result, GlobalConstants.EN, GlobalConstants.CH)
                                .build());
                    }
                }else{
                    if(!isLeftLangCN){
                        trans = HttpGet.get(TransManager.getInstance()
                                .params(result, GlobalConstants.CH, GlobalConstants.EN)
                                .build());
                    }else{
                        trans = HttpGet.get(TransManager.getInstance()
                                .params(result, GlobalConstants.EN, GlobalConstants.CH)
                                .build());
                    }
                }

                if(StringUtil.isEmpty(trans)){
                    ConfigUtil.showToask(VoiceTranslateActivity.this, "找不到翻译结果，请重新再试！");
                    stopSpeech();
                    VoiceTranslateActivity.super.progressDialog.dismiss();
                    return;
                }
                //翻译后文本
                String dstT = GsonManager.getInstance()
                        .fromJson(trans, TransResultResponse.class)
                        .getTrans_result().get(0).getDst();
                //翻译前文本
                String srcT = GsonManager.getInstance().fromJson(trans, TransResultResponse.class)
                        .getTrans_result().get(0).getSrc();

                String src;
                String dst;
                try {
                    src = URLDecoder.decode(srcT, "utf-8");
                    dst = URLDecoder.decode(dstT, "utf-8");
                    addTranContent(src, dst);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            isClick = false;
                            createSynthesizer(dst);
                        }
                    });
                } catch (UnsupportedEncodingException e) {
                    VoiceTranslateActivity.super.progressDialog.dismiss();
                    e.printStackTrace();
                }
            }
        });
    }


    /**添加翻译内容*/
    private void addTranContent(String src, String dst){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                VoiceTransDTO dto = new VoiceTransDTO();
                if(isPhone){
                    //从耳机
                    dto.setLagType(VoiceTranslateAdapter.FROM_PHONE);
                }else{
                    //从蓝牙
                    dto.setLagType(VoiceTranslateAdapter.FROM_BLUE);
                }
                dto.setLanSrc(src);
                dto.setLanDst(dst);
                voiceTransDTOList.add(dto);
                voiceTranslateAdapter.notifyItemInserted(voiceTransDTOList.size() - 1);
                rv_tran.scrollToPosition(voiceTransDTOList.size() - 1);
            }
        });

    }

    /**
     *翻译内容点击再次语音合成
     * */
    @Override
    public void goTTS(String dst, ImageView imageView) {
        if(!isNet){
            ConfigUtil.showToask(this, "网络异常，功能无法使用");
            return;
        }
        isClick = true;
        stopAni();
        currPlayImage = imageView;
        createSynthesizer(dst);
    }

    /**
     * 语音合成
     * */
    private synchronized void createSynthesizer(String dst){
        super.progressDialog.dismiss();
        if(!isPhone){
            //从耳机入，手机出
            if(FROM_PHONE_MIC){ //从麦克风出
                mAudioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
                /*mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC),
                        AudioManager.FX_KEY_CLICK);*/
                mAudioManager.setSpeakerphoneOn(true);
                mTts.setParameter(com.iflytek.cloud.SpeechConstant.STREAM_TYPE, "3");
                startTTsCode(dst);
            }else{
                //从听筒出
                mAudioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
                mAudioManager.setSpeakerphoneOn(false);
                mTts.setParameter(com.iflytek.cloud.SpeechConstant.STREAM_TYPE, "0");
                startTTsCode(dst);
            }
        }else{
            //手机入，耳机出
            mAudioManager.setMode(AudioManager.MODE_NORMAL);
            mAudioManager.setSpeakerphoneOn(false);
            mTts.setParameter(com.iflytek.cloud.SpeechConstant.STREAM_TYPE, "3");
            startTTsCode(dst);

        }
    }

    private void startTTsCode(String text){
        int code = mTts.startSpeaking(text, mTtsListener);
        if(code != ErrorCode.SUCCESS){
            ConfigUtil.showToask(this, "合成失败，错误码：" + code);
        }
    }

    /**
     *科大讯飞合成回调
     **/
    private SynthesizerListener mTtsListener = new SynthesizerListener() {
        @Override
        public void onSpeakBegin() {
            showPlayAni();
        }

        @Override
        public void onBufferProgress(int i, int i1, int i2, String s) {

        }

        @Override
        public void onSpeakPaused() {

        }

        @Override
        public void onSpeakResumed() {

        }

        @Override
        public void onSpeakProgress(int i, int i1, int i2) {

        }

        @Override
        public void onCompleted(com.iflytek.cloud.SpeechError speechError) {
            stopAni();
            if(speechError != null){
                ConfigUtil.showToask(VoiceTranslateActivity.this, speechError.getPlainDescription(true));
            }
        }

        @Override
        public void onEvent(int i, int i1, int i2, Bundle bundle) {

        }
    };


    /**
     * 个人设置
     * */
    @OnClick(R.id.avt_iv_setting) void sett(){
        stopSpeech();
        stopAni();
        showVolmn(false);
        RedirectUtil.redirect(this, MySettingActivity.class);
    }

    /**
     * 中英文互换
     * */
    @OnClick(R.id.avt_ll_tranTitle) void tranLang(){
        if(!isLeftLangCN){
            isLeftLangCN = true;
            tv_tranLeft.setText(getResources().getString(R.string.tran_cn));
            tv_tranRight.setText(getResources().getString(R.string.tran_zn));
        }else{
            isLeftLangCN = false;
            tv_tranLeft.setText(getResources().getString(R.string.tran_zn));
            tv_tranRight.setText(getResources().getString(R.string.tran_cn));
        }
    }

    /**
     * 连接情况
     * */
    @Override
    public void bluetoothOff() {
        deviceConState(BLUETOOTH_OFF);
    }

    @Override
    public void noProfile() {
        //ConfigUtil.showToask(this, "请连接耳机，方能使用翻译功能");
        deviceConState(NO_FIND_DEVICE);
        if(isBluetoothConned){
            if(createGattManager != null){
                createGattManager.nextGetProfile();
            }

            if(createBlueManager != null){
                createBlueManager.nextGetProfile();
            }

        }
    }

    @Override
    public void noRequest() {
        //ConfigUtil.showToask(this, "连接蓝牙不是本公司产品，请重新连接");
        deviceConState(NO_REQUEST_DEVICE);
    }

    @Override
    public void conState(boolean state) {
        if(state){
            deviceConState(BLE_CONNECTED);
            showConState(true);
        }else{
            deviceConState(BLE_DISCONNECTED);
        }
    }

    @Override
    public void gattOrder(String order) {
        if(!isNet){
            ConfigUtil.showToask(this, "网络异常，功能无法使用");
            return;
        }
        if(isSpeech && isPhone){
            ConfigUtil.showToask(this, "请从手机控制");
            return;
        }
        if(isSpeech){
            ConfigUtil.showToask(this, "已启动语音功能");
            return;
        }
        if(order.contains("o") || order.contains("c")){
            //启动
            isPhone = false;
            isSpeech = true;
            mAudioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
            mAudioManager.startBluetoothSco();
            initSCOReceiver();
        }
    }

    private void initSCOReceiver(){
        scoReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int state = intent.getIntExtra(AudioManager.EXTRA_SCO_AUDIO_STATE, -1);
                if(AudioManager.SCO_AUDIO_STATE_CONNECTED == state){
                    mAudioManager.setBluetoothScoOn(true);
                    //mAudioManager.setMicrophoneMute(false);
                    if(!isLeftLangCN){
                        toCNSpeech(true);
                    }else{
                        toENSpeech(true);
                    }
                    //unregisterReceiver(this);
                }else if(AudioManager.SCO_AUDIO_STATE_DISCONNECTED == state){
                    isSpeech = false;
                    showVolmn(false);
                    stopSpeech();
                    ConfigUtil.showToask(VoiceTranslateActivity.this, "打开耳机失败，请尝试重新连接耳机蓝牙！");
                    mAudioManager.stopBluetoothSco();
                    mAudioManager.setBluetoothScoOn(false);
                    //unregisterReceiver(this);
                    if(scoReceiver != null){
                        unregisterReceiver(scoReceiver);
                        scoReceiver = null;
                    }
                }
            }
        };
        registerReceiver(scoReceiver, new IntentFilter(AudioManager.ACTION_SCO_AUDIO_STATE_UPDATED));
    }

    /**
     * 开始录音
     * */
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if(!isNet){
            ConfigUtil.showToask(this, "网络异常，功能无法使用");
            return false;
        }
        if(isSpeech && !isPhone){
            ConfigUtil.showToask(this, "请从耳机控制,可能耳机录音没有关闭，请再次按下耳机控制键");
            return false;
        }
        switch (motionEvent.getAction()){
            case MotionEvent.ACTION_DOWN:
                //开始录音
                isPhone = true;
                isSpeech = true;
                /*mAudioManager.stopBluetoothSco();
                mAudioManager.setBluetoothScoOn(false);
                mAudioManager.setMode(AudioManager.MODE_NORMAL);
                mAudioManager.setMicrophoneMute(true);*/
                if(isLeftLangCN){
                    //左中
                    toCNSpeech(true);
                }else{
                    //左英
                    toENSpeech(true);
                }
                break;
            case MotionEvent.ACTION_UP:
                //结束录音
                isSpeech = false;
                showVolmn(false);
                stopSpeech();
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            default:
                isSpeech = false;
                showVolmn(false);
                stopSpeech();
                break;
        }
        return true;
    }

    /**中文输入*/
    private void toCNSpeech(boolean flag){
       setIatParam();
        // 设置语言
        mIat.setParameter(com.iflytek.cloud.SpeechConstant.LANGUAGE, "zh_cn");
        // 设置语言区域
        mIat.setParameter(com.iflytek.cloud.SpeechConstant.ACCENT, "mandarin");
        startIatResult();
    }

    /**英文输入*/
    private void toENSpeech(boolean flag){
        setIatParam();
        mIat.setParameter(com.iflytek.cloud.SpeechConstant.LANGUAGE, "en_us");
        mIat.setParameter(com.iflytek.cloud.SpeechConstant.ACCENT, null);
        startIatResult();
    }

    private void setIatParam(){
        mIat.setParameter(com.iflytek.cloud.SpeechConstant.PARAMS, null);
        // 设置听写引擎
        mIat.setParameter(com.iflytek.cloud.SpeechConstant.ENGINE_TYPE, com.iflytek.cloud.SpeechConstant.TYPE_CLOUD);
        // 设置返回结果格式
        mIat.setParameter(com.iflytek.cloud.SpeechConstant.RESULT_TYPE, "json");

        // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
        mIat.setParameter(com.iflytek.cloud.SpeechConstant.VAD_BOS,  "4000");

        // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
        mIat.setParameter(com.iflytek.cloud.SpeechConstant.VAD_EOS, "1000");

        // 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
        mIat.setParameter(com.iflytek.cloud.SpeechConstant.ASR_PTT,  "1");
    }

    private void startIatResult(){
        int ret = mIat.startListening(this);
        if(ret != ErrorCode.SUCCESS){
            ConfigUtil.showToask(this, "听写失败：" + ret);
        }else {
            showVolmn(true);
            stopAni();
        }
    }

    private void stopSpeech(){
       if(mIat.isListening()){
           mIat.stopListening();
       }
    }

    /**
     * 查看是否有线程池存在执行任务，有就关闭，建立新线程池
     * */
    private void checkPoolState(){
        if (executorService != null){
            executorService.shutdown();
            executorService = null;
        }
        executorService = Executors.newSingleThreadScheduledExecutor();
    }

    @Override
    public void onBackPressed() {
        System.gc();
        ConfigUtil.againExit(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mIat != null){
            if(mIat.isListening()){
                mIat.cancel();
            }
            mIat.destroy();
            mIat = null;
        }
        if(mTts != null){
            mTts.stopSpeaking();
            mTts.destroy();
            mTts = null;
        }
        if (executorService != null){
            executorService.shutdown();
            executorService = null;
        }
        if(voiceTransDTOList != null){
            voiceTransDTOList.clear();
            voiceTransDTOList = null;
            voiceTranslateAdapter = null;
            rv_tran.setAdapter(null);
            rv_tran.setLayoutManager(null);
        }
        if(createGattManager != null){
            createGattManager.onMyDestroy();
            createGattManager = null;
        }
        if(createBlueManager != null){
            createBlueManager.onMyDestroy();
            createBlueManager = null;
        }
        if(mAudioManager != null){
            mAudioManager.setMode(AudioManager.MODE_NORMAL);
            mAudioManager = null;
        }
        if(scoReceiver != null){
            unregisterReceiver(scoReceiver);
            scoReceiver = null;
        }
        animationDrawable = null;
        mBluetoothAdapter = null;
        currPlayImage = null;
    }

    /**
     * 网络连接状态
     * */
    private void checkNetState(boolean state){
        if(state){
            isNet = true;
            vs_unableConn.setVisibility(View.GONE);
        }else{
            isNet = false;
            vs_unableConn.setVisibility(View.VISIBLE);
            stopAni();
        }
    }

    /**
     * 蓝牙设备连接情况
     * */
    private void deviceConState(String state){
        switch (state){
            case BLUETOOTH_OFF:
                isBluetoothConned = false;
                showVolmn(false);
                stopSpeech();
                stopAni();
                GlobalParams.BlUETOOTH_DEVICE = null;
                checkDisOrConn(true, "请先开启系统蓝牙功能\n并连接TOPPERS E1");
                break;
            case A2DP_DISCONNECTED:
                GlobalParams.BlUETOOTH_DEVICE = null;
                checkDisOrConn(true, "请连接耳机，方能使用翻译功能");
                break;
            case A2DP_CONNECTED:
                checkDisOrConn(true, "耳机连接成功，等待连接翻译功能...");
                break;
            case NO_FIND_DEVICE:
                checkDisOrConn(true, "请检查耳机连接是否正确，方能使用翻译功能");
                break;
            case NO_REQUEST_DEVICE:
                checkDisOrConn(true, "连接蓝牙不是本公司产品，请重新连接");
                break;
            case BLE_CONNECTED:
                checkDisOrConn(false, null);
                break;
            case BLE_DISCONNECTED:
                showVolmn(false);
                stopAni();
                //ConfigUtil.showToask(this, "连接失败");
                //checkDisOrConn(true, "断开连接，请等待连接...");
                if(isSpeech){
                    stopSpeech();
                }
                break;
        }
    }

    private void checkDisOrConn(boolean flag, String s){
        if(flag){
            if(s.contains("TOPPERS E1")){
                iv_noFindDeviceIcon.setImageResource(R.mipmap.blue_off);
            }else{
                iv_noFindDeviceIcon.setImageResource(R.mipmap.no_find_device);
            }
            ll_noFindDevice.setVisibility(View.VISIBLE);
            tv_noFindDeviceText.setText(s);
            showOrHide(true);
        }else{
            ll_noFindDevice.setVisibility(View.GONE);
            showOrHide(false);
        }
    }

    private void showOrHide(boolean con){
        if(con){
            rv_tran.setVisibility(View.GONE);
            iv_phoneVoic.setVisibility(View.GONE);
        }else{
            rv_tran.setVisibility(View.VISIBLE);
            iv_phoneVoic.setVisibility(View.VISIBLE);
        }
    }

    private void showConState(boolean sta){
        if(sta){
            ll_showConState.setVisibility(View.VISIBLE);
            iv_showConIcon.setVisibility(View.VISIBLE);
            tv_showConText.setVisibility(View.VISIBLE);
            tv_showConText.setText("连接成功");
            checkPoolState();
            executorService.schedule(new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ll_showConState.setVisibility(View.GONE);
                        }
                    });
                }
            }, 2000, TimeUnit.MILLISECONDS);
        }

    }


    private void showPlayAni(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(!isClick){
                    stopAni();
                    if(voiceTranslateAdapter.getCurrPlayImage() != null){
                        currPlayImage = voiceTranslateAdapter.getCurrPlayImage();
                    }
                }
                if(currPlayImage == null){
                    return;
                }

                currPlayImage.setImageResource(R.drawable.tts_voice_playing);
                animationDrawable = (AnimationDrawable) currPlayImage.getDrawable();
                animationDrawable.start();
            }
        });

    }

    private void stopAni(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if(animationDrawable != null && animationDrawable.isRunning()){
                    animationDrawable.stop();
                    if(mTts.isSpeaking()){
                        mTts.stopSpeaking();
                    }
                    if(currPlayImage != null){
                        currPlayImage.setImageResource(R.drawable.tts_voice_playing3);
                    }
                }
            }
        });

    }

    private void showPermission(){
        RxPermissions rxPermissions = new RxPermissions(this);

        rxPermissions.request(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.READ_PHONE_STATE)
                .subscribe(granted -> {
                    if(!granted){
                        ConfigUtil.showToask(this, "请在手机设置中打开相应权限！");
                    }
                });
    }

    private void showVolmn(boolean flag){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(flag){
                    iv_microVolume.setImageResource(R.drawable.microphone1);
                    ll_showWlv.setVisibility(View.VISIBLE);
                    if(isPhone){
                        if(isLeftLangCN){
                            tv_showInputType.setText("中文输入...");
                        }else{
                            tv_showInputType.setText("English input...");
                        }
                    }else{
                        if(!isLeftLangCN){
                            tv_showInputType.setText("中文输入...");
                        }else{
                            tv_showInputType.setText("English input...");
                        }
                    }
                    tv_showInputType.setVisibility(View.VISIBLE);
                }else{
                    tv_showInputType.setVisibility(View.GONE);
                    ll_showWlv.setVisibility(View.GONE);
                }
            }
        });
    }

    private void unregisterSCO(){
        if(scoReceiver != null){
            unregisterReceiver(scoReceiver);
            scoReceiver = null;
        }
    }

    private File getPathFile(boolean flag){
        File ttsFile;
        if(flag){ //true是蓝牙播放 false是从蓝牙录音
            ttsFile = new File(CacheManager.createTmpDir(this, GlobalParams.FILE_NAME), GlobalParams.PLAY_PATH);
            Log.i("ttfile", ttsFile.getAbsolutePath());
        }else {
            ttsFile = new File(CacheManager.createTmpDir(this, GlobalParams.FILE_NAME), GlobalParams.RECORDER_PATH);
            Log.i("ttfile", ttsFile.getAbsolutePath());
        }

        //ttsFile = new File(CacheManager.createTmpDir(this, GlobalParams.FILE_NAME), GlobalParams.DEMO_PATH);
        //Log.i("ttfile", ttsFile.getAbsolutePath());
        if(ttsFile.exists()){
            ttsFile.delete();
        }

        try {
            ttsFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ttsFile;
    }
}
