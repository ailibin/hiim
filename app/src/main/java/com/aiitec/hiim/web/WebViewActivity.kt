package com.aiitec.letar.web

import android.content.Context
import android.content.Intent
import android.net.http.SslError
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.text.TextUtils
import android.view.KeyEvent
import android.view.View
import android.webkit.*
import com.aiitec.hiim.R
import com.aiitec.hiim.annotation.ContentView
import com.aiitec.hiim.base.App
import com.aiitec.hiim.base.BaseKtActivity
import com.aiitec.hiim.ui.login.LoginActivity
import com.aiitec.hiim.utils.BaseUtil
import com.aiitec.hiim.utils.StatusBarUtil
import com.aiitec.openapi.model.RequestQuery
import com.aiitec.openapi.model.ResponseQuery
import com.aiitec.openapi.net.AIIResponse
import com.aiitec.openapi.utils.LogUtil
import com.aiitec.openapi.utils.PacketUtil
import com.aiitec.widgets.CommonDialog
import com.umeng.analytics.MobclickAgent
import kotlinx.android.synthetic.main.activity_webview.*

@ContentView(R.layout.activity_webview)
class WebViewActivity : BaseKtActivity() {

    private var hasLoadFinish = false
    lateinit var javascriptInterface: JavascriptInterface

    companion object {
        var ARG_URL = "url"
        var ARG_TAG = "tag"
    }

    override fun init(savedInstanceState: Bundle?) {

        StatusBarUtil.addStatusBarView(ll_include_title_bar, R.color.transparent)
        val url: String
        if (intent.hasExtra(ARG_URL)) {
            url = intent.getStringExtra(ARG_URL)
        } else {
            url = bundle.getString(ARG_URL)
        }
        LogUtil.e("url: " + url)

//        对于需要使用 file 协议的应用，禁止 file 协议加载 JavaScript。
        webView.settings.allowFileAccess = false
        webView.settings.allowFileAccessFromFileURLs = false
        webView.settings.allowUniversalAccessFromFileURLs = false
//        webView.settings.allowFileAccess = true
        // 禁止 file 协议加载 JavaScript
        webView.settings.javaScriptEnabled = !url.startsWith("file://")
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        //设置可以访问文件
        webView.settings.allowFileAccess = true
        // 设置缓存
        webView.settings.setAppCacheEnabled(true)
        webView.settings.javaScriptCanOpenWindowsAutomatically = true
        webView.settings.loadsImagesAutomatically = true
        //设置字体显示方式(这样设置就可以禁止缩放,按照百分比显示)
        webView.settings.textZoom = 100

        webView.webChromeClient = WebChromeClient()
        //如果在https里面加载http的图片需要用到下面这句，否则可能加载不出来
        if (Build.VERSION.SDK_INT >= 21) {
            webView.settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //开启远程调试(h5调试app)
            WebView.setWebContentsDebuggingEnabled(true)
        }

        javascriptInterface = JavascriptInterface(this)
//        webView.addJavascriptInterface(javascriptInterface, Constant.JS_INTERACTIVE_NAME)

        webView.webViewClient = object : WebViewClient() {

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                LogUtil.d("ailibin", "url: " + url)
                if (!hasLoadFinish) {//该方法可能执行多次，这样设置可上它只执行一次
                    hasLoadFinish = true
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                        //android调用js代码
                        webView.loadUrl("javascript:getSession('" + PacketUtil.session_id + "')")//单引号记得加上
                    } else {
                        // 因为该方法在 Android 4.4 版本才可使用，所以使用时需进行版本判断
                        webView.evaluateJavascript("javascript:getSession('" + PacketUtil.session_id + "')", object : ValueCallback<String> {
                            override fun onReceiveValue(p0: String?) {

                            }
                        })
                    }
                }
            }

            /**
             * 在加载页面资源时会调用，每一个资源（比如图片）的加载都会调用一次。
             */
            override fun onLoadResource(view: WebView?, url: String?) {
                super.onLoadResource(view, url)

            }

            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                view.loadUrl(url)
                return true
            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                val url = request.url.toString()
                view.loadUrl(url)
                return true
            }

            //如果遇到因为安全证书不能访问的情况可以用如下
            override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
                super.onReceivedSslError(view, handler, error)
                // 接受所有网站的证书，忽略SSL错误，执行访问网页
                handler?.proceed()
            }
        }

        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, newProgress: Int) {
                //这里不用网页加载进度条,用我们自己的加载进度条
                if (newProgress == 100) {
                    progressDialogDismiss()
                    ll_include_title_bar.visibility = View.VISIBLE
                    rlt_place_holder.visibility = View.GONE
                } else {
                    progressDialogShow()
                    rlt_place_holder.visibility = View.VISIBLE
                    ll_include_title_bar.visibility = View.GONE
                }
            }
        }
        webView.loadUrl(url)
    }


    private var imgDatas = ArrayList<String>()

    // js通信接口,传一个上下文
    inner class JavascriptInterface(context: Context) : BaseJavascriptInterface(context) {

        @android.webkit.JavascriptInterface
        override fun forward() {

        }

        //关闭当前网页
        @android.webkit.JavascriptInterface
        override fun backward(last: String) {
            super.backward(last)
        }

        @android.webkit.JavascriptInterface
        override fun setLoading(progress: String) {
            super.setLoading(progress)
        }

        @android.webkit.JavascriptInterface
        fun invokeCheckoutCounter(orderStr: String) {
            LogUtil.d("ailibin", "订单：" + orderStr)
            //对订单数据进行相关的解析(解析成一个对象)
            //val intent = Intent(context, CheckStandActivity::class.java)
//            intent.putExtra(Constant.ON_ORDER_PAY, orderStr)
//            context.startActivity(intent)
        }

        @android.webkit.JavascriptInterface
        override fun invokeShare(shareInfo: String) {
            //分享
            LogUtil.d("ailibin", shareInfo)
            if (!TextUtils.isEmpty(shareInfo)) {
                super.invokeShare(shareInfo)
            }
        }


        //跳转到登录页面
        @android.webkit.JavascriptInterface
        fun invokeLogin() {
            switchToActivity(LoginActivity::class.java)
        }

        //退出登录
        @android.webkit.JavascriptInterface
        fun invokeLogout() {
            //弹出对话框让用户选择
            showCommonDialog()
        }

    }

    private fun showCommonDialog() {
        val dialog = CommonDialog(this)
        dialog.setTitle("确认退出")
        dialog.setContent("是否退出登录")
        dialog.show()

        dialog.setOnConfirmClickListener {
            //确认
//            switchToActivity(LoginActivity::class.java)
            requestLogout()
        }
    }

    /**
     * 请求登出
     */
    private fun requestLogout() {
        val query = RequestQuery()
        query.setDir("cis")
        query.setNamespace("UserLogout")
        App.aiiRequest?.send(query, object : AIIResponse<ResponseQuery>(this, false) {
            override fun onSuccess(response: ResponseQuery?, index: Int) {
                super.onSuccess(response, index)
                BaseUtil.showToast("退出成功")
                //友盟统计用户的上线量(这里是下线量)
                MobclickAgent.onProfileSignOff()
                switchToActivity(LoginActivity::class.java)
//                logoutIM()
            }

            override fun onServiceError(content: String?, status: Int, index: Int) {
                super.onServiceError(content, status, index)
            }
        })
    }

    /**
     * 防止按返回键直接退出程序
     */
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (webView.canGoBack()) {
                //当webview不是处于第一页面时，返回上一个页面
                webView.goBack()
                return true
            } else {
                //当webview处于第一页面时,直接退出程序
                finish()
            }
        }
        return super.onKeyDown(keyCode, event)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        javascriptInterface.onActivityResult(requestCode, resultCode, data)
    }
}
