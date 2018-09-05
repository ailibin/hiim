package com.aiitec.widgets.crop.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;

/**
 * @Author Xiaobing
 * @Version 1.0
 * Created on 2018/1/3
 * @effect 选择、上传图片的工具
 */

public class UploadPhotoUtils {

    public static DisplayMetrics metric = new DisplayMetrics();

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
     * 得到屏幕高度
     *
     * @param context
     * @return
     * @author shc DateTime 2014-11-20 下午3:04:44
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
     */
    public static int getScreenHeight(Context context) {
        WindowManager manager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        manager.getDefaultDisplay().getMetrics(metric);
        return metric.heightPixels;
    }

    //toast相关
    private static Toast mToast;
    private static String lastText = "";

    public static void showToast(Context context, String info) {
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

    public static void showToast(Context context, int info) {
        showToast(context, context.getResources().getString(info));
    }

    /**
     * 获取协议缓存路径
     *
     * @param context
     * @return
     */
    public static String getCacheDir(Context context) {
        String cacheDir = "";
        if (context.getExternalCacheDir() != null) {
            cacheDir = context.getExternalCacheDir().getAbsolutePath() + "/cache/";
        } else if (isSDCardEnable()) {
            cacheDir = getSDCardPath() + "/" + context.getPackageName() + "/cache/";
        }

        return cacheDir;
    }


    public static Bitmap safeDecodeStream(Context context, Uri uri,
                                          int minWidth, int minHeight) {
        if (context == null || uri == null) {
            return null;
        }
        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        ContentResolver resolver = context.getContentResolver();
        try {
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(
                    new BufferedInputStream(resolver.openInputStream(uri),
                            16 * 1024), null, options);
            int oldWidth = options.outWidth;
            int oldHeight = options.outHeight;
            int scale = 1;
            if (oldWidth > oldHeight && oldHeight > minHeight) {
                scale = (int) (oldHeight / (float) minHeight);
            } else if (oldWidth <= oldHeight && oldWidth > minWidth) {
                scale = (int) (oldWidth / (float) minWidth);
            }
            scale = scale < 1 ? 1 : scale;
            options.inSampleSize = scale;
            options.inJustDecodeBounds = false;
            bitmap = BitmapFactory.decodeStream(new BufferedInputStream(
                    resolver.openInputStream(uri), 16 * 1024), null, options);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    // 专为Android4.4设计的从Uri获取文件绝对路径，以前的方法已不好使
    @SuppressLint("NewApi")
    public static String getPathByUri4kitkat(final Context context, final Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),
                        Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }// File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used yjb_scrollbar_animation_in_1 the query.
     * @param selectionArgs (Optional) Selection arguments used yjb_scrollbar_animation_in_1 the query.
     * @return The value of the _data column, which is typically a file path.
     */
    private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }
}
