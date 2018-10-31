package com.symbio.i2qcamera.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.widget.ImageView;

import com.blankj.utilcode.constant.PermissionConstants;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.PermissionUtils;
import com.symbio.i2qcamera.R;
import com.symbio.i2qcamera.ui.adapter.ContentFragmentAdapter;
import com.symbio.i2qcamera.app.Config;
import com.symbio.i2qcamera.base.contract.MainContract;
import com.symbio.i2qcamera.ui.fragment.EmptyFragment;
import com.symbio.i2qcamera.ui.fragment.FolderFragment;
import com.symbio.i2qcamera.ui.fragment.ImgFragment;
import com.symbio.i2qcamera.presenter.MainPresenterImpl;
import com.symbio.i2qcamera.util.DialogUtils;
import com.symbio.i2qcamera.util.WindowUtils;
import com.symbio.i2qcamera.ui.view.NoScrollViewPager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class MainActivity extends FragmentActivity implements MainContract.View {

    @BindView(R.id.main_quit_btn)
    ImageView mainQuitBtn;
    @BindView(R.id.main_content_vp)
    NoScrollViewPager mainContentVp;
    private Unbinder mBind;
    private List<Fragment> mFragments;
    private ContentFragmentAdapter mAdapter;
    private MainContract.Presenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        WindowUtils.setStateBarColor(this, android.R.color.black);
        mBind = ButterKnife.bind(this);
        mPresenter = new MainPresenterImpl(this);
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
        mPresenter.exit();
    }

    @Override
    public void showExitDialog() {
        DialogUtils.showNoOrYesDialog(this,"Sure to quit?",
                (dialog, index) -> dialog.dismiss(),
                (dialog, index) -> finish());

    }

    @Override
    public void initFragments() {
        mFragments = new ArrayList<>();
        mFragments.add(EmptyFragment.newInstance());
        mFragments.add(FolderFragment.newInstance());
        mFragments.add(ImgFragment.newInstance());
        mAdapter = new ContentFragmentAdapter(getSupportFragmentManager(), mFragments);
        mainContentVp.setAdapter(mAdapter);
        mainContentVp.setOffscreenPageLimit(5);
    }
}
