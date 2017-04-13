package com.qg.route.route;

import android.Manifest;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.PolylineOptions;
import com.qg.route.BaseActivity;
import com.qg.route.R;
import com.qg.route.utils.Constant;
import com.qg.route.utils.SPUtil;


/**
 * Created by Ricco on 2017/4/10.
 * 选择路线功能
 */

public class RouteActivity extends BaseActivity implements AMap.OnMyLocationChangeListener, AMap.OnMapLoadedListener {
    private static final String TAG = "ROUTEACTIVITY";

    private static final int REQUEST_LOCATION = 0;
    private static final String LOCATION_LONGITUDE = "LONGITUDE";
    private static final String LOCATION_LATITUDE = "LATITUDE";

    private AMap aMap;
    private MapView mapView;

    private double mLongitude;
    private double mLatitude;
            
    // 精度范围圆形边框颜色   
    private static final int STROKE_COLOR = Color.argb(180, 3, 145, 255);
    // 精度范围填充颜色
    private static final int FILL_COLOR = Color.argb(10, 0, 0, 180);



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);

        mapView =  (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);// 此方法必须重写

        init();
    }

    /**
     * 初始化
     */
    private void init() {
        // 初始化地图
        if (aMap == null) {
            aMap = mapView.getMap();
            setUpMap();
        }
    }

    /**
     * 设置一些amap的属性
     */
    private void setUpMap() {

        aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        aMap.setOnMyLocationChangeListener(this);
        aMap.setOnMapLoadedListener(this);
        setupLocationStyle();
    }

    /**
     * 设置自定义定位蓝点
     */
    private void setupLocationStyle(){
        // 自定义系统定位蓝点
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        // 自定义定位蓝点图标
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.
                fromResource(R.drawable.gps_point));
        // 自定义精度范围的圆形边框颜色
        myLocationStyle.strokeColor(STROKE_COLOR);
        //自定义精度范围的圆形边框宽度
        myLocationStyle.strokeWidth(5);
        // 设置圆形的填充颜色
        myLocationStyle.radiusFillColor(FILL_COLOR);
        //定位一次，且将视角移动到地图中心点。
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE) ;
        // 将自定义的 myLocationStyle 对象添加到地图上
        aMap.setMyLocationStyle(myLocationStyle);
    }

    @Override
    public void onMyLocationChange(Location location) {
        mLongitude = location.getLongitude();
        mLatitude = location.getLatitude();
        Log.d(TAG, "onMyLocationChange: " + mLongitude + ":" + mLatitude);
    }

    @Override
    public void onMapLoaded() {
        // 初始化经纬度
        mLatitude = Double.valueOf(
                (String) SPUtil.get(getApplicationContext(), LOCATION_LATITUDE, Constant.GDUT_LATITUDE));
        mLongitude = Double.valueOf(
                (String) SPUtil.get(getApplicationContext(), LOCATION_LONGITUDE, Constant.GDUT_LONGITUDE));
        // 防止取到空值
        if ( mLongitude == 0 && mLatitude == 0) {
            mLatitude = Double.valueOf(Constant.GDUT_LATITUDE);
            mLongitude = Double.valueOf(Constant.GDUT_LONGITUDE);
        }

        Log.d(TAG, "onMapLoaded:"  + mLongitude + ":" + mLatitude);
        LatLng location = new LatLng(mLatitude, mLongitude);
        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 16));
    }

    public void onChange(View view) {
        aMap.addPolyline((new PolylineOptions())
                .add(new LatLng(43.828, 87.621), new LatLng(45.808, 126.55))
                .geodesic(true).color(Color.RED));
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        requestPermission(REQUEST_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION);
        mapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
        // 保存当前经纬度消息
        SPUtil.put(getApplicationContext(), LOCATION_LONGITUDE, String.valueOf(mLongitude));
        SPUtil.put(getApplicationContext(), LOCATION_LATITUDE, String.valueOf(mLatitude));
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mapView.onDestroy();
	}
}
