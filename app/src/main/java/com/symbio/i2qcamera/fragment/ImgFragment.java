package com.symbio.i2qcamera.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.constant.PermissionConstants;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.ImageUtils;
import com.blankj.utilcode.util.PermissionUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.huantansheng.easyphotos.EasyPhotos;
import com.symbio.i2qcamera.R;
import com.symbio.i2qcamera.activity.AlbumActivity;
import com.symbio.i2qcamera.adapter.ImgListAdapter;
import com.symbio.i2qcamera.data.FolderRefreshEvent;
import com.symbio.i2qcamera.data.ImgDeleteEvent;
import com.symbio.i2qcamera.ui.activity.MainActivity;
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
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class ImgFragment extends Fragment {

    private static final int REQ_FULL_SIZE_CAPTURE = 10000;
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
    private Unbinder mBinder;
    private int num;
    private int padding;
    private ImgListAdapter mAdapter;
    private File mCurrentFile;
    private File mCurrentImg;
    private Handler mHandler = new Handler();
    private boolean isFirstOpenCamera = true;
    private List<String> mMenuList;

    public static Fragment newInstance() {
        Fragment fragment = new ImgFragment();
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.view_img, null);
        mBinder = ButterKnife.bind(this, view);
        int screenWidth = ScreenUtils.getScreenWidth();
        int dp101 = SizeUtils.dp2px(101);
        num = screenWidth / dp101;
        padding = (screenWidth % dp101) / 2;
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mContentImgRv.setPadding(padding, SizeUtils.dp2px(15), padding, SizeUtils.dp2px(15));
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), num);
        mContentImgRv.setLayoutManager(layoutManager);
        initData();
    }

    private void initData() {
        mAdapter = new ImgListAdapter(R.layout.item_img, new ArrayList<>());
        View addView = getLayoutInflater().inflate(R.layout.item_add_img, null);
        View addLayout = addView.findViewById(R.id.add_layout);
        addLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "add img", Toast.LENGTH_SHORT).show();
            }
        });
        mContentImgRv.setAdapter(mAdapter);
        mAdapter.addData(new File("add_button"));
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (adapter.getData().size() - 1 == position) {

                    DialogUtils.singleSelectDialog(mMenuList, getActivity(), new DialogUtils.ItemSelectListener() {
                        @Override
                        public void onSelect(int position) {
                            switch (position) {
                                case 0:
//                                    mTakePhoto.onPickMultiple(9);
                                    break;
                                case 1:
                                    openCamera();
                                    break;
                            }
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
                deletePhoto(file, position);
            }
        });
    }

    private void openCamera() {
        if (!isFirstOpenCamera) {
            takePhoto();
            return;
        }
        boolean isGranted = PermissionUtils.isGranted(PermissionConstants.CAMERA);
        if (isGranted) {
            takePhoto();
        } else {
            PermissionUtils.permission(PermissionConstants.CAMERA)
                    .callback(new PermissionUtils.SimpleCallback() {
                        @Override
                        public void onGranted() {
                            isFirstOpenCamera = false;
                            PermissionUtils.permission("android.permission.SYSTEM_ALERT_WINDOW")
                                    .callback(new PermissionUtils.SimpleCallback() {
                                        @Override
                                        public void onGranted() {
                                            takePhoto();
                                        }

                                        @Override
                                        public void onDenied() {
                                            takePhoto();
                                            Toast.makeText(getContext(), "No system alert window permission!", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .request();
                        }

                        @Override
                        public void onDenied() {
                            Toast.makeText(getContext(), "No camera permission!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .request();
        }
    }

    private void deletePhoto(File file, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                .setMessage("Sure to delete?")
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        boolean delete = file.delete();
                        if (delete) {
                            mAdapter.remove(position);
                            Toast.makeText(getContext(), "Image file deleted successfully!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Image file deleted failed!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    private void takePhoto() {
//        mCurrentImg = new File(mCurrentFile, System.currentTimeMillis() + ".jpg");
//        Uri uri = Uri.fromFile(mCurrentImg);
//        // 跳转
//        Intent fullIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        fullIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
//        fullIntent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//        startActivityForResult(fullIntent, REQ_FULL_SIZE_CAPTURE);
//        showMenu();

//        Uri uri = Uri.fromFile(mCurrentFile);
//        mTakePhoto.onPickFromCapture(uri);

        EasyPhotos.createAlbum(this, true, GlideEngine.getInstance())
                .setFileProviderAuthority(FILE_PROVIDER_AUTHORITY)//参数说明：见下方`FileProvider的配置`
                .setCount(9)//参数说明：最大可选数，默认1
                .setPuzzleMenu(false)
                .start(REQUEST_CODE_ALBUM);

//        EasyPhotos.createCamera(this)//参数说明：上下文
//                .setFileProviderAuthority(FILE_PROVIDER_AUTHORITY)//参数说明：见下方`FileProvider的配置`
//                .start(REQUEST_CODE_CAMERA);

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
        } else if (requestCode == REQUEST_CODE_CAMERA) {
            if (resultCode == -1) {
                if (mCurrentImg.exists()) {
                    mAdapter.addData(mAdapter.getData().size() - 1, mCurrentImg);
                    takePhoto();
                } else {
                    WindowUtils.hidePopupWindow();
                }
            } else {
                WindowUtils.hidePopupWindow();
            }
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinder.unbind();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        mMenuList = new ArrayList<>();
        mMenuList.add("Album");
        mMenuList.add("Camera");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
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
                if (files != null && files.length > 0) {
                    List<String> fileNames = new ArrayList<>();
                    for (int i = 0; i < files.length; i++) {
                        String name = files[i].getName();
                        if (name.contains("ID=")) {
                            String[] checkPathName = name.split(" \\(ID=");
                            if (checkPathName != null && checkPathName.length > 1) {
                                fileNames.add(checkPathName[0]);
                            }
                        } else {
                            fileNames.add(name);
                        }
                    }
                    DialogUtils.singleSelectDialog(fileNames, getActivity(), new DialogUtils.ItemSelectListener() {
                        @Override
                        public void onSelect(int position) {
                            loadCheckPath(files[position]);
                        }
                    });
                }
                break;
        }
    }

//    @Override
//    public void takeSuccess(TResult result) {
//        super.takeSuccess(result);
//        ArrayList<TImage> images = result.getImages();
//        for (TImage tImage : images) {
//            String originalPath = tImage.getOriginalPath();
//            File srcFile = new File(originalPath);
//            if (srcFile.exists()) {
//                File destFile = new File(mCurrentFile, System.currentTimeMillis() + ".jpg");
//                boolean copyFile = FileUtils.copyFile(srcFile, destFile);
//                if (copyFile) {
//                    mAdapter.addData(mAdapter.getData().size() - 1, destFile);
//                }
//            }
//        }
//
//    }
//
//    @Override
//    public void takeCancel() {
//        super.takeCancel();
//    }

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
                if (pathName.contains("ID=")) {
                    String[] checkPathName = split[split.length - 1].split(" \\(ID=");
                    if (checkPathName != null && checkPathName.length > 1) {
                        mPathImgTv.setText(new StringBuilder().append(split[0]).append(" > > ").append(checkPathName[0]).toString());
                    }
                } else {
                    mPathImgTv.setText(new StringBuilder().append(split[0]).append(" > > ").append(split[split.length - 1]).toString());
                }
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
