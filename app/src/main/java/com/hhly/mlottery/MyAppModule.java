package com.hhly.mlottery;

import com.hhly.mlottery.data.DataManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * @author: Wangg
 * @name：xxx
 * @description: xxx
 * @created on:2017/4/8  15:23.
 */

@Module
public class MyAppModule {

    private final MyApp myApp;

    private final String mHostUrl;

    public MyAppModule(MyApp myApp, String mHostUrl) {
        this.myApp = myApp;
        this.mHostUrl = mHostUrl;
    }

    @Provides
    @Singleton
    DataManager provideDataManager() {
        return new DataManager(myApp, mHostUrl);
    }
}
