package com.zhiyuweilai.tiger.robotbook.act;

import com.zhiyuweilai.tiger.robotbook.R;
import com.zhiyuweilai.tiger.robotbook.databinding.ActivityHomeBinding;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;

import cn.robotpen.act.connect.BleConnectActivity;
import cn.robotpen.act.show.ShowPointActivity;

public class HomeActivity extends BaseAcitivty {
    private ActivityHomeBinding mHomeBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHomeBinding = DataBindingUtil.setContentView(this, R.layout.activity_home);
        mHomeBinding.titlebar.setRightTitle(R.string.device_state);
        mHomeBinding.tablayout.addTab(mHomeBinding.tablayout.newTab().setText(R.string.my_diary));
        mHomeBinding.tablayout.addTab(mHomeBinding.tablayout.newTab().setText(R.string.my_article));
        mHomeBinding.tablayout.addTab(mHomeBinding.tablayout.newTab().setText(R.string.my_class));
        mHomeBinding.tablayout.addTab(mHomeBinding.tablayout.newTab().setText(R.string.bookcase));
        MainPagerAdapter adapter = new MainPagerAdapter(getSupportFragmentManager(), mHomeBinding.tablayout);
        mHomeBinding.tablayout.setupWithViewPager(mHomeBinding.viewpager);
        mHomeBinding.viewpager.setAdapter(adapter);
        mHomeBinding.floatActionbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, ShowPointActivity.class));
            }
        });
        mHomeBinding.titlebar.titlebarViewBinding.titleRightName1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, BleConnectActivity.class));
            }
        });
    }

}
