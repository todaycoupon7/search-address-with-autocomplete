package com.sample.todaycoupon7.addresssearch;

import android.app.Application;

/**
 * Created by chaesooyang on 2018. 2. 1..
 */

public class AppMain extends Application {

    private static String mKakaoKey;

    @Override
    public void onCreate() {
        super.onCreate();

        mKakaoKey = getString(R.string.daum_app_key);
    }

    public static String getKakaoKey() {
        return mKakaoKey;
    }

}
