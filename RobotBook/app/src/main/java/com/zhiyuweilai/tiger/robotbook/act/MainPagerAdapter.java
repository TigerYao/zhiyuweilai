package com.zhiyuweilai.tiger.robotbook.act;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.zhiyuweilai.tiger.robotbook.mainview.HomeFragment;
import com.zhiyuweilai.tiger.robotbook.widget.TabLayout;

import java.util.HashMap;
import java.util.Map;

public class MainPagerAdapter extends FragmentStatePagerAdapter {
    private Map<String, HomeFragment> mFragments;
    private TabLayout mTablayout;

    public MainPagerAdapter(FragmentManager fm, TabLayout tabLayout) {
        super(fm);
        mFragments = new HashMap<String, HomeFragment>();
        this.mTablayout = tabLayout;
    }

    @Override
    public Fragment getItem(int position) {

        return HomeFragment.newInstance(mTablayout.getTabAt(position).getText().toString());
    }

    @Override
    public int getCount() {
        return mTablayout.getTabCount();
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

}