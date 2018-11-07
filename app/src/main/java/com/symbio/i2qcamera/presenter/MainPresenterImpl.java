package com.symbio.i2qcamera.presenter;

import com.symbio.i2qcamera.base.contract.MainContract;

/**
 * 作者 : halo332035436
 * 时间 : 2018/10/30 15:50
 * 描述 :
 */
public class MainPresenterImpl implements MainContract.Presenter {

    private MainContract.View mView;

    public MainPresenterImpl(MainContract.View view) {
        mView = view;
    }

    @Override
    public void initDataAndEvent() {

    }

    @Override
    public void setCurrentItem(int index) {

    }

    @Override
    public void exit() {
        mView.showExitDialog();
    }

}
