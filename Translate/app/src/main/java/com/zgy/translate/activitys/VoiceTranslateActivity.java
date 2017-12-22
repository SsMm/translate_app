package com.zgy.translate.activitys;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.speech.EventListener;
import com.baidu.speech.EventManager;
import com.baidu.speech.EventManagerFactory;
import com.baidu.speech.asr.SpeechConstant;
import com.baidu.tts.client.SpeechError;
import com.baidu.tts.client.SpeechSynthesizer;
import com.baidu.tts.client.SpeechSynthesizerListener;
import com.baidu.tts.client.TtsMode;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.zgy.translate.R;

import com.zgy.translate.adapters.VoiceTranslateAdapter;
import com.zgy.translate.adapters.interfaces.VoiceTranslateAdapterInterface;
import com.zgy.translate.base.BaseActivity;
import com.zgy.translate.domains.RecogResult;
import com.zgy.translate.domains.dtos.VoiceTransDTO;
import com.zgy.translate.domains.response.TransResultResponse;
import com.zgy.translate.global.GlobalConstants;
import com.zgy.translate.global.GlobalInit;
import com.zgy.translate.global.GlobalKey;
import com.zgy.translate.global.GlobalParams;
import com.zgy.translate.http.HttpGet;
import com.zgy.translate.managers.CacheManager;
import com.zgy.translate.managers.inst.CreateGattManager;
import com.zgy.translate.managers.GsonManager;
import com.zgy.translate.managers.inst.inter.CreateGattManagerInterface;
import com.zgy.translate.managers.sing.SpeechAsrStartParamManager;
import com.zgy.translate.managers.sing.TransManager;
import com.zgy.translate.utils.AudioRecordUtil;
import com.zgy.translate.utils.ConfigUtil;
import com.zgy.translate.utils.RedirectUtil;
import com.zgy.translate.utils.StringUtil;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class VoiceTranslateActivity extends BaseActivity implements EventListener, SpeechSynthesizerListener,
        VoiceTranslateAdapterInterface, CreateGattManagerInterface{

    private static final String UTTERANCE_ID = "appolo";
    private static boolean FROM_PHONE_MIC = true; //默认从手机麦克风出

    @BindView(R.id.avt_tv_tranLeft) TextView tv_tranLeft; //翻译左语言
    @BindView(R.id.avt_tv_tranRight) TextView tv_tranRight; //翻译右语言
    @BindView(R.id.avt_rv_tranContent) RecyclerView rv_tran; //显示翻译内容
    @BindView(R.id.avt_iv_voice) ImageView iv_phoneVoic; //从手机入显示，耳机入隐藏


    private EventManager mAsr; //识别
    private SpeechSynthesizer mSpeechSynthesizer; //合成

    private String inputResult = "";
    private AudioManager mAudioManager;

    private FileOutputStream ttsFileOutputStream;
    private BufferedOutputStream ttsBufferedOutputStream;
    private File mediaRecorderPath; //存放蓝牙录音和语音合成蓝牙播放文件地址

    private ScheduledExecutorService executorService;

    private VoiceTranslateAdapter voiceTranslateAdapter;
    private List<VoiceTransDTO> voiceTransDTOList;
    private volatile boolean isPhone; //判断是否从手机入
    private boolean isSpeech = false; //是否在输入录音
    private boolean isLeftLangCN = true; //左翻译语言是中文

    private CreateGattManager createGattManager;
    private BluetoothAdapter mBluetoothAdapter;

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

    }

    /**初始化*/
    private void baseInit(){
        if(!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)){
            ConfigUtil.showToask(this, GlobalConstants.NO_BLE);
            finish();
        }

        mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        if(mBluetoothAdapter == null){
            ConfigUtil.showToask(this, GlobalConstants.NO_BLUETOOTH);
            finish();
        }

        showPermission();

        initSpeech();
        initTTs();

        //与gatt建立联系
        createGattManager = new CreateGattManager(this, this);
        createGattManager.setParams(mBluetoothAdapter).init();


        voiceTransDTOList = new ArrayList<>();
        voiceTranslateAdapter = new VoiceTranslateAdapter(this, voiceTransDTOList, this);
        rv_tran.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        rv_tran.setAdapter(voiceTranslateAdapter);

    }

    /**初始化语音识别*/
    private void initSpeech(){
        mAsr = EventManagerFactory.create(this, "asr");
        mAsr.registerListener(this);
    }

    /**初始化语音合成*/
    private void initTTs(){
        mSpeechSynthesizer = SpeechSynthesizer.getInstance();
        mSpeechSynthesizer.setContext(this);
        mSpeechSynthesizer.setSpeechSynthesizerListener(this);
        mSpeechSynthesizer.setAppId(GlobalKey.TTS_APP_ID);
        mSpeechSynthesizer.setApiKey(GlobalKey.TTS_APP_KEY, GlobalKey.TTS_SECURITY_KEY);
        mSpeechSynthesizer.auth(TtsMode.ONLINE); //在线混合
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEAKER, "0");
        mSpeechSynthesizer.initTts(TtsMode.ONLINE); //初始化在线混合
    }

    @Override
    protected void onResume() {
        super.onResume();
        //获取用户手机输出位置

        if(true){ //默认从手机麦克风出
            FROM_PHONE_MIC = true;
        }else{
            FROM_PHONE_MIC = false;
        }
    }

    /**
     * 语音识别回调
     * */
    @Override
    public void onEvent(String name, String params, byte[] bytes, int offset, int length) {
        switch (name){
            case SpeechConstant.CALLBACK_EVENT_ASR_READY: // 引擎准备就绪，可以开始说话
                if(isPhone){
                    ConfigUtil.showToask(this, "开始讲话。。。");
                }
                break;
            case SpeechConstant.CALLBACK_EVENT_ASR_BEGIN: // 检测到用户的已经开始说话
                Log.i("speech--BEGIN", "开始说话");
                break;
            case SpeechConstant.CALLBACK_EVENT_ASR_END: // 检测到用户的已经停止说话
                if(!isPhone){
                    stopSpeech();
                    ConfigUtil.showToask(this, "停止说话");
                }
                break;
            case SpeechConstant.CALLBACK_EVENT_ASR_PARTIAL: // 临时识别结果, 长语音模式需要从此消息中取出结果
                RecogResult recogResult = RecogResult.parseJson(params);
                Log.i("speech--PARTIAL", params);
                String[] results = recogResult.getResultsRecognition();
                if(recogResult.isFinalResult()){
                    List<String> list = new ArrayList<>();
                    list.addAll(Arrays.asList(results));
                    if(list.size() > 0){
                        inputResult += list.get(0);
                    }
                }
                break;
            case SpeechConstant.CALLBACK_EVENT_ASR_FINISH: // 识别结束， 最终识别结果或可能的错误
                if(!isPhone){
                    Log.i("FINISH后录音结果", inputResult);
                    if(!StringUtil.isEmpty(inputResult)){
                        speechToTransAndSynt(inputResult);
                        inputResult = "";
                    }else{
                        ConfigUtil.showToask(this, "没有检测到输入，请重新输入");
                    }
                }
                break;
            case SpeechConstant.CALLBACK_EVENT_ASR_LONG_SPEECH:
                Log.i("长语音结束", "长语音结束");
                Log.i("结束后录音结果", inputResult);
                if(!StringUtil.isEmpty(inputResult)){
                    speechToTransAndSynt(inputResult);
                    inputResult = "";
                }else{
                    ConfigUtil.showToask(this, "没有检测到输入，请重新输入");
                }
                break;
            case SpeechConstant.CALLBACK_EVENT_ASR_VOLUME:
                Volume vol = parseVolumeJson(params);
                Log.i("音量", vol.volumePercent + "");
                break;
        }
    }

    /**语音识别后自动翻译合成*/
    private void speechToTransAndSynt(String result){
        checkPoolState();
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                String trans = null;
                if(isLeftLangCN){
                    trans = HttpGet.get(TransManager.getInstance()
                            .params(result, GlobalConstants.CH, GlobalConstants.EN)
                            .build());
                }else{
                    trans = HttpGet.get(TransManager.getInstance()
                            .params(result, GlobalConstants.EN, GlobalConstants.CH)
                            .build());
                }
                if(StringUtil.isEmpty(trans)){
                    ConfigUtil.showToask(VoiceTranslateActivity.this, "找不到翻译结果，请重新再试！");
                    stopSpeech();
                    return;
                }
                Log.i("翻译结果", trans);
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
                    Log.i("翻译前文本", src);
                    Log.i("合成文本", dst);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            createSynthesizer(dst);
                        }
                    });
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private Volume parseVolumeJson(String jsonStr) {
        Volume vol = new Volume();
        vol.origalJson = jsonStr;
        try {
            JSONObject json = new JSONObject(jsonStr);
            vol.volumePercent = json.getInt("volume-percent");
            vol.volume = json.getInt("volume");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return vol;
    }

    private class Volume {
        private int volumePercent = -1;
        private int volume = -1;
        private String origalJson;
    }

    /**添加翻译内容*/
    private void addTranContent(String src, String dst){
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

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                voiceTransDTOList.add(dto);
                voiceTranslateAdapter.notifyItemInserted(voiceTransDTOList.size() - 1);
            }
        });

    }

    /**
     *翻译内容点击再次语音合成
     * */
    @Override
    public void goTTS(String dst) {
        createSynthesizer(dst);
    }

    /**
     * 语音合成
     * */
    private void createSynthesizer(String dst){
        if(!isPhone){
            //从耳机入，手机出
            if(FROM_PHONE_MIC){ //从麦克风出
                mSpeechSynthesizer.speak(dst);
            }else{
                //从听筒出
                mSpeechSynthesizer.synthesize(dst, UTTERANCE_ID);
            }
        }else{
            //手机入，耳机出
            mSpeechSynthesizer.synthesize(dst, UTTERANCE_ID);
        }
    }

    /**
     * 语音合成回调
     * */
    @Override
    public void onSynthesizeStart(String utteranceId) {
        //合成过程开始
        if(UTTERANCE_ID.equals(utteranceId)){
            Log.i("合成过程开始", "合成过程开始");
            try {
                mediaRecorderPath = getPathFile(true);
                ttsFileOutputStream = new FileOutputStream(mediaRecorderPath);
                ttsBufferedOutputStream = new BufferedOutputStream(ttsFileOutputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onSynthesizeDataArrived(String utteranceId, byte[] audioData, int progress) {
        //合成数据过程中的回调接口，返回合成数据和进度，分多次回调
        if(UTTERANCE_ID.equals(utteranceId)){
            Log.i("合成数据过程中的回调接口", "合成数据过程中的回调接口");
            try {
                ttsBufferedOutputStream.write(audioData);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onSynthesizeFinish(String utteranceId) {
        //合成正常结束状态
        if(UTTERANCE_ID.equals(utteranceId)){
            close();
            if(!isPhone && !FROM_PHONE_MIC){
                //从手机听筒出

            }
            if(isPhone){
                AudioRecordUtil.startTrack(this, mediaRecorderPath, mAudioManager);
            }
        }

    }

    @Override
    public void onError(String utteranceId, SpeechError speechError) {
        ConfigUtil.showToask(this, "语音合成出错，请重新合成");
    }

    private void close(){
        if(ttsBufferedOutputStream != null){
            try {
                ttsBufferedOutputStream.flush();
                ttsBufferedOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            ttsBufferedOutputStream = null;
        }

        if(ttsFileOutputStream != null){
            try {
                ttsFileOutputStream.close();
                ttsFileOutputStream = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 个人设置
     * */
    @OnClick(R.id.avt_iv_setting) void sett(){
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

    @Override
    public void noProfile() {
        ConfigUtil.showToask(this, "请连接耳机，方能使用翻译功能");
        finish();
    }

    @Override
    public void noRequest() {
        ConfigUtil.showToask(this, "连接蓝牙不是本公司产品，请重新连接");
        finish();
    }

    @Override
    public void gattOrder(String order) {
        if(order.contains("o")){
            Log.i("oooo", "oooo"); //启动
            isPhone = false;
            isSpeech = true;
            mediaRecorderPath = getPathFile(false);
            AudioRecordUtil.startRecord(mediaRecorderPath, this, mAudioManager);
        }else if(order.contains("c")){
            //停止
            if(!isSpeech){
                return;
            }
            isSpeech = false;
            AudioRecordUtil.stopRecord();
            Log.i("ccc", "cccc");
            if(isLeftLangCN){
                toCNSpeech(false);
            }else{
                toENSpeech(false);
            }
        }else if(order.contains("w")){
            Log.i("wwww", "wwww");
        }
    }

    /**
     * 开始录音
     * */
    @OnClick(R.id.avt_iv_voice) void startInput(){
        //从手机入
        isPhone = true;
        if(!isSpeech){
            //开始录音
            isSpeech = true;
            if(isLeftLangCN){
                //左中
                toCNSpeech(true);
            }else{
                //左英
                toENSpeech(true);
            }
        }else{
            //结束录音
            isSpeech = false;
            stopSpeech();
        }
    }


    /**中文输入*/
    private void toCNSpeech(boolean flag){
        String  json;
        if(flag){
            json = new JSONObject(SpeechAsrStartParamManager.getInstance()
                    .createCN()
                    .createVoide()
                    .build()).toString();
            Log.i("cn-voide-josn", json);
        }else{
             json = new JSONObject(SpeechAsrStartParamManager.getInstance()
                    .createCN()
                    .createBlue(mediaRecorderPath.getAbsolutePath())
                    .build()).toString();
            Log.i("cn-blue-josn", json);
        }
        mAsr.send(SpeechConstant.ASR_START, json, null, 0, 0);
    }

    /**英文输入*/
    private void toENSpeech(boolean flag){
        String  json;
        if(flag){
            json = new JSONObject(SpeechAsrStartParamManager.getInstance()
                    .createEN()
                    .createVoide()
                    .build()).toString();
            Log.i("en-voide-josn", json);
        }else{
            json = new JSONObject(SpeechAsrStartParamManager.getInstance()
                    .createEN()
                    .createBlue(mediaRecorderPath.getAbsolutePath())
                    .build()).toString();
            Log.i("en-blue-josn", json);
        }
        mAsr.send(SpeechConstant.ASR_START, json, null, 0, 0);
    }

    private void stopSpeech(){
        mAsr.send(SpeechConstant.ASR_STOP, "{}", null, 0, 0);
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
        if(mAsr != null){
            mAsr.unregisterListener(this);
            mAsr.send(SpeechConstant.ASR_CANCEL, "{}", null, 0, 0);
            mAsr = null;
        }
        if(mSpeechSynthesizer != null){
            mSpeechSynthesizer.setSpeechSynthesizerListener(null);
            mSpeechSynthesizer.release();
            mSpeechSynthesizer = null;
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
        mBluetoothAdapter = null;
    }

    @Override
    public void onSpeechStart(String s) {

    }

    @Override
    public void onSpeechProgressChanged(String s, int i) {

    }

    @Override
    public void onSpeechFinish(String s) {

    }

    private void showPermission(){
        RxPermissions rxPermissions = new RxPermissions(this);

        rxPermissions.request(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                .subscribe(granted -> {
                    if(!granted){
                        ConfigUtil.showToask(this, "请在手机设置中打开相应权限！");
                    }
                });
    }
}
