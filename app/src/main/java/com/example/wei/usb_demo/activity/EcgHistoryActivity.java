package com.example.wei.usb_demo.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.wei.pl2303_test.R;
import com.example.wei.usb_demo.activity.base.BaseActivity;
import com.example.wei.usb_demo.app.MainRouter;
import com.example.wei.usb_demo.common.utils.DateUtils;
import com.example.wei.usb_demo.data.db.DataDBM;
import com.example.wei.usb_demo.main.router.MainUI;
import com.example.wei.usb_demo.utils.file.EcgDataSource;

import java.util.List;

/**
 * Created by Wei on 2017/2/15.
 */

public class EcgHistoryActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    private static final String TAG = "TAG_EcgHistoryActivity";

    private ListView listView;
    private LayoutInflater inflater;
    private List<EcgDataSource> _list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blood_oxygen_history);

        inflater = LayoutInflater.from(this);

        _list = DataDBM.getInstance(this).getAllEcgDataSources();

        listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(new MyAdapter(_list));
        listView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        EcgDataSource dataModel = _list.get(position);
        Bundle bundle = new Bundle();
        bundle.putString("ecgFile", "/sdcard/EcgSdkDemo/"+dataModel.getDataFileName());
        MainRouter.getInstance(EcgHistoryActivity.this).showActivity(MainUI.ECG_REVIEW, bundle);
    }

    private class MyAdapter extends BaseAdapter {

        private List<EcgDataSource> _data = null;

        public MyAdapter(List<EcgDataSource> list) {
            super();
            _data = list;
        }

        @Override
        public int getCount() {
            if (_data == null) {
                return 0;
            }
            return _data.size();
        }

        @Override
        public Object getItem(int position) {
            return _data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View cell = inflater.inflate(R.layout.data_history_adapter_item_layout, null);
            TextView nameLabel = (TextView) cell.findViewById(R.id.name_label);
            TextView dateLabel = (TextView) cell.findViewById(R.id.date_label);
            EcgDataSource dataModel = (EcgDataSource) getItem(position);
            nameLabel.setText(dataModel.getDataFileName());
            dateLabel.setText(DateUtils.formatDate(dataModel.getDataStartTime(), DateUtils.yyyyMMddHHmmssGAP));
            return cell;
        }
    }
}
