package com.symbio.i2qcamera.adapter;

import android.graphics.Color;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.symbio.i2qcamera.R;

import java.io.File;
import java.util.List;

public class ImgListAdapter extends BaseQuickAdapter<File, BaseViewHolder> {

    public ImgListAdapter(int layoutResId, @Nullable List<File> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, File item) {
        boolean isAddButton = item.getPath().equals("add_button");
        if (isAddButton) {
            helper.setImageResource(R.id.item_img_iv, R.mipmap.add);
            helper.setBackgroundRes(R.id.item_img_iv, R.drawable.add_button_selector);
        } else {
            helper.setBackgroundRes(R.id.item_img_iv, R.color.color_a0ffffff);
            Glide.with(mContext)
                    .load(item)
                    .into((ImageView) helper.getView(R.id.item_img_iv));
        }
        helper.setVisible(R.id.item_del_iv, !isAddButton);
        helper.addOnClickListener(R.id.item_del_iv);
    }
}
