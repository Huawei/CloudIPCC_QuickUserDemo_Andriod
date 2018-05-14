package com.huawei.esdk;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created on 2018/1/30.
 */

public class NoScrollViewPager extends ViewPager
{
    public NoScrollViewPager(Context context)
    {
        super(context);
    }

    public NoScrollViewPager(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev)
    {
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev)
    {
        return false;
    }

    @Override
    public void setCurrentItem(int item, boolean smoothScroll)
    {
        super.setCurrentItem(item, smoothScroll);
    }

    @Override
    public void setCurrentItem(int item)
    {
        super.setCurrentItem(item, false);
    }
    
}
