package com.qg.route.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.qg.route.R;

/**
 * 用于选择路线、切换路线的控件
 * Created by Ricco on 2017/4/14.
 * @author zhikang_wen
 */

public class ChooseRoute extends FrameLayout implements View.OnClickListener {
    private View parentView;

    // 路线设置按钮
    private ImageButton walkBtn;
    private ImageButton rideBtn;
    private ImageButton busBtn;
    private ImageButton driveBtn;

    // 起点与终点的文本
    private TextView startText;
    private TextView endText;

    private OnRouteClickListener mListener;

    // 接口
    public interface OnRouteClickListener {
        void walk(View view);
        void ride(View view);
        void bus(View view);
        void drive(View view);
        void start(View view);
        void end(View view);
    }

    // 设置监听
    public void setOnRouteClickListener(OnRouteClickListener listener) {
        mListener = listener;
    }

    public ChooseRoute(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        parentView = inflater.inflate(R.layout.choose_route_main, this, true);

        // 初始化控件
        init();
        // 给自定义控件添加属性
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ChooseRoute);
        startText.setText(ta.getString(R.styleable.ChooseRoute_start_text));
        endText.setText(ta.getString(R.styleable.ChooseRoute_end_text));
        endText.setHint(ta.getString(R.styleable.ChooseRoute_end_hint_text));
    }

    private void init() {
        walkBtn = (ImageButton) findViewById(R.id.ib_walk);
        walkBtn.setOnClickListener(this);
        rideBtn = (ImageButton) findViewById(R.id.ib_ride);
        rideBtn.setOnClickListener(this);
        busBtn = (ImageButton) findViewById(R.id.ib_bus);
        busBtn.setOnClickListener(this);
        driveBtn = (ImageButton) findViewById(R.id.ib_drive);
        driveBtn.setOnClickListener(this);

        startText = (TextView) findViewById(R.id.tv_start);
        startText.setOnClickListener(this);
        endText = (TextView) findViewById(R.id.tv_end);
        endText.setOnClickListener(this);
    }

    // 设置起点
    public void setStartText(String text) {
        startText.setText(text);
    }

    // 设置终点
    public void setEndText(String text) {
        endText.setText(text);
    }

    public String getStartText() {
        return startText.getText().toString();
    }

    public String getEndText() {
        return endText.getText().toString();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_walk:
                initStatus();
                walkBtn.setImageResource(R.mipmap.selected_walk);
                mListener.walk(v);
                break;
            case R.id.ib_ride:
                initStatus();
                rideBtn.setImageResource(R.mipmap.selected_bike);
                mListener.ride(v);
                break;
            case R.id.ib_bus:
                initStatus();
                busBtn.setImageResource(R.mipmap.selected_bus);
                mListener.bus(v);
                break;
            case R.id.ib_drive:
                initStatus();
                driveBtn.setImageResource(R.mipmap.selected_car);
                mListener.drive(v);
                break;
            case R.id.tv_start:
                mListener.start(v);
                break;
            case R.id.tv_end:
                mListener.end(v);
                break;
        }
    }

    private void initStatus() {
        walkBtn.setImageResource(R.mipmap.unselected_walk);
        rideBtn.setImageResource(R.mipmap.unselected_bike);
        busBtn.setImageResource(R.mipmap.unselected_bus);
        driveBtn.setImageResource(R.mipmap.unselected_car);
    }
}