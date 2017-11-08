package com.minivision.machinefacerecognition.activity.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.minivision.machinefacerecognition.R;

/**
 * Created by Zhaojf on 2017/9/6 0006.
 */

public class ScanSurfaceView extends SurfaceView implements SurfaceHolder.Callback, Runnable {
    private SurfaceHolder holder;

    private boolean running = false;

    private Paint paint, paint2, paint3;
    private int startX, startY;
    private int w, h;
    private Matrix matrix;
    private int translate;
    private LinearGradient linearGradient;
    private Canvas mCanvas;

    public ScanSurfaceView(Context context) {
        super(context);
        init();
    }

    public void init() {
        holder = getHolder();
        holder.addCallback(this);

        paint = new Paint();
        paint.setAlpha(30);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(2);
        paint.setColor(getResources().getColor(R.color.colorText));
        paint2 = new Paint();
        paint.setAlpha(100);
        paint2.setAntiAlias(true);
        paint2.setStrokeWidth(2);
        paint2.setColor(getResources().getColor(R.color.colorText));

        paint3 = new Paint();
        paint3.setStrokeWidth(30);
        paint3.setColor(getResources().getColor(R.color.colorText));
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        running = true;
        new Thread(this).start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        running = false;
    }

    @Override
    public void run() {
        while (running) {
            draw();
        }
    }


    public void draw() {
        try {
            mCanvas = holder.lockCanvas();
            //清屏
            mCanvas.drawColor(Color.WHITE);
            //draw

            h = getHeight() + getHeight() / 2;
            w = getWidth();
            Log.d("tag", h + "<-h w-> " + w);
            for (int i = 1; i < w; i++) {
                mCanvas.drawLine(i * 60, 0, i * 60, h / 6, paint);
            }
            for (int i = 1; i * 60 < h / 6; i++) {
                mCanvas.drawLine(20, i * 60, w - 20, i * 60, paint);
            }


            for (int i = 1; i < w; i++) {
                mCanvas.drawLine(i * 60, h / 6, i * 60, h / 3, paint2);
            }
            for (int i = 0; i * 60 < h / 6; i++) {
                mCanvas.drawLine(20, (i * 60) + h / 6, w - 20, (i * 60) + h / 6, paint2);
            }


            for (int i = 1; i < w; i++) {
                mCanvas.drawLine(i * 60, h / 3, i * 60, h / 2, paint2);
            }
            for (int i = 0; i * 60 < h / 6; i++) {
                mCanvas.drawLine(20, (i * 60) + h / 3, w - 20, (i * 60) + h / 3, paint2);
            }

            mCanvas.drawLine(20, h / 2, w - 20, h / 2, paint3);
            if (matrix != null) {
                translate += (w - 20) / 5;
                if (translate > (w - 20)) {
                    translate = -getWidth() + 20;
                }

                matrix.setTranslate(translate, 0);
                linearGradient.setLocalMatrix(matrix);
                postInvalidateDelayed(500);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //放在finally中，每次都可以保证绘制的类容提交
            if (mCanvas != null) {
                holder.unlockCanvasAndPost(mCanvas);
            }
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
                        getResources().getColor(R.color.color00c4e6),
                        getResources().getColor(R.color.colorPrimaryDark),
                        getResources().getColor(R.color.colorPrimary),
                        getResources().getColor(R.color.colorText),
//                        getResources().getColor(R.color.color87CEEB)
                }, null, Shader.TileMode.CLAMP);
        paint3.setShader(linearGradient);
//        paint2.setShader(linearGradient);
//        paint.setShader(linearGradient);
        matrix = new Matrix();
    }
}
