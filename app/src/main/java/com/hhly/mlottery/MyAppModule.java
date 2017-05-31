package com.hhly.mlottery;


import com.hhly.mlottery.frame.footballframe.mvptask.data.DataManager;

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

    private final String timeZone;

    private final String lang;

    public MyAppModule(MyApp myApp, String mHostUrl, String timeZone, String lang) {
        this.myApp = myApp;
        this.mHostUrl = mHostUrl;
        this.timeZone = timeZone;
        this.lang = lang;
    }

    @Provides
    @Singleton
    DataManager provideDataManager() {
        return new DataManager(myApp, mHostUrl, timeZone, lang);
    }
}
