package com.aiitec.crocodilesecret.mvp.login

import android.content.Context
import com.aiitec.letar.mvp.base.IBaseCallbackListener

/**
 * @author ailibin
 * @time 2018/5/3
 * @description 登录交互器
 *
 */
interface ILoginInteractor {

    interface OnLoginCallbackListener : IBaseCallbackListener {

        /**
         * 发送验证码成功回调
         */
        fun onSendCodeSuccResponse(id: Int)

        /**
         * 验证码有误
         */
        fun onCodeErrorResponse(content: String)

        /**
         * 获取用户im账号成功
         */
        fun onGetImSuccResponse()

        /**
         * 获取用户im账号失败
         */
        fun onGetImFailResponse()

        /**
         * 请求用户详情失败
         */
        fun onUserDetailFailResponse()

        /**
         * 绑定用户信息失败
         */
        fun onBindError(content: String)

    }

    /**
     * 这里还是要传一个上下文
     */
    fun requestLogin(mobile: String, smsKey: String, openId: String, listener: OnLoginCallbackListener, context: Context)

    /**
     * 发送验证码 type=1登录和注册  type=9第三方登录
     */
    fun requestSendCode(phone: String, listener: OnLoginCallbackListener, context: Context, type: Int)

    /**
     * action=1 普通登录验证码  action=2第三方登录验证码
     */
    fun requestVerifyCode(code: String, phone: String, smscodeId: Int, listener: OnLoginCallbackListener,
                          context: Context, type: Int, openId: String, partner: Int)

}