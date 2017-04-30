package com.qg.route.routemap;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.help.Tip;
import com.amap.api.services.route.BusPath;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RidePath;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkPath;
import com.amap.api.services.route.WalkRouteResult;
import com.qg.route.BaseActivity;
import com.qg.route.R;
import com.qg.route.bean.Trace;
import com.qg.route.route.RouteFragment;
import com.qg.route.utils.AMapUtil;
import com.qg.route.utils.Constant;
import com.qg.route.utils.SPUtil;
import com.qg.route.custom.ChooseRoute;
import com.qg.route.utils.ToastUtil;

import java.util.List;

import overlay.BusRouteOverlay;
import overlay.DrivingRouteOverlay;
import overlay.RideRouteOverlay;
import overlay.WalkRouteOverlay;


/**
 * Created by Ricco on 2017/4/10.
 * 选择路线功能
 */

public class RouteActivity extends BaseActivity implements AMap.OnMyLocationChangeListener, AMap.OnMapLoadedListener, ChooseRoute.OnRouteClickListener, RouteSearch.OnRouteSearchListener, RouteSelectedAdapter.OnClickListener {
    private static final String TAG = "ROUTEACTIVITY";

    // 权限请求
    private static final int REQUEST_LOCATION = 0;

    // intent key
    public static final String ROUTE_NAME = "ROUTENAME";

    // sharedpreference key name
    private static final String LOCATION_LONGITUDE = "LONGITUDE";
    private static final String LOCATION_LATITUDE = "LATITUDE";

    // activity for result key name
    private static final int REQUEST_SET_ROUTE_START = 0;
    private static final int REQUEST_SET_ROUTE_END = 1;

    // 经度和纬度
    private double mLongitude;
    private double mLatitude;

    // 起点与终点
    private LatLonPoint mStartPoint;
    private LatLonPoint mEndPoint;
            
    // 精度范围圆形边框颜色   
    private static final int STROKE_COLOR = Color.argb(255, 103, 115, 255);
    // 精度范围填充颜色
    private static final int FILL_COLOR = Color.argb(10, 0, 0, 180);

    // 地图
    private AMap aMap;
    private MapView mapView;
    private RouteSearch mRouteSearch;

    // search result
    private WalkRouteResult mWalkRouteResult;
    private BusRouteResult mBusRouteResult;
    private DriveRouteResult mDriveRouteResult;
    private RideRouteResult mRideRouteResult;

    // adapter and data
    private RouteSelectedAdapter mSelectedAdapter;

    private ChooseRoute chooseRoute;
    private Toolbar mToolBar;
    private RecyclerView mGridView;

    private int selectedRoute = 0;
    private int selectedPath = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);

        initToolBar();
        initGridView();
        initMap();

        mapView.onCreate(savedInstanceState);// 此方法必须重写
    }

    private void initGridView() {
        mGridView = (RecyclerView) findViewById(R.id.gv_select_route);
        mGridView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mGridView.setAdapter(mSelectedAdapter = new RouteSelectedAdapter(this, R.layout.item_route_select, 0));
        mSelectedAdapter.setOnClickListener(this);
    }

    private void initToolBar() {
        mToolBar = (Toolbar) findViewById(R.id.tb_route);
        mToolBar.setTitle("选择路线");
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /**
     * 初始化初始化地图
     */
    private void initMap() {
        mapView =  (MapView) findViewById(R.id.map);

        if (aMap == null) {
            aMap = mapView.getMap();
            setUpMap();
        }
        mRouteSearch = new RouteSearch(this);
        mRouteSearch.setRouteSearchListener(this);
        chooseRoute = (ChooseRoute) findViewById(R.id.route_choose);
        chooseRoute.setOnRouteClickListener(this);
    }

    /**
     * 设置一些amap的属性
     */
    private void setUpMap() {

        aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        aMap.getUiSettings()
                .setZoomPosition(AMapOptions.ZOOM_POSITION_RIGHT_CENTER);
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
                fromResource(R.drawable.amap_start));
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
        mStartPoint = new LatLonPoint(mLatitude, mLongitude);
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

        mStartPoint = new LatLonPoint(mLatitude, mLongitude);
        LatLng location = AMapUtil.convertToLatLng(mStartPoint);
        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 16));
        Log.d(TAG, "onMapLoaded:"  + mLongitude + ":" + mLatitude);
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

    @Override
    public void walk(View view) {
        searchRouteResult(Constant.ROUTE_TYPE_WALK, RouteSearch.WALK_DEFAULT);
    }

    @Override
    public void ride(View view) {
        searchRouteResult(Constant.ROUTE_TYPE_RIDE, RouteSearch.RIDING_DEFAULT);
    }

    @Override
    public void bus(View view) {
        searchRouteResult(Constant.ROUTE_TYPE_BUS, RouteSearch.BUS_DEFAULT);
    }

    @Override
    public void drive(View view) {
        searchRouteResult(Constant.ROUTE_TYPE_DRIVE, RouteSearch.DRIVING_SINGLE_DEFAULT);
    }

    @Override
    public void start(View view) {
        if (chooseRoute.getStartText().equals(getResources().getString(R.string.text_my_position))) {
            SettingRouteActivity.actionStartForResult(this, REQUEST_SET_ROUTE_START, "");
        } else  {
            SettingRouteActivity.actionStartForResult(this, REQUEST_SET_ROUTE_START, chooseRoute.getStartText());
        }
    }

    @Override
    public void end(View view) {
        SettingRouteActivity.actionStartForResult(this, REQUEST_SET_ROUTE_END, chooseRoute.getEndText());
    }

    private void searchRouteResult(int routeType, int mode) {
        if (mStartPoint == null) {
            ToastUtil.show(this, "起点未设置");
            return;
        }
        if (mEndPoint == null) {
            ToastUtil.show(this, "终点未设置");
        }

        final RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(
                mStartPoint, mEndPoint);
        if (routeType == Constant.ROUTE_TYPE_BUS) {// 公交路径规划
            RouteSearch.BusRouteQuery query = new RouteSearch.BusRouteQuery(fromAndTo, mode,
                    Constant.CURRENT_CITY, 0);// 第一个参数表示路径规划的起点和终点，第二个参数表示公交查询模式，第三个参数表示公交查询城市区号，第四个参数表示是否计算夜班车，0表示不计算
            mRouteSearch.calculateBusRouteAsyn(query);// 异步路径规划公交模式查询
        } else if (routeType == Constant.ROUTE_TYPE_DRIVE) {// 驾车路径规划
            RouteSearch.DriveRouteQuery query = new RouteSearch.DriveRouteQuery(fromAndTo, mode, null,
                    null, "");// 第一个参数表示路径规划的起点和终点，第二个参数表示驾车模式，第三个参数表示途经点，第四个参数表示避让区域，第五个参数表示避让道路
            mRouteSearch.calculateDriveRouteAsyn(query);// 异步路径规划驾车模式查询
        } else if (routeType == Constant.ROUTE_TYPE_WALK) {// 步行路径规划
            RouteSearch.WalkRouteQuery query = new RouteSearch.WalkRouteQuery(fromAndTo);
            mRouteSearch.calculateWalkRouteAsyn(query);// 异步路径规划步行模式查询
        } else if (routeType == Constant.ROUTE_TYPE_RIDE) {
            RouteSearch.RideRouteQuery query = new RouteSearch.RideRouteQuery(fromAndTo);
            mRouteSearch.calculateRideRouteAsyn(query);// 异步路径规划骑行模式查询
        }
    }

    private void setSelectedRoute(BusRouteResult  result, int i) {
        List<BusPath> path = result.getPaths();
        int num = path.size();
        if (num > 3) num = 3;
        // adapter 数据集改变
        mSelectedAdapter.setClickItem(i);
        mSelectedAdapter.notifyNumChanged(num);
        mGridView.setVisibility(View.VISIBLE);
        for (int j = 0; j < num; j++) {
            // 判断那一条才是选择的
            if (j != i) {
                BusPath busPath = path.get(j);
                BusRouteOverlay busrouteOverlay = new BusRouteOverlay(this,
                        aMap, busPath,
                        mBusRouteResult.getStartPos(),
                        mBusRouteResult.getTargetPos(), false);
                busrouteOverlay.removeFromMap();
                busrouteOverlay.setNodeIconVisibility(false);
                busrouteOverlay.addToMap();
            }
        }
        BusPath busPath = path.get(i);
        BusRouteOverlay busrouteOverlay = new BusRouteOverlay(this,
                aMap, busPath,
                mBusRouteResult.getStartPos(),
                mBusRouteResult.getTargetPos(), true);
        busrouteOverlay.removeFromMap();
        busrouteOverlay.setNodeIconVisibility(false);
        busrouteOverlay.addToMap();
        busrouteOverlay.zoomToSpan();
    }
    
    private void setSelectedRoute(DriveRouteResult result, int i) {

        List<DrivePath> path = result.getPaths();
        int num = path.size();
        if (num > 3) num = 3;
        // adapter 数据集改变
        mSelectedAdapter.setClickItem(i);
        mSelectedAdapter.notifyNumChanged(num);
        mGridView.setVisibility(View.VISIBLE);
        for (int j = 0; j < num; j++) {
            // 判断那一条才是选择的
            if (j != i) {
                DrivePath drivePath = path.get(j);
                DrivingRouteOverlay drivingRouteOverlay = new DrivingRouteOverlay(
                        this, aMap, drivePath,
                        mDriveRouteResult.getStartPos(),
                        mDriveRouteResult.getTargetPos(), null, false);
                drivingRouteOverlay.setNodeIconVisibility(false);//设置节点marker是否显示
                drivingRouteOverlay.setIsColorfulline(false);//是否用颜色展示交通拥堵情况，默认true
                drivingRouteOverlay.removeFromMap();
                drivingRouteOverlay.addToMap();
            }
        }
        DrivePath drivePath = path.get(i);
        DrivingRouteOverlay drivingRouteOverlay = new DrivingRouteOverlay(
                this, aMap, drivePath,
                mDriveRouteResult.getStartPos(),
                mDriveRouteResult.getTargetPos(), null, true);
        drivingRouteOverlay.setNodeIconVisibility(false);//设置节点marker是否显示
        drivingRouteOverlay.setIsColorfulline(false);//是否用颜色展示交通拥堵情况，默认true
        drivingRouteOverlay.removeFromMap();
        drivingRouteOverlay.addToMap();
        drivingRouteOverlay.zoomToSpan();
    }

    private void setSelectedRoute(WalkRouteResult  result, int i) {
        List<WalkPath> path = result.getPaths();
        int num = path.size();
        if (num > 3) num = 3;
        // adapter 数据集改变
        mSelectedAdapter.setClickItem(i);
        mSelectedAdapter.notifyNumChanged(num);
        mGridView.setVisibility(View.VISIBLE);
        for (int j = 0; j < num; j++) {
            // 判断那一条才是选择的
            if (j != i) {
                WalkPath walkPath = path.get(j);
                WalkRouteOverlay walkRouteOverlay = new WalkRouteOverlay(
                        this, aMap, walkPath,
                        mWalkRouteResult.getStartPos(),
                        mWalkRouteResult.getTargetPos(), false);
                walkRouteOverlay.removeFromMap();
                walkRouteOverlay.setNodeIconVisibility(false);
                walkRouteOverlay.addToMap();
            }
        }

        WalkPath walkPath = path.get(i);
        WalkRouteOverlay walkRouteOverlay = new WalkRouteOverlay(
                this, aMap, walkPath,
                mWalkRouteResult.getStartPos(),
                mWalkRouteResult.getTargetPos(), true);
        walkRouteOverlay.removeFromMap();
        walkRouteOverlay.setNodeIconVisibility(false);
        walkRouteOverlay.addToMap();
        walkRouteOverlay.zoomToSpan();
    }

    private void setSelectedRoute(RideRouteResult  result, int i) {
        List<RidePath> path = result.getPaths();
        int num = path.size();
        if (num > 3) num = 3;
        // adapter 数据集改变
        mSelectedAdapter.setClickItem(i);
        mSelectedAdapter.notifyNumChanged(num);
        mGridView.setVisibility(View.VISIBLE);
        for (int j = 0; j < num; j++) {
            // 判断那一条才是选择的
            if (j != i) {
                RidePath ridePath = mRideRouteResult.getPaths().get(j);
                RideRouteOverlay rideRouteOverlay = new RideRouteOverlay(
                        this, aMap, ridePath,
                        mRideRouteResult.getStartPos(),
                        mRideRouteResult.getTargetPos(), false);
                rideRouteOverlay.removeFromMap();
                rideRouteOverlay.setNodeIconVisibility(false);
                rideRouteOverlay.addToMap();
            }
        }
        // 保证目标路线是最后一条绘制的
        RidePath ridePath = mRideRouteResult.getPaths().get(i);
        RideRouteOverlay rideRouteOverlay = new RideRouteOverlay(
                this, aMap, ridePath,
                mRideRouteResult.getStartPos(),
                mRideRouteResult.getTargetPos(), true);
        rideRouteOverlay.removeFromMap();
        rideRouteOverlay.setNodeIconVisibility(false);
        rideRouteOverlay.addToMap();
        rideRouteOverlay.zoomToSpan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 0) {
            return;
        }
        if (requestCode == REQUEST_SET_ROUTE_START) {
            Tip tip = data.getParcelableExtra(ROUTE_NAME);
            mStartPoint = tip.getPoint();
            chooseRoute.setStartText(tip.getName());
        } else if (requestCode == REQUEST_SET_ROUTE_END) {
            Tip tip = data.getParcelableExtra(ROUTE_NAME);
            mEndPoint = tip.getPoint();
            chooseRoute.setEndText(tip.getName());
        }
    }

    /**
     * 公交路线搜索结果方法回调
     */
    @Override
    public void onBusRouteSearched(BusRouteResult result, int errorCode) {

        aMap.clear();// 清理地图上的所有覆盖物
        if (errorCode == AMapException.CODE_AMAP_SUCCESS) {
            if (result != null && result.getPaths() != null) {
                if (result.getPaths().size() > 0) {
                    mBusRouteResult = result;
                    setSelectedRoute(result, 0);
                    selectedRoute = Constant.ROUTE_TYPE_BUS;

                } else if (result.getPaths() == null) {
                    ToastUtil.show(this, R.string.no_result);
                }
            } else {
                ToastUtil.show(this, R.string.no_result);
            }
        } else {
            ToastUtil.showerror(this.getApplicationContext(), errorCode);
        }
    }

    /**
     * 驾车路线搜索结果方法回调
     */
    @Override
    public void onDriveRouteSearched(DriveRouteResult result, int errorCode) {

        aMap.clear();// 清理地图上的所有覆盖物
        if (errorCode == AMapException.CODE_AMAP_SUCCESS) {
            if (result != null && result.getPaths() != null) {
                if (result.getPaths().size() > 0) {
                    mDriveRouteResult = result;
                    setSelectedRoute(result, 0);
                    selectedRoute = Constant.ROUTE_TYPE_DRIVE;

                } else if (result.getPaths() == null) {
                    ToastUtil.show(this, R.string.no_result);
                }

            } else {
                ToastUtil.show(this, R.string.no_result);
            }
        } else {
            ToastUtil.showerror(this.getApplicationContext(), errorCode);
        }
    }

    @Override
    public void onWalkRouteSearched(WalkRouteResult result, int errorCode) {

        aMap.clear();// 清理地图上的所有覆盖物
        if (errorCode == AMapException.CODE_AMAP_SUCCESS) {
            if (result != null && result.getPaths() != null) {
                if (result.getPaths().size() > 0) {
                    mWalkRouteResult = result;
                    setSelectedRoute(result, 0);
                    selectedRoute = Constant.ROUTE_TYPE_WALK;

                } else if (result != null && result.getPaths() == null) {
                    ToastUtil.show(this, R.string.no_result);
                }

            } else {
                ToastUtil.show(this, R.string.no_result);
            }
        } else {
            ToastUtil.showerror(this.getApplicationContext(), errorCode);
        }
    }

    @Override
    public void onRideRouteSearched(RideRouteResult result, int errorCode) {
        aMap.clear();// 清理地图上的所有覆盖物
        if (errorCode == AMapException.CODE_AMAP_SUCCESS) {
            if (result != null && result.getPaths() != null) {
                if (result.getPaths().size() > 0) {
                    mRideRouteResult = result;
                    setSelectedRoute(result, 0);
                    selectedRoute = Constant.ROUTE_TYPE_RIDE;

                } else if (result != null && result.getPaths() == null) {
                    ToastUtil.show(this, R.string.no_result);
                }
            } else {
                ToastUtil.show(this, R.string.no_result);
            }
        } else {
            ToastUtil.showerror(this.getApplicationContext(), errorCode);
        }
    }

    @Override
    public void onClick(View v, int position) {
        aMap.clear();
        selectedPath = position;
        switch (selectedRoute) {
            case Constant.ROUTE_TYPE_WALK:
                setSelectedRoute(mWalkRouteResult, position);
                break;
            case Constant.ROUTE_TYPE_RIDE:
                setSelectedRoute(mRideRouteResult, position);
                break;
            case Constant.ROUTE_TYPE_BUS:
                setSelectedRoute(mBusRouteResult, position);
                break;
            case Constant.ROUTE_TYPE_DRIVE:
                setSelectedRoute(mDriveRouteResult, position);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_map, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_confirm) {
            if (selectedRoute == 0) {
                ToastUtil.show(this, "还没选择路线");
            } else {
                Intent intent = new Intent();
                switch (selectedRoute) {
                    case Constant.ROUTE_TYPE_WALK:
                        intent.putExtra(RouteFragment.TRACE,
                                new Trace(mStartPoint, mEndPoint, selectedRoute,
                                        mWalkRouteResult.getPaths().get(selectedPath)));
                        break;
                    case Constant.ROUTE_TYPE_BUS:
                        intent.putExtra(RouteFragment.TRACE,
                                new Trace(mStartPoint, mEndPoint, selectedRoute,
                                        mBusRouteResult.getPaths().get(selectedPath)));
                        break;
                    case Constant.ROUTE_TYPE_DRIVE:
                        intent.putExtra(RouteFragment.TRACE,
                                new Trace(mStartPoint, mEndPoint, selectedRoute,
                                        mDriveRouteResult.getPaths().get(selectedPath)));
                        break;
                    case Constant.ROUTE_TYPE_RIDE:
                        intent.putExtra(RouteFragment.TRACE,
                                new Trace(mStartPoint, mEndPoint, selectedRoute,
                                        mRideRouteResult.getPaths().get(selectedPath)));
                        break;
                    default:
                        break;
                }
                setResult(RouteFragment.RESULT_ROUTE_SUCCESS, intent);
                finish();
            }
        } else if (item.getItemId() == android.R.id.home) {
            setResult(RouteFragment.RESULT_ROUTE_FAILURE);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    // 快速启动
    public static void actionStartForResult(Activity context, Fragment fragment,  int requestCode) {
        Intent intent = new Intent(context, RouteActivity.class);
        fragment.startActivityForResult(intent, requestCode);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(0);
    }
}
