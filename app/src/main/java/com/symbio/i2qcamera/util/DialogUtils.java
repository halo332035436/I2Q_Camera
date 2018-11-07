package com.symbio.i2qcamera.util;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;

import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.symbio.i2qcamera.app.Config;

public class DialogUtils {

    public interface ItemSelectListener {
        void onSelect(int position);
    }

    public static void showNoOrYesDialog(Context context, String content,
                                         QMUIDialogAction.ActionListener noListener,
                                         QMUIDialogAction.ActionListener yesListener) {
        new QMUIDialog.MessageDialogBuilder(context)
                .setTitle(content)
                .addAction("No", noListener)
                .addAction("Yes", yesListener)
                .create(Config.QMUI_DIALOG_STYLE)
                .show();

    }


    public static void singleSelectionDialog(Context context, int checkedIndex,
                                             String[] items, ItemSelectListener listener) {
        QMUIDialog.CheckableDialogBuilder builder = new QMUIDialog.CheckableDialogBuilder(context);
        if (checkedIndex != -1) {
            builder.setCheckedIndex(checkedIndex);
        }
        builder.addItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                listener.onSelect(which);
                dialog.dismiss();
            }
        })
                .create(Config.QMUI_DIALOG_STYLE)
                .show();
    }

    public static void singleSelectionDialog(Context context,
                                             String[] items,
                                             ItemSelectListener listener) {
        singleSelectionDialog(context, -1, items, listener);
    }

    public static void showSuccessTipDialog(Context context, View currentView, String tip) {
        showInfoTipDialog(context, currentView, tip, QMUITipDialog.Builder.ICON_TYPE_SUCCESS);
    }

    public static void showFailTipDialog(Context context, View currentView, String tip) {
        showInfoTipDialog(context, currentView, tip, QMUITipDialog.Builder.ICON_TYPE_FAIL);
    }

    public static void showInfoTipDialog(Context context, View currentView, String tip) {
        showInfoTipDialog(context, currentView, tip, QMUITipDialog.Builder.ICON_TYPE_INFO);
    }

    public static void showTipDialog(Context context, View currentView, String tip) {
        showInfoTipDialog(context, currentView, tip, QMUITipDialog.Builder.ICON_TYPE_NOTHING);
    }

    private static void showInfoTipDialog(Context context, View currentView, String tip, int iconType) {
        QMUITipDialog tipDialog = new QMUITipDialog.Builder(context)
                .setIconType(iconType)
                .setTipWord(tip)
                .create();
        tipDialog.show();
        currentView.postDelayed(() -> tipDialog.dismiss(), 1000);
    }

}
