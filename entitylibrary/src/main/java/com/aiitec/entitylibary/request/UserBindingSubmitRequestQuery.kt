package com.aiitec.entitylibary.request

import com.aiitec.openapi.model.RequestQuery

/**
 * Created by ailibin on 2018/5/23.
 * 用户第三方绑定
 */
class UserBindingSubmitRequestQuery : RequestQuery() {

    /**
     * 手机号码
     */
    var mobile: String = ""
    /**
     * 短信验证key
     */
    var checkKey: String = ""
    /**
     * 11 QQ；12QQ空间；13微信APP；14新浪微博；15人人网；16支付宝；17微信公众号
     */
    var partner: Int = 0
    /**
     * 第三方用户唯一标识
     */
    var openId: String = ""
    /**
     *用户昵称
     */
    var name: String = ""
    /**
     * 头像
     */
    var imageUrl: String = ""
}