package com.aiitec.hiim.ui.login

import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import com.aiitec.hiim.R
import com.aiitec.hiim.annotation.ContentView
import com.aiitec.hiim.base.BaseKtActivity
import com.aiitec.hiim.im.business.ImLoginBusiness
import com.aiitec.hiim.im.model.UserInfo
import kotlinx.android.synthetic.main.activity_login.*

/**
 * 登录页, 逻辑后面再实现
 * @author ailibin
 *
 */
@ContentView(R.layout.activity_login)
class LoginActivity : BaseKtActivity() {

    override fun init(savedInstanceState: Bundle?) {
        initBottomView()
        btn_login.setOnClickListener {
            //登录IM
            UserInfo.getInstance().id = "give"
            UserInfo.getInstance().userSig = "eJx1kE9rgzAYh*9*ipCrY6vR0FjYQYbV4tYepqPrJYiJ9sVWg8nsn7HvPnGFetl7fR54frzfFkIIp6-vj3lRtF*N4eaiJEYLhOc*wQ93rBQInhvudmLEjjcbzqGuO7HkWUEneV4a2Y0WoT4ZtIkCQjYGSrgJFfRyQrWo*Zj6v6GhGuFbmL2s4s842y31ZpnZuVfv0r7ain1Rt0HEkrJf252T2EqvIxO3AYSBd9Vbu*xJfQaWPrH58SCSSDL3cPI9f-Oxj4Kkai7OSmfh8yRp4Pj3k2EKpYQxQrH1Y-0CAEpWWg__"
            ImLoginBusiness.getInstance().navToHome(this)
//            switchToActivity(MainActivity::class.java)
        }
    }

    /**
     * 初始化底部用户协议的富文本
     */
    private fun initBottomView() {

        val ttt = "《用户注册协议》"
        val sss = "《隐私政策》"
        val spanUserAgreement = SpannableString(ttt)
        val spanUserPrivacy = SpannableString(sss)
        val clickUserAgreement = MyClickbleSpan(ttt, this, 1)
        val clickUserPrivacy = MyClickbleSpan(sss, this, 2)
        spanUserAgreement.setSpan(clickUserAgreement, 0, ttt.length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
        spanUserPrivacy.setSpan(clickUserPrivacy, 0, sss.length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
        tv_user_agreement.text = "注册即代表您已阅读并同意"
        tv_user_agreement.append(spanUserAgreement)
        tv_user_agreement.append("和")
        tv_user_agreement.append(spanUserPrivacy)
        tv_user_agreement.movementMethod = LinkMovementMethod.getInstance()

    }

}
