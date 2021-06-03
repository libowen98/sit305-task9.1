package com.example.positioningdemo;

import android.app.Application;

import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.lljjcoder.style.citylist.utils.CityListLoader;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // 百度地图SDK初始化
        SDKInitializer.initialize(this);
        SDKInitializer.setCoordType(CoordType.BD09LL);
        /**
         * 全国所有城市市的数据
         */
        CityListLoader.getInstance().loadCityData(this);
    }
}
