package com.minivision.machinefacerecognition.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.bigkoo.pickerview.TimePickerView;
import com.minivision.machinefacerecognition.R;
import com.minivision.machinefacerecognition.activity.adapter.DailyAdapter;
import com.minivision.machinefacerecognition.activity.dbutils.DailyDBUtil;
import com.minivision.machinefacerecognition.activity.dbutils.FaceDBUtils;
import com.minivision.machinefacerecognition.activity.entity.PersonDaily;
import com.minivision.machinefacerecognition.activity.log.LogMobot;
import com.minivision.machinefacerecognition.activity.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Zhaojf on 2017/8/10 0010.
 */

public class FaceDailyActivity extends Activity {
    @BindView(R.id.name_et)
    EditText nameEt;
    @BindView(R.id.starttime_et)
    EditText startTimeEt;
    @BindView(R.id.endtime_et)
    EditText endTimeEt;
    String name, startTime, endTime;
    //    @BindView(R.id.dpPicker)
//    DatePicker dpPicker;
    ListView dailyLv;
    int years, month, day;
    String starttime, endtime;
    FaceDBUtils faceDBUtils;


    //    @BindView(R.id.edit_btn)
//    Button editBtn;
    DailyAdapter dailyAdapter;
    DailyDBUtil dailyDBUtil;
    HashMap<Integer, String> maps = new HashMap<Integer, String>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_facedaily);
        ButterKnife.bind(this);

        dailyLv = (ListView) this.findViewById(R.id.daily_lv);
        dailyLv.addHeaderView(getLayoutInflater().inflate(R.layout.daily_headview, null));
        dailyDBUtil = new DailyDBUtil(FaceDailyActivity.this);
//        dpPicker = (DatePicker) this.findViewById(R.id.dpPicker);
//        dpPicker.setVisibility(View.INVISIBLE);
        startTimeEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    showDate(startTimeEt);
                }
            }
        });
        endTimeEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    showDate(endTimeEt);
                }
            }
        });

    }


    @OnClick(R.id.query_btn)
    public void queryTv() {
        setAdapter(1);
    }


    @OnClick(R.id.clear_btn)
    public void clear() {
        nameEt.setText("");
        startTimeEt.setText("");
        endTimeEt.setText("");
    }
//        editText.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                editText.setFocusable(false);
//            }
//        });
//        editText.setInputType(InputType.TYPE_NULL);
//        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if (hasFocus) {
//                    dpPicker.setVisibility(View.VISIBLE);
//                    dpPicker.init(2017, 8, 14, new DatePicker.OnDateChangedListener() {
//                        @Override
//                        public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
//                            years = year;
//                            month = monthOfYear;
//                            day = dayOfMonth;
//                            editText.setText(formatDate());
//                        }
//                    });
//                } else {
//                    dpPicker.setVisibility(View.INVISIBLE);
//                }
//            }
//        });


//时间选择器

    public void showDate(final EditText editText) {
        TimePickerView pvTime = new TimePickerView.Builder(this, new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {//选中事件回调
                SimpleDateFormat format = new SimpleDateFormat(
                        "yyyy-MM-dd");

                editText.setText(format.format(date));
            }
        }).setSubmitColor(Color.BLACK)//确定按钮文字颜色
                .setCancelColor(Color.BLACK)//取消按钮文字颜色
                .setContentSize(18)//滚轮文字大小
                .setType(new boolean[]{true, true, true, false, false, false})
                .setLabel("", "", "", null, null, null)
                .build();
        pvTime.setDate(Calendar.getInstance());
        pvTime.show();
    }


    //    public String formatDate() {
//        // 获取一个日历对象，并初始化为当前选中的时间
//        Calendar calendar = Calendar.getInstance();
//        calendar.set(years, month, day);
//        SimpleDateFormat format = new SimpleDateFormat(
//                "yyyy年MM月dd日  HH:mm");
//        Toast.makeText(FaceDailyActivity.this,
//                format.format(calendar.getTime()), Toast.LENGTH_SHORT)
//                .show();
//        String time = format.format(calendar.getTime());
//        return time;
//    }
    @OnClick(R.id.back_imgv)
    public void back() {
        Intent intent = new Intent(new Intent(FaceDailyActivity.this, SystemSettingActivity.class));
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.leftin, R.anim.rightout);
        finish();
    }

    int flag = 0;

//    @OnClick(R.id.edit_btn)
//    public void editBtn() {
//        if (flag == 0) {
//            editBtn.setText("取消");
//            setAdapter(0);
//            flag = 1;
//            dailyDBUtil.queryAll();
//        } else {
//            editBtn.setText("编辑");
//            setAdapter(1);
//            flag = 0;
//        }
//
//    }

    public void setAdapter(int flag) {
        try {
            name = nameEt.getText().toString();
//            if (name == null || name.equals("")) {
//                Toast.makeText(this, "请填写查询姓名", Toast.LENGTH_SHORT).show();
//            }
            List<PersonDaily> personDailies1 = dailyDBUtil.queryAll();
            Collections.reverse(personDailies1);
            //时间比较
            startTime = startTimeEt.getText().toString() + " 00:00:00";
            endTime = endTimeEt.getText().toString() + " 24:59:59";

            //名字 时间齐全
            if (!TextUtils.isEmpty(name.trim()) && !TextUtils.isEmpty(startTimeEt.getText().toString()) && !TextUtils.isEmpty(endTimeEt.getText().toString())) {
                if (!Utils.dateCompare(endTime, startTime)) {
                    Toast.makeText(this, "结束时间请大于开始时间", Toast.LENGTH_SHORT).show();
                }
                LogMobot.logd("名字 时间均不为空");
                List<PersonDaily> personDailies = dailyDBUtil.queryByNameAndTime(name, startTime, endTime);
                setAdapterDate(personDailies, flag);
                LogMobot.logd("名字 时间均不为空 personDailies" + personDailies.size());
            }
            //名字没有，按时间查找
            else if (!TextUtils.isEmpty(startTimeEt.getText().toString()) && !TextUtils.isEmpty(endTimeEt.getText().toString())) {
                if (!Utils.dateCompare(endTime, startTime)) {
                    Toast.makeText(this, "结束时间请大于开始时间", Toast.LENGTH_SHORT).show();
                }
                LogMobot.logd("名字 为空 时间不为空");
                List<PersonDaily> personDailies = dailyDBUtil.queryByTime(startTime, endTime);
                setAdapterDate(personDailies, flag);
                LogMobot.logd("名字 为空 时间不为空 personDailies" + personDailies.size());

            } else if (!TextUtils.isEmpty(name.trim())) {
                LogMobot.logd("名字 不为空");
                List<PersonDaily> personDailies = dailyDBUtil.queryByName(name);
                setAdapterDate(personDailies, flag);
                LogMobot.logd("名字 不为空 personDailies" + personDailies.size());

            } else if (TextUtils.isEmpty(name.trim()) && !TextUtils.isEmpty(startTimeEt.getText().toString())) {
                //名字为空，开始时间不为空
                LogMobot.logd("名字 为空 开始时间不为空");
                List<PersonDaily> personDailies = dailyDBUtil.queryBystTime(startTime);
                setAdapterDate(personDailies, flag);
                LogMobot.logd("名字 为空 时间不为空 personDailies" + personDailies.size());
            } else if (TextUtils.isEmpty(name.trim()) && !TextUtils.isEmpty(endTimeEt.getText().toString())) {
                //名字 为空 结束时间不为空
                LogMobot.logd("名字 为空 结束时间不为空");
                List<PersonDaily> personDailies = dailyDBUtil.queryByetTime(endTime);
                setAdapterDate(personDailies, flag);
                LogMobot.logd("名字 为空 时间不为空 personDailies" + personDailies.size());
            } else {
                LogMobot.logd("名字 时间均为空");
                dailyAdapter = new DailyAdapter(personDailies1, FaceDailyActivity.this, flag) {
                    @Override
                    public void getCheckBoxValue(HashMap<Integer, String> map) {
                        maps = map;
                    }
                };
                dailyAdapter.notifyDataSetChanged();
                dailyLv.setAdapter(dailyAdapter);
                Animation animation = AnimationUtils.loadAnimation(FaceDailyActivity.this, R.anim.item);

                LayoutAnimationController layoutAnimationController = new LayoutAnimationController(animation);
                layoutAnimationController.setDelay(0.5f);
                layoutAnimationController.setOrder(LayoutAnimationController.ORDER_NORMAL);
                dailyLv.setLayoutAnimation(layoutAnimationController);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void setAdapterDate(List<PersonDaily> personDailies, int code) {
        dailyAdapter = new DailyAdapter(personDailies, FaceDailyActivity.this, code) {
            @Override
            public void getCheckBoxValue(HashMap<Integer, String> map) {
                maps = map;
            }
        };
        dailyAdapter.notifyDataSetChanged();
        dailyLv.setAdapter(dailyAdapter);
        Animation animation = AnimationUtils.loadAnimation(FaceDailyActivity.this, R.anim.item);

        LayoutAnimationController layoutAnimationController = new LayoutAnimationController(animation);
        layoutAnimationController.setDelay(0.5f);
        layoutAnimationController.setOrder(LayoutAnimationController.ORDER_NORMAL);
        dailyLv.setLayoutAnimation(layoutAnimationController);
    }

//    //删除daily
//    @OnClick(R.id.update_btn)
//    public void delete() {
//        if (!editBtn.getText().toString().equals("取消")) {
//            Toast.makeText(this, "请先点击编辑", Toast.LENGTH_SHORT).show();
//        }
//        Iterator iter = maps.entrySet().iterator();
//        while (iter.hasNext()) {
//            Map.Entry entity = (Map.Entry) iter.next();
//            name = nameEt.getText().toString();
//            dailyDBUtil.deletDailyByTime(name, (String) entity.getValue());
//            editBtn.setText("编辑");
//            setAdapter(1);
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
        Intent intent = new Intent(new Intent(FaceDailyActivity.this, SystemSettingActivity.class));
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.leftin, R.anim.rightout);
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == event.KEYCODE_BACK) {
            Intent intent = new Intent(new Intent(FaceDailyActivity.this, SystemSettingActivity.class));
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
