package com.minivision.machinefacerecognition.activity.entity;

import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by Administrator on 2017/8/14 0014.
 */
@org.greenrobot.greendao.annotation.Entity
public class PersonDaily {
    @Id(autoincrement = true)//自增
    long _dailyid;
    String takePicPath;
    String pic;
    String name;
    String num;
    String takeTime;

    @Generated(hash = 987556673)
    public PersonDaily(long _dailyid, String takePicPath, String pic, String name,
            String num, String takeTime) {
        this._dailyid = _dailyid;
        this.takePicPath = takePicPath;
        this.pic = pic;
        this.name = name;
        this.num = num;
        this.takeTime = takeTime;
    }

    @Generated(hash = 1324882627)
    public PersonDaily() {
    }

    public long get_dailyid() {
        return _dailyid;
    }

    public void set_dailyid(long _dailyid) {
        this._dailyid = _dailyid;
    }

    public String getTakePicPath() {
        return takePicPath;
    }

    public void setTakePicPath(String takePicPath) {
        this.takePicPath = takePicPath;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getTakeTime() {
        return takeTime;
    }

    public void setTakeTime(String takeTime) {
        this.takeTime = takeTime;
    }
}
