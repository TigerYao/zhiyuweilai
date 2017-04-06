package com.zhiyuweilai.tiger.robotbook.view;

import com.zhiyuweilai.tiger.robotbook.R;
import com.zhiyuweilai.tiger.robotbook.databinding.InputviewBinding;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.text.Editable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * Created by yaohu on 2017/4/1.
 */

public class InputViewLayout extends LinearLayout {
    public InputViewLayout(Context context) {
        super(context);
    }

    public InputViewLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public InputViewLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void addInputView(boolean edited, int hintId, int resId, boolean showDelImg, String tag) {
        InputItemInfo inputItemInfo = new InputItemInfo(edited, hintId, resId, showDelImg, tag);
        addView(inputItemInfo.getView());
    }

    public void addInputView(boolean showDelImg, String tag) {
        addInputView(true, 0, 0, showDelImg, tag);
    }

    class InputItemInfo {

        private InputviewBinding mInputviewBinding;

        public InputItemInfo(boolean edited, int hintId, int resId, boolean showDelImg, String tag) {
            mInputviewBinding = DataBindingUtil.bind(View.inflate(getContext(), R.layout.inputview, null));
            if (resId > 0)
                mInputviewBinding.img.setImageResource(resId);
            mInputviewBinding.delet.setVisibility(showDelImg ? View.VISIBLE : View.GONE);
            if (hintId > 0)
                mInputviewBinding.inputContent.setHint(hintId);
            mInputviewBinding.setEdit(edited);
            if (showDelImg) {
                mInputviewBinding.delet.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int index = mInputviewBinding.inputContent.getSelectionStart();
                        Editable editable = mInputviewBinding.inputContent.getText();
                        editable.delete(index - 1, index);
                    }
                });
            }
            mInputviewBinding.getRoot().setTag(tag);
        }

        public View getView() {
            return mInputviewBinding.getRoot();
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }
}
