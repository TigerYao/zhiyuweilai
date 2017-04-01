package com.zhiyuweilai.tiger.robotbook.view;

import com.zhiyuweilai.tiger.robotbook.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

/**
 * Created by yaohu on 2017/4/1.
 */

public class TitleBarView extends FrameLayout {
    public TitleBarView(Context context) {
        this(context,null);
    }

    public TitleBarView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public TitleBarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        View.inflate(getContext(), R.layout.titlebar_view, this);
    }

}
