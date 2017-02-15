package com.example.wei.usb_demo.bloodpressure;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.wei.pl2303_test.R;
import com.example.wei.usb_demo.activity.base.BaseActivity;
import com.example.wei.usb_demo.bloodpressure.adapter.BPRecordAdapter;
import com.example.wei.usb_demo.bloodpressure.presenter.BloodPressureRecordPresenter;
import com.example.wei.usb_demo.bloodpressure.view.IBloodPressureRecordView;
import com.example.wei.usb_demo.data.db.bean.ModelBloodPressure;

import java.util.ArrayList;

public class BloodPressureRecordActivity extends BaseActivity implements IBloodPressureRecordView, AdapterView.OnItemClickListener {

    private TextView tvHint;
    private ListView mListView;

    private ProgressDialog progressDialog;
    private BPRecordAdapter adapter;
    private ArrayList<ModelBloodPressure> datas = new ArrayList<>();
    private BloodPressureRecordPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blood_pressure_record);

        tvHint = (TextView) findViewById(R.id.tv_hint);
        mListView = (ListView) findViewById(R.id.lv);
        mListView.setOnItemClickListener(this);
        adapter = new BPRecordAdapter(this, datas);
        mListView.setAdapter(adapter);
        presenter = new BloodPressureRecordPresenter(this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("加载数据中...");

        presenter.load(this);
    }

    @Override
    public void showNodata() {
        tvHint.setVisibility(View.VISIBLE);
        mListView.setVisibility(View.GONE);
        tvHint.setText("暂无数据");
    }

    @Override
    public void showLoading() {
        progressDialog.show();

    }

    @Override
    public void stopLoading() {
        progressDialog.dismiss();
    }

    @Override
    public void notifiData(ArrayList<ModelBloodPressure> data) {
        datas.addAll(data);
        adapter.notifyDataSetChanged();
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ModelBloodPressure modelBloodPressure = datas.get(position);
        Intent intent = new Intent(BloodPressureRecordActivity.this, DialogActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable("data", modelBloodPressure);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
