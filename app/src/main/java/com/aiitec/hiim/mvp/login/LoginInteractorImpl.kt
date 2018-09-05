package com.aiitec.crocodilesecret.mvp.login

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.text.TextUtils
import com.aiitec.entitylibary.request.SmsCodeRequestQuery
import com.aiitec.entitylibary.request.UserDetailsRequestQuery
import com.aiitec.entitylibary.request.UserLoginRequestQuery
import com.aiitec.entitylibary.response.SmsCodeResponseQuery
import com.aiitec.entitylibary.response.UserDetailsResponseQuery
import com.aiitec.entitylibary.response.UserLoginResponseQuery
import com.aiitec.hiim.base.App
import com.aiitec.hiim.base.Constants
import com.aiitec.hiim.ui.MainActivity
import com.aiitec.hiim.ui.login.BindPhoneActivity
import com.aiitec.openapi.constant.AIIConstant
import com.aiitec.openapi.json.JSON
import com.aiitec.openapi.json.enums.AIIAction
import com.aiitec.openapi.model.ResponseQuery
import com.aiitec.openapi.net.AIIResponse
import com.aiitec.openapi.utils.AiiUtil
import com.aiitec.openapi.utils.LogUtil
import com.umeng.analytics.MobclickAgent

/**
 * @author ailbin
 * @time 2018/5/3.
 * @description 交互器处理中心,主要处理网络返回的数据
 *
 */
class LoginInteractorImpl : ILoginInteractor {

    override fun requestSendCode(phone: String, listener: ILoginInteractor.OnLoginCallbackListener, context: Context, type: Int) {
        val query = SmsCodeRequestQuery()
        //发送验证码
        query.action = AIIAction.ONE
        //登录
        query.type = type
        query.mobile = phone
        query.setDir("Base")
        listener?.onStart()
        LogUtil.d("ailibin", "phone: $phone")
        App.aiiRequest?.send(query, object : AIIResponse<SmsCodeResponseQuery>(context, false) {
            override fun onSuccess(response: SmsCodeResponseQuery?, index: Int) {
                super.onSuccess(response, index)
                response?.id.let {
                    listener.onSendCodeSuccResponse(it!!.toInt())
                }
            }

            override fun onFinish(index: Int) {
                super.onFinish(index)
                listener.onFinish()
            }

            override fun onServiceError(content: String?, status: Int, index: Int) {
                super.onServiceError(content, status, index)
                listener.onFail()
            }
        })
    }

    /**
     * 这里是php的后台
     */
    override fun requestVerifyCode(code: String, phone: String, smscodeId: Int,
                                   listener: ILoginInteractor.OnLoginCallbackListener
                                   , context: Context, type: Int, openId: String, partner: Int) {
        val query = SmsCodeRequestQuery()
        //验证验证码
        query.action = AIIAction.TWO
        query.type = type//登录
        query.mobile = phone
        query.smscodeId = smscodeId
        query.code = code
        query.setDir("Base")
        App.aiiRequest?.send(query, object : AIIResponse<SmsCodeResponseQuery>(context, false) {
            override fun onSuccess(response: SmsCodeResponseQuery?, index: Int) {
                super.onSuccess(response, index)
                when (type) {
                    1 -> {
                        //普通的登录,这里还要请求一次登录协议
                        response?.smsKey.let {
                            requestLogin(phone, it!!, "", listener, context)
                        }
                    }
                    2 -> {
                        //第三方登录绑定用户信息
//                        requestLogin(phone, "", openId, listener, context)
                        //这里不用请求登录协议了,这里还要请求修改绑定手机号码协议
                        response?.smsKey.let {
                            requestUserChangeBind(phone, it!!, listener, context)
                        }

//                        requestUserBindingSubmit(checkKey, phone, listener, context, openId, partner)
                    }
                    3 -> {
                        //修改手机验证旧手机,这里跳转到验证新手机号码界面
                    }
                    4 -> {
                        // 修改手机验证新手机,验证通过,完成修改手机操作,跳转到指定的界面
                    }
                }

            }

            override fun onServiceError(content: String?, status: Int, index: Int) {
                super.onServiceError(content, status, index)
                //这里要显示验证码有误的ui
                if (!TextUtils.isEmpty(content)) {
                    listener?.onCodeErrorResponse(content!!)
                }
            }

            override fun onFailure(content: String?, index: Int) {
                super.onFailure(content, index)
                listener?.onFail()
            }
        })
    }

    fun requestUserChangeBind(mobile: String, smsKey: String, listener: ILoginInteractor.OnLoginCallbackListener, context: Context) {

        val query = UserLoginRequestQuery()
        query.setNamespace("UserChangeBind")
        query.setDir("Cis")
//        query.code = openId
        query.smsKey = smsKey
        query.mobile = mobile
        query.action = AIIAction.ONE

        App.aiiRequest?.send(query, object : AIIResponse<ResponseQuery>(context, false) {
            override fun onSuccess(response: ResponseQuery?, index: Int) {
                super.onSuccess(response, index)
                //跳转到首页,普通的登录直接跳转首页,如果是第三方登录就判断当前用户是否已经绑定微信,如果没有跳绑定页面,否则就跳转首页
                listener.onFinish()
                context.startActivity(Intent(context, MainActivity::class.java))
            }

            override fun onServiceError(content: String?, status: Int, index: Int) {
                super.onServiceError(content, status, index)
                listener.onFinish()
                if (!TextUtils.isEmpty(content)) {
                    listener.onBindError(content!!)
                }
            }
        })


    }


    /**
     * 请求登录协议,标准的用户名和密码形式,当前项目用到了
     * @parm smsKey 验证验证码返回
     * @param openId 第三方绑定需要用到
     */
    override fun requestLogin(mobile: String, smsKey: String, openId: String, listener: ILoginInteractor.OnLoginCallbackListener, context: Context) {
        val query = UserLoginRequestQuery()
        query.setDir("Cis")
        var action: Int
        if (TextUtils.isEmpty(smsKey)) {
            //第三方绑定
            query.action = AIIAction.TWO
            query.code = openId
            action = 2
        } else {
            //普通的登录
            query.action = AIIAction.ONE
            query.smsKey = smsKey
            query.mobile = mobile
            action = 1
        }
        listener.onStart()
        App.aiiRequest?.send(query, object : AIIResponse<UserLoginResponseQuery>(context, false) {
            override fun onSuccess(response: UserLoginResponseQuery?, index: Int) {
                super.onSuccess(response, index)
                //跳转到首页,普通的登录直接跳转首页,如果是第三方登录就判断当前用户是否已经绑定微信,如果没有跳绑定页面,否则就跳转首页
                listener.onFinish()
                if (action == 1) {
                    //普通的登录
                    requestUserDetail(listener, context)
                } else {
                    //第三方登录
                    val isBindPhone = response?.isBindMobile
                    if (isBindPhone == 2) {
                        requestUserDetail(listener, context)
                    } else {
                        context.startActivity(Intent(context, BindPhoneActivity::class.java))
                    }
                }
            }

            override fun onServiceError(content: String?, status: Int, index: Int) {
                super.onServiceError(content, status, index)
                listener.onFinish()
            }
        })


    }

    fun requestUserDetail(listener: ILoginInteractor.OnLoginCallbackListener, context: Context) {
        val query = UserDetailsRequestQuery()
        query.setDir("Cis")
        query.id = 0
        App.aiiRequest?.send(query, object : AIIResponse<UserDetailsResponseQuery>(context, false) {
            override fun onSuccess(response: UserDetailsResponseQuery, index: Int) {
                super.onSuccess(response, index)
                //设置全局user
                response?.user.let {
                    Constants?.user = it
                    AiiUtil.putString(context, "user", JSON.toJsonString(it))
//                    这个id不知道为什么报空
                    AIIConstant.USER_ID = it!!.id
//                    用于统计登陆在线的用户量
                    MobclickAgent.onProfileSignIn("${it!!.id}")
                }
                //跳转到首页
                context.startActivity(Intent(context, MainActivity::class.java))
                if (context != null) {
                    val contextActivity = context as Activity
                    contextActivity.finish()
                }
            }

            override fun onServiceError(content: String?, status: Int, index: Int) {
                super.onServiceError(content, status, index)
                listener?.onUserDetailFailResponse()
            }

            override fun onFailure(content: String?, index: Int) {
                super.onFailure(content, index)
                listener?.onUserDetailFailResponse()
            }
        })
    }


}
