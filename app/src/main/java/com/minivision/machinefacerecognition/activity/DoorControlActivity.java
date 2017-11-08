package com.minivision.machinefacerecognition.activity;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import com.minivision.machinefacerecognition.R;
import com.minivision.machinefacerecognition.activity.log.LogMobot;
import com.minivision.machinefacerecognition.fragment.FifthFloorDoorFragment;
import com.minivision.machinefacerecognition.fragment.FirstFloorDoorFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/8/10 0010.
 */

public class DoorControlActivity extends Activity {
    @BindView(R.id.spinner)
    Spinner spinner;
    private Fragment mFirstFloorDoorFragment, mFifthFloorDoorFragment;
    int mCurrentDoorType;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    int doorlocation;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doorcontrol);
        ButterKnife.bind(this);
        sp = getSharedPreferences("threshold", Context.MODE_PRIVATE);
        doorlocation = sp.getInt("door_location", 0);
        LogMobot.loge("door_location: " + doorlocation);
        spinner.setSelection(doorlocation);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                LogMobot.loge("onItemClick: " + position);
                mCurrentDoorType = position;
                setFragment(mCurrentDoorType);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setFragment(int type) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (type == 0) {
            mFirstFloorDoorFragment = new FirstFloorDoorFragment();
            transaction.replace(R.id.content_setting, mFirstFloorDoorFragment);
        } else if (type == 1) {
            mFifthFloorDoorFragment = new FifthFloorDoorFragment();
            transaction.replace(R.id.content_setting, mFifthFloorDoorFragment);
        }

        transaction.commit();
    }

    public void submit() {
        editor = sp.edit();
        int setting = checkSetting(mCurrentDoorType);
        LogMobot.loge("commit: " + mCurrentDoorType);
        LogMobot.loge("setting: " + setting);
        switch (setting) {

            case -1:
                Toast.makeText(this, "填写错误", Toast.LENGTH_SHORT).show();
                break;
            case 0:
                Toast.makeText(this, "请填写完整", Toast.LENGTH_SHORT).show();
                break;
            case 1:
                Toast.makeText(this, "IP格式填写错误", Toast.LENGTH_SHORT).show();
                break;
            case 2:
                Toast.makeText(this, "端口号4~5位，请重新填写", Toast.LENGTH_SHORT).show();
                break;
            case 3:
                editor.putInt("door_location", mCurrentDoorType);
                editor.putBoolean("doorSetting", true);
                editor.commit();
                Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(new Intent(DoorControlActivity.this, SystemSettingActivity.class));
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.leftin, R.anim.rightout);
                finish();
                break;
        }

    }

    private int checkSetting(int currentDoorType) {
        if (currentDoorType == 0) {
            return ((FirstFloorDoorFragment) mFirstFloorDoorFragment).checkSetting();
        } else if (currentDoorType == 1) {
            return ((FifthFloorDoorFragment) mFifthFloorDoorFragment).checkSetting();
        }
        return -1;
    }


    @OnClick(R.id.back_imgv)
    public void back() {
        Intent intent = new Intent(new Intent(DoorControlActivity.this, SystemSettingActivity.class));
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.leftin, R.anim.rightout);
        finish();
    }

    @OnClick(R.id.confim_btn)
    public void confim() {
        submit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(new Intent(DoorControlActivity.this, SystemSettingActivity.class));
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.leftin, R.anim.rightout);
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == event.KEYCODE_BACK) {
            Intent intent = new Intent(new Intent(DoorControlActivity.this, SystemSettingActivity.class));
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

