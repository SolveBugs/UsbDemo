package com.example.wei.usb_demo.activity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.wei.pl2303_test.R;
import com.example.wei.usb_demo.activity.base.BaseActivity;
import com.example.wei.usb_demo.utils.printer_utils.myprinter.Global;
import com.example.wei.usb_demo.utils.printer_utils.myprinter.WorkService;
import com.example.wei.usb_demo.utils.printer_utils.utils.FileUtils;

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

        if (null == WorkService.workThread) {
            Intent intent = new Intent(this, WorkService.class);
            startService(intent);
        }

        handleIntent(getIntent());
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

    private void handleIntent(Intent intent) {
        String action = intent.getAction();
        String type = intent.getType();
        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                handleSendText(intent); // Handle text being sent
            } else if (type.startsWith("image/")) {
                handleSendImage(intent); // Handle single image being sent
            } else {
                handleSendRaw(intent);
            }
        }
    }

    private void handleSendText(Intent intent) {
        Uri textUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (textUri != null) {
            // Update UI to reflect text being shared

            if (WorkService.workThread.isConnected()) {
                byte[] buffer = { 0x1b, 0x40, 0x1c, 0x26, 0x1b, 0x39, 0x01 }; // 设置中文，切换双字节编码。
                Bundle data = new Bundle();
                data.putByteArray(Global.BYTESPARA1, buffer);
                data.putInt(Global.INTPARA1, 0);
                data.putInt(Global.INTPARA2, buffer.length);
                WorkService.workThread.handleCmd(Global.CMD_POS_WRITE, data);
            }
            if (WorkService.workThread.isConnected()) {
                String path = textUri.getPath();
                String strText = FileUtils.ReadToString(path);
                byte buffer[] = strText.getBytes();

                Bundle data = new Bundle();
                data.putByteArray(Global.BYTESPARA1, buffer);
                data.putInt(Global.INTPARA1, 0);
                data.putInt(Global.INTPARA2, buffer.length);
                data.putInt(Global.INTPARA3, 128);
                WorkService.workThread.handleCmd(
                        Global.CMD_POS_WRITE_BT_FLOWCONTROL, data);

            } else {
                Toast.makeText(this, Global.toast_notconnect,
                        Toast.LENGTH_SHORT).show();
            }

            finish();
        }
    }

    private void handleSendImage(Intent intent) {
        Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (imageUri != null) {
            String path = getRealPathFromURI(imageUri);

            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, opts);
            opts.inJustDecodeBounds = false;
            if (opts.outWidth > 1200) {
                opts.inSampleSize = opts.outWidth / 1200;
            }

            Bitmap mBitmap = BitmapFactory.decodeFile(path);

            if (mBitmap != null) {
                if (WorkService.workThread.isConnected()) {
                    Bundle data = new Bundle();
                    data.putParcelable(Global.PARCE1, mBitmap);
                    data.putInt(Global.INTPARA1, 384);
                    data.putInt(Global.INTPARA2, 0);
                    WorkService.workThread.handleCmd(
                            Global.CMD_POS_PRINTPICTURE, data);
                } else {
                    Toast.makeText(this, Global.toast_notconnect,
                            Toast.LENGTH_SHORT).show();
                }
            }
            finish();
        }
    }

    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.MediaColumns.DATA };
        CursorLoader loader = new CursorLoader(this, contentUri, proj, null,
                null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();
        String path = cursor.getString(column_index);
        cursor.close();
        return path;
    }

    private void handleSendRaw(Intent intent) {
        Uri textUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (textUri != null) {
            // Update UI to reflect text being shared
            if (WorkService.workThread.isConnected()) {
                String path = textUri.getPath();
                byte buffer[] = FileUtils.ReadToMem(path);
                // Toast.makeText(this, "length:" + buffer.length,
                // Toast.LENGTH_LONG).show();
                Bundle data = new Bundle();
                data.putByteArray(Global.BYTESPARA1, buffer);
                data.putInt(Global.INTPARA1, 0);
                data.putInt(Global.INTPARA2, buffer.length);
                data.putInt(Global.INTPARA3, 256);
                WorkService.workThread.handleCmd(
                        Global.CMD_POS_WRITE_BT_FLOWCONTROL, data);

            } else {
                Toast.makeText(this, Global.toast_notconnect,
                        Toast.LENGTH_SHORT).show();
            }

            // finish();
        }
    }
}
