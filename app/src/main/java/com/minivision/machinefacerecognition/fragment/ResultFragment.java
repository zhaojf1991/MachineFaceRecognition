package com.minivision.machinefacerecognition.fragment;

import android.animation.ObjectAnimator;
import android.app.Fragment;
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
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.minivision.machinefacerecognition.R;
import com.minivision.machinefacerecognition.activity.log.LogMobot;
import com.minivision.machinefacerecognition.activity.utils.Constant;
import com.minivision.machinefacerecognition.activity.utils.Utils;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Zhaojf on 2017/9/19 0019.
 */

public class ResultFragment extends Fragment {
    @BindView(R.id.pic_imgv)
    ImageView pic;
    @BindView(R.id.pic_imgv2)
    ImageView pic2;
    @BindView(R.id.name_et)
    TextView nameEt;
    @BindView(R.id.id_et)
    TextView idEt;
    @BindView(R.id.work_et)
    TextView workEt;
    @BindView(R.id.tv)
    TextView tv;
    @BindView(R.id.name_ll)
    LinearLayout nameLL;
    @BindView(R.id.id_ll)
    LinearLayout idLL;
    @BindView(R.id.work_ll)
    LinearLayout workLL;
    @BindView(R.id.img1)
    ImageView img1;
    @BindView(R.id.img2)
    ImageView img2;
    @BindView(R.id.img3)
    ImageView img3;
    @BindView(R.id.imgarrow)
    ImageView arrow;

    private Handler handler;
    CountDownTimer timer;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_result, container, false);
        ButterKnife.bind(this, view);
        Glide.with(getActivity()).load(R.drawable.arrow3).into(arrow);


        String name = getActivity().getIntent().getStringExtra("name");
        if (name != null && !name.equals("null")) {
            tv.setVisibility(View.INVISIBLE);
            String number = getActivity().getIntent().getStringExtra("number");
            String work = getActivity().getIntent().getStringExtra("work");


            Toast.makeText(getActivity(), "识别为真脸.", Toast.LENGTH_LONG).show();

            number = number == null ? "" : number;
            work = work == null ? "" : work;
            nameEt.setText(name);
            idEt.setText(number);
            workEt.setText(work);


            if (new File(Constant.SHOWPIC + "/" + name + ".jpg").exists()) {

                Bitmap bitmap1 = BitmapFactory.decodeFile(Constant.SHOWPIC + "/" + name + ".jpg");
                Bitmap bitmap2 = Utils.scaleBitmap(bitmap1, 790, 760);

                pic2.setImageBitmap(makeRoundCorner(bitmap2));
            } else if (new File(Constant.PIC + "/" + name + ".jpg").exists()) {
                pic2.setImageBitmap(makeRoundCorner(BitmapFactory.decodeFile(Constant.PIC + "/" + name + ".jpg")));
            } else {
                Toast.makeText(getActivity(), "请检查底库照片", Toast.LENGTH_LONG).show();
            }


        } else {
            pic2.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.sad));
            nameLL.setVisibility(View.INVISIBLE);
            idLL.setVisibility(View.INVISIBLE);
            workLL.setVisibility(View.INVISIBLE);
            img1.setVisibility(View.INVISIBLE);
            img2.setVisibility(View.INVISIBLE);
            img3.setVisibility(View.INVISIBLE);
            tv.setVisibility(View.VISIBLE);
        }
        pic.setImageDrawable(getActivity().getDrawable(R.mipmap.resultpicbg));


        ObjectAnimator animator = ObjectAnimator.ofFloat(pic, "rotation", 0f, 360f);
        animator.setDuration(8000);
        animator.start();
        return view;
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


}
