package com.zhiyuweilai.tiger.robotbook.view;

import com.zhiyuweilai.tiger.robotbook.R;
import com.zhiyuweilai.tiger.robotbook.databinding.TitlebarViewBinding;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

public class TitleBarView extends FrameLayout {
    public TitlebarViewBinding titlebarViewBinding;

    public TitleBarView(Context context) {
        this(context, null);
    }

    public TitleBarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TitleBarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        titlebarViewBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.titlebar_view, this, true);
    }

    public void setTitle(int id) {
        if (id > 0)
            titlebarViewBinding.title.setVisibility(View.VISIBLE);
        titlebarViewBinding.title.setText(id);
    }

    public void setTitle(String str) {
        titlebarViewBinding.title.setVisibility(View.VISIBLE);
        titlebarViewBinding.title.setText(str);
    }

    public void setLeftImg(int id){
        titlebarViewBinding.titleLeftImg.setVisibility(View.VISIBLE);
        titlebarViewBinding.titleLeftImg.setImageResource(id);
    }

    public void setRightImg(int id){
        titlebarViewBinding.titleRightImg.setVisibility(View.VISIBLE);
        titlebarViewBinding.titleRightImg.setImageResource(id);
    }

    public void setRightTitle(int id) {
        titlebarViewBinding.titleRightName1.setVisibility(View.VISIBLE);
        titlebarViewBinding.titleRightName1.setText(id);
    }

}
