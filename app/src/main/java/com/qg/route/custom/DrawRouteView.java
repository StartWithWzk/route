package com.qg.route.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.View;

import com.qg.route.bean.Route;
import com.qg.route.bean.XYPoint;

import java.util.ArrayList;

/**
 * Created by Ricco on 2017/5/7.
 */

public class DrawRouteView extends View {

    private final ArrayList<XYPoint> mPath;
    private float density = getResources().getDisplayMetrics().density;
    private Paint mPaint;

    private double pointWidth;
    private double pointHeight;

    public DrawRouteView(Context context, Route route) {
        super(context);
        mPath = initDate(route);
        mPaint = new Paint();
        // 初始化画笔
        init();
        // 初始化数据集
    }

    private void init() {
        // 设置颜色
        mPaint.setColor(Color.parseColor("#537edc"));
        // 设置抗锯齿
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(8f);
        mPaint.setStyle(Paint.Style.STROKE);
    }

    private ArrayList<XYPoint> initDate(Route route) {
        ArrayList<XYPoint> temp = new ArrayList<>();

        if (route == null || route.getPath() == null)
            return temp;

        // 最大最小宽高
        double maxHeight, maxWidth, minHeight, minWidth;
        // 初始化
        maxHeight = minHeight = route.getPath().get(0).getY();
        maxWidth = minWidth = route.getPath().get(0).getX();
        // 遍历选出最小点，最大点
        for (XYPoint xyPoint : route.getPath()
             ) {
            double x = xyPoint.getX();
            double y = xyPoint.getY();
            if (x > maxWidth) {
                maxWidth = x;
            } else if (x < minWidth) {
                minWidth = x;
            }
            if (y > maxHeight) {
                maxHeight = y;
            } else if (y < minHeight) {
                minHeight = y;
            }
        } // end of for

        pointWidth = maxWidth - minWidth;
        pointHeight = maxHeight - minHeight;

        // 将数据集于Canvas坐标置于同一原点
        for (XYPoint xyPoint : route.getPath()
                ) {
            double x = xyPoint.getX();
            double y = xyPoint.getY();
            temp.add(new XYPoint(x - minWidth, y - minHeight));
        } // end of for

        return temp;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int canvasWidth = canvas.getWidth();
        int canvasHeight = canvas.getHeight();
        /**
         *      canvasHeight
         * cx = ------------ * px
         *       pointWidth
         */
        double scaleX = canvasWidth / pointWidth;
        double scaleY = canvasHeight / pointHeight;

        Path path = new Path();
        for (int i=0; i<mPath.size(); i++) {
            float x = (float) (mPath.get(i).getX() * scaleX);
            float y = (float) (mPath.get(i).getY() * scaleY);
            if (i == 0) {
                path.moveTo(x, y);
            } else {
                path.lineTo(x, y);
            }
        }
        // path准备就绪，真正将点绘制到Canvas上
        canvas.drawPath(path, mPaint);
    }
}
