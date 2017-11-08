package com.minivision.machinefacerecognition.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Window;
import android.view.WindowManager;

import com.minivision.machinefacerecognition.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Zhaojf on 2017/8/10 0010.
 */

public class BackstageActivity extends Activity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_backstage);
        ButterKnife.bind(this);

    }


    @OnClick(R.id.setting_btn)
    public void onClick() {
        startActivity(new Intent(BackstageActivity.this, SettingCheckActivity.class));
        finish();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }
}
