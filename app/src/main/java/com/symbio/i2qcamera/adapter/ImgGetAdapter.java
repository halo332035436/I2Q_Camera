package com.symbio.i2qcamera.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.symbio.i2qcamera.R;

import java.util.List;

/**
 * 版    权 :华润创业有限公司
 * 项目名称 : ShangShuiTuFang
 * 包    名 : com.huarun.huachuang.tufang.adapter.taskcenter
 * 作    者 : 贺竞辉
 * 创建时间 : 2018/8/13 14:27
 * <p>
 * 描述 :
 */
public class ImgGetAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    public ImgGetAdapter(int layoutResId, @Nullable List<String> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        helper.setText(R.id.tv_fill_in_img_add, item);
    }
}
