package com.minivision.machinefacerecognition.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.minivision.face.Config;
import com.minivision.face.auth.DeviceInfo;
import com.minivision.face.auth.Licensor;
import com.minivision.face.recognize.RecognizerWrapper;
import com.minivision.livebody.LivingbodyConfig;
import com.minivision.machinefacerecognition.R;
import com.minivision.machinefacerecognition.activity.log.LogMobot;
import com.minivision.machinefacerecognition.activity.utils.LiveFace;
import com.minivision.machinefacerecognition.activity.utils.Utils;

import org.json.JSONException;

import java.io.File;
import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.minivision.machinefacerecognition.R.id.init;

public class MainActivity extends Activity {

    private final static String INTERNET = "android.permission.INTERNET";
    private final static String CAMERA = "android.permission.CAMERA";//相机权限
    private static final String WRITE_EXTERNAL_STORAGE = "android.permission.WRITE_EXTERNAL_STORAGE";//读写权限
    private static final String READ_EXTERNAL_STORAGE = "android.permission.READ_EXTERNAL_STORAGE";
    private String isChage;
    SharedPreferences sp;
    ProgressDialog progressDialog;
    Config mConfig;
    RecognizerWrapper mRecognizerWrapper;
    Licensor licensor;
    String intervalValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);


//        isChage = sp.getString("isChange", "no");
//        LogMobot.logd("是否读取文件夹" + isChage);

//        initFaceEngine();
        //判断android版本，大于23动态申请权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkAllPremissions(new String[]{WRITE_EXTERNAL_STORAGE, CAMERA, INTERNET, READ_EXTERNAL_STORAGE})) {
                return;
            }
            ActivityCompat.requestPermissions(this, new String[]{CAMERA, WRITE_EXTERNAL_STORAGE, INTERNET, READ_EXTERNAL_STORAGE}, 100);
        } else {

        }
    }

    /*
     * 判断所有权限
     * */
    public boolean checkAllPremissions(String[] permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            boolean result = true;
            for (int granResult : grantResults) {
                if (granResult != PackageManager.PERMISSION_GRANTED) {
                    result = false;
                    break;
                }
            }
            if (result) {

            } else {
                Toast.makeText(MainActivity.this, "请在系统设置界面手动赋予权限", Toast.LENGTH_SHORT).show();
            }
        }

    }


    private void initFaceEngine() {

        new AsyncTask<Void, Void, Void>() {


            @Override
            protected void onPreExecute() {

            }

            @Override
            protected Void doInBackground(Void... params) {
//                boolean flag = AuthManager.instance().localCheck(path, md5);
//                if (!flag) {
//                    authManagerInit();
//                }
//                String macAddress = getMacAddress();
//                LogMobot.logd("macAddress:" + macAddress);
//                FaceRecognizer.initEngine(MainActivity.this, path);
//                Voicer.init(MainActivity.this.getApplicationContext());
//                String macAddress2 = getMacAddress();
//                LogMobot.logd("macAddress2:" + macAddress2);
//                if (progressDialog != null) {
//                    progressDialog.show();
//                }
//                if (isChage.equals("yes")) {
//                    File file = new File(Constant.PIC);
//                    File[] images = file.listFiles();
//                    if (images != null && images.length > 0) {
//                        FaceRecognizer faceRecognizer = FaceRecognizer.instance();
//                        for (File f : images) {
//                            String filename = f.getName();
//                            int index = filename.indexOf(".");
//                            if (index != -1) {
//                                filename = filename.substring(0, index);
//                            }
//                            Bitmap bitmap = BitmapFactory.decodeFile(f.getAbsolutePath());
//                            float[] feature = faceRecognizer.getFeature(bitmap);
//                            faceRecognizer.addFeature(filename, feature);
//                        }
//                        faceRecognizer.updateFeatureToFile();
//                    }
//                }

                //0 为首次安装
                sp = getSharedPreferences("threshold", Context.MODE_PRIVATE);
                String fistinstall = sp.getString("fistinstall", "0");
                if (fistinstall.equals("0")) {
                    LogMobot.logd("首次安装");
                    File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Minivision/FaceRecognize/Result/picLib.xml");
                    if (file.exists()) {

                        file.delete();
                        LogMobot.logd("删除");
                    }
                }

                SharedPreferences.Editor edit = sp.edit();
                edit.putString("fistinstall", "1");
                edit.commit();


                mConfig = new Config();
                LogMobot.logd("init 1");
                mRecognizerWrapper = RecognizerWrapper.instance();
                LogMobot.logd("init 2");
                LivingbodyConfig config = new LivingbodyConfig();

                LogMobot.logd("init 3");
                int init = LiveFace.getInstance().init(MainActivity.this, config);
                LogMobot.logd("活体初始化-->" + init);

                int result = -999;
                if (!mConfig.licenceExist()) {// 授权文件不存在
                    licensor = new Licensor(mConfig);
                    // 获取设备信息
                    DeviceInfo deviceInfo = licensor.getDeviceInfo();

                    if (deviceInfo != null) {
                        LogMobot.logd("deviceInfo: " + deviceInfo.toString());

                        //    调用获取 licence 的接口
                        int ret = 0;
                        try {
                            ret = Utils.getLicence(deviceInfo, mConfig);
                            LogMobot.logd("ret: " + ret);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (ret == 1) {
                            // 初始化识别引擎
                            result = mRecognizerWrapper.init(MainActivity.this, mConfig);
                            LogMobot.logd("result: " + result);
                        } else {
                            LogMobot.logd("getLicence: failed");
                        }
                    } else {
                        LogMobot.logd("获取设备信息失败");
                    }
                } else { // 授权文件存在
                    result = mRecognizerWrapper.init(MainActivity.this, mConfig);
                }


                LogMobot.logd("onCreate: init recognizer result = " + result);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
                Toast.makeText(MainActivity.this, "初始化完成...", Toast.LENGTH_SHORT).show();
                if (checkAllPremissions(new String[]{WRITE_EXTERNAL_STORAGE, CAMERA, INTERNET, READ_EXTERNAL_STORAGE})) {
                    startActivity(new Intent(MainActivity.this, RecongnitionActivity.class));
                    finish();
                } else {
                    Toast.makeText(MainActivity.this, "请到系统设置界面授予相应使用权限.", Toast.LENGTH_SHORT).show();
                }


            }
        }.execute();
    }


    @OnClick(init)
    public void goinit() {
        Toast.makeText(MainActivity.this, "引擎初始化中...", Toast.LENGTH_SHORT).show();
        initFaceEngine();
        progressDialog = ProgressDialog.show(MainActivity.this, "引擎初始化...", "Please wait...");

    }

    private long mExitTime;


    private static boolean isExit = false;
    Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            isExit = false;
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void exit() {

        if (!isExit) {
            isExit = true;
            Toast.makeText(getApplicationContext(), "再按一次退出程序",
                    Toast.LENGTH_SHORT).show();
            // 利用handler延迟发送更改状态信息
            mHandler.sendEmptyMessageDelayed(0, 2000);
        } else {


            finish();
            System.exit(0);
//            android.os.Process.killProcess(android.os.Process.myPid());
        }
        //授权
//    String authCode = "Y1F5FA";
//    String companyName = "测试客户";
//    String identifier = "$2a$10$5FAACv1Gzh1rXHQToPjGFO5smaRLl8Cl.Jphx8ZpdQZUDtBS3IucK";
//    String url = "https://118.31.114.240:8444/api/v1/getLicenseFile";
//    String md5 = "";
//    String licFileBase64Str = "";
//    String path = "/sdcard/licfi.txt";

//    public void authManagerInit() {
//        WeierInfo info = new WeierInfo();
//        boolean flag = false;
//        try {
//            flag = AuthManager.instance().getInfo(info);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        LogMobot.logd("flag --->" + flag);
//        auth(companyName, info);
//
//    }
//
//    public void auth(final String company, final WeierInfo info) {
//        new Thread() {
//            public void run() {
//                HashMap<String, String> parameters = new HashMap<>();
//                parameters.put("authCode", authCode);
//                parameters.put("md5", info.getMd5());
//                parameters.put("companyName", company);
//                parameters.put("identifier", identifier);
//
//
//                String result = null;
//                try {
//                    result = HttpUtil2.singleFileUploadWithParameters(url, info.getPath(), parameters);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                LogMobot.logd("result --->" + result);
//
//                try {
//                    md5 = Utils.getLicenceMD5(result);
//                    LogMobot.logd("md5 --->" + md5);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                try {
//                    licFileBase64Str = Utils.getLicFileBase64Str(result);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                byte[] decodedString = Base64.decode(licFileBase64Str, Base64.DEFAULT);
//                LogMobot.logd("str --->" + decodedString);
//                Utils.writeToFile(path, decodedString);
//                boolean flag = AuthManager.instance().localCheck(path, md5);
//                LogMobot.logd("flag --->" + flag);
//
//            }
//        }.start();
//    }


    }
}