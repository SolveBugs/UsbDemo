package com.example.wei.usb_demo.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;

/**
 * Created by ygc on 14-10-23.
 */
public final class IDGenerator {
    private final static int RANDOM_LEN = 6;
    private final static int DNU_ID_GENERATOR_SEQ_MAX = 1000000;

    private static SimpleDateFormat sDateFormat = null;

    private IDGenerator() {

    }

    public static String newID() {
        return newIdWithTag(null);
    }

    public static String newIdWithTag(String tag) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat format = getDateFormat();
        String curDate = format.format(cal.getTime());
        Random random = new Random();
        int value = random.nextInt(DNU_ID_GENERATOR_SEQ_MAX);
        String strValue = String.format(Locale.US, "%06d", value);
        StringBuilder sb = new StringBuilder();
        if (tag != null) {
            sb.append(tag);
        }

        sb.append(curDate);
        sb.append(strValue);

        return sb.toString();
    }

    public static String newIdWithTag(String tag, long dataTime, long oldId) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(dataTime);
        SimpleDateFormat format = getDateFormat();
        String curDate = format.format(cal.getTime());

        int value;
        if (oldId != 0) {
            value = (int) (oldId % DNU_ID_GENERATOR_SEQ_MAX);
        } else {
            value = new Random().nextInt(DNU_ID_GENERATOR_SEQ_MAX);
        }
        String strValue = String.format(Locale.US, "%06d", value);

        StringBuilder sb = new StringBuilder();
        if (tag != null)
            sb.append(tag);
        sb.append(curDate);
        sb.append(strValue);

        return sb.toString();
    }


    public static String getNextID(String oldId) {
        if (oldId.length() < RANDOM_LEN) {
            return oldId;
        }

        String beforeString = oldId.substring(0, oldId.length() - RANDOM_LEN);
        String snString = oldId.substring(oldId.length() - RANDOM_LEN);
        int iSeqSn = Integer.parseInt(snString);
        iSeqSn = (iSeqSn + 1) % DNU_ID_GENERATOR_SEQ_MAX;
        String newValue = String.format(Locale.US, "%06d", iSeqSn);
        return beforeString + newValue;
    }

    private static SimpleDateFormat getDateFormat() {
        synchronized (IDGenerator.class) {
            if (sDateFormat == null) {
                sDateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.US);
            }
        }

        return sDateFormat;
    }


    public static String taskTag(int tag) {
        return taskTag(String.valueOf(tag));
    }

    public static String taskTag(String tag) {
        tag = "000" + tag;
        tag = tag.substring(tag.length() - 3, tag.length());
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd", Locale.US);
        String curDate = format.format(cal.getTime());
        StringBuilder sb = new StringBuilder();

        sb.append("T");
        sb.append(curDate);
        sb.append(tag);

        return sb.toString();
    }
}
