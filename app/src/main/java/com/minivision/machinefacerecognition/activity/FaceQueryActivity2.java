package com.minivision.machinefacerecognition.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.minivision.face.recognize.Recognizer;
import com.minivision.face.recognize.RecognizerWrapper;
import com.minivision.machinefacerecognition.R;
import com.minivision.machinefacerecognition.activity.adapter.FaceAdapter;
import com.minivision.machinefacerecognition.activity.adapter.FaceAdapter2;
import com.minivision.machinefacerecognition.activity.dbutils.FaceDBUtils;
import com.minivision.machinefacerecognition.activity.entity.PersonFaces;
import com.minivision.machinefacerecognition.activity.log.LogMobot;
import com.minivision.machinefacerecognition.activity.utils.Constant;
import com.minivision.machinefacerecognition.activity.utils.DelFeature;
import com.minivision.machinefacerecognition.activity.utils.Utils;

import org.dom4j.DocumentException;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Zhaojf on 2017/8/10 0010.
 */

public class FaceQueryActivity2 extends Activity {
    @BindView(R.id.per_lv)
    ListView listView;

    @BindView(R.id.name_et)
    EditText nameEt;
    @BindView(R.id.number_et)
    EditText numberEt;

    private List<PersonFaces> personFaces;
    private AlertDialog.Builder dialog = null;
    private EditText DnameEt, DnumberEt, DworkEt, DphoneEt;
    private RadioGroup radioSex;
    private String sex = null;
    private FaceDBUtils faceDBUtils;
    private FaceAdapter2 faceAdapter = null;
    private Spinner startTime, endTime;


    @BindView(R.id.delall_btn)
    Button delallBtn;

    @BindView(R.id.del_btn)
    Button delBtn;

    HashMap<Integer, String> maps = new HashMap<Integer, String>();

    @BindView(R.id.tv_pernum)
    TextView perNumTv;
    int st = 1, et = 24;
    Recognizer faceRecognizer;
    DelFeature delFeature;
    int flag = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facequery);
        ButterKnife.bind(this);
        faceRecognizer = RecognizerWrapper.instance().getRecognizer();

        faceDBUtils = new FaceDBUtils(FaceQueryActivity2.this);
        listView.addHeaderView(getLayoutInflater().inflate(R.layout.lv_headview, null));
        try {
            delFeature = new DelFeature();
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        final int headerViewsCount = listView.getHeaderViewsCount();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                LogMobot.logd("setOnItemClickListener" + id);
                String name = personFaces.get(position - headerViewsCount).getName();
                LogMobot.logd("name ---> " + name);

                final View dialogView = LayoutInflater.from(FaceQueryActivity2.this).inflate(R.layout.dialog_update, null);
                DnameEt = (EditText) dialogView.findViewById(R.id.name_et);
                DnumberEt = (EditText) dialogView.findViewById(R.id.number_et);
                DworkEt = (EditText) dialogView.findViewById(R.id.work_et);
                DphoneEt = (EditText) dialogView.findViewById(R.id.phone_et);
                radioSex = (RadioGroup) dialogView.findViewById(R.id.radio_sex);
                startTime = (Spinner) dialogView.findViewById(R.id.limit_st);
                endTime = (Spinner) dialogView.findViewById(R.id.limit_et);


//                startTime.setSelection(faceDBUtils.getstByName(name) - 1);
//                if (faceDBUtils.getstByName(name) >= 23) {
//                    et = 23;
//                } else {
//                    endTime.setSelection(faceDBUtils.getetByName(name));
//                }
                startTime.setSelection(faceDBUtils.getstByName(name));
                endTime.setSelection(faceDBUtils.getetByName(name));

                LogMobot.logd("et --> " + faceDBUtils.getetByName(name));
                LogMobot.logd("st --> " + faceDBUtils.getstByName(name));

                startTime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        st = (int) id;
                        LogMobot.logd("st ---> " + st);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                endTime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        et = (int) id;
                        LogMobot.logd("et ---> " + et);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                final RadioButton radioButton = (RadioButton) radioSex.findViewById(radioSex.getCheckedRadioButtonId());
                TextView choseTv = (TextView) dialogView.findViewById(R.id.chosesp);
                choseTv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("image/*");
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        startActivityForResult(intent, 1);
                    }
                });


                final String num = personFaces.get(position - headerViewsCount).getNum();
                final String phoneNum = personFaces.get(position - headerViewsCount).getPhoneNum();
                final String work = personFaces.get(position - headerViewsCount).getWork();
                final String time = personFaces.get(position - headerViewsCount).getTime();
                String sexStr = personFaces.get(position - headerViewsCount).getSex();

                LogMobot.logd("sexStr ---> " + sexStr);
                if (sexStr != null) {
                    switch (sexStr) {
                        case "男":
                            ((RadioButton) radioSex.getChildAt(0)).setChecked(true);
                            break;
                        case "女":
                            ((RadioButton) radioSex.getChildAt(1)).setChecked(true);
                            break;
                        default:
                            break;

                    }
                }
                radioSex.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                        LogMobot.logd("checkedId = " + checkedId);
                        if (checkedId == ((RadioButton) radioSex.getChildAt(0)).getId()) {
                            sex = "男";
                        } else if (checkedId == ((RadioButton) radioSex.getChildAt(1)).getId()) {
                            sex = "女";
                        } else {
                            sex = "";
                        }
                    }
                });

                LogMobot.logd("sex = " + sex);
                dialog = new AlertDialog.Builder(FaceQueryActivity2.this);
                dialog.setTitle("修改信息");
                dialog.setView(dialogView);
                DnameEt.setText(name == null ? "" : name);
                DnumberEt.setText(num == null ? "" : num);
                DphoneEt.setText(phoneNum == null ? "" : phoneNum);
                DworkEt.setText(work == null ? "" : work);


                dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        SharedPreferences sp = getSharedPreferences("threshold", Context.MODE_PRIVATE);
//                        SharedPreferences.Editor edit = sp.edit();
//                        edit.putString("isChange", "yes");
//                        edit.commit();
                        boolean flag = Utils.fileName2Person(DnameEt.getText().toString(), Constant.SHOWPIC);
                        PersonFaces pf = new PersonFaces();
                        pf.setName(DnameEt.getText().toString());
                        pf.setNum(DnumberEt.getText().toString());
                        pf.setWork(DworkEt.getText().toString());
                        pf.setPhoneNum(DphoneEt.getText().toString());
                        pf.setSex(FaceQueryActivity2.this.sex);
                        pf.setTime(time);
                        pf.setLimtStarttime(String.valueOf(st));
                        pf.setLimtEndtime(String.valueOf(et));
                        pf.setPicPath(faceDBUtils.getPicByName(DnameEt.getText().toString()));
                        if (flag) {
                            pf.setShowPicPath(Constant.SHOWPIC + DnameEt.getText().toString() + ".jpg");
                        } else {
                            pf.setShowPicPath(faceDBUtils.getPicByName(DnameEt.getText().toString()));
                        }
                        pf.set_id(faceDBUtils.getIDByName(DnameEt.getText().toString()));


                        LogMobot.logd(DnameEt.getText().toString() + "\n"
                                + DnumberEt.getText().toString() + "\n"
                                + DworkEt.getText().toString() + "\n"
                                + DphoneEt.getText().toString() + "\n"
                                + FaceQueryActivity2.this.sex + "\n"
                                + time + "\n"
                                + String.valueOf(st) + "\n"
                                + String.valueOf(et) + "\n"
                                + faceDBUtils.getPicByName(DnameEt.getText().toString()) + "\n"
                                + Constant.SHOWPIC + DnameEt.getText().toString() + ".jpg" + "\n");
                        if (et <= st) {
                            Toast.makeText(FaceQueryActivity2.this, "开始时间要小于结束时间", Toast.LENGTH_SHORT).show();
                        } else {
                            faceDBUtils.update(pf);
                        }
                        queryTv();
                    }
                });
                dialog.setNegativeButton("取消", null);
                dialog.show();
                dialog.create();
            }
        });
    }


    @OnClick(R.id.query_imgv)
    public void queryTv() {
        try {
            setDataAdapter();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void setDataAdapter() {
        personFaces = faceDBUtils.queryByNameAndNum(nameEt.getText().toString(), numberEt.getText().toString());
        faceAdapter = new FaceAdapter2(personFaces, FaceQueryActivity2.this) {
            @Override
            public void getCheckBoxValue(HashMap<Integer, String> map) {
                maps = map;
            }
        };
        faceAdapter.notifyDataSetChanged();
        perNumTv.setText("(" + personFaces.size() + ")");
        listView.setAdapter(faceAdapter);
        Animation animation = AnimationUtils.loadAnimation(FaceQueryActivity2.this, R.anim.item);
        LayoutAnimationController layoutAnimationController = new LayoutAnimationController(animation);
        layoutAnimationController.setDelay(0.5f);
        layoutAnimationController.setOrder(LayoutAnimationController.ORDER_NORMAL);
        listView.setLayoutAnimation(layoutAnimationController);
    }


    @OnClick(R.id.clear_imgv)
    public void clearImgv() {
        nameEt.setText("");
        numberEt.setText("");
        perNumTv.setText("");
        listView.setAdapter(new FaceAdapter(null, FaceQueryActivity2.this, 1) {
            @Override
            public void getCheckBoxValue(HashMap<Integer, String> map) {

            }
        });

    }

    @OnClick(R.id.back_imgv)
    public void back() {
        if (flag == 1) {
            Intent intent = new Intent(new Intent(FaceQueryActivity2.this, MainActivity.class));
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            overridePendingTransition(R.anim.leftin, R.anim.rightout);
            finish();
        } else {
            Intent intent = new Intent(new Intent(FaceQueryActivity2.this, SystemSettingActivity.class));
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            overridePendingTransition(R.anim.leftin, R.anim.rightout);
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null) {
            if (requestCode == 1) {
                try {
                    Uri uri = data.getData();
                    Bitmap bitmap = Utils.getBitmap(this.getContentResolver(), uri);
                    Utils.savePic2SD(Constant.SHOWPIC, bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    @OnClick(R.id.delall_btn)
    public void delall() {
        flag = 1;
        AlertDialog.Builder builder = new AlertDialog.Builder(FaceQueryActivity2.this);
        builder.setTitle("全部删除");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                faceDBUtils.deleteAll();
                faceRecognizer.cleanAllFeatures();
                faceRecognizer.saveFeatures();
                File file = new File(Environment.getExternalStorageDirectory() + "/pic");
                if (file.exists() && file.isDirectory()) {
                    for (File f : file.listFiles()) {
                        boolean delete = f.delete();
                        LogMobot.logd("全部删除 " + delete);
                    }
                }
                File file2 = new File(Environment.getExternalStorageDirectory() + "/showpic");
                if (file2.exists() && file2.isDirectory()) {
                    for (File f : file2.listFiles()) {
                        boolean delete = f.delete();
                        LogMobot.logd("全部删除 " + delete);
                    }
                }
                queryTv();
            }
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }


    //删除
    @OnClick(R.id.del_btn)
    public void delete() {
        flag = 1;
        if (maps.size() == 0) {
            Toast.makeText(this, "请选择删除对象", Toast.LENGTH_SHORT).show();
        } else {
            LogMobot.logd("maps " + maps.size());
            AlertDialog.Builder builder = new AlertDialog.Builder(FaceQueryActivity2.this);
            builder.setTitle("删除");
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Iterator iter = maps.entrySet().iterator();

                    while (iter.hasNext()) {
                        Map.Entry entity = (Map.Entry) iter.next();
                        faceDBUtils.deletFaceByName(entity.getValue().toString());

                        int code = faceRecognizer.deleteFeature(entity.getValue().toString());
                        LogMobot.logd("delete code==== " + code);
                        faceRecognizer.saveFeatures();
                        LogMobot.logd("entity name==== " + entity.getValue().toString());

//                        delFeature.deleteFeatureByName(entity.getValue().toString());
                    }
//                    delFeature.saveWriter();
                    queryTv();
                }
            });
            builder.setNegativeButton("取消", null);
            builder.show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dialog != null) {
            dialog.create().dismiss();
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (flag == 1) {
            Intent intent = new Intent(new Intent(FaceQueryActivity2.this, MainActivity.class));
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            overridePendingTransition(R.anim.leftin, R.anim.rightout);
            finish();
        } else {
            Intent intent = new Intent(new Intent(FaceQueryActivity2.this, SystemSettingActivity.class));
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            overridePendingTransition(R.anim.leftin, R.anim.rightout);
            finish();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == event.KEYCODE_BACK) {
            if (flag == 1) {
                Intent intent = new Intent(new Intent(FaceQueryActivity2.this, MainActivity.class));
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.leftin, R.anim.rightout);
                finish();
            } else {
                Intent intent = new Intent(new Intent(FaceQueryActivity2.this, SystemSettingActivity.class));
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.leftin, R.anim.rightout);
                finish();
            }
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }
}