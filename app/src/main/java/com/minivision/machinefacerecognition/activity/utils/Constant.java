package com.minivision.machinefacerecognition.activity.utils;

import android.os.Environment;

/**
 * Created by yuan_hao on 2017/6/28.
 * Email:yuanhao@minivision.cn
 */
public class Constant {

    public static final String SHARED_PREFS_NAME = "minivision_door";

    public static final int ALGORITHM_RATIO = 2;

    public static final int MESSAGE_RECOGNIZE = 1;

    public static final int TIMEOVER = 2;

    //资源文件相关
    public static final String SD_CARD_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
    public static final String APPLICATION_DIR = SD_CARD_PATH + "/Minivision/MachineRecognition";
    public static final String PICTURE_DIR = APPLICATION_DIR + "/ImageOfLibrary";
    public static final String RESOURCE_DIR = APPLICATION_DIR + "/Resource";
    public static final String RESULT_DIR = APPLICATION_DIR + "/Result";
    public static final String FEATURES_PATH = RESULT_DIR + "/picLib.xml";
    public static final String PIC = SD_CARD_PATH + "/pic";

    public static final String LICENCE_FILE_NAME = "minilcs";


    public static final String SHOWPIC = SD_CARD_PATH + "/showpic";
    public static final String LSMPP_FILE_NAME = "lsmpp";
    public static final String MODEL_FILE_NAME = "mdlnt";
    public static final String RES_FILE_NAME_1 = "minires";
    public static final String RES_FILE_NAME_2 = "minires2";
    public static final String RES_FILE_NAME_3 = "minires3";
    public static final String RES_FILE_NAME_4 = "minires4";
    public static final String RES_FILE_NAME_5 = "minires5";

    public static final String THRESHOLD_FILE_NAME = "Threshold.xml";
    public static final String TAKEPIC_PATCH = SD_CARD_PATH + "/takePic";
}
