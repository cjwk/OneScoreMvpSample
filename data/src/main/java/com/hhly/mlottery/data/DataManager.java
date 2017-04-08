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
    public BasketIndexReposeitory basketIndexReposeitory;

    public DataManager(Context context, String apiHostUrl) {

        DaggerDataComponent.builder()
                .dataModule(new DataModule(context, apiHostUrl))
                .build()
                .inject(this);


    }
}
