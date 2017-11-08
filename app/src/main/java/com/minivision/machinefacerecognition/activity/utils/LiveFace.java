package com.minivision.machinefacerecognition.activity.utils;

import android.content.Context;

import com.minivision.livebody.Livingbody;
import com.minivision.livebody.LivingbodyConfig;
import com.minivision.machinefacerecognition.activity.log.LogMobot;

/**
 * Created by Zhaojf on 2017/10/19 0019.
 */

public class LiveFace {
    private static Livingbody livingbody;

    public static Livingbody getInstance() {
        if (livingbody == null) {
            synchronized (LiveFace.class) {
                livingbody = new Livingbody();
                LogMobot.logd("livingbody create");
            }
        }
        return livingbody;
    }

    public int initLivingbody(Context context, LivingbodyConfig config) {
        LogMobot.logd("livingbody init1");
        int res = livingbody.init(context, config);
        LogMobot.logd("livingbody init");
        LogMobot.logd("livingbody res"+res);
        return res;
    }

}
