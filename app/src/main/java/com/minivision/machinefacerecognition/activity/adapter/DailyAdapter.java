package com.minivision.machinefacerecognition.activity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.minivision.machinefacerecognition.R;
import com.minivision.machinefacerecognition.activity.entity.PersonDaily;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Zhaojf on 2017/8/14 0014.
 */

public abstract class DailyAdapter extends BaseAdapter {
    private List<PersonDaily> lists;
    private Context context;
    int flag;
    HashMap<Integer, String> state = new HashMap<Integer, String>();

    public abstract void getCheckBoxValue(HashMap<Integer, String> map);

    public DailyAdapter(List<PersonDaily> lists, Context context, int flag) {
        this.lists = lists;
        this.context = context;
        this.flag = flag;

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
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        holder = new ViewHolder();
        convertView = LayoutInflater.from(context).inflate(R.layout.daily_item, null);
        holder.cb = (CheckBox) convertView.findViewById(R.id.cb);
        holder.takePicTv = (ImageView) convertView.findViewById(R.id.takepic_imgv);
        holder.picTv = (ImageView) convertView.findViewById(R.id.pic_imgv);
        holder.nameTv = (TextView) convertView.findViewById(R.id.name_tv);
        holder.numberTv = (TextView) convertView.findViewById(R.id.number_tv);
        holder.takeTimeTv = (TextView) convertView.findViewById(R.id.taketime_tv);
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
                    state.put(position, lists.get(position).getTakeTime());
                } else {
                    state.remove(position);
                }
            }
        });
        holder.cb.setChecked((state.get(position) == null ? false : true));
        getCheckBoxValue(state);
        Glide.with(context).load(lists.get(position).getTakePicPath()).into(holder.takePicTv);
        Glide.with(context).load(lists.get(position).getPic()).into(holder.picTv);
        holder.nameTv.setText(lists.get(position).getName());
        holder.numberTv.setText(lists.get(position).getNum());
        holder.takeTimeTv.setText(lists.get(position).getTakeTime());
        return convertView;
    }

    class ViewHolder {
        TextView nameTv, numberTv, takeTimeTv;
        ImageView takePicTv, picTv;
        CheckBox cb;
    }

}
