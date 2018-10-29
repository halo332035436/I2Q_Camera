package com.symbio.i2qcamera.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.symbio.i2qcamera.R;
import com.symbio.i2qcamera.adapter.AlbumFragmentAdapter;
import com.symbio.i2qcamera.data.ImgDeleteEvent;
import com.symbio.i2qcamera.fragment.AlbumFragment;
import com.symbio.i2qcamera.view.PhotoViewPager;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class AlbumActivity extends FragmentActivity {

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);
        ButterKnife.bind(this);
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
                AlertDialog.Builder builder = new AlertDialog.Builder(this)
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
                                    Toast.makeText(AlbumActivity.this, "Image file deleted successfully!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(AlbumActivity.this, "Image file deleted failed!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
                break;
        }
    }
}
