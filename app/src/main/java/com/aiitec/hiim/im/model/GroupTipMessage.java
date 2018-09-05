package com.aiitec.hiim.im.model;

import android.content.Context;
import android.view.View;

import com.aiitec.hiim.R;
import com.aiitec.hiim.base.App;
import com.aiitec.hiim.im.adapter.ChatAdapter;
import com.tencent.imsdk.TIMGroupMemberInfo;
import com.tencent.imsdk.TIMGroupTipsElem;
import com.tencent.imsdk.TIMMessage;

import java.util.Iterator;
import java.util.Map;

/**
 * 群tips消息
 *
 * @author ailibin
 */
public class GroupTipMessage extends Message {


    public GroupTipMessage(TIMMessage message) {
        this.message = message;
    }


    /**
     * 显示消息
     *
     * @param viewHolder 界面样式
     * @param context    显示消息的上下文
     */
    @Override
    public void showMessage(ChatAdapter.ViewHolder viewHolder, Context context) {
        viewHolder.leftPanel.setVisibility(View.GONE);
        viewHolder.rightPanel.setVisibility(View.GONE);
        viewHolder.systemMessage.setVisibility(View.VISIBLE);
        viewHolder.systemMessage.setText(getSummary());
    }

    /**
     * 获取消息摘要
     */
    @Override
    public String getSummary() {
        final TIMGroupTipsElem e = (TIMGroupTipsElem) message.getElement(0);
        StringBuilder stringBuilder = new StringBuilder();
        Iterator<Map.Entry<String, TIMGroupMemberInfo>> iterator = e.getChangedGroupMemberInfo().entrySet().iterator();
        switch (e.getTipsType()) {
            case CancelAdmin:
            case SetAdmin:
                return App.Companion.getContext().getString(R.string.summary_group_admin_change);
            case Join:
                while (iterator.hasNext()) {
                    Map.Entry<String, TIMGroupMemberInfo> item = iterator.next();
                    stringBuilder.append(getName(item.getValue()));
                    stringBuilder.append(" ");
                }
                return stringBuilder +
                        App.Companion.getContext().getString(R.string.summary_group_mem_add);
            case Kick:
                return e.getUserList().get(0) +
                        App.Companion.getContext().getString(R.string.summary_group_mem_kick);
            case ModifyMemberInfo:
                while (iterator.hasNext()) {
                    Map.Entry<String, TIMGroupMemberInfo> item = iterator.next();
                    stringBuilder.append(getName(item.getValue()));
                    stringBuilder.append(" ");
                }
                return stringBuilder +
                        App.Companion.getContext().getString(R.string.summary_group_mem_modify);
            case Quit:
                return e.getOpUser() +
                        App.Companion.getContext().getString(R.string.summary_group_mem_quit);
            case ModifyGroupInfo:
                return App.Companion.getContext().getString(R.string.summary_group_info_change);
            default:
                break;
        }
        return "";
    }

    /**
     * 保存消息或消息文件
     */
    @Override
    public void save() {

    }

    private String getName(TIMGroupMemberInfo info) {
        if ("".equals(info.getNameCard())) {
            return info.getUser();
        }
        return info.getNameCard();
    }
}
