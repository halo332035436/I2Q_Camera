package com.symbio.i2qcamera.base.contract;

/**
 * 作者 : halo332035436
 * 时间 : 2018/10/30 11:23
 * 描述 :
 */
public interface MainContract {

    interface View {
        void showExitDialog();

        void initFragments();
    }

    interface Presenter {

        void setCurrentItem(int index);

        void exit();
    }

}
