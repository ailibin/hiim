package com.aiitec.widgets.crop;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aiitec.hiim.R;
import com.aiitec.hiim.utils.StatusBarUtil;
import com.aiitec.widgets.crop.util.BitmapLoadUtils;
import com.aiitec.widgets.crop.view.CropImageView;
import com.aiitec.widgets.crop.view.GestureCropImageView;
import com.aiitec.widgets.crop.view.OverlayView;
import com.aiitec.widgets.crop.view.TransformImageView;
import com.aiitec.widgets.crop.view.UCropView;

import java.io.OutputStream;

/**
 * 图片裁剪页面
 */
public class UCropActivity extends AppCompatActivity {

    GestureCropImageView mGestureCropImageView;
    OverlayView mOverlayView;
    UCropView ucvCropView;
    TextView tvTitle;
    //输出的uri
    private Uri mOutputUri;
    LinearLayout titleBar;
    //是否显示边框
    private boolean isShowCropFrame = false;
    TextView tvSureForCrop;
    TextView tvCancelForCrop;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.afb_activity_for_edit_user_header);
        titleBar = findViewById(R.id.ll_title_bar);
        StatusBarUtil.addStatusBarView(titleBar, R.color.transparent);
        initViews();
        ucvCropView = findViewById(R.id.ucv_crop_view);
        mGestureCropImageView = ucvCropView.getCropImageView();
        mOverlayView = ucvCropView.getOverlayView();

        // 设置是否允许缩放
        mGestureCropImageView.setScaleEnabled(true);
        // 设置是否允许旋转
        mGestureCropImageView.setRotateEnabled(false);
        // 设置剪切后的最大宽度
//        mGestureCropImageView.setMaxResultImageSizeX(300);
        // 设置剪切后的最大高度
//        mGestureCropImageView.setMaxResultImageSizeY(300);
        // 设置外部阴影颜色
        mOverlayView.setDimmedColor(Color.parseColor("#cc888888"));
        // 设置周围阴影是否为椭圆(如果false则为矩形)
        mOverlayView.setOvalDimmedLayer(true);
        // 设置显示裁剪边框
        mOverlayView.setShowCropFrame(false);
        // 设置不显示裁剪网格
        mOverlayView.setShowCropGrid(false);

        //其他
        final Intent intent = getIntent();
        setImageData(intent);
        initEvent();
    }

    private void initViews() {

        findViewById(R.id.iv_go_back_for_edit_user_header).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        findViewById(R.id.tv_save_selected_photo_for_edit_user_header).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cropAndSaveImage();
            }
        });

    }

    private void setImageData(Intent intent) {
        Uri inputUri = intent.getParcelableExtra(UCrop.EXTRA_INPUT_URI);
        mOutputUri = intent.getParcelableExtra(UCrop.EXTRA_OUTPUT_URI);
//        isShowCropFrame = intent.getBooleanExtra(UCrop.EXTRA_IS_SHOW_CROPFRAME, false);
        if (inputUri != null && mOutputUri != null) {
            try {
                mGestureCropImageView.setImageUri(inputUri);
            } catch (Exception e) {
                setResultException(e);
                finish();
            }
        } else {
            setResultException(new NullPointerException("Both input and output Uri must be specified"));
            finish();
        }

        // 设置裁剪宽高比
        if (intent.getBooleanExtra(UCrop.EXTRA_ASPECT_RATIO_SET, false)) {
            float aspectRatioX = intent.getFloatExtra(UCrop.EXTRA_ASPECT_RATIO_X, 0);
            float aspectRatioY = intent.getFloatExtra(UCrop.EXTRA_ASPECT_RATIO_Y, 0);

            if (aspectRatioX > 0 && aspectRatioY > 0) {
                mGestureCropImageView.setTargetAspectRatio(aspectRatioX / aspectRatioY);
            } else {
                mGestureCropImageView.setTargetAspectRatio(CropImageView.SOURCE_IMAGE_ASPECT_RATIO);
            }
        }

        // 设置裁剪的最大宽高
        if (intent.getBooleanExtra(UCrop.EXTRA_MAX_SIZE_SET, false)) {
            int maxSizeX = intent.getIntExtra(UCrop.EXTRA_MAX_SIZE_X, 0);
            int maxSizeY = intent.getIntExtra(UCrop.EXTRA_MAX_SIZE_Y, 0);

            if (maxSizeX > 0 && maxSizeY > 0) {
                mGestureCropImageView.setMaxResultImageSizeX(maxSizeX);
                mGestureCropImageView.setMaxResultImageSizeY(maxSizeY);
            } else {
                Log.w("xiaobing", "尺寸需大于零");
            }
        }
    }

    private void setTitleView() {

    }

    private void initEvent() {
        mGestureCropImageView.setTransformImageListener(mImageListener);
    }

    /**
     * 保存裁剪图片
     */
    private void cropAndSaveImage() {
        OutputStream outputStream = null;
        try {
            final Bitmap croppedBitmap = mGestureCropImageView.cropImage();
            if (croppedBitmap != null) {
                outputStream = getContentResolver().openOutputStream(mOutputUri);
                croppedBitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream);
                croppedBitmap.recycle();

                setResultUri(mOutputUri, mGestureCropImageView.getTargetAspectRatio());
                finish();
            } else {
                setResultException(new NullPointerException("CropImageView.cropImage() returned null."));
            }
        } catch (Exception e) {
            setResultException(e);
            finish();
        } finally {
            BitmapLoadUtils.close(outputStream);
        }
    }

    private TransformImageView.TransformImageListener mImageListener = new TransformImageView.TransformImageListener() {
        @Override
        public void onRotate(float currentAngle) {
        }

        @Override
        public void onScale(float currentScale) {
        }

        @Override
        public void onLoadComplete() {
            Animation fadeInAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.ucrop_fade_in);
            fadeInAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    ucvCropView.setVisibility(View.VISIBLE);
                    mGestureCropImageView.setImageToWrapCropBounds();
                }

                @Override
                public void onAnimationEnd(Animation animation) {

                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
            ucvCropView.startAnimation(fadeInAnimation);
        }

        @Override
        public void onLoadFailure(Exception e) {
            setResultException(e);
            finish();
        }
    };

    private void setResultUri(Uri uri, float resultAspectRatio) {
        setResult(RESULT_OK, new Intent()
                .putExtra(UCrop.EXTRA_OUTPUT_URI, uri)
                .putExtra(UCrop.EXTRA_OUTPUT_CROP_ASPECT_RATIO, resultAspectRatio));
    }

    private void setResultException(Throwable throwable) {
        setResult(UCrop.RESULT_ERROR, new Intent().putExtra(UCrop.EXTRA_ERROR, throwable));
    }
}
