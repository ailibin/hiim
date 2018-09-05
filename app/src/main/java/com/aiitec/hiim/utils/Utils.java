package com.aiitec.hiim.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Anthony
 * @version 1.0
 * createTime 2017/11/2.
 */

public class Utils {

    @SuppressLint("StaticFieldLeak")
    private static Application sApplication;

    private static final ActivityLifecycleImpl ACTIVITY_LIFECYCLE = new ActivityLifecycleImpl();

    /**
     * 返回中文和英文字符的统一长度，中文字符占2个字节长度，英文占1个
     *
     * @param str
     * @return
     */
    public static int getUnicodeLength(String str) {
        if (TextUtils.isEmpty(str)) {
            return 0;
        }
        try {
            return (new String(str.getBytes("gb2312"), "iso-8859-1").length());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return str.length();
    }

    /**
     * 高亮显示
     *
     * @param all    全部文字
     * @param target 需要加高亮文字内容 "[0-9|.]" 正则，匹配数字
     * @param color  高亮颜色
     * @return
     */
    public static SpannableStringBuilder highlightText(String all,
                                                       String target, int color) {
        SpannableStringBuilder spannable = new SpannableStringBuilder(all);
        CharacterStyle span = null;
        Pattern p = Pattern.compile(target);
        Matcher m = p.matcher(all);
        while (m.find()) {
            span = new ForegroundColorSpan(color);// 需要重复！
            spannable.setSpan(span, m.start(), m.end(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spannable;
    }

    public static SpannableStringBuilder getDifferenceSixeText(String all,
                                                               String target, int spSize) {
        SpannableStringBuilder spannable = new SpannableStringBuilder(all);
        Pattern p = Pattern.compile(target);
        Matcher m = p.matcher(all);
        while (m.find()) {
            AbsoluteSizeSpan span = new AbsoluteSizeSpan(spSize, true);// 需要重复！
            spannable.setSpan(span, m.start(), m.end(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spannable;
    }

    public static SpannableString getSizeSpanUseSp(String str, int start, int end, int spSize) {
        SpannableString ss = new SpannableString(str);
        ss.setSpan(new AbsoluteSizeSpan(spSize, true), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return ss;
    }

    /**
     * 显示虚拟键盘
     */
    public static void showKeyboard(View v, Context context) {
        v.requestFocus();
        InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(v, InputMethodManager.SHOW_FORCED);
    }

    /**
     * 隐藏软键盘
     */
    public static void hideKeyboard(Activity context) {
        try {
            if (context != null && context.getCurrentFocus() != null) {
                InputMethodManager imm = (InputMethodManager) context
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm.isActive()) {
                    imm.hideSoftInputFromWindow(context.getCurrentFocus()
                            .getApplicationWindowToken(), 0);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private final static Pattern ATTR_PATTERN = Pattern.compile("<img[^<>]*?\\ssrc=['\"]?(.*?)['\"]?\\s.*?>", Pattern.CASE_INSENSITIVE);

    //替换body里面的图片路径问题
    public static String getAbsSource(String source, String bigpath) {
        try {
            Matcher matcher = ATTR_PATTERN.matcher(source);
            List<String> list = new ArrayList<String>();  // 装载了匹配整个的Tag
            List<String> list2 = new ArrayList<String>(); // 装载了src属性的内容
            while (matcher.find()) {
                list.add(matcher.group(0));
                list2.add(matcher.group(1));
            }
            StringBuilder sb = new StringBuilder();
            String data0 = source.split("<img")[0];
            sb.append(data0); // 连接<img之前的内容

            for (int i = 0; i < list.size(); i++) { // 遍历list
                String imagePath = list2.get(i).substring(1);

                String relace1 = list.get(i).replace("img", "img style=\"width:100%\" ");
                String relaceAfter = "";
                if (!imagePath.startsWith("http")) {
                    relaceAfter = relace1.replace(list2.get(i), // 对每一个Tag进行替换
                            bigpath + imagePath);
                } else {
                    relaceAfter = relace1;
                }

                sb.append(relaceAfter);
            }
            sb.append(source.split("(?:<img[^<>]*?\\s.*?['\"]?\\s.*?>)+")[1]);
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * make true current connect service is wifi
     *
     * @param mContext
     * @return
     */
    public static boolean isWifi(Context mContext) {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return true;
        }
        return false;
    }

    /**
     * Return the context of Application object.
     *
     * @return the context of Application object
     */
    public static Application getApp() {
        if (sApplication != null) return sApplication;
        Application app = getApplicationByReflect();
        init(app);
        return app;
    }

    /**
     * Init utils.
     * <p>Init it in the class of Application.</p>
     *
     * @param app application
     */
    public static void init(final Application app) {
        if (sApplication == null) {
            if (app == null) {
                Utils.sApplication = getApplicationByReflect();
            } else {
                Utils.sApplication = app;
            }
            Utils.sApplication.registerActivityLifecycleCallbacks(ACTIVITY_LIFECYCLE);
        }
    }

    private static Application getApplicationByReflect() {
        try {
            @SuppressLint("PrivateApi")
            Class<?> activityThread = Class.forName("android.app.ActivityThread");
            Object thread = activityThread.getMethod("currentActivityThread").invoke(null);
            Object app = activityThread.getMethod("getApplication").invoke(thread);
            if (app == null) {
                throw new NullPointerException("u should init app first");
            }
            return (Application) app;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        throw new NullPointerException("u should init first");
    }

    static final AdaptScreenArgs ADAPT_SCREEN_ARGS = new AdaptScreenArgs();

    static class AdaptScreenArgs {
        int     sizeInPx;
        boolean isVerticalSlide;
    }

    static void restoreAdaptScreen() {
        final DisplayMetrics systemDm = Resources.getSystem().getDisplayMetrics();
        final DisplayMetrics appDm = Utils.getApp().getResources().getDisplayMetrics();
        final Activity activity = ACTIVITY_LIFECYCLE.getTopActivity();
        if (activity != null) {
            final DisplayMetrics activityDm = activity.getResources().getDisplayMetrics();
            if (ADAPT_SCREEN_ARGS.isVerticalSlide) {
                activityDm.density = activityDm.widthPixels / (float) ADAPT_SCREEN_ARGS.sizeInPx;
            } else {
                activityDm.density = activityDm.heightPixels / (float) ADAPT_SCREEN_ARGS.sizeInPx;
            }
            activityDm.scaledDensity = activityDm.density * (systemDm.scaledDensity / systemDm.density);
            activityDm.densityDpi = (int) (160 * activityDm.density);

            appDm.density = activityDm.density;
            appDm.scaledDensity = activityDm.scaledDensity;
            appDm.densityDpi = activityDm.densityDpi;
        } else {
            if (ADAPT_SCREEN_ARGS.isVerticalSlide) {
                appDm.density = appDm.widthPixels / (float) ADAPT_SCREEN_ARGS.sizeInPx;
            } else {
                appDm.density = appDm.heightPixels / (float) ADAPT_SCREEN_ARGS.sizeInPx;
            }
            appDm.scaledDensity = appDm.density * (systemDm.scaledDensity / systemDm.density);
            appDm.densityDpi = (int) (160 * appDm.density);
        }
    }

    static void cancelAdaptScreen() {
        final DisplayMetrics systemDm = Resources.getSystem().getDisplayMetrics();
        final DisplayMetrics appDm = Utils.getApp().getResources().getDisplayMetrics();
        final Activity activity = ACTIVITY_LIFECYCLE.getTopActivity();
        if (activity != null) {
            final DisplayMetrics activityDm = activity.getResources().getDisplayMetrics();
            activityDm.density = systemDm.density;
            activityDm.scaledDensity = systemDm.scaledDensity;
            activityDm.densityDpi = systemDm.densityDpi;
        }
        appDm.density = systemDm.density;
        appDm.scaledDensity = systemDm.scaledDensity;
        appDm.densityDpi = systemDm.densityDpi;
    }

    /**
     * 是否适配屏幕
     * @author ailibin
     * @return
     */
    static boolean isAdaptScreen() {
        final DisplayMetrics systemDm = Resources.getSystem().getDisplayMetrics();
        final DisplayMetrics appDm = Utils.getApp().getResources().getDisplayMetrics();
        return systemDm.density != appDm.density;
    }

    static class ActivityLifecycleImpl implements Application.ActivityLifecycleCallbacks {

        final LinkedList<Activity> mActivityList = new LinkedList<>();
        final HashMap<Object, OnAppStatusChangedListener> mStatusListenerMap = new HashMap<>();

        private int mForegroundCount = 0;
        private int mConfigCount = 0;

        void addListener(final Object object, final OnAppStatusChangedListener listener) {
            mStatusListenerMap.put(object, listener);
        }

        void removeListener(final Object object) {
            mStatusListenerMap.remove(object);
        }

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            setTopActivity(activity);
        }

        @Override
        public void onActivityStarted(Activity activity) {
            setTopActivity(activity);
            if (mForegroundCount <= 0) {
                postStatus(true);
            }
            if (mConfigCount < 0) {
                ++mConfigCount;
            } else {
                ++mForegroundCount;
            }
        }

        @Override
        public void onActivityResumed(Activity activity) {
            setTopActivity(activity);
        }

        @Override
        public void onActivityPaused(Activity activity) {/**/}

        @Override
        public void onActivityStopped(Activity activity) {
            if (activity.isChangingConfigurations()) {
                --mConfigCount;
            } else {
                --mForegroundCount;
                if (mForegroundCount <= 0) {
                    postStatus(false);
                }
            }
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {/**/}

        @Override
        public void onActivityDestroyed(Activity activity) {
            mActivityList.remove(activity);
        }

        private void postStatus(final boolean isForeground) {
            if (mStatusListenerMap.isEmpty()) return;
            for (OnAppStatusChangedListener onAppStatusChangedListener : mStatusListenerMap.values()) {
                if (onAppStatusChangedListener == null) return;
                if (isForeground) {
                    onAppStatusChangedListener.onForeground();
                } else {
                    onAppStatusChangedListener.onBackground();
                }
            }
        }

        private void setTopActivity(final Activity activity) {
//            if (activity.getClass() == PermissionUtils.PermissionActivity.class) return;
            if (mActivityList.contains(activity)) {
                if (!mActivityList.getLast().equals(activity)) {
                    mActivityList.remove(activity);
                    mActivityList.addLast(activity);
                }
            } else {
                mActivityList.addLast(activity);
            }
        }

        Activity getTopActivity() {
            if (!mActivityList.isEmpty()) {
                final Activity topActivity = mActivityList.getLast();
                if (topActivity != null) {
                    return topActivity;
                }
            }
            Activity topActivityByReflect = getTopActivityByReflect();
            if (topActivityByReflect != null) {
                setTopActivity(topActivityByReflect);
            }
            return topActivityByReflect;
        }

        private Activity getTopActivityByReflect() {
            try {
                @SuppressLint("PrivateApi")
                Class<?> activityThreadClass = Class.forName("android.app.ActivityThread");
                Object activityThread = activityThreadClass.getMethod("currentActivityThread").invoke(null);
                Field activitiesField = activityThreadClass.getDeclaredField("mActivityList");
                activitiesField.setAccessible(true);
                Map activities = (Map) activitiesField.get(activityThread);
                if (activities == null) return null;
                for (Object activityRecord : activities.values()) {
                    Class activityRecordClass = activityRecord.getClass();
                    Field pausedField = activityRecordClass.getDeclaredField("paused");
                    pausedField.setAccessible(true);
                    if (!pausedField.getBoolean(activityRecord)) {
                        Field activityField = activityRecordClass.getDeclaredField("activity");
                        activityField.setAccessible(true);
                        return (Activity) activityField.get(activityRecord);
                    }
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public interface OnAppStatusChangedListener {
        void onForeground();

        void onBackground();
    }
}
