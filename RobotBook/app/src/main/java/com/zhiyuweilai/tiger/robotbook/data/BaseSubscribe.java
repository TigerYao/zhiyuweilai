package com.zhiyuweilai.tiger.robotbook.data;

import android.util.Log;

import rx.Subscriber;

/**
 * Created by yaohu on 2017/4/20.
 */

public abstract class BaseSubscribe<T> extends Subscriber<T>{
    private static final String TAG = "BaseSubsribe";


    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "onStart");

    }

    @Override
    public void onNext(T t) {
        Log.i(TAG, "response" + t.toString());

        onSuccess(t);
    }

    @Override
    public void onCompleted() {
        Log.i(TAG, "onCompleted");

    }

    public abstract void onSuccess(T result);

    @Override
    public void onError(Throwable e) {
        e.printStackTrace();
        Log.i(TAG, "onError" + e.getMessage());

    }
}
