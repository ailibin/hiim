package com.aiitec.widgets.crop.util;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.aiitec.openapi.utils.LogUtil;
import com.aiitec.widgets.crop.UCrop;
import com.aiitec.widgets.crop.UCropActivity;
import com.zhy.base.fileprovider.FileProvider7;

import java.io.File;
import java.io.IOException;

import static android.app.Activity.RESULT_OK;

/**
 * 拍照与上传图像
 * <p>
 * 用法：使用showPickDialog弹出对话框，getImageId()获得上传后的图片id
 * <p>
 * 必须：在onActivityResult调用同名方法
 */
public class UploadPhotoHelper {

    private String cachedir;
    private Activity activity;
    private ImageView imageView;

    private final int CAMERA;
    private final int PHOTO;
    private final int CUT;
    /**
     * 头像上传本地图片的缓存路径
     */
    private String filePath;
    private boolean cutEnable = true;
    /**
     * 是否自定义裁剪
     */
    private boolean customCut;
    /**
     * 是证件照还是头像照 type=1证件照 type=2 头像照
     */
    private int type = -1;
    private String title;
    private PhotoDialog photoDialog;

    private GetUploadFileSuccessListener getUploadFileSuccessListener;
    /**
     * 点击图片所在的索引
     */
    private int imagePosition;

    public void setCutEnable(boolean cutEnable) {
        this.cutEnable = cutEnable;
    }

    /**
     * 通用构造器
     *
     * @param activity  使用uploadphotohelper的activity
     * @param customCut 是否自定义裁剪
     * @param title     自定义裁剪页面的标题
     */
    public UploadPhotoHelper(Activity activity, boolean customCut, String title, int type) {
        this(activity, null);
        this.customCut = customCut;
        this.title = title;
        this.type = type;
    }


    public UploadPhotoHelper(Activity activity, ImageView imageView) {
        this(activity, imageView, 0);
    }

    public UploadPhotoHelper(Activity activity, ImageView imageView, int defaultImgRes) {
        this(activity, imageView, defaultImgRes, 1024);
    }

    public UploadPhotoHelper(Activity activity, ImageView imageView, int defaultImgRes, int baseRequestCode) {
        this.activity = activity;

        CAMERA = 0x1 + baseRequestCode * 4;
        PHOTO = 0x2 + baseRequestCode * 4;
        CUT = 0x3 + baseRequestCode * 4;

        photoDialog = new PhotoDialog(activity);
        photoDialog.setOnButtonClickListener(onButtonClickListener);

        File topDir = new File(UploadPhotoUtils.getCacheDir(activity));
        if (!topDir.exists()) {
            topDir.mkdir();
        }
        if (UploadPhotoUtils.isSDCardEnable()) {

            cachedir = topDir.getAbsolutePath() + "/uploadfiles/";
            File dir = new File(cachedir);
            if (!dir.exists()) {
                if (!dir.getParentFile().exists()) {
                    dir.getParentFile().mkdir();
                }
                dir.mkdir();
//                dir.mkdirs();
            }
        }
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            filePath = savedInstanceState.getString("filePath");
        }
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString("filePath", filePath);
    }


    /**
     * 在activity的onActivityResult里加入这个方法（必须）
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        // 如果是直接从相册获取
        if (requestCode == PHOTO && data != null) {
            if (resultCode == RESULT_OK) {
                if (cutEnable) {
                    if (customCut) {
                        beginCrop(data.getData());
                    } else {
                        startPhotoZoom(data.getData());
                    }
                } else {
                    setPicToView(data.getData());
                }
            }
        }
        // 如果是调用相机拍照时
        else if (requestCode == CAMERA) {
            if (resultCode == RESULT_OK) {
                if (!TextUtils.isEmpty(filePath)) {
                    File temp = new File(filePath);
                    if (cutEnable) {
                        if (customCut) {
                            beginCrop(Uri.fromFile(temp));
                        } else {
                            startPhotoZoom(Uri.fromFile(temp));
                        }
                    } else {
                        setPicToView(Uri.fromFile(temp));
                    }
                }
            }
        }
        // 取得裁剪后的图片(系统裁剪方式)
        else if (requestCode == CUT) {
            if (resultCode == RESULT_OK) {
                if (tempPhotoUri != null) {
                    setPicToView(tempPhotoUri);
                }
            }
        }
        // 取得裁剪后的图片(自定义裁剪方式)UCrop,对头像进行处理
        else if (requestCode == UCrop.REQUEST_CROP) {
            handleCrop(resultCode, data);
        }
//        //使用另外一个库对证件照处理Crop
//        else if (requestCode == Crop.REQUEST_CROP) {
//            handleCrop(resultCode, data);
//        }
    }

    /**
     * 开始裁剪(自定义)
     *
     * @param source
     */
    private void beginCrop(Uri source) {
        //这里要加后缀名.jpg!!!
        Uri destination = Uri.fromFile(new File(activity.getCacheDir(), "wanqian-crop-" + System.currentTimeMillis() + ".jpg"));
        boolean isShowCropFrame = false;
        if (type == 2) {
            //证件照(显示方形),这里用了另外一个库,因为不管用系统的裁剪或者UCrop都不满足条件
//            Crop.of(source, destination).withAspect(1, 2).start(activity);
        } else {
            //头像照
            String path = UploadPhotoUtils.getPathByUri4kitkat(activity, source);
            File file = new File(path);
            Log.e("ailibin", "before_crop: " + file.length());
            UCrop.of(source, destination, isShowCropFrame)
                    .withTargetActivity(UCropActivity.class)
                    .withAspectRatio(1, 1)
                    .start(activity);
        }
    }

    /**
     * 处理裁剪结果(自定义)
     *
     * @param resultCode
     * @param result
     */
    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            String path = "";
            if (type == 2) {
//                path = UploadPhotoUtils.getPathByUri4kitkat(activity, Crop.getOutput(result));
            } else {
                path = UploadPhotoUtils.getPathByUri4kitkat(activity, UCrop.getOutput(result));
                File file = new File(path);
                Log.e("ailibin", "after_crop: " + file.length());
            }
            if (TextUtils.isEmpty(path)) {
                return;
            }
            File file = new File(path);
            if (file.exists()) {
                if (getUploadFileSuccessListener != null) {
                    getUploadFileSuccessListener.getUploadFileSuccess(file, imagePosition);
                }
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            UploadPhotoUtils.showToast(activity, UCrop.getError(result).getMessage());
        }
//        else if (resultCode == Crop.RESULT_ERROR) {
//            UploadPhotoUtils.showToast(activity, Crop.getError(result).getMessage());
//        }
    }

    /**
     * 从图片库选取图片
     */
    public void getPhotoFromPictureLibrary() {
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            UploadPhotoUtils.showToast(activity.getApplicationContext(), "SD卡不存在");
            return;
        }
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        activity.startActivityForResult(intent, PHOTO);

    }

    /**
     * 通过拍照获取照片
     */
    private void getPhotoByTakePhotos() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = new File(cachedir);

        //如果没有这个目录的话，部分手机无法保存照片
        if (!file.exists()) {
            if (!file.getParentFile().exists()) {
                if (!file.getParentFile().getParentFile().exists()) {
                    file.getParentFile().getParentFile().mkdir();
                }
                file.getParentFile().mkdir();
            }
            file.mkdir();
        }
        filePath = cachedir + System.currentTimeMillis() + ".jpg";
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(filePath)));
        activity.startActivityForResult(intent, CAMERA);
    }

    /**
     * “取消”
     */
    public void cancel() {
        photoDialog.dismiss();
    }

    /**
     * 弹窗各视图的点击事件
     */
    PhotoDialog.OnButtonClickListener onButtonClickListener = new PhotoDialog.OnButtonClickListener() {
        @Override
        public void onPhotoClick(View view) {
            photoDialog.dismiss();
            getPhotoFromPictureLibrary();
        }

        @Override
        public void onCameraClick(View view) {
            photoDialog.dismiss();
            getPhotoByTakePhotos();
        }

        @Override
        public void onCancelClick(View view) {
            photoDialog.dismiss();
        }
    };

    /**
     * 选择上传图片提示对话框
     */
    public void showPickDialog(int index) {
        photoDialog.show();
        this.imagePosition = index;
    }

    // 选择存储位置
    Uri tempPhotoUri;
    File tempPhotoFile;

    private Uri getTempUri() {
        tempPhotoUri = FileProvider7.getUriForFile(activity, getTempFile());
        LogUtil.d("ailibin", "tempPhotoUri: " + tempPhotoUri);
        return tempPhotoUri;
    }

    private File getTempFile() {
        if (UploadPhotoUtils.isSDCardEnable()) {
            File f = new File(cachedir, "temp" + System.currentTimeMillis() + ".jpg");
            try {
                if (!f.exists()) {
                    if (!f.getParentFile().exists()) {
                        f.getParentFile().mkdirs();
                    }
                    f.createNewFile();
                }
                tempPhotoFile = f;
            } catch (IOException e) {
            }
            return f;
        }
        return null;
    }

    private double cropRatio = 1;
    private int photoMinSize = 320;

    public double getCropRatio() {
        return cropRatio;
    }

    public void setCropRatio(double cropRatio) {
        this.cropRatio = cropRatio;
    }

    public int getPhotoMinSize() {
        return photoMinSize;
    }

    public void setPhotoMinSize(int photoSize) {
        this.photoMinSize = photoSize;
    }

    /**
     * 裁剪图片方法实现
     *
     * @param uri
     */
    public void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");

        // 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", (int) (cropRatio * 10000));
        intent.putExtra("aspectY", 10000);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX",
                cropRatio > 1 ? (int) (photoMinSize * cropRatio) : photoMinSize);
        intent.putExtra("outputY", cropRatio > 1 ? photoMinSize
                : (int) (photoMinSize / cropRatio));

        //不启用人脸识别
        intent.putExtra("noFaceDetection", true);
        intent.putExtra("scale", true);
        //不用直接返回bitmap(调用系统的时候),可能很耗内存
        intent.putExtra("return-data", false);
        intent.putExtra("scaleUpIfNeeded", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, getTempUri());
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        activity.startActivityForResult(intent, CUT);
    }

    private Bitmap photo;

    /**
     * 保存裁剪之后的图片数据
     *
     * @param uri
     */
    private void setPicToView(Uri uri) {
        if (photo != null) {
            if (!photo.isRecycled()) {
                photo.recycle();
            }
            photo = null;
        }

        photo = UploadPhotoUtils.safeDecodeStream(activity, uri, photoMinSize, photoMinSize);
        LogUtil.d("ailibin", "uri: " + uri);
        String path = UploadPhotoUtils.getPathByUri4kitkat(activity, uri);
        LogUtil.d("ailibin", "afterPath: " + path);
        if (photo != null) {
            if (imageView != null && !photo.isRecycled()) {
                imageView.setImageBitmap(photo);
            }

            if (TextUtils.isEmpty(path)) {
                return;
            }
            final File file = new File(path);
            LogUtil.d("ailibin", "path1: " + file.getAbsolutePath() + "  length1: " + file.length());
            if (file.exists()) {
                if (getUploadFileSuccessListener != null) {
                    getUploadFileSuccessListener.getUploadFileSuccess(file, imagePosition);
                }
            }
        }
    }

    public void setGetUploadFileSuccessListener(GetUploadFileSuccessListener getUploadFileSuccessListener) {
        this.getUploadFileSuccessListener = getUploadFileSuccessListener;
    }

    public interface GetUploadFileSuccessListener {
        void getUploadFileSuccess(File file, int index);
    }
}
