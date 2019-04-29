package com.aiitec.hiim.ui.login

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import com.aiitec.hiim.R
import com.aiitec.hiim.annotation.ContentView
import com.aiitec.hiim.base.BaseKtActivity
import com.aiitec.hiim.im.utils.LogUtil
import com.aiitec.hiim.utils.SmscodeCountDown
import kotlinx.android.synthetic.main.include_login_top.*

/**
 * Created by ailibin on 2018/5/4.
 * 第三方登录绑定手机号码
 */
@ContentView(R.layout.activity_for_bind_phone)
class BindPhoneActivity : BaseKtActivity(),View.OnClickListener {

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

    override fun onClick(view: View?) {
        //开发阶段直接跳首页
        when (view?.id) {
            R.id.tv_verify_and_register_for_register -> {
                //进入首页
                val code = et_sms_code_for_register.text.toString().trim()
                val mobile = et_phone_for_register.text.toString().trim()
            }
            R.id.tv_to_send_sms_code_for_register -> {
                //获取验证码按钮
                val mobile = et_phone_for_register.text.toString().trim()
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        isSend = false
    }
}