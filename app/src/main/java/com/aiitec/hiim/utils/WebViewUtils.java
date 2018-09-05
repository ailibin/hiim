package com.aiitec.hiim.utils;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.aiitec.hiim.base.Api;


/**
 * @Author ailibin
 * @Version 1.0
 * Created on 2018/5/04
 * @effect webview的设置工具类
 */
public class WebViewUtils {

    /**
     * 用webview先html内容
     *
     * @param webView
     * @param htmlStr
     */
    public static void setHtmlContent(WebView webView, String htmlStr) {
        StringBuffer sb = new StringBuffer();
        sb.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");
        sb.append("<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">");
        sb.append("<meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">");
        sb.append("<meta name=\"viewport\" content=\"width=device-width,initial-scale=1.0,maximum-scale=1.0,user-scalable=0\" />");
        sb.append("<meta name=\"referrer\" content=\"origin-when-cross-origin\">");
        sb.append("<meta name=\"apple-mobile-web-app-capable\" content=\"yes\">");
        sb.append("<meta name=\"apple-mobile-web-app-status-bar-style\" content=\"black\">");
        sb.append("<meta name=\"format-detection\" content=\"telephone=no\"></head><body>");
        sb.append("<style type=\"text/css\">img{max-width:100% !important; height:auto !important;}</style>");

        sb.append(htmlStr);
        sb.append("</body></html>");
        String useHtmlstr = sb.toString();
        WebSettings settings = webView.getSettings();
        //是否允许放大缩小
        settings.setSupportZoom(false);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        //布局适配换行处理,控制webview不能左右滑动
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        //是否可以支持javaScript打开窗口
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        //支持javascript
        settings.setJavaScriptEnabled(true);
        //访问文件权限
        settings.setAllowFileAccess(true);
        String ua = settings.getUserAgentString();
        settings.setUserAgentString(ua + " wxhappwebview/1.1");
        webView.loadDataWithBaseURL(Api.INSTANCE.getBASE_URL(), useHtmlstr, "text/html", "utf-8", null);

        //api 21
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                return true;
            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return true;
            }
        });

    }
}
