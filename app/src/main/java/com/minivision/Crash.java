package com.minivision;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import static android.content.ContentValues.TAG;

/**
 * Created by Zhaojf on 2017/9/11 0011.
 */

public class Crash implements Thread.UncaughtExceptionHandler {
    static Crash mCrash = null;
    private Context mcontext;
    String PathName = "/CrashLog/";

    String fileName = "crash.txt";

    public Crash() {

    }

    public static Crash getInstance() {
        mCrash = new Crash();
        return mCrash;
    }

    public void init(Context context) {
        Thread.setDefaultUncaughtExceptionHandler(this);
        mcontext = context.getApplicationContext();
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        // 提供方法 printStackTrace(PrintStream s)
        Log.e(TAG, "uncaughtException: "+e.toString());
        save2SD(e);
    }

    public void save2SD(Throwable throwable) {
        //判断sd卡 是否加载
        String path;
        if (Environment.isExternalStorageRemovable()) {
            Log.e(TAG, "save2SD: SD IS REMOVE");
            return;
        }
        //保存路径
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            path = mcontext.getExternalCacheDir().getPath();
        } else {
            path = mcontext.getCacheDir().getPath();
        }
        File file = new File("sdcard" + PathName);
        Toast.makeText(mcontext, "save2SD: " + "/sdcard/" + PathName, Toast.LENGTH_SHORT).show();
        Log.e(TAG, "save2SD: " + path + PathName);

        if (!file.exists()) {
            file.mkdir();
        }

        File files = new File("/sdcard/" + PathName + fileName);
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(files);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        PrintStream ps = new PrintStream(fileOutputStream);
        //保存提供的方法，可看源码
        throwable.printStackTrace(ps);
        ps.close();
    }
}


