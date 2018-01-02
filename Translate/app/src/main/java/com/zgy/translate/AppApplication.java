package com.zgy.translate;

import com.tencent.tinker.loader.app.TinkerApplication;
import com.tencent.tinker.loader.shareutil.ShareConstants;
import com.umeng.socialize.PlatformConfig;

/**
 * Created by zhouguangyue on 2017/12/6.
 */

public class AppApplication extends TinkerApplication{

    {
        PlatformConfig.setWeixin("wxdc1e388c3822c80b", "3baf1193c85774b3fd9d18447d76cab0");
        PlatformConfig.setQQZone("100424468", "c7394704798a158208a74ab60104f0ba");
    }

    public AppApplication() {
        super(ShareConstants.TINKER_ENABLE_ALL, "com.zgy.translate.AppApplicationLike");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
