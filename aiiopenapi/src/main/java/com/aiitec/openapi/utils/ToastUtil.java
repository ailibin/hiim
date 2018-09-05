package com.aiitec.openapi.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

public class ToastUtil {

    private static Toast mToast;
    private static String lastText = "";

    public static void show(Context context, String info) {
        if (mToast == null) {
            if (context instanceof Activity) {
                context = context.getApplicationContext();
            }
            mToast = Toast.makeText(context, info, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(info);
            mToast.setDuration(Toast.LENGTH_SHORT);
        }
        if (!lastText.equals(info)) {// 相同内容不弹出
            mToast.show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    lastText = "";// 清除前一次弹出文字信息，这样下次有同意信息才会弹出
                }
            }, 1000);
        }
        lastText = info;
    }

    public static void show(Context context, int info) {
        show(context, context.getResources().getString(info));
    }
}
