package com.aiitec.openapi.utils;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author shc
 * @version 1.0
 * @since 2015-05-05 爱特安为工具类
 * 包含：
 * SD卡相关
 * 网络相关
 * 屏幕与像素转换相关
 * 时间标准化相关
 * 字符串相关
 * 键盘相关
 * 应用相关
 * Bitmap相关
 * Sharepreferences相关
 * 其他
 */
public class AiiUtil {

    public static String PREFERENCE_NAME = "Aiitec";
    public static DisplayMetrics metric = new DisplayMetrics();

	/*=============SD卡相关===============*/

    /**
     * 判断SDCard是否可用
     *
     * @return
     */
    public static boolean isSDCardEnable() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);

    }

    /**
     * 获取SD卡路径
     *
     * @return
     */
    public static String getSDCardPath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath()
                + File.separator;
    }


    /**
     * 获取最大内存
     *
     * @return
     */
    public static long getMaxMemory() {

        return Runtime.getRuntime().maxMemory() / 1024;
    }

	
	
	/*=============网络相关===============*/

    /**
     * 检查网络
     *
     * @param context
     * @return
     */
    public static boolean isNetWorkConnect(Context context) {

        boolean netstate = false;
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {

            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {

                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {

                        netstate = true;
                        break;
                    }
                }
            }
        }
        return netstate;
    }

	/*=============屏幕与像素转换相关===============*/

    /**
     * 得到屏幕高度
     *
     * @param context
     * @return
     * @author shc DateTime 2014-11-20 下午3:04:44
     * @deprecated 已过时 建议使用 ScreenUtils 类
     */
    public static int getScreenWidth(Context context) {
        WindowManager manager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        manager.getDefaultDisplay().getMetrics(metric);
        return metric.widthPixels;
    }

    /**
     * 得到屏幕宽度
     *
     * @param context
     * @return
     * @author shc DateTime 2014-11-20 下午3:04:44
     * @deprecated 已过时 建议使用 ScreenUtils 类
     */
    public static int getScreenHeight(Context context) {
        WindowManager manager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        manager.getDefaultDisplay().getMetrics(metric);
        return metric.heightPixels;
    }

    /**
     * @param context
     * @param dpValue dp值
     * @return 像素值
     * @deprecated 已过时 建议使用 ScreenUtils 类
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * @param context
     * @param pxValue 像素值
     * @return dp值
     * @deprecated 已过时 建议使用 ScreenUtils 类
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {

        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }


    /**
     * 把图片转成字节数组
     *
     * @param image     图片对象
     * @param isRecycle image对象是否不再需要，不需要就销毁了
     * @return
     */
    public synchronized static byte[] bmpToByteArray(Bitmap image, boolean isRecycle) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        try {
            while (baos.toByteArray().length / 1024 > 200) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
                baos.reset();// 重置baos即清空baos
                image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
                options -= 10;// 每次都减少10
            }
        } catch (OutOfMemoryError e1) {
            e1.printStackTrace();
            baos.reset();
        }
        byte[] result = baos.toByteArray();
        if (isRecycle) {
            image.recycle();
            image = null;
        }
        try {
            baos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;// 把压缩后的数据baos存放到ByteArrayInputStream中
    }


    /**
     * @param timeStamp
     * @return
     * @deprecated 已过时， 建议用DateUtil 类
     * 时间戳转化为日期 yyyy-MM-dd HH:mm:ss 格式
     */
    public static Date timeStamp2Date(String timeStamp) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = null;
        try {
            date = format.parse(timeStamp);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * @param date 时间
     * @return
     * @deprecated 已过时， 建议用DateUtil 类
     * Date 转化为时间戳  yyyy-MM-dd HH:mm:ss  格式
     */
    public static String date2TimeStamp(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String timestamp = null;
        timestamp = format.format(date);
        return timestamp;
    }

	/*=============字符串相关===============*/

    /**
     * 补全两位小数
     *
     * @param money
     * @return
     */
    public static String formatDouble(double money) {
        if (money == 0)
            return "0.00";
        DecimalFormat df = new DecimalFormat("#0.00");
        return df.format(money);
    }

    /**
     * 去掉两位小数点(取整数)
     */
    public static String formatDoubleToInt(double money) {
        if (money == 0)
            return "0";
        DecimalFormat df = new DecimalFormat("#0");
        return df.format(money);
    }


    /**
     * 小数点后面两位，有0就去掉
     *
     * @param text
     * @return
     */
    public static String formatString(double text) {
        DecimalFormat df = new DecimalFormat("0.##");
        String str = df.format(text);
        return str;
    }

    /**
     * 小数点后面两位，有0就去掉
     *
     * @param text
     * @return
     */
    public static String formatString(Object text) {
        DecimalFormat df = new DecimalFormat("0.##");
        String str = df.format(text);
        return str;
    }


    /**
     * 验证字符串为空
     *
     * @param str 字符串
     * @return 是否为空
     */
    public static boolean isEmpty(String str) {
        if (null == str || "".equals(str))
            return true;
        else
            return null == str.trim() || "".equals(str.trim());
    }

    /**
     * 验证邮箱格式是否合法
     *
     * @param email 邮箱
     * @return 邮箱格式是否合法
     */
    public static boolean isEmail(String email) {

        Pattern pattern = Pattern
                .compile("^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$");
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();

    }


    /**
     * MD5加密
     *
     * @param source 加密内容
     * @return 加密后的内容（String类型）
     * @throws UnsupportedEncodingException
     */
    public static final String md5(String source) {
        String dest = null;
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] byteArray;
            try {
                byteArray = source.getBytes("UTF-8");
                byte[] md5Bytes = md5.digest(byteArray);
                StringBuffer hexValue = new StringBuffer();
                for (int i = 0; i < md5Bytes.length; i++) {
                    int val = (md5Bytes[i]) & 0xff;
                    if (val < 16)
                        hexValue.append("0");
                    hexValue.append(Integer.toHexString(val));
                }
                dest = hexValue.toString();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return dest;
    }

    /**
     * encoded in utf-8
     * <p>
     * <pre>
     * utf8Encode(null)        =   null
     * utf8Encode("")          =   "";
     * utf8Encode("aa")        =   "aa";
     * utf8Encode("哈哈")   = "%E5%95%8A%E5%95%8A%E5%95%8A%E5%95%8A";
     * </pre>
     *
     * @param str
     * @return
     */
    public static String utf8Encode(String str) {
        if (!isEmpty(str) && str.getBytes().length != str.length()) {
            try {
                return URLEncoder.encode(str, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException("UnsupportedEncodingException occurred. ", e);
            }
        }
        return str;
    }

    /**
     * encoded in utf-8, if exception, return defultReturn
     *
     * @param str
     * @param defultReturn
     * @return
     */
    public static String utf8Encode(String str, String defultReturn) {
        if (!isEmpty(str) && str.getBytes().length != str.length()) {
            try {
                return URLEncoder.encode(str, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                return defultReturn;
            }
        }
        return str;
    }
	
	
	/*=============键盘相关===============*/

    /**
     * 显示虚拟键盘
     *
     * @param context
     * @param v
     */
    public static void showKeyboard(Context context, View v) {
        v.requestFocus();
        InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(v, InputMethodManager.SHOW_FORCED);
    }

    /**
     * 隐藏软键盘
     *
     * @param context
     * @param v
     */
    public static void hideKeyboard(Context context, View v) {
        InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
        }
    }
	
	
	
	/*=============应用相关===============*/

    /**
     * 应用是否在后台运行
     *
     * @param context
     * @return A
     */
    public static boolean isApplicationInBackground(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> taskList = am.getRunningTasks(1);
        if (taskList != null && !taskList.isEmpty()) {
            ComponentName topActivity = taskList.get(0).topActivity;
            if (topActivity != null && !topActivity.getPackageName().equals(context.getPackageName())) {
                return true;
            }
        }
        return false;
    }


    /**
     * 获取应用版本号
     *
     * @param context
     * @return
     */
    public static int getAppVersionCode(Context context) {
        if (context != null) {
            PackageManager pm = context.getPackageManager();
            if (pm != null) {
                PackageInfo pi;
                try {
                    pi = pm.getPackageInfo(context.getPackageName(), 0);
                    if (pi != null) {
                        return pi.versionCode;
                    }
                } catch (NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        return -1;
    }
    
    /*=============bitmap相关===============*/

    /**
     * 缩放位图
     *
     * @param org       bitmap
     * @param newWidth  bitmap的宽
     * @param newHeight bitmap的高
     * @return
     */
    public static Bitmap scaleImageTo(Bitmap org, int newWidth, int newHeight) {
        return scaleImage(org, (float) newWidth / org.getWidth(), (float) newHeight / org.getHeight());
    }

    /**
     * 缩放位图
     *
     * @param org         bitmap
     * @param scaleWidth  缩放的宽比例
     * @param scaleHeight 缩放的高比例
     * @return
     */
    public static Bitmap scaleImage(Bitmap org, float scaleWidth, float scaleHeight) {
        if (org == null) {
            return null;
        }

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(org, 0, 0, org.getWidth(), org.getHeight(), matrix, true);
    }
    

    
    /*=============Sharepreferences相关===============*/

    /**
     * put string preferences
     *
     * @param context
     * @param key     The name of the preference to modify
     * @param value   The new value for the preference
     * @return True if the new values were successfully written to persistent storage.
     */
    public static boolean putString(Context context, String key, String value) {
        SharedPreferences settings = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, value);
        return editor.commit();
    }

    /**
     * get string preferences
     *
     * @param context
     * @param key     The name of the preference to retrieve
     * @return The preference value if it exists, or null. Throws ClassCastException if there is a preference with this
     * name that is not a string
     * @see #getString(Context, String, String)
     */
    public static String getString(Context context, String key) {
        return getString(context, key, null);
    }

    /**
     * get string preferences
     *
     * @param context
     * @param key          The name of the preference to retrieve
     * @param defaultValue Value to return if this preference does not exist
     * @return The preference value if it exists, or defValue. Throws ClassCastException if there is a preference with
     * this name that is not a string
     */
    public static String getString(Context context, String key, String defaultValue) {
        SharedPreferences settings = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        return settings.getString(key, defaultValue);
    }

    /**
     * put int preferences
     *
     * @param context
     * @param key     The name of the preference to modify
     * @param value   The new value for the preference
     * @return True if the new values were successfully written to persistent storage.
     */
    public static boolean putInt(Context context, String key, int value) {
        SharedPreferences settings = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(key, value);
        return editor.commit();
    }

    /**
     * get int preferences
     *
     * @param context
     * @param key     The name of the preference to retrieve
     * @return The preference value if it exists, or -1. Throws ClassCastException if there is a preference with this
     * name that is not a int
     * @see #getInt(Context, String, int)
     */
    public static int getInt(Context context, String key) {
        return getInt(context, key, -1);
    }

    /**
     * get int preferences
     *
     * @param context
     * @param key          The name of the preference to retrieve
     * @param defaultValue Value to return if this preference does not exist
     * @return The preference value if it exists, or defValue. Throws ClassCastException if there is a preference with
     * this name that is not a int
     */
    public static int getInt(Context context, String key, int defaultValue) {
        SharedPreferences settings = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        return settings.getInt(key, defaultValue);
    }

    /**
     * put long preferences
     *
     * @param context
     * @param key     The name of the preference to modify
     * @param value   The new value for the preference
     * @return True if the new values were successfully written to persistent storage.
     */
    public static boolean putLong(Context context, String key, long value) {
        SharedPreferences settings = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putLong(key, value);
        return editor.commit();
    }

    /**
     * get long preferences
     *
     * @param context
     * @param key     The name of the preference to retrieve
     * @return The preference value if it exists, or -1. Throws ClassCastException if there is a preference with this
     * name that is not a long
     * @see #getLong(Context, String, long)
     */
    public static long getLong(Context context, String key) {
        return getLong(context, key, -1);
    }

    /**
     * get long preferences
     *
     * @param context
     * @param key          The name of the preference to retrieve
     * @param defaultValue Value to return if this preference does not exist
     * @return The preference value if it exists, or defValue. Throws ClassCastException if there is a preference with
     * this name that is not a long
     */
    public static long getLong(Context context, String key, long defaultValue) {
        SharedPreferences settings = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        return settings.getLong(key, defaultValue);
    }

    /**
     * put float preferences
     *
     * @param context
     * @param key     The name of the preference to modify
     * @param value   The new value for the preference
     * @return True if the new values were successfully written to persistent storage.
     */
    public static boolean putFloat(Context context, String key, float value) {
        SharedPreferences settings = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putFloat(key, value);
        return editor.commit();
    }

    /**
     * get float preferences
     *
     * @param context
     * @param key     The name of the preference to retrieve
     * @return The preference value if it exists, or -1. Throws ClassCastException if there is a preference with this
     * name that is not a float
     * @see #getFloat(Context, String, float)
     */
    public static float getFloat(Context context, String key) {
        return getFloat(context, key, -1);
    }

    /**
     * get float preferences
     *
     * @param context
     * @param key          The name of the preference to retrieve
     * @param defaultValue Value to return if this preference does not exist
     * @return The preference value if it exists, or defValue. Throws ClassCastException if there is a preference with
     * this name that is not a float
     */
    public static float getFloat(Context context, String key, float defaultValue) {
        SharedPreferences settings = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        return settings.getFloat(key, defaultValue);
    }

    /**
     * put boolean preferences
     *
     * @param context
     * @param key     The name of the preference to modify
     * @param value   The new value for the preference
     * @return True if the new values were successfully written to persistent storage.
     */
    public static boolean putBoolean(Context context, String key, boolean value) {
        SharedPreferences settings = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(key, value);
        return editor.commit();
    }

    /**
     * get boolean preferences, default is false
     *
     * @param context
     * @param key     The name of the preference to retrieve
     * @return The preference value if it exists, or false. Throws ClassCastException if there is a preference with this
     * name that is not a boolean
     * @see #getBoolean(Context, String, boolean)
     */
    public static boolean getBoolean(Context context, String key) {
        return getBoolean(context, key, false);
    }

    /**
     * get boolean preferences
     *
     * @param context
     * @param key          The name of the preference to retrieve
     * @param defaultValue Value to return if this preference does not exist
     * @return The preference value if it exists, or defValue. Throws ClassCastException if there is a preference with
     * this name that is not a boolean
     */
    public static boolean getBoolean(Context context, String key, boolean defaultValue) {
        SharedPreferences settings = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        return settings.getBoolean(key, defaultValue);
    }

    /**
     * 清除所有SharedPreferences 的数据
     *
     * @param context
     */
    public static void clearData(Context context) {
        SharedPreferences sp = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.clear();
        editor.commit();
    }
    
    /*=============其他===============*/

    /**
     * 在0-max之间取一个随机值
     *
     * @param max
     * @return
     */
    public static int getRandom(int max) {
        return getRandom(0, max);
    }

    /**
     * 在 min 到 max之间取随机值
     *
     * @param min
     * @param max
     * @return
     */
    public static int getRandom(int min, int max) {
        if (min > max) {
            return 0;
        }
        if (min == max) {
            return min;
        }
        return min + new Random().nextInt(max - min);
    }


}
