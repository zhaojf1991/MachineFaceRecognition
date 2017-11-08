package com.minivision.machinefacerecognition.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.minivision.machinefacerecognition.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class SystemSettingActivity extends Activity implements View.OnClickListener {
    private RelativeLayout singleRegister, multiRegister, faceQuery, faceDaily, doorControl, thresholdControl, systemPwd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sysetting);
        ButterKnife.bind(this);
        initView();
    }


    public void initView() {
        singleRegister = (RelativeLayout) this.findViewById(R.id.single_register_rv);
        multiRegister = (RelativeLayout) this.findViewById(R.id.multi_register_rv);
        faceQuery = (RelativeLayout) this.findViewById(R.id.face_query_rv);
        faceDaily = (RelativeLayout) this.findViewById(R.id.face_daily_rv);
        doorControl = (RelativeLayout) this.findViewById(R.id.door_control_rv);
        thresholdControl = (RelativeLayout) this.findViewById(R.id.threshold_control_rv);
        systemPwd = (RelativeLayout) this.findViewById(R.id.systempwd_control_rv);

        singleRegister.setOnClickListener(this);
        multiRegister.setOnClickListener(this);
        faceQuery.setOnClickListener(this);
        faceDaily.setOnClickListener(this);
        doorControl.setOnClickListener(this);
        thresholdControl.setOnClickListener(this);
        systemPwd.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.single_register_rv:
                startIntent(SingleRegisterActivity.class);
                break;
            case R.id.multi_register_rv:
                startIntent(MultiRegisterActivity.class);
                break;
            case R.id.face_query_rv:
                startIntent(FaceQueryActivity2.class);
                break;
            case R.id.face_daily_rv:
                startIntent(FaceDailyActivity.class);
                break;
            case R.id.door_control_rv:
                startIntent(DoorControlActivity.class);
                break;
            case R.id.threshold_control_rv:
                startIntent(ThresholdActivity.class);
                break;
            case R.id.systempwd_control_rv:
                startIntent(SystempwdActivity.class);
                break;
        }
    }

    @OnClick(R.id.back_imgv)
    public void back() {
        Intent intent = new Intent(new Intent(SystemSettingActivity.this, MainActivity.class));
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.leftin, R.anim.rightout);
        finish();
    }


    public void startIntent(Class c) {
        Intent intent = new Intent(new Intent(SystemSettingActivity.this, c));
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.leftin, R.anim.rightout);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(new Intent(SystemSettingActivity.this, MainActivity.class));
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.leftin, R.anim.rightout);
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == event.KEYCODE_BACK) {
//            Intent intent = new Intent(SystemSettingActivity.this, MainActivity.class);
            Intent intent = new Intent(new Intent(SystemSettingActivity.this, MainActivity.class));
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            overridePendingTransition(R.anim.leftin, R.anim.rightout);
            finish();
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }
}
