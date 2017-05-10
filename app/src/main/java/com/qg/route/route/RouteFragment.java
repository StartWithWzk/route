package com.qg.route.route;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ViewStubCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.qg.route.R;
import com.qg.route.animition.ZoomOutPageTransformer;
import com.qg.route.bean.Business;
import com.qg.route.bean.ChatRoom;
import com.qg.route.bean.RequestResult;
import com.qg.route.bean.Route;
import com.qg.route.bean.Trace;
import com.qg.route.bean.User;
import com.qg.route.chat.ChatActivity;
import com.qg.route.main.MainActivity;
import com.qg.route.routemap.RouteActivity;
import com.qg.route.utils.Constant;
import com.qg.route.utils.HttpUtil;
import com.qg.route.utils.JsonUtil;
import com.qg.route.utils.ToastUtil;
import com.qg.route.utils.URLHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by Ricco on 2017/4/18.
 */

public class RouteFragment extends Fragment implements View.OnClickListener {
    public static final String TAG = "RouteFragment";

    public final static int RESULT_ROUTE_SUCCESS = 1;
    public final static int RESULT_ROUTE_FAILURE = 0;
    public final static int REQUEST_ROUTE = 0;
    public final static String TRACE = "TRACE";

    // UI组件
    private ViewPager mViewPage;
    private RecyclerView mUserHeadRV;
    private TextView mGroupName;
    private TextView mGroupNum;
    private RelativeLayout mGroupRView;
    private Button mGroupAdd;
    private FloatingActionButton mFAB;
    private ViewStubCompat viewStubCompat; // 空视图
    private RelativeLayout mEmptyView;
    private RecyclerView mBusinessRV;
    // 路线详情
    private TextView mStartName; // 路线详情起点和终点的名字
    private TextView mEndName;
    private ImageView mRouteTypePic; // 路线类型
    private TextView mRouteTypeText;

    // adapter
    private RouteViewPagerAdapter mRouteVPagerAdapter;
    private BusinessAdapter mBusinessAdapter;

    // 数据类
    private ArrayList<Route> mPath;
    private ArrayList<Business> mBusinesses;
    private ChatRoom mChatRoom; // 圈子数据类
    private ArrayList<User> mUserList;

    private int currentItem; // 当前路线
    private UserHeadAdapter mUserHeadAdapter;
    private boolean isInflateEmptyView; // 是否显示了空页面

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 初始化路线
        mPath = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_route, container, false);

        viewStubCompat = (ViewStubCompat) view.findViewById(R.id.empty_view);
        mEmptyView = (RelativeLayout) view.findViewById(R.id.rl_empty_view);

        // 浮动按钮设置点击事件
        mFAB = (FloatingActionButton) view.findViewById(R.id.fb_add_route);
        mFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RouteActivity.actionStartForResult(getActivity(), RouteFragment.this, REQUEST_ROUTE);
            }
        });
        setUpViewPage(view);
        initScrollView(view);

        return view;
    }

    /**
     * 初始化主视图
     * @param view
     */
    private void initScrollView(View view) {
        // 这两行代码需要后期改进，考虑RecyclerView的多种ViewHolder情况,目前主要只用解决RL抢夺焦点问题
        NestedScrollView nestview = (NestedScrollView) view.findViewById(R.id.nsv_route);
        nestview.smoothScrollTo(0, 0);

        // 路线详情
        mStartName = (TextView) view.findViewById(R.id.tv_start);
        mEndName = (TextView) view.findViewById(R.id.tv_end);
        mRouteTypePic = (ImageView) view.findViewById(R.id.iv_route_type);
        mRouteTypeText = (TextView) view.findViewById(R.id.tv_route_type);


        // 圈子详情
        // 为圈子名称设置点击事件
        mGroupRView = (RelativeLayout) view.findViewById(R.id.rl_group_name);
        mGroupRView.setOnClickListener(this);
        // 圈子名称以及人数
        mGroupName = (TextView) view.findViewById(R.id.tv_group_name);
        mGroupNum = (TextView) view.findViewById(R.id.tv_group_num);
        // 圈子头像横向列表
        mUserList = new ArrayList<>();
        mUserHeadRV = (RecyclerView) view.findViewById(R.id.rv_user_head);
        mUserHeadRV.setLayoutManager( // 设置为横向
                new GridLayoutManager(getActivity(), 5));
        // 先设置空数据的adapter
        mUserHeadRV.setAdapter(mUserHeadAdapter =
                new UserHeadAdapter(getActivity(), R.layout.item_user_headview, mUserList, 5));
        mGroupAdd = (Button) view.findViewById(R.id.btn_add_group);
        mGroupAdd.setOnClickListener(this);

        // 初始化商家信息
        mBusinessRV = (RecyclerView) view.findViewById(R.id.rv_business_list);
        mBusinessRV.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mBusinesses = new ArrayList<>();
        mBusinessRV.setAdapter(mBusinessAdapter = new BusinessAdapter(mBusinesses, getActivity()));

        // 请求路线数据
        HttpUtil.DoGet(Constant.RouteUrl.ROUTE_LIST, new HttpUtil.HttpConnectCallback() {
            @Override
            public void onSuccess(Response response) {
                ResponseBody mBody = response.body();
                if(mBody != null){
                    RequestResult requestResult = JsonUtil.toObject(mBody.charStream(), RequestResult.class);
                    // 路线数据
                    if (requestResult.getState() == 206) {
                        ArrayList<Route> temp = JsonUtil.toObjectList(requestResult.getData()
                                , new TypeToken<ArrayList<Route>>(){}.getType());
                        mPath.clear();
                        mPath.addAll(temp);

                        // 若是路线数目大于0，隐藏空白页面，并加载跟新viewpager
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (mPath.size() > 0) {
                                    if (mRouteVPagerAdapter != null) {
                                        mRouteVPagerAdapter.notifyViewChanged(mPath);
                                        mRouteVPagerAdapter.notifyDataSetChanged();
                                    }
                                    setRouteDetail();
                                    setGroupDetail(mPath.get(0).getId());
                                    setBusinessDetail();
                                } else { // 无数据，显示空页面
                                    viewStubCompat.inflate();
                                    isInflateEmptyView = true;
                                }
                            }
                        });

                        Log.d(TAG, "onSuccess: 获得路线成功，路线数目：" + mPath.size());
                    } else  {
                        viewStubCompat.inflate();
                        Log.d(TAG, "onSuccess: 获得路线失败，错误状态：" + requestResult.getState());
                    }
                }
            }

            @Override
            public void onFailure(IOException e) {

            }

            @Override
            public void onFailure() {

            }
        }, false);
    }

    /**
     * 设置路线ViewPage
     * @param view
     */
    private void setUpViewPage(View view) {
        // 初始化ViewPage
        mViewPage = (ViewPager) view.findViewById(R.id.vp_route);
        mViewPage.setPageMargin(48);
        mViewPage.setOffscreenPageLimit(3);
        mViewPage.setPageTransformer(true, new ZoomOutPageTransformer());
        mViewPage.setAdapter(mRouteVPagerAdapter = new RouteViewPagerAdapter(mPath, getActivity()));
        mViewPage.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                currentItem = position;
                setRouteDetail();
                setGroupDetail(mPath.get(position).getId());
                setBusinessDetail();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    /**
     * 设置商家
     */
    private void setBusinessDetail() {
        // 获取商家数据
        HttpUtil.DoGet(Constant.BusinessUrl.BUSINESS_LIST, new HttpUtil.HttpConnectCallback() {
            @Override
            public void onSuccess(Response response) {
                ResponseBody mBody = response.body();
                if(mBody != null){
                    RequestResult requestResult = JsonUtil.toObject(mBody.charStream(), RequestResult.class);
                    // 商家数据
                    ArrayList<Business> temp = JsonUtil.toObjectList(requestResult.getData()
                            , new TypeToken<List<Business>>(){}.getType());
                    mBusinesses.clear();
                    mBusinesses.addAll(temp);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (mBusinessAdapter != null) {
                                mBusinessAdapter.notifyDataSetChanged();
                            }
                        }
                    });
                }
            }

            @Override
            public void onFailure(IOException e) {

            }

            @Override
            public void onFailure() {

            }
        }, false);
    }

    /**
     * 设置圈子详情
     */
    private void setGroupDetail(final int routeId) {
        HttpUtil.DoGet(Constant.GroupUrl.GROUP_GET + routeId, new HttpUtil.HttpConnectCallback() {
            @Override
            public void onSuccess(Response response) {
                final ResponseBody body = response.body();
                if (body != null) {
                    RequestResult requestResult = JsonUtil.toObject(body.charStream(), RequestResult.class);
                    if (requestResult.getState() == 207) {
                        Log.d(TAG, "onSuccess: " + requestResult.getStateInfo());
                        if (requestResult.getData() != null) { // 数据不为空，说明圈子含有数据
                            mChatRoom = JsonUtil.toObject(requestResult.getData().toString(), ChatRoom.class);
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mGroupName.setText(mChatRoom.getRoomName());
                                    mGroupNum.setText(mChatRoom.getRoomCount() + "");
                                    if (mChatRoom.getRoomCount() > 0) { //若是圈子人数大于0，则请求用户列表
                                        HttpUtil.DoGet(Constant.GroupUrl.GROUP_GET_LIST + mChatRoom.getId(),
                                                new HttpUtil.HttpConnectCallback() {
                                                    @Override
                                                    public void onSuccess(Response response) {
                                                        ResponseBody body1 = response.body();
                                                        if (body1 != null) {
                                                            RequestResult requestResult1 = JsonUtil.toObject(body1.charStream(), RequestResult.class);
                                                            Log.d(TAG, "onSuccess: " + requestResult1.getStateInfo());
                                                            ArrayList<User> temp = JsonUtil.toObjectList(requestResult1.getData()
                                                                    , new TypeToken<ArrayList<User>>(){}.getType());
                                                            mUserList.clear();
                                                            mUserList.addAll(temp);
                                                            getActivity().runOnUiThread(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    mUserHeadAdapter.notifyDataSetChanged();
                                                                }
                                                            });
                                                        }
                                                    }

                                                    @Override
                                                    public void onFailure(IOException e) {

                                                    }

                                                    @Override
                                                    public void onFailure() {

                                                    }
                                                }, false);
                                    }
                                }
                            });
                        } else { // 到此处，说明，圈子为空，那么
                            mChatRoom = null;
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mGroupName.setText("好好学习天天向上");
                                    mGroupNum.setText("0");
                                    mUserList.clear();
                                    mUserHeadAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                    }
                }
            }

            @Override
            public void onFailure(IOException e) {

            }

            @Override
            public void onFailure() {

            }
        }, false);
    }

    /**
     * 设置路线详情
     */
    private void setRouteDetail() {
        Route route = mPath.get(currentItem);
        mStartName.setText(route.getStartName());
        mEndName.setText(route.getEndName());
        switch (route.getSelectedRouteType()) {
            case Constant.ROUTE_TYPE_WALK:
                mRouteTypeText.setText("步行");
                Glide.with(getActivity()).load(R.mipmap.selected_walk).into(mRouteTypePic);
                break;
            case Constant.ROUTE_TYPE_RIDE:
                mRouteTypeText.setText("骑行");
                Glide.with(getActivity()).load(R.mipmap.selected_bike).into(mRouteTypePic);
                break;
            case Constant.ROUTE_TYPE_BUS:
                mRouteTypeText.setText("公交");
                Glide.with(getActivity()).load(R.mipmap.selected_bus).into(mRouteTypePic);
                break;
            case Constant.ROUTE_TYPE_DRIVE:
                mRouteTypeText.setText("驾车");
                Glide.with(getActivity()).load(R.mipmap.selected_car).into(mRouteTypePic);
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(final int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_ROUTE_SUCCESS) {
            return;
        }
        if (requestCode == REQUEST_ROUTE) {
            Trace trace = data.getParcelableExtra(TRACE);
            if (trace != null) {
                Route route = trace.getRoute();
                mPath.add(route);
                Log.d(TAG, "onActivityResult: " + JsonUtil.toJson(route));
                if (isInflateEmptyView) {
                    // TODO: 2017/5/8 隐藏画面
                    viewStubCompat.setVisibility(View.GONE);
                }
                HttpUtil.PostMap(Constant.RouteUrl.ROUTE_SAVE, URLHelper.sendRoute(JsonUtil.toJson(route))
                        , new HttpUtil.HttpConnectCallback() {
                    @Override
                    public void onSuccess(Response response) {
                        final ResponseBody body = response.body();
                        if (body != null) {
                            final RequestResult requestResult = JsonUtil.toObject(body.charStream(), RequestResult.class);
                            if (requestResult.getState() == 201) {
                                int routeId = JsonUtil.toObject(requestResult.getData().toString(), Integer.class);
                                mPath.get(mPath.size() - 1).setId(routeId);
                                Log.d(TAG, "onSuccess: routeId" + routeId);
                                // 在此处更新圈子详情
                                setGroupDetail(routeId);
                            }
                        }
                    }

                    @Override
                    public void onFailure(IOException e) {

                    }

                    @Override
                    public void onFailure() {

                    }
                }, false);
                mRouteVPagerAdapter.notifyViewChanged(mPath);
                mRouteVPagerAdapter.notifyDataSetChanged();
                currentItem = mPath.size() - 1;
                mViewPage.setCurrentItem(currentItem, true);
            }
        }
    }

    private void toChatActivity(ChatRoom chatRoom){
        if(chatRoom != null) {
            Intent intent = ChatActivity.newIntent(getActivity(), chatRoom.getRoomName(), chatRoom.getId() + "", true);
            startActivity(intent);
        }
    }

    /**
     * 加入圈子点击
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_group_name:
                if (mChatRoom == null) { // 还不存在圈子的时候
                    ToastUtil.show(getActivity(), "此圈子没还有被创建");
                } else {
                    GroupDetailActivity.actionStart(getActivity(), mChatRoom.getId());
                }
                break;
            case R.id.btn_add_group:
                if (mChatRoom != null) {
                    // 应该加入圈子,这里还应该判断我以前是否加入过此圈子
                    HttpUtil.DoGet(Constant.GroupUrl.GROUP_ALL, new HttpUtil.HttpConnectCallback() {
                        @Override
                        public void onSuccess(Response response) {
                            Log.d(TAG, "onSuccess: GROUP_ALL");
                            ResponseBody body = response.body();
                            if (body != null) {
                                final RequestResult requestResult = JsonUtil.toObject(body.charStream(), RequestResult.class);
                                if (requestResult.getState() == 301) {
                                    ArrayList<ChatRoom> mChatRoomList = JsonUtil.toObjectList(requestResult.getData(),
                                            new TypeToken<ArrayList<ChatRoom>>(){}.getType());
                                    for (ChatRoom chatRoom : mChatRoomList
                                         ) {
                                        if (chatRoom.getId() == mChatRoom.getId()) { // 说明我曾经加入过此圈子
                                            toChatActivity(chatRoom);
                                            return;
                                        }
                                    }
                                    // 到达此处说明我没有加入过此圈子，于是请求加入
                                    HttpUtil.DoGet(Constant.GroupUrl.GROUP_ADD + mChatRoom.getId(), new HttpUtil.HttpConnectCallback() {
                                        @Override
                                        public void onSuccess(Response response) {
                                            ResponseBody body = response.body();
                                            if (body != null) {
                                                RequestResult requestresult = JsonUtil.toObject(body.charStream(), RequestResult.class);
                                                if (requestresult.getState() == 174) {
                                                    Log.d(TAG, "onSuccess: " + requestresult.getStateInfo());
                                                    toChatActivity(mChatRoom);
                                                }
                                            }
                                        }

                                        @Override
                                        public void onFailure(IOException e) {

                                        }

                                        @Override
                                        public void onFailure() {

                                        }
                                    }, false);
                                }
                            }
                        }

                        @Override
                        public void onFailure(IOException e) {

                        }

                        @Override
                        public void onFailure() {

                        }
                    }, false);
                } else {
                    // 此处应该创建圈子
                    HttpUtil.DoGet(Constant.GroupUrl.GROUP_CREATE + mPath.get(currentItem).getId(),
                            new HttpUtil.HttpConnectCallback() {
                        @Override
                        public void onSuccess(Response response) {
                            Log.d(TAG, "onSuccess: GROUP_CREATE");
                            ResponseBody body = response.body();
                            if (body != null) {
                                RequestResult requestResult = JsonUtil.toObject(body.charStream(), RequestResult.class);
                                if (requestResult.getState() == 172) {
                                    int groupId = JsonUtil.toObject(requestResult.getData().toString(), Integer.class);
                                    intoNewGroup(groupId);
                                }
                            }
                        }
                        private void intoNewGroup(int id){
                            HttpUtil.DoGet(Constant.GroupUrl.GROUP_INFOMATION + id, new HttpUtil.HttpConnectCallback() {
                                @Override
                                public void onSuccess(Response response) {
                                    if(response != null){
                                        RequestResult<ChatRoom> roomRequestResult = null;
                                        Gson gson = new Gson();
                                        try{
                                            roomRequestResult = gson.fromJson(response.body().charStream() , new TypeToken<RequestResult<ChatRoom>>(){}.getType());
                                        }catch (Exception e){
                                            e.printStackTrace();
                                        }
                                        if(roomRequestResult != null && roomRequestResult.getData()!= null){
                                            ChatRoom chatRoom = roomRequestResult.getData();
                                            toChatActivity(chatRoom);
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(IOException e) {

                                }

                                @Override
                                public void onFailure() {

                                }
                            } , false);
                        }

                        @Override
                        public void onFailure(IOException e) {

                        }

                        @Override
                        public void onFailure() {

                        }
                    }, false);
                }
                break;
        }
    }
}
