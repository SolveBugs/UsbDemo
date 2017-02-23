package com.example.wei.usb_demo.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.example.wei.pl2303_test.R;
import com.example.wei.usb_demo.activity.base.BaseActivity;
import com.example.wei.usb_demo.activity.settings.SettingMenuFragment;
import com.example.wei.usb_demo.activity.settings.WifiSettingFragment;

public class SettingsActivity extends BaseActivity {

    private static final String TAG = "SettingsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        SettingMenuFragment menuFragment = new SettingMenuFragment();
        WifiSettingFragment wifiFragment = new WifiSettingFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.left_fragment, menuFragment);
        transaction.add(R.id.right_fragment, wifiFragment);
        transaction.commit();
    }
}
