package com.aiitec.letar.ui.web

import android.annotation.SuppressLint
import android.net.http.SslError
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.text.TextUtils
import android.webkit.*
import com.aiitec.hiim.R
import com.aiitec.hiim.annotation.ContentView
import com.aiitec.hiim.base.BaseKtActivity
import com.aiitec.hiim.utils.WebViewUtils
import com.aiitec.openapi.utils.LogUtil

/**
 * @author ailibin(用户登录注册协议和关于我们等界面)
 */
@ContentView(R.layout.activity_common_webview)
class CommonWebViewActivity : BaseKtActivity() {

    companion object {
        val ARG_URL = "url"
        val ARG_TITLE = "title"
        val ARG_CONTENT = "content"
        val PROCESS_SPEED = 100
    }

    private var webView: WebView? = null

    @SuppressLint("ResourceType", "SetJavaScriptEnabled")
    override fun init(savedInstanceState: Bundle?) {

        webView = findViewById(R.id.webView)
        val url = bundle.getString(ARG_URL, "")
        val content = bundle.getString(ARG_CONTENT, "")
        val title = bundle.getString(ARG_TITLE, "")
        addBaseStatusBarView()
        setColumnTitle(title)
//        if (!TextUtils.isEmpty(title)) {
//            setColumnTitle(title)
//            setTitleBarVisible(true)
//        } else {
//            setTitleBarVisible(false)
//        }
        LogUtil.d("ailibin", "url: " + url!!)
//        if (TextUtils.isEmpty(url)) {
//            return
//        }
        if (!TextUtils.isEmpty(content)) {
            WebViewUtils.setHtmlContent(webView, content)
//            setColumnTitle("广告详情")
        }

        val settings = webView?.settings
        //        对于需要使用 file 协议的应用，禁止 file 协议加载 JavaScript。
        settings?.allowFileAccess = false
        settings?.allowFileAccessFromFileURLs = false
        settings?.allowUniversalAccessFromFileURLs = false

        settings?.javaScriptEnabled = !url.startsWith("file://")
        settings?.domStorageEnabled = true
        settings?.setAppCacheEnabled(true)
        settings?.javaScriptCanOpenWindowsAutomatically = true
        settings?.loadsImagesAutomatically = true

        settings?.javaScriptEnabled = true
        //兼容https和http的图片 api-21
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings?.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }

        //解决图片不显示问题
        settings?.blockNetworkImage = false
        //设置字体显示方式(这样设置就可以禁止缩放,按照百分比显示)
        settings?.textZoom = 100

        webView?.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                //加载进度条,加载过程可能有点慢
                if (newProgress == 100) {
                    progressDialogDismiss()
                } else {
                    progressDialogShow()
                }
            }
        }

        //链接没有就不要执行这段代码了
        if (TextUtils.isEmpty(url)) {
            return
        }
        webView?.webViewClient = object : WebViewClient() {
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

            //接受数字证书
            override fun onReceivedSslError(view: WebView, handler: SslErrorHandler, error: SslError) {
                super.onReceivedSslError(view, handler, error)
                handler.proceed()
            }

        }
        webView?.loadUrl(url)
    }
}