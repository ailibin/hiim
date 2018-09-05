package com.aiitec.hiim.utils;


import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;


/**
 * 软键盘弹出与收起监听类
 *
 * @author Anthony
 */
public class SoftKeyboardStateHelper implements OnGlobalLayoutListener {

    private final List<SoftKeyboardStateListener> listeners = new LinkedList<SoftKeyboardStateListener>();
    private final View activityRootView;
    private int lastSoftKeyboardHeightInPx;
    private boolean isSoftKeyboardOpened;
    private Activity mActivity;

    public SoftKeyboardStateHelper(View activityRootView, Activity activity) {
        this(activityRootView, false, activity);
    }

    public SoftKeyboardStateHelper(View activityRootView,
                                   boolean isSoftKeyboardOpened, Activity activity) {
        this.mActivity = activity;
        this.activityRootView = activityRootView;
        this.isSoftKeyboardOpened = isSoftKeyboardOpened;
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    @Override
    public void onGlobalLayout() {
        final Rect r = new Rect();
        activityRootView.getWindowVisibleDisplayFrame(r);
        //因为添加了沉浸式状态栏,所以要减去r.top状态栏高度(没有添加沉浸式状态栏就不用减去r.top的高度)
        int heightDiff = activityRootView.getRootView().getHeight()
                - (r.bottom - r.top);

        int realKeyboardHeight = 0;
        //获取真实的软键盘高度,解决华为手机等手机的虚拟键盘高度的问题
        if (Build.VERSION.SDK_INT >= 20) {
            //判断手机是否有虚拟按键
            boolean HasNavigationBar = checkDeviceHasNavigationBar(mActivity);
            if (HasNavigationBar) {
                realKeyboardHeight = heightDiff - getSoftButtonsBarHeight() - StatusBarUtil.getStatusBarHeight(activityRootView.getContext());
            } else {
                realKeyboardHeight = heightDiff - StatusBarUtil.getStatusBarHeight(activityRootView.getContext());
            }
        } else {
            realKeyboardHeight = heightDiff - StatusBarUtil.getStatusBarHeight(activityRootView.getContext());
        }

        if (!isSoftKeyboardOpened && isKeyboardShown(activityRootView)) {
            isSoftKeyboardOpened = true;
            notifyOnSoftKeyboardOpened(realKeyboardHeight);
        } else if (isSoftKeyboardOpened && !isKeyboardShown(activityRootView)) {
            isSoftKeyboardOpened = false;
            notifyOnSoftKeyboardClosed(realKeyboardHeight);
        }
    }


    /**
     * 获取是否存在NavigationBar，是否有虚拟按钮
     */
    public static boolean checkDeviceHasNavigationBar(Context context) {
        boolean hasNavigationBar = false;
        Resources rs = context.getResources();
        int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
        if (id > 0) {
            hasNavigationBar = rs.getBoolean(id);
        }
        try {
            Class systemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method m = systemPropertiesClass.getMethod("get", String.class);
            String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
            if ("1".equals(navBarOverride)) {
                hasNavigationBar = false;
            } else if ("0".equals(navBarOverride)) {
                hasNavigationBar = true;
            }
        } catch (Exception e) {

        }
        return hasNavigationBar;
    }


    /**
     * 因为各种手机的软键盘高度是不同的,所以进行屏幕适配
     *
     * @param rootView
     * @return
     */
    private boolean isKeyboardShown(View rootView) {
        final int softKeyboardHeight = 100;
        Rect r = new Rect();
        rootView.getWindowVisibleDisplayFrame(r);
        DisplayMetrics dm = rootView.getResources().getDisplayMetrics();
        int heightDiff = rootView.getBottom() - r.bottom;
        return heightDiff > softKeyboardHeight * dm.density;
    }


    /**
     * 底部虚拟按键栏的高度
     *
     * @return
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private int getSoftButtonsBarHeight() {
        DisplayMetrics metrics = new DisplayMetrics();
        //这个方法获取可能不是真实屏幕的高度
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int usableHeight = metrics.heightPixels;
        //获取当前屏幕的真实高度
        mActivity.getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
        int realHeight = metrics.heightPixels;
        if (realHeight > usableHeight) {
            return realHeight - usableHeight;
        } else {
            return 0;
        }
    }


    public void setIsSoftKeyboardOpened(boolean isSoftKeyboardOpened) {
        this.isSoftKeyboardOpened = isSoftKeyboardOpened;
    }

    public boolean isSoftKeyboardOpened() {
        return isSoftKeyboardOpened;
    }

    /**
     * * Default value is zero (0) * @return last saved keyboard height in px
     */
    public int getLastSoftKeyboardHeightInPx() {
        return lastSoftKeyboardHeightInPx;
    }

    public void addSoftKeyboardStateListener(SoftKeyboardStateListener listener) {
        listeners.add(listener);
    }

    public void removeSoftKeyboardStateListener(
            SoftKeyboardStateListener listener) {
        listeners.remove(listener);
    }

    private void notifyOnSoftKeyboardOpened(int keyboardHeightInPx) {
        this.lastSoftKeyboardHeightInPx = keyboardHeightInPx;
        for (SoftKeyboardStateListener listener : listeners) {
            if (listener != null) {
                listener.onSoftKeyboardOpened(keyboardHeightInPx);
            }
        }
    }

    private void notifyOnSoftKeyboardClosed(int keyboardHeightInPx) {
        for (SoftKeyboardStateListener listener : listeners) {
            if (listener != null) {
                listener.onSoftKeyboardClosed(keyboardHeightInPx);
            }
        }
    }

    /***
     * 键盘弹出收起监听回调接口
     *
     * @author Anthony
     *
     */
    public interface SoftKeyboardStateListener {
        /**
         * 键盘弹起
         *
         * @param keyboardHeightInPx 键盘高度
         */
        void onSoftKeyboardOpened(int keyboardHeightInPx);

        /**
         * 键盘收起
         */
        void onSoftKeyboardClosed(int keyboardHeightInPx);
    }


}
