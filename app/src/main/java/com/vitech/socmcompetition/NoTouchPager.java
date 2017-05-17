package com.vitech.socmcompetition;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.DecelerateInterpolator;
import android.widget.Scroller;

import java.lang.reflect.Field;

/**
 * Created by varma on 16-05-2017.
 */

public class NoTouchPager extends ViewPager {
    public NoTouchPager(Context context) {
        super(context);
        setMyScroller();
    }

    public NoTouchPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        setMyScroller();
    }



    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
               return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        return false;
    }
    private void setMyScroller() {
        try {
            Class<?> viewpager = ViewPager.class;
            Field scroller = viewpager.getDeclaredField("mScroller");
            scroller.setAccessible(true);
            scroller.set(this, new MyScroller(getContext()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class MyScroller extends Scroller {
        public MyScroller(Context context) {
            super(context, new DecelerateInterpolator());
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy, int duration) {
            super.startScroll(startX, startY, dx, dy, 350);
        }
    }
}
