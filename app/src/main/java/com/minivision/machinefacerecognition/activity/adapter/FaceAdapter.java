package com.minivision.machinefacerecognition.activity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.minivision.machinefacerecognition.R;
import com.minivision.machinefacerecognition.activity.entity.PersonFaces;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Zhaojf on 2017/8/14 0014.
 */

public abstract class FaceAdapter extends BaseAdapter {
    private List<PersonFaces> lists;
    private Context context;

    int flag;
    HashMap<Integer,String> state = new HashMap<Integer, String>();
    public abstract void getCheckBoxValue(HashMap<Integer, String> map);

    public FaceAdapter(List<PersonFaces> lists, Context context,int flag) {
        this.lists = lists;
        this.context = context;
        this.flag = flag;
    }

    @Override
    public int getCount() {
        if (lists == null) {
            return 0;
        }
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        holder = new ViewHolder();
        convertView = LayoutInflater.from(context).inflate(R.layout.face_item, null);
        holder.cb = (CheckBox) convertView.findViewById(R.id.cb);
        holder.idTv = (TextView) convertView.findViewById(R.id.id);
        holder.nameTv = (TextView) convertView.findViewById(R.id.name);
        holder.numberTv = (TextView) convertView.findViewById(R.id.number);
        holder.phoneTv = (TextView) convertView.findViewById(R.id.phone);
        holder.timeTv = (TextView) convertView.findViewById(R.id.time);
        convertView.setTag(holder);


        if (flag == 0) {
            holder.cb.setVisibility(View.VISIBLE);
        } else {
            holder.cb.setVisibility(View.GONE);
        }
        holder.cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    state.put(position, lists.get(position).getName());
                } else {
                    state.remove(position);
                }
            }
        });

        holder.cb.setChecked((state.get(position) == null ? false : true));
        getCheckBoxValue(state);
        holder.idTv.setText(lists.get(position).get_id() + "");
        holder.nameTv.setText(lists.get(position).getName());
        holder.numberTv.setText(lists.get(position).getNum());
        holder.phoneTv.setText(lists.get(position).getPhoneNum());
        holder.timeTv.setText(lists.get(position).getTime());

        return convertView;
    }

    class ViewHolder {
        TextView idTv, nameTv, numberTv, phoneTv, timeTv;
        CheckBox cb;
    }
}
