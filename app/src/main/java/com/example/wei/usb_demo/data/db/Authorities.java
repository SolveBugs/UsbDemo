
package com.example.wei.usb_demo.data.db;

import android.net.Uri;

import com.example.wei.usb_demo.common.database.DnurseAuthority;
import com.example.wei.usb_demo.data.db.bean.BloodOxygenModel;
import com.example.wei.usb_demo.data.db.bean.ModelBloodPressure;
import com.example.wei.usb_demo.data.db.bean.ModelBloodSugar;
import com.example.wei.usb_demo.data.db.bean.ModelReport;
import com.example.wei.usb_demo.utils.file.EcgDataSource;


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

    public static final class DataSpo2h {
        public final static String PATH = BloodOxygenModel.TABLE;
        public final static Uri AUTHORITY_URI = Uri.parse("content://"
                + AUTHORITY + "/" + PATH);
    }

    public static final class DataEcg {
        public final static String PATH = EcgDataSource.TABLE;
        public final static Uri AUTHORITY_URI = Uri.parse("content://"
                + AUTHORITY + "/" + PATH);
    }
    public static final class DataReport {
        public final static String PATH = ModelReport.TABLE;
        public final static Uri AUTHORITY_URI = Uri.parse("content://"
                + AUTHORITY + "/" + PATH);
    }
}
