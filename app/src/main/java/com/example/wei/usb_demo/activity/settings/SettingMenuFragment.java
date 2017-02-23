package com.example.wei.usb_demo.activity.settings;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.wei.pl2303_test.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingMenuFragment extends Fragment {

    private String[] menuList = {"WLAN", "微信绑定", "添加家庭成员", "显示与声音", "时间与日期", "系统"};

    public SettingMenuFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mainView = inflater.inflate(R.layout.fragment_setting_menu, container, false);

        ListView listView = (ListView) mainView.findViewById(R.id.list_view);
        listView.setAdapter(new MyAdapter());

        return mainView;
    }

    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (menuList == null) {
                return 0;
            }
            return menuList.length;
        }

        @Override
        public Object getItem(int position) {
            return menuList[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView cell = new TextView(getContext());
            cell.setHeight(110);
            cell.setBackgroundColor(Color.TRANSPARENT);
            cell.setText(menuList[position]);
            cell.setTextColor(Color.WHITE);
            return cell;
        }
    }
}
