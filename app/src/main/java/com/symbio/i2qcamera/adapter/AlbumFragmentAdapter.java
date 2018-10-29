package com.symbio.i2qcamera.adapter;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class AlbumFragmentAdapter extends FragmentStatePagerAdapter {

    private List<Fragment> mFragments = new ArrayList<>();

    public AlbumFragmentAdapter(FragmentManager fm, List<Fragment> fragments) {
        super(fm);
        mFragments.clear();
        mFragments.addAll(fragments);
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return PagerAdapter.POSITION_NONE;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }
}
