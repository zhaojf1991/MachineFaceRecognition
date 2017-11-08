package com.minivision.machinefacerecognition.activity.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.view.View;

import com.minivision.machinefacerecognition.R;

/**
 * Created by Zhaojf on 2017/8/24 0024.
 */

public class ScanView extends View {
    private Paint paint, paint2, paint3, paint4;
    private int startX, startY;
    int w, h;
    Matrix matrix;
    int translate;
    LinearGradient linearGradient;

    public ScanView(Context context) {
        super(context);


        paint2 = new Paint();
        paint2.setAntiAlias(true);
        paint2.setStrokeWidth(4);
        paint2.setColor(getResources().getColor(R.color.colorText));


        paint3 = new Paint();
        paint3.setStrokeWidth(28);
        paint3.setStrokeCap(Paint.Cap.ROUND);
        paint3.setColor(getResources().getColor(R.color.colorText));

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        h = getHeight() + getHeight() / 2;
        w = getWidth();
        paint2.setAlpha(30);
        for (int i = 1; i < w; i++) {
            canvas.drawLine(i * 60, 0, i * 60, h / 6, paint2);
        }
        for (int i = 1; i * 60 < h / 6; i++) {
            canvas.drawLine(20, i * 60, w - 20, i * 60, paint2);
        }

        paint2.setAlpha(80);
        for (int i = 1; i < w; i++) {
            canvas.drawLine(i * 60, h / 6, i * 60, h / 3, paint2);
        }
        for (int i = 0; i * 60 < h / 6; i++) {
            canvas.drawLine(20, (i * 60) + h / 6, w - 20, (i * 60) + h / 6, paint2);
        }

        for (int i = 1; i < w; i++) {
            canvas.drawLine(i * 60, h / 3, i * 60, h / 2, paint2);
        }
        for (int i = 0; i * 60 < h / 6; i++) {
            canvas.drawLine(20, (i * 60) + h / 3, w - 20, (i * 60) + h / 3, paint2);
        }

        canvas.drawLine(20, h / 2, w - 20, h / 2, paint3);
        if (matrix != null) {
            translate += (w - 20) / 5;
            if (translate > (w - 20)) {
                translate = -getWidth() + 20;
            }

            matrix.setTranslate(translate, 0);
            linearGradient.setLocalMatrix(matrix);
            postInvalidateDelayed(500);
        }
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        int height = getHeight() + getHeight() / 2;
        int width = getWidth();
        linearGradient = new LinearGradient(20, height / 2, width - 20, height / 2,
                new int[]{
//                        getResources().getColor(R.color.color87CEEB),
                        getResources().getColor(R.color.color4ecfe5),
                        getResources().getColor(R.color.color18bdda),
                        getResources().getColor(R.color.color0ba4bf),
//                        getResources().getColor(R.color.colorPrimary),
//                        getResources().getColor(R.color.colorText),

                }, null, Shader.TileMode.CLAMP);
        paint3.setShader(linearGradient);
//        paint2.setShader(linearGradient);
//        paint.setShader(linearGradient);
        matrix = new Matrix();
    }


    /*
    *
    *  for (int i = 1; i < w; i++) {
            canvas.drawLine(i * 60, 0, i * 60, h / 6, paint);
        }
        for (int i = 1; i * 60 < h / 6; i++) {
            canvas.drawLine(20, i * 60, w - 20, i * 60, paint);
        }


        for (int i = 1; i < w; i++) {
            canvas.drawLine(i * 60, h / 6, i * 60, h / 3, paint2);
        }
        for (int i = 0; i * 60 < h / 6; i++) {
            canvas.drawLine(20, (i * 60) + h / 6, w - 20, (i * 60) + h / 6, paint2);
        }


        for (int i = 1; i < w; i++) {
            canvas.drawLine(i * 60, h / 3, i * 60, h / 2, paint2);
        }
        for (int i = 0; i * 60 < h / 6; i++) {
            canvas.drawLine(20, (i * 60) + h / 3, w - 20, (i * 60) + h / 3, paint2);
        }
    * */
}
