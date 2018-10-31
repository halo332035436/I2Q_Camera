package com.symbio.i2qcamera.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.symbio.i2qcamera.R;
import com.symbio.i2qcamera.app.Config;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class EmptyFragment extends Fragment {

    @BindView(R.id.tips_empty_tv)
    TextView tipsEmptyView;
    @BindView(R.id.quit_empty_btn)
    Button quitEmptyView;
    private Unbinder mBinder;

    public static Fragment newInstance() {
        Fragment fragment = new EmptyFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.view_empty, null);
        TextView textView = view.findViewById(R.id.tips_empty_tv);
        textView.setText(Config.EMPTY_DIR_TEXT);
        mBinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinder.unbind();
    }

    @OnClick(R.id.quit_empty_btn)
    public void onViewClicked() {
        getActivity().finish();
    }
}
