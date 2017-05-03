package com.zhiyuweilai.tiger.robotbook.data;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitUtils {

    private static final String BASEAPI = "http://139.129.202.11:8085/api/";
    private Retrofit retrofit;
    private String token;

    protected RetrofitUtils() {
        retrofit = new Retrofit.Builder().baseUrl(BASEAPI).addConverterFactory(GsonConverterFactory.create()).addCallAdapterFactory(RxJavaCallAdapterFactory.create()).client(genericClient()).build();
    }

    private volatile static RetrofitUtils instance = null;

    public static RetrofitUtils getInstance() {
        if (instance == null) {
            synchronized (RetrofitUtils.class) {
                if (instance == null) {
                    instance = new RetrofitUtils();
                }
            }
        }
        return instance;
    }

    public <T> T createService(Class<T> clz) {
        return retrofit.create(clz);
    }

    public  OkHttpClient genericClient() {

        OkHttpClient httpClient = new OkHttpClient.Builder()

                .addInterceptor(new Interceptor() {

                    @Override

                    public Response intercept(Chain chain) throws IOException {

                        Request request = chain.request()
                                .newBuilder()
                                .addHeader("Content-Type", "application/json")
                                .addHeader("Accept-Encoding", "gzip, deflate")
                                .addHeader("Authorization", "Bearer"+token)
                                .build();

                        return chain.proceed(request);

                    }
                }).build();
        return httpClient;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
