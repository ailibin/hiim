package com.aiitec.hiim.im.other;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.aiitec.hiim.R;
import com.aiitec.hiim.annotation.ContentView;
import com.aiitec.hiim.base.BaseKtActivity;
import com.aiitec.hiim.im.chat.ChatActivity;
import com.aiitec.hiim.im.utils.LogUtil;
import com.aiitec.hiim.utils.BaseUtil;
import com.tencent.imsdk.TIMImage;
import com.tencent.imsdk.TIMImageElem;
import com.tencent.imsdk.TIMImageType;
import com.tencent.imsdk.TIMMessage;

import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * 聊天大图显示
 *
 * @author Anthony
 * @version 1.0
 * createTime 2017/9/22.
 */
@ContentView(R.layout.activity_chat_big_image_show)
public class ChatBigImageShowActivity extends BaseKtActivity {

    public static final String ARG_MESSAGE_ID = "messageId";
    private static final int SAVE_SUCCESSFUL = 1;
    private static final int SAVE_ERROR = 2;
    ViewPager viewPager;
    TextView tv_image_index;
    TextView tvToSaveThePicture;
    private ImageAdapter imageAdapter;
    private List<BigImageFragment> fragments = new ArrayList<>();
    private File savePictureFile;
    /**
     * 图片保存结果的handler
     */
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == SAVE_SUCCESSFUL) {
                //发送广播通知系统更新图库
                if (savePictureFile != null) {
                    Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    Uri uri = Uri.fromFile(savePictureFile);
                    intent.setData(uri);
                    sendBroadcast(intent);
                }
                //提示
                BaseUtil.showToast("保存成功");
            } else {
                BaseUtil.showToast("保存失败");
            }
            tvToSaveThePicture.setEnabled(true);
            progressDialogDismiss();
        }
    };

    @Override
    protected void init(@Nullable Bundle savedInstanceState) {
        String messageId = getBundle().getString(ARG_MESSAGE_ID);
        viewPager = findViewById(R.id.viewpager);
        tv_image_index=findViewById(R.id.tv_image_index);
        tvToSaveThePicture=findViewById(R.id.tv_to_save_the_picture);
        int position = 0;
        int index = 0;
        for (TIMMessage timMessage : ChatActivity.Companion.getImageMessageList()) {
            BigImageFragment imageFragment = BigImageFragment.newInstance(timMessage);
            fragments.add(imageFragment);
            if (timMessage.getMsgId().equals(messageId)) {
                position = index;
            }
            index++;
        }
        imageAdapter = new ImageAdapter(this.getSupportFragmentManager());
        viewPager.setAdapter(imageAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }

            @Override
            public void onPageSelected(int position) {
                tv_image_index.setText((position + 1) + "/" + ChatActivity.Companion.getImageMessageList().size());
            }
        });
        viewPager.setCurrentItem(position);
        tv_image_index.setText((position + 1) + "/" + ChatActivity.Companion.getImageMessageList().size());
        //保存图片
        tvToSaveThePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentIndex = viewPager.getCurrentItem();
                TIMMessage message = ChatActivity.Companion.getImageMessageList().get(currentIndex);
                TIMImageElem elem = (TIMImageElem) message.getElement(0);
                String picturePath = "";
                for (TIMImage image : elem.getImageList()) {
                    if (image.getType() == TIMImageType.Original) {
                        picturePath = image.getUrl();
                        break;
                    }
                }
                LogUtil.i("当前的位置为：" + currentIndex +
                        "\n图片的地址为：" + picturePath +
                        "\n这个消息有图片张数为：" + elem.getImageList().size());

                onDownLoadAndSave(picturePath);
                tvToSaveThePicture.setEnabled(false);
            }
        });

    }


    /**
     * 启动图片下载线程
     */
    private void onDownLoadAndSave(String url) {
        progressDialogShow();
        SavePictureByGlide service = new SavePictureByGlide(getApplicationContext(),
                url,
                new SavePictureByGlide.ImageDownLoadCallBack() {
                    @Override
                    public void onDownLoadSuccess(Bitmap bitmap, File file) {
                        savePictureFile = file;
                        // 在这里执行图片保存方法
                        Message message = new Message();
                        message.what = SAVE_SUCCESSFUL;
                        handler.sendMessageDelayed(message, 60);
                    }

                    @Override
                    public void onDownLoadFailed() {
                        // 图片保存失败
                        Message message = new Message();
                        message.what = SAVE_ERROR;
                        handler.sendMessageDelayed(message, 60);
                    }
                });
        //启动图片下载线程
        new Thread(service).start();
    }

    private class ImageAdapter extends FragmentPagerAdapter {
        public ImageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public Fragment getItem(int arg0) {
            return fragments.get(arg0);
        }
    }
}
