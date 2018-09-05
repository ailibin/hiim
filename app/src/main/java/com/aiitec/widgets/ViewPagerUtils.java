package com.aiitec.widgets;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.animation.DecelerateInterpolator;

import java.lang.reflect.Field;


/**
 * @author ailibin
 * @time 2018/08/17
 */
public class ViewPagerUtils {

    /**
     * 利用反射机制，自定义viewpager的滑动速度
     *
     * @param context
     * @param viewPager    设置对象
     * @param scrollerTime 滑动时间
     */
    public static void smoothForViewPager(Context context, ViewPager viewPager, int scrollerTime) {
        try {
            //利用反射拿到viewpager的私用成员mScroller
            Field mScroller = ViewPager.class.getDeclaredField("mScroller");
            mScroller.setAccessible(true);
            ViewPagerScroller scroller = new ViewPagerScroller(context, new DecelerateInterpolator());
            scroller.setmScrollDuration(scrollerTime);
            mScroller.set(viewPager, scroller);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
