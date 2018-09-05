package com.aiitec.hiim.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.aiitec.openapi.utils.LogUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by ailibin on 2018/2/4.
 */

public class BitmapUtil {

//    private static int mLogoResId = R.drawable.my_icon_default_avatar2x;
//    private static int mBackgroundResId = R.color.white;
//
//    public static Bitmap createBitmap(Activity activity, String qrContent, int logoResId, int backgroundResId) {
//        if (logoResId > 0) {
//            mLogoResId = logoResId;
//        }
//        if (backgroundResId > 0) {
//            mBackgroundResId = backgroundResId;
//        }
//        Resources res = activity.getResources();
//        Bitmap logoBitmap = BitmapFactory.decodeResource(res, mLogoResId);
//        Bitmap qrBg = BitmapFactory.decodeResource(res, mBackgroundResId);
//        Bitmap bitmap = new QREncode.Builder(activity)
//                .setColor(activity.getResources().getColor(R.color.black))//二维码颜色
////                .setColors(0xFF0094FF, 0xFFFED545, 0xFF5ACF00, 0xFFFF4081)//二维码彩色
//                .setQrBackground(qrBg)//二维码背景
//                .setMargin(0)//二维码边框
//                //二维码类型
//                .setParsedResultType(TextUtils.isEmpty(qrContent) ? ParsedResultType.URI : ParsedResultType.TEXT)
//                //二维码内容
//                .setContents(TextUtils.isEmpty(qrContent) ? "https://github.com/mylhyl" : qrContent)
//                .setSize(500)//二维码等比大小
////                .setLogoBitmap(logoBitmap, 90)
//                .build().encodeAsBitmap();
//
//        return bitmap;
//    }

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
