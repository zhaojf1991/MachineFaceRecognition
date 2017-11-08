package com.minivision;

import android.app.Application;

import com.minivision.machinefacerecognition.activity.utils.ResultMap2;
import com.minivision.machinefacerecognition.activity.utils.Voicer;

/**
 * Created by Zhaojf on 2017/9/1 0001.
 */

public class app extends Application {
    String intervalValue;

    @Override
    public void onCreate() {
        super.onCreate();
        Crash instance = Crash.getInstance();
        instance.init(this);
        ResultMap2.init();
        Voicer.init(getApplicationContext());
    }


}
