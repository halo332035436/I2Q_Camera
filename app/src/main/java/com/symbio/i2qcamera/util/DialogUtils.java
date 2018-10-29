package com.symbio.i2qcamera.util;

import android.app.Activity;
import android.app.Dialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.symbio.i2qcamera.R;
import com.symbio.i2qcamera.adapter.ImgGetAdapter;
import com.symbio.i2qcamera.view.RimlessDialog;

import java.util.List;

public class DialogUtils {

    public static void singleSelectDialog(List<String> strings, Activity context, ItemSelectListener listener) {
        Dialog dialog = new RimlessDialog(context, R.style.myDialog);
        //实例化布局
        LayoutInflater inflater = context.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_property_fill_in, null);
        RecyclerView viewList = dialogView.findViewById(R.id.rv_fill_in_property_item);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        viewList.setLayoutManager(linearLayoutManager);
        ImgGetAdapter imgGetAdapter = new ImgGetAdapter(R.layout.item_fill_in_img_add, strings);
        imgGetAdapter.setOnItemClickListener((adapter, view, position) -> {
            dialog.dismiss();
            listener.onSelect(position);
        });
        viewList.setAdapter(imgGetAdapter);
        dialog.setContentView(dialogView);
        dialog.show();
    }

    public interface ItemSelectListener {
        void onSelect(int position);
    }

}
