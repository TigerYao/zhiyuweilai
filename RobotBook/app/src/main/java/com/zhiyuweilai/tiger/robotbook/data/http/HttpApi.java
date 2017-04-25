package com.zhiyuweilai.tiger.robotbook.data.http;


import com.zhiyuweilai.tiger.robotbook.data.ArticleService;
import com.zhiyuweilai.tiger.robotbook.data.BaseSubscribe;
import com.zhiyuweilai.tiger.robotbook.data.RetrofitUtils;
import com.zhiyuweilai.tiger.robotbook.model.BaseModel;
import com.zhiyuweilai.tiger.robotbook.model.ResponseModel;
import com.zhiyuweilai.tiger.robotbook.model.TestRquest;

import junit.framework.TestResult;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by yaohu on 2017/4/20.
 */

public class HttpApi extends RetrofitUtils {

    private ArticleService service = RetrofitUtils.getInstance().createService(ArticleService.class);
    private static HttpApi instance= null;

    private HttpApi() {
    }

    public static HttpApi getInstance() {
        if(instance == null)
            instance = new HttpApi();
        return instance;
    }

    public Subscription getTestResult(String auth, TestRquest model, BaseSubscribe<ResponseModel> baseSubscribe){
        Observable<ResponseModel> observable= service.postTest(auth,model);
        return observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(baseSubscribe);
    }
}
