package com.symbio.i2qcamera.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.widget.ImageView;

import com.blankj.utilcode.constant.PermissionConstants;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.PermissionUtils;
import com.symbio.i2qcamera.R;
import com.symbio.i2qcamera.adapter.ContentFragmentAdapter;
import com.symbio.i2qcamera.base.Config;
import com.symbio.i2qcamera.fragment.EmptyFragment;
import com.symbio.i2qcamera.fragment.FolderFragment;
import com.symbio.i2qcamera.fragment.ImgFragment;
import com.symbio.i2qcamera.view.NoScrollViewPager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class MainActivity extends FragmentActivity {

    @BindView(R.id.main_quit_btn)
    ImageView mainQuitBtn;
    @BindView(R.id.main_content_vp)
    NoScrollViewPager mainContentVp;
    private Unbinder mBind;
    private List<Fragment> mFragments;
    private ContentFragmentAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBind = ButterKnife.bind(this);
        initState();
    }

    private void initFragment() {
        mFragments = new ArrayList<>();
        mFragments.add(EmptyFragment.newInstance());
        mFragments.add(FolderFragment.newInstance());
        mFragments.add(ImgFragment.newInstance());
        mAdapter = new ContentFragmentAdapter(getSupportFragmentManager(), mFragments);
        mainContentVp.setAdapter(mAdapter);
        mainContentVp.setOffscreenPageLimit(5);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
//        Intent intent = getIntent();
//        finish();
//        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBind.unbind();
    }

    private void initState() {
        boolean isGranted = PermissionUtils.isGranted(PermissionConstants.STORAGE);
        if (isGranted) {
            initDir();
        } else {
            PermissionUtils.permission(PermissionConstants.STORAGE)
                    .callback(new PermissionUtils.SimpleCallback() {
                        @Override
                        public void onGranted() {
                            initDir();
                        }

                        @Override
                        public void onDenied() {
                            showEmptyView();
                        }
                    })
                    .request();
        }

    }

    private void initDir() {
        initFragment();
        File basePath = new File(Config.BASE_PATH);
        if (!basePath.exists()) {
            basePath.mkdirs();
        }
        List<File> fileList = FileUtils.listFilesInDir(Config.BASE_PATH);
        if (fileList == null || fileList.size() == 0) {
            showEmptyView();
        } else {
            //加载文件树目录
            showFolderView();
        }
    }

    private void showEmptyView() {
        mainContentVp.setCurrentItem(0);
    }

    public void showFolderView() {
        mainContentVp.setCurrentItem(1);
    }

    public void showImgView(File file) {
        mainContentVp.setCurrentItem(2);
        ImgFragment fragment = (ImgFragment) mFragments.get(2);
        fragment.loadCheckPath(file);
    }

    @OnClick(R.id.main_quit_btn)
    public void onViewClicked() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setMessage("Sure to quit?")
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

}
