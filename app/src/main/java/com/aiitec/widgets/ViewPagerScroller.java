package com.aiitec.widgets;

import android.content.Context;
import android.view.animation.Interpolator;
import android.widget.Scroller;

/**
 * @author ailibin
 */
public class ViewPagerScroller extends Scroller {
    /**
     * 自定义滑动速度
     */
    private int mScrollDuration = 0;

    public void setmScrollDuration(int mScrollDuration) {
        this.mScrollDuration = mScrollDuration;
    }

    public ViewPagerScroller(Context context) {
        super(context);
    }

    public ViewPagerScroller(Context context, Interpolator interpolator) {
        super(context, interpolator);
    }

    public ViewPagerScroller(Context context, Interpolator interpolator, boolean flywheel) {
        super(context, interpolator, flywheel);
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy) {
        super.startScroll(startX, startY, dx, dy, mScrollDuration);
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy, int duration) {
        super.startScroll(startX, startY, dx, dy, mScrollDuration);
    }
}
