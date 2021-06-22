package com.dyf.andriod_frontend;

import android.app.Application;

//import androidx.multidex.MultiDex;

public class App  extends Application {
    private static App mApp;

    @Override
    public void onCreate() {
        super.onCreate();
        mApp = this;
        // 初始化MultiDex
//        MultiDex.install(this);
    }

    public static App getApp() {
        return mApp;
    }
}
