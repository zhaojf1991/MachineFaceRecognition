package com.minivision.machinefacerecognition.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.minivision.machinefacerecognition.R;
import com.minivision.machinefacerecognition.activity.utils.Utils;


/**
 * Created by yuan_hao on 2017/7/26.
 * Email:yuanhao@minivision.cn
 */

public class FirstFloorDoorFragment extends Fragment {

    private EditText et_controller_ip, et_controller_port, et_door_duration;
    SharedPreferences sp;
    SharedPreferences.Editor editor;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.firstfloor, container, false);
        sp = getActivity().getSharedPreferences("threshold", Context.MODE_PRIVATE);
        editor = sp.edit();
        et_controller_ip = (EditText) view.findViewById(R.id.et_controller_ip);
        et_controller_port = (EditText) view.findViewById(R.id.et_controller_port);
        et_door_duration = (EditText) view.findViewById(R.id.et_door_duration);


        et_controller_ip.setText(sp.getString("controller_ip", getString(R.string.controller_ip_default)));
        et_controller_port.setText(sp.getString("controller_port", getString(R.string.controller_port_default)));
        et_door_duration.setText(sp.getString("open_door_duration", getString(R.string.duration_default)));
        return view;
    }

    public int checkSetting() {
        String controllerIp = et_controller_ip.getText().toString().trim();
        String controllerPort = et_controller_port.getText().toString().trim();
        String doorDuration = et_door_duration.getText().toString().trim();

        if (TextUtils.isEmpty(controllerIp) || TextUtils.isEmpty(controllerPort) || TextUtils.isEmpty(doorDuration)) {
            //空
            return 0;
        }
        if (!Utils.isIP(controllerIp)) {
//            Toast.makeText(getActivity(), "控制器IP格式填写错误", Toast.LENGTH_SHORT).show();
            return 1;
        } else if (controllerPort.length() > 5 || controllerPort.length() < 4) {
//            Toast.makeText(getActivity(), "控制器端口号格式填写错误", Toast.LENGTH_SHORT).show();
            return 2;
        } else {
            editor.putString("controller_ip", controllerIp);
            editor.putString("controller_port", controllerPort);
            editor.putString("open_door_duration", doorDuration);
            editor.commit();
            return 3;
        }
    }


}
