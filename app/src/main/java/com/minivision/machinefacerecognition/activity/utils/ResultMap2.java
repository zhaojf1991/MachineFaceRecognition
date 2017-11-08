package com.minivision.machinefacerecognition.activity.utils;

import android.util.Log;

import com.minivision.machinefacerecognition.activity.log.LogMobot;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by yuan_hao on 2017/2/15.
 * Email:yuanhao@minivision.cn
 */
public class ResultMap2 {

    private static final String TAG = ResultMap2.class.getSimpleName();

    private Map<Long, String> mResults;

    private static Timer mTimer;
    private int interval;
    private static ResultMap2 INSTANCE;

    public ResultMap2() {
        mTimer = new Timer();
        mResults = new ConcurrentHashMap<>();
        LogMobot.logd("识别间隔..." + interval);
    }

    public static ResultMap2 init() {
        Log.d(TAG, "ResultMap.init");
        if (INSTANCE == null) {
            synchronized (ResultMap2.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ResultMap2();
                }
            }
        }
        return INSTANCE;
    }

    public static ResultMap2 instance() {
        if (INSTANCE == null) {
            throw new RuntimeException("resultmap not init.");
        }
        return INSTANCE;
    }


    public void add(final String name, int limtime) {
//        long time1 = System.currentTimeMillis();

        if (contains(name)) {
            Log.d(TAG, "已存在的元素,添加失败..." + name);
//            LogMobot.logd("contains time  = " + (System.currentTimeMillis() - time1));
            return;
        }
        long time = System.currentTimeMillis();

        mResults.put(time, name);
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                remove(name);
            }
        }, limtime * 1000);
        Log.d(TAG, "添加元素..." + name);
    }

    public void remove(String resultOfRecognize) {
        mResults.values().remove(resultOfRecognize);
        Log.d(TAG, "时间结束，移除元素..." + resultOfRecognize);
    }

    public void clear() {
        mResults.clear();
        mTimer.cancel();
        mTimer.purge();
        mTimer = null;
    }

    public boolean contains(String name) {
        return mResults.containsValue(name);
    }

}
