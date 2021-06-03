package com.example.positioningdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.positioningdemo.act.NewPlaceActivity;
import com.example.positioningdemo.act.ShowMapActivity;
import com.tbruyelle.rxpermissions3.RxPermissions;

//首页
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView newPlace;//添加位置记录
    private TextView showMap;//查看位置记录
    private RxPermissions rxPermissions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();//初始化View控件
    }

    //初始化View 和权限适配
    private void init() {
        rxPermissions = new RxPermissions(this);
        newPlace = findViewById(R.id.newPlace);
        showMap = findViewById(R.id.showMap);
        newPlace.setOnClickListener(this::onClick);
        showMap.setOnClickListener(this::onClick);
    }

    private Intent intent;

    @Override
    public void onClick(View v) {
        //判断是否有权限
        if (!isPermission()) {
            return;
        }
        switch (v.getId()) {
            case R.id.newPlace:
                //点击添加
                intent = new Intent(MainActivity.this, NewPlaceActivity.class);
                startActivity(intent);
                break;
            case R.id.showMap:
                //点击查看历史记录
                intent = new Intent(MainActivity.this, ShowMapActivity.class);
                startActivity(intent);

                break;
        }
    }

    /**
     * 定位所需要的权限
     * GPS+数据流量+基站
     */
    boolean myPermi = false;

    private boolean isPermission() {
        rxPermissions.request(Manifest.permission.ACCESS_COARSE_LOCATION
                , Manifest.permission.ACCESS_FINE_LOCATION
                , Manifest.permission.WRITE_EXTERNAL_STORAGE
                , Manifest.permission.READ_PHONE_STATE).subscribe(granted -> {
            if (granted) {
                myPermi = true;
            } else {
                //没用权限
                ToUtils.toast(MainActivity.this, "Please go to the settings center to open permissions!");
                myPermi = false;
            }
        });
        return myPermi;
    }
}