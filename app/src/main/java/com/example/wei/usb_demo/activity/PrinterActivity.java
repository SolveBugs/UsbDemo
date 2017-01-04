package com.example.wei.usb_demo.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.wei.pl2303_test.R;
import com.example.wei.usb_demo.activity.base.BaseActivity;
import com.example.wei.usb_demo.utils.printer_utils.myprinter.Global;
import com.example.wei.usb_demo.utils.printer_utils.myprinter.WorkService;

import java.lang.ref.WeakReference;

import lecho.lib.hellocharts.model.PointValue;

public class PrinterActivity extends BaseActivity {

    private static final String TAG = "PrinterActivity";

    private static Handler mHandler = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_printer);

        mHandler = new MHandler(this);
        WorkService.addHandler(mHandler);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        WorkService.delHandler(mHandler);
        mHandler = null;
    }

    public void onClick(View arg0) {
        // TODO Auto-generated method stub
        switch (arg0.getId()) {
            case R.id.print_btn: {
                byte[] buf = new byte[360 * 7];
                int tmp;
                for (int i = 0; i < 360; i++) {
                    buf[i * 7 + 0] = 0x1d;
                    buf[i * 7 + 1] = 0x27;
                    buf[i * 7 + 2] = 0x01;
                    tmp = (int) (180 + 180 * Math.sin(i * Math.PI / 180));
                    buf[i * 7 + 3] = (byte) (tmp & 0xff);
                    buf[i * 7 + 4] = (byte) ((tmp >> 8) & 0xff);
                    tmp += 3;
                    buf[i * 7 + 5] = (byte) (tmp & 0xff);
                    buf[i * 7 + 6] = (byte) ((tmp >> 8) & 0xff);
                }
                if (WorkService.workThread.isConnected()) {
                    Bundle data = new Bundle();
                    data.putByteArray(Global.BYTESPARA1, buf);
                    data.putInt(Global.INTPARA1, 0);
                    data.putInt(Global.INTPARA2, buf.length);
                    WorkService.workThread.handleCmd(Global.CMD_POS_WRITE, data);
                } else {
                    Toast.makeText(this, Global.toast_notconnect, Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    static class MHandler extends Handler {

        WeakReference<PrinterActivity> mActivity;

        MHandler(PrinterActivity activity) {
            mActivity = new WeakReference<PrinterActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            PrinterActivity theActivity = mActivity.get();
            switch (msg.what) {

                case Global.CMD_POS_WRITERESULT: {
                    int result = msg.arg1;
                    Toast.makeText(theActivity, (result == 1) ? Global.toast_success : Global.toast_fail,
                            Toast.LENGTH_SHORT).show();
                    Log.v(TAG, "Result: " + result);
                    break;
                }

            }
        }
    }
}
