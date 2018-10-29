package com.symbio.i2qcamera.view;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;

import com.symbio.i2qcamera.R;

/**
 * 版    权 :华润创业有限公司
 * 项目名称 : ShangShuiTuFang
 * 包    名 : com.symbio.i2qcamera.view
 * 作    者 : 贺竞辉
 * 创建时间 : 2018/10/25 16:35
 * <p>
 * 描述 :
 */
public class RimlessDialog extends Dialog {


    public RimlessDialog(@NonNull Context context, int themeResId) {
        super(context, R.style.myDialog);
    }
}
