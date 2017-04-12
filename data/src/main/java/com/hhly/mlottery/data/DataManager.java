package com.hhly.mlottery.data;

import android.content.Context;

import com.hhly.mlottery.data.repository.BasketIndexReposeitory;

import javax.inject.Inject;

/**
 * @author: Wangg
 * @name：xxx
 * @description: xxx
 * @created on:2017/4/8  14:26.
 */

public class DataManager {

    @Inject
    public BasketIndexReposeitory basketIndexReposeitory;   //在DataModule中已经被注入，dataManager里面可以直接使用

    public DataManager(Context context, String apiHostUrl, String timeZone, String lang) {

        DaggerDataComponent.builder()
                .dataModule(new DataModule(context, apiHostUrl, timeZone, lang))
                .build()
                .inject(this);


    }
}
