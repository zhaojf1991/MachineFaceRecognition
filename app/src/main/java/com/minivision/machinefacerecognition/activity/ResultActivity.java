package com.minivision.machinefacerecognition.activity;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.minivision.machinefacerecognition.R;
import com.minivision.machinefacerecognition.activity.dbutils.DailyDBUtil;
import com.minivision.machinefacerecognition.activity.dbutils.FaceDBUtils;
import com.minivision.machinefacerecognition.activity.entity.PersonDaily;
import com.minivision.machinefacerecognition.activity.log.LogMobot;
import com.minivision.machinefacerecognition.activity.utils.Constant;
import com.minivision.machinefacerecognition.activity.utils.Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.minivision.machinefacerecognition.activity.log.LogMobot.logd;
import static com.minivision.machinefacerecognition.activity.log.LogMobot.loge;

/**
 * Created by Zhaojf on 2017/8/17 0017.
 */

public class ResultActivity extends Activity {
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
    private String mControllerIp, mControllerPort, mDoorOpenDuration;//一楼门禁配置
    private String mServerIp, mServerPort, mDoorIp, mDoorPort, mDoorId, mDoorControllType;//五楼门禁配置
    private int mDoorLocation;
    SharedPreferences sp;
    SharedPreferences.Editor edit;
    FaceDBUtils faceDBUtils;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        ButterKnife.bind(this);
        faceDBUtils = new FaceDBUtils(ResultActivity.this);
        sp = getSharedPreferences("threshold", Context.MODE_PRIVATE);
        edit = sp.edit();
        getThreshold();

        try {
            Glide.with(this).load(R.drawable.arrow3).into(arrow);


            String name = getIntent().getStringExtra("name");
            if (name != null && !name.equals("")) {
                Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                nameEt.setText(name);
                tv.setVisibility(View.INVISIBLE);
                LogMobot.logd("hour --->" + hour);
                LogMobot.logd("stattime --->" + faceDBUtils.getstByName(name));
                LogMobot.logd("endtime --->" + faceDBUtils.getetByName(name));
                if (faceDBUtils.getstByName(name) > hour || hour > faceDBUtils.getetByName(name)) {
                    Toast.makeText(this, "不在允许访问时间.", Toast.LENGTH_LONG).show();
                    LogMobot.logd("不在允许访问时间");
                } else {

                    doFaceDataBase(name);
                    openDoor(mDoorLocation);
                    Toast.makeText(this, "识别为真脸.", Toast.LENGTH_LONG).show();

                }
                if (new File(Constant.SHOWPIC + "/" + name + ".jpg").exists()) {

                    Bitmap bitmap1 = BitmapFactory.decodeFile(Constant.SHOWPIC + "/" + name + ".jpg");
                    Bitmap bitmap2 = Utils.scaleBitmap(bitmap1, 790, 760);

                    pic2.setImageBitmap(makeRoundCorner(bitmap2));
                } else if (new File(Constant.SHOWPIC + "/" + name + ".png").exists()) {
                    Bitmap bitmap1 = BitmapFactory.decodeFile(Constant.SHOWPIC + "/" + name + ".png");
                    Bitmap bitmap2 = Utils.scaleBitmap(bitmap1, 790, 760);
                    pic2.setImageBitmap(makeRoundCorner(bitmap2));
                } else if (new File(Constant.PIC + "/" + name + ".jpg").exists()) {

                    pic2.setImageBitmap(makeRoundCorner(BitmapFactory.decodeFile(Constant.PIC + "/" + name + ".jpg")));
                } else if (new File(Constant.PIC + "/" + name + ".png").exists()) {

                    pic2.setImageBitmap(makeRoundCorner(BitmapFactory.decodeFile(Constant.PIC + "/" + name + ".png")));
                } else {
                    Toast.makeText(this, "请检查底库照片", Toast.LENGTH_LONG).show();
                }


            } else

            {
                pic2.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.sad));
                nameLL.setVisibility(View.INVISIBLE);
                idLL.setVisibility(View.INVISIBLE);
                workLL.setVisibility(View.INVISIBLE);
                img1.setVisibility(View.INVISIBLE);
                img2.setVisibility(View.INVISIBLE);
                img3.setVisibility(View.INVISIBLE);
                tv.setVisibility(View.VISIBLE);
            }
            pic.setImageDrawable(

                    getDrawable(R.mipmap.resultpicbg));

            ObjectAnimator animator = ObjectAnimator.ofFloat(pic, "rotation", 0f, 360f);
            animator.setDuration(8000);
            animator.start();

            timer = new

                    CountDownTimer(2000, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            //                LogMobot.loge("时间" + millisUntilFinished);
                        }

                        @Override
                        public void onFinish() {
                            //                LogMobot.loge("时间到");
                            Intent intent = new Intent(ResultActivity.this, RecongnitionActivity.class);
                            intent.putExtra("name", getIntent().getStringExtra("name"));
                            startActivity(intent);
                            overridePendingTransition(R.anim.out, R.anim.in);// 淡出淡入动画效果
                            finish();
                        }
                    }

            ;
            timer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getThreshold() {


        mDoorLocation = sp.getInt("door_location", 1);
        //一楼设置
        mControllerIp = sp.getString("controller_ip", getString(R.string.controller_ip_default));
        mControllerPort = sp.getString("controller_port", getString(R.string.controller_port_default));
        mDoorOpenDuration = sp.getString("open_door_duration", getString(R.string.duration_default));
        //五楼设置
        mServerIp = sp.getString("server_ip", getString(R.string.server_ip_default));
        mServerPort = sp.getString("server_port", getString(R.string.server_port_default));
        mDoorIp = sp.getString("door_ip", getString(R.string.door_ip_default));
        mDoorPort = sp.getString("door_port", getString(R.string.door_port_default));
        mDoorId = sp.getString("door_id", getString(R.string.door_id_default));
        mDoorControllType = sp.getString("door_control_type", getString(R.string.door_control_type_default));
    }


    public static Bitmap makeRoundCorner(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();


//        logd("w---> " + width + "h ----> " + height);
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

    float y_start, y_end;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {

            y_start = event.getY();
        }

        if (event.getAction() == MotionEvent.ACTION_UP) {
            //当手指离开的时候

            y_end = event.getY();
            if (y_start - y_end > 50) {
                timer.cancel();
                Intent intent = new Intent(ResultActivity.this, SettingCheckActivity.class);
                startActivity(intent);
                finish();

            }
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
        }
        finish();
    }

    private void openDoor(final int doorLocation) {
        if (doorLocation == 0) {
            LogMobot.loge("doorLocation===0");
            openFirstFloorDoor();
        } else if (doorLocation == 1) {
            openFifthFloorDoor();
            LogMobot.loge("doorLocation===1");
        }
    }

    private void openFifthFloorDoor() {
        new Thread() {
            public void run() {
                try {
                    Socket socket = new Socket(mServerIp, Integer.parseInt(mServerPort));
                    socket.setSoTimeout(8000);
                    //发送数据给服务端
//                    LogMobot.loge("openDoor: ip = " + mServerIp);
//                    LogMobot.loge("openDoor: port = " + mServerPort);
//                    LogMobot.loge("openDoor: id = " + mDoorId);
//                    LogMobot.loge("openDoor: type = " + mDoorControllType);

                    String data = "<root>"
                            + "<data commandtype=\"cmd_type_dev_param\"  command=\"0016\"  RemoteControlType=\"" + mDoorControllType + "\""
                            + " doorId=\"" + mDoorId + "\""
                            + " controlIp=\"" + mDoorIp + "\""
                            + " controlPort=\"" + mDoorPort + "\""
                            + " controlAddr=\"1\" controlType=\"2\" controlPsw=\"0000000000\" exitMode=\"0\">"
                            + "</data>"
                            + "</root></XSKJ2017>";

                    LogMobot.loge("openDoor: " + data);

                    OutputStream outputStream = socket.getOutputStream();
                    outputStream.write(data.getBytes("UTF-8"));
                    socket.shutdownOutput();
                    //读取数据
                    BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    final String line = br.readLine();
                    LogMobot.loge("openDoor: " + line);
                    br.close();
                    socket.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void openFirstFloorDoor() {
        new Thread() {
            public void run() {
                try {
                    if (mDoorOpenDuration.length() == 1) {
                        sendCommand("on1:0" + mDoorOpenDuration);
                    } else {
                        sendCommand("on1:" + mDoorOpenDuration);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    try {
                        OutputStream os = new FileOutputStream("/sdcard/openDoorFile.txt");
                        os.write(e.toString().getBytes());
                        os.flush();
                        os.close();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
//                    Toast.makeText(ResultActivity.this, "提示：" + e.toString(), Toast.LENGTH_SHORT).show();

//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            Toast.makeText(ResultActivity.this, "开门异常.", Toast.LENGTH_SHORT).show();
//                        }
//                    });
                }
            }
        }.start();
    }

    private void sendCommand(String command) throws IOException {
        Socket socket = new Socket(mControllerIp, Integer.parseInt(mControllerPort));
        LogMobot.logd(mControllerIp + "- -" + Integer.parseInt(mControllerPort));
        socket.setSoTimeout(5000);
        loge(command);
        OutputStream outputStream = socket.getOutputStream();
        outputStream.write(command.getBytes("UTF-8"));
        socket.shutdownOutput();
    }

    private void doFaceDataBase(final String nameStr) {
        new AsyncTask<Void, Void, String[]>() {
            @Override
            protected void onPreExecute() {

            }

            @Override
            protected String[] doInBackground(Void... params) {
                long start = System.currentTimeMillis();

                //当前时间
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date d1 = new Date(start);
                String nowTime = format.format(d1);

//                String number = faceDBUtils.getNumberByName(nameStr);
                String work = faceDBUtils.getWorkByName(nameStr);
                String pic = faceDBUtils.getPicByName(nameStr);
                String number = faceDBUtils.getPhoneByName(nameStr);
                logd("PIC ---> " + pic);
                SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
                Date d2 = new Date(start);
                String saveTime = format2.format(d2);
                Utils.fileName2Person(nameStr + saveTime, Constant.TAKEPIC_PATCH);

                DailyDBUtil dailyDBUtil = new DailyDBUtil(ResultActivity.this);
                PersonDaily personDaily = new PersonDaily();
                personDaily.setName(nameStr);
                personDaily.setNum(number);
                personDaily.setPic(pic);
                personDaily.setTakeTime(nowTime);

                personDaily.setTakePicPath(Constant.TAKEPIC_PATCH + "/" + nameStr + saveTime + ".jpg");
//                LogMobot.loge(personDaily.getTakePicPath());
                boolean insert = dailyDBUtil.insert(personDaily);
                logd("存储--> " + insert);


                logd("name -->" + nameStr);
//
                return new String[]{number, work};

            }

            @Override
            protected void onPostExecute(String[] strings) {
                super.onPostExecute(strings);
                String number = "";
                String work = "";
                number = strings[0] == null ? "" : strings[0];
                work = strings[1] == null ? "" : strings[1];
                idEt.setText(number);
                workEt.setText(work);
            }
        }.execute();
    }

}
