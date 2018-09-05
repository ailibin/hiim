package com.aiitec.hiim.im.model;

import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;

import com.aiitec.hiim.im.adapter.ChatAdapter;
import com.aiitec.hiim.im.utils.TimeUtil;
import com.tencent.imsdk.TIMConversationType;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.TIMMessageStatus;
import com.tencent.imsdk.ext.message.TIMMessageExt;

/**
 * 消息数据基类
 *
 * @author ailibin
 */
public abstract class Message {

    protected final String TAG = "Message";

    TIMMessage message;

    private boolean hasTime;

    /**
     * 消息描述信息
     */
    private String desc;


    public TIMMessage getMessage() {
        return message;
    }


    /**
     * 显示消息
     *
     * @param viewHolder 界面样式
     * @param context    显示消息的上下文
     */
    public abstract void showMessage(ChatAdapter.ViewHolder viewHolder, Context context);

    /**
     * 获取显示气泡
     *
     * @param viewHolder 界面样式
     */
    public RelativeLayout getBubbleView(ChatAdapter.ViewHolder viewHolder) {
        viewHolder.systemMessage.setVisibility(hasTime ? View.VISIBLE : View.GONE);
        viewHolder.systemMessage.setText(TimeUtil.getChatTimeStr(message.timestamp()));

        showDesc(viewHolder);
        if (message.isSelf()) {
            viewHolder.leftPanel.setVisibility(View.GONE);
            viewHolder.rightPanel.setVisibility(View.VISIBLE);
            return viewHolder.rightMessage;
        } else {
            viewHolder.leftPanel.setVisibility(View.VISIBLE);
            viewHolder.rightPanel.setVisibility(View.GONE);
            //群聊显示名称，群名片>个人昵称>identify
            if (message.getConversation().getType() == TIMConversationType.Group) {
                //群聊在这里还是不显示名称,格式有问题
                viewHolder.sender.setVisibility(View.GONE);
//                viewHolder.sender.setVisibility(View.VISIBLE);
//                String name = "";
//                if (message.getSenderGroupMemberProfile() != null) {
//                    name = message.getSenderGroupMemberProfile().getNameCard();
//                }
//                if ("".equals(name) && message.getSenderProfile() != null) {
//                    name = message.getSenderProfile().getNickName();
//                }
//                if ("".equals(name)) {
//                    name = message.getSender();
//                }
//                viewHolder.sender.setText(name);
            } else {
                viewHolder.sender.setVisibility(View.GONE);
            }
            return viewHolder.leftMessage;
        }

    }

    /**
     * 显示消息状态
     *
     * @param viewHolder 界面样式
     */
    public void showStatus(ChatAdapter.ViewHolder viewHolder) {
        switch (message.status()) {
            case Sending:
                viewHolder.error.setVisibility(View.GONE);
                viewHolder.sending.setVisibility(View.VISIBLE);
                break;
            case SendSucc:
                viewHolder.error.setVisibility(View.GONE);
                viewHolder.sending.setVisibility(View.GONE);
                break;
            case SendFail:
                viewHolder.error.setVisibility(View.VISIBLE);
                viewHolder.sending.setVisibility(View.GONE);
                viewHolder.leftPanel.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }

    /**
     * 判断是否是自己发的
     */
    public boolean isSelf() {
        return message.isSelf();
    }

    /**
     * 获取消息摘要
     */
    public abstract String getSummary();

    String getRevokeSummary() {
        if (message.status() == TIMMessageStatus.HasRevoked) {
            if (message.isSelf()) {
                //自己发出去的消息
                return "您撤回了一条消息";
            } else {
                //别人收到消息
                return getSender() + "撤回了一条消息";
            }
        }
        return null;
    }

    /**
     * 保存消息或消息文件
     */
    public abstract void save();


    /**
     * 删除消息
     */
    public void remove() {
        TIMMessageExt ext = new TIMMessageExt(message);
        ext.remove();
    }


    /**
     * 是否需要显示时间获取
     */
    public boolean getHasTime() {
        return hasTime;
    }


    /**
     * 是否需要显示时间设置
     *
     * @param message 上一条消息
     */
    public void setHasTime(TIMMessage message) {
        if (message == null) {
            hasTime = true;
            return;
        }
        hasTime = this.message.timestamp() - message.timestamp() > 300;
    }


    /**
     * 消息是否发送失败
     */
    public boolean isSendFail() {
        return message.status() == TIMMessageStatus.SendFail;
    }

    /**
     * 清除气泡原有数据
     */
    protected void clearView(ChatAdapter.ViewHolder viewHolder) {

        viewHolder.sendStatus.setVisibility(View.VISIBLE);
        viewHolder.leftMessage.setVisibility(View.VISIBLE);
        viewHolder.rightMessage.setVisibility(View.VISIBLE);
        viewHolder.ll_item_custom_message.setVisibility(View.GONE);
        getBubbleView(viewHolder).removeAllViews();
        getBubbleView(viewHolder).setOnClickListener(null);

    }

    /**
     * 显示撤回的消息
     */
    boolean checkRevoke(ChatAdapter.ViewHolder viewHolder) {

        if (message.status() == TIMMessageStatus.HasRevoked) {
            viewHolder.leftPanel.setVisibility(View.GONE);
            viewHolder.rightPanel.setVisibility(View.GONE);
            viewHolder.systemMessage.setVisibility(View.VISIBLE);
            viewHolder.systemMessage.setText(getSummary());
            return true;
        }
        return false;

    }

    /**
     * 获取发送者
     */
    public String getSender() {
        if (message.getSender() == null) {
            return "";
        }
        return message.getSender();
    }


    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }


    private void showDesc(ChatAdapter.ViewHolder viewHolder) {

        if (desc == null || "".equals(desc)) {
            viewHolder.rightDesc.setVisibility(View.GONE);
        } else {
            viewHolder.rightDesc.setVisibility(View.VISIBLE);
            viewHolder.rightDesc.setText(desc);
        }
    }
}
