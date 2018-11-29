package com.symbio.i2qcamera.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;

import com.symbio.i2qcamera.R;
import com.symbio.i2qcamera.ui.adapter.MenuAdapter;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;

public class WindowUtils {
    private static final String LOG_TAG = "WindowUtils";
    private static View mView = null;
    private static WindowManager mWindowManager = null;
    private static Context mContext = null;
    public static Boolean isShown = false;

    /**
     * 显示弹出框
     *
     * @param context
     */
    public static void showPopupWindow(final Context context, File file) {
        try {
            if (isShown) {
                Log.i(LOG_TAG, "return cause already shown");
                return;
            }
            isShown = true;
            Log.i(LOG_TAG, "showPopupWindow");
            // 获取应用的Context
            mContext = context.getApplicationContext();
            // 获取WindowManager
            mWindowManager = (WindowManager) mContext
                    .getSystemService(Context.WINDOW_SERVICE);
            mView = setUpView(context, file);
            final WindowManager.LayoutParams params = new WindowManager.LayoutParams();
            // 类型
            params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
            // WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
            // 设置flag
            int flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            // | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            // 如果设置了WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE，弹出的View收不到Back键的事件
            params.flags = flags;
            // 不设置这个弹出框的透明遮罩显示为黑色
            params.format = PixelFormat.TRANSLUCENT;
            // FLAG_NOT_TOUCH_MODAL不阻塞事件传递到后面的窗口
            // 设置 FLAG_NOT_FOCUSABLE 悬浮窗口较小时，后面的应用图标由不可长按变为可长按
            // 不设置这个flag的话，home页的划屏会有问题
            params.width = LayoutParams.MATCH_PARENT;
            params.height = LayoutParams.WRAP_CONTENT;
            params.gravity = Gravity.TOP | Gravity.RIGHT;
            mWindowManager.addView(mView, params);
            Log.i(LOG_TAG, "add view");
        } catch (RuntimeException e) {
            Log.e(LOG_TAG, "add view error!");
        }
    }

    /**
     * 隐藏弹出框
     */
    public static void hidePopupWindow() {
        try {
            Log.i(LOG_TAG, "hide " + isShown + ", " + mView);
            if (isShown && null != mView) {
                Log.i(LOG_TAG, "hidePopupWindow");
                mWindowManager.removeView(mView);
                isShown = false;
            }
        } catch (RuntimeException e) {
            Log.e(LOG_TAG, "hidePopupWindow error ");
        }
    }

    private static View setUpView(final Context context, File file) {
        Log.i(LOG_TAG, "setUp view");
        View view = LayoutInflater.from(context).inflate(R.layout.window_list,
                null);
        ImageView menu = view.findViewById(R.id.window_iv);
        RecyclerView list = view.findViewById(R.id.window_tv);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        list.setLayoutManager(layoutManager);
        File[] files = file.getParentFile().listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isDirectory();
            }
        });
        if (file != null) {
            MenuAdapter adapter = new MenuAdapter(R.layout.item_menu, new ArrayList<>(Arrays.asList(files)), file);
            list.setAdapter(adapter);
        }
        menu.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int visibility = list.getVisibility();
                if (View.GONE == visibility) {
                    list.setVisibility(View.VISIBLE);
                } else if (View.VISIBLE == visibility) {
                    list.setVisibility(View.GONE);
                }
            }
        });

        return view;
    }

    public static void setStateBarColor(Activity activity, int color) {
        Window window = activity.getWindow();
        //取消状态栏透明
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //添加Flag把状态栏设为可绘制模式
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        //设置状态栏颜色
        window.setStatusBarColor(activity.getResources().getColor(color));
        //设置系统状态栏处于可见状态
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
    }
}

