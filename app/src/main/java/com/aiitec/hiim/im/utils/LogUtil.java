package com.aiitec.hiim.im.utils;

import android.text.TextUtils;
import android.util.Log;

/**
 * @author Anthony
 * createTime 2015/x/x
 * edit by 2017/10/13 修改兼容大字符串的显示，用分段来显示
 */
public class LogUtil {

    public static final String DEFAULT_TAG = "TAG_AII_NET";
    public static boolean showLog = true;
    private static final int MAX = 3800;

    private static void logIntercept(String tag, String logText, LogLevel level) {
        if (TextUtils.isEmpty(logText)) {
            return;
        }
        if (logText.length() > MAX) {
            int start = 0;
            int index = 0;
            while (logText.length() - start > MAX) {
                log(tag + index, logText.substring(start, start + MAX), level);
                start += MAX;
                index++;
            }
            if (start < logText.length()) {
                log(tag + index, logText.substring(start, logText.length()), level);
            }

        } else {
            log(tag, logText, level);
        }
    }

    private static void log(String tag, String logText, LogLevel level) {
        switch (level) {
            case VERBOSE:
                Log.v(tag, logText);
                break;
            case DEBUG:
                Log.d(tag, logText);
                break;
            case INFO:
                Log.i(tag, logText);
                break;
            case WARN:
                Log.w(tag, logText);
                break;
            case ERROR:
                Log.e(tag, logText);
                break;
            default:
                break;

        }
    }

    public enum LogLevel {
        /**
         * 冗长的
         */
        VERBOSE,
        /**
         * 调试
         */
        DEBUG,
        /**
         * INFO
         */
        INFO,
        /**
         * 警告
         */
        WARN,
        /**
         * 错误
         */
        ERROR
    }

    public static void v(String logText) {
        if (showLog) {
            logIntercept(DEFAULT_TAG, String.valueOf(logText), LogLevel.VERBOSE);
        }
    }

    public static void v(String TAG, String logText) {
        if (showLog) {
            logIntercept(TAG, logText, LogLevel.VERBOSE);
        }
    }

    public static void d(String logText) {
        if (showLog) {
            logIntercept(DEFAULT_TAG, String.valueOf(logText), LogLevel.DEBUG);
        }
    }

    public static void d(String TAG, String logText) {
        if (showLog) {
            logIntercept(TAG, logText, LogLevel.DEBUG);
        }
    }

    public static void i(String logText) {
        if (showLog) {
            logIntercept(DEFAULT_TAG, logText, LogLevel.INFO);
        }
    }

    public static void i(String TAG, String logText) {
        if (showLog) {
            logIntercept(TAG, logText, LogLevel.INFO);
        }
    }

    public static void w(String TAG, String logText) {
        if (showLog) {
            logIntercept(TAG, logText, LogLevel.WARN);
        }
    }

    public static void w(String logText) {
        if (showLog) {
            logIntercept(DEFAULT_TAG, logText, LogLevel.WARN);
        }
    }

    public static void e(String logText) {
        if (showLog) {
            logIntercept(DEFAULT_TAG, logText, LogLevel.ERROR);
        }
    }

    public static void e(String TAG, String logText) {
        if (showLog) {
            logIntercept(TAG, logText, LogLevel.ERROR);
        }
    }

    public static void d(Class<?> c, String logText) {
        if (showLog) {
            logIntercept(DEFAULT_TAG, "[" + c.getSimpleName() + "]" + logText, LogLevel.DEBUG);
        }
    }

    public static void d(Object c, String logText) {
        if (showLog) {
            logIntercept(DEFAULT_TAG, "[" + c.getClass().getSimpleName() + "]" + logText, LogLevel.DEBUG);
        }
    }

    public static void print(String logText) {
        if (showLog) {
            System.out.println(logText);
        }
    }

}
