package com.aiitec.hiim.base

import android.app.Application
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.multidex.MultiDex
import android.support.v4.app.FragmentActivity
import android.util.Log
import com.aiitec.hiim.BuildConfig
import com.aiitec.hiim.R
import com.aiitec.hiim.im.utils.LogUtil
import com.aiitec.hiim.utils.BaseUtil
import com.umeng.message.IUmengRegisterCallback
import com.umeng.message.PushAgent
import com.umeng.message.UmengMessageHandler
import com.umeng.message.UmengNotificationClickHandler
import com.umeng.message.entity.UMessage
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * @author  Anthony
 * @version 1.0
 * createTime 2017/11/17.
 */
class App : Application() {

    companion object {
        lateinit var app: App
//        var aiiRequest: AIIRequest? = null
//        var aiidbManager: AIIDBManager? = null
        var context: Context? = null
        fun getInstance(): App {
            return app
        }
    }

    private val activities = ArrayList<FragmentActivity>()
    lateinit var cachedThreadPool: ExecutorService


    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        App.app = this
        cachedThreadPool = Executors.newCachedThreadPool()
//        aiiRequest = AIIRequest(this, Api.API)
//        aiidbManager = AIIDBManager(this, "haiim_db")
        LogUtil.showLog = BuildConfig.DEBUG
        //初始化打印工具类
        BaseUtil.init(this)
//        //屏幕适配
        initParams()

    }

    private fun initParams() {
        initSession()
        //友盟分享初始化
        initUMShare()
        //友盟推送初始化
        initUMPush()
        //友盟分享api初始化
//        UMShareAPI.get(this)
        //友盟统计分析
        initUMAnalysis()
    }

    /**
     * 初始化session相关参数
     *
     */
    private fun initSession() {
//        PacketUtil.session_id = AiiUtil.getString(this, CommonKey.KEY_SESSION)
//        val session = AiiUtil.getString(context, CommonKey.KEY_SESSION)
//        if (TextUtils.isEmpty(session)) {
//            //这里自动获取session_id,不要手动获取,不然会报泛型异常,导致第一次获取不到用户session_id
//            PacketUtil.session_id = null
//        }
    }

    private fun initUMPush() {

        val mPushAgent = PushAgent.getInstance(this)
        //注册推送服务，每次调用register方法都会回调该接口
        mPushAgent.register(object : IUmengRegisterCallback {
            override fun onSuccess(deviceToken: String) {
                LogUtil.e("ailibin", "umegn_push is register seccuess deviceToken is " + deviceToken)
//                AiiUtil.putString(context, CommonKey.KEY_DEVICETOKEN, deviceToken)
//                val session = AiiUtil.getString(context, CommonKey.KEY_SESSION)
//                if (TextUtils.isEmpty(session)) {
//                    //这里自动获取session_id,不要手动获取,不然会报泛型异常,导致第一次获取不到用户session_id
//                    PacketUtil.session_id = null
//                }
            }

            override fun onFailure(s: String, s1: String) {
                LogUtil.e("ailibin", "umeg push register fail $s    $s1")
            }
        })

        //用户点击的后续动作(这个要后台处理打开后的动作:go_custom)
        val onClickHandler = object : UmengNotificationClickHandler() {
            override fun dealWithCustomAction(context: Context?, msg: UMessage?) {
                super.dealWithCustomAction(context, msg)
                LogUtil.e("ailibin", "extra: " + msg?.extra)
                val extra = msg?.extra
                //课程id
                var targetId: Long = -1
                var type = -1
                try {
                    if (extra != null) {
                        for (entry in extra!!.entries) {
                            Log.e("ailibin", "key= " + entry.key + " and value= " + entry.value)
                            if (entry.key == "targetId") {
                                targetId = java.lang.Long.parseLong(entry.value)
                            } else if (entry.key == "type") {
                                type = Integer.parseInt(entry.value)
                            }
                        }
                    }
                } catch (e: NumberFormatException) {
                    e.printStackTrace()
                }

                //type 0 无跳转 1 课程详情 2 我的钱包 3 我的课程(待评价课程)
                when (type) {
                    1 -> {
//                        //课程详情(跳转到课程详情页面)
//                        val bundle = Bundle()
//                        bundle.putLong(Constants.COURSE_ID, targetId)
//                        val intent = Intent(context, CourseDetailActivity::class.java)
//                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                        intent.putExtras(bundle)
//                        context?.startActivity(intent)
                    }
                }
            }
        }
        mPushAgent.notificationClickHandler = onClickHandler

        //收到推送消息通知栏会调用这个方法
        val umengMessageHandler = object : UmengMessageHandler() {

            @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
            override fun getNotification(context: Context?, msg: UMessage?): Notification {
                LogUtil.e("ailibin", "extra456: " + msg?.extra)
                val extra = msg?.extra
                //课程id
                var targetId: Long = -1
                //type=1課程詳情 2 我的課程 3我的錢包 4待評價課程 5系统消息
                var type = -1
                try {
                    if (extra != null) {
                        for (entry in extra!!.entries) {
                            Log.e("ailibin", "key= " + entry.key + " and value= " + entry.value)
                            if (entry.key == "targetId" || entry.key == "id") {
                                targetId = java.lang.Long.parseLong(entry.value)
                            } else if (entry.key == "type") {
                                type = Integer.parseInt(entry.value)
                            }
                        }
                    }
                } catch (e: NumberFormatException) {
                    e.printStackTrace()
                }
                //api=26 安卓8.0channel
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val manager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    val channel = NotificationChannel("channel_id", "channel_name", NotificationManager.IMPORTANCE_HIGH)
                    manager?.createNotificationChannel(channel)
                    val builder = Notification.Builder(context, "channel_id")
                    builder.setSmallIcon(R.mipmap.ic_launcher)
                            .setWhen(System.currentTimeMillis())
                            .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher))
                            .setContentTitle(msg?.title)
                            .setContentText(msg?.text)
                            .setAutoCancel(true)
                    return builder.build()
                } else {
                    val builder = Notification.Builder(context)
                    builder.setSmallIcon(R.mipmap.ic_launcher)
                            .setWhen(System.currentTimeMillis())
                            .setLargeIcon(BitmapFactory.decodeResource(context?.resources, R.mipmap.ic_launcher))
                            .setContentTitle(msg?.title)
                            .setContentText(msg?.text)
                            .setAutoCancel(true)
                    return builder.build()
                }
            }
        }
        mPushAgent.messageHandler = umengMessageHandler
    }

    private fun initUMShare() {
//        PlatformConfig.setWeixin(getString(R.string.weixinId), getString(R.string.weixinSecret))
//        PlatformConfig.setQQZone(getString(R.string.qq_id), getString(R.string.qq_key))
//        //这里换成新浪微博分享的回调地址.java
//        val callbackUrl = Api.BASE_URL + "/weibo/callback.java"
////        PlatformConfig.setSinaWeibo(getString(R.string.sina_id), getString(R.string.sina_key), callbackUrl)
//        val channel = WalleChannelReader.getChannel(applicationContext)
//        val pushSecret = getString(R.string.umeng_message_secret)
//        val deviceType = UMConfigure.DEVICE_TYPE_PHONE
//        UMConfigure.init(this, getString(R.string.umeng_key), channel, deviceType, pushSecret)
    }

    private fun initUMAnalysis() {
        //统计分析配置
//        val channel = WalleChannelReader.getChannel(this.applicationContext, "letar")
//        val config = MobclickAgent.UMAnalyticsConfig(this, resources.getString(R.string.umeng_key), channel)
//        MobclickAgent.startWithConfigure(config)
    }


    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }


    /**
     * 关闭所有页面
     *
     * @param instance activity实例对象
     */
    fun removeInstance(instance: FragmentActivity) {
        activities.remove(instance)
    }

    /**
     * 把所有页面添加到列表统一管理
     *
     * @param instance activity实例对象
     */
    fun addInstance(instance: FragmentActivity) {
        activities.add(instance)
    }

    /**
     * 退出 关闭所有页面并杀死所有线程
     */
    fun exit() {
        try {
            for (activity in activities) {
                activity?.finish()
            }
        } catch (e: Exception) {
        } finally {
            android.os.Process.killProcess(android.os.Process.myPid())
            System.exit(0)
        }
    }

    /**
     * 退出 关闭所有页面 不杀死后台线程
     */
    fun exitHome() {
        try {
            for (activity in activities) {
                activity?.finish()
            }
        } catch (e: Exception) {
        }

    }

    fun closeAllActivity() {
        try {
            for (activity in activities) {
                activity?.finish()
            }
        } catch (e: Exception) {
        }

    }
}