package com.aiitec.hiim.base

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.aiitec.hiim.R
import com.aiitec.hiim.utils.ContentViewUtils
import com.aiitec.hiim.utils.StatusBarUtil
import com.aiitec.widgets.CustomProgressDialog
import com.umeng.analytics.MobclickAgent
import java.io.Serializable

/**
 * Fragment 基类
 */
abstract class BaseKtFragment : Fragment() {

    /**
     * 标题栏相关
     */
    var ll_title_bar: LinearLayout? = null
    var tv_title_column: TextView? = null
    var tv_right_action: TextView? = null
    var iv_go_back: ImageView? = null
    var iv_right_action: ImageView? = null

    private var hasLoadData = false
    private var isCreated = false
    //避免toast弹出多个,界面半天之后都没有消失
    private var mToast: Toast? = null

    fun Fragment.toast(message: CharSequence) {
        activity?.let {
            if (mToast == null) {
                mToast = Toast.makeText(activity, "", Toast.LENGTH_SHORT)
            }
            mToast?.duration = Toast.LENGTH_SHORT
            mToast?.setText(message.toString())
            mToast?.show()
        }

    }

    fun Fragment.toast(messageRes: Int) {
        activity?.let {
            if (mToast == null) {
                mToast = Toast.makeText(activity, "", Toast.LENGTH_SHORT)
            }
            mToast?.duration = Toast.LENGTH_SHORT
            mToast?.setText(messageRes)
            mToast?.show()
        }
    }

    var progressDialog: CustomProgressDialog? = null
    var toolbar: Toolbar? = null
    var tv_title: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        progressDialog = CustomProgressDialog(activity)
        progressDialog?.setMessage("正在加载中...")
        setHasOptionsMenu(true)
    }


    fun setToolBar(toolBar: Toolbar?) {
        if (toolBar == null) return
        this.toolbar = toolBar
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        val actionBar = (activity as AppCompatActivity).supportActionBar
        actionBar!!.setDisplayShowTitleEnabled(false)
        actionBar.setDisplayHomeAsUpEnabled(false)
        actionBar.setHomeButtonEnabled(false)
        setHasOptionsMenu(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            activity?.finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = ContentViewUtils.inject(this)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tv_title = view.findViewById<TextView?>(R.id.tv_title)

        //标题栏(抽取)
        ll_title_bar = activity?.findViewById<LinearLayout?>(R.id.ll_title_bar)
        //标题栏标题
        tv_title_column = activity?.findViewById<TextView?>(R.id.tv_title_column)
        //标题栏返回按钮
        iv_go_back = activity?.findViewById<ImageView?>(R.id.iv_go_back)
        //右侧按钮的操作事件
        tv_right_action = activity?.findViewById<TextView?>(R.id.tv_right_action)
        //右侧图标
        iv_right_action = activity?.findViewById<ImageView?>(R.id.iv_right_action)

        iv_go_back?.setOnClickListener {
            if (!childFragmentManager.isDestroyed) {
                activity?.finish()
            }
        }
//        if (this.javaClass == MineFragment::class.java) {
//            //我的界面状态栏白色主题
//            StatusBarUtil.StatusBarDarkMode(activity)
//        }else{
//            //设置状态栏的文字为黑色主题
//            StatusBarUtil.StatusBarLightMode(activity)
//        }

        init(view)
    }

    protected abstract fun init(view: View)

    /**
     *设置左侧按钮的点击事件
     */
    fun setLeftBtnClickListener(listener: View.OnClickListener) {
        iv_go_back?.setOnClickListener(listener)
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
     * 设置最右侧的图标
     */
    fun setRightImageViewRes(imageResId: Int) {
        iv_right_action?.visibility = View.VISIBLE
        if (imageResId > 0) {
            iv_right_action?.setImageResource(imageResId)
        }
    }

    /**
     * 右侧的图标点击事件
     */
    fun setRightImageClickListener(listener: View.OnClickListener) {
        iv_right_action?.setOnClickListener(listener)
    }


    /**
     * 设置右边按钮的文本信息
     */
    fun setRightBtnText(textStr: CharSequence) {
        tv_right_action?.visibility = View.VISIBLE
        tv_right_action?.text = textStr
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

    fun addBaseStatusBarView(view: View) {
//        val statusBarHeight = StatusBarUtil.getStatusBarHeight(activity)
//        view?.setPadding(0, statusBarHeight, 0, 0)
//        StatusBarUtil.setWhiteStatusBar(activity)
//        StatusBarUtil.setStatusBarColor(activity, R.color.transparent)
        StatusBarUtil.addStatusBarView(view, R.color.transparent)
    }


    /**
     * 设置标题的沉浸式状态栏(设置状态栏的颜色为透明)
     */
    fun addBaseStatusBarView() {
    }

    /**
     * mode状态栏字体切换模式,是白色还是黑色 mode=1白色 mode=2黑色
     */
    open fun switchStatusMode(mode: Int) {
        if (mode == 1) {
            StatusBarUtil.StatusBarDarkMode(activity)
        } else if (mode == 2) {
            StatusBarUtil.StatusBarLightModeNew(activity)
        }
    }


    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        var context: Context?
        context = activity
        if (context == null) {
            context = App.getInstance().applicationContext
        }
        if (progressDialog == null) {
            progressDialog = CustomProgressDialog(context!!)
        }

    }


    fun switchToActivity(clazz: Class<*>) {
        val intent = Intent(activity, clazz)
        startActivity(intent)
    }

    fun switchToActivity(clazz: Class<*>, bundle: Bundle) {
        val intent = Intent(activity, clazz)
        intent.putExtras(bundle)
        startActivity(intent)
    }

    fun switchToActivityForResult(clazz: Class<*>, requestCode: Int) {
        val intent = Intent(activity, clazz)
        startActivityForResult(intent, requestCode)
    }

    fun switchToActivityForResult(clazz: Class<*>, bundle: Bundle?, requestCode: Int) {
        val intent = Intent(activity, clazz)
        if (bundle != null)
            intent.putExtras(bundle)
        startActivityForResult(intent, requestCode)
    }

    fun switchToActivity(clazz: Class<*>, anim_in: Int, anim_out: Int) {
        val intent = Intent(activity, clazz)
        startActivity(intent)
        activity?.overridePendingTransition(anim_in, anim_out)
    }

    fun switchToActivity(clazz: Class<*>, bundle: Bundle, anim_in: Int, anim_out: Int) {
        val intent = Intent(activity, clazz)
        intent.putExtras(bundle)
        startActivity(intent)
        activity?.overridePendingTransition(anim_in, anim_out)
    }

    fun switchToActivity(clazz: Class<*>, vararg pairs: Pair<String, Any?>) {
        val intent = Intent(activity, clazz)
        intent.putExtras(getBundleExtras(pairs))
        startActivity(intent)
    }

    fun switchToActivityForResult(clazz: Class<*>, requestCode: Int, vararg pairs: Pair<String, Any?>) {
        val intent = Intent(activity, clazz)
        intent.putExtras(getBundleExtras(pairs))
        startActivityForResult(intent, requestCode)
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

    override fun onResume() {
        super.onResume()
        MobclickAgent.onPageStart(javaClass.getSimpleName()) //统计页面
    }

    override fun onPause() {
        super.onPause()
        MobclickAgent.onPageEnd(javaClass.getSimpleName())
    }

    override fun onDestroy() {
        super.onDestroy()
    }


    fun progressDialogShow() {
        try {
            if (progressDialog != null && !progressDialog!!.isShowing) {
                progressDialog!!.show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun progressDialogDismiss() {
        try {
            if (progressDialog != null && progressDialog!!.isShowing) {
                progressDialog!!.dismiss()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
}