package com.minivision.machinefacerecognition.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.minivision.machinefacerecognition.R;
import com.minivision.machinefacerecognition.activity.dbutils.FaceDBUtils;
import com.minivision.machinefacerecognition.activity.mutilupload.MutilloadupActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/8/10 0010.
 */

public class MultiRegisterActivity extends Activity implements View.OnClickListener {
    @BindView(R.id.file_img)
    ImageView fileImgv;
    private AlertDialog.Builder builder;
    private AlertDialog alertDialog;
    private FaceDBUtils faceDBUtils;

    @BindView(R.id.submit_btn)
    Button submitBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi);
        ButterKnife.bind(this);
        fileImgv.setOnClickListener(this);
        int lens = getIntent().getIntExtra("lens", 0);
        int size = getIntent().getIntExtra("size", 0);
        if (lens != 0) {
            builder = new AlertDialog.Builder(this);
            alertDialog = builder.create();
            alertDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);//去掉这句话，背景会变暗
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            alertDialog.show();
            alertDialog.setContentView(R.layout.dialog_success);
            TextView tv = (TextView) alertDialog.findViewById(R.id.tv_dialog);
            tv.setText("批量注册完成");
            Toast.makeText(this, "成功注册" + size + "人", Toast.LENGTH_SHORT).show();
            faceDBUtils = new FaceDBUtils(MultiRegisterActivity.this);
//            faceDBUtils.queryAll();
//            SharedPreferences sp = getSharedPreferences("threshold", Context.MODE_PRIVATE);
//            SharedPreferences.Editor edit = sp.edit();
//            edit.putString("isChange", "yes");
//            edit.commit();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (alertDialog != null) {
                        alertDialog.dismiss();
                    }
                    Intent intent = new Intent(MultiRegisterActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    overridePendingTransition(R.anim.leftin, R.anim.rightout);
                    finish();
                }
            }, 2000);
        }
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MultiRegisterActivity.this, "请选择上传文件夹", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.file_img:
                Intent intent = new Intent(MultiRegisterActivity.this, MutilloadupActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.leftin, R.anim.rightout);
                finish();
                break;
        }
    }


//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (data != null) {
////            LogMobot.logd("onActivityResult: data is null");
//
//            if (requestCode == 1) {
//                try {
//                    Uri uri = data.getData();
//                    LogMobot.loge("onActivityResult: uri " + uri.toString());
//
//                    String realFilePath = Utils.getRealFilePath(MultiRegisterActivity.this, uri);
//                    LogMobot.loge("onActivityResult: uri path" + realFilePath);
//                    Intent intent = new Intent(MultiRegisterActivity.this, MutilloadupActivity.class);
//                    intent.putExtra("filepath", realFilePath);
//                    startActivity(intent);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            } else if (requestCode == RESULT_CANCELED) {
//
//            }
//
//        } else {
//            return;
//        }
//
//    }

    @OnClick(R.id.back_imgv)
    public void back() {
        Intent intent = new Intent(new Intent(MultiRegisterActivity.this, SystemSettingActivity.class));
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.leftin, R.anim.rightout);
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
//        startActivity(new Intent(MultiRegisterActivity.this, SystemSettingActivity.class));
        Intent intent = new Intent(new Intent(MultiRegisterActivity.this, SystemSettingActivity.class));
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.leftin, R.anim.rightout);

        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == event.KEYCODE_BACK) {
//            startActivity(new Intent(MultiRegisterActivity.this, SystemSettingActivity.class));
            Intent intent = new Intent(new Intent(MultiRegisterActivity.this, SystemSettingActivity.class));
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

