package com.aiitec.hiim.im.other;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;

/**
 * @Author Xiaobing
 * @Version 1.0
 * Created on 2017/10/19
 * @effect 用glide保存图片
 */

public class SavePictureByGlide implements Runnable {
    private String url;
    private Context context;
    private ImageDownLoadCallBack callBack;
    private File currentFile;

    public SavePictureByGlide(Context context, String url, ImageDownLoadCallBack callBack) {
        this.url = url;
        this.callBack = callBack;
        this.context = context;
    }

    @Override
    public void run() {
        Bitmap bitmap = null;
        try {
//            bitmap = Glide.with(context)
//                    .load(url)
//                    .asBitmap()
//                    .into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
//                    .get();
            if (bitmap != null) {
                // 在这里执行图片保存方法  
                saveImageToGallery(context, bitmap);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bitmap != null && currentFile.exists()) {
                callBack.onDownLoadSuccess(bitmap, currentFile);
            } else {
                callBack.onDownLoadFailed();
            }
        }
    }

    /**
     * 保存图片
     *
     * @param context
     * @param bmp
     */
    public void saveImageToGallery(Context context, Bitmap bmp) {
        // 首先保存图片(//注意小米手机必须这样获得public绝对路径)
        File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsoluteFile();
        String fileName = "giftflyPicture";
        File appDir = new File(file, fileName);
        if (!appDir.exists()) {
            appDir.mkdirs();
        }
        //保存的图片名
        String pictureName = "giftfly_" + System.currentTimeMillis() + ".jpg";
        currentFile = new File(appDir, pictureName);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(currentFile);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 图片保存结果的回调借口
     */
    public interface ImageDownLoadCallBack {
        /**
         * 图片保存成功的回调方法
         *
         * @param bitmap
         */
        void onDownLoadSuccess(Bitmap bitmap, File file);

        /**
         * 图片保存失败的回调方法
         */
        void onDownLoadFailed();
    }
}
