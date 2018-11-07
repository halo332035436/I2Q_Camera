package com.symbio.i2qcamera.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.symbio.i2qcamera.R;
import com.symbio.i2qcamera.app.Config;
import com.symbio.i2qcamera.base.BaseFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class EmptyFragment extends BaseFragment {

    @BindView(R.id.tips_empty_tv)
    TextView tipsEmptyView;
    @BindView(R.id.quit_empty_btn)
    Button quitEmptyView;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView textView = view.findViewById(R.id.tips_empty_tv);
        textView.setText(Config.EMPTY_DIR_TEXT);
    }

    public static Fragment newInstance() {
        Fragment fragment = new EmptyFragment();
        return fragment;
    }

    @Override
    public int getLayoutResID() {
        return R.layout.view_empty;
    }

    @OnClick(R.id.quit_empty_btn)
    public void onViewClicked() {
        getActivity().finish();
    }
}
