package com.hhly.mlottery.data;

import java.io.IOException;

import okhttp3.Request;
import okhttp3.Response;

/**
 * @author: Wangg
 * @name：xxx
 * @description: xxx
 * @created on:2017/4/8  17:49.
 */

public class Interceptor implements okhttp3.Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {

        Request request = chain.request();
        Request.Builder builder = request.newBuilder()

        request = builder.build();
        return chain.proceed(request);


        return null;
    }
}
