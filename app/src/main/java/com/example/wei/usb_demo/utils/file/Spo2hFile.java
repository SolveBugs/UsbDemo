package com.example.wei.usb_demo.utils.file;

import android.util.Log;

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
    public static void writeData(File file, byte[] data) {
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
                out.write(data);
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
