package com.aiitec.crocodilesecret.mvp.login

import com.aiitec.letar.mvp.base.IBasePresenter

/**
 * @author ailibin
 * @time 2018/05/03
 * @description 提供回调接口方法的业务逻辑层
 */
interface LoginPresenter : IBasePresenter {

    /**
     * 进行一个登录验证
     */
    fun validateCredentials(username: String, password: String)

    /**
     * 请求发送验证码
     */
    fun requestSendCode(phone: String, type: Int)

    /**
     * @param action=1 普通的登录 action=2第三方登录
     */
    fun requestVerifyCode(code: String, phone: String, smscodeId: Int, type: Int, openId: String, partner: Int)

    /**
     * 请求第三方登录协议
     */
//    fun requestPartnerLogin(user: SimpleUser)

    /**
     * 登录协议和第三方登录协议一起
     */
    fun requestLogin(mobile: String, smsKey: String, openId: String)

}