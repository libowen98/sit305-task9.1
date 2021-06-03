package com.example.positioningdemo.act;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;

import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.example.positioningdemo.R;

/**
 * 显示当前位置页面
 */
public class ShowOnMapActivity extends Activity {

    private MapView mapview;

    private BaiduMap mBaiduMap = null;
    private LocationClient mLocationClient;

    private double mLongitude;//经度
    private double mLatitude;//维度

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_on_map_activity);
        //初始化
        init();
    }

    private void init() {
        mapview = findViewById(R.id.mapview);
        mBaiduMap = mapview.getMap();
        mBaiduMap.setMyLocationEnabled(true);

        //定位初始化
        mLocationClient = new LocationClient(this);
        Log.e("TAG","mLongitude2------->"+NewPlaceActivity.mLongitude);
        /**
         * 如需修改别的城市，请自行去百度地图抓取具体的城市经纬度修改下面的mLongitude,mLatitude
         * 考虑到是模拟器的问题，给你定死位置在北京的坐标
         */
        //        mLongitude = AddMapActivity.mLongitude == 0.0 ? 116.38 : AddMapActivity.mLongitude;
        //        mLatitude = AddMapActivity.mLatitude == 0.0 ? 39.9 : AddMapActivity.mLatitude;
        mLongitude = 116.38;
        mLatitude = 39.9;
        //通过LocationClientOption设置LocationClient相关参数
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);
        // 设置初始中心点
        LatLng center = new LatLng(mLatitude, mLongitude);
        MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newLatLngZoom(center, 12);
        mBaiduMap.setMapStatus(mapStatusUpdate);
        // 构建Marker图标
        BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.mipmap.icon_circle_select); // 推算结果;
        // 构建MarkerOption，用于在地图上添加Marker
        OverlayOptions layOption = new MarkerOptions().position(center).icon(bitmap);
        // 在地图上添加Marker，并显示
        mBaiduMap.addOverlay(layOption);
        //设置locationClientOption
        mLocationClient.setLocOption(option);
        //开启地图定位图层
        mLocationClient.start();
        //LatLng ll = new LatLng(mLatitude, mLongitude);
        MapStatus.Builder builder = new MapStatus.Builder();
        builder.target(center).zoom(20.0f);
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
    }

    @Override
    protected void onResume() {
        mapview.onResume();
        super.onResume();
    }

    @Override
    protected void onPause() {
        mapview.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mLocationClient.stop();
        mBaiduMap.setMyLocationEnabled(false);
        mapview.onDestroy();
        mapview = null;
        super.onDestroy();
    }

}
