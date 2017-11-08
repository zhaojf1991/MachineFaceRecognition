package com.minivision.machinefacerecognition.activity.mutilupload;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.minivision.machinefacerecognition.R;
import com.minivision.machinefacerecognition.activity.MultiRegisterActivity;
import com.minivision.machinefacerecognition.activity.dbutils.FaceDBUtils;
import com.minivision.machinefacerecognition.activity.entity.PersonFaces;
import com.minivision.machinefacerecognition.activity.log.LogMobot;
import com.minivision.machinefacerecognition.activity.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/8/11 0011.
 */

public class MutilloadupActivity extends Activity {
    //    @BindView(R.id.checkbox)
//    CheckBox checkBox;
//    @BindView(R.id.filename_tv)
//    TextView filenameTv;
    @BindView(R.id.filesize_tv)
    TextView filesizeTv;
    @BindView(R.id.upload_btn)
    Button uploadBtn;
    private String filepath, filename;
    private String fileParentPath;
    private int fileSize;
    private FaceDBUtils fb = null;
    private ProgressDialog dialog = null;
    private ExecutorService executorService;
    int lens;
    int oldSize;
    @BindView(R.id.file_lv)
    ListView fileLv;
    File[] files = null;
    private int selectPosition = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.multiloadup);
        ButterKnife.bind(this);
        fb = new FaceDBUtils(MutilloadupActivity.this);
//        filepath = getIntent().getStringExtra("filepath");
//        filename = Utils.getFileName2(filepath);
//        filenameTv.setText(filename);
//        LogMobot.logd(filepath);
        filesizeTv.setText("已选0张");
//        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked) {
//                    fileParentPath = Utils.getFileParentPath(filepath);
//                    fileSize = Utils.getFileSize(fileParentPath);
//                    filesizeTv.setText("已选" + fileSize + "张");
//                } else {
//                    filesizeTv.setText("已选0张");
//                }
//            }
//        });

        oldSize = fb.queryAll().size();

        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
        final List<File> lists = new ArrayList<>();
        files = file.listFiles();
        for (File file2 : files) {
            if (file2.isDirectory()) {
                LogMobot.logd(file2.getName());
                lists.add(file2);
            }

        }
        LogMobot.logd("files size = " + lists.size());
        final FileAdapter fileAdapter = new FileAdapter(lists);
        fileLv.setAdapter(fileAdapter);
        fileLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectPosition = position;
                fileAdapter.notifyDataSetChanged();

//                fileSize = lists.get(position).listFiles().length;
                File[] files = lists.get(position).listFiles();
                int count = 0;
                for (int i = 0; i < files.length; i++) {
                    if (files[i].isFile()) {
                        count += 1;
                    }
                }
                fileSize = count;
                filesizeTv.setText("已选" + fileSize + "张");
                filepath = lists.get(position).getAbsolutePath();
            }
        });
    }


    @OnClick(R.id.upload_btn)
    public void loadup() {
        if (filepath == null) {
            Toast.makeText(this, "请选择上传文件夹", Toast.LENGTH_SHORT).show();
        } else {

            //批量注册人脸库
            dialog = ProgressDialog.show(MutilloadupActivity.this, "提示", "正在上传中", false);
            try {
                executorService = Executors.newSingleThreadExecutor();
                executorService.execute(new SingleRunnable() {

                    @Override
                    public void result(int len) {
                        LogMobot.logd("loadup len ---> " + len);
                        if (len == fileSize) {
                            dialog.dismiss();
                            Message msg = new Message();
                            lens = len;
                            //查询成功上传个数
                            List<PersonFaces> personFaces = fb.queryAll();

                            Bundle bundle = new Bundle();
                            bundle.putInt("lens", lens);
                            bundle.putInt("size", personFaces.size()-oldSize);
                            msg.setData(bundle);
                            hanlder.sendMessage(msg);
                        }
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    abstract class SingleRunnable implements Runnable {

        public abstract void result(int len);

        @Override
        public void run() {

            try {
                int len = Utils.MultiUpLoadPic(filepath, fb);
                LogMobot.loge("filepath === " + filepath);

                result(len);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    Handler hanlder = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            Bundle data = msg.getData();
            int dataLen = data.getInt("lens");
            int size = data.getInt("size");
            Intent intent = new Intent(new Intent(MutilloadupActivity.this, MultiRegisterActivity.class));
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("lens", dataLen);
            intent.putExtra("size", size);
            startActivity(intent);
            overridePendingTransition(R.anim.leftin, R.anim.rightout);
            finish();

        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdown();
        }
    }

    @OnClick(R.id.back_imgv)
    public void back() {
        Intent intent = new Intent(new Intent(MutilloadupActivity.this, MultiRegisterActivity.class));
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("lens", 0);
        startActivity(intent);
        overridePendingTransition(R.anim.leftin, R.anim.rightout);
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == event.KEYCODE_BACK) {
//            Intent intent = new Intent(MutilloadupActivity.this, MultiRegisterActivity.class);
//
//            startActivity(intent);
//            finish();

            Intent intent = new Intent(new Intent(MutilloadupActivity.this, MultiRegisterActivity.class));
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("lens", 0);
            startActivity(intent);
            overridePendingTransition(R.anim.leftin, R.anim.rightout);
            finish();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    public class FileAdapter extends BaseAdapter {
        public List<File> lists;

        public FileAdapter(List lists) {
            this.lists = lists;

        }

        @Override
        public int getCount() {
            return lists.size();
        }

        @Override
        public Object getItem(int position) {
            return lists.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(MutilloadupActivity.this).inflate(R.layout.fileitem, null);
                holder.tv = (TextView) convertView.findViewById(R.id.filename_tv);
                holder.radiobutton = (RadioButton) convertView.findViewById(R.id.radiobutton);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.tv.setText(lists.get(position).getName());

            if (selectPosition == position) {
                holder.radiobutton.setChecked(true);
            } else {
                holder.radiobutton.setChecked(false);
            }
            LogMobot.logd("adapter size = " + position);
//            LogMobot.logd("adapter size = "+lists.get(61).getName());

            return convertView;
        }

        class ViewHolder {
            TextView tv;
            RadioButton radiobutton;
        }
    }
}
