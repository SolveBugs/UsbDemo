package com.example.wei.usb_demo.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.example.wei.pl2303_test.R;

public class DialogActivity extends Activity implements View.OnClickListener {

    private StateButton ok;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);
        initView();
        initEvents();
    }

    private void initEvents() {
        ok.setOnClickListener(this);
    }

    private void initView() {
        ok = (StateButton) findViewById(R.id.ok);
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
