package com.symbio.i2qcamera.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.symbio.i2qcamera.R;
import com.symbio.i2qcamera.base.BaseFragment;
import com.symbio.i2qcamera.ui.activity.MainActivity;
import com.symbio.i2qcamera.ui.adapter.FolderListAdapter;
import com.symbio.i2qcamera.app.Config;
import com.symbio.i2qcamera.data.FolderRefreshEvent;
import com.symbio.i2qcamera.util.CommonUtil;
import com.symbio.i2qcamera.util.DialogUtils;

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

public class FolderFragment extends BaseFragment {

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
    @BindView(R.id.filter_folder_iv)
    ImageView mFilterFolderIv;
    private File[] mJobs;
    private String currentDir = Config.BASE_PATH;
    private FolderListAdapter mAdapter;
    private String filterWord = "";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        File file = new File(Config.BASE_PATH);
        mJobs = file.listFiles();
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
                        DialogUtils.showNoOrYesDialog(getActivity(), "Sure to delete?",
                                (dialog, index) -> dialog.dismiss(),
                                (dialog, index) -> {
                                    File file1 = (File) adapter.getData().get(position);
                                    boolean delete = file1.delete();
                                    if (delete) {
                                        mAdapter.remove(position);
                                        DialogUtils.showSuccessTipDialog(getContext(), view,
                                                "Folder deleted successfully!");
                                    } else {
                                        DialogUtils.showFailTipDialog(getContext(), view,
                                                "Folder deleted failed!");
                                    }
                                    dialog.dismiss();
                                });
                    }

                }
            });
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public static Fragment newInstance() {
        Fragment fragment = new FolderFragment();
        return fragment;
    }

    @Override
    public int getLayoutResID() {
        return R.layout.view_folder;
    }

    private void changeTitle() {
        if (isBasePath()) {
            numberFolderTv.setVisibility(View.VISIBLE);
            mFilterFolderIv.setVisibility(View.VISIBLE);
            mTitleFolderLayout.setVisibility(View.INVISIBLE);
        } else {
            numberFolderTv.setVisibility(View.INVISIBLE);
            mFilterFolderIv.setVisibility(View.INVISIBLE);
            mTitleFolderLayout.setVisibility(View.VISIBLE);
        }
    }

    private boolean isBasePath() {
        return currentDir.equals(Config.BASE_PATH);
    }

    @OnClick({R.id.back_folder_iv, R.id.filter_folder_iv})
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
            case R.id.filter_folder_iv:
                final QMUIDialog.EditTextDialogBuilder builder = new QMUIDialog.EditTextDialogBuilder(getActivity());
                builder.setTitle("Folder Filter")
                        .setPlaceholder("Enter keywords here~")
                        .setInputType(InputType.TYPE_CLASS_TEXT)
                        .addAction("reset", new QMUIDialogAction.ActionListener() {
                            @Override
                            public void onClick(QMUIDialog dialog, int index) {
                                filterWord = "";
                                File file = new File(currentDir);
                                mJobs = file.listFiles();
                                numberFolderTv.setText("You have " + mJobs.length + " Jobs:");
                                refreshFileList(file, mJobs);
                                dialog.dismiss();
                            }
                        })
                        .addAction("confirm", new QMUIDialogAction.ActionListener() {
                            @Override
                            public void onClick(QMUIDialog dialog, int index) {
                                CharSequence text = builder.getEditText().getText();
                                filterWord = String.valueOf(text);
                                File file = new File(currentDir);
                                mJobs = file.listFiles(new FilenameFilter() {
                                    @Override
                                    public boolean accept(File dir, String name) {
                                        return name.toUpperCase().contains(filterWord.toUpperCase());
                                    }
                                });
                                numberFolderTv.setText("You have " + mJobs.length + " Jobs:");
                                refreshFileList(file, mJobs);
                                dialog.dismiss();
                            }
                        })
                        .create(com.qmuiteam.qmui.R.style.QMUI_Dialog).show();
                builder.getEditText().setText(filterWord);
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
