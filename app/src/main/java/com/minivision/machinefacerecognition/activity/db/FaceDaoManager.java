package com.minivision.machinefacerecognition.activity.db;

import android.content.Context;

import com.minivision.machinefacerecognition.activity.dailydb.DaoMaster;
import com.minivision.machinefacerecognition.activity.dailydb.DaoSession;

import org.greenrobot.greendao.query.QueryBuilder;

/**
 * Created by Zhaojf on 2017/8/14 0014.
 */

public class FaceDaoManager {
    private volatile static FaceDaoManager manager;
    private static DaoMaster daoMaster;
    private static DaoMaster.DevOpenHelper openHelper;
    private static DaoSession daoSession;
    private final static String NAME_DB = "face";


    public static FaceDaoManager getInstance() {
        if (manager == null) {
            synchronized (FaceDaoManager.class) {
                if (manager == null) {
                    manager = new FaceDaoManager();
                }
            }
        }
        return manager;
    }

    //DaoMaster数据库连接
    public DaoMaster getDaoMaster(Context context) {
        if (daoMaster == null) {
            DaoMaster.DevOpenHelper devOpenHelper = new DaoMaster.DevOpenHelper(context, NAME_DB, null);
            daoMaster = new DaoMaster(devOpenHelper.getWritableDatabase());
        }
        return daoMaster;
    }

    //数据库操作
    public DaoSession getDaoSession(Context context) {
        if (daoSession == null) {
            if (daoMaster == null) {
                daoMaster = getDaoMaster(context);
                daoSession = daoMaster.newSession();
            }
        }
        return daoSession;
    }


    public void setLog() {
        QueryBuilder.LOG_SQL = true;
        QueryBuilder.LOG_VALUES = true;
    }

    public void closeDataBase() {
        closeHelp();
        closeDaoSession();
    }

    public void closeHelp() {
        if (openHelper != null) {
            openHelper.close();
            openHelper = null;
        }
    }

    public void closeDaoSession() {
        if (daoSession != null) {
            daoSession.clear();
            daoSession = null;
        }
    }

}
