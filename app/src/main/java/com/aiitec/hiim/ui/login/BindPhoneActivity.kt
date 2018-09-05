package com.aiitec.hiim.ui.login

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import com.aiitec.crocodilesecret.mvp.login.*
import com.aiitec.hiim.R
import com.aiitec.hiim.annotation.ContentView
import com.aiitec.hiim.base.BaseKtActivity
import com.aiitec.hiim.ui.MainActivity
import com.aiitec.hiim.utils.BaseUtil
import com.aiitec.hiim.utils.SmscodeCountDown
import com.aiitec.openapi.utils.LogUtil
import kotlinx.android.synthetic.main.include_login_top.*

/**
 * Created by ailibin on 2018/5/4.
 * 第三方登录绑定手机号码
 */
@ContentView(R.layout.activity_for_bind_phone)
class BindPhoneActivity : BaseKtActivity(), ILoginView, View.OnClickListener {

    private var loginPresenter: LoginPresenter? = null
    private lateinit var smsCodeCountDown: SmscodeCountDown
    private var smscodeId = 0
    private var openId: String? = null
    private var partner: Int = -1
    private var isSend = false

    /**
     * 输入框字符变化的监听
     */
    private fun textChangeListener(etView: EditText) {
        etView.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                tv_verify_and_register_for_register.isEnabled = verifyCompleteBtnCanClick()
                tv_verify_and_register_for_register.setTextColor(resources.getColor(R.color.white))
            }
        })
    }


    override fun init(savedInstanceState: Bundle?) {

        openId = intent.getStringExtra("openid")
        partner = intent.getIntExtra("partner", 0)
        LogUtil.d("ailibin", "openid***: " + openId)
        addBaseStatusBarView()
        setColumnTitle("绑定手机")
        setLeftIconVisible(false)
        //初始化presenter
        loginPresenter = LoginPresenterImpl(this, LoginInteractorImpl(), this)
        initData()
        textChangeListener(et_phone_for_register)
        textChangeListener(et_sms_code_for_register)
        tv_verify_and_register_for_register.setOnClickListener(this)
        tv_to_send_sms_code_for_register.setOnClickListener(this)
    }

    private fun verifyCompleteBtnCanClick(): Boolean {
        val newPhoneNumberStr = et_phone_for_register.text.toString()
        val smsCodeStr = et_sms_code_for_register.text.toString()
        val hasNewPhoneNumber = !TextUtils.isEmpty(newPhoneNumberStr)
        val hasSmsCode = !TextUtils.isEmpty(smsCodeStr)
        return hasNewPhoneNumber && hasSmsCode
    }


    private fun initData() {
        smsCodeCountDown = SmscodeCountDown(60 * 1000, 1000)
        smsCodeCountDown.setSmscodeBtn(tv_to_send_sms_code_for_register)
    }

    /**
     * 发送验证码成功的回调
     */
    override fun onSendCodeSuccResponse(smscodeId: Int) {
        this.smscodeId = smscodeId
        BaseUtil.showToast("验证码发送成功")
        try {
            smsCodeCountDown.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onCodeErrorResponse(content: String) {
        //验证验证码出现问题,获取验证码按钮要重新刷新
        BaseUtil.showToast(content)
        smsCodeCountDown.resetSmsCode()
    }

    override fun onBindError(content: String) {
        smsCodeCountDown.resetSmsCode()
    }


    override fun showProgress() {
        //加载进度条
        progressDialogShow()
    }

    override fun hideProgress() {
        //隐藏进度条
        progressDialogDismiss()
    }


    /**
     * 请求成功
     */
    override fun navigateToHomeResponse() {
        //跳转到首页
        switchToActivity(MainActivity::class.java)
        progressDialogDismiss()
    }

    override fun onGetImSuccResponse() {
        navigateToHomeResponse()
    }

    override fun onGetImFailResponse() {
        switchToActivity(LoginActivity::class.java)
    }

    override fun onUserDetailFailResponse() {
        switchToActivity(LoginActivity::class.java)
    }


    override fun onClick(view: View?) {
        //开发阶段直接跳首页
        when (view?.id) {
            R.id.tv_verify_and_register_for_register -> {
                //进入首页
                val code = et_sms_code_for_register.text.toString().trim()
                val mobile = et_phone_for_register.text.toString().trim()
                val isPass = LoginHelper.verifyPhoneAndCode(mobile, code)
                if (isPass) {
                    //第三方登录绑定
                    loginPresenter?.requestVerifyCode(code, mobile, smscodeId, 2, openId!!, partner)
                }
            }
            R.id.tv_to_send_sms_code_for_register -> {
                //获取验证码按钮
                val mobile = et_phone_for_register.text.toString().trim()
                val isPass = LoginHelper.verifyPhone(mobile)
                if (isPass) {
                    //type=9第三方绑定
                    loginPresenter?.requestSendCode(mobile, 2)
                }
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        isSend = false
        loginPresenter?.onDestroy()
    }
}