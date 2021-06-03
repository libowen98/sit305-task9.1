package com.example.positioningdemo.act;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.example.positioningdemo.R;
import com.lljjcoder.style.citylist.CityListSelectActivity;
import com.lljjcoder.style.citylist.bean.CityInfoBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 地图->模糊搜索位置
 */
public class LocationActivity extends Activity  implements View.OnClickListener,OnGetPoiSearchResultListener,OnGetSuggestionResultListener,BaiduMap.OnMapClickListener,BaiduMap.OnMarkerClickListener{

    private MapView mapview;//地图定位
    private EditText etSearch;//搜索内容

    private TextView btSearch;//点击搜索的按钮
    private TextView btSave;//点击保存的按钮
    private TextView tvCity;//当前定位的城市


    private static final int REQUEST_CODE_PICK_CITY = 0;
    // 地图View实例
    private BaiduMap mBaiduMap = null;


    private PoiSearch mPoiSearch = null;

    private SuggestionSearch mSuggestionSearch = null;


    private BitmapDescriptor mBitmapDescWaterDrop =
            BitmapDescriptorFactory.fromResource(R.mipmap.icon_circle_select);

    private HashMap<Marker, PoiInfo> mMarkerPoiInfo = new HashMap<>();

    private Marker mPreSelectMarker = null;


    // 分页
    private int mLoadIndex = 0;

    private double mLongitude;//经度
    private double mLatitude;//维度
    private String city;
    private String name;//搜索的内容

    private double resultLongitude;//确认选中的维度
    private double resultLatitude;//确认选中的经度
    private String resultName;//确认选中位置的名字

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_activity);
        mLongitude = NewPlaceActivity.mLongitude == 0.0 ? 116.385306 : NewPlaceActivity.mLongitude;
        mLatitude = NewPlaceActivity.mLatitude == 0.0 ? 39.871281 : NewPlaceActivity.mLatitude;
        city = NewPlaceActivity.city == null ? "北京" :  NewPlaceActivity.city;
        name = NewPlaceActivity.editName;
        init();
    }



    private void init() {
        initView();
        initMap();
        mPoiSearch = PoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(this);

        mSuggestionSearch = SuggestionSearch.newInstance();
        mSuggestionSearch.setOnGetSuggestionResultListener(this);
        searchPoiInCity();
    }

    private void initMap() {
        if (null == mapview) {
            return;
        }

        mBaiduMap = mapview.getMap();
        if (null == mBaiduMap) {
            return;
        }

        // 解决圆角屏幕手机，地图logo被遮挡的问题
        mBaiduMap.setViewPadding(30, 0, 30, 20);
        mapview.showZoomControls(false);

        // 设置初始中心点
        LatLng center = new LatLng(mLatitude, mLongitude);
        MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newLatLngZoom(center, 12);
        mBaiduMap.setMapStatus(mapStatusUpdate);
        mBaiduMap.setOnMapClickListener(this);
        mBaiduMap.setOnMarkerClickListener(this);
    }

    private void initView() {
        mapview = findViewById(R.id.mapview);
        etSearch = findViewById(R.id.etSearch);

        tvCity = findViewById(R.id.tvCity);
        btSearch = findViewById(R.id.btSearch);
        btSave = findViewById(R.id.btSave);

        tvCity.setText(city);
        etSearch.setText(name);

        tvCity.setOnClickListener(this::onClick);
        btSearch.setOnClickListener(this::onClick);
        btSave.setOnClickListener(this::onClick);
        if (null == tvCity || null == etSearch) {
            return;
        }
        name = getIntent().getStringExtra("name");
    }

    private void searchPoiInCity() {
        String cityStr = tvCity.getText().toString();
        // 获取检索关键字
        String keyWordStr = etSearch.getText().toString();
        if (TextUtils.isEmpty(cityStr) || TextUtils.isEmpty(keyWordStr)) {
            return;
        }

        mPoiSearch.searchInCity((new PoiCitySearchOption())
                .city(cityStr)
                .keyword(keyWordStr)
                .pageNum(mLoadIndex) // 分页编号
                .cityLimit(true)
                .scope(1));
    }
    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.btSearch: //**********点击搜索********
                searchPoiInCity();
                break;
            case R.id.btSave://***点击保存*******
                intent = new Intent();
                intent.putExtra("longitude", resultLongitude);
                intent.putExtra("latitude", resultLatitude);
                intent.putExtra("name", resultName);
                intent.putExtra("province",city);
                setResult(1, intent);
                finish();
                break;
            case R.id.tvCity://*****切换城市**********
                intent = new Intent(this, CityListSelectActivity.class);
                startActivityForResult(intent, CityListSelectActivity.CITY_SELECT_RESULT_FRAG);
                break;
        }
    }

    /**
     * 以下基本上都是百度地图第三方SDK的回调和监听方法
     * 是从官网Demo里面参考得到的
     * @param poiResult
     */

    @Override
    public void onGetPoiResult(PoiResult poiResult) {
        if (poiResult == null || poiResult.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
            mLoadIndex = 0;
            Toast.makeText(this, "未找到结果", Toast.LENGTH_LONG).show();
            return;
        }

        List<PoiInfo> poiInfos = poiResult.getAllPoi();
        if (null == poiInfos) {
            return;
        }

        setPoiResult(poiInfos);
    }

    /**
     * @param poiDetailResult
     * @deprecated
     */
    @Override
    public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {
    }

    @Override
    public void onGetPoiDetailResult(PoiDetailSearchResult poiDetailSearchResult) {

    }

    @Override
    public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

    }

    @Override
    public void onGetSuggestionResult(SuggestionResult suggestionResult) {
        if (suggestionResult == null
                || suggestionResult.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
            mLoadIndex = 0;
            Toast.makeText(this, "未找到结果", Toast.LENGTH_LONG).show();
            return;
        }

        List<SuggestionResult.SuggestionInfo> suggesInfos = suggestionResult.getAllSuggestions();
        if (null == suggesInfos) {
            return;
        }

    }

    @Override
    public void onMapClick(LatLng latLng) {
    }

    @Override
    public void onMapPoiClick(MapPoi mapPoi) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (null == marker || null == mMarkerPoiInfo || mMarkerPoiInfo.size() <= 0) {
            return false;
        }

        Iterator itr = mMarkerPoiInfo.entrySet().iterator();
        Marker tmpMarker;
        PoiInfo poiInfo = null;
        Map.Entry<Marker, PoiInfo> markerPoiInfoEntry;
        while (itr.hasNext()) {
            markerPoiInfoEntry = (Map.Entry<Marker, PoiInfo>) itr.next();
            tmpMarker = markerPoiInfoEntry.getKey();
            if (null == tmpMarker) {
                continue;
            }

            if (tmpMarker.getId() == marker.getId()) {
                poiInfo = markerPoiInfoEntry.getValue();
                break;
            }
        }

        if (null == poiInfo) {
            return false;
        }

        InfoWindow infoWindow = getPoiInfoWindow(poiInfo);

        mBaiduMap.showInfoWindow(infoWindow);
        resultLatitude = poiInfo.location.latitude;
        resultLongitude = poiInfo.location.longitude;
        resultName = poiInfo.getCity() + poiInfo.getName();
        if (null != mPreSelectMarker) {
            mPreSelectMarker.setScale(1.0f);
        }

        marker.setScale(1.5f);
        mPreSelectMarker = marker;

        return true;
    }

    private void setPoiResult(List<PoiInfo> poiInfos) {
        if (null == poiInfos || poiInfos.size() <= 0) {
            return;
        }

        clearData();

        // 将地图平移到 latLng 位置
        LatLng latLng = poiInfos.get(0).getLocation();
        MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newLatLng(latLng);
        mBaiduMap.setMapStatus(mapStatusUpdate);
        resultLatitude = latLng.latitude;
        resultLongitude = latLng.longitude;
        resultName = poiInfos.get(0).getCity() + poiInfos.get(0).getName();
        Iterator itr = poiInfos.iterator();
        List<LatLng> latLngs = new ArrayList<>();
        PoiInfo poiInfo = null;
        int i = 0;
        //搜索结果
        while (itr.hasNext()) {
            poiInfo = (PoiInfo) itr.next();
            if (null == poiInfo) {
                continue;
            }

            locatePoiInfo(poiInfo, i);
            latLngs.add(poiInfo.getLocation());


            i++;
        }

        setBounds(latLngs);
    }


    private void locatePoiInfo(PoiInfo poiInfo, int i) {
        if (null == poiInfo) {
            return;
        }

        // 显示当前的
        showPoiMarker(poiInfo, i);
    }

    private void showPoiMarker(PoiInfo poiInfo, int i) {
        if (null == poiInfo) {
            return;
        }

        MarkerOptions markerOptions = new MarkerOptions()
                .position(poiInfo.getLocation())
                .icon(mBitmapDescWaterDrop);

        // 第一个poi放大显示
        if (0 == i) {
            InfoWindow infoWindow = getPoiInfoWindow(poiInfo);
            markerOptions.scaleX(1.5f).scaleY(1.5f).infoWindow(infoWindow);
        }

        Marker marker = (Marker) mBaiduMap.addOverlay(markerOptions);
        if (null != marker) {
            mMarkerPoiInfo.put(marker, poiInfo);

            if (0 == i) {
                mPreSelectMarker = marker;
            }
        }
    }

    private InfoWindow getPoiInfoWindow(PoiInfo poiInfo) {
        TextView textView = new TextView(this);
        textView.setText(poiInfo.getName());
        textView.setPadding(10, 5, 10, 5);
        textView.setBackground(this.getResources().getDrawable(R.drawable.bg_info));
        InfoWindow infoWindow = new InfoWindow(textView, poiInfo.getLocation(), -150);
        return infoWindow;
    }



    private void clearData() {
        mBaiduMap.clear();
        mMarkerPoiInfo.clear();
        mPreSelectMarker = null;
    }



    /**
     * 最佳视野内显示所有点标记
     */
    private void setBounds(List<LatLng> latLngs) {
        if (null == latLngs || latLngs.size() <= 0) {
            return;
        }

        int horizontalPadding = 80;
        int verticalPaddingBottom = 400;

        // 构造地理范围对象
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        // 让该地理范围包含一组地理位置坐标
        builder.include(latLngs);

        // 设置显示在指定相对于MapView的padding中的地图地理范围
        MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newLatLngBounds(builder.build(),
                horizontalPadding,
                verticalPaddingBottom,
                horizontalPadding,
                verticalPaddingBottom);
        // 更新地图
        mBaiduMap.setMapStatus(mapStatusUpdate);
        // 设置地图上控件与地图边界的距离，包含比例尺、缩放控件、logo、指南针的位置
        mBaiduMap.setViewPadding(0,
                0,
                0,
                verticalPaddingBottom);
    }

    //    选中某个城市
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CityListSelectActivity.CITY_SELECT_RESULT_FRAG) {
            if (resultCode == RESULT_OK) {
                if (data == null) {
                    return;
                }
                Bundle bundle = data.getExtras();

                CityInfoBean cityInfoBean = (CityInfoBean) bundle.getParcelable("cityinfo");

                if (null == cityInfoBean) {
                    return;
                }
                city = cityInfoBean.getName();
                tvCity.setText(cityInfoBean.getName());
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (null != mapview) {
            mapview.onResume();
        }
    }

    @SuppressWarnings("checkstyle:WhitespaceAround")
    @Override
    protected void onPause() {
        super.onPause();
        if (null != mapview) {
            mapview.onPause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mPoiSearch != null) {
            mPoiSearch.destroy();
        }

        if (null != mSuggestionSearch) {
            mSuggestionSearch.destroy();
        }

        if (null != mapview) {
            mapview.onDestroy();
        }

        if (null != mBitmapDescWaterDrop) {
            mBitmapDescWaterDrop.recycle();
        }
    }
}
