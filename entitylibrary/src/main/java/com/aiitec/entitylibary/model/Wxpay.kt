package com.aiitec.entitylibary.model

import com.aiitec.openapi.model.Entity

/**
 * Created by ailibin on 2018/5/11.
 */
class Wxpay : Entity() {

    //    var retcode = -1
    //这里后台不知道为什么返回的是string类型
    var retcode: String? = null
    var retmsg: String? = null
    var appid: String? = null
    var noncestr: String? = null
    var packager: String? = null
    var partnerid: String? = null
    var prepayid: String? = null
    var sign: String? = null
    var timestamp: String? = null
}