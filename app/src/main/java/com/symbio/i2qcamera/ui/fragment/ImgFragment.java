package com.symbio.i2qcamera.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.constant.PermissionConstants;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.ImageUtils;
import com.blankj.utilcode.util.PermissionUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.huantansheng.easyphotos.EasyPhotos;
import com.symbio.i2qcamera.R;
import com.symbio.i2qcamera.base.BaseFragment;
import com.symbio.i2qcamera.data.FolderRefreshEvent;
import com.symbio.i2qcamera.data.ImgDeleteEvent;
import com.symbio.i2qcamera.ui.activity.AlbumActivity;
import com.symbio.i2qcamera.ui.activity.MainActivity;
import com.symbio.i2qcamera.ui.adapter.ImgListAdapter;
import com.symbio.i2qcamera.util.CommonUtil;
import com.symbio.i2qcamera.util.DialogUtils;
import com.symbio.i2qcamera.util.GlideEngine;
import com.symbio.i2qcamera.util.WindowUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class ImgFragment extends BaseFragment {

    private static final String FILE_PROVIDER_AUTHORITY = "com.symbio.i2qcamera.fileprovider";
    private static final int REQUEST_CODE_CAMERA = 10001;
    private static final int REQUEST_CODE_ALBUM = 10002;
    private static final int RESULT_CODE_SUCCESS = -1;
    private static final int RESULT_CODE_CANCEL = 0;
    @BindView(R.id.back_img_iv)
    ImageView mBackImgIv;
    @BindView(R.id.path_img_tv)
    TextView mPathImgTv;
    @BindView(R.id.content_img_rv)
    RecyclerView mContentImgRv;
    @BindView(R.id.quick_img_tv)
    ImageView quickImgTv;
    private int num;
    private int padding;
    private ImgListAdapter mAdapter;
    private File mCurrentFile;
    private Handler mHandler = new Handler();
    private String[] mMenuList = new String[]{"Album", "Camera"};

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int screenWidth = ScreenUtils.getScreenWidth();
        int dp101 = SizeUtils.dp2px(101);
        num = screenWidth / dp101;
        padding = (screenWidth % dp101) / 2;
        EventBus.getDefault().register(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mContentImgRv.setPadding(padding, SizeUtils.dp2px(15), padding, SizeUtils.dp2px(15));
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), num);
        mContentImgRv.setLayoutManager(layoutManager);
        initData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public static Fragment newInstance() {
        Fragment fragment = new ImgFragment();
        return fragment;
    }


    @Override
    public int getLayoutResID() {
        return R.layout.view_img;
    }

    private void initData() {
        mAdapter = new ImgListAdapter(R.layout.item_img, new ArrayList<>());
        View addView = getLayoutInflater().inflate(R.layout.item_add_img, null);
        View addLayout = addView.findViewById(R.id.add_layout);
        mContentImgRv.setAdapter(mAdapter);
        mAdapter.addData(new File("add_button"));
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (adapter.getData().size() - 1 == position) {
                    DialogUtils.singleSelectionDialog(getContext(), mMenuList,
                            position1 -> {
                                switch (position1) {
                                    case 0:
                                        EasyPhotos.createAlbum(ImgFragment.this, true, GlideEngine.getInstance())
                                                .setFileProviderAuthority(FILE_PROVIDER_AUTHORITY)
                                                .setCount(9)
                                                .setPuzzleMenu(false)
                                                .start(REQUEST_CODE_ALBUM);
                                        break;
                                    case 1:
                                        openCamera(view);
                                        break;
                                }
                            });

                } else {
                    File file = (File) adapter.getData().get(position);
                    AlbumActivity.start(getContext(), file.getAbsolutePath(), mAdapter.getData());
                }
            }
        });
        mAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                File file = (File) adapter.getData().get(position);
                deletePhoto(file, position, view);
            }
        });
    }

    private void openCamera(View currentView) {
        boolean isGranted = PermissionUtils.isGranted(PermissionConstants.CAMERA);
        if (isGranted) {
            takePhoto();
        } else {
            PermissionUtils.permission(PermissionConstants.CAMERA)
                    .callback(new PermissionUtils.SimpleCallback() {
                        @Override
                        public void onGranted() {
                            takePhoto();
                        }

                        @Override
                        public void onDenied() {
                            DialogUtils.showFailTipDialog(getContext(), currentView,
                                    "No camera permission!");
                        }
                    })
                    .request();
        }
    }

    private void deletePhoto(File file, int position, View currentView) {
        DialogUtils.showNoOrYesDialog(getActivity(), "Sure to delete?",
                (dialog, index) -> dialog.dismiss(),
                (dialog, index) -> {
                    boolean delete = file.delete();
                    if (delete) {
                        mAdapter.remove(position);
                        DialogUtils.showSuccessTipDialog(getContext(), currentView,
                                "Image file deleted successfully!");
                    } else {
                        DialogUtils.showFailTipDialog(getContext(), currentView,
                                "Image file deleted failed!");
                    }
                    dialog.dismiss();
                });
    }

    private void takePhoto() {
        EasyPhotos.createCamera(this)
                .setFileProviderAuthority(FILE_PROVIDER_AUTHORITY)
                .start(REQUEST_CODE_CAMERA);
        showMenu();
    }

    private void showMenu() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                WindowUtils.showPopupWindow(getContext(), mCurrentFile);
            }
        }, 800);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ALBUM) {
            if (resultCode == RESULT_CODE_SUCCESS) {
                executeImageResult(data);
            }
        } else if (requestCode == REQUEST_CODE_CAMERA) {
            WindowUtils.hidePopupWindow();
            if (resultCode == RESULT_CODE_SUCCESS) {
                takePhoto();
                executeImageResult(data);
            }
        }

    }

    private void executeImageResult(Intent data) {
        ArrayList<String> resultPaths = data.getStringArrayListExtra(EasyPhotos.RESULT_PATHS);
        for (String originalPath : resultPaths) {
            File srcFile = new File(originalPath);
            if (srcFile.exists()) {
                File destFile = new File(mCurrentFile, System.currentTimeMillis() + ".jpg");
                boolean copyFile = FileUtils.copyFile(srcFile, destFile);
                if (copyFile) {
                    mAdapter.addData(mAdapter.getData().size() - 1, destFile);
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ImgDeleteEvent event) {
        if (mAdapter.getData().size() > event.getPosition()) {
            mAdapter.remove(event.getPosition());
        }
    }

    @OnClick({R.id.back_img_iv, R.id.quick_img_tv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back_img_iv:
                MainActivity activity = (MainActivity) getActivity();
                activity.showFolderView();
                EventBus.getDefault().post(new FolderRefreshEvent());
                break;
            case R.id.quick_img_tv:
                File parentFile = mCurrentFile.getParentFile();
                File[] files = parentFile.listFiles(new FileFilter() {
                    @Override
                    public boolean accept(File pathname) {
                        return CommonUtil.isNeedAddIMG(pathname);
                    }
                });
                int checkedIndex = -1;
                if (files != null && files.length > 0) {
                    String[] fileNames = new String[files.length];
                    for (int i = 0; i < files.length; i++) {
                        String name = CommonUtil.simplifyFileName(files[i].getName());
                        fileNames[i] = name;
                        if (mCurrentFile.getName().equals(files[i].getName())) {
                            checkedIndex = i;
                        }
                    }
                    DialogUtils.singleSelectionDialog(getContext(), checkedIndex, fileNames,
                            position -> loadCheckPath(files[position]));
                }
                break;
        }
    }


    public void loadCheckPath(File file) {
        if (mAdapter == null) {
            return;
        }
        mCurrentFile = file;
        mAdapter.getData().clear();
        mAdapter.notifyDataSetChanged();
        mAdapter.addData(new File("add_button"));
        String[] path = file.getAbsolutePath().split("/Jobs/");
        if (path != null && path.length > 1) {
            String pathName = path[1];
            String[] split = pathName.split("/");
            if (split != null && split.length > 1) {
                mPathImgTv.setText(new StringBuilder().append(split[0])
                        .append(" > > ")
                        .append(CommonUtil.simplifyFileName(split[split.length - 1])));
            } else if (split != null && split.length == 1) {
                mPathImgTv.setText(split[0]);
            }
        }
        File[] files = file.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return ImageUtils.isImage(name);
            }
        });
        if (files != null && files.length > 0) {
            List<File> fileList = new ArrayList<>(Arrays.asList(files));
            mAdapter.addData(0, fileList);
        }
    }
}
