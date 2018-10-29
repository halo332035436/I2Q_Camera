package com.symbio.i2qcamera.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.symbio.i2qcamera.R;

import java.util.List;

public class ImgGetAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    public ImgGetAdapter(int layoutResId, @Nullable List<String> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        helper.setText(R.id.tv_fill_in_img_add, item);
    }
}
