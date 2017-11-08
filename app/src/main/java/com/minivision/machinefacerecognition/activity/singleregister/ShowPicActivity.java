package com.minivision.machinefacerecognition.activity.singleregister;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.PaintDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.minivision.machinefacerecognition.R;
import com.minivision.machinefacerecognition.activity.SingleRegisterActivity;
import com.minivision.machinefacerecognition.activity.log.LogMobot;
import com.minivision.machinefacerecognition.activity.utils.Constant;
import com.minivision.machinefacerecognition.activity.utils.Utils;

import java.io.File;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/8/11 0011.
 */

public class ShowPicActivity extends Activity {
    private String TAG = "ShowPicActivity";
    private ImageView toolImgv, userpic_imgv;
    private View popupWindowView, rootView;
    private PopupWindow popupWindow;
    private Button takephotoBtn, chosephotoBtn, savephotoBtn, cancelBtn;
    private Bitmap cameraBp;
    SharedPreferences sp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_showpic);
        ButterKnife.bind(this);
        initView();
        sp = getSharedPreferences("threshold", Context.MODE_PRIVATE);
    }

    public void initView() {
        toolImgv = (ImageView) this.findViewById(R.id.tool_imgv);
        userpic_imgv = (ImageView) this.findViewById(R.id.userpic_imgv);
        popupWindowView = LayoutInflater.from(ShowPicActivity.this).inflate(R.layout.popupwindow, null);
        rootView = LayoutInflater.from(ShowPicActivity.this).inflate(R.layout.single_showpic, null);

        toolImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow = new PopupWindow(popupWindowView);
                popupWindow.setFocusable(true);
                popupWindow.setOutsideTouchable(true);
                popupWindow.setBackgroundDrawable(new PaintDrawable());
                popupWindow.setHeight(RelativeLayout.LayoutParams.WRAP_CONTENT);
                popupWindow.setWidth(RelativeLayout.LayoutParams.MATCH_PARENT);
                popupWindow.setAnimationStyle(R.style.take_photo_anim);
                popupWindow.showAtLocation(rootView, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                if (popupWindow.isShowing()) {
                    popupWindowClick(popupWindow);
                }
            }
        });
    }


    public void popupWindowClick(final PopupWindow popupWindow) {
        takephotoBtn = (Button) popupWindowView.findViewById(R.id.takephoto_btn);
        chosephotoBtn = (Button) popupWindowView.findViewById(R.id.chosephoto_btn);
        savephotoBtn = (Button) popupWindowView.findViewById(R.id.savephoto_btn);
        cancelBtn = (Button) popupWindowView.findViewById(R.id.cancel_btn);

        try {
            takephotoBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LogMobot.logd("onClick: takephotoBtn");
                    //                Utils.fileUtil();

                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, 100);

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        chosephotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogMobot.logd("onClick: chosephotoBtn");
//                Utils.fileUtil();
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent, 1);
            }
        });
        savephotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogMobot.logd("onClick: savephotoBtn");
                File file = new File(Constant.SHOWPIC + "/default.jpg");
                if (file.exists()) {
//                    Intent intent = new Intent(ShowPicActivity.this, SingleRegisterActivity.class);
//                    startActivity(intent);

                    Intent intent = new Intent(new Intent(ShowPicActivity.this, SingleRegisterActivity.class));
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    overridePendingTransition(R.anim.leftin, R.anim.rightout);
                    finish();
                } else {
                    Toast.makeText(ShowPicActivity.this, "请选择图片", Toast.LENGTH_SHORT).show();
                }
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (popupWindow != null) {
            if (popupWindow.isShowing()) {
                popupWindow.dismiss();
            }
        }
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (requestCode == 100) {
                Bundle bundle = data.getExtras();
                cameraBp = (Bitmap) bundle.get("data");
                userpic_imgv.setImageBitmap(cameraBp);
                //保存图片到sd 默认为default
                userpic_imgv.setImageBitmap(Utils.scaleBitmap(cameraBp, 800, 1200));
                Utils.savePic2SD(Constant.SHOWPIC, cameraBp);
                SharedPreferences.Editor edit = sp.edit();
                edit.putString("showpic", "1");
                LogMobot.logd("showpic: 1");
                edit.commit();
            } else if (requestCode == 1) {
                try {
                    Uri uri = data.getData();
                    Log.e(TAG, "onActivityResult: uri---> " + uri);
                    Bitmap bitmap = Utils.getBitmap(this.getContentResolver(), uri);
                    userpic_imgv.setImageBitmap(Utils.scaleBitmap(bitmap, 800, 1200));
                    Utils.savePic2SD(Constant.SHOWPIC, bitmap);
                    SharedPreferences.Editor edit = sp.edit();
                    edit.putString("showpic", "1");
                    LogMobot.logd("showpic: 1");
                    edit.commit();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            return;
        }
    }

    @OnClick(R.id.back_imgv)
    public void back() {
        Intent intent = new Intent(new Intent(ShowPicActivity.this, SingleRegisterActivity.class));
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.leftin, R.anim.rightout);
        finish();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(new Intent(ShowPicActivity.this, SingleRegisterActivity.class));
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.leftin, R.anim.rightout);
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == event.KEYCODE_BACK) {
            Intent intent = new Intent(new Intent(ShowPicActivity.this, SingleRegisterActivity.class));
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
