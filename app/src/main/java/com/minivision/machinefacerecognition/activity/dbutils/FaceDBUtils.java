package com.minivision.machinefacerecognition.activity.dbutils;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import com.minivision.face.recognize.Recognizer;
import com.minivision.face.recognize.RecognizerWrapper;
import com.minivision.machinefacerecognition.activity.dailydb.PersonFacesDao;
import com.minivision.machinefacerecognition.activity.db.FaceDaoManager;
import com.minivision.machinefacerecognition.activity.entity.PersonFaces;
import com.minivision.machinefacerecognition.activity.log.LogMobot;
import com.minivision.machinefacerecognition.activity.utils.Utils;

import org.greenrobot.greendao.query.QueryBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

/**
 * Created by Zhaojf on 2017/8/14 0014.
 */

public class FaceDBUtils {
    private FaceDaoManager daoManager;
    private Context context;

    public FaceDBUtils(Context context) {
        daoManager = FaceDaoManager.getInstance();
        this.context = context;
    }

    /*
    * 新增前 查库
    * 有：提示 无 新增
    * */
    public boolean singleinsert(PersonFaces personFace) throws FileNotFoundException {
        boolean result = false;
        result = query(personFace.getName());
        LogMobot.loge("result 已存在--->" + result);
        Recognizer instance;
        if (result) {
            return false;
        } else {
            List<PersonFaces> personFaces = queryAll();
            int size = personFaces.size();
            LogMobot.loge("size--->" + size);
            int i = 0;
            while (true) {
                i++;
                LogMobot.loge("i=  " + i);
                long id = size + i;

                PersonFaces load = daoManager.getDaoSession(context).getPersonFacesDao().load(id);
                if (load == null) {
                    LogMobot.loge("id 可用" + id);
                    personFace.set_id(id);

                    LogMobot.loge("insert != -1");
                    File file = new File(personFace.getPicPath());
                    if (file.exists()) {
                        LogMobot.loge("file exists");
                        instance = RecognizerWrapper.instance().getRecognizer();
                        Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
                        float[] feature = instance.getFeature(bitmap);
                        int addresult = instance.addFeature(personFace.getName(), feature);
                        LogMobot.loge("addresult ==== " + addresult);
                        if (addresult == 1) {
                            LogMobot.loge("id final====" + size + i);
                            long insert = daoManager.getDaoSession(context).getPersonFacesDao().insertOrReplace(personFace);
                            if (insert != -1) {
                                result = true;
                                break;
                            } else {
                                result = false;
                                break;
                            }

                        } else {
                            result = false;
                            break;
                        }
                    } else {
//                         "照片不存在，请重新注册"
                        result = false;
                        break;
                    }
                }
            }
        }

        //            return insert == -1 ? false : true;

        return result;


    }

    /*
      * 新增前 查库
      * 有：提示 无 新增
      * */
    public boolean singleinsert2(PersonFaces personFace) throws FileNotFoundException {
        boolean result = false;
        result = query(personFace.getName());
        Recognizer instance = RecognizerWrapper.instance().getRecognizer();
        LogMobot.loge("Queryresult  查询 照片 " + personFace.getName() + " 结果 ：" + result);
        if (result) {
            return false;
        } else {
            File file = new File(personFace.getPicPath());
            boolean isPng = file.getName().endsWith(".png");
            boolean isJpg = file.getName().endsWith(".jpg");
            boolean isUpJpg = file.getName().endsWith(".JPG");
            boolean isUpPng = file.getName().endsWith(".PNG");
            //判断是否是文件
            if (file.exists() && file.isFile()) {
                LogMobot.loge("file is file");
                //判断是否是jpg/png图片
                if (isPng || isJpg || isUpJpg || isUpPng) {
                    instance = RecognizerWrapper.instance().getRecognizer();
                    Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
                    float[] feature = instance.getFeature(bitmap);
                    int addresult = instance.addFeature(personFace.getName(), feature);
                    LogMobot.loge("addresult = " + addresult);
                    //检查特征
                    if (addresult != 1) {
                        return false;
                    } else {
                        List<PersonFaces> personFaces = queryAll();
                        int size = personFaces.size();
                        LogMobot.loge("size--->" + size);
                        int i = 0;
                        while (true) {
                            i++;
                            LogMobot.loge("i=  " + i);
                            long id = size + i;
                            PersonFaces load = daoManager.getDaoSession(context).getPersonFacesDao().load(id);
                            if (load == null) {
                                LogMobot.loge("id 可用" + id);
                                personFace.set_id(id);
                                long insert = daoManager.getDaoSession(context).getPersonFacesDao().insertOrReplace(personFace);
                                if (insert != -1) {
                                    LogMobot.loge("insert != -1");
                                    result = true;
                                    break;
                                } else {
                                    result = false;
                                    break;
                                }
                            }
                        }
                    }
                } else {
                    LogMobot.loge("图片无人脸" );
                    return false;
                }
            } else {

                LogMobot.loge("文件不是以png、jpg结果图片 "+ file.getName()+" ++"+file.getAbsolutePath()+file.isFile());
                return false;
            }
            return result;
        }
    }
    /*
      * 新增前 查库
      * 有：提示 无 新增
      * */

    public boolean mulinsert(String path, PersonFaces personFace) throws FileNotFoundException {
        boolean result = false;
        result = query(personFace.getName());
        Recognizer instance = RecognizerWrapper.instance().getRecognizer();
        LogMobot.loge("Queryresult --->" + result);
        if (result) {
            return false;
        } else {
            List<PersonFaces> personFaces = queryAll();
            int size = personFaces.size();
            LogMobot.loge("size--->" + size);

            int i = 0;
            while (true) {
                i++;
                LogMobot.loge("i=  " + i);
                long id = size + i;

                PersonFaces load = daoManager.getDaoSession(context).getPersonFacesDao().load(id);

                if (load == null) {
                    LogMobot.loge("id not used");
                    personFace.set_id(id);
                    long insert = daoManager.getDaoSession(context).getPersonFacesDao().insertOrReplace(personFace);
                    if (insert != -1) {
                        File file = new File(path);
                        LogMobot.loge("检测file" + path);
                        if (file.exists() && file.isFile()) {
                            LogMobot.loge("file 存在");
                            Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
                            float[] feature = instance.getFeature(bitmap);
                            int addresult = instance.addFeature(personFace.getName(), feature);
                            if (addresult == 1) {
                                LogMobot.loge("添加成功---> " + personFace.getName() + i);
                                result = true;
                                break;
                            } else {
                                result = false;
                                break;
                            }
                        } else {
//                            Toast.makeText(context, personFace.getName() + "照片不存在，请重新注册", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
//                        Utils.delFile(personFace.getPicPath());
//                        delete(personFace);
//                        instance.deleteFeature(personFace.getName());
//                        instance.saveFeatures();
            }
            return result;
        }

        //            return insert == -1 ? false : true;
//        return result;

    }


    /*
     * 新增前 查库
     * 有：提示 无 新增
     * */
    public boolean mulinsert2(String path, PersonFaces personFace) throws FileNotFoundException {
        boolean result = false;
        result = query(personFace.getName());
        Recognizer instance = RecognizerWrapper.instance().getRecognizer();
        LogMobot.loge("Queryresult  查询 照片 " + personFace.getName() + "结果 ：" + result);
        if (result) {
            return false;
        } else {
            File file = new File(path);
            LogMobot.loge("getPicPath = " + file.getAbsolutePath());
            boolean isPng = file.getName().endsWith(".png");
            boolean isJpg = file.getName().endsWith(".jpg");
            boolean isUpJpg = file.getName().endsWith(".JPG");
            boolean isUpPng = file.getName().endsWith(".PNG");
//            判断是否是文件
            if (file.exists() && file.isFile()) {
                LogMobot.loge("file  是文件 ");
//                判断是否是jpg/png图片
                if (isPng || isJpg || isUpJpg || isUpPng) {
                    instance = RecognizerWrapper.instance().getRecognizer();
                    Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
                    float[] feature = instance.getFeature(bitmap);
                    int addresult = instance.addFeature(personFace.getName(), feature);
                    LogMobot.loge("图片格式正确");
                    LogMobot.loge("addresult = " + addresult);
                    //检查特征
                    if (addresult != 1) {
                        LogMobot.loge("图片不存在人脸");
                        return false;
                    } else {
                        LogMobot.loge("图片存在人脸");
                        List<PersonFaces> personFaces = queryAll();
                        int size = personFaces.size();
                        LogMobot.loge("size--->" + size);
                        int i = 0;
                        while (true) {
                            i++;
                            LogMobot.loge("i=  " + i);
                            long id = size + i;
                            PersonFaces load = daoManager.getDaoSession(context).getPersonFacesDao().load(id);
                            if (load == null) {
                                LogMobot.loge("id 可用" + id);
                                personFace.set_id(id);
                                long insert = daoManager.getDaoSession(context).getPersonFacesDao().insertOrReplace(personFace);
                                if (insert != -1) {
                                    LogMobot.loge("insert != -1");
                                    result = true;
                                    break;
                                } else {
                                    result = false;
                                    break;
                                }
                            }
                        }
                    }
                } else {
                    LogMobot.loge("图片格式不正确");
                    return false;
                }
            } else {
                LogMobot.loge("file 不是文件");
                return false;
            }
            return result;
        }

    }


    //查询
    public List<PersonFaces> queryAll() {
        List<PersonFaces> entities = daoManager.getDaoSession(context).loadAll(PersonFaces.class);
        for (PersonFaces p : entities) {
//            LogMobot.loge(p.getName() + p.getPicPath() + p.getNum());
        }
        return entities;
    }


    //条件查询

    public boolean query(String value) {
        List<PersonFaces> entities = daoManager.getDaoSession(context).queryRaw(PersonFaces.class, "where name = ?", new String[]{value});
        for (int i = 0; i < entities.size(); i++) {
//            LogMobot.logd(entities.get(i).getName());
        }
        if (entities.size() > 0) {
            return true;
        } else {
            return false;
        }

    }

    //更新
    public void update(PersonFaces e) {
        daoManager.getDaoSession(context).getPersonFacesDao().update(e);

    }

    //删除实体
    public void delete(PersonFaces entity) {
        daoManager.getDaoSession(context).getPersonFacesDao().delete(entity);
    }


    //删除实体2
    public void delete2(PersonFaces entity) {
        LogMobot.loge("delete2-->" + entity.get_id());
        daoManager.getDaoSession(context).getPersonFacesDao().deleteByKey(entity.get_id());
    }

    //删除全部
    public void deleteAll() {
        daoManager.getDaoSession(context).getPersonFacesDao().deleteAll();
    }

    public List<PersonFaces> queryByNameAndNum(String name, String number) {
        QueryBuilder<PersonFaces> personFacesQueryBuilder = daoManager.getDaoSession(context).queryBuilder(PersonFaces.class);
        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(number)) {
            personFacesQueryBuilder.where(PersonFacesDao.Properties.Name.eq(name), PersonFacesDao.Properties.Num.eq(number));
            return personFacesQueryBuilder.list();
        } else if (!TextUtils.isEmpty(name)) {
            personFacesQueryBuilder.where(PersonFacesDao.Properties.Name.eq(name));
            return personFacesQueryBuilder.list();
        } else if (!TextUtils.isEmpty(number)) {
            personFacesQueryBuilder.where(PersonFacesDao.Properties.Num.eq(number));
            return personFacesQueryBuilder.list();
        } else {
            return queryAll();
        }
    }


    /*
    * 根据姓名查工号
    * */
    public String getNumberByName(String name) {
        String number = null;
        QueryBuilder<PersonFaces> personFacesQueryBuilder = daoManager.getDaoSession(context).queryBuilder(PersonFaces.class);
        personFacesQueryBuilder.where(PersonFacesDao.Properties.Name.eq(name));
        List<PersonFaces> list = personFacesQueryBuilder.list();
        for (PersonFaces p : list) {
            number = p.getNum();
        }
        return number;
    }

    /*
    * 根据姓名查手机号
    * */
    public String getPhoneByName(String name) {
        String number = null;
        QueryBuilder<PersonFaces> personFacesQueryBuilder = daoManager.getDaoSession(context).queryBuilder(PersonFaces.class);
        personFacesQueryBuilder.where(PersonFacesDao.Properties.Name.eq(name));
        List<PersonFaces> list = personFacesQueryBuilder.list();
        for (PersonFaces p : list) {
            number = p.getPhoneNum();
        }
        return number;
    }

    /*
    * 姓名查底库照片
    * */
    public String getPicByName(String name) {
        String picPath = null;
        QueryBuilder<PersonFaces> personFacesQueryBuilder = daoManager.getDaoSession(context).queryBuilder(PersonFaces.class);
        personFacesQueryBuilder.where(PersonFacesDao.Properties.Name.eq(name));
        List<PersonFaces> list = personFacesQueryBuilder.list();
        for (PersonFaces p : list) {
            picPath = p.getPicPath();
        }
        return picPath;
    }

    /*
        * 姓名查找部门
        * */
    public String getWorkByName(String name) {
        String work = null;
        QueryBuilder<PersonFaces> personFacesQueryBuilder = daoManager.getDaoSession(context).queryBuilder(PersonFaces.class);
        personFacesQueryBuilder.where(PersonFacesDao.Properties.Name.eq(name));
        List<PersonFaces> list = personFacesQueryBuilder.list();
        for (PersonFaces p : list) {
            work = p.getWork();
        }
        return work;
    }

    /*
           * 姓名查找id
           * */
    public long getIDByName(String name) {
        long id = -1;
        QueryBuilder<PersonFaces> personFacesQueryBuilder = daoManager.getDaoSession(context).queryBuilder(PersonFaces.class);
        personFacesQueryBuilder.where(PersonFacesDao.Properties.Name.eq(name));
        List<PersonFaces> list = personFacesQueryBuilder.list();
        for (PersonFaces p : list) {
            id = p.get_id();
        }
        return id;
    }


    public void deletFaceByName(String name) {

        QueryBuilder<PersonFaces> personFacesQueryBuilder = daoManager.getDaoSession(context).queryBuilder(PersonFaces.class);
        personFacesQueryBuilder.where(PersonFacesDao.Properties.Name.eq(name));
        List<PersonFaces> list = personFacesQueryBuilder.list();

        Utils.deleteByName(name);
        for (PersonFaces p : list) {
            daoManager.getDaoSession(context).getPersonFacesDao().delete(p);
        }
    }

    public int getstByName(String name) {
        int st = 0;
        QueryBuilder<PersonFaces> personFacesQueryBuilder = daoManager.getDaoSession(context).queryBuilder(PersonFaces.class);
        personFacesQueryBuilder.where(PersonFacesDao.Properties.Name.eq(name));
        List<PersonFaces> list = personFacesQueryBuilder.list();
        for (PersonFaces p : list) {
            st = Integer.parseInt(p.getLimtStarttime());
        }

        return st;
    }

    public int getetByName(String name) {
        int st = 0;
        QueryBuilder<PersonFaces> personFacesQueryBuilder = daoManager.getDaoSession(context).queryBuilder(PersonFaces.class);
        personFacesQueryBuilder.where(PersonFacesDao.Properties.Name.eq(name));
        List<PersonFaces> list = personFacesQueryBuilder.list();
        for (PersonFaces p : list) {
            st = Integer.parseInt(p.getLimtEndtime());
        }

        return st;
    }

}
