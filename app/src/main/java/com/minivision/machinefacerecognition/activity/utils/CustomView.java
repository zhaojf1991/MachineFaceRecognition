package com.minivision.machinefacerecognition.activity.utils;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.view.View;

import com.minivision.machinefacerecognition.R;

/**
 * Created by Zhaojf on 2017/8/31 0031.
 */

public class CustomView extends View {
    Paint paint;
    Path path, dst1;
    Path path2, dst2;
    private float mAnimatorValue;
    float length1, length2;
    PathMeasure measure1, measure2;

    public CustomView(Context context) {
        super(context);
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(getResources().getColor(R.color.colorText));
        paint.setStrokeWidth(20);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);

        path = new Path();
        RectF rectF = new RectF(500, 860, 1100, 1460);
        path.arcTo(rectF, 50, 350);
        measure1 = new PathMeasure();
        length1 = measure1.getLength();
        measure1.setPath(path, false);
        length1 = measure1.getLength();
        dst1 = new Path();


        path2 = new Path();

        path2.moveTo(630, 1230);
        path2.lineTo(800, 1330);
        path2.lineTo(1020, 1030);
        measure2 = new PathMeasure(path2, false);
        length2 = measure2.getLength();
        dst2 = new Path();


        final ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mAnimatorValue = (float) valueAnimator.getAnimatedValue();
                invalidate();
            }
        });
        valueAnimator.setDuration(1000);
//        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator.start();


    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


        dst1.reset();
        dst1.lineTo(0, 0);
        float stop = length1 * mAnimatorValue;
        measure1.getSegment(0, stop, dst1, true);
        canvas.drawPath(dst1, paint);


        dst2.reset();
        dst2.lineTo(0, 0);

        float stop2 = length2 * mAnimatorValue;
        measure2.getSegment(0, stop2, dst2, true);
        canvas.drawPath(dst2, paint);


    }
}
