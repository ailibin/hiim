package com.aiitec.letar.web

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.text.TextUtils
import com.aiitec.hiim.base.Constants
import com.aiitec.hiim.ui.login.LoginActivity
import com.aiitec.openapi.utils.LogUtil
import com.aiitec.openapi.utils.PacketUtil
import com.aiitec.widgets.CustomProgressDialog
import com.aiitec.widgets.dialog.ShareDialog

/**
 * Created by ailibin on 2018/2/1.
 * js和android互相调用的基类接口(为了混淆方便)
 */
open class BaseJavascriptInterface(val context: Context) {

    var shareDialog: ShareDialog? = null

    protected var progressDialog: CustomProgressDialog? = null

    init {
        shareDialog = ShareDialog(context)
        progressDialog = CustomProgressDialog(context)
    }

    val TAG = "TAG_JS_EXCEPTION"
    /**返回按钮*/
    @android.webkit.JavascriptInterface
    open fun backward(last: String) {
        if (context is Activity) {
            context.finish()
        }
    }

    /**前进按钮*/
    @android.webkit.JavascriptInterface
    open fun forward() {

    }

    /**关闭网页*/
    @android.webkit.JavascriptInterface
    open fun close() {
        if (context is Activity) {
            context.finish()
        }
    }

    /**加载进度*/
    @android.webkit.JavascriptInterface
    open fun setLoading(progress: String) {
        if (TextUtils.isEmpty(progress)) {
            progressDialogDismiss()
        } else {
            progressDialogShow()
        }
    }

    /**设置显示底部页脚*/
    @android.webkit.JavascriptInterface
    open fun setShowFooter() {
    }

    /**设置显示顶部标题栏*/
    @android.webkit.JavascriptInterface
    open fun setShowHeader() {
    }

    @Synchronized
    fun progressDialogDismiss() {
        try {
            if (progressDialog != null && progressDialog!!.isShowing) {
                progressDialog!!.cancel()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    @Synchronized
    fun progressDialogShow() {
        try {
            if (progressDialog != null && !progressDialog!!.isShowing) {
                progressDialog!!.show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    /**
     * 获取session
     * @param isLoginRequired   是否需要登录，返回true是如果没有登录的话就登录，
     * @param isRelogin    强制登录，一般不会用
     */
    @android.webkit.JavascriptInterface
    open fun getSession(isLoginRequired: Boolean, isRelogin: Boolean): String {
        if (isRelogin) {
            context.startActivity(Intent(context, LoginActivity::class.java))
        } else if (isLoginRequired && Constants.user == null) {
            context.startActivity(Intent(context, LoginActivity::class.java))
        }
        return PacketUtil.session_id
    }

    /**捕获异常*/
    @android.webkit.JavascriptInterface
    open fun catchException(exceptionName: String, msg: String) {
        LogUtil.w(TAG, "$exceptionName:    $msg")
    }


    /**分享*/
    @android.webkit.JavascriptInterface
    open fun invokeShare(shareInfo: String) {
//        Handler(Looper.getMainLooper()).post(Runnable {
//            val shareInfoModel = WxShareInfo()
//            var realUrl = ""
//            val payInfoMap = BaseUtil.jsonToMap(shareInfo)
//            LogUtil.d("ailibin", "payInfoMap: " + payInfoMap)
////            shareInfoModel.imageUrl = payInfoMap["iconPath"].toString()
//            shareInfoModel.imageUrl = payInfoMap["shareImage"].toString()
//            //分享的链接地址要拼接前缀
//            val url = payInfoMap["shareUrl"].toString()
//            if (url.startsWith("https") || url.startsWith("http")) {
//                realUrl = url
//            } else {
////                realUrl = H5Api.PREFIX_URL + url
//            }
//            LogUtil.d("ailibin", "realUrl: " + realUrl)
//            shareInfoModel.url = realUrl
//            shareInfoModel.description = payInfoMap["shareText"].toString()
//            shareInfoModel.title = payInfoMap["shareTitle"].toString()
//            //请求的唯一标示
//            shareInfoModel.reqTag = "com.aiitec.eypassword" + Random().nextInt(10000)
//            //分享的类型,是纯网页分享还是图片分享
//            shareInfoModel.shareType = payInfoMap["shareType"].toString().toInt()
//            //在主线程更新ui
//            shareDialog?.setShareContent(shareInfoModel, 1)
//            shareDialog?.show()
//        })
    }


    /**
     * 发起好友聊天
     * @param userImId IM的id
     * @param url 链接， 可能为空
     * @param contentText 内容
     * @param iconPath 显示图标的路径
     *
     */
    @android.webkit.JavascriptInterface
    open fun invokeIm(userImId: String) {
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        shareDialog?.onActivityResult(requestCode, resultCode, data)
    }
}
