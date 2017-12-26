package com.zgy.translate.activitys;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.imnjh.imagepicker.activity.PhotoPickerActivity;
import com.zgy.translate.R;
import com.zgy.translate.base.BaseActivity;
import com.zgy.translate.global.GlobalConstants;
import com.zgy.translate.managers.CacheManager;
import com.zgy.translate.managers.GlideImageManager;
import com.zgy.translate.managers.MultiMediaManager;
import com.zgy.translate.managers.inst.ImageInst;
import com.zgy.translate.utils.ActionSheet;
import com.zgy.translate.utils.ConfigUtil;
import com.zgy.translate.utils.OptItem;
import com.zgy.translate.utils.RedirectUtil;
import com.zgy.translate.widget.CommonBar;
import com.zgy.translate.widget.CommonNav;
import com.zgy.translate.widget.CustomDatePicker;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class MyMsgActivity extends BaseActivity implements CommonBar.CommonBarInterface, ConfigUtil.AlertDialogInterface {

    public static final int REQUEST_CODE = 9;//相册选择后返回值
    public static final int PHOTO = 0; //拍照

    @BindView(R.id.amm_cb) CommonBar commonBar;
    @BindView(R.id.amm_civ_headerIcon) CircleImageView civ_headerIcon;
    @BindView(R.id.amm_cn_goPer) CommonNav cn_goPer; //个性签名
    @BindView(R.id.amm_cn_goName) CommonNav cn_goName; //姓名
    @BindView(R.id.amm_cn_goSex) CommonNav cn_goSex; //性别
    @BindView(R.id.amm_cn_goBir) CommonNav cn_goBir; //生日
    @BindView(R.id.amm_cn_goPhone) CommonNav cn_goPhoto; //手机号


    private ImageInst imageInst;
    private Uri photoUri;
    private File photoFile;
    private ActionSheet actionSheet;
    private EditText et_name;
    private CustomDatePicker customDatePicker;
    private String nowDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_msg);
        ButterKnife.bind(this);
        super.init();
    }

    @Override
    public void initView() {
        baseInit();
    }

    @Override
    public void initEvent() {
        commonBar.setBarInterface(this);
    }

    @Override
    public void initData() {

    }

    @Override
    public void disConnected() {

    }

    @Override
    public void disNetConnected() {

    }

    @Override
    public void netConnected() {

    }

    @Override
    public void checkLeftIcon() {
        finish();
    }

    @Override
    public void checkRightIcon() {

    }

    private void baseInit(){
        imageInst = new ImageInst();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
        nowDate = sdf.format(new Date());
        cn_goBir.setRightTitle(nowDate.split(" ")[0]);

        customDatePicker = new CustomDatePicker(this, new CustomDatePicker.ResultHandler() {
            @Override
            public void handle(String time) {
                cn_goBir.setRightTitle(time.split(" ")[0]);
            }
        }, "1970-01-01 00:00", nowDate);
        customDatePicker.showSpecificTime(false);
        customDatePicker.setIsLoop(true);

    }

    /**更换头像*/
    @OnClick(R.id.amm_rl_headerIcoN) void headerIcon(){
        actionSheet = ActionSheet.create(this).setOptItems(
                new OptItem("拍照", getResources().getColor(R.color.colorCommon), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        goPhoto();
                        actionSheet.dismiss();
                    }
                }),
                new OptItem("相册", getResources().getColor(R.color.colorCommon), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        goImage();
                        actionSheet.dismiss();
                    }
                }));
        actionSheet.show();
    }

    private void goPhoto(){
        photoFile = MultiMediaManager.getFile(CacheManager.getDiskCacheDir(this), getString(R.string.app_name),
                String.valueOf(System.currentTimeMillis()));
        photoUri = imageInst.openCamera(this, photoFile, PHOTO, GlobalConstants.PROVOIDER);
    }

    private void goImage(){
        imageInst.openPhotoAlbum(this, REQUEST_CODE, R.drawable.bg_common_bar);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == PHOTO){
                MultiMediaManager.updateImages(this, photoUri);
                GlideImageManager.showFileDownloadImage(this, photoFile.getAbsolutePath(), civ_headerIcon);
            }else if(requestCode ==  REQUEST_CODE){
                List<String> path = data.getStringArrayListExtra(PhotoPickerActivity.EXTRA_RESULT_SELECTION);
                if(path != null && path.size() > 0){
                    GlideImageManager.showFileDownloadImage(this, path.get(0), civ_headerIcon);
                }
            }
        }
    }

    @OnClick(R.id.amm_cn_goPer) void per(){
        RedirectUtil.redirect(this, PersonalizedActivity.class);
    }

    @OnClick(R.id.amm_cn_goName) void name(){
        et_name = new EditText(this);
        ConfigUtil.showAlertDialog(this, "修改姓名", null, et_name, this);
    }

    @Override
    public void confirmDialog() {
        String name = et_name.getText().toString();
        cn_goName.setRightTitle(name);
    }

    @Override
    public void cancelDialog() {

    }

    /**性别*/
    @OnClick(R.id.amm_cn_goSex) void sex(){
        actionSheet = ActionSheet.create(this).setOptItems(
                new OptItem("男", R.color.colorCommon, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        actionSheet.dismiss();
                        cn_goSex.setRightTitle("男");
                    }
                }),
                new OptItem("女", R.color.colorCommon, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        actionSheet.dismiss();
                        cn_goSex.setRightTitle("女");
                    }
                })
        );
        actionSheet.show();
    }

    @OnClick(R.id.amm_cn_goBir) void bir(){
        customDatePicker.show(nowDate);
    }

    @OnClick(R.id.amm_cn_goPhone) void phone(){
        RedirectUtil.redirect(this, RevisePhoneActivity.class);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        commonBar.setBarInterface(null);
        if(imageInst != null){
            imageInst.onMyDestroy();
        }
        if(actionSheet != null){
            actionSheet.onMyDestroy();
        }
        if(customDatePicker != null){
            customDatePicker = null;
        }
    }


}
