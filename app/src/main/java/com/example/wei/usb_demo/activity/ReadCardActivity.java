package com.example.wei.usb_demo.activity;

import android.graphics.Bitmap;
import android.os.Environment;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wei.pl2303_test.R;
import com.example.wei.usb_demo.activity.base.BaseActivity;
import com.hdos.usbdevice.HdosUsbDeviceLib;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

public class ReadCardActivity extends BaseActivity {

    private static final String TAG = "ReadCardActivity";

    private HdosUsbDeviceLib hdosUsbDeviceLib;
    private TextView textView;

    private byte[] name = new byte[32];
    private byte[] sex = new byte[6];
    private byte[] birth = new byte[18];
    private byte[] nation = new byte[12];
    private byte[] address = new byte[72];
    private byte[] Department = new byte[32];
    private byte[] IDNo = new byte[38];
    private byte[] EffectDate = new byte[18];
    private byte[] ExpireDate = new byte[18];
    private byte[] ErrMsg = new byte[20];
    private byte[] BmpFile = new byte[38556];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_card);

        hdosUsbDeviceLib = new HdosUsbDeviceLib(this);

        textView = (TextView) findViewById(R.id.show_view);
    }

    @Override
    protected void onDestroy() {
        hdosUsbDeviceLib.closeDevice();
        super.onDestroy();
    }

    public void btnClick(View v) {
        int btn_id = v.getId();
        if (btn_id == R.id.open_device) {
            if (hdosUsbDeviceLib.openDevice()) {
                Toast.makeText(this, "设备打开成功", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "设备打开失败", Toast.LENGTH_SHORT).show();
            }
        } else if (btn_id == R.id.read_card) {
            Map<String, String> socialSecurityCardInformation = new HashMap<String, String>();
            socialSecurityCardInformation=hdosUsbDeviceLib.readSocialSecurityCard();
            String str = "";
            if(socialSecurityCardInformation.get("result").equals("-1")) {
                str += "读取社保卡失败";
            } else {
                str += "姓名: "+socialSecurityCardInformation.get("cardName") + "\n";
                str += "性别: "+socialSecurityCardInformation.get("cardSex") + "\n";
                str += "民族: "+socialSecurityCardInformation.get("nation") + "\n";
                str += "生日: "+socialSecurityCardInformation.get("birthday") + "\n";
                str += "社会保障卡号: "+socialSecurityCardInformation.get("cardNo") + "\n";
                str += "社会保障号码: "+socialSecurityCardInformation.get("SocialSecurityCardNo") + "\n";
            }
            textView.setText(str);
        }
    }

    public String getValuetoString(String src){

        byte[] temp= src.getBytes();
        int i=0;
        for(;i<temp.length;i++){

            if(temp[i]==0x00) break;
        }
        byte[] des= new byte[i];
        System.arraycopy(temp,0,des,0,i);
        return new String(des);

    }

    private void saveBitmap(Bitmap bitmap)
    {
        File sdcardDir = Environment.getExternalStorageDirectory();
        File file = new File(sdcardDir.getPath()+"/存储的图片.bmp");
        if(file.exists()){
            file.delete();
        }
        FileOutputStream out;
        try{
            out = new FileOutputStream(file);
            if(bitmap.compress(Bitmap.CompressFormat.PNG, 90, out))
            {
                out.flush();
                out.close();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }
}
