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
import com.example.wei.usb_demo.data.db.bean.BloodOxygenModel;
import com.example.wei.usb_demo.main.router.MainUI;

import java.util.List;

/**
 * Created by Wei on 2017/2/15.
 */

public class BloodOxygenHistoryActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    private static final String TAG = "TAG_BloodOxygenHistoryActivity";

    private ListView listView;
    private LayoutInflater inflater;
    private List<BloodOxygenModel> _list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blood_oxygen_history);

        inflater = LayoutInflater.from(this);

        _list = DataDBM.getInstance(this).getAllBloodOxygenModels();

        listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(new MyAdapter(_list));
        listView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        BloodOxygenModel dataModel = _list.get(position);
        Bundle bundle = new Bundle();
        bundle.putParcelable("data_model", dataModel);
        MainRouter.getInstance(BloodOxygenHistoryActivity.this).showActivity(MainUI.BLOOD_OXYGEN_REVIEW, bundle);
    }

    private class MyAdapter extends BaseAdapter {

        private List<BloodOxygenModel> _data = null;

        public MyAdapter(List<BloodOxygenModel> list) {
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
            BloodOxygenModel dataModel = (BloodOxygenModel) getItem(position);
            nameLabel.setText(dataModel.getDataFileName());
            dateLabel.setText(DateUtils.formatDate(dataModel.getDataTime()*1000, DateUtils.yyyyMMddHHmmssGAP));
            return cell;
        }
    }
}
