package com.qg.route.animition;


import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * Created by Ricco on 2017/4/20.
 */

public class ZoomOutPageTransformer implements ViewPager.PageTransformer {

    // 默认缩放
    private static final float DEFAULT_MIN_SCALE = 0.85f;
    private float mMinScale = DEFAULT_MIN_SCALE;

    public ZoomOutPageTransformer()
    {
        this(DEFAULT_MIN_SCALE);
    }

    public ZoomOutPageTransformer(float minScale)
    {
        mMinScale = minScale;
    }

    @Override
    public void transformPage(View page, float position) {
        int pageWidth = page.getWidth();
        int pageHeight = page.getHeight();

        page.setPivotY(pageHeight / 2);
        page.setPivotX(pageWidth / 2);
        if (position < -1)
        { // [-Infinity,-1)
            // This page is way off-screen to the left.
            page.setScaleX(mMinScale);
            page.setScaleY(mMinScale);
            page.setPivotX(pageWidth);
        } else if (position <= 1)
        { // [-1,1]
            // Modify the default slide transition to shrink the page as well
            if (position < 0) //1-2:1[0,-1] ;2-1:1[-1,0]
            {

                float scaleFactor = (1 + position) * (1 - mMinScale) + mMinScale;
                page.setScaleX(scaleFactor);
                page.setScaleY(scaleFactor);

                page.setPivotX(pageWidth * (0.5f + (0.5f * -position)));

            } else //1-2:2[1,0] ;2-1:2[0,1]
            {
                float scaleFactor = (1 - position) * (1 - mMinScale) + mMinScale;
                page.setScaleX(scaleFactor);
                page.setScaleY(scaleFactor);
                page.setPivotX(pageWidth * ((1 - position) * 0.5f));
            }


        } else
        { // (1,+Infinity]
            page.setPivotX(0);
            page.setScaleX(mMinScale);
            page.setScaleY(mMinScale);
        }
    }
}
