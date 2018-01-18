package com.zgy.translate;

import com.tencent.tinker.loader.app.TinkerApplication;
import com.tencent.tinker.loader.shareutil.ShareConstants;
import com.umeng.socialize.PlatformConfig;

/**
 * Created by zhouguangyue on 2017/12/6.
 */

public class AppApplication extends TinkerApplication{

    {
        PlatformConfig.setWeixin("wxc3b4a8082a6cc59d", "80a20fd7aac8b3fdaa3d53c74494e610");
        //PlatformConfig.setQQZone("100424468", "c7394704798a158208a74ab60104f0ba");
        PlatformConfig.setQQZone("1106605767", "OSy2dkP2PLld7rQK");
    }

    public AppApplication() {
        super(ShareConstants.TINKER_ENABLE_ALL, "com.zgy.translate.AppApplicationLike");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
