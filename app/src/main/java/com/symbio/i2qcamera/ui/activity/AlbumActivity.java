package com.symbio.i2qcamera.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.symbio.i2qcamera.R;
import com.symbio.i2qcamera.base.BaseActivity;
import com.symbio.i2qcamera.data.ImgDeleteEvent;
import com.symbio.i2qcamera.ui.adapter.AlbumFragmentAdapter;
import com.symbio.i2qcamera.ui.fragment.AlbumFragment;
import com.symbio.i2qcamera.ui.view.PhotoViewPager;
import com.symbio.i2qcamera.util.DialogUtils;
import com.symbio.i2qcamera.util.WindowUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;


public class AlbumActivity extends BaseActivity {

    @BindView(R.id.back_album_iv)
    ImageView backAlbumIv;
    @BindView(R.id.album_num_tv)
    TextView albumNumTv;
    @BindView(R.id.album_delete_btn)
    ImageView albumDeleteBtn;
    @BindView(R.id.album_list_pvp)
    PhotoViewPager albumListPvp;
    List<File> mFileList = new ArrayList<>();
    private AlbumFragmentAdapter mAdapter;
    private int mPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowUtils.setStateBarColor(this, android.R.color.black);
        Intent intent = getIntent();
        String path = intent.getStringExtra("path");
        ArrayList<String> pathData = intent.getStringArrayListExtra("pathData");
        albumListPvp.setOffscreenPageLimit(0);
        if (pathData != null && pathData.size() > 0) {
            mFileList.clear();
            for (int i = 0; i < pathData.size(); i++) {
                String s = pathData.get(i);
                mFileList.add(new File(s));
                if (s.equals(path)) {
                    mPage = i;
                }
            }
            initData();
            initListener();
        }
    }

    public static void start(Context context, String imgPath, List<File> data) {
        Intent intent = new Intent(context, AlbumActivity.class);
        intent.putExtra("path", imgPath);
        ArrayList<String> pathData = new ArrayList<>();
        for (File file : data) {
            pathData.add(file.getAbsolutePath());
        }
        pathData.remove(pathData.size() - 1);
        intent.putStringArrayListExtra("pathData", pathData);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutResID() {
        return R.layout.activity_album;
    }

    private void initListener() {
        albumListPvp.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                albumNumTv.setText(position + 1 + "/" + mFileList.size());
            }
        });
    }

    private void initData() {
        List<Fragment> fragments = new ArrayList<>();
        for (int i = 0; i < mFileList.size(); i++) {
            fragments.add(AlbumFragment.newInstance(mFileList.get(i).getAbsolutePath()));
        }
        mAdapter = new AlbumFragmentAdapter(getSupportFragmentManager(), fragments);
        albumListPvp.setAdapter(mAdapter);
        albumListPvp.setCurrentItem(mPage);
        albumNumTv.setText(mPage + 1 + "/" + mFileList.size());
    }

    @OnClick({R.id.back_album_iv, R.id.album_delete_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back_album_iv:
                finish();
                break;
            case R.id.album_delete_btn:
                DialogUtils.showNoOrYesDialog(this, "Sure to delete?",
                        (dialog, index) -> dialog.dismiss(),
                        (dialog, index) -> {
                            int currentItem = albumListPvp.getCurrentItem();
                            File file = mFileList.get(currentItem);
                            boolean delete = file.delete();
                            if (delete) {
                                mFileList.remove(currentItem);
                                int size = mFileList.size();
                                if (size == 0) {
                                    initData();
                                    albumNumTv.setText("No pictures!");
                                } else if (size == currentItem) {
                                    mPage = currentItem - 1;
                                    initData();
                                } else if (size > currentItem) {
                                    mPage = currentItem;
                                    initData();
                                }
                                EventBus.getDefault().post(new ImgDeleteEvent(currentItem));
                                DialogUtils.showSuccessTipDialog(AlbumActivity.this, albumDeleteBtn,
                                        "Image file deleted successfully!");
                            } else {
                                DialogUtils.showFailTipDialog(AlbumActivity.this, albumDeleteBtn,
                                        "Image file deleted failed!");

                            }
                            dialog.dismiss();
                        });
                break;
        }
    }
}
