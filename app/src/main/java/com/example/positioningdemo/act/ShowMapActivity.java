package com.example.positioningdemo.act;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;
import com.example.positioningdemo.R;
import com.example.positioningdemo.db.DbManage;
import com.example.positioningdemo.db.MapInfoBean;

import java.util.ArrayList;
import java.util.List;

/**
 * 查看历史地图记录页面
 */
public class ShowMapActivity extends Activity{
    MapView mapview;
    //数据库类
    private DbManage dbManage;
    private BaiduMap mBaiduMap = null;
    //地图上显示的图标
    private BitmapDescriptor mBitmapDescriptor = BitmapDescriptorFactory.fromResource(R.mipmap.icon_circle_select);
    private List<MapInfoBean> dataList;//从数据量里面取出历史记录
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_on_map_activity);
        init();
    }

    private void init() {
        mapview = findViewById(R.id.mapview);
        mBaiduMap = mapview.getMap();
        mBaiduMap.setMyLocationEnabled(true);
        //初始化数据库
        dbManage = DbManage.getInstance();
        dbManage.init(this);
        dataList = dbManage.getPositioningInfo();
        List<InfoWindow> infoWindowList = new ArrayList<>();
        for (int i = 0; i < dataList.size(); i++) {
            if (this.mBitmapDescriptor != null) {
                Button button = new Button(getApplicationContext());
                button.setTextSize(10.0f);//文字大小 字节
                button.setWidth(300);//宽度
                button.setPadding(5, 5, 5, 5);//边距
                button.setBackgroundResource(R.drawable.top);//地理位置背景图
                button.setText(dataList.get(i).getName());
                button.setTextColor(-1);//
                LatLng point = new LatLng(dataList.get(i).getLatitude(), dataList.get(i).getLongitude());
                infoWindowList.add(new InfoWindow(BitmapDescriptorFactory.fromView(button), point, -95, null));
                this.mBaiduMap.addOverlay(new MarkerOptions().icon(this.mBitmapDescriptor).position(point));//添加图片
            }
        }
        this.mBaiduMap.showInfoWindows(infoWindowList);
    }


}
