package com.microslt.parseapp;

import android.app.Application;

import com.parse.Parse;

import cn.jpush.android.api.JPushInterface;

/**
 * For developer startup JPush SDK
 * <p>
 * 一般建议在自定义 Application 类里初始化。也可以在主 Activity 里。
 */
public class ExampleApplication extends Application {
    private static final String TAG = "JIGUANG-Example";

    @Override
    public void onCreate() {
        Logger.d(TAG, "[ExampleApplication] onCreate");
        super.onCreate();

        String parseId = getString(R.string.parse_app_id);
        String parseUrl = getString(R.string.parse_server_url);

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(parseId)
                .server(parseUrl)
                .build()
        );

        JPushInterface.setDebugMode(true);    // 设置开启日志,发布时请关闭日志
        JPushInterface.init(this);            // 初始化 JPush
    }
}
