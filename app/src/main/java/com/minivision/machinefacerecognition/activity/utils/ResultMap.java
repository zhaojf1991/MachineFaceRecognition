package com.minivision.machinefacerecognition.activity.utils;

import android.util.Log;

import com.minivision.face.recognize.RecognizedFace;
import com.minivision.machinefacerecognition.activity.log.LogMobot;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by yuan_hao on 2017/2/15.
 * Email:yuanhao@minivision.cn
 */
public class ResultMap {

    private static final String TAG = ResultMap.class.getSimpleName();

    private Map<Long, RecognizedFace> mResults;

    private static Timer mTimer;
    private int interval;
    private static ResultMap INSTANCE;

    public ResultMap(int interval) {
        mTimer = new Timer();
        mResults = new ConcurrentHashMap<>();
        this.interval = interval;
        LogMobot.logd("识别间隔..." + interval);
    }

    public static ResultMap init(int interval) {
        Log.d(TAG, "ResultMap.init");
        if (INSTANCE == null) {
            synchronized (ResultMap.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ResultMap(interval);
                    Log.d(TAG, "ResultMap.init" + interval);
                }
            }
        }
        return INSTANCE;
    }

    public static ResultMap instance() {
        if (INSTANCE == null) {
            throw new RuntimeException("resultmap not init.");
        }
        return INSTANCE;
    }


    public void add(final RecognizedFace resultOfRecognize) {
//        long time1 = System.currentTimeMillis();

        if (contains(resultOfRecognize)) {
            Log.d(TAG, "已存在的元素,添加失败..." + resultOfRecognize.getId());
//            LogMobot.logd("contains time  = " + (System.currentTimeMillis() - time1));
            return;
        }
        long time = System.currentTimeMillis();

        mResults.put(time, resultOfRecognize);
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                remove(resultOfRecognize);
            }
        }, interval * 1000);
        Log.d(TAG, "添加元素..." + resultOfRecognize.getId());
    }

    public void remove(RecognizedFace resultOfRecognize) {
        mResults.values().remove(resultOfRecognize);
        Log.d(TAG, "时间结束，移除元素..." + resultOfRecognize.getId());
    }

    public void clear() {
        mResults.clear();
        mTimer.cancel();
        mTimer.purge();
        mTimer = null;
    }

    public boolean contains(RecognizedFace resultOfRecognize) {
        return mResults.containsValue(resultOfRecognize);
    }

}
