package com.minivision.machinefacerecognition.activity.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.hardware.Camera;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Surface;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.minivision.face.Config;
import com.minivision.face.auth.DeviceInfo;
import com.minivision.machinefacerecognition.activity.dbutils.FaceDBUtils;
import com.minivision.machinefacerecognition.activity.entity.PersonFaces;
import com.minivision.machinefacerecognition.activity.entity.RecognizeResult;
import com.minivision.machinefacerecognition.activity.log.LogMobot;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Zhaojf on 2017/8/14 0014.
 */

public class Utils {
    private static String TAG = "UTILS";

    public static String getPicName(String filepath) {
        int start = filepath.lastIndexOf("/");
        int end = filepath.lastIndexOf(".");
        if (start != -1 && end != -1) {
            Log.d(TAG, "getFilename: filepath.substring(start + 1, end)" + filepath.substring(start - 1, start));
            return filepath.substring(start + 1, end);
        } else {
            return null;
        }
    }

    /*
    * 获取批量上传文件名
    * */
    public static String getFileName(String filepath) {
        String[] split = filepath.split("/");
        Log.d(TAG, "getFileName: " + split[split.length - 2]);
        return split[split.length - 2];
    }

    /*
        * 获取批量上传文件名
        * */
    public static String getFileName2(String filepath) {
        File f = new File(filepath);
        return f.getParentFile().getName();
    }

    public static String getFileParentPath(String filepath) {
        File f = new File(filepath);
        return f.getParent();
    }

    /*
    * 获取批量上传文件数量
    * */
    public static int getFileSize(String path) {
        File file = new File(path);
        File[] files = file.listFiles();
        return files.length;
    }

    /*
    * 单人注册 显示照片
    * */
    public static void savePic2SD(String path, Bitmap bitmap) {
        try {
            File file = new File(path);

            if (!file.exists()) {
                file.mkdir();

            }
            File file2 = new File(file.getAbsolutePath() + "/default.jpg");
           LogMobot.logd("savePic2SD: " + file2.getPath());
            if (!file2.exists()) {
                try {
                    file2.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(file2);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            try {
                fos.flush();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
    * 删除底库照片
    * */
    public static void deleteByName(String name) {
        try {
            File file = new File(Constant.PIC + "/" + name + ".jpg");
            if (file.exists()) {
                boolean delete = file.delete();
                LogMobot.logd("删除" + delete);
            }

            File file2 = new File(Constant.SHOWPIC + "/" + name + ".jpg");
            if (file2.exists()) {
                boolean delete = file2.delete();
                LogMobot.logd("删除2" + delete);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
    * 从uri中得到真实路径
    * */
    public static String getRealFilePath(final Context context, final Uri uri) {
        if (null == uri) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null)
            data = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }

    //uri转bitmap
    public static final Bitmap getBitmap(ContentResolver cr, Uri url)
            throws FileNotFoundException, IOException {
        InputStream input = cr.openInputStream(url);
        Bitmap bitmap = BitmapFactory.decodeStream(input);
        input.close();
        return bitmap;
    }

    public static void defaultPICFileDel() {

        try {
            File file = new File("/sdcard/pic/default.jpg");
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void defaultSHOWFileDel() {

        try {
            File file = new File("/sdcard/showpic/default.jpg");
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
    * 将默认照片文件名替换成注册名
    * */
    public static boolean fileName2Person(String name, String path) {
        File file = new File(path + "/default.jpg");
        if (!file.exists()) {
            Log.e(TAG, "fileName2Person: file is not exist");
            return false;
        }
        boolean b = file.renameTo(new File(path + "/" + name + ".jpg"));
        return b;
    }

    /*
    * 批量插入数据
    * */
    public static int MultiUpLoadPic(String parentPath, FaceDBUtils fb) throws Exception {
        boolean insert = false;
        int len = 0;
        File root = new File(parentPath);
        File[] files = root.listFiles();
        int bytesum = 0;
        int byteread = 0;
        for (File file : files) {
            if (file.exists() && file.isFile()) {
                PersonFaces pf = new PersonFaces();
                pf.setName(getPicName(file.getAbsolutePath()));
                pf.setPicPath(Constant.PIC + "/" + file.getName());
                pf.setLimtStarttime("0");
                pf.setLimtEndtime("23");
                insert = fb.mulinsert2(file.getAbsolutePath(), pf);
//            LogMobot.logd("显示" + Constant.PIC + "/" + file.getName() + insert);
                len += 1;
                if (insert) {
                    InputStream inStream = new FileInputStream(file.getAbsoluteFile()); //读入原文件
                    File dir = new File(Constant.PIC + "/");
                    if (!dir.exists()) {
                        dir.mkdir();
                    }
                    File picFile = new File(dir.getAbsolutePath() + "/" + file.getName());
                    FileOutputStream fs = new FileOutputStream(picFile);

                    byte[] buffer = new byte[2048];
                    int length;
                    while ((byteread = inStream.read(buffer)) != -1) {
                        bytesum += byteread; //字节数 文件大小
                        System.out.println(bytesum);
                        fs.write(buffer, 0, byteread);
                    }
                    inStream.close();
                }
            }

        }
        return len;
    }


    public static void initFiles() throws IOException {
        File pictureDirFile = new File(Constant.PICTURE_DIR);
        if (!pictureDirFile.exists()) {
            pictureDirFile.mkdirs();
        }

        File resourceDirFile = new File(Constant.RESOURCE_DIR);
        if (!resourceDirFile.exists()) {
            resourceDirFile.mkdirs();
        }

        File resultDirFile = new File(Constant.RESULT_DIR);
        if (!resultDirFile.exists()) {
            resultDirFile.mkdirs();
        }
    }

    public static void sendBroadcast(Context context, File file) {
        Uri uri = Uri.fromFile(file);
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri);
        context.sendBroadcast(intent);
    }

    public static File stream2File(InputStream in, String path) throws IOException {
        File file = new File(path);
        FileOutputStream fos = new FileOutputStream(file);

        byte[] buffer = new byte[1024 * 4];
        int len;
        while ((len = in.read(buffer)) != -1) {
            fos.write(buffer, 0, len);
            fos.flush();
        }
        in.close();
        fos.close();
        return file;
    }


    /**
     * 获取屏幕的宽高(像素)
     *
     * @param context context
     * @return 屏幕的宽高(像素)
     */
    public static Point getScreenMetrics(Context context) {

        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        int w_screen = dm.widthPixels;
        int h_screen = dm.heightPixels;
        Log.i("TAG", "Screen---Width = " + w_screen + " Height = " + h_screen + " densityDpi = " + dm.densityDpi);
        return new Point(w_screen, h_screen);
    }

    /**
     * Gets the current display rotation in angles.
     */
    public static int getDisplayRotation(Activity context) {
        int rotation = context.getWindowManager().getDefaultDisplay().getRotation();
        switch (rotation) {
            case Surface.ROTATION_0:
                return 0;
            case Surface.ROTATION_90:
                return 90;
            case Surface.ROTATION_180:
                return 180;
            case Surface.ROTATION_270:
                return 270;
        }
        return 0;
    }

    public static int getDisplayOrientation(int degrees, int cameraId) {
        // See android.hardware.Camera.setDisplayOrientation for documentation.
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        return result;
    }

    public static RecognizeResult.RecognizeResultsBean processRecognizeResult(String json) {
        if (TextUtils.isEmpty(json)) {
            return null;
        }

        JsonParser parse = new JsonParser();
        JsonArray jsonArray = parse.parse(json).getAsJsonArray();

        Gson gson = new Gson();
        final ArrayList<RecognizeResult> results = new ArrayList<>();

        for (JsonElement result : jsonArray) {
            RecognizeResult recognizeResult = gson.fromJson(result, RecognizeResult.class);
            results.add(recognizeResult);
        }

        return results.get(0).getRecognizeResults().get(0);
    }


    /*
    * Bitmap旋转
    * */

    public static Bitmap rotateBitmap(Bitmap origin, float alpha) {
        if (origin == null) {
            return null;
        }
        int width = origin.getWidth();
        int height = origin.getHeight();
        Matrix matrix = new Matrix();
        matrix.setRotate(alpha);
        // 围绕原地进行旋转
        Bitmap newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
        if (newBM.equals(origin)) {
            return newBM;
        }
        origin.recycle();
        return newBM;
    }

    // 缩放图片
    public static Bitmap scaleBitmap(Bitmap bm, int newWidth, int newHeight) {
        // 获得图片的宽高
        int width = bm.getWidth();
        int height = bm.getHeight();
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
//        LogMobot.loge("scaleHeight = " + scaleHeight + "scaleWidth = " + scaleWidth);
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片
        Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        return newbm;
    }

    public static boolean dateCompare(String str1, String str2) {
        boolean isBigger = false;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date dt1 = null;
        Date dt2 = null;
        try {
            dt1 = sdf.parse(str1);
            dt2 = sdf.parse(str2);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (dt1.getTime() >= dt2.getTime()) {
            isBigger = true;
        } else if (dt1.getTime() < dt2.getTime()) {
            isBigger = false;
        }
        return isBigger;
    }


    //---------------授权-------------
    public static String getLicenceMD5(String json) throws JSONException {
        JSONObject jsonObject = new JSONObject(json);
        String pathStr = jsonObject.optString("md5");
        Log.d(TAG, "getLicenceMD5: " + pathStr);
        return pathStr;
    }

    public static String getLicFileBase64Str(String json) throws JSONException {
        JSONObject jsonObject = new JSONObject(json);
        String pathStr = jsonObject.optString("licFileBase64Str");
        Log.d(TAG, "getLicFileBase64Str: " + pathStr);
        return pathStr;
    }

    public static void writeToFile(String path, byte[] content) {
        File file = new File(path);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        try {

            FileOutputStream fos = new FileOutputStream(file);
            try {
                fos.write(content, 0, content.length);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static File copyFile(File licence, String dstPath) throws IOException {

        FileInputStream fis = new FileInputStream(licence);
        File f = stream2File(fis, dstPath);
        if (fis != null) {
            fis.close();
        }

        return f;
    }

    public static void delFile(String path) {
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }
    }

    //[{"RecognizeResults":
    // [{
    // "imagePath":"/storage/emulated/0/Minivision/MachineRecognition/./ImageOfLibrary/赵剑峰","errorCode":0,"name":"赵剑峰","age":-4,"sex":-4,"distance":1.616305,"score":89.796191,"faceRect_x":86,"faceRect_y":284,"faceRect_w":222,"faceRect_h":222
    // }]
    // }]  RecognizeResult.RecognizeResultsBean
    public static RecognizeResult.RecognizeResultsBean fastRecognizeResult(String json) {
        if (TextUtils.isEmpty(json)) {
            return null;
        }
        JSONArray jsonarray = JSONArray.parseArray(json);
//        LogMobot.logd(jsonarray.get(0) + "数组");
        RecognizeResult recognizeResult = JSON.parseObject(jsonarray.get(0).toString(), RecognizeResult.class);
        return recognizeResult.getRecognizeResults().get(0);
    }

    public static int getLicence(DeviceInfo deviceInfo, Config config) throws IOException, JSONException {
//        String authCode = "Y1F5FA";
        String authCode = "q4KvCZF6";
        String companyName = "测试";
//        String identifier = "$2a$10$5FAACv1Gzh1rXHQToPjGFO5smaRLl8Cl.Jphx8ZpdQZUDtBS3IucK";

        String identifier = "$2a$10$WSY5i17VWdoLth3S4xfG0.zDBCqUbEbzqcvJW37Y1vS5xbGNSTlCK";
        //测试环境 url
//        String url = "https://118.31.114.240:8444/api/v1/getLicenseFile";

        String url = "https://xcx.minivision.com.cn:8445/api/v1/getLicenseFile";
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("authCode", authCode);
        parameters.put("md5", deviceInfo.getMd5());
        parameters.put("companyName", companyName);
        parameters.put("identifier", identifier);
        //联网请求
        String result = HttpUtil.singleFileUploadWithParameters(url, deviceInfo.getPath(), parameters);
//Log.d(TAG, "getLicence: " + result);

        if (!TextUtils.isEmpty(result)) {
            JSONObject jsonObject = new JSONObject(result);
            int code = jsonObject.optInt("errorCode");

            if (code != 1) {
                Log.e("TAG", jsonObject.optString("message"));
                return -11;
            } else {
                String content = jsonObject.optString("licFileBase64Str");
                byte[] bytes = Base64.decode(content, Base64.DEFAULT);
                String path = config.getLicencePath();
                createFileWithByte(bytes, path);
                return 1;
            }
        } else {
            Log.e("TAG", "empty response.");
            return -11;
        }
    }


    public static File createFileWithByte(byte[] bytes, String path) throws IOException {
        String s = new String(bytes);
        Log.d(TAG, "createFileWithByte: " + s);

        File file = new File(path);
        if (!file.exists()) {
            file.createNewFile();
        }
        FileOutputStream fos = new FileOutputStream(file);
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        bos.write(bytes);
        bos.flush();
        fos.close();
        bos.close();
        return file;
    }

    public static String stream2String(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int len;
        byte[] buffer = new byte[1024];
        while ((len = in.read(buffer)) != -1) {
            out.write(buffer, 0, len);
        }

        String result = out.toString();
        in.close();
        out.close();
        return result;
    }

    public static boolean isIP(String addr) {
        if (addr.length() < 7 || addr.length() > 15 || "".equals(addr)) {
            return false;
        }
        /**
         * 判断IP格式和范围
         */
        String rexp = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";
        Pattern pat = Pattern.compile(rexp);
        Matcher mat = pat.matcher(addr);
        boolean ipAddress = mat.find();
        //============对之前的ip判断的bug在进行判断
        if (ipAddress == true) {
            String ips[] = addr.split("\\.");
            if (ips.length == 4) {
                try {
                    for (String ip : ips) {
                        if (Integer.parseInt(ip) < 0 || Integer.parseInt(ip) > 255) {
                            return false;
                        }
                    }
                } catch (Exception e) {
                    return false;
                }
                return true;
            } else {
                return false;
            }
        }
        return ipAddress;
    }
}
