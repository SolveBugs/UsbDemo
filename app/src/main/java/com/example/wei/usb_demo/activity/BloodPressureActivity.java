package com.example.wei.usb_demo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.wei.pl2303_test.R;
import com.example.wei.usb_demo.activity.base.BaseActivity;
import com.example.wei.usb_demo.utils.StringUtil;

public class BloodPressureActivity extends BaseActivity {

    private Button btnSend;
    private EditText etSendData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blood_pressure);

        Button btn1 = (Button) findViewById(R.id.record_btn);
        btn1.setOnClickListener(btnOnClickListener);

        Button btn2 = (Button) findViewById(R.id.real_time_btn);
        btn2.setOnClickListener(btnOnClickListener);

        etSendData= (EditText) findViewById(R.id.send_data_et);
        btnSend= (Button) findViewById(R.id.send_btn);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }

    private View.OnClickListener btnOnClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            int btn_id = v.getId();
            Intent intent = new Intent();
            if (btn_id == R.id.record_btn) {
                intent.setClass(BloodPressureActivity.this, BloodRecordActivity.class);
            } else if (btn_id == R.id.real_time_btn) {
                intent.setClass(BloodPressureActivity.this, RealtimeActivity.class);
            }
            BloodPressureActivity.this.startActivity(intent);
        }
    };
}
