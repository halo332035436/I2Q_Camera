package com.symbio.i2qcamera.ui.adapter;

import android.graphics.Color;
import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.symbio.i2qcamera.R;

import java.io.File;
import java.util.List;

public class MenuAdapter extends BaseQuickAdapter<File, BaseViewHolder> {

    private File file;

    public MenuAdapter(int layoutResId, @Nullable List<File> data, File file) {
        super(layoutResId, data);
        this.file = file;
    }

    @Override
    protected void convert(BaseViewHolder helper, File item) {
        helper.setText(R.id.item_menu_tv, item.getName());
        if (item.getName().equals(file.getName())) {
            helper.setTextColor(R.id.item_menu_tv, Color.parseColor("#FFFFFFFF"));
            helper.setBackgroundColor(R.id.item_menu_layout,Color.parseColor("#96000000"));
        }else {
            helper.setTextColor(R.id.item_menu_tv, Color.parseColor("#b4111111"));
            helper.setBackgroundColor(R.id.item_menu_layout,Color.parseColor("#96ffffff"));
        }
    }
}
