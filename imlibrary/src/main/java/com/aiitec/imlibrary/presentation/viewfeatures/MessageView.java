package com.aiitec.imlibrary.presentation.viewfeatures;

/**
 * 消息回调接口
 */
public interface MessageView {


    void onStatusChange(Status newStatus);


    enum Status{
        /**正常*/
        NORMAL,
        /**发送中*/
        SENDING,
        /**错误*/
        ERROR,
    }
}
