package com.minivision.machinefacerecognition.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.minivision.machinefacerecognition.R;
import com.minivision.machinefacerecognition.activity.log.LogMobot;
import com.minivision.machinefacerecognition.activity.utils.CustomView;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Zhaojf on 2017/8/10 0010.
 */

public class SettingCheckActivity extends Activity implements View.OnClickListener {
    private EditText settingcheckEt;
    private Button settingcheckBtn;
    private AlertDialog.Builder builder;
    String TAG = "SettingCheckActivity";
    Handler handler = new Handler();
    AlertDialog alertDialog;
    String pwd;
    RelativeLayout root;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settingcheck);
        ButterKnife.bind(this);
        initView();
        SharedPreferences sp = getSharedPreferences("threshold", Context.MODE_PRIVATE);
        pwd = sp.getString("oldpwd", "");
        LogMobot.logd("密码：" + pwd);
    }

    public void initView() {
        settingcheckEt = (EditText) this.findViewById(R.id.pwd_et);
        settingcheckBtn = (Button) this.findViewById(R.id.confim_btn);
        settingcheckBtn.setOnClickListener(this);
        root = (RelativeLayout) this.findViewById(R.id.root);
    }

    @Override
    public void onClick(View v) {

        String pwdet = settingcheckEt.getText().toString();
        builder = new AlertDialog.Builder(this);
        Log.d(TAG, "onClick: IS click" + pwd);
        if (!pwdet.equals(pwd)) {
            builder.setTitle("提示");
            builder.setMessage("密码错误，请重新输入！");
            builder.setNegativeButton("确定", null);
            builder.create();
            builder.show();
        } else {
//            alertDialog = builder.create();
////            alertDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);//去掉这句话，背景会变暗
//            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//            alertDialog.show();
//            alertDialog.setContentView(R.layout.dialog_success);
//            TextView tv = (TextView) alertDialog.findViewById(R.id.tv_dialog);
//            tv.setText("登陆成功");
//            handler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    startActivity(new Intent(SettingCheckActivity.this, SystemSettingActivity.class));
//                    finish();
//                }
//            }, 1000);

            CustomView customView = new CustomView(this);
            root.addView(customView);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(SettingCheckActivity.this, SystemSettingActivity.class));
                    finish();
                }
            }, 1000);
        }
    }

    @OnClick(R.id.back_imgv)
    public void back() {
        Intent intent = new Intent(new Intent(SettingCheckActivity.this, MainActivity.class));
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.leftin, R.anim.rightout);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(null);
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(new Intent(SettingCheckActivity.this, MainActivity.class));
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.leftin, R.anim.rightout);
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == event.KEYCODE_BACK) {
            Intent intent = new Intent(new Intent(SettingCheckActivity.this, MainActivity.class));
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
