package com.minivision.machinefacerecognition.activity;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.minivision.machinefacerecognition.R;
import com.minivision.machinefacerecognition.activity.log.LogMobot;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Zhaojf on 2017/8/17 0017.
 */

public class UnRealActivity extends Activity {
    @BindView(R.id.pic_imgv)
    ImageView pic;
    @BindView(R.id.pic_imgv2)
    ImageView pic2;


    @BindView(R.id.imgarrow)
    ImageView arrow;

    CountDownTimer timer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unreal);
        ButterKnife.bind(this);
//        Glide.with(this).load(R.drawable.arrow3).into(arrow);
        pic.setImageDrawable(getDrawable(R.mipmap.resultpicbg));
        pic2.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.sad));

        ObjectAnimator animator = ObjectAnimator.ofFloat(pic, "rotation", 0f, 360f);
        animator.setDuration(8000);
        animator.start();


        timer = new CountDownTimer(2500, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                LogMobot.loge("时间" + millisUntilFinished);
            }

            @Override
            public void onFinish() {
                LogMobot.loge("时间到");
                startActivity(new Intent(UnRealActivity.this, RecongnitionActivity.class));
                finish();
            }
        };
        timer.start();
    }


    public static Bitmap makeRoundCorner(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();


        LogMobot.logd("w---> " + width + "h ----> " + height);
        int left = 0, top = 0, right = width, bottom = height;
        float roundPx = height / 2;
        if (width > height) {
            left = (width - height) / 2;
            top = 0;
            right = left + height;
            bottom = height;
        } else if (height > width) {
            left = 0;
            top = (height - width) / 2;
            right = width;
            bottom = top + width;
            roundPx = width / 2;
        }

        Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        int color = 0xff424242;
        Paint paint = new Paint();
        Rect rect = new Rect(left, top, right, bottom);
        RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
        }
        finish();
    }
}
