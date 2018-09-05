package com.aiitec.hiim


/**
 * Created by ailibin on 2018/1/22.
 * 各种事物的事件
 */
class Event {

    /**
     * 接受好友的通知
     */
    class OnReceiveFriendEvent {

        var tag: String? = null

        var friendId: String? = null
    }

    /**
     * 接受好友的通知
     */
    class OnRefreshUserInfoEvent {

        var tag: String? = null

    }

    /**
     * 好友资料变更通知
     */
    class OnChangeFriendProfileEvent {

        var tag: String? = null

    }


    /**
     * 刷新免打扰界面
     */
    class OnRefreshDisturbEvent {

        var tag: String? = null
    }

    /**
     * 发送邀请通知当前用户新的朋友界面更新
     */
    class OnPostInviteEvent {

        var tag: String? = null
    }

    /**
     * 第三方登陆通知
     */
    class OnPartnerLoginEvent {

        var tag: String? = null
    }

    /**
     * 打开界面的通知
     */
    class OnOpenActivityEvent {

        var tag: String? = null
    }

    /**
     * 支付成功的通知
     */
    class OnPaySuccessEvent {

        var tag: String? = null
    }


}