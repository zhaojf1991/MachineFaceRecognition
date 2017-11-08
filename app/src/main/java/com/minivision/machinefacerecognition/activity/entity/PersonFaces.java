package com.minivision.machinefacerecognition.activity.entity;

import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by Zhaojf on 2017/8/14 0014.
 */
@org.greenrobot.greendao.annotation.Entity
public class PersonFaces {
    @Id(autoincrement = true)//自增
            long _id;
    String name;
    String num;
    String phoneNum;
    String time;
    String showPicPath;
    String picPath;
    String sex;
    String work;
    String limtStarttime;

    public String getLimtStarttime() {
        return limtStarttime;
    }

    public void setLimtStarttime(String limtStarttime) {
        this.limtStarttime = limtStarttime;
    }

    public String getLimtEndtime() {
        return limtEndtime;
    }

    public void setLimtEndtime(String limtEndtime) {
        this.limtEndtime = limtEndtime;
    }

    String limtEndtime;


    @Generated(hash = 222474120)
    public PersonFaces(long _id, String name, String num, String phoneNum,
            String time, String showPicPath, String picPath, String sex,
            String work, String limtStarttime, String limtEndtime) {
        this._id = _id;
        this.name = name;
        this.num = num;
        this.phoneNum = phoneNum;
        this.time = time;
        this.showPicPath = showPicPath;
        this.picPath = picPath;
        this.sex = sex;
        this.work = work;
        this.limtStarttime = limtStarttime;
        this.limtEndtime = limtEndtime;
    }

    @Generated(hash = 471392397)
    public PersonFaces() {
    }


    public long get_id() {
        return this._id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNum() {
        return this.num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getPhoneNum() {
        return this.phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getTime() {
        return this.time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getShowPicPath() {
        return this.showPicPath;
    }

    public void setShowPicPath(String showPicPath) {
        this.showPicPath = showPicPath;
    }

    public String getPicPath() {
        return this.picPath;
    }

    public void setPicPath(String picPath) {
        this.picPath = picPath;
    }

    public String getSex() {
        return this.sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getWork() {
        return this.work;
    }

    public void setWork(String work) {
        this.work = work;
    }

}
