package com.aiitec.crocodilesecret.mvp.login

import android.content.Context

/**
 * @author ailibin
 * @time 2018/05/03
 * @decription 逻辑业务处理中心(控制view和model交互)代理类
 */
class LoginPresenterImpl(loginView: ILoginView?, loginInteractor: ILoginInteractor?, context: Context) : LoginPresenter, ILoginInteractor.OnLoginCallbackListener {

    private var loginView: ILoginView? = null
    private var loginInteractor: ILoginInteractor? = null
    private var context: Context? = null

    init {
        this.loginView = loginView
        this.loginInteractor = loginInteractor
        this.context = context
    }

    /**
     */
    override fun requestSendCode(phone: String, type: Int) {
        loginInteractor?.requestSendCode(phone, this, context!!, type)
    }

    override fun requestVerifyCode(code: String, phone: String, smscodeId: Int, type: Int, openId: String, partner: Int) {
        loginInteractor?.requestVerifyCode(code, phone, smscodeId, this, context!!, type, openId, partner)
    }


    /**
     * 登录协议
     */
    override fun requestLogin(mobile: String, smsKey: String, openId: String) {
        loginInteractor?.requestLogin(mobile, smsKey, openId, this, context!!)
    }


    override fun validateCredentials(username: String, password: String) {
//        loginInteractor.login(username, password, this, context!!)
    }

    /**
     * 界面消失掉之后,对象引用置为空
     */
    override fun onDestroy() {
        loginView = null
    }


    /**
     * 发送验证码成功的回调(响应方法)
     */
    override fun onSendCodeSuccResponse(smscodeId: Int) {
        loginView?.onSendCodeSuccResponse(smscodeId)
        loginView?.hideProgress()
    }

    override fun onBindError(content: String) {
        loginView?.onBindError(content)
    }


    /**
     * 验证码有误
     */
    override fun onCodeErrorResponse(content: String) {
        loginView?.onCodeErrorResponse(content)
        loginView?.hideProgress()
    }

    override fun onGetImSuccResponse() {
        loginView?.onGetImSuccResponse()
        loginView?.hideProgress()
    }

    override fun onGetImFailResponse() {
        loginView?.onGetImFailResponse()
        loginView?.hideProgress()
    }

    override fun onUserDetailFailResponse() {
        loginView?.onUserDetailFailResponse()
        loginView?.hideProgress()
    }


    override fun onStart() {
        loginView?.showProgress()
    }


    override fun onFinish() {
        loginView?.hideProgress()
    }

    /**
     * 服务端成功的回调
     */
    override fun onSuccess() {
        loginView?.hideProgress()
    }

    /**
     * 服务端失败的回调
     */
    override fun onFail() {
        loginView?.hideProgress()
    }

}