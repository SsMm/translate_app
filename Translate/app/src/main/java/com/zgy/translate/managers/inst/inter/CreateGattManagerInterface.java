package com.zgy.translate.managers.inst.inter;

/**
 * Created by zhouguangyue on 2017/12/22.
 */

public interface CreateGattManagerInterface {

    void noProfile(); //
    void noRequest(); //不是要求蓝牙耳机
    void conState(boolean state);
    void gattOrder(String order);

}
