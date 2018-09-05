package com.aiitec.hiim.im.model;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.aiitec.hiim.R;
import com.aiitec.hiim.base.App;
import com.aiitec.hiim.im.adapter.ChatAdapter;
import com.aiitec.hiim.im.chat.ChatActivity;
import com.aiitec.hiim.im.utils.FileUtil;
import com.aiitec.hiim.im.utils.MediaUtil;
import com.aiitec.openapi.utils.LogUtil;
import com.aiitec.openapi.utils.ScreenUtils;
import com.tencent.imsdk.TIMCallBack;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.TIMSoundElem;
import com.tencent.imsdk.ext.message.TIMMessageExt;

import java.io.File;
import java.io.FileInputStream;

/**
 * 语音消息数据
 *
 * @author ailibin
 */
public class VoiceMessage extends Message {

    private static final String TAG = "VoiceMessage";
    private int maxLen = 10;

    public VoiceMessage(TIMMessage message) {
        this.message = message;

    }


    /**
     * 语音消息构造方法
     *
     * @param duration 时长
     * @param filePath 语音数据地址
     */
    public VoiceMessage(long duration, String filePath) {
        message = new TIMMessage();
        TIMSoundElem elem = new TIMSoundElem();
        elem.setPath(filePath);
        //填写语音时长
        elem.setDuration(duration);
        message.addElement(elem);

    }

    /**
     * 显示消息
     *
     * @param viewHolder 界面样式
     * @param context    显示消息的上下文
     */
    @Override
    public void showMessage(final ChatAdapter.ViewHolder viewHolder, Context context) {
        if (checkRevoke(viewHolder)) {
            return;
        }
        int baseWidth = ScreenUtils.dip2px(context, 36);
        int dpStep = ScreenUtils.dip2px(context, 10);

        LinearLayout linearLayout = new LinearLayout(App.Companion.getInstance().getApplicationContext());
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        ImageView voiceIcon = new ImageView(App.Companion.getInstance().getApplicationContext());
        voiceIcon.setImageResource(message.isSelf() ? R.drawable.afb_right_voice : R.drawable.afb_left_voice);
        final AnimationDrawable frameAnimatio = (AnimationDrawable) voiceIcon.getDrawable();
        frameAnimatio.stop();
        frameAnimatio.selectDrawable(0);
        long duration = ((TIMSoundElem) message.getElement(0)).getDuration();
        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 18, context.getResources().getDisplayMetrics());
        int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 14, context.getResources().getDisplayMetrics());
        LinearLayout.LayoutParams imageLp = new LinearLayout.LayoutParams(width, height);
        if (message.isSelf()) {
            imageLp.setMargins(10, 0, 0, 0);
            voiceIcon.setLayoutParams(imageLp);
            linearLayout.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
            linearLayout.addView(voiceIcon);
            //自己发的语音就不显示红点
            viewHolder.voiceUnread.setVisibility(View.GONE);
        } else {
            voiceIcon.setLayoutParams(imageLp);
            linearLayout.addView(voiceIcon);
            linearLayout.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
            //其他人的就显示
            boolean isRead = ChatActivity.Companion.getPresenter().isVoiceMessageRead(message);
            if (isRead) {
                //当前语音消息已读
                viewHolder.voiceUnread.setVisibility(View.GONE);
                LogUtil.d("ailibin", "isRead: "+isRead);
            } else {
                LogUtil.d("ailibin", "isUnRead: "+isRead);
                viewHolder.voiceUnread.setVisibility(View.VISIBLE);
            }
//            viewHolder.voiceUnread.setVisibility(View.GONE);
        }

        int bubbleWidth = 0;
        int len = (int) duration;
        if (len > maxLen) {
            bubbleWidth = baseWidth + maxLen * dpStep;
        } else {
            bubbleWidth = baseWidth + len * dpStep;
        }
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(bubbleWidth, LinearLayout.LayoutParams.WRAP_CONTENT);
        linearLayout.setLayoutParams(params);

        clearView(viewHolder);
        getBubbleView(viewHolder).addView(linearLayout);
        getBubbleView(viewHolder).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playAudio(frameAnimatio);
                ChatActivity.Companion.getPresenter().readMessage(message);
                //点击之后,标记语音消息已读
                ChatActivity.Companion.getPresenter().setVoiceMessageRead(1, message);
                viewHolder.voiceUnread.setVisibility(View.GONE);
            }
        });
        showStatus(viewHolder);
    }

    /**
     * 判断某条消息已读
     *
     * @author ailibin
     */
    public boolean isRead(TIMMessage timMessage) {
        TIMMessageExt msgExt = new TIMMessageExt(timMessage);
        return msgExt.isRead();
    }


    /**
     * 获取消息摘要
     */
    @Override
    public String getSummary() {
        String str = getRevokeSummary();
        if (str != null) {
            return str;
        }
        return App.Companion.getInstance().getApplicationContext().getString(R.string.summary_voice);
    }

    /**
     * 保存消息或消息文件
     */
    @Override
    public void save() {

    }

    private void playAudio(final AnimationDrawable frameAnimatio) {
        if (frameAnimatio.isRunning()) {
            frameAnimatio.stop();
            return;
        }
        TIMSoundElem elem = (TIMSoundElem) message.getElement(0);
        final File tempAudio = FileUtil.getTempFile(FileUtil.FileType.AUDIO);
        elem.getSoundToFile(tempAudio.getAbsolutePath(), new TIMCallBack() {
            @Override
            public void onError(int i, String s) {

            }

            @Override
            public void onSuccess() {
                try {
                    FileInputStream fis = new FileInputStream(tempAudio);
                    MediaUtil.getInstance().play(fis);
                    frameAnimatio.start();
                    MediaUtil.getInstance().setEventListener(new MediaUtil.EventListener() {
                        @Override
                        public void onStop() {
                            frameAnimatio.stop();
                            frameAnimatio.selectDrawable(0);
                        }
                    });
                } catch (Exception e) {

                }

            }
        });

    }
}
