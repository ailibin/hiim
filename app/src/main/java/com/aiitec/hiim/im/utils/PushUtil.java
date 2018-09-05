package com.aiitec.hiim.im.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import com.aiitec.imlibrary.presentation.event.MessageEvent;
import com.aiitec.hiim.R;
import com.aiitec.hiim.base.App;
import com.aiitec.hiim.im.chat.ChatActivity;
import com.aiitec.hiim.im.chat.NewFriendActivity;
import com.aiitec.hiim.im.model.CustomMessage;
import com.aiitec.hiim.im.model.Message;
import com.aiitec.hiim.im.model.MessageFactory;
import com.aiitec.hiim.utils.BaseUtil;
import com.aiitec.openapi.utils.LogUtil;
import com.tencent.imsdk.TIMConversationType;
import com.tencent.imsdk.TIMFriendshipManager;
import com.tencent.imsdk.TIMGroupReceiveMessageOpt;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.TIMUserProfile;
import com.tencent.imsdk.TIMValueCallBack;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * 在线消息通知展示
 *
 * @author anthony
 */
public class PushUtil implements Observer {

    private static final String TAG = PushUtil.class.getSimpleName();

    private static int pushNum = 0;

    private final int messagePushId = 1;
    private final int friendPushId = 12;
    private String senderName;

    private static PushUtil instance = new PushUtil();

    private PushUtil() {
        MessageEvent.getInstance().addObserver(this);
    }

    public static PushUtil getInstance() {
        return instance;
    }


    private void PushNotify(final TIMMessage msg) {
        //系统消息，自己发的消息，程序在前台的时候不通知
        /*Foreground.get().isForeground()||*/

        //如果用户设置了不接受消息,那么不能通知了
//        boolean isReceiveMessage = AiiUtil.getBoolean(App.Companion.getContext(), Constant.CHAT_MESSAGE_REMIND_SETTING, true);
//        if (!isReceiveMessage) {
//            return;
//        }

        if (msg == null) {
            return;
        }
        if (/*msg.getConversation().getType()!= TIMConversationType.Group&&*/
                msg.getConversation().getType() != TIMConversationType.C2C) {
            return;
        }
        if (msg.isSelf() || msg.getRecvFlag() == TIMGroupReceiveMessageOpt.ReceiveNotNotify ||
                MessageFactory.getMessage(msg) instanceof CustomMessage) {
            return;
        }

        final String senderStr, contentStr;
        final Message message = MessageFactory.getMessage(msg);
        if (message == null) {
            return;
        }
        senderStr = message.getSender();
        contentStr = message.getSummary();
        if (BaseUtil.isAllNum(senderStr) || senderStr.startsWith("IM")) {

            //那么从存储的地方获取昵称
//            senderStr = AiiUtil.getString(App.getContext(), "nickname_" + senderStr, senderStr);
            List<String> identifiers = new ArrayList<>();
            identifiers.add(senderStr);
            //users这里表示要获取资料的用户identifier列表
            TIMFriendshipManager.getInstance().getUsersProfile(identifiers, new TIMValueCallBack<List<TIMUserProfile>>() {
                @Override
                public void onError(int code, String desc) {
                    setNotificationView(senderStr, contentStr, msg);
                }

                @Override
                public void onSuccess(List<TIMUserProfile> result) {
                    if (TextUtils.isEmpty(result.get(0).getNickName())) {
                        senderName = message.getSender();
                    } else {
                        senderName = result.get(0).getNickName();
                    }
                    setNotificationView(senderName, contentStr, msg);
                }
            });
        } else {
            setNotificationView(senderStr, contentStr, msg);
        }
    }

    /**
     * 设置推送的内容
     *
     * @param nickName
     * @param content
     * @param msg
     */
    public void setNotificationView(String nickName, String content, TIMMessage msg) {
        NotificationManager mNotificationManager = (NotificationManager) App.Companion.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(App.Companion.getContext());
        Intent notificationIntent = new Intent(App.Companion.getContext(), ChatActivity.class);
        notificationIntent.putExtra("identify", msg.getSender());
        notificationIntent.putExtra("type", TIMConversationType.C2C);
        notificationIntent.putExtra("nickname", nickName);
        LogUtil.d("setNotificationView--identify:" + msg.getSender());
        LogUtil.d("setNotificationView--identify:" + nickName);

        //这里是chatActivity的singTask惹得祸,可以把ChatActivity的启动模式设置成standard
//        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
//                | Intent.FLAG_ACTIVITY_SINGLE_TOP);

//        PendingIntent intent = PendingIntent.getActivity(App.getContext(), 0,
//                notificationIntent, 0);

        //最新的一条记录
        PendingIntent intent = PendingIntent.getActivity(App.Companion.getContext(), 0,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        //每次发intent都是最新的一个intent,没有历史记录..
//        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //设置通知栏标题
        mBuilder.setContentTitle(nickName)
                .setContentText(content)
                .setContentIntent(intent) //设置通知栏点击意图
//                .setNumber(++pushNum) //设置通知集合的数量
                .setTicker(nickName + ":" + content) //通知首次出现在通知栏，带上升动画效果的
                .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
                .setDefaults(Notification.DEFAULT_ALL)//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合
                .setSmallIcon(R.mipmap.ic_launcher);//设置通知小ICON

        //这里用户在软件中设置了声音和振动,就不能全部弄成默认的了
        Notification notify = mBuilder.build();
        //先根据用户系统的设置,然后再根据软件的设置通知来到时是否振动和响铃
//        SystemUtil.setAlarmParams(notify);
        notify.flags |= Notification.FLAG_AUTO_CANCEL;
        mNotificationManager.notify(friendPushId, notify);
    }

    public void pushNotify(String title, String content) {
        NotificationManager mNotificationManager = (NotificationManager) App.Companion.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(App.Companion.getContext());
        Intent notificationIntent = new Intent(App.Companion.getContext(), NewFriendActivity.class);
//        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
//                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//        PendingIntent intent = PendingIntent.getActivity(App.Companion.getContext(), 0,
//                notificationIntent, 0);

        //最新的一条记录(只有这样做界面才会刷新)
        PendingIntent intent = PendingIntent.getActivity(App.Companion.getContext(), 0,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        mBuilder.setContentTitle(title)//设置通知栏标题
                .setContentText(content)
                .setContentIntent(intent) //设置通知栏点击意图
//                .setNumber(++pushNum) //设置通知集合的数量
                .setTicker(content) //通知首次出现在通知栏，带上升动画效果的
                .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
                .setDefaults(Notification.DEFAULT_ALL)//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合
                .setSmallIcon(R.mipmap.ic_launcher);//设置通知小ICON
        Notification notify = mBuilder.build();
        //先根据用户系统的设置,然后再根据软件的设置通知来到时是否振动和响铃
//        SystemUtil.setAlarmParams(notify);
        notify.flags |= Notification.FLAG_AUTO_CANCEL;
        mNotificationManager.notify(messagePushId, notify);
    }

    public static void resetPushNum() {
        pushNum = 0;
    }

    public void reset() {
        reset(messagePushId);
    }

    public void reset(int pushId) {
        NotificationManager notificationManager = (NotificationManager) App.Companion.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(pushId);
    }

    /**
     * This method is called if the specified {@code Observable} object's
     * {@code notifyObservers} method is called (because the {@code Observable}
     * object has been updated.
     *
     * @param observable the {@link Observable} object.
     * @param data       the data passed to {@link Observable#notifyObservers(Object)}.
     */
    @Override
    public void update(Observable observable, Object data) {
        if (observable instanceof MessageEvent) {
            if (data instanceof TIMMessage) {
                TIMMessage msg = (TIMMessage) data;
                if (msg != null) {
                    PushNotify(msg);
                }
            }
        }
    }
}
