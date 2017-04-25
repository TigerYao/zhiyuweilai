package com.zhiyuweilai.tiger.robotbook.data;

import java.util.Map;

import com.zhiyuweilai.tiger.robotbook.model.BaseModel;
import com.zhiyuweilai.tiger.robotbook.model.ResponseModel;

import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by yaohu on 2017/4/15.
 */

public interface ArticleService {
    @POST("wdm-job-selfitems")
    Observable<ResponseModel> postTest(@Header("Authorization") String author, @Body BaseModel model);
}
