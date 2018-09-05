package com.aiitec.hiim.base

/**
 * Created by ailibin on 2018/1/22.
 * 各种事物的事件
 */
class Event {


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

        /**
         * 是课程购买还是章节购买 type=1课程购买 type=2章节购买
         */
        var type: Int = 0
    }

    class OnAttentionEvent {

        var tag: String? = null
        var isAttention = -1
        var position = 0
        var open = 0

    }

    /**
     * 消息刷新
     */
    class OnMessageChangeEvent {

        var tag: String? = null

    }

    /**
     * 会话界面滚动的监听
     */
    class OnScrollChangeEvent{

        var tag: String? = null

    }


}