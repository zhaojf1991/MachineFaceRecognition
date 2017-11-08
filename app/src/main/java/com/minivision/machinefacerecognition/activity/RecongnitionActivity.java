package com.minivision.machinefacerecognition.activity;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.minivision.face.recognize.RecognizedFace;
import com.minivision.face.recognize.Recognizer;
import com.minivision.face.recognize.RecognizerWrapper;
import com.minivision.livebody.Livingbody;
import com.minivision.machinefacerecognition.R;
import com.minivision.machinefacerecognition.activity.log.LogMobot;
import com.minivision.machinefacerecognition.activity.utils.Constant;
import com.minivision.machinefacerecognition.activity.utils.HelpView;
import com.minivision.machinefacerecognition.activity.utils.LiveFace;
import com.minivision.machinefacerecognition.activity.utils.ResultMap2;
import com.minivision.machinefacerecognition.activity.utils.ScanView;
import com.minivision.machinefacerecognition.activity.utils.UnRealView;
import com.minivision.machinefacerecognition.activity.utils.Utils;
import com.minivision.machinefacerecognition.activity.utils.Voicer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import static com.minivision.machinefacerecognition.activity.log.LogMobot.logd;
import static com.minivision.machinefacerecognition.activity.log.LogMobot.loge;

/**
 * Created by Zhaojf on 2017/8/16 0016.
 */

public class RecongnitionActivity extends Activity implements SurfaceHolder.Callback, Camera.PreviewCallback {
    private int cameraFront = Camera.CameraInfo.CAMERA_FACING_FRONT;
    private Camera mCamera;
    private SurfaceView surface;
    //    private FaceRecognizer mFaceRecognizer;
    private Recognizer mRecognizer;
    private volatile boolean isThreadWorking;
    private Thread detectThread;
    private String thresholdValue, intervalValue;
    private RelativeLayout timeRv;
    private CountDownTimer timer, timer2;
    private Voicer mVoicer;
    private RelativeLayout root;
    private ImageView gif;
    private int mScreenWidth, mScreenHeight;
    private ScanView scanView;
    private ResultMap2 resultMap;
    //    private FaceManager faceManager;
    private FrameLayout frameLayout;
    UnRealView unRealView;
    long start = System.currentTimeMillis();
    boolean isRecognize = true;
    FragmentManager fragmentManager;
    Livingbody livingbody;
    HelpView helpView;

    long start1;

    int featureLibSize = 0;
    String name;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        long start1 = System.currentTimeMillis();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_recongnition);


        name = getIntent().getStringExtra("name");
        getThreshold();
        initView();
        mRecognizer = RecognizerWrapper.instance().getRecognizer();
        featureLibSize = mRecognizer.getFeatureLibSize();
        LogMobot.loge("人脸库 " + featureLibSize);

        surface.setVisibility(View.VISIBLE);

        mVoicer = Voicer.instance();
        timer = new CountDownTimer(300000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
//                LogMobot.loge("时间" + millisUntilFinished);
                unRealView.setVisibility(View.GONE);
                helpView.setVisibility(View.GONE);

            }

            @Override
            public void onFinish() {
                LogMobot.loge("时间到");
                unRealView.setVisibility(View.GONE);
                Message msg = new Message();
                msg.what = Constant.TIMEOVER;
                handler.sendMessage(msg);
            }
        };
        timer.start();

        SurfaceHolder holder = surface.getHolder();
        holder.addCallback(this);
        holder.setFormat(ImageFormat.NV21);
        long end = System.currentTimeMillis();
        loge("onCreate总消耗 ：" + (end - start1));
        LogMobot.logd("onCreate:" + (end - start1));
        fragmentManager = getFragmentManager();
    }


    public void getThreshold() {
        SharedPreferences sp;
        SharedPreferences.Editor edit;
        sp = getSharedPreferences("threshold", Context.MODE_PRIVATE);
        long start1 = System.currentTimeMillis();
        edit = sp.edit();
        edit.putString("isChange", "no");
        thresholdValue = sp.getString("thresholdValue", "82");
        intervalValue = sp.getString("intervalValue", "0");

        long end = System.currentTimeMillis();
        LogMobot.logd("getThreshold:" + (end - start1));

    }

    public void initView() {
        long start1 = System.currentTimeMillis();

        root = (RelativeLayout) findViewById(R.id.root);
        surface = (SurfaceView) findViewById(R.id.surface);
        RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        scanView = new ScanView(this);
        scanView.setAlpha(0.5f);
        scanView.setLayoutParams(param);

        root.addView(scanView);
        AlphaAnimation alpha = new AlphaAnimation(2.0f, 0.0f);
        alpha.setDuration(4000);
        alpha.setRepeatCount(Animation.INFINITE);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.translate);
        animation.setRepeatCount(Animation.INFINITE);

        AnimationSet set = new AnimationSet(true);
        set.addAnimation(alpha);
        set.addAnimation(animation);
        scanView.startAnimation(set);

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) surface.getLayoutParams();
        Point p = Utils.getScreenMetrics(this);
        params.width = p.x;
        params.height = p.y;

        mScreenWidth = p.x;
        mScreenHeight = p.y;
        surface.setLayoutParams(params);

        resultMap = ResultMap2.instance();
        if (resultMap == null) {
            LogMobot.logd("resultMap: is null");
        }
        if (name != null) {
            resultMap.add(name, Integer.parseInt(intervalValue));
        }

        timeRv = (RelativeLayout) this.findViewById(R.id.time_rv);

        timeRv.setVisibility(View.INVISIBLE);
        unRealView = new UnRealView(this);
        root.addView(unRealView);
        unRealView.setVisibility(View.GONE);
        long end = System.currentTimeMillis();
        LogMobot.logd("initView:" + (end - start1));

        frameLayout = (FrameLayout) this.findViewById(R.id.content);
        frameLayout.setVisibility(View.GONE);

        helpView = new HelpView(this);
        root.addView(helpView);
        helpView.setVisibility(View.GONE);
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        long time = System.currentTimeMillis();


        mCamera = Camera.open(cameraFront);


        logd("Camera open....");
        try {
            mCamera.setPreviewDisplay(surface.getHolder());
        } catch (IOException e) {
            logd("OpenCamera: 开启前置Camera失败...");
            e.printStackTrace();
        }
        long endtime2 = System.currentTimeMillis();
        loge("surfaceCreated：" + (endtime2 - time));
        LogMobot.logd("surfaceCreated:" + (endtime2 - time));
    }


    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        long time = System.currentTimeMillis();
        //没有预览结果
        if (holder.getSurface() == null) {
            return;
        }

        //停止当前预览

        mCamera.stopPreview();

        Camera.Parameters parameters = mCamera.getParameters();
        configureCamera(width, height, parameters);
        setDisplayOrientation();
        //启动摄像头预览
        startPreview();
        long endtime2 = System.currentTimeMillis();
        loge("surfaceChanged：" + (endtime2 - time));
        LogMobot.logd("surfaceChanged: " + (endtime2 - time));
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }


    private void setOptimalPreviewSize(Camera.Parameters cameraParameters) {
        long time = System.currentTimeMillis();
        LogMobot.logd("setOptimalPreviewSize");
        int previewWidth = 1280;
        int previewHeight = 720;
//        logd("setOptimalPreviewSize: " + previewWidth + "----" + previewHeight);
        cameraParameters.setPreviewSize(1280, 720);
        long endtime2 = System.currentTimeMillis();
        loge("setOptimalPreviewSize：" + (endtime2 - time));
        LogMobot.logd("setOptimalPreviewSize: " + (endtime2 - time));
    }

    private void configureCamera(int width, int height, Camera.Parameters parameters) {
        long time = System.currentTimeMillis();

        //设置预览界面尺寸和自动对焦:
//        setOptimalPreviewSize(parameters, width, height);
        setOptimalPreviewSize(parameters);

        List<String> focusModes = parameters.getSupportedFocusModes();
        if (focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE))
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);

        List<String> sceneModes = parameters.getSupportedSceneModes();
        if (sceneModes.contains(Camera.Parameters.SCENE_MODE_PORTRAIT)) {
            parameters.setSceneMode(Camera.Parameters.SCENE_MODE_PORTRAIT);
        }

        List<String> whiteBalanceModes = parameters.getSupportedWhiteBalance();
        if (whiteBalanceModes.contains(Camera.Parameters.WHITE_BALANCE_AUTO)) {
            parameters.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_AUTO);
        }
        //设置相机参数:
        mCamera.setParameters(parameters);
        long endtime2 = System.currentTimeMillis();
        LogMobot.logd("configureCamera: " + (endtime2 - time));
    }

    private void setDisplayOrientation() {
        long time = System.currentTimeMillis();
        //设置显示方向
        int displayRotation = Utils.getDisplayRotation(RecongnitionActivity.this);
        int displayOrientation = Utils.getDisplayOrientation(displayRotation, cameraFront);

        mCamera.setDisplayOrientation(displayOrientation);
        long endtime2 = System.currentTimeMillis();
        LogMobot.logd("setDisplayOrientation: " + (endtime2 - time));
    }


    private void startPreview() {
        long time = System.currentTimeMillis();
        if (mCamera != null) {
//            LogMobot.logd("do startPreview");
            isThreadWorking = false;
            mCamera.startPreview();
            mCamera.setPreviewCallback(this);
        }
        long endtime2 = System.currentTimeMillis();
        LogMobot.logd("startPreview: " + (endtime2 - time));

    }


    @Override
    public void onPreviewFrame(final byte[] data, final Camera camera) {
//        LogMobot.logd("onPreviewFrame");
        unRealView.setVisibility(View.GONE);
        long time1 = System.currentTimeMillis();
        if (!isThreadWorking && data != null) {
            isThreadWorking = true;
            waitForDetectThreadComplete();

            final long startime1 = System.currentTimeMillis();
            if (isRecognize) {
                detectThread = new Thread(new DetectRunnable(data) {
                    @Override
                    public void onSuccess(byte[] datas) {
//                    LogMobot.loge("onSuccess execute ");
                        if (datas != null && camera != null) {
                            getCameraPreviewPic(camera, datas);
                        }
                    }
                });
                detectThread.start();
            }
        }
    }

    public void getCameraPreviewPic(Camera camera, byte[] data) {
//        LogMobot.logd("抓拍+1");
        long time1 = System.currentTimeMillis();
        final YuvImage image = new YuvImage(data, ImageFormat.NV21, 1280, 720, null);
        ByteArrayOutputStream os = new ByteArrayOutputStream(data.length);
        if (!image.compressToJpeg(new Rect(0, 0, 1280, 720), 100, os)) {
            return;
        }
        byte[] tmp = os.toByteArray();
        Bitmap bmp = BitmapFactory.decodeByteArray(tmp, 0, tmp.length);
        int orignheight = bmp.getHeight();
        int orignwidth = bmp.getWidth();

        int scale;
        if (orignheight > orignwidth) {
            scale = orignheight / orignwidth;
        } else {
            scale = orignwidth / orignheight;
        }
//        LogMobot.logd("scale ---> " + scale);

        Utils.savePic2SD(Constant.TAKEPIC_PATCH, Utils.rotateBitmap(Utils.scaleBitmap(bmp, 450, 300), -90));
        long end = System.currentTimeMillis();

    }


    private void waitForDetectThreadComplete() {
        long time1 = System.currentTimeMillis();
//        LogMobot.logd("waitForDetectThreadComplete");
        if (detectThread == null) {
            return;
        }
        if (detectThread.isAlive()) {
            try {
                detectThread.join();
                detectThread = null;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        long end = System.currentTimeMillis();
    }


    abstract class DetectRunnable implements Runnable {

        long time1 = System.currentTimeMillis();
        private byte[] frame;
        private int width = 1280, height = 720;

        public abstract void onSuccess(byte[] data);

        public DetectRunnable(byte[] data) {
            frame = data;
        }

        @Override
        public void run() {
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
//            int faceValue = faceManager.checkLiveBody(frame, 1280, 720, 90, true, 6.5f);

//            LogMobot.logd("realface check");
            int faceValue = LiveFace.getInstance().identifyCameraPreview(frame, 1280, 720, Recognizer.Orientation.RIGHT_TURNED);
//            LogMobot.logd("fakeface -->" + faceValue);
            switch (faceValue) {
                case 1:
                    long time3 = System.currentTimeMillis();
                    RecognizedFace recognizedFace = mRecognizer.recognizeCameraPreview(frame, width, height, Recognizer.Orientation.RIGHT_TURNED);
//                    LogMobot.logd("recognize -->" + recognizedFace.toString());
//                  RecognizeResult.RecognizeResultsBean recognizeResultsBean = Utils.processRecognizeResult(recognizeResults);
//                    RecognizeResult.RecognizeResultsBean recognizeResultsBean = Utils.fastRecognizeResult(recognizeResults);
//                    LogMobot.logd("recognize" + recognizedFace.getId());
                    LogMobot.logd("recognize takes time " + (System.currentTimeMillis() - time3));
                    if (recognizedFace != null) {
                        LogMobot.logd("recognize  " + recognizedFace.toString());
//                        unRealView.setVisibility(View.GONE);
                        if (!resultMap.contains(recognizedFace.getId())) {
//                            long time = System.currentTimeMillis();
//                            LogMobot.logd("recognizedFace.getScore() " + recognizedFace.getScore());
//                            LogMobot.logd("resultMap.add: " + (System.currentTimeMillis() - time));
//                            resultMap.add(recognizedFace.getId(), Integer.valueOf(intervalValue));
                            if (recognizedFace.getId() != null && frame != null) {
                                start1 = System.currentTimeMillis();
                                if (recognizedFace.getScore() >= Integer.parseInt(thresholdValue)) {

                                    long end = System.currentTimeMillis();
                                    isRecognize = false;
                                    LogMobot.logd("onSuccess: " + (end - time1));
                                    LogMobot.logd("send MESSAGE_RECOGNIZE 0");
                                    handler.obtainMessage(Constant.MESSAGE_RECOGNIZE, recognizedFace).sendToTarget();
                                    LogMobot.logd("send MESSAGE_RECOGNIZE 1");
                                    isThreadWorking = true;
                                    onSuccess(frame);

                                } else if (recognizedFace.getScore() >= Integer.parseInt(thresholdValue) - 50) {
////                            handler.obtainMessage(-4).sendToTarget();
//                                    isThreadWorking = false;
                                } else {
                                    isRecognize = false;
                                    handler.obtainMessage(-3, recognizedFace).sendToTarget();
                                    LogMobot.logd("send -3");
                                    isThreadWorking = true;
                                    handler.removeCallbacks(null);
                                }
                            }

                        }

                    } else {
                        if (featureLibSize == 0) {
                            handler.obtainMessage(-4).sendToTarget();
                        } else {
                            handler.obtainMessage(-2).sendToTarget();
                        }
                    }
                    break;
                case 0:
                    long end = System.currentTimeMillis();
                    handler.obtainMessage(-1).sendToTarget();
//                            Toast.makeText(RecongnitionActivity.this, "假脸", Toast.LENGTH_SHORT).show();
                    isThreadWorking = true;
                    loge("预览到假脸总时间：" + (end - time1));
                    LogMobot.logd("onPreviewFrame假脸: " + (end - time1));
                    break;

            }

            isThreadWorking = false;
        }

    }


    int recLen = 10;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
//            LogMobot.logd("handler");
            long time = System.currentTimeMillis();
            switch (msg.what) {
                case Constant.MESSAGE_RECOGNIZE:
                    handler.removeCallbacks(null);
                    RecognizedFace bean = (RecognizedFace) msg.obj;
                    dealWithRecognizeResult(bean);
                    timeRv.setVisibility(View.INVISIBLE);
                    break;
                case Constant.TIMEOVER:
                    scanView.clearAnimation();
                    root.removeView(scanView);
                    timeRv.setVisibility(View.VISIBLE);
                    break;
                case -1:
                    handler.removeCallbacks(null);
                    unRealView.setVisibility(View.VISIBLE);
//                    Toast.makeText(RecongnitionActivity.this, "非真人", Toast.LENGTH_SHORT).show();
//                    dealwithunrealFace();
                    long end2 = System.currentTimeMillis();
                    loge("hanlder -1 ：" + (end2 - time));
                    break;
                case -2:
                    helpView.setVisibility(View.VISIBLE);
                    break;
                case -3:
                    handler.removeCallbacks(null);
                    RecognizedFace bean2 = (RecognizedFace) msg.obj;
                    dealwithfail(bean2);
                    timeRv.setVisibility(View.INVISIBLE);
                    break;
                case -4:
                    Toast.makeText(RecongnitionActivity.this, "未检测到人脸库，请添加.", Toast.LENGTH_SHORT).show();
                    break;

            }
        }
    };


    public void dealWithRecognizeResult(RecognizedFace bean) {
        isRecognize = false;
        handler.removeCallbacks(null);
        isThreadWorking = true;
        final String name = bean.getId();
        String voiceContent = "小视欢迎" + name + "光临";
        mVoicer.speck(voiceContent);
        Intent intent = new Intent(RecongnitionActivity.this, ResultActivity.class);
        intent.putExtra("name", name);
        long end = System.currentTimeMillis();
        loge("识别到人脸，跳转" + (end - start1));
        timer.cancel();
        waitForDetectThreadComplete();
        startActivity(intent);
        overridePendingTransition(R.anim.out, R.anim.in);// 淡出淡入动画效果
        scanView.clearAnimation();
        root.removeView(scanView);
        finish();
    }


    public void dealwithfail(RecognizedFace bean) {
        isRecognize = false;
        resultMap.remove(bean.getId());
        Intent intent = new Intent(RecongnitionActivity.this, ResultActivity.class);
        intent.putExtra("name", "");
        timer.cancel();
        long end = System.currentTimeMillis();
        loge("识别到人脸不在人脸库，跳转" + (end - start1));
        waitForDetectThreadComplete();
        startActivity(intent);
        overridePendingTransition(R.anim.out, R.anim.in);// 淡出淡入动画效果
        scanView.clearAnimation();
        root.removeView(scanView);
        finish();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        getThreshold();
        LogMobot.logd("onRestart");
    }


    @Override
    protected void onStop() {
        super.onStop();
//        LogMobot.loge("onStop");
        isThreadWorking = true;
//        getThreshold();
//        LogMobot.logd("onStop");
        if (mCamera != null) {
            mCamera.stopPreview();
//            mCamera.release();
//            mCamera = null;
//
        }

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
                isThreadWorking = true;
                waitForDetectThreadComplete();
                Intent intent = new Intent(RecongnitionActivity.this, SettingCheckActivity.class);
                startActivity(intent);
                finish();
            }
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogMobot.logd("onDestroy");
//        LogMobot.loge("onDestroy");
        try {
            timer.cancel();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(new Intent(RecongnitionActivity.this, MainActivity.class));
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.leftin, R.anim.rightout);
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == event.KEYCODE_BACK) {
            isThreadWorking = true;
            waitForDetectThreadComplete();
//            startActivity(new Intent(RecongnitionActivity.this, MainActivity.class));
            Intent intent = new Intent(new Intent(RecongnitionActivity.this, MainActivity.class));
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
