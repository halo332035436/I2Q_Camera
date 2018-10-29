package com.symbio.i2qcamera.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.symbio.i2qcamera.R;
import com.symbio.i2qcamera.activity.MainActivity;
import com.symbio.i2qcamera.adapter.FolderListAdapter;
import com.symbio.i2qcamera.base.Config;
import com.symbio.i2qcamera.data.FolderRefreshEvent;
import com.symbio.i2qcamera.util.CommonUtil;

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

public class FolderFragment extends Fragment {

    @BindView(R.id.number_folder_tv)
    TextView numberFolderTv;
    @BindView(R.id.content_folder_rv)
    RecyclerView contentFolderRv;
    @BindView(R.id.back_folder_iv)
    ImageView mBackFolderIv;
    @BindView(R.id.path_folder_tv)
    TextView mPathFolderTv;
    @BindView(R.id.title_folder_layout)
    LinearLayout mTitleFolderLayout;
    private Unbinder mBinder;
    private File[] mJobs;
    private String currentDir = Config.BASE_PATH;
    private FolderListAdapter mAdapter;

    public static Fragment newInstance() {
        Fragment fragment = new FolderFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.view_folder, null);
        mBinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        File file = new File(Config.BASE_PATH);
        mJobs = file.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.startsWith("JOB");
            }
        });
        if (mJobs != null && mJobs.length > 0) {
            numberFolderTv.setText("You have " + mJobs.length + " Jobs:");
            LinearLayoutManager manager = new LinearLayoutManager(getContext());
            contentFolderRv.setLayoutManager(manager);
            List<File> fileList = new ArrayList<>(Arrays.asList(mJobs));
            mAdapter = new FolderListAdapter(R.layout.item_folder, fileList);
            mAdapter.isFirstOnly(false);
            contentFolderRv.setAdapter(mAdapter);
            mAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
                @Override
                public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                    if (view.getId() == R.id.item_folder_layout) {
                        File file = (File) adapter.getData().get(position);
                        if (file.isDirectory()) {
                            if (CommonUtil.isNeedAddIMG(file)) {
                                MainActivity activity = (MainActivity) getActivity();
                                activity.showImgView(file);
                            } else {
                                File[] files = file.listFiles(new FileFilter() {
                                    @Override
                                    public boolean accept(File pathname) {
                                        return pathname.isDirectory();
                                    }
                                });
                                if (file != null && file.length() > 0) {
                                    mAdapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_RIGHT);
                                    refreshFileList(file, files);
                                }
                            }
                        }
                    } else if (view.getId() == R.id.folder_delete_btn) {
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
                                        File file = (File) adapter.getData().get(position);
                                        boolean delete = file.delete();
                                        if (delete) {
                                            mAdapter.remove(position);
                                            Toast.makeText(getContext(), "Folder deleted successfully!", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(getContext(), "Folder deleted failed!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                        AlertDialog dialog = builder.create();
                        dialog.setCanceledOnTouchOutside(false);
                        dialog.show();
                    }

                }
            });
        }
    }

    private void changeTitle() {
        if (isBasePath()) {
            numberFolderTv.setVisibility(View.VISIBLE);
            mTitleFolderLayout.setVisibility(View.INVISIBLE);
        } else {
            numberFolderTv.setVisibility(View.INVISIBLE);
            mTitleFolderLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinder.unbind();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private boolean isBasePath() {
        return currentDir.equals(Config.BASE_PATH);
    }

    @OnClick({R.id.back_folder_iv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back_folder_iv:
                File parentFile = new File(currentDir).getParentFile();
                File[] files = parentFile.listFiles(new FileFilter() {
                    @Override
                    public boolean accept(File pathname) {
                        return pathname.isDirectory();
                    }
                });
                if (files != null && files.length > 0) {
                    refreshFileList(parentFile, files);
                    mAdapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_LEFT);
                }
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(FolderRefreshEvent event) {
        File parentFile = new File(currentDir);
        File[] files = parentFile.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isDirectory();
            }
        });
        if (files != null && files.length > 0) {
            refreshFileList(parentFile, files);
            mAdapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
        }

    }

    private void refreshFileList(File parentFile, File[] files) {
        List<File> childDir = new ArrayList<>(Arrays.asList(files));
        mAdapter.getData().clear();
        mAdapter.getData().addAll(childDir);
        mAdapter.notifyDataSetChanged();
        currentDir = parentFile.getAbsolutePath();
        changeTitle();
        String[] jobs = parentFile.getAbsolutePath().split("/Jobs/");
        if (jobs != null && jobs.length >= 2) {
            mPathFolderTv.setText(jobs[1].replace("/", " > "));
        }
    }

}
