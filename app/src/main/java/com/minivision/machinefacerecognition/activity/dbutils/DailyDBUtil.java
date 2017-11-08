package com.minivision.machinefacerecognition.activity.dbutils;

import android.content.Context;

import com.minivision.machinefacerecognition.activity.dailydb.PersonDailyDao;
import com.minivision.machinefacerecognition.activity.db.DailyDaoManager;
import com.minivision.machinefacerecognition.activity.entity.PersonDaily;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

/**
 * Created by Zhaojf on 2017/8/14 0014.
 */

public class DailyDBUtil {
    private DailyDaoManager daoManager;
    private Context context;


    public DailyDBUtil(Context context) {
        daoManager = DailyDaoManager.getInstance();
        this.context = context;
    }

    /*
       *
       * */
    public boolean insert(PersonDaily personDaily) {
        List<PersonDaily> personDailies = queryAll();
        long id;
        long idmax = 0;
        for (PersonDaily p : personDailies) {
            id = p.get_dailyid();
            if (idmax < id) {
                idmax = id;
            }
        }
        personDaily.set_dailyid(idmax + 1);
        long insert = daoManager.getDaoSession(context).getPersonDailyDao().insert(personDaily);
        return insert == -1 ? false : true;
    }

    //查询
    public List<PersonDaily> queryAll() {
        List<PersonDaily> entities = daoManager.getDaoSession(context).loadAll(PersonDaily.class);
        for (PersonDaily p : entities) {
//            LogMobot.logd(p.get_dailyid() + "--- id " + p.getName() + "-" + p.getNum() + "-" + p.getTakeTime());
        }
        return entities;
    }

    //条件查询
    public boolean query(String value) {
        List<PersonDaily> entities = daoManager.getDaoSession(context).queryRaw(PersonDaily.class, "where name = ?", new String[]{value});
        for (int i = 0; i < entities.size(); i++) {
//            LogMobot.logd(entities.get(i).getName());
        }
        if (entities.size() > 0) {
            return true;
        } else {
            return false;
        }

    }


    public List<PersonDaily> queryByNameAndTime(String name, String startTime, String endTime) {
        QueryBuilder<PersonDaily> personFacesQueryBuilder = daoManager.getDaoSession(context).queryBuilder(PersonDaily.class);
        personFacesQueryBuilder.where(PersonDailyDao.Properties.Name.eq(name), PersonDailyDao.Properties.TakeTime.between(startTime, endTime));
        return personFacesQueryBuilder.list();
    }

    public List<PersonDaily> queryByTime(String startTime, String endTime) {
        QueryBuilder<PersonDaily> personFacesQueryBuilder = daoManager.getDaoSession(context).queryBuilder(PersonDaily.class);
        personFacesQueryBuilder.where(PersonDailyDao.Properties.TakeTime.between(startTime, endTime));
        return personFacesQueryBuilder.list();
    }

    public List<PersonDaily> queryByName(String name) {
        QueryBuilder<PersonDaily> personFacesQueryBuilder = daoManager.getDaoSession(context).queryBuilder(PersonDaily.class);
        personFacesQueryBuilder.where(PersonDailyDao.Properties.Name.eq(name));
        return personFacesQueryBuilder.list();
    }

    public List<PersonDaily> queryBystTime(String startTime) {
        QueryBuilder<PersonDaily> personFacesQueryBuilder = daoManager.getDaoSession(context).queryBuilder(PersonDaily.class);
        personFacesQueryBuilder.where(PersonDailyDao.Properties.TakeTime.ge(startTime));
        return personFacesQueryBuilder.list();
    }
    public List<PersonDaily> queryByetTime(String startTime) {
        QueryBuilder<PersonDaily> personFacesQueryBuilder = daoManager.getDaoSession(context).queryBuilder(PersonDaily.class);
        personFacesQueryBuilder.where(PersonDailyDao.Properties.TakeTime.le(startTime));
        return personFacesQueryBuilder.list();
    }

    //删除全部
    public void deleteAll() {
        daoManager.getDaoSession(context).getPersonDailyDao().deleteAll();
    }


    public void deletDailyByTime(String name, String time) {
        List<PersonDaily> personDailies = queryDailyByNameAndTime(name, time);
        for (PersonDaily p : personDailies) {
            daoManager.getDaoSession(context).getPersonDailyDao().delete(p);
        }

    }

    public List<PersonDaily> queryDailyByNameAndTime(String name, String time) {
        QueryBuilder<PersonDaily> personFacesQueryBuilder = daoManager.getDaoSession(context).queryBuilder(PersonDaily.class);
        personFacesQueryBuilder.where(PersonDailyDao.Properties.Name.eq(name), PersonDailyDao.Properties.TakeTime.eq(time));
        return personFacesQueryBuilder.list();
    }


}
