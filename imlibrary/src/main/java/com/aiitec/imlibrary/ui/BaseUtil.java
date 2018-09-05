package com.aiitec.imlibrary.ui;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.aiitec.imlibrary.R;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 常用工具
 *
 * @author afb
 * @date 2017/9/18
 */

public class BaseUtil {

    private static Context mContext;

    public static void init(Context context) {
        mContext = context;
        if (context instanceof Activity) {
            mContext = context.getApplicationContext();
        }

    }

    private static Handler mHandler = new Handler(Looper.getMainLooper());

    /**
     * 判断当前线程是否是主线程
     */
    private static boolean isUIThread() {
        return Looper.getMainLooper() == Looper.myLooper();
    }

    private static void runOnUIThread(Runnable run) {
        if (isUIThread()) {
            run.run();
        } else {
            mHandler.post(run);
        }
    }


    /**
     * 隐藏输入法
     */
    public static void hidenInputMethod(View view, Context context) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getApplicationContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 隐藏软键盘
     */
    public static void hideKeyboard(Activity activity) {
        try {
            if (activity != null && activity.getCurrentFocus() != null) {
                InputMethodManager imm = (InputMethodManager) activity
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm.isActive()) {
                    imm.hideSoftInputFromWindow(activity.getCurrentFocus()
                            .getApplicationWindowToken(), 0);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * editText弹出软键盘
     */
    public static void openKeyboard(Activity activity, View view) {
        view.setFocusable(true);
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    /**
     * Long类型分钟值时间转换
     */
    public static String formartLongToTime(long time, String formart) {
        String _time = String.valueOf(time);
        if (!TextUtils.isEmpty(_time)) {
            SimpleDateFormat format = new SimpleDateFormat(formart);
            _time = format.format(new Date(Long.valueOf(_time) * 1000));
        }
        return _time;
    }

    /**
     * 把毫秒转化成日期
     *
     * @param dateFormat(日期格式yyyy年MM月dd日 HH:mm:ss)
     * @param millSec(毫秒数)
     * @return
     */
    public static String transferLongToDate(Long millSec, String dateFormat) {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        Date date = new Date(millSec);
        return sdf.format(date);
    }


    /**
     * 获取版本名和版本号
     *
     * @param context
     * @return 字符串数组0：版本名 1：版本号
     */
    public static String[] getVersion(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            String versionName = info.versionName;
            int versionCode = info.versionCode;
            return new String[]{versionName, versionCode + ""};
        } catch (Exception e) {
            e.printStackTrace();
            return new String[]{"", ""};
        }
    }

    /**
     * 获取手机屏幕的物理尺寸(多少英寸)
     */
    private double getScreenSizeOfDevice(Context context) {

        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        double x = Math.pow(width, 2);
        double y = Math.pow(height, 2);
        double diagonal = Math.sqrt(x + y);
        int dens = dm.densityDpi;
        double screenInches = diagonal / (double) dens;

        return screenInches;
    }

    /**
     * 获取屏幕的分辨率(真实的分辨率)
     *
     * @param activity
     */
    private Point getDisplayInfomation(Activity activity) {
        Point point = new Point();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            activity.getWindowManager().getDefaultDisplay().getRealSize(point);
        }
        return point;
    }

    private static Toast mToast;
    private static String lastMsg;
    private static long lastTime;

    /**
     * 可以在子线程中调用
     */
    public static void showToast(final String msg) {
        //这是用于当网络异常时,限制toast弹出的次数
        if (msg.equals(lastMsg) && System.currentTimeMillis() - lastTime <= 600) {
            return;
        }
        lastMsg = msg;
        lastTime = System.currentTimeMillis();
        runOnUIThread(new Runnable() {
            //            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void run() {

                if (mToast == null) {
                    mToast = new Toast(mContext);
                }
                View view = LayoutInflater.from(mContext).inflate(R.layout.ui_common_toast_layout, null, false);
                TextView tvToastContent = (TextView) view.findViewById(R.id.tv_toast_content);
                tvToastContent.setText(msg);
                mToast.setDuration(Toast.LENGTH_SHORT);
                mToast.setGravity(Gravity.CENTER, 0, 0);
                mToast.setView(view);
                mToast.show();
            }
        });
    }

    /**
     * 设置页面的透明度(兼容华为手机)
     * @param bgAlpha 1表示不透明
     */
    public static void setBackgroundAlpha(Activity activity, float bgAlpha) {
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        lp.alpha = bgAlpha;
        if (bgAlpha == 1) {
            //不移除该Flag的话,在有视频的页面上的视频会出现黑屏的bug
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        } else {
            //此行代码主要是解决在华为手机上半透明效果无效的bug
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        }
        activity.getWindow().setAttributes(lp);
    }
}
