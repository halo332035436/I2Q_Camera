package com.symbio.i2qcamera.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.symbio.i2qcamera.R;
import com.symbio.i2qcamera.base.BaseFragment;

import java.io.File;

import butterknife.BindView;


public class AlbumFragment extends BaseFragment {


    @BindView(R.id.album_photo_pv)
    PhotoView albumPhotoPv;
    private String imgPath;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        File file = new File(imgPath);
        Glide.with(getContext())
                .load(file)
                .into(albumPhotoPv);
    }

    public static Fragment newInstance(String imgPath) {
        AlbumFragment fragment = new AlbumFragment();
        fragment.setImgPath(imgPath);
        return fragment;
    }

    @Override
    public int getLayoutResID() {
        return R.layout.view_album;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }


}
