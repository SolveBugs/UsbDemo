package com.example.wei.usb_demo.bloodpressure;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

import com.example.wei.pl2303_test.R;
import com.example.wei.usb_demo.activity.base.BaseActivity;
import com.example.wei.usb_demo.data.views.CircleLoadingProgressView;


public class BloodPressureActivity extends BaseActivity {
    private static final String TAG = "BloodPressureActivity";

    private String deviceKey = "";
    private boolean usbDeviceDiscerned;
    private CircleLoadingProgressView progressView;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blood_pressure);
        final Bundle intentData = getIntent().getExtras();
        deviceKey = intentData.getString("USB_DEVICE_KEY");
        usbDeviceDiscerned = intentData.getBoolean("USB_DEVICE_DISCERNED");
        Button btn1 = (Button) findViewById(R.id.record_btn);
        btn1.setOnClickListener(btnOnClickListener);

        Button btn2 = (Button) findViewById(R.id.real_time_btn);
        btn2.setOnClickListener(btnOnClickListener);
        progressView = (CircleLoadingProgressView) findViewById(R.id.pregress);
        progressView.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        toolbar.setTitle("测量血压");
        new Thread(new Runnable() {
            @Override
            public void run() {
                int i = 5;
                while (i != -1) {
                    try {
                        Thread.sleep(1000);
                        final int m = i;
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                progressView.setProgress(m);
                            }
                        });
                        i--;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        progressView.setVisibility(View.GONE);
                        progressView.stop();
                    }
                });
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        progressView.setVisibility(View.GONE);
        progressView.stop();
    }

    private View.OnClickListener btnOnClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            int btn_id = v.getId();
            Intent intent = new Intent();
            if (btn_id == R.id.record_btn) {
                intent.setClass(BloodPressureActivity.this, BloodPressureRecordActivity.class);
            } else if (btn_id == R.id.real_time_btn) {
                intent.putExtra("USB_DEVICE_KEY", deviceKey);
                intent.putExtra("USB_DEVICE_DISCERNED", usbDeviceDiscerned);
                intent.setClass(BloodPressureActivity.this, RealtimeActivity.class);
            }
            BloodPressureActivity.this.startActivity(intent);
        }
    };

}
