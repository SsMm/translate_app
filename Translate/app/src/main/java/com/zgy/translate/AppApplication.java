package com.zgy.translate;

import com.tencent.tinker.loader.app.TinkerApplication;
import com.tencent.tinker.loader.shareutil.ShareConstants;

/**
 * Created by zhouguangyue on 2017/12/6.
 */

public class AppApplication extends TinkerApplication{

    public AppApplication() {
        super(ShareConstants.TINKER_ENABLE_ALL, "com.zgy.translate.AppApplicationLike");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
