package com.aiitec.hiim.ui

import android.os.Bundle
import android.os.Handler
import com.aiitec.hiim.base.BaseKtActivity
import com.aiitec.hiim.base.Constants
import com.aiitec.hiim.im.utils.AiiUtil

/**
 * 启动页
 * @author Anthony
 * createTime 2018-01-30
 *
 */
class WelcomeActivity : BaseKtActivity() {


    override fun init(savedInstanceState: Bundle?) {

//        Slidr.attach(this)
        //初始化腾讯云im
        initIM()
        //获取用户详情数据,实现自动登录
        Handler().postDelayed({
            if(supportFragmentManager.isDestroyed){
                return@postDelayed
            }
            val isFirstLaunch = AiiUtil.getBoolean(this@WelcomeActivity, Constants.IS_FIRST_LAUNCH, true)
            if(isFirstLaunch){
                switchToActivity(GuideActivity::class.java)
            } else {
//                if(AIIConstant.USER_ID > 0){
//                    switchToActivity(MainActivity::class.java)
//                } else {
//                    switchToActivity(LoginActivity::class.java)
//                }
            }
            finish()
        }, 1000)
    }





//    /**
//     * 请求用户详情数据,实现第二次自动登录功能,id传0或者不传都表示查询自己
//     */
//    private fun requestUserDetails() {
//        val query = UserDetailsRequestQuery()
//        query.setDir("cis")
//        query.id = 0
//        App.aiiRequest?.send(query, object : AIIResponse<UserDetailsResponseQuery>(this, false) {
//            override fun onSuccess(response: UserDetailsResponseQuery?, index: Int) {
//                super.onSuccess(response, index)
//                Constants.user = response?.user
//
//
//            }
//
//            override fun onServiceError(content: String?, status: Int, index: Int) {
////                super.onServiceError(content, status, index)
//            }
//
//            override fun onLoginOut(status: Int) {
////                super.onLoginOut(status)
//                //不做跳转处理
//            }
//            override fun onFailure(content: String?, index: Int) {
//                super.onFailure(content, index)
//            }
//        })
//    }

}