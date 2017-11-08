package com.minivision.machinefacerecognition.activity.utils;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.Locale;

/**
 * Created by yuan_hao on 2017/6/29.
 * Email:yuanhao@minivision.cn
 * <p>
 * tts
 */
public class Voicer {

    private static final String TAG = Voicer.class.getSimpleName();
    private static volatile Voicer INSTANCE;

    private Voicer(Context context) {
        mTextToSpeech = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {

                    int result = mTextToSpeech.setLanguage(Locale.CHINESE);
                    if (result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.i(TAG, "Chinese is not supported");
                        if (mTextToSpeech != null) {
                            mTextToSpeech.stop();
                            mTextToSpeech.shutdown();
                            mTextToSpeech = null;
                        }
                    }
                }
            }
        });
    }

    public static void init(Context c) {
        if (INSTANCE == null) {
            synchronized (Voicer.class) {
                if (INSTANCE == null) {
                    INSTANCE = new Voicer(c);
                }
            }
        }
    }

    public static Voicer instance() {
        if (INSTANCE == null) {
            throw new RuntimeException("tts has not init");
        }
        return INSTANCE;
    }

    public static void destroy() {
        INSTANCE = null;
    }

    private TextToSpeech mTextToSpeech;

    public void speck(String content) {
        if (mTextToSpeech != null) {
            mTextToSpeech.speak(content, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    public void shutdown() {
        mTextToSpeech.shutdown();
    }
}
