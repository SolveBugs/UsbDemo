package com.example.wei.usb_demo.activity.base;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.example.wei.pl2303_test.R;
import com.example.wei.usb_demo.app.AppContext;
import com.example.wei.usb_demo.common.broatcast.UIBroadcastReceiver;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import static com.example.wei.usb_demo.usb_device.UsbDeviceHandle.ACTION_DEVICE_DISCERN_FINISH_NOTIFY;
import static com.example.wei.usb_demo.usb_device.UsbDeviceHandle.K_DEVICE_DISCERN_FINISH_KEY;
import static com.example.wei.usb_demo.usb_device.UsbDeviceHandle.K_DEVICE_DISCERN_FINISH_STATE;
import static com.example.wei.usb_demo.usb_device.UsbDeviceHandle.K_DEVICE_DISCERN_FINISH_TYPE;

/**
 * Created by zhenqiang on 2016/12/28.
 */

public class BaseActivity extends AppCompatActivity implements UIBroadcastReceiver.OnActionReceive, UIBroadcastReceiver.OnActiveReceive {


    private static final String TAG = "BaseActivity";

    public ToolBarHelper mToolBarHelper;
    public Toolbar toolbar;
    public SystemBarTintManager tintManager;


    private boolean needBroadcast;
    private boolean alreadyRegisterBroadcast;
    private UIBroadcastReceiver broadcastReceiver;
    private boolean show;
    private boolean active, created;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        created = true;
        AppManager.getAppManager().addActivity(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }

        // create our manager instance after the content view is set
        tintManager = new SystemBarTintManager(this);
        // enable status bar tint
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.color.common_tint_bar_color);

        IntentFilter intent = new IntentFilter(ACTION_DEVICE_DISCERN_FINISH_NOTIFY);
        registerReceiver(discernFinishReceiver, intent);
    }

    @TargetApi(19)
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    @Override
    public void setContentView(int layoutResID) {
        View view = LayoutInflater.from(this).inflate(layoutResID, null);
        mToolBarHelper = new ToolBarHelper(this, layoutResID);
        toolbar = mToolBarHelper.getToolBar();
        mToolBarHelper.getContentView().setFitsSystemWindows(true);
        setContentView(mToolBarHelper.getContentView());
        setSupportActionBar(toolbar);
        onCreateCustomToolBar(toolbar);
        hideBack(false);
    }

    public void onCreateCustomToolBar(Toolbar toolbar) {
        toolbar.setContentInsetsRelative(0, 0);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void hideBack(boolean hide) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(!hide);
    }

    public boolean isNeedBroadcast() {
        return needBroadcast;
    }

    public boolean isAlreadyRegisterBroadcast() {
        return alreadyRegisterBroadcast;
    }

    private void registerBroadcastReceiver() {
        if (broadcastReceiver == null) {
            broadcastReceiver = new UIBroadcastReceiver();
        }
        broadcastReceiver.setOnActionReceive(this);
        registerReceiver(broadcastReceiver, UIBroadcastReceiver.getIntentFilter(this));
        alreadyRegisterBroadcast = true;
        Log.i(TAG, "registerBroadcastReceiver: 注册广播");
    }

    public void setNeedBroadcast(boolean needBroadcast) {
        this.needBroadcast = needBroadcast;
    }

    public boolean isShow() {
        return show;
    }

    public boolean isActive() {
        return active;
    }

    public boolean isCreated() {
        return created;
    }

    @Override
    protected void onResume() {
        /**
         * 设置为横屏
         */
        if(getRequestedOrientation()!= ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        super.onResume();
        ((AppContext) getApplicationContext()).setOnActiveReceive(this);
        if (isNeedBroadcast() && !isAlreadyRegisterBroadcast()) {
            registerBroadcastReceiver();
        }
        show = true;
        active = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        hideSoftInput();
        show = false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        active = false;
    }

    @Override
    protected void onDestroy() {
        AppManager.getAppManager().finishActivity(this);
        unregisterReceiver(discernFinishReceiver);
        created = false;
        if (broadcastReceiver != null) {
            unregisterReceiver(broadcastReceiver);
            alreadyRegisterBroadcast = false;
        }
        super.onDestroy();
    }

    protected void onDeviceDiscernFinish(int type, String usbKey, int state) {
        Log.i("设备连接成功", "onDeviceDiscernFinish: type->" + type + ", deviceKey->" + usbKey + ", state->" + state);
    }

    BroadcastReceiver discernFinishReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            onDeviceDiscernFinish(bundle.getInt(K_DEVICE_DISCERN_FINISH_TYPE), bundle.getString(K_DEVICE_DISCERN_FINISH_KEY), bundle.getInt(K_DEVICE_DISCERN_FINISH_STATE));
        }
    };

    @Override
    public void onActionReceive(int action, Bundle bundle) {

    }

    @Override
    public void onActiveReceive(int action, Bundle bundle) {

    }

    public void hideSoftInput() {
        View view = getWindow().peekDecorView();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null)
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
