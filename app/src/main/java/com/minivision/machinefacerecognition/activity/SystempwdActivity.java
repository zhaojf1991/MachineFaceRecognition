package com.minivision.machinefacerecognition.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.Toast;

import com.minivision.machinefacerecognition.R;
import com.minivision.machinefacerecognition.activity.log.LogMobot;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/8/11 0011.
 */

public class SystempwdActivity extends Activity {

    @BindView(R.id.oldpwd_et)
    EditText oldEt;
    @BindView(R.id.newpwd_et)
    EditText newEt;
    @BindView(R.id.confimpwd_et)
    EditText confimEt;
    SharedPreferences sp;
    SharedPreferences.Editor edit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_systempwdcontrol);
        ButterKnife.bind(this);
        sp = getSharedPreferences("threshold", Context.MODE_PRIVATE);
        edit = sp.edit();
    }

    @OnClick(R.id.back_imgv)
    public void back() {
        Intent intent = new Intent(new Intent(SystempwdActivity.this, SystemSettingActivity.class));
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.leftin, R.anim.rightout);
        finish();
    }

    @OnClick(R.id.confim_btn)
    public void confim() {
        String old = sp.getString("oldpwd", "");
        String newpwd = newEt.getText().toString();
        String confimpwd = confimEt.getText().toString();
        String oldpwd = oldEt.getText().toString();

        LogMobot.loge("old pwd " + old);
        if (old.equals(oldpwd)) {

//           if(newpwd.equals(confimpwd))
            if (!newpwd.equals("") || !confimpwd.equals("")) {

                if (newpwd.length() < 8 || newpwd.length() > 16) {
                    Toast.makeText(this, "请按照相应格式填写密码", Toast.LENGTH_SHORT).show();
                } else if (!newpwd.equals(confimpwd)) {
                    Toast.makeText(this, "确认密码与新密码不符", Toast.LENGTH_SHORT).show();
                } else if (newpwd.equals(oldpwd)) {
                    Toast.makeText(this, "新密码与原密码相同，请重新填写", Toast.LENGTH_SHORT).show();
                } else {
                    edit.putString("oldpwd", newpwd);
                    edit.commit();
                    Toast.makeText(this, "密码修改成功", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(new Intent(SystempwdActivity.this, SystemSettingActivity.class));
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    overridePendingTransition(R.anim.leftin, R.anim.rightout);
                    finish();
                }
            } else {
                Toast.makeText(this, "新密码不能为空", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "原密码输入错误", Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(new Intent(SystempwdActivity.this, SystemSettingActivity.class));
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.leftin, R.anim.rightout);
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == event.KEYCODE_BACK) {
//            startActivity(new Intent(SystempwdActivity.this, SystemSettingActivity.class));
            Intent intent = new Intent(new Intent(SystempwdActivity.this, SystemSettingActivity.class));
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
