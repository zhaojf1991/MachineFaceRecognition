package com.minivision.machinefacerecognition.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.minivision.machinefacerecognition.R;
import com.minivision.machinefacerecognition.activity.adapter.FaceAdapter;
import com.minivision.machinefacerecognition.activity.dbutils.FaceDBUtils;
import com.minivision.machinefacerecognition.activity.entity.PersonFaces;
import com.minivision.machinefacerecognition.activity.log.LogMobot;
import com.minivision.machinefacerecognition.activity.utils.Constant;
import com.minivision.machinefacerecognition.activity.utils.Utils;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Zhaojf on 2017/8/10 0010.
 */

public class FaceQueryActivity extends Activity {
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
    private FaceAdapter faceAdapter = null;
    private Spinner startTime, endTime;


//    @BindView(R.id.edit_btn)
//    Button editBtn;
    HashMap<Integer, String> maps = new HashMap<Integer, String>();

    @BindView(R.id.tv_pernum)
    TextView perNumTv;
    int st = 1, et = 24;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facequery);
        ButterKnife.bind(this);
        faceDBUtils = new FaceDBUtils(FaceQueryActivity.this);
        listView.addHeaderView(getLayoutInflater().inflate(R.layout.lv_headview, null));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String name = personFaces.get(position - 1).getName();
                LogMobot.logd("name ---> " + name);

                final View dialogView = LayoutInflater.from(FaceQueryActivity.this).inflate(R.layout.dialog_update, null);
                DnameEt = (EditText) dialogView.findViewById(R.id.name_et);
                DnumberEt = (EditText) dialogView.findViewById(R.id.number_et);
                DworkEt = (EditText) dialogView.findViewById(R.id.work_et);
                DphoneEt = (EditText) dialogView.findViewById(R.id.phone_et);
                radioSex = (RadioGroup) dialogView.findViewById(R.id.radio_sex);
                startTime = (Spinner) dialogView.findViewById(R.id.limit_st);
                endTime = (Spinner) dialogView.findViewById(R.id.limit_et);


                startTime.setSelection(faceDBUtils.getstByName(name)-1);
                endTime.setSelection(faceDBUtils.getetByName(name)-1);


                startTime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        st = position + 1;
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                endTime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        et = position + 1;
                        LogMobot.logd("et ---> " + et);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                if (et <= st) {
                    Toast.makeText(FaceQueryActivity.this, "开始时间要大于结束时间", Toast.LENGTH_SHORT).show();
                    et = 24;
                    st = 1;
                }
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


                if (radioButton != null) {
                    sex = radioButton.getText().toString();
                }
                final String num = personFaces.get(position - 1).getNum();
                final String phoneNum = personFaces.get(position - 1).getPhoneNum();
                final String work = personFaces.get(position - 1).getWork();
                final String time = personFaces.get(position - 1).getTime();
                dialog = new AlertDialog.Builder(FaceQueryActivity.this);
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
                        pf.setSex(sex);
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
                                + sex + "\n"
                                + time + "\n"
                                + String.valueOf(st) + "\n"
                                + String.valueOf(et) + "\n"
                                + faceDBUtils.getPicByName(DnameEt.getText().toString()) + "\n"
                                + Constant.SHOWPIC + DnameEt.getText().toString() + ".jpg" + "\n");
                        faceDBUtils.update(pf);
                        queryTv();
                    }
                });
                dialog.setNegativeButton("取消", null);
                dialog.show();
            }
        });
    }


    @OnClick(R.id.query_imgv)
    public void queryTv() {
        try {
            setDataAdapter(1);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void setDataAdapter(int code) {

        personFaces = faceDBUtils.queryByNameAndNum(nameEt.getText().toString(), numberEt.getText().toString());
        faceAdapter = new FaceAdapter(personFaces, FaceQueryActivity.this, code) {
            @Override
            public void getCheckBoxValue(HashMap<Integer, String> map) {
                maps = map;
            }
        };
        if (code == 1) {
            faceAdapter.notifyDataSetChanged();
        }
        perNumTv.setText("(" + personFaces.size() + ")");
        listView.setAdapter(faceAdapter);
        Animation animation = AnimationUtils.loadAnimation(FaceQueryActivity.this, R.anim.item);
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
        listView.setAdapter(new FaceAdapter(null, FaceQueryActivity.this, 1) {
            @Override
            public void getCheckBoxValue(HashMap<Integer, String> map) {

            }
        });

    }

    @OnClick(R.id.back_imgv)
    public void back() {
        Intent intent = new Intent(new Intent(FaceQueryActivity.this, SystemSettingActivity.class));
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.leftin, R.anim.rightout);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


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

    int flag = 0;

//    @OnClick(R.id.edit_btn)
//    public void editBtn() {
//        if (flag == 0) {
//            editBtn.setText("取消");
//            setDataAdapter(0);
//            flag = 1;
//
//        } else {
//            editBtn.setText("编辑");
//            setDataAdapter(1);
//            flag = 0;
//        }
//
//    }

//    //删除daily
//    @OnClick(R.id.update_btn)
//    public void delete() {
//
//        if (!editBtn.getText().toString().equals("取消")) {
//            Toast.makeText(this, "请先点击编辑", Toast.LENGTH_SHORT).show();
//        }
//
//        Iterator iter = maps.entrySet().iterator();
//        while (iter.hasNext()) {
//            Map.Entry entity = (Map.Entry) iter.next();
//            faceDBUtils.deletFaceByName(entity.getValue().toString());
//            Recognizer faceRecognizer = RecognizerWrapper.instance().getRecognizer();
//            faceRecognizer.deleteFeature(entity.getValue().toString());
//            faceRecognizer.saveFeatures();
//            editBtn.setText("编辑");
//            setDataAdapter(1);
//            flag = 0;
//        }
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        startActivity(new Intent(FaceQueryActivity.this, SystemSettingActivity.class));
        Intent intent = new Intent(new Intent(FaceQueryActivity.this, SystemSettingActivity.class));
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.leftin, R.anim.rightout);
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == event.KEYCODE_BACK) {
            Intent intent = new Intent(new Intent(FaceQueryActivity.this, SystemSettingActivity.class));
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            overridePendingTransition(R.anim.leftin, R.anim.rightout);
            finish();
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }
}