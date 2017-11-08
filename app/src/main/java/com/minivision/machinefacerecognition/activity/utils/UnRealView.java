package com.minivision.machinefacerecognition.activity.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

import com.minivision.machinefacerecognition.R;

/**
 * Created by Zhaojf on 2017/9/19 0019.
 */

public class UnRealView extends View {
    private Bitmap mBitmap;
    private Paint bitmapPaint;
    private Paint textPaint;
    String text = "请本人进行识别";

    public UnRealView(Context context) {
        super(context);
        bitmapPaint = new Paint();
        textPaint = new Paint();
        textPaint.setColor(getResources().getColor(R.color.colorWhite));
        textPaint.setTextSize(72);
        mBitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.sad);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        LogMobot.loge(getWidth() + "w h " + getHeight());
        int width = mBitmap.getWidth();
        canvas.drawBitmap(mBitmap, getWidth() / 2 - width / 2, 600, bitmapPaint);
        float v = textPaint.measureText(text);
        canvas.drawText(text, getWidth() / 2 - v / 2, 1600, textPaint);
    }
}
