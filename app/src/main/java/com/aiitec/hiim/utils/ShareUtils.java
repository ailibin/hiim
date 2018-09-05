package com.aiitec.hiim.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.Toast;

import com.aiitec.openapi.utils.LogUtil;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

import java.io.File;
import java.util.Map;

/**
 * 分享工具类
 * createTime 2018/03/30
 *
 * @author Anthony
 */
public class ShareUtils {

    private Context context;
    private String content, path, shareUrl, title;
    private Bitmap bitmap;
    private ShareSuccessedListener listener = null;
    private UMShareAPI mShareAPI;
    public static boolean showLog;
    public static boolean showToast;
    public static String TAG = "AII_SHARE";

    public void setShareContent(String content) {
        this.content = content;
    }

    public void setShareImage(String path) {
        this.path = path;
        LogUtil.e("ailibin", "path123: " + path);
    }

    public void setShareImage(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public void setShareUrl(String url) {
        this.shareUrl = url;
    }

    public void setShareTitle(String title) {
        this.title = title;
    }

    public ShareUtils(Context context) {
        this.context = context;
        mShareAPI = UMShareAPI.get(context);
//        UMConfigure.setLogEnabled(true);
    }

    public void deleteUserAuth(final SHARE_MEDIA platfrom,
                               UMAuthListener umListener) {
        mShareAPI.deleteOauth((Activity) context, platfrom, umListener);
    }

    public void getUserData(final SHARE_MEDIA platfrom,
                            UMAuthListener umListener) {
        try {
            mShareAPI.getPlatformInfo((Activity) context, platfrom, umListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onStart(SHARE_MEDIA share_media) {

        }

        @Override
        public void onResult(SHARE_MEDIA platform) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, "分享成功", Toast.LENGTH_SHORT).show();
                    if (listener != null) {
                        listener.onsuccessed();
                    }
                }
            });

        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            LogUtil.d("ailibin", t.toString());
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, "分享失败", Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            // Toast.makeText(context,platform + " 分享取消了",
            // Toast.LENGTH_SHORT).show();
        }
    };

    /**
     * 直接分享
     */
    public void directShare(SHARE_MEDIA platform) {
        if (content == null) {
            content = "";
        }
        if (title == null) {
            title = "";
        }
        UMImage image = null;
        if (!TextUtils.isEmpty(path)) {
            if (path.startsWith("http")) {
                image = new UMImage(context, path);
            } else if (bitmap != null) {
                image = new UMImage(context, bitmap);
            } else {
                File file = new File(path);
                if (file.exists()) {
                    image = new UMImage(context, file);
                }
            }

        } else {
            int id = context.getResources().getIdentifier("ic_launcher", "mipmap", context.getPackageName());
            image = new UMImage(context, BitmapFactory.decodeResource(context.getResources(), id));
        }

        if (image != null) {
            image.setDescription(content);
            image.setTitle(title);
        }
        ShareAction shareAction = new ShareAction((Activity) context).setPlatform(platform)
                .setCallback(umShareListener).withMedia(image).withText(title);

        if (!TextUtils.isEmpty(shareUrl)) {
            UMWeb web = new UMWeb(shareUrl);
            web.setDescription(content);
            web.setTitle(title);
            web.setThumb(image);
            shareAction.withMedia(web);
        }
        shareAction.share();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        UMShareAPI.get(context).onActivityResult(requestCode, resultCode, data);
    }

    public void setShareSuccessedListener(ShareSuccessedListener listener) {
        this.listener = listener;
    }

    public interface ShareSuccessedListener {
        void onsuccessed();
    }

    private OnLoginSuccessedListener onLoginSuccessedListener;

    public void setOnLoginSuccessedListener(
            OnLoginSuccessedListener onLoginSuccessedListener) {
        this.onLoginSuccessedListener = onLoginSuccessedListener;
    }

    public interface OnLoginSuccessedListener {
        void onsuccessed(SHARE_MEDIA platform, Map<String, String> map);
    }
}
