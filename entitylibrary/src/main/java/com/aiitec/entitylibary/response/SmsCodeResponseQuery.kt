package com.aiitec.entitylibary.response

import com.aiitec.openapi.model.ResponseQuery

/**
 * Created by ailibin on 2018/5/15.
 */
class SmsCodeResponseQuery : ResponseQuery() {

    /**
     * 短信验证码验证唯一key
     */
    var smsKey: String = ""

    /**
     *  验证类型 例如 mobile/email type为1才返回
     */
    var loginType: String = ""

    /**
     * 验证码
     */
    var code: Int = 0

    /**
     * （action=1/2/6/9不是必须）返回手机号码，服务器要打*隐藏中间几位
     */
    var mobile: String? = null
}