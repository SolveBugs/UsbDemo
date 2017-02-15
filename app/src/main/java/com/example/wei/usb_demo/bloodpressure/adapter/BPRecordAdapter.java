package com.example.wei.usb_demo.bloodpressure.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.wei.pl2303_test.R;
import com.example.wei.usb_demo.common.utils.DateUtils;
import com.example.wei.usb_demo.data.db.bean.ModelBloodPressure;

import java.util.ArrayList;

/**
 * Created by zhenqiang on 2017/2/15.
 */

public class BPRecordAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<ModelBloodPressure> datas;
    private LayoutInflater inflater;

    public BPRecordAdapter(Context context, ArrayList<ModelBloodPressure> datas) {
        this.context = context;
        this.datas = datas;
        inflater = LayoutInflater.from(this.context);
    }

    @Override
    public int getCount() {
        return datas == null ? 0 : datas.size();
    }

    @Override
    public Object getItem(int position) {
        return datas.get(position);
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
            convertView = inflater.inflate(R.layout.bp_record_adapter_layout, null);
            holder.tvDia = (TextView) convertView.findViewById(R.id.tv_dia);
            holder.tvSys = (TextView) convertView.findViewById(R.id.tv_sys);
            holder.tvPul = (TextView) convertView.findViewById(R.id.tv_pul);
            holder.tvDate = (TextView) convertView.findViewById(R.id.tv_date);
            holder.llHead = (LinearLayout) convertView.findViewById(R.id.ll_head);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (position == 0) {
            holder.llHead.setVisibility(View.VISIBLE);
        }

        ModelBloodPressure pressure = datas.get(position);
        holder.tvDia.setText(pressure.getDiastolic() + "");
        holder.tvSys.setText(pressure.getSystolic() + "");
        holder.tvPul.setText(pressure.getPulse() + "");
        holder.tvDate.setText(DateUtils.formatDate(pressure.getDataTime(), DateUtils.yyyyMMddHHmmGAP));
        return convertView;
    }

    static class ViewHolder {
        TextView tvDia, tvSys, tvPul, tvDate;
        LinearLayout llHead;
    }

}
