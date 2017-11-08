package com.minivision.machinefacerecognition.activity.utils;

import android.util.Log;

import com.minivision.machinefacerecognition.activity.log.LogMobot;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Set;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class HttpUtil2 {

    private static final String TAG = HttpUtil2.class.getSimpleName();

    private static final String end = "\r\n";
    private static final String twoHyphens = "--";
    private static final String boundary = "---------------------------7e0dd540448";

    public static final String BASE_URL_PRODUCTION = "https://xcx.minivision.com.cn:8444";

    public static final String GET_LICENCE_URL = "/api/v1/getLicenseFile";
    public static final String BASE_URL_TEST = "https://192.168.123.12:8444";
    public static final String UPDATE_AUTH_STATUS = "/api/v1/updateAuthStatus";

    final static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {

        public boolean verify(String hostname, SSLSession session) {
            HostnameVerifier hv = HttpsURLConnection.getDefaultHostnameVerifier();
            boolean result = hv.verify("xcx.minivision.com.cn", session);
            Log.d(TAG, "verify: " + result);
            return result;
        }
    };

    private static void trustAllHosts() {
        final String TAG = "trustAllHosts";
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {

            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[]{};
            }

            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                Log.i(TAG, "checkClientTrusted");
            }

            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                Log.i(TAG, "checkServerTrusted");
            }
        }};

        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static String singleFileUploadWithParameters(String actionURL, String uploadFile,
                                                        HashMap<String, String> parameters) throws IOException {
        trustAllHosts();
        URL url = new URL(actionURL);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        //发送post请求需要下面两行
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setHostnameVerifier(DO_NOT_VERIFY);
//			connection.setSSLSocketFactory(sslContext.getSocketFactory());

        LogMobot.logd("http --->1");
        //设置请求参数
        connection.setUseCaches(false);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Connection", "Keep-Alive");
        connection.setRequestProperty("Charset", "UTF-8");
        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
        LogMobot.logd("http --->2");
        //获取请求内容输出流
        DataOutputStream ds = new DataOutputStream(connection.getOutputStream());
        String fileName = uploadFile.substring(uploadFile.lastIndexOf(File.separator) + 1);
        //开始写表单格式内容
        //写参数
        Set<String> keys = parameters.keySet();
        for (String key : keys) {
            ds.writeBytes(twoHyphens + boundary + end);
            ds.writeBytes("Content-Disposition: form-data; name=\"");
            ds.write(key.getBytes());
            ds.writeBytes("\"" + end);
            ds.writeBytes(end);
            ds.write(parameters.get(key).getBytes());
            ds.writeBytes(end);
        }
        LogMobot.logd("http --->3");

        //写文件
        ds.writeBytes(twoHyphens + boundary + end);
        ds.writeBytes("Content-Disposition: form-data; " + "name=\"authfile\"; " + "filename=\"");
        //防止中文乱码
        ds.write(fileName.getBytes());
        ds.writeBytes("\"" + end);
        ds.writeBytes(end);
        //根据路径读取文件
        FileInputStream fis = new FileInputStream(uploadFile);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = fis.read(buffer)) != -1) {
            ds.write(buffer, 0, length);
        }
        ds.writeBytes(end);
        ds.writeBytes(twoHyphens + boundary + twoHyphens + end);
        ds.writeBytes(end);
        LogMobot.logd("http --->4");
        ds.flush();
        fis.close();
        ds.close();
        LogMobot.logd("http --->5");
        int responseCode = connection.getResponseCode();
        LogMobot.logd("http --->"+responseCode);
        Log.d(TAG, "getLicence: " + responseCode);
        if (200 == responseCode) {
            InputStream in = connection.getInputStream();
            return stream2String(in);
        } else {
            return null;
        }
    }

    public static String stream2String(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int len;
        byte[] buffer = new byte[1024];
        while ((len = in.read(buffer)) != -1) {
            out.write(buffer, 0, len);
        }

        String result = out.toString();
        Log.d(TAG, "stream2String: " + result);
        in.close();
        out.close();
        return result;
    }

    public static String download(String strurl, String filepath, HashMap<String, String> parameters) throws IOException {


        URL url = new URL(strurl);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        //设置消息头
        conn.setRequestMethod("POST");
        conn.setDoInput(true);
        conn.setDoOutput(true);

        conn.setConnectTimeout(3000);
        conn.setUseCaches(false);
        conn.setRequestProperty("Content-Type", "multipart/form-data; boundary= " + boundary);
        conn.setRequestProperty("Charset", "utf-8");  //设置编码语言
        conn.setRequestProperty("Connection", "keep-alive");  //设置连接的状态


        DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
        Set<String> keys = parameters.keySet();
        for (String key : keys) {
            dos.writeBytes(twoHyphens + boundary + end);
            dos.writeBytes("Content-Disposition: form-data; name=\"");
            dos.write(key.getBytes());
            dos.writeBytes("\"" + end);
            dos.writeBytes(end);
            dos.write(parameters.get(key).getBytes());
            dos.writeBytes(end);
        }

        //写文件名
        String name = filepath.substring(filepath.lastIndexOf(File.separator) + 1);
        dos.writeBytes(twoHyphens + boundary + end);

        dos.writeBytes("Content-Disposition: form-data; " + "name=\"authfile\"; " + "filename=\"");
        dos.write(name.getBytes());
        dos.writeBytes("\"" + end);
        dos.writeBytes(end);


        FileInputStream fis = new FileInputStream(filepath);
        int len = 0;
        byte[] buffer = new byte[1024];
        while ((len = fis.read()) != -1) {
            dos.write(buffer, 0, len);
        }
        dos.writeBytes(end);
        dos.writeBytes(twoHyphens + boundary + twoHyphens + end);
        dos.writeBytes(end);

        dos.flush();
        dos.close();
        fis.close();
        if (conn.getResponseCode() == 200) {
            return stream2String(conn.getInputStream());
        } else {
            return null;
        }
    }
}