package com.aiitec.hiim.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.aiitec.hiim.im.utils.LogUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by ailibin on 2018/2/4.
 */

public class BitmapUtil {


    /**
     * 将Bitmap对象转成字节码
     *
     * @param bmp
     * @param needRecycle 是否需要回收
     * @return
     */
    public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, output);
        if (needRecycle) {
            bmp.recycle();
        }

        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    /**
     * 根据一个网络连接(String)获取bitmap图像
     *
     * @param imageUri
     * @return
     */
    public static Bitmap getBitmap(String imageUri) {
        LogUtil.d("ailibin", "getbitmap:" + imageUri);
        // 显示网络上的图片
        Bitmap bitmap = null;
        try {
            URL myFileUrl = new URL(imageUri);
            HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
            LogUtil.d("ailibin", "image download finished." + imageUri);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            bitmap = null;
        } catch (IOException e) {
            e.printStackTrace();
            LogUtil.d("ailibin", "getbitmap bmp fail---");
            bitmap = null;
        }
        return bitmap;
    }
}
