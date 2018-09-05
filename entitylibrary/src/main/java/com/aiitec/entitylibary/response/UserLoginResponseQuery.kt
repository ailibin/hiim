package com.aiitec.entitylibary.response

import com.aiitec.openapi.model.ResponseQuery

class UserLoginResponseQuery : ResponseQuery() {

    /**
     *是否绑定手机 1 否 2 是 客户端如果发现未绑定，应该跳转到绑定界面提示用户绑定手机号码。
     */
    var isBindMobile: Int = -1
}