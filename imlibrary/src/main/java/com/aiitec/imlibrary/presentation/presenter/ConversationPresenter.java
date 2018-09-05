package com.aiitec.imlibrary.presentation.presenter;

import android.util.Log;

import com.aiitec.imlibrary.presentation.event.FriendshipEvent;
import com.aiitec.imlibrary.presentation.event.GroupEvent;
import com.aiitec.imlibrary.presentation.event.MessageEvent;
import com.aiitec.imlibrary.presentation.event.RefreshEvent;
import com.aiitec.imlibrary.presentation.viewfeatures.ConversationView;
import com.tencent.imsdk.TIMCallBack;
import com.tencent.imsdk.TIMConversation;
import com.tencent.imsdk.TIMConversationType;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.TIMValueCallBack;
import com.tencent.imsdk.ext.group.TIMGroupCacheInfo;
import com.tencent.imsdk.ext.message.TIMConversationExt;
import com.tencent.imsdk.ext.message.TIMManagerExt;
import com.tencent.imsdk.ext.message.TIMMessageExt;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * 会话界面逻辑
 */
public class ConversationPresenter implements Observer {

    private static final String TAG = "ConversationPresenter";
    private ConversationView view;
//    private List<TIMMessage> mTimMessages = new ArrayList<>();

    public ConversationPresenter(ConversationView view) {
        //注册消息监听
        MessageEvent.getInstance().addObserver(this);
        //注册刷新监听
        RefreshEvent.getInstance().addObserver(this);
        //注册好友关系链监听
        FriendshipEvent.getInstance().addObserver(this);
        //注册群关系监听
        GroupEvent.getInstance().addObserver(this);
        this.view = view;
    }

    @Override
    public void update(Observable observable, Object data) {
        if (observable instanceof MessageEvent) {
            if (data instanceof TIMMessage) {
                TIMMessage msg = (TIMMessage) data;
                view.updateMessage(msg);
            }
        } else if (observable instanceof FriendshipEvent) {
            FriendshipEvent.NotifyCmd cmd = (FriendshipEvent.NotifyCmd) data;
            switch (cmd.type) {
                case ADD_REQ:
                case READ_MSG:
                case ADD:
                    view.updateFriendshipMessage();
                    break;
                default:
                    break;
            }
        } else if (observable instanceof GroupEvent) {
            GroupEvent.NotifyCmd cmd = (GroupEvent.NotifyCmd) data;
            switch (cmd.type) {
                case UPDATE:
                case ADD:
                    view.updateGroupInfo((TIMGroupCacheInfo) cmd.data);
                    break;
                case DEL:
                    view.removeConversation((String) cmd.data);
                    break;
                default:
                    break;
            }
        } else if (observable instanceof RefreshEvent) {
            view.refresh();
        }
    }


    /**
     * 获取会话,判断是否打开系统消息开关
     *
     */
    public void getConversation() {
        List<TIMConversation> list = TIMManagerExt.getInstance().getConversationList();
        List<TIMConversation> result = new ArrayList<>();
        for (TIMConversation conversation : list) {
            //这里用户通过开关设置是否显示系统消息
            if (conversation.getType() == TIMConversationType.System) {
                continue;
            }
            result.add(conversation);
            TIMConversationExt conversationExt = new TIMConversationExt(conversation);
            conversationExt.getMessage(1, null, new TIMValueCallBack<List<TIMMessage>>() {
                @Override
                public void onError(int i, String s) {
                    Log.e(TAG, "get message error" + s);
                }

                @Override
                public void onSuccess(List<TIMMessage> timMessages) {
                    if (timMessages.size() > 0) {
                        view.updateMessage(timMessages.get(0));
                    }
                }
            });
        }
        view.initView(result);
    }


    /**
     * 更新最后一条信息(清空聊天记录成功后需要更新会话界面)
     */
    public void updateLastConversation(String identify) {
        List<TIMConversation> list = TIMManagerExt.getInstance().getConversationList();
        List<TIMConversation> result = new ArrayList<>();
        for (TIMConversation conversation : list) {
            if (conversation.getType() == TIMConversationType.System) {
                continue;
            } else if (conversation.getPeer().equals(identify)) {
                result.add(conversation);
                return;
            } else {
                continue;
            }
        }
        TIMConversationExt conversationExt = new TIMConversationExt(result.get(0));
        conversationExt.getMessage(1, null, new TIMValueCallBack<List<TIMMessage>>() {
            @Override
            public void onError(int i, String s) {
                Log.e(TAG, "get message error" + s);
            }

            @Override
            public void onSuccess(List<TIMMessage> timMessages) {
                if (timMessages.size() > 0) {
                    view.updateMessage(timMessages.get(0));
                } else {
                    view.updateMessage(null);
                }
            }
        });
        view.initView(result);
    }

    /**
     * 删除会话
     *
     * @param type 会话类型
     * @param id   会话对象id
     */
    public boolean delConversation(TIMConversationType type, String id) {
        return TIMManagerExt.getInstance().deleteConversationAndLocalMsgs(type, id);
    }

    /**
     * 删除某个会话下的所有的腾讯云的远程的聊天记录
     *
     * @return
     */
    public void delConversationMessages(String identify) {
        List<TIMConversation> list = TIMManagerExt.getInstance().getConversationList();
        TIMConversation targetConversation = new TIMConversation();
        //查找到会话下所有的消息之后,加入到当前的集合中去
        final List<TIMMessage> mTimMessages = new ArrayList<>();
        for (TIMConversation conversation : list) {
            if (conversation.getPeer().equals(identify)) {
                targetConversation = conversation;
                Log.e("ailibin", "conversation.getPeer(): " + conversation.getPeer());
            } else {
                continue;
            }
        }
        TIMConversationExt conversationExt = new TIMConversationExt(targetConversation);
        //i :获取此会话最近的多少条消息  timMessage:  null,不指定从哪条消息开始获取 - 等同于从最新的消息开始往前
        conversationExt.getMessage(10, null, new TIMValueCallBack<List<TIMMessage>>() {
            @Override
            public void onError(int i, String s) {
                Log.e("ailibin", "get message error" + s);
            }

            @Override
            public void onSuccess(List<TIMMessage> timMessages) {
                if (timMessages.size() > 0) {
                    for (TIMMessage timMessage : timMessages) {
                        TIMMessageExt ext = new TIMMessageExt(timMessage);
                        ext.remove();
                    }
                    Log.e("ailibin", "get message success");
                }
            }
        });
    }

    /**
     * 根据某个会话id删除本会话下的所有消息
     *
     * @param identify 会话唯一标示(这里会报6019错误,数据库查找错误)
     * @author ailibin
     */
    public void clearLocalMessage(String identify) {
        List<TIMConversation> list = TIMManagerExt.getInstance().getConversationList();
        TIMConversation targetConversation = new TIMConversation();
        for (TIMConversation conversation : list) {
            if (conversation.getPeer().equals(identify)) {
                targetConversation = conversation;
            } else {
                continue;
            }
        }
        TIMConversationExt conversationExt = new TIMConversationExt(targetConversation);
        conversationExt.deleteLocalMessage(new TIMCallBack() {
            @Override
            public void onError(int i, String s) {
                Log.e("ailibin", "code: " + i + " message: " + s.toString());
            }

            @Override
            public void onSuccess() {
                Log.e("ailibin", "删除本地会话消息成功");
            }
        });
    }


//    /**
//     * 查找漫游下某个会话下的所有消息记录
//     *
//     * @param identify 会话的唯一标示
//     * @param count    消息总数
//     */
//    public List<TIMMessage> findConversationMessages(String identify, int count) {
//        List<TIMConversation> list = TIMManagerExt.getInstance().getConversationList();
////        final List<TIMMessage> mTimMessages = new ArrayList<>();
//        TIMConversation targetConversation = new TIMConversation();
//        for (TIMConversation conversation : list) {
//            if (conversation.getPeer().equals(identify)) {
//                targetConversation = conversation;
//                Log.e("ailibin", "conversation.getPeer(): " + conversation.getPeer());
//            } else {
//                continue;
//            }
//        }
//        TIMConversationExt conversationExt = new TIMConversationExt(targetConversation);
//        //这里的i参数需要传消息总数,不能是最近一条的数量
//        conversationExt.getMessage(count, null, new TIMValueCallBack<List<TIMMessage>>() {
//            @Override
//            public void onError(int i, String s) {
//                Log.e(TAG, "get message error" + s);
//            }
//
//            @Override
//            public void onSuccess(List<TIMMessage> timMessages) {
//                if (timMessages.size() > 0) {
//                    mTimMessages.clear();
//                    mTimMessages.addAll(timMessages);
//
//                }
//                for (TIMMessage timMessage : mTimMessages) {
//                    Log.e("ailibin", "sender: " + timMessage.getSender() + " msg: " + timMessage.getMsg().toString()+" timestamp: "+timMessage.timestamp());
//                }
//                Log.e("ailibin", "size: " + mTimMessages.size());
//            }
//        });
//        return mTimMessages;
//    }

}
