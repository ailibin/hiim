package com.aiitec.hiim.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.aiitec.hiim.base.Api;

import java.io.File;


/**
 * @Author Anthony
 * @Version 1.0
 * createTime 2017/3/27.
 */

public class GlideImgManager {

    private static final int DEFAULT_ROUND = 10;

    public enum GlideType {
        /**
         * 加载圆形的图
         */
        TYPE_CIRCLE,
        /**
         * 加载带圆角的图
         */
        TYPE_ROUND
    }


    public static void load(Context context, String url, ImageView iv) {
        load(context, url, -1, iv, null, 0);
    }

    public static void load(Context context, String url, int emptyImg, ImageView iv) {
        load(context, url, emptyImg, emptyImg, iv, null, 0);
    }

    public static void load(Context context, String url, int emptyImg, ImageView iv, GlideType type) {
        load(context, url, emptyImg, emptyImg, iv, type, 0);
    }

    public static void load(Context context, String url, int emptyImg, ImageView iv, GlideType type, int roundDp) {
        load(context, url, emptyImg, emptyImg, iv, type, roundDp);
    }

    public static void load(Context context, String url, ImageView iv, GlideType type, int roundDp) {
        load(context, url, -1, iv, type, roundDp);
    }

//    public static DrawableTypeRequest<String> load(Context context, String url) {
//
//        if (!TextUtils.isEmpty(url)) {
//            if (!url.startsWith("http")) {
//                url = Api.INSTANCE.getBASE_URL() + url;
//            }
//        }
//        return Glide.with(context).load(url);
//    }


    /**
     * load normal  for  circle or round img
     *
     * @param url
     * @param erroImg
     * @param emptyImg
     * @param iv
     * @param type
     */
    public static void load(Context context, String url, int erroImg, int emptyImg, ImageView iv, GlideType type, int roundDp) {
        if (url == null) {
            url = "";
        }
        if (!url.startsWith("http")) {
            url = Api.INSTANCE.getBASE_URL() + url;
        }
//        DrawableTypeRequest<String> request = Glide.with(context).load(url);
//        if (emptyImg != -1) {
//            request.placeholder(emptyImg);
//        }
//        if (erroImg != -1) {
//            request.error(erroImg);
//        }
//
//        if (GlideType.TYPE_CIRCLE == type) {
//            request.transform(new GlideCircleTransform(context)).into(iv);
//        } else if (GlideType.TYPE_ROUND == type) {
//            request.transform(new GlideRoundTransform(context, roundDp)).into(iv);
//        } else {
//            request.into(iv);
//        }
    }


    public static void loadFile(Context context, String path, ImageView iv) {
        loadFile(context, new File(path), -1, -1, iv, null, 0);
    }

    public static void loadFile(Context context, String path, int emptyImg, ImageView iv) {
        loadFile(context, new File(path), emptyImg, emptyImg, iv, null, 0);
    }

    public static void loadFile(Context context, String path, int erroImg, int emptyImg, ImageView iv) {
        loadFile(context, new File(path), erroImg, emptyImg, iv, null, 0);
    }

    public static void loadFile(Context context, String path, int erroImg, int emptyImg, ImageView iv, GlideType type) {
        loadFile(context, new File(path), erroImg, emptyImg, iv, type, 0);
    }

//    public static DrawableTypeRequest<File> loadFile(Context context, String url) {
//        return Glide.with(context).load(new File(url));
//    }

    public static void loadFile(Context context, File file, int erroImg, int emptyImg, ImageView iv, GlideType type, int roundDp) {

    }


    /**
     * 使用bitmap方式加载圆形图片,此方法靠谱
     *
     * @param context
     * @param url
     * @param imageView
     * @author ailibin
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static void loadCircleByBitmap(final Context context, String url, final ImageView imageView, int emptyImageRes) {

        if (TextUtils.isEmpty(url)) {
            return;
        }
        if (!url.startsWith("http")) {
            url = Api.INSTANCE.getBASE_URL() + url;
        }

        try {
            if (((Activity) context).isDestroyed()) {
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

//        Glide.with(context)
//                .load(url)
//                .asBitmap()
//                .centerCrop()
//                .placeholder(emptyImageRes)
//                .into(new BitmapImageViewTarget(imageView) {
//                    @Override
//                    protected void setResource(Bitmap resource) {
//                        RoundedBitmapDrawable circularBitmapDrawable =
//                                RoundedBitmapDrawableFactory.create(context.getResources(), resource);
//                        circularBitmapDrawable.setCircular(true);
//                        imageView.setImageDrawable(circularBitmapDrawable);
//                    }
//                });
    }

    /**
     * 加载圆角图
     *
     * @param context
     * @param url
     * @param imageView
     */
    public static void loadRoundByBitmap(final Context context, String url, final ImageView imageView) {
        loadRoundByBitmap(context, url, imageView, -1, 0, -1);
    }

    /**
     * 使用bitmap方式加载圆角图片,此方法靠谱
     *
     * @param context
     * @param url
     * @param imageView
     * @param roundDp
     * @author ailibin
     */
    public static void loadRoundByBitmap(final Context context, String url, final ImageView imageView, final int roundDp) {
        loadRoundByBitmap(context, url, imageView, -1, roundDp, -1);
    }

    private static float radius = 5f;

    /**
     * 使用bitmap方式加载圆角图片,此方法靠谱
     *
     * @param context
     * @param url
     * @param imageView
     * @param scaleType 图片缩放类型 scaleType=1 fitCenter  scaleType=2 centerCrop scaleType=3 fitXY scaleType=4 centerInside
     * @author ailibin
     */
    public static void loadRoundByBitmap(final Context context, String url, final ImageView imageView, int emptyImageRes, final int roundDp, final int scaleType) {

        if (TextUtils.isEmpty(url)) {
            return;
        }
        if (!url.startsWith("http")) {
            url = Api.INSTANCE.getBASE_URL() + url;
        }

        //单位转换一下dpTopx
        if (roundDp > 0) {
            radius = Resources.getSystem().getDisplayMetrics().density * roundDp;
        }

    }


    /**
     * 任何控件加载背景图,此方法靠谱
     *
     * @param context
     * @param url
     * @param view
     * @author ailibin
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static void loadImgToBackground(Context context, String url, final View view) {
        loadImgToBackground(context, url, -1, view);
    }


    /**
     * 任何控件加载背景图,此方法靠谱
     *
     * @param context
     * @param url
     * @param view
     * @author ailibin
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static void loadImgToBackground(Context context, String url, int emptyView, final View view) {

        if (TextUtils.isEmpty(url)) {
            return;
        }

        if (!url.startsWith("http")) {
            url = Api.INSTANCE.getBASE_URL() + url;
        }

        try {
            if (((Activity) context).isDestroyed()) {
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 任何控件加载背景图,此方法靠谱,动态设置背景的宽高
     *
     * @param context
     * @param url
     * @param view
     * @author ailibin
     */
    public static void LoadImgToBackground(Context context, String url, final View view, int width, int height) {

        if (TextUtils.isEmpty(url)) {
            return;
        }
        if (!url.startsWith("http")) {
            url = Api.INSTANCE.getBASE_URL() + url;
        }
    }
}
