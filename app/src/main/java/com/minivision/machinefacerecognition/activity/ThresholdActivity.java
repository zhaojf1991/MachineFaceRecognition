package com.minivision.machinefacerecognition.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.Toast;

import com.minivision.machinefacerecognition.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Zhaojf on 2017/8/17 0017.
 */

public class ThresholdActivity extends Activity {

    @BindView(R.id.threshold_et)
    EditText threshold;

    @BindView(R.id.interval_et)
    EditText interval;
    private SharedPreferences sp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.threshold_control);
        ButterKnife.bind(this);
        sp = getSharedPreferences("threshold", Context.MODE_PRIVATE);
        threshold.setText(sp.getString("thresholdValue", "82"));
        interval.setText(sp.getString("intervalValue", "5"));
    }

    @OnClick(R.id.back_imgv)
    public void back() {
//        startActivity(new Intent(ThresholdActivity.this, SystemSettingActivity.class));
        Intent intent = new Intent(new Intent(ThresholdActivity.this, SystemSettingActivity.class));
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.leftin, R.anim.rightout);
        finish();
    }

    @OnClick(R.id.cancel_btn)
    public void cancel() {
        Intent intent = new Intent(new Intent(ThresholdActivity.this, SystemSettingActivity.class));
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.leftin, R.anim.rightout);
        finish();
    }

    @OnClick(R.id.sure_btn)
    public void sure() {
        String thresholdValue = threshold.getText().toString();
        String intervalValue = interval.getText().toString();
        if (!TextUtils.isEmpty(thresholdValue) && !TextUtils.isEmpty(intervalValue)) {

            SharedPreferences.Editor edit = sp.edit();
            edit.putString("thresholdValue", thresholdValue);
            edit.putString("intervalValue", intervalValue);
            edit.commit();
            Intent intent = new Intent(new Intent(ThresholdActivity.this, SystemSettingActivity.class));
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            overridePendingTransition(R.anim.leftin, R.anim.rightout);
            finish();
        } else {
            Toast.makeText(ThresholdActivity.this, "请填写相关阀值", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(new Intent(ThresholdActivity.this, SystemSettingActivity.class));
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.leftin, R.anim.rightout);
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == event.KEYCODE_BACK) {
            Intent intent = new Intent(new Intent(ThresholdActivity.this, SystemSettingActivity.class));
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
