package com.example.wei.usb_demo.app;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.os.Looper;
import android.text.format.Time;
import android.util.Log;
import android.widget.Toast;

import com.example.wei.pl2303_test.R;
import com.example.wei.usb_demo.activity.base.AppManager;
import com.example.wei.usb_demo.common.utils.StringUtils;
import com.example.wei.usb_demo.common.utils.UIHelper;
import com.example.wei.usb_demo.common.utils.Utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Date;
import java.util.TreeSet;


public class AppException extends Exception implements UncaughtExceptionHandler {
    private static final String TAG = "AppException";

    private static final String CRASH_REPORTER_EXTENSION = ".cr";
    /**
     * 定义异常类型
     */
    public final static byte TYPE_NETWORK = 0x01;
    public final static byte TYPE_SOCKET = 0x02;
    public final static byte TYPE_HTTP_CODE = 0x03;
    public final static byte TYPE_HTTP_ERROR = 0x04;
    public final static byte TYPE_XML = 0x05;
    public final static byte TYPE_IO = 0x06;
    public final static byte TYPE_RUN = 0x07;
    public final static byte TYPE_JSON = 0x08;
    public final static byte TYPE_FORBIDDEN = 0x09;
    public final static byte TYPE_PARAM = 0x0A;
    /**
     *
     */
    private static final long serialVersionUID = 8392861922148780972L;
    private final static boolean Debug = false;// 是否保存错误日志
    private byte type;
    private int code;


    /**
     * 系统默认的UncaughtException处理类
     */
    private UncaughtExceptionHandler mDefaultHandler;

    private AppException() {
        this.mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
    }

    private AppException(byte type, int code, Exception excp) {
        super(excp);
        this.type = type;
        this.code = code;
    }

    public static AppException http(int code) {
        return new AppException(TYPE_HTTP_CODE, code, null);
    }

    public static AppException http(Exception e) {
        return new AppException(TYPE_HTTP_ERROR, 0, e);
    }

    public static AppException socket(Exception e) {
        return new AppException(TYPE_SOCKET, 0, e);
    }

    public static AppException json(Exception e) {
        return new AppException(TYPE_JSON, 0, e);
    }

    public static AppException forbidden(Exception e) {
        return new AppException(TYPE_FORBIDDEN, 0, e);
    }

    public static AppException param(Exception e) {
        return new AppException(TYPE_PARAM, 0, e);
    }

    public static AppException io(Exception e) {
        if (e instanceof UnknownHostException || e instanceof ConnectException) {
            return new AppException(TYPE_NETWORK, 0, e);
        } else if (e instanceof IOException) {
            return new AppException(TYPE_IO, 0, e);
        }
        return run(e);
    }

    public static AppException xml(Exception e) {
        return new AppException(TYPE_XML, 0, e);
    }

    public static AppException network(Exception e) {
        if (e instanceof UnknownHostException || e instanceof ConnectException) {
            return new AppException(TYPE_NETWORK, 0, e);
        } else if (e instanceof SocketException) {
            return socket(e);
        }
        return http(e);
    }

    public static AppException run(Exception e) {
        return new AppException(TYPE_RUN, 0, e);
    }

    public int getCode() {
        return this.code;
    }

    public int getType() {
        return this.type;
    }

    public static AppException getAppExceptionHandler() {
        return new AppException();
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        ex.printStackTrace();
        if (!handleException(ex) && mDefaultHandler != null) {
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            //Sleep一会后结束程序
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
            }
            android.os.Process.killProcess(android.os.Process.myPid());
            //System.exit(10);
        }
    }

    private boolean handleException(Throwable ex) {
        if (ex == null) {
            return false;
        }

        final Context context = AppManager.getAppManager().currentActivity();

        if (context == null) {
            return false;
        }

        Log.d("ygc", "handleException------");

        // 显示异常信息&发送报告

        if (!notToast) {
            new Thread() {
                public void run() {
                    Looper.prepare();
                    new Toast(context).makeText(context, R.string.app_error_message_toast, Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }

            }.start();
        }

        saveCrashInfoToFile(context, ex);

        return true;
    }

    private static boolean notToast;

    public static boolean isNotToast() {
        return notToast;
    }

    public static void setNotToast(boolean notToast) {
        AppException.notToast = notToast;
    }


    private String saveCrashInfoToFile(Context context, Throwable ex) {
        final String crashReport = getCrashReport(context, ex);
        BufferedWriter writer = null;
        try {
            Time t = new Time("GMT+8");
            t.setToNow(); // 取得系统时间
            int date = t.year * 10000 + t.month * 100 + t.monthDay;
            int time = t.hour * 10000 + t.minute * 100 + t.second;
            String fileName = "crash-" + date + "-" + time + CRASH_REPORTER_EXTENSION;

            File cr = new File(context.getFilesDir(), fileName);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(cr), StringUtils.ENCODE);
            writer = new BufferedWriter(outputStreamWriter);
            writer.write(crashReport);
            Utils.writeToSd(crashReport);
            return fileName;
        } catch (IOException e) {
            Log.e(TAG, "an error occured while writing report file...", e);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                }
            }
        }
        return null;
    }

    private static String[] getCrashReportFiles(Context ctx) {
        File filesDir = ctx.getFilesDir();
        FilenameFilter filter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(CRASH_REPORTER_EXTENSION);
            }
        };
        return filesDir.list(filter);
    }

    public static String readFileByLines(File file) {
        BufferedReader reader = null;
        StringBuilder stringBuilder = new StringBuilder();
        try {
            InputStreamReader isr = new InputStreamReader(new FileInputStream(file), StringUtils.ENCODE);
            reader = new BufferedReader(isr);
            String tempString = null;
            int line = 1;
            while ((tempString = reader.readLine()) != null) {
                stringBuilder.append(tempString);
                stringBuilder.append("\n");
                line++;
            }
            reader.close();

        } catch (IOException e) {
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }

        Log.d("ygc", stringBuilder.toString());
        return stringBuilder.toString();
    }


    private String getCrashReport(Context context, Throwable ex) {
        AppContext appContext = (AppContext) context.getApplicationContext();
        PackageInfo pinfo = appContext.getPackageInfo();

        StringBuffer exceptionStr = new StringBuffer();
        exceptionStr.append("dattime: " + new Date() + "\n");
        exceptionStr.append("appid: " + pinfo.packageName + "\n");
        exceptionStr.append("Version: " + pinfo.versionName + "("
                + pinfo.versionCode + ")\n");
        String serviceType = "developer";

        exceptionStr.append("\nException: \n" + ex.getMessage() + "\n");
        StackTraceElement[] elements = ex.getStackTrace();
        for (int i = 0; i < elements.length; i++) {
            exceptionStr.append(elements[i].toString() + "\n");
        }
        if (ex.getCause() != null) {
            elements = ex.getCause().getStackTrace();

            exceptionStr.append("\n\nCaused by:\n");
            for (int i = 0; i < elements.length; i++) {
                exceptionStr.append(elements[i].toString() + "\n");
            }
        }

        return exceptionStr.toString();
    }

}
