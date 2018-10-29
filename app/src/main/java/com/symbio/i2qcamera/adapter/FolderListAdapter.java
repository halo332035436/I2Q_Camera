package com.symbio.i2qcamera.adapter;

import android.animation.Animator;
import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.symbio.i2qcamera.R;
import com.symbio.i2qcamera.util.CommonUtil;

import java.io.File;
import java.util.List;

public class FolderListAdapter extends BaseQuickAdapter<File, BaseViewHolder> {

    public FolderListAdapter(int layoutResId, @Nullable List<File> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, File item) {
        helper.setText(R.id.item_folder_name_tv, item.getName());
        boolean isNeedShowDocIcon = CommonUtil.isNeedShowDocIcon(item);
        helper.setImageResource(R.id.item_folder_icon_iv, isNeedShowDocIcon ? R.mipmap.document : R.mipmap.folder);
        int needShowNum = CommonUtil.isNeedShowNum(item);
        if (needShowNum > 0) {
            helper.setVisible(R.id.item_folder_num_tv, true);
            helper.setText(R.id.item_folder_num_tv, needShowNum > 99 ? "99+" : needShowNum + "");
        } else {
            helper.setVisible(R.id.item_folder_num_tv, false);
        }

        helper.addOnClickListener(R.id.item_folder_layout);
        helper.addOnClickListener(R.id.folder_delete_btn);
    }

    @Override
    protected void startAnim(Animator anim, int index) {
        anim.setDuration(100);
        super.startAnim(anim, index);
    }

}
