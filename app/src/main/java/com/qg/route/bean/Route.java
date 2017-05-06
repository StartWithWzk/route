package com.qg.route.bean;

import java.util.List;

/**
 * Created by Ricco on 2017/5/2.
 */

public class Route {

    private int id;         //唯一标识符

    // 起点和终点
    private XYPoint mStartPoint;
    private XYPoint mEndPoint;
    // 起点和终点的名字
    private String mStartName;
    private String mEndName;
    // 路线的类型
    private int selectedRouteType;
    // 路线的点集合，包含起点与终点
    private List<XYPoint> mPath;

    public Route(XYPoint mStartPoint, XYPoint mEndPoint, String mStartName, String mEndName, int selectedRouteType, List<XYPoint> mPath) {
        this.mStartPoint = mStartPoint;
        this.mEndPoint = mEndPoint;
        this.mStartName = mStartName;
        this.mEndName = mEndName;
        this.selectedRouteType = selectedRouteType;
        this.mPath = mPath;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public XYPoint getStartPoint() {
        return mStartPoint;
    }

    public void setStartPoint(XYPoint startPoint) {
        mStartPoint = startPoint;
    }

    public XYPoint getEndPoint() {
        return mEndPoint;
    }

    public void setEndPoint(XYPoint endPoint) {
        mEndPoint = endPoint;
    }

    public String getStartName() {
        return mStartName;
    }

    public void setStartName(String startName) {
        mStartName = startName;
    }

    public String getEndName() {
        return mEndName;
    }

    public void setEndName(String endName) {
        mEndName = endName;
    }

    public int getSelectedRouteType() {
        return selectedRouteType;
    }

    public void setSelectedRouteType(int selectedRouteType) {
        this.selectedRouteType = selectedRouteType;
    }

    public List<XYPoint> getPath() {
        return mPath;
    }

    public void setPath(List<XYPoint> path) {
        mPath = path;
    }
}
