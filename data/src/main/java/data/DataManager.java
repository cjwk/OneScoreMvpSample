package data;

import android.content.Context;

import javax.inject.Inject;

import data.repository.Repository;

/**
 * @author: Wangg
 * @name：xxx
 * @description: xxx
 * @created on:2017/6/5  17:24.
 */

public class DataManager {

    @Inject
    public Repository repository;   //在DataModule中已经被注入，dataManager里面可以直接使用

    public DataManager(Context context, String apiHostUrl, String timeZone, String lang) {

        DaggerDataComponent.builder()
                .dataModule(new DataModule(context, apiHostUrl, timeZone, lang))
                .build()
                .inject(this);


    }
}