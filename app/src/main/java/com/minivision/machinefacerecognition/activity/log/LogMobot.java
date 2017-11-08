package com.minivision.machinefacerecognition.activity.log;

import android.util.Log;

/**
 * Created by Administrator on 2017/8/14 0014.
 */

public class LogMobot {
    static String TAG = "LogMobot";

    public static void logd(String content) {
        if (content != null)
            Log.d(TAG, content);
    }

    public static void loge(String content) {
        if (content != null)
            Log.e(TAG, content);
    }

    public static void logw(String content) {
        if (content != null)
            Log.w(TAG, content);
    }
}
