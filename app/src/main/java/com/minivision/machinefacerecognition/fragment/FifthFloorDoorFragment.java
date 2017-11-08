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
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;

import com.minivision.machinefacerecognition.R;
import com.minivision.machinefacerecognition.activity.utils.Utils;


/**
 * Created by yuan_hao on 2017/7/26.
 * Email:yuanhao@minivision.cn
 */

public class FifthFloorDoorFragment extends Fragment {

    private EditText et_server_ip, et_server_port, et_door_ip, et_door_port, et_door_id, et_door_control_type;

    SharedPreferences sp;
    SharedPreferences.Editor editor;
    Spinner spinner;
    String doorid = "1";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fivefloor, container, false);
        sp = getActivity().getSharedPreferences("threshold", Context.MODE_PRIVATE);
        editor = sp.edit();
        spinner = (Spinner) view.findViewById(R.id.doorId_sp);
        et_server_ip = (EditText) view.findViewById(R.id.et_server_ip);
        et_server_port = (EditText) view.findViewById(R.id.et_server_port);
        et_door_ip = (EditText) view.findViewById(R.id.et_door_ip);
        et_door_port = (EditText) view.findViewById(R.id.et_door_port);
//        et_door_id = (EditText) view.findViewById(R.id.et_door_id);
        et_door_control_type = (EditText) view.findViewById(R.id.et_door_control_type);

        spinner.setSelection(Integer.parseInt(sp.getString("door_id", getString(R.string.door_id_default))) - 1);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                doorid = position + 1 + "";
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        et_server_ip.setText(sp.getString("server_ip", getString(R.string.server_ip_default)));
        et_server_port.setText(sp.getString("server_port", getString(R.string.server_port_default)));
        et_door_ip.setText(sp.getString("door_ip", getString(R.string.door_ip_default)));
        et_door_port.setText(sp.getString("door_port", getString(R.string.door_port_default)));
//        et_door_id.setText(sp.getString("door_id", getString(R.string.door_id_default)));
        et_door_control_type.setText(sp.getString("door_control_type", getString(R.string.door_control_type_default)));
        return view;
    }

    public int checkSetting() {
        String serverIp = et_server_ip.getText().toString().trim();
        String serverPort = et_server_port.getText().toString().trim();
        String doorIp = et_door_ip.getText().toString().trim();
        String doorPort = et_door_port.getText().toString().trim();
//        String doorId = et_door_id.getText().toString().trim();
        String doorControlType = et_door_control_type.getText().toString().trim();

        if (TextUtils.isEmpty(serverIp) || TextUtils.isEmpty(serverPort) || TextUtils.isEmpty(doorIp)
                || TextUtils.isEmpty(doorPort) || TextUtils.isEmpty(doorid) || TextUtils.isEmpty(doorControlType)) {
            return 0;
        } else if (!Utils.isIP(serverIp) || !Utils.isIP(doorIp)) {
            return 1;
        } else if (serverPort.length() < 4 || serverPort.length() > 5 || doorPort.length() < 4 || doorPort.length() > 5) {
            return 2;
        } else {
            editor.putString("server_ip", serverIp);
            editor.putString("server_port", serverPort);
            editor.putString("door_ip", doorIp);
            editor.putString("door_port", doorPort);
            editor.putString("door_id", doorid);
            editor.putString("door_control_type", doorControlType);
            editor.commit();
            return 3;
        }


    }
}
