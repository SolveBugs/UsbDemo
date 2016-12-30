package com.example.wei.usb_demo.activity.base;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

/**
 * Created by zhenqiang on 2016/12/28.
 */

public abstract class BaseActivity extends AppCompatActivity {

    private ToolBarHelper mToolBarHelper;
    public Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setContentView(int layoutResID) {
        mToolBarHelper = new ToolBarHelper(this, layoutResID);
        toolbar = mToolBarHelper.getToolBar();
        setContentView(mToolBarHelper.getContentView());
        setSupportActionBar(toolbar);
        onCreateCustomToolBar(toolbar);
        hideBack(false);
    }

    public void onCreateCustomToolBar(Toolbar toolbar) {
        toolbar.setContentInsetsRelative(0, 0);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void hideBack(boolean hide) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(!hide);
    }
}
