package com.minivision.machinefacerecognition.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.minivision.machinefacerecognition.R;
import com.minivision.machinefacerecognition.activity.dbutils.FaceDBUtils;
import com.minivision.machinefacerecognition.activity.entity.PersonFaces;
import com.minivision.machinefacerecognition.activity.log.LogMobot;
import com.minivision.machinefacerecognition.activity.singleregister.NativePicActivity;
import com.minivision.machinefacerecognition.activity.singleregister.ShowPicActivity;
import com.minivision.machinefacerecognition.activity.utils.Constant;
import com.minivision.machinefacerecognition.activity.utils.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.minivision.machinefacerecognition.R.id.single_register_name;

/**
 * Created by Administrator on 2017/8/10 0010.
 */

public class SingleRegisterActivity extends Activity {

    private RelativeLayout relativeLayout1, relativeLayout2;
    @BindView(R.id.register_btn)
    Button registerBtn;
    @BindView(single_register_name)
    EditText registerName;
    @BindView(R.id.single_register_number)
    EditText registerNumber;
    @BindView(R.id.single_register_phone)
    EditText registerPhone;
    @BindView(R.id.single_register_work)
    EditText registerWork;
    private RadioGroup radioSex;
    String flag;
    @BindView(R.id.img_showpic)
    ImageView showPicImgv;
    @BindView(R.id.img_pic)
    ImageView picImgv;
    private AlertDialog.Builder builder;
    AlertDialog alertDialog;
    @BindView(R.id.single_limit_st)
    Spinner startTimeSp;
    @BindView(R.id.single_limit_et)
    Spinner endTimeSp;
    private int st = 0, et = 23;
    SharedPreferences sp;
    String isShow, isNative;
    String sex;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single);
        ButterKnife.bind(this);
        radioSex = (RadioGroup) this.findViewById(R.id.radio_sex);
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
        LogMobot.logd("sex ---> " + sex);
        sp = getSharedPreferences("threshold", Context.MODE_PRIVATE);
        getData();
        initView();
        isShow = sp.getString("showpic", "0");
        isNative = sp.getString("nativepic", "0");
        LogMobot.logd("SingleRegisterActivity: show" + isShow + "  native" + isNative);
        if (isShow.equals("1")) {
            Bitmap showpic = BitmapFactory.decodeFile(Constant.SHOWPIC + "/default.jpg");
            if (showpic != null) {
                showPicImgv.setImageBitmap(BitmapFactory.decodeFile(Constant.SHOWPIC + "/default.jpg"));
            }
        }
        if (isNative.equals("1")) {
            Bitmap nativepic = BitmapFactory.decodeFile(Constant.PIC + "/default.jpg");
            if (nativepic != null) {
                picImgv.setImageBitmap(BitmapFactory.decodeFile(Constant.PIC + "/default.jpg"));
            }
        }

    }

    public void initView() {
        startTimeSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                st = position;
                LogMobot.loge("开始时间---> " + position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        endTimeSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                et = position;
                LogMobot.loge("结束时间---> " + position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        relativeLayout1 = (RelativeLayout) this.findViewById(R.id.showpic_rv);
        relativeLayout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.defaultSHOWFileDel();
                submitSp();
                Intent intent = new Intent(SingleRegisterActivity.this, ShowPicActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.leftin, R.anim.rightout);
                finish();
            }
        });
        relativeLayout2 = (RelativeLayout) this.findViewById(R.id.nativepic_rv);
        relativeLayout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.defaultPICFileDel();
                submitSp();
                Intent intent = new Intent(SingleRegisterActivity.this, NativePicActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.leftin, R.anim.rightout);
                finish();
            }
        });
    }

    public void getData() {
        String name = sp.getString("name", "");
        String number = sp.getString("number", "");
        String phone = sp.getString("phone", "");
        String work = sp.getString("work", "");
        int st = sp.getInt("st", 0);
        int et = sp.getInt("et", 23);
        String sexStr = sp.getString("sex", "");
        startTimeSp.setSelection(st);
        endTimeSp.setSelection(et);
        registerName.setText(name);
        registerNumber.setText(number);
        registerPhone.setText(phone);
        registerWork.setText(work);

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

    @OnClick(R.id.register_btn)
    public void onRegisterBtn() {
        //只限制底库照片isShow.equals("1") &&
        if (isNative.equals("1") && !TextUtils.isEmpty(registerName.getText())) {
            resetSp();
            SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String date = sDateFormat.format(new java.util.Date());
            String name = registerName.getText().toString();
            LogMobot.logd("name ==> " + name);
            boolean flag = Utils.fileName2Person(name, Constant.PIC);
            LogMobot.loge("修改名称---> " + flag);
            boolean flag2 = Utils.fileName2Person(name, Constant.SHOWPIC);
            LogMobot.loge("修改名称2---> " + flag2);

            String number = registerNumber.getText().toString();
            String phone = registerPhone.getText().toString();
            String work = registerWork.getText().toString();
            RadioButton radioButton = (RadioButton) radioSex.findViewById(radioSex.getCheckedRadioButtonId());
            String sex = null;

            if (radioButton != null) {
                sex = radioButton.getText().toString();
            }


            //只限制姓名 || TextUtils.isEmpty(number) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(work) || TextUtils.isEmpty(sex)
            if (TextUtils.isEmpty(name)) {
                Toast.makeText(SingleRegisterActivity.this, "请填写用户姓名", Toast.LENGTH_SHORT).show();
            }


            if (et <= st) {
                Toast.makeText(SingleRegisterActivity.this, "开始时间要小于结束时间", Toast.LENGTH_SHORT).show();
            } else {
                FaceDBUtils faceDBUtils = new FaceDBUtils(SingleRegisterActivity.this);
                PersonFaces pf = new PersonFaces();
                pf.setName(name);
                pf.setNum(number == null ? "" : number);
                pf.setWork(work == null ? "" : work);
                pf.setPhoneNum(phone == null ? "" : phone);
                pf.setSex(sex == null ? "" : sex);
                pf.setTime(date == null ? "" : date);


                pf.setPicPath(Constant.PIC + "/" + name + ".jpg");
                LogMobot.logd("st = " + st + "  et = " + et + " name= " + name);
                pf.setLimtStarttime(String.valueOf(st));
                pf.setLimtEndtime(String.valueOf(et));
                boolean insert = false;
                try {
                    insert = faceDBUtils.singleinsert2(pf);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                LogMobot.logd("insert --> " + insert + "| pf = " + pf.getPicPath());
                if (insert) {
                    builder = new AlertDialog.Builder(this);
                    alertDialog = builder.create();
//                alertDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);//去掉这句话，背景会变暗
                    alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    alertDialog.show();
                    alertDialog.setContentView(R.layout.dialog_success);
                    TextView tv = (TextView) alertDialog.findViewById(R.id.tv_dialog);
                    tv.setText("注册成功");
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
//                            startActivity(new Intent(SingleRegisterActivity.this, SystemSettingActivity.class));
                            Intent intent = new Intent(new Intent(SingleRegisterActivity.this, MainActivity.class));
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            overridePendingTransition(R.anim.leftin, R.anim.rightout);
                            finish();
                        }
                    }, 500);
                } else {
                    File delFile = new File(Constant.PIC + "/" + name + ".jpg");
                    if (delFile.exists()) {
                        delFile.delete();
                    }
                    File delFile2 = new File(Constant.SHOWPIC + "/" + name + ".jpg");
                    if (delFile2.exists()) {
                        delFile2.delete();
                    }
                    Toast.makeText(SingleRegisterActivity.this, "请核对用户信息或检查图片信息,重新选择图片", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            Toast.makeText(this, "请填写姓名和底库照片", Toast.LENGTH_SHORT).show();
        }
    }


    @OnClick(R.id.back_imgv)
    public void back() {
        Intent intent = new Intent(new Intent(SingleRegisterActivity.this, SystemSettingActivity.class));
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.leftin, R.anim.rightout);
        resetSp();
        finish();
    }

    @OnClick(R.id.unregister_btn)
    public void unregister() {
        Intent intent = new Intent(new Intent(SingleRegisterActivity.this, SystemSettingActivity.class));
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.leftin, R.anim.rightout);
        resetSp();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(new Intent(SingleRegisterActivity.this, SystemSettingActivity.class));
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.leftin, R.anim.rightout);
        resetSp();
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == event.KEYCODE_BACK) {
//            startActivity(new Intent(SingleRegisterActivity.this, SystemSettingActivity.class));
            Intent intent = new Intent(new Intent(SingleRegisterActivity.this, SystemSettingActivity.class));
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            overridePendingTransition(R.anim.leftin, R.anim.rightout);
            resetSp();
            finish();
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    public void submitSp() {
        final String name = registerName.getText().toString();
        final String number = registerNumber.getText().toString();
        final String phone = registerPhone.getText().toString();
        final String work = registerWork.getText().toString();
        //添加name,work,phone,number,sex,et,st
        SharedPreferences.Editor edit1 = sp.edit();
        edit1.putString("name", name);
        edit1.putString("number", number);
        edit1.putString("phone", phone);
        edit1.putString("work", work);
        edit1.putString("sex", sex);
        edit1.putInt("st", st);
        edit1.putInt("et", et);
        edit1.commit();
    }

    public void resetSp() {
        SharedPreferences.Editor edit1 = sp.edit();
        edit1.putString("showpic", "0");
        edit1.putString("nativepic", "0");
        edit1.putString("name", "");
        edit1.putString("number", "");
        edit1.putString("phone", "");
        edit1.putString("work", "");
        edit1.putString("sex", "");
        edit1.putInt("st", 0);
        edit1.putInt("et", 23);
        edit1.commit();
        showPicImgv.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.people));
        picImgv.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.people));

    }
}
