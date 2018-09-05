package com.aiitec.crocodilesecret.mvp.login

import com.aiitec.letar.mvp.base.IBaseView

/**
 * @author ailibin
 * @time 2018/05/03
 * @description 主要提供和activity等界面交互的方法的接口
 *
 */
interface ILoginView : IBaseView {

    /**
     * 验证通过后,跳转到首页界面
     */
    fun navigateToHomeResponse()

    /**
     * 发送验证码成功的回调
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

    fun onBindError(content: String)


}