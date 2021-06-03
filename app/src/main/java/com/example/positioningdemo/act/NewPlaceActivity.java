package com.example.positioningdemo.act;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.MyLocationData;
import com.example.positioningdemo.R;
import com.example.positioningdemo.ToUtils;
import com.example.positioningdemo.db.DbManage;
import com.example.positioningdemo.db.MapInfoBean;

/**
 * 添加确认页面
 */
public class NewPlaceActivity extends Activity implements View.OnClickListener {
    private EditText placeName;//搜索地方名
    private TextView location;//跳转到搜索页
    private TextView showOnMap;//跳转到显示当前位置的地图页面
    private TextView save;//保存位置记录

    private DbManage dbManage;
    private LocationClient mClient;
    private boolean isFirstLoc = true;//是否第一次获取位置

    public static double mLongitude;//地图的经度
    public static double mLatitude;//地图的维度
    public static String city;//定位地址的名
    public static String editName;//搜索位置名字
    private static String province;//定位城市

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_place_activity);
        //初始化数据库
        dbManage = DbManage.getInstance();
        dbManage.init(this);
        //初始化百度地图的信息
        baiduInit();
        //初始化view
        init();
    }

    private void init() {
        placeName = findViewById(R.id.placeName);
        location = findViewById(R.id.location);
        showOnMap = findViewById(R.id.showOnMap);
        save = findViewById(R.id.save);
        location.setOnClickListener(this::onClick);
        showOnMap.setOnClickListener(this::onClick);
        save.setOnClickListener(this::onClick);
    }

    //初始化百度地图的信息
    private void baiduInit() {
        mClient = new LocationClient(this);
        //通过LocationClientOption设置LocationClient相关参数
        LocationClientOption option = new LocationClientOption();
        option.setScanSpan(1000);
        option.setIsNeedAddress(true);
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        mClient.setLocOption(option);
        //注册LocationListener监听器
        MyLocationListener myLocationListener = new MyLocationListener();
        mClient.registerLocationListener(myLocationListener);
        //开启地图定位图层
        mClient.start();
    }



    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.location://******跳转到搜索页面*********
                //判断有没有输入地方名字
                if (TextUtils.isEmpty(placeName.getText().toString())){
                    ToUtils.toast(this,"Input Place Name");
                    return;
                }
                editName = placeName.getText().toString();
                Intent intent = new Intent(this, LocationActivity.class);
                startActivityForResult(intent, 1);
                break;

            case R.id.showOnMap://******显示当前位置*********
                //跳转到显示当前位置的页面
                Intent mIntent = new Intent(this,ShowOnMapActivity.class);
                startActivity(mIntent);
                break;
            case R.id.save://******保存按钮*********
                String name = placeName.getText().toString();
                if (TextUtils.isEmpty(name) || mLongitude == 0.0 || mLatitude == 0.0){
                    ToUtils.toast(this,"Location Can not be empty!");
                    return;
                }
                MapInfoBean data = new MapInfoBean();
                data.setLatitude(mLatitude);
                data.setLongitude(mLongitude);
                data.setName(placeName.getText().toString());
                data.setProvince(province);
                dbManage.addRecording(data);
                finish();
                Toast.makeText(this, "Saved successfully!", Toast.LENGTH_LONG).show();
                break;
        }
    }

    public class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location == null || !isFirstLoc)  {
                return;
            }
            //获取到定位的信息
            isFirstLoc = false;
            mLongitude = location.getLongitude();
            mLatitude = location.getLatitude();
            city = location.getCity();
            MyLocationData locData = new MyLocationData.Builder().accuracy(location.getRadius()).direction(location.getDirection()).latitude(location.getLatitude()).longitude(location.getLongitude()).build();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode){
            case 1:
                if (data == null){ return; }
                mLongitude = data.getDoubleExtra("longitude",0);
                mLatitude = data.getDoubleExtra("latitude",0);
                editName = data.getStringExtra("name");
                province = data.getStringExtra("province");
                placeName.setText(editName);
                break;
        }
    }
    @Override
    protected void onDestroy() {
        mClient.stop();
        super.onDestroy();
    }
}
