package com.example.wei.usb_demo.activity;

import android.app.Activity;
import android.hardware.usb.UsbDevice;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.wei.pl2303_test.R;
import com.example.wei.usb_demo.activity.base.BaseActivity;
import com.example.wei.usb_demo.utils.StringUtil;
import com.example.wei.usb_demo.usb_device.BloodOxygenDeviceHandle;
import com.example.wei.usb_demo.usb_device.UsbDeviceHandle;
import com.example.wei.usb_demo.usb_device.UsbHandle;

/**
 * Created by Wei on 2016/12/20.
 */

public class WriteDeviceActivity extends BaseActivity {

    private String deviceKey = "";
    private BloodOxygenDeviceHandle reader = null;
    private EditText editText = null;
    private TextView showView = null;
    private ScrollView scrollView = null;
    private UsbHandle handel = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.write_device_activity);

        Button readBtn = (Button) findViewById(R.id.submit_btn);
        readBtn.setOnClickListener(btnOnClickListener);
        handel = UsbHandle.ShareHandle(this);
        handel.setUSBDetachedListener(usbDetachedListener);

        editText = (EditText) findViewById(R.id.textedit);
        showView = (TextView) findViewById(R.id.show_view);
        scrollView = (ScrollView) findViewById(R.id.scrollview_id);

        final Bundle intentData = getIntent().getExtras();
        deviceKey = intentData.getString("USB_DEVICE_KEY");

        reader = new BloodOxygenDeviceHandle(this, deviceKey);
        reader.setUSBDeviceInputDataListener(usbDeviceInputDataListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        reader.stop();
        reader.release();
        handel.setUSBDetachedListener(null);
    }

    private View.OnClickListener btnOnClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            int btn_id = v.getId();
            byte[] data = null;
            if (btn_id == R.id.submit_btn) {
                String p_id = "SpO2_LFC_FC_Module";
                byte[] pdata = p_id.getBytes();
                byte[] headdata = StringUtil.hexStringToBytes("AA55FF1401");
                data = new byte[headdata.length+pdata.length];
                System.arraycopy(headdata, 0, data, 0, headdata.length);
                System.arraycopy(pdata, 0, data, headdata.length, pdata.length);
                Log.i("AA", "onClick: ");
            } else if (btn_id == R.id.submit_btn1) {
                data = StringUtil.hexStringToBytes("AA555104111101");
            } else if (btn_id == R.id.submit_btn2) {
                data = StringUtil.hexStringToBytes("AA5550030201");
            }
//            String inputStr = editText.getText().toString();
            reader.sendToUsb(data);
//            editText.setText("");
        }
    };

    private UsbDeviceHandle.USBDeviceInputDataListener usbDeviceInputDataListener = new UsbDeviceHandle.USBDeviceInputDataListener() {
        @Override
        public void onUSBDeviceInputData(byte[] data, String deviceKey) {
            String ret_str = StringUtil.bytesToHexString(data);
//            try {
//                ret_str = new String(data, "UTF-8");
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//                Log.i("读取数据", "readFromUsb: "+"异常");
//            }
            Log.i("Write", "run: finalRevWord" + ret_str);
            if (ret_str != null) {
                showView.setText(showView.getText().toString()+"\n"+ret_str);
                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        }
    };

    private UsbHandle.USBDetachedListener usbDetachedListener = new UsbHandle.USBDetachedListener() {
        @Override
        public void onUSBDetached(UsbDevice device) {
            if (device.getDeviceName().equals(deviceKey)) {
                Log.i("USB拔出", "onUSBDetached: "+device.getDeviceName());
                finish();
            }
        }
    };
}
