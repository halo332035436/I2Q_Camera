package com.symbio.i2qcamera.ui.view;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;

import com.symbio.i2qcamera.R;

public class RimlessDialog extends Dialog {


    public RimlessDialog(@NonNull Context context, int themeResId) {
        super(context, R.style.myDialog);
    }
}
