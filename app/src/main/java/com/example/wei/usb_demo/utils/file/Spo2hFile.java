package com.example.wei.usb_demo.utils.file;

import com.example.wei.usb_demo.data.db.bean.BloodOxygenModel;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Wei on 2017/2/9.
 */

public class Spo2hFile {

    private static final String TAG = "TAG_Spo2hFile";

    public static byte[] read(File file) {
        if (!file.exists()) {
            return null;
        }
        FileInputStream inputStream = null;
        byte[] data = null;
        try {
            inputStream = new FileInputStream(file);
            int len = 0;
            try {
                len = inputStream.available();
                if (len > 0) {
                    data = new byte[len];
                    inputStream.read(data);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return data;
    }

    /**
     * 写入数据
     * @param file
     * @param data
     */
    public static void writeData(File file, BloodOxygenModel data) {
        if (!file.exists()) {
            Spo2hFile.createFile(file);
        }
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file, true);
            if (out == null) {
                return;
            }
            try {
                for (byte[] data_b: data.getSporhData()) {
                    int spo2 = (data_b[5] >= 0 ? data_b[5] : data_b[5] + 256);
                    int pr_l = data_b[6] >= 0 ? data_b[6] : data_b[6] + 256;
                    int pr_h = data_b[7] >= 0 ? data_b[7] : data_b[7] + 256;
                    int pr = pr_l + pr_h;
                    int pi = data_b[8] >= 0 ? data_b[8] : data_b[8] + 256;
                    out.write((spo2+","+pr+","+pi+"\n\r").getBytes());
//                    out.write(data_b);
//                    out.write((StringUtil.bytesToHexString(data_b)+"\n\r").getBytes());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                out.flush();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean createFile(File file) {
        File path = file.getParentFile();
        path.mkdirs();
        boolean ret = false;
        try {
            ret = file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
    }
}
