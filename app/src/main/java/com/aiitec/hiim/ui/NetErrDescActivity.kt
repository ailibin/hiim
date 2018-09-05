package com.aiitec.hiim.ui

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.provider.Settings
import com.aiitec.hiim.R
import com.aiitec.hiim.annotation.ContentView
import com.aiitec.hiim.base.BaseKtActivity
import kotlinx.android.synthetic.main.activity_net_err_desc.*

/**
 * @author  Anthony
 * @version 1.0
 * createTime 2017/11/17.
 */
@ContentView(R.layout.activity_net_err_desc)
class NetErrDescActivity : BaseKtActivity(){


    fun onClicSet() {
        //这个页面本来就是无网络点击进来的，也不方便刷新，所以就不管网络状态了，点击直接跳转
        setNetwork(this)
    }


    @SuppressLint("MissingPermission")
            /**
     * 我们做开发的都知道，由于Android的SDK版本不同(尤其在Android 3.0 及后面)的版本中,UI及显示方式都发生了比较大的变化,打开网络设置为例,代码如下:
     * 1，我们判断网络是否打开：
     */
    fun isConnectNet(context: Context): Boolean {
        var bisConnFlag = false
        val conManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = conManager.activeNetworkInfo
        if (network != null) {
            bisConnFlag = conManager.activeNetworkInfo.isAvailable
        }
        return bisConnFlag
    }

    /**
     * 2. 未开启网络时打开设置界面(如果不写在Activity里面则不需要参数),在相应的位置调用即可
     */
    private fun setNetwork(context: Context) {
        var intent: Intent? = null
        //判断手机系统的版本  即API大于10 就是3.0或以上版本
        if (android.os.Build.VERSION.SDK_INT > 10) {
            intent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
        } else {
            intent = Intent()
            val component = ComponentName("com.android.settings", "com.android.settings.WirelessSettings")
            intent.component = component
            intent.action = "android.intent.action.VIEW"
        }
        context.startActivity(intent)
    }

    override fun init(savedInstanceState: Bundle?) {
        setTitle(R.string.net_desc)
        val appName = resources.getString(R.string.app_name)
        val desc_set_permission = resources.getString(R.string.hint_net_desc_set_permission, appName)
        tv_net_permission_set.text = desc_set_permission
        btn_set_net.setOnClickListener{onClicSet()}
    }

}