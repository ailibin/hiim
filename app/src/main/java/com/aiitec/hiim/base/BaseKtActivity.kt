package com.aiitec.hiim.base

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Resources
import android.os.Bundle
import android.os.Parcelable
import android.support.annotation.StringRes
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.aiitec.imlibrary.presentation.business.InitBusiness
import com.aiitec.imlibrary.tlslibrary.service.TLSService
import com.aiitec.imlibrary.tlslibrary.service.TlsBusiness
import com.aiitec.hiim.R
import com.aiitec.hiim.im.model.UserInfo
import com.aiitec.hiim.utils.ContentViewUtils
import com.aiitec.hiim.utils.ScreenUtils
import com.aiitec.hiim.utils.StatusBarUtil
import com.aiitec.openapi.cache.AiiFileCache
import com.aiitec.openapi.constant.AIIConstant
import com.aiitec.openapi.utils.PacketUtil
import com.aiitec.widgets.CustomProgressDialog
import com.tencent.imsdk.TIMLogLevel
import com.umeng.analytics.MobclickAgent
import java.io.Serializable


/**
 * @author Anthony
 * *
 * @version 1.0
 * * createTime 2017-05-29
 */
abstract class BaseKtActivity : AppCompatActivity() {


    private var tv_title: TextView? = null

    /**
     * 标题栏相关
     */
    var ll_title_bar: LinearLayout? = null
    var tv_title_column: TextView? = null
    var tv_right_action: TextView? = null
    var iv_go_back: ImageView? = null
    var iv_right_action: ImageView? = null

    //避免toast弹出多个,界面半天之后都没有消失
    private var mToast: Toast? = null

    fun Activity.toast(message: CharSequence) {
        if (mToast == null) {
            mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT)
        }
        mToast?.duration = Toast.LENGTH_SHORT
        mToast?.setText(message.toString())
        mToast?.show()
    }

    fun Activity.toast(@StringRes messageRes: Int) {
        if (mToast == null) {
            mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT)
        }
        mToast?.duration = Toast.LENGTH_SHORT
        mToast?.setText(messageRes)
        mToast?.show()
    }

    open fun doBeforeSetContent() {
        //竖屏
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //配置左滑finish界面
//        val c = Class.forName("com.r0adkll.slidr.model.SlidrConfig")
//        val con = c.getDeclaredConstructor()
//        con.isAccessible = true
//        val config = con.newInstance() as SlidrConfig
//        config.scrimColor = ContextCompat.getColor(this, R.color.bg_all)
//        config.scrimStartAlpha = 0.5f
////        config.scrimEndAlpha = 1f
//        Slidr.attach(this, config)

        //适配各个手机,假如设计稿的宽度为360,仿照今日头条适配方案,
        // 该方案不用百分比适配但是又是百分比的效果,有些适配需要创建大量资源文件,
        // 这个适配只需要一套图片即可,这些方法要放在setContentView()方法之前,也就是
        //在ContentViewUtils.inject(this)之前就可以了
        if(ScreenUtils.isPortrait()){
            //竖屏适配宽度
            ScreenUtils.adaptScreen4VerticalSlide(this,375)
        }else{
            //横屏适配的宽度即竖屏的高度
            ScreenUtils.adaptScreen4HorizontalSlide(this,667)
        }
//        ScreenUtils.adaptScreen4VerticalSlide(this,360)

        doBeforeSetContent()
        ContentViewUtils.inject(this)
        progressDialog = CustomProgressDialog(this)
        progressDialog?.setCancelable(true)
        progressDialog?.setCanceledOnTouchOutside(true)

        val myApp = application as App
        myApp.addInstance(this)
        tv_title = findViewById<TextView?>(R.id.tv_title)
        toolbar = findViewById<Toolbar?>(R.id.toolbar)
        setToolBar(toolbar)

        //设置状态栏的文字为黑色主题
//        StatusBarUtil.StatusBarDarkMode(this)
//        StatusBarUtil.StatusBarLightMode(this)

        //标题栏(抽取)
        ll_title_bar = findViewById(R.id.ll_title_bar)
        //标题栏标题
        tv_title_column = findViewById(R.id.tv_title_column)
        //标题栏返回按钮
        iv_go_back = findViewById(R.id.iv_go_back)
        //右侧按钮的操作事件
        tv_right_action = findViewById(R.id.tv_right_action)
        //右边图片
        iv_right_action = findViewById(R.id.iv_right_action)

        iv_go_back?.setOnClickListener {
            if (!supportFragmentManager.isDestroyed) {
                finish()
            }
        }

        init(savedInstanceState)

    }

    /**
     *设置左侧按钮的点击事件
     */
    fun setLeftBtnClickListener(listener: View.OnClickListener) {
        iv_go_back?.setOnClickListener(listener)
    }


    fun setBackGroundColor(color: Int) {
        ll_title_bar?.setBackgroundColor(color)
    }

    /**
     * 是否显示右侧按钮
     */
    fun setRightBtnVisible(visible: Boolean) {
        //默认不显示
        if (visible) {
            //显示
            tv_right_action?.visibility = View.VISIBLE
        } else {
            tv_right_action?.visibility = View.GONE
        }
    }

    /**
     * 设置右边按钮的文本信息和文字颜色
     */
    fun setRightBtnText(textStr: CharSequence, textColor: Int) {
        tv_right_action?.visibility = View.VISIBLE
        tv_right_action?.text = textStr
        if (textColor > 0) {
            tv_right_action?.setTextColor(ContextCompat.getColor(this, textColor))
        } else {
            tv_right_action?.setTextColor(ContextCompat.getColor(this, R.color.black3))
        }
    }


    /**
     * 设置右边图片
     */
    fun setRightImageResource(imageResId: Int) {
        iv_right_action?.visibility = View.VISIBLE
        iv_right_action?.setImageResource(imageResId)
    }

    /**
     * 设置右边图片的点击事件
     */
    fun setRightImageClickListener(listener: View.OnClickListener) {
        iv_right_action?.setOnClickListener(listener)
    }


    /**
     * 设置右侧按钮的点击事件
     */
    fun setRightBtnClickListener(listener: View.OnClickListener) {
        tv_right_action?.setOnClickListener(listener)
    }


    /**
     * 设置左侧按钮是否可见
     */
    fun setLeftIconVisible(visible: Boolean) {
        //默认可见
        if (visible) {
            iv_go_back?.visibility = View.VISIBLE
        } else {
            iv_go_back?.visibility = View.GONE
        }
    }


    /**
     * 设置标题栏标题
     */
    fun setColumnTitle(title: CharSequence) {
        tv_title_column?.text = title
    }

    fun setColumnTitleColorAndSize(colorRes: Int, size: Float) {
        tv_title_column?.setTextColor(colorRes)
        tv_title_column?.textSize = size
    }

    /**
     * 设置标题栏标题(传一个资源id)
     */
    fun setColumnTitle(titleResId: Int) {
        tv_title_column?.setText(titleResId)
    }

    /**
     * 设置标题的沉浸式状态栏(自定义)
     */
    fun addBaseStatusBarView(view: View, colorRes: Int) {
        StatusBarUtil.addStatusBarView(view, colorRes)
    }

    /**
     * 设置标题的沉浸式状态栏(设置状态栏的颜色为透明)
     */
    fun addBaseStatusBarView() {
        StatusBarUtil.addStatusBarView(ll_title_bar, R.color.transparent)
//        if (this.javaClass == InviteFriendsActivity::class.java) {
//            StatusBarUtil.addStatusBarView(ll_title_bar, R.color.transparent)
//        } else {
//            StatusBarUtil.setWhiteStatusBar(this)
//        }
    }

    protected abstract fun init(savedInstanceState: Bundle?)

    protected var progressDialog: CustomProgressDialog? = null
    protected var toolbar: Toolbar? = null
    protected val bundle: Bundle
        get() {
            var bundle: Bundle? = intent.extras
            if (bundle == null) bundle = Bundle()
            return bundle
        }

    fun setToolBar(toolBar: Toolbar?) {
        if (toolBar == null) return
        this.toolbar = toolBar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)
    }

    override fun setTitle(title: CharSequence) {
        tv_title?.text = title
    }

    override fun setTitle(titleRes: Int) {
        tv_title?.setText(titleRes)
    }

    /**
     * 设置app字体不跟随系统字体大小变换
     */
    override fun getResources(): Resources {
        //获取到resources对象
        val res = super.getResources()
        //修改configuration的fontScale属性
        res.configuration.fontScale = 1f
        //将修改后的值更新到metrics.scaledDensity属性上
        res.updateConfiguration(null, null)
        return res
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

    fun switchToActivity(clazz: Class<*>) {
        val intent = Intent(this, clazz)
        startActivity(intent)
    }

    fun switchToActivity(context: Context, clazz: Class<*>) {
        val intent = Intent(context, clazz)
        startActivity(intent)
    }

    fun switchToActivity(clazz: Class<*>, bundle: Bundle) {
        val intent = Intent(this, clazz)
        intent.putExtras(bundle)
        startActivity(intent)
    }

    fun switchToActivity(clazz: Class<*>, vararg pairs: Pair<String, Any?>) {
        val intent = Intent(this, clazz)
        intent.putExtras(getBundleExtras(pairs))
        startActivity(intent)

    }

    private fun getBundleExtras(pairs: Array<out Pair<String, Any?>>): Bundle {
        val bundle = Bundle()

        for (pair in pairs) {
            pair.second?.let {
                when {
                    Integer::class.java.isAssignableFrom(it::class.java) -> bundle.putInt(pair.first, it as Int)
                    String::class.java.isAssignableFrom(it::class.java) -> bundle.putString(pair.first, it as String)
                    Float::class.java.isAssignableFrom(it::class.java) -> bundle.putFloat(pair.first, it as Float)
                    Double::class.java.isAssignableFrom(it::class.java) -> bundle.putDouble(pair.first, it as Double)
                    Long::class.java.isAssignableFrom(it::class.java) -> bundle.putLong(pair.first, it as Long)
                    Serializable::class.java.isAssignableFrom(it::class.java) -> bundle.putSerializable(pair.first, it as Serializable)
                    Parcelable::class.java.isAssignableFrom(it::class.java) -> bundle.putParcelable(pair.first, it as Parcelable)
                    else -> {
                    }
                }
            }
        }
        return bundle
    }

    fun switchToActivity(context: Context, clazz: Class<*>, bundle: Bundle) {
        val intent = Intent(context, clazz)
        intent.putExtras(bundle)
        startActivity(intent)
    }

    protected fun initIM() {
        val pref = getSharedPreferences("data", Context.MODE_PRIVATE)
        val loglvl = pref.getInt("loglvl", TIMLogLevel.DEBUG.ordinal)
        //初始化IMSDK
        InitBusiness.start(applicationContext, loglvl)
        //初始化TLS
        TlsBusiness.init(applicationContext)
        val id = TLSService.getInstance().lastUserIdentifier
        UserInfo.getInstance().id = id
        UserInfo.getInstance().userSig = TLSService.getInstance().getUserSig(id)
    }

    /**
     * 更改用户
     *
     * @param userId 用户id
     */
    fun changeUser(userId: Long) {
        //更改用户id的时候，需要把缓存路径也更改一下，否则读取缓存有可能读到别人的缓存
        AIIConstant.USER_ID = userId
        AiiFileCache.changeDir(PacketUtil.getCacheDir(this))
    }

    fun switchToActivityForResult(context: Context, clazz: Class<*>,
                                  bundle: Bundle?, requestCode: Int) {
        val intent = Intent(context, clazz)
        if (bundle != null) {
            intent.putExtras(bundle)
        }
        startActivityForResult(intent, requestCode)
    }

    fun switchToActivityForResult(context: Context, clazz: Class<*>, requestCode: Int, vararg pairs: Pair<String, Any?>) {
        val intent = Intent(context, clazz)
        intent.putExtras(getBundleExtras(pairs))
        startActivityForResult(intent, requestCode)
    }

    fun switchToActivityForResult(clazz: Class<*>, requestCode: Int, vararg pairs: Pair<String, Any?>) {
        val intent = Intent(this, clazz)
        intent.putExtras(getBundleExtras(pairs))
        startActivityForResult(intent, requestCode)
    }

    fun switchToActivityForResult(clazz: Class<*>, bundle: Bundle?, requestCode: Int) {
        val intent = Intent(this, clazz)
        if (bundle != null) {
            intent.putExtras(bundle)
        }
        startActivityForResult(intent, requestCode)
    }

    fun switchToActivityForResult(clazz: Class<*>, requestCode: Int) {
        val intent = Intent(this, clazz)
        startActivityForResult(intent, requestCode)
    }

    fun switchToActivityForResult(context: Context, clazz: Class<*>, requestCode: Int) {
        val intent = Intent(context, clazz)
        startActivityForResult(intent, requestCode)
    }

    fun switchToActivity(context: Context, clazz: Class<*>, anim_in: Int, anim_out: Int) {
        val intent = Intent(context, clazz)
        startActivity(intent)
        overridePendingTransition(anim_in, anim_out)
    }

    fun switchToActivity(context: Context, clazz: Class<*>, bundle: Bundle, anim_in: Int, anim_out: Int) {
        val intent = Intent(context, clazz)
        intent.putExtras(bundle)
        startActivity(intent)
        overridePendingTransition(anim_in, anim_out)
    }


    override fun onResume() {
        super.onResume()
        MobclickAgent.onResume(this) // 友盟统计时长
    }

    override fun onPause() {
        super.onPause()
        MobclickAgent.onPause(this)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        val myApp = application as App
        myApp.removeInstance(this)
//        //界面消失之后取消适配,减少资源的损耗
//        ScreenUtils.cancelAdaptScreen(this)
    }


    override fun onStop() {
        super.onStop()
        if (progressDialog != null && progressDialog!!.isShowing) {
            progressDialog!!.dismiss()
        }
    }

    override fun onBackPressed() {
//        Utils.hideKeyboard(this)
        super.onBackPressed()
    }


}
