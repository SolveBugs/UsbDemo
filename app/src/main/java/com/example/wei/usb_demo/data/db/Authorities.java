
package com.example.wei.usb_demo.data.db;

import android.net.Uri;

import com.example.wei.usb_demo.common.database.DnurseAuthority;
import com.example.wei.usb_demo.data.db.bean.ModelBloodPressure;
import com.example.wei.usb_demo.data.db.bean.ModelBloodSugar;


public final class Authorities extends DnurseAuthority {

    public static final class DataPressure {
        public final static String PATH = ModelBloodPressure.TABLE;
        public final static Uri AUTHORITY_URI = Uri.parse("content://"
                + AUTHORITY + "/" + PATH);
    }

    public static final class DataSugar {
        public final static String PATH = ModelBloodSugar.TABLE;
        public final static Uri AUTHORITY_URI = Uri.parse("content://"
                + AUTHORITY + "/" + PATH);
    }
}
