package com.aiitec.entitylibary.request

import com.aiitec.openapi.model.BaseWhere
import com.aiitec.openapi.model.RequestQuery

/**
 * Created by ailibin on 2018/5/3.
 */
class SmsCodeRequestQuery : RequestQuery() {

    //action:	1	是	1 获取验证码  2 验证验证码
    /**
     * 1 登录;2 第三方登录;3 修改手机验证旧手机;4 修改手机验证新手机;5 教师注册
     */
//    var type = 1
//    /**
//     * 手机号码
//     */
//    var mobile: String? = null
//    /**
//     * action=2 时需要
//     */
//    var code: String? = null

    /**@type
     * 1注册（需验证手机号是否存在)
     * 2忘记密码/重置密码
     * 3重新绑定手机验证新手机
     * 4邮箱验证
     * 5重新绑定手机时验证旧手机
     */
    var type: Int = 0

    var mobile: String? = null

    var smscodeId = -1

    var where: BaseWhere? = null

    var code: String? = null

}