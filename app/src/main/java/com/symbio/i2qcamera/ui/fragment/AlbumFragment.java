package com.symbio.i2qcamera.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.symbio.i2qcamera.R;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class AlbumFragment extends Fragment {


    @BindView(R.id.album_photo_pv)
    PhotoView albumPhotoPv;
    private Unbinder mBind;
    private String imgPath;

    public static Fragment newInstance(String imgPath) {
        AlbumFragment fragment = new AlbumFragment();
        fragment.setImgPath(imgPath);
        return fragment;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        PhotoView view = (PhotoView) inflater.inflate(R.layout.view_album, null);
        mBind = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        File file = new File(imgPath);
        Glide.with(getContext())
                .load(file)
                .into(albumPhotoPv);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBind.unbind();
    }
}
