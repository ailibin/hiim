package com.aiitec.hiim.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.aiitec.hiim.im.utils.LogUtil;

import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static java.lang.System.currentTimeMillis;

/**
 * 常用工具
 * Created by afb on 2017/9/18.
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
     * view弹出软键盘
     */
    public static void openKeyboard(Activity activity, View view) {
        view.setFocusable(true);
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * Long类型分钟值时间转换
     */
    public static String formartLongToTime(long time, String formart) {
        String formatTime = String.valueOf(time);
        if (!TextUtils.isEmpty(formatTime)) {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat(formart);
            formatTime = format.format(new Date(Long.valueOf(formatTime) * 1000));
        }
        return formatTime;
    }

    /**
     * 点击空白处隐藏键盘用高
     *
     * @param view
     * @param event
     * @return
     */
    public static boolean isShouldHideInput(View view, MotionEvent event) {
        if (view != null && (view instanceof EditText)) {
            int[] leftTop = {0, 0};
            //获取输入框当前的location位置  
            view.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + view.getHeight();
            int right = left + view.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击的是输入框区域，保留点击EditText的事件  
                return false;
            } else {
                return true;
            }
        }
        return false;
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

    public static String getToDay() {
        String str;
        //得到当前的时间
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日");
        Date curDate = new Date(currentTimeMillis());
        str = formatter.format(curDate);
        return str;
    }

    public static String getDate() {
        String str;
        //得到当前的时间(小时和分钟)
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
        Date curDate = new Date(currentTimeMillis());
        str = formatter.format(curDate);
        return str;
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

    public static void showToast(final String msg) {
        //这是用于当网络异常时,限制toast弹出的次数
        if (mToast == null) {
            mToast = Toast.makeText(mContext, "", Toast.LENGTH_SHORT);
        }
        mToast.setDuration(Toast.LENGTH_SHORT);
        mToast.setText(msg);
        mToast.show();

    }

    public static void showToast(final int msgResId) {
        //这是用于当网络异常时,限制toast弹出的次数
        if (mToast == null) {
            mToast = Toast.makeText(mContext, "", Toast.LENGTH_SHORT);
        } else {
            mToast.setDuration(Toast.LENGTH_SHORT);
            mToast.setText(msgResId);
            mToast.show();
        }
    }

    /**
     * 设置页面的透明度(兼容华为手机)
     *
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

    /**
     * 判断是否全是字母
     *
     * @param strNum
     * @return
     */
    public static boolean isAllNum(String strNum) {
        return strNum.matches("[0-9]{1,}");
    }

    /**
     * 是否全是字母
     *
     * @param str
     * @return
     */
    public static boolean isAllLetter(String str) {
        char[] chars = str.toCharArray();
        boolean isPhontic = false;
        for (int i = 0; i < chars.length; i++) {
            isPhontic = (chars[i] >= 'a' && chars[i] <= 'z') || (chars[i] >= 'A' && chars[i] <= 'Z');
            if (!isPhontic) {
                return false;
            }
        }
        return true;
    }


    /**
     * 格式化单位
     * 计算文件的大小
     *
     * @param size
     * @return
     */
    public static String getFormatSize(double size) {
        double kiloByte = size / 1024;
        if (kiloByte < 1) {
//            return size + "Byte";
            return "0K";
        }

        double megaByte = kiloByte / 1024;
        if (megaByte < 1) {
            BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "KB";
        }

        double gigaByte = megaByte / 1024;
        if (gigaByte < 1) {
            BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "MB";
        }

        double teraBytes = gigaByte / 1024;
        if (teraBytes < 1) {
            BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "GB";
        }
        BigDecimal result4 = new BigDecimal(teraBytes);
        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString()
                + "TB";
    }


    /**
     * 自动缩进方法，外部调用
     *
     * @param tv     TextView对象，在xml中必须得使用自定义的这个，至于参数为啥是 TextView ，其实你换成自己也没问题。
     * @param indent 在文本之后缩进，比如你需要缩进 1. 就传入 "1." 字符串就好, 会测量indent 的宽度，以他的宽度缩进
     * @return 返回缩进完了之后的 字符串，所以你的 setText 哦，傻傻的盯着屏幕，还问为啥不好使。
     */
    public static String autoSplitText(final TextView tv, final String indent) {
        //原始文本
        final String rawText = tv.getText().toString();
        //画笔，还包含字体信息
        final Paint paint = tv.getPaint();
        int a = tv.getPaddingLeft();
        int b = tv.getPaddingRight();
        int c = tv.getWidth();
        if (c != 0) {
            //空间可用宽度
            final float tvWidth = c - a - b;
            //将缩进处理成空格
            String indentSpace = "";
            float indentWidth = 0;
            if (!TextUtils.isEmpty(indent)) {
                float rawIndentWidth = paint.measureText(indent);
                if (rawIndentWidth < tvWidth) {
                    while ((indentWidth = paint.measureText(indentSpace)) < rawIndentWidth) {
                        indentSpace += " ";
                    }
                }
            }

            //将原始文本按行拆分
            String[] rawTextLines = rawText.replaceAll("\r", "").split("\n");
            StringBuilder sbNewText = new StringBuilder();
            for (String rawTextLine : rawTextLines) {
                if (paint.measureText(rawTextLine) <= tvWidth) {
                    //如果行宽度在空间范围之内，就不处理了
                    sbNewText.append(rawTextLine + "\n");
                } else {
                    //否则按字符测量，在超过可用宽度的前一个字符处，手动替换，加上换行，缩进
                    float lineWidth = 0;
                    for (int i = 0; i != rawTextLine.length(); ++i) {
                        char ch = rawTextLine.charAt(i);
                        //从手动换行的第二行开始加上缩进
                        if (lineWidth < 0.1f && i != 0) {
                            sbNewText.append(indentSpace);
                            lineWidth += indentWidth;
                        }
                        float textWidth = paint.measureText(String.valueOf(ch));
                        lineWidth += textWidth;
                        if (lineWidth < tvWidth) {
                            sbNewText.append(ch);
                        } else {
                            sbNewText.append("\n");
                            lineWidth = 0;
                            --i;
                        }
                    }
                    sbNewText.append("\n");
                }
            }
            //结尾多余的换行去掉
            if (!rawText.endsWith("\n")) {
                sbNewText.deleteCharAt(sbNewText.length() - 1);
            }
            return sbNewText.toString();
        } else {
            return "";
        }
    }

    /**
     * 键值对形式json字符串转map对象
     *
     * @param jsonStr
     * @return
     */
    public static Map<String, String> jsonToMap(String jsonStr) {
        Map<String, String> data = new HashMap<>();
        // 将json字符串转换成jsonObject
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(jsonStr);
            Iterator ite = jsonObject.keys();
            // 遍历jsonObject数据,添加到Map对象
            while (ite.hasNext()) {
                String key = ite.next().toString();
                String value = null;
                value = jsonObject.get(key).toString();
                data.put(key, value);
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.i("键值对形式json字符串转map对象出错了");
        }
        // 或者直接将 jsonObject赋值给Map
        // data = jsonObject;
        return data;
    }

}
