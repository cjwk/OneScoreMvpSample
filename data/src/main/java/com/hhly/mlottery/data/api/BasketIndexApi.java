package com.hhly.mlottery.data.api;

import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * @author: Wangg
 * @name：xxx
 * @description: xxx
 * @created on:2017/4/6  17:34.
 */

public interface BasketIndexApi {
    //篮球指数列表
    @POST("mlottery/core/basketballMatch.findIndexList.do")
    Observable<BasketIndexApi> getIndexList(@Query("lang") String lang, @Query("timeZone") String timeZone, @Query("date") String date, @Query("type") String type, @Query("appType") String appType);

}
