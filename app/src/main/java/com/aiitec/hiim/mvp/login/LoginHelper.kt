package com.aiitec.crocodilesecret.mvp.login

import android.text.TextUtils
import com.aiitec.hiim.utils.BaseUtil
import com.aiitec.hiim.utils.VerifyUtil

/**
 * @author ailibin
 * @time 2018/5/4
 */
class LoginHelper {

    companion object {
        fun verifyPhone(phone: String): Boolean {

            if (TextUtils.isEmpty(phone)) {
                BaseUtil.showToast("请先输入手机号码")
                return false
            }

            if (!VerifyUtil.isMobileNO(phone)) {
                BaseUtil.showToast("您输入的手机号码不合法，请重新输入")
                return false
            }

            //这里不验证格式了,手机号格式经常变化,很难固定,只要验证11位就好了
            if (phone.length != 11) {
                BaseUtil.showToast("您输入的手机号码位数不对，请重新输入")
                return false
            }
            return true
        }

        fun verifyPhoneAndCode(phone: String, code: String): Boolean {

            //这里不验证格式了,手机号格式经常变化,很难固定,只要验证11位就好了
            if (phone.length != 11) {
                BaseUtil.showToast("您输入的手机号码位数不对，请重新输入")
                return false
            }

            if (!VerifyUtil.isMobileNO(phone)) {
                BaseUtil.showToast("您输入的手机号码不合法，请重新输入")
                return false
            }

            if (code.length < 4) {
                BaseUtil.showToast("您输入的验证码有误，请重新输入")
                return false
            }
            return true
        }
    }
}