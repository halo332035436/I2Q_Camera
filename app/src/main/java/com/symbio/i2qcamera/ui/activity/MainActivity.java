package com.symbio.i2qcamera.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.ImageView;
import android.widget.Toast;

import com.blankj.utilcode.constant.PermissionConstants;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.PermissionUtils;
import com.symbio.i2qcamera.R;
import com.symbio.i2qcamera.app.Config;
import com.symbio.i2qcamera.base.BaseActivity;
import com.symbio.i2qcamera.base.contract.MainContract;
import com.symbio.i2qcamera.presenter.MainPresenterImpl;
import com.symbio.i2qcamera.ui.adapter.ContentFragmentAdapter;
import com.symbio.i2qcamera.ui.fragment.EmptyFragment;
import com.symbio.i2qcamera.ui.fragment.FolderFragment;
import com.symbio.i2qcamera.ui.fragment.ImgFragment;
import com.symbio.i2qcamera.ui.view.NoScrollViewPager;
import com.symbio.i2qcamera.util.DialogUtils;
import com.symbio.i2qcamera.util.WindowUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class MainActivity extends BaseActivity implements MainContract.View {

    @BindView(R.id.main_quit_btn)
    ImageView mainQuitBtn;
    @BindView(R.id.main_content_vp)
    NoScrollViewPager mainContentVp;
    private List<Fragment> mFragments;
    private ContentFragmentAdapter mAdapter;
    private MainContract.Presenter mPresenter;

    public static void start(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowUtils.setStateBarColor(this, android.R.color.black);
        mPresenter = new MainPresenterImpl(this);
        initState();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    public int getLayoutResID() {
        return R.layout.activity_main;
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
        ImgFragment fragment = (ImgFragment) mFragments.get(2);
        if (fragment.isNeedReload()) {
            Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage(getApplication().getPackageName());
            LaunchIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(LaunchIntent);
            Toast.makeText(this, "The resources have been recovered by the system", Toast.LENGTH_SHORT).show();
            return;
        }
        mainContentVp.setCurrentItem(2);
        fragment.loadCheckPath(file);
    }

    @OnClick(R.id.main_quit_btn)
    public void onViewClicked() {
        mPresenter.exit();
    }

    @Override
    public void showExitDialog() {
        DialogUtils.showNoOrYesDialog(this, "Sure to quit?",
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
