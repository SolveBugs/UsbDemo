package com.example.wei.usb_demo.bloodpressure;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.wei.pl2303_test.R;
import com.example.wei.usb_demo.activity.StateButton;
import com.example.wei.usb_demo.common.utils.DateUtils;
import com.example.wei.usb_demo.data.db.bean.ModelBloodPressure;

public class DialogActivity extends Activity implements View.OnClickListener {

    private StateButton ok;
    private TextView tvDate, tvDataDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);
        initView();
        initEvents();
        Bundle extras = getIntent().getExtras();
        ModelBloodPressure pressure = extras.getParcelable("data");
        tvDate.setText(DateUtils.formatDate(pressure.getDataTime(), DateUtils.yyyyMMddHHmmssGAP));
        tvDataDetails.setText("舒张压" + pressure.getDiastolic() + "\n" + "收缩压" + pressure.getSystolic() + "\n" + "脉搏" + pressure.getPulse());
    }

    private void initEvents() {
        ok.setOnClickListener(this);
    }

    private void initView() {
        ok = (StateButton) findViewById(R.id.ok);
        tvDate = (TextView) findViewById(R.id.date);
        tvDataDetails = (TextView) findViewById(R.id.data_details);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.ok:
                finish();
                break;
            default:
                break;
        }
    }
}
