package com.aiitec.hiim.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.ColorRes;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.aiitec.hiim.R;
import com.aiitec.hiim.ui.MainActivity;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by 赵晨璞
 * edit by Anthony
 * 作者：简名
 * 链接：http://www.jianshu.com/p/7f5a9969be53
 * 來源：简书
 * 著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。
 */
public class StatusBarUtil {

    /**
     * 修改状态栏为全透明
     *
     * @param activity
     */
    @TargetApi(19)
    public static void transparencyBar(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = activity.getWindow();
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }


    /**
     * 状态栏亮色模式，设置状态栏黑色文字、图标，
     * 适配4.4以上版本MIUIV、Flyme和6.0以上版本其他Android
     *
     * @param activity
     * @return 1:MIUUI 2:Flyme 3:android6.0
     */
    public static int StatusBarLightMode(Activity activity) {
        int result = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (MIUISetStatusBarLightMode(activity, true)) {
                result = 1;
            } else if (FlymeSetStatusBarLightMode(activity.getWindow(), true)) {
                result = 2;
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                result = 3;
            }
        }
        return result;
    }

    /**
     * 已知系统类型时，设置状态栏黑色文字、图标。
     * 适配4.4以上版本MIUIV、Flyme和6.0以上版本其他Android
     *
     * @param activity
     * @param type     1:MIUUI 2:Flyme 3:android6.0
     */
    public static void StatusBarLightMode(Activity activity, int type) {
        if (type == 1) {
            MIUISetStatusBarLightMode(activity, true);
        } else if (type == 2) {
            FlymeSetStatusBarLightMode(activity.getWindow(), true);
        } else if (type == 3) {
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

    }

    /**
     * 状态栏暗色模式，清除MIUI、flyme或6.0以上版本状态栏黑色文字、图标
     */
    public static void StatusBarDarkMode(Activity activity, int type) {
        if (type == 1) {
            MIUISetStatusBarLightMode(activity, false);
        } else if (type == 2) {
            FlymeSetStatusBarLightMode(activity.getWindow(), false);
        } else if (type == 3) {
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        }

    }

    /**
     * 状态栏暗色模式，清除MIUI、flyme或6.0以上版本状态栏黑色文字、图标
     */
    public static void StatusBarDarkMode(Activity activity) {
        int mode = StatusBarUtil.StatusBarLightMode(activity);
        if (mode == 1) {
            MIUISetStatusBarLightMode(activity, false);
        } else if (mode == 2) {
            FlymeSetStatusBarLightMode(activity.getWindow(), false);
        } else if (mode == 3) {
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        }

    }

    /**
     * 已知系统类型时，设置状态栏黑色文字、图标。
     * 适配4.4以上版本MIUIV、Flyme和6.0以上版本其他Android
     *
     * @param activity
     * @param
     */
    public static void StatusBarLightModeNew(Activity activity) {
        int mode = StatusBarUtil.StatusBarLightMode(activity);
        if (mode == 1) {
            MIUISetStatusBarLightMode(activity, true);
        } else if (mode == 2) {
            FlymeSetStatusBarLightMode(activity.getWindow(), true);
        } else if (mode == 3) {
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }


    /**
     * 设置状态栏图标为深色和魅族特定的文字风格
     * 可以用来判断是否为Flyme用户
     *
     * @param window 需要设置的窗口
     * @param dark   是否把状态栏文字及图标颜色设置为深色
     * @return boolean 成功执行返回true
     */
    public static boolean FlymeSetStatusBarLightMode(Window window, boolean dark) {
        boolean result = false;
        if (window != null) {
            try {
                WindowManager.LayoutParams lp = window.getAttributes();
                Field darkFlag = WindowManager.LayoutParams.class
                        .getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
                Field meizuFlags = WindowManager.LayoutParams.class
                        .getDeclaredField("meizuFlags");
                darkFlag.setAccessible(true);
                meizuFlags.setAccessible(true);
                int bit = darkFlag.getInt(null);
                int value = meizuFlags.getInt(lp);
                if (dark) {
                    value |= bit;
                } else {
                    value &= ~bit;
                }
                meizuFlags.setInt(lp, value);
                window.setAttributes(lp);
                result = true;
            } catch (Exception e) {

            }
        }
        return result;
    }

    /**
     * 需要MIUIV6以上
     *
     * @param activity
     * @param dark     是否把状态栏文字及图标颜色设置为深色
     * @return boolean 成功执行返回true
     */
    public static boolean MIUISetStatusBarLightMode(Activity activity, boolean dark) {
        boolean result = false;
        Window window = activity.getWindow();
        if (window != null) {
            Class clazz = window.getClass();
            try {
                int darkModeFlag = 0;
                Class layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
                Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
                darkModeFlag = field.getInt(layoutParams);
                Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
                if (dark) {
                    //状态栏透明且黑色字体
                    extraFlagField.invoke(window, darkModeFlag, darkModeFlag);
                } else {
                    //清除黑色字体
                    extraFlagField.invoke(window, 0, darkModeFlag);
                }
                result = true;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    //开发版 7.7.13 及以后版本采用了系统API，旧方法无效但不会报错，所以两个方式都要加上
                    if (dark) {
                        activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                    } else {
                        activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                    }
                }
            } catch (Exception e) {

            }
        }
        return result;
    }

    /**
     * 为透明状态栏的Activity下的Fragment增加仿StatusBar的view
     * 因为Activity下面可能只有一个Fragment是透明的，而我们设置透明就是整个Activity了，
     * 其他Fragment会收到影响，所以在收到影响的Fragment增加StatusBar
     *
     * @param view
     */
    public static void addStatusBarView(View view) {
        addStatusBarView(view, R.color.colorPrimary);
    }

    /**
     * 在View 上添加一个View代替状态栏
     *
     * @param view     父控件
     * @param colorRes 颜色资源id
     */
    public static View addStatusBarView(View view, @ColorRes int colorRes) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return null;
        }
        final View statusBarView = new View(view.getContext());
//        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getStatusBarHeight(view.getContext()));
        ViewGroup.LayoutParams lp;
        if (ScreenUtils.isAdaptScreen()) {
            //做了屏幕适配
            lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getStatusBarHeight());
        } else {
            //没有做屏幕适配
            lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getStatusBarHeight(view.getContext()));
        }
        statusBarView.setBackgroundResource(colorRes);
        statusBarView.setLayoutParams(lp);
        ((ViewGroup) view).addView(statusBarView, 0);
        return statusBarView;
    }

    /**
     * 设置白色状态栏,针对MIUI和Flyme系统优化
     *
     * @param activity
     */
    public static void setWhiteStatusBar(Activity activity) {
        int mode = StatusBarUtil.StatusBarLightMode(activity);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (mode == 1 || mode == 2 || mode == 3) {
                Window window = activity.getWindow();
                StatusBarLightMode(activity, mode);
                window.setStatusBarColor(Color.WHITE);   //这里动态修改颜色
            }
        }
        setStatusBarColor(activity, android.R.color.white);
    }

    /**
     * 设置状态栏颜色，其实是添加View 占据状态栏的位置，并且设置成对应颜色
     *
     * @param activity 活动类
     * @param colorRes 颜色资源id
     */
    public static void setStatusBarColor(Activity activity, @ColorRes int colorRes) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (activity.getClass() != MainActivity.class/* && activity.getClass() != VideoDetailsActivity.class &&
                    activity.getClass() != LiveBroadcastActivity.class && activity.getClass() != LoginActivity.class
                    && activity.getClass() != WelcomeActivity.class  && activity.getClass() != GuideActivity.class
                    && activity.getClass() != MineActivity.class && activity.getClass() != SearchActivity.class
                   */) {
                View rootView = ((ViewGroup) activity.findViewById(android.R.id.content)).getChildAt(0);
                if (rootView instanceof LinearLayout) {
                    addStatusBarView(rootView, colorRes);
                }
            }
        }
    }

    /**
     * 获取状态栏高度，没有做屏幕适配的,默认使用系统的缩放
     *
     * @param context
     * @return
     */
    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * 获取状态栏高度(屏幕适配会导致状态栏的高度发生变化)，这里使用系统的
     *
     * @return
     */
    public static int getStatusBarHeight() {
        int result = 0;
        int resourceId = Resources.getSystem().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = Resources.getSystem().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}

