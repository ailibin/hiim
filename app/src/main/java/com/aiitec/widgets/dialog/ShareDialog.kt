package com.aiitec.widgets.dialog

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.support.v7.widget.GridLayoutManager
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import com.aiitec.entitylibary.model.WxShareInfo
import com.aiitec.hiim.R
import com.aiitec.hiim.adapter.CommonRecyclerViewAdapter
import com.aiitec.hiim.adapter.CommonRecyclerViewHolder
import com.aiitec.hiim.utils.ShareUtils
import com.aiitec.openapi.utils.LogUtil
import com.aiitec.openapi.utils.ToastUtil
import com.aiitec.widgets.AbsCommonDialog
import com.umeng.socialize.bean.SHARE_MEDIA
import kotlinx.android.synthetic.main.dialog_share.*

/**
 * Created by ailibin on 2018/5/5.
 */
class ShareDialog(context: Context) : AbsCommonDialog(context) {

    lateinit var adapter: ShareAdapter
    lateinit var datas: ArrayList<String>
    lateinit var titles: Array<String>
    var shareUtils: ShareUtils? = null
    private var clipboardManager: ClipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

    override fun widthScale(): Float = 1f
    override fun layoutId(): Int = R.layout.dialog_share
    override fun animStyle(): Int = R.style.BottomAnimationStyle

    private var shareInfo: WxShareInfo? = null
    //分享类型
    private var shareType: Int = 0
    private var bitmap: Bitmap? = null
    private var tvCancel: TextView? = null
    private var path: String? = null
    private var shareUrl: String? = null

    override fun findView(view: View?) {
        super.findView(view)
        tvCancel = view?.findViewById(R.id.tv_dialog_cancel)
        shareUtils = ShareUtils(context)
        datas = ArrayList()
        titles = arrayOf("微信好友", "QQ好友", "朋友圈", "复制链接")
        for (title in titles) {
            datas.add(title)
        }
        recyclerView.layoutManager = GridLayoutManager(context, 4)
        adapter = ShareAdapter(context, datas)
        adapter.setOnRecyclerViewItemClickListener { _, position ->
            when (datas[position]) {
                titles[0] -> {
                    shareUtils?.directShare(SHARE_MEDIA.WEIXIN)
                }
                titles[1] -> {
                    shareUtils?.directShare(SHARE_MEDIA.QQ)
                }
                titles[2] -> {
                    shareUtils?.directShare(SHARE_MEDIA.WEIXIN_CIRCLE)
                }
                titles[3] -> {
                    //复制链接
//                    shareUtils?.directShare(SHARE_MEDIA.SINA)
                    shareUrl?.let {
                        copyText(it)
                    }
                }
            }
        }
        recyclerView.adapter = adapter
        tvCancel?.setOnClickListener {
            //点击取消按钮
            if (this.isShowing) {
                dismiss()
            }
        }
    }

    fun setCancelBottomVisible(isVisible: Boolean) {
        if (isVisible) {
            tvCancel?.visibility = View.VISIBLE
        } else {
            tvCancel?.visibility = View.GONE
        }
    }

    /**
     * 复制链接
     */
    private fun copyText(url: String) {
        if (TextUtils.isEmpty(url)) {
            return
        }
        // 将文本内容放到系统剪贴板里。
        clipboardManager.primaryClip = ClipData.newPlainText(null, url)
        ToastUtil.show(context, "复制成功")
        this.dismiss()
    }

    protected fun initDialog() {

    }

    override fun setGravity(params: WindowManager.LayoutParams?) {
        params?.gravity = Gravity.BOTTOM
    }


    fun setAttributes() {
        val window = window
        val params = window!!.attributes
        params.gravity = Gravity.BOTTOM
        window.attributes = setLayoutParams(params)
    }

    fun setAttributes1() {
        val display = (context as Activity).windowManager.defaultDisplay
        val params = this.window.attributes
        params.width = display.width
        this.window.attributes = params
        this.window.setGravity(Gravity.BOTTOM)
    }

    fun setShareContent(url: String?, title: String?, content: String?, imagepath: String?) {
        shareUtils?.setShareUrl(url)
        shareUtils?.setShareTitle(title)
        shareUtils?.setShareContent(content)
        shareUtils?.setShareImage(imagepath)
        //复制链接用到
        shareUrl = url
        LogUtil.e("ailibin", "shareUrl: $shareUrl")
    }

    /**
     * 分享一张图
     */
    fun setShareContent(path: String, url: String) {
        shareUtils?.setShareImage(path)
        shareUrl = url
    }

    //shareType==1 分享商品链接  shareType==2分享用户二维码
    fun setShareContent(shareInfo: WxShareInfo, shareType: Int) {
        this.shareInfo = shareInfo
        this.shareType = shareType
    }

    fun setShareUrl(path: String?) {
        shareUtils?.setShareUrl(path)
        this.path = path
    }

    fun setShareContent(shareInfo: WxShareInfo) {
        if (shareInfo.shareType == 0) {
            //分享链接
            shareUtils?.setShareUrl(shareInfo.url)
            shareUtils?.setShareTitle(shareInfo.title)
            shareUtils?.setShareContent(shareInfo.description)
            shareUtils?.setShareImage(shareInfo.imageUrl)
        } else if (shareInfo.shareType == 1) {
            //分享单图片
            shareUtils?.setShareImage(shareInfo.imageUrl)
        }
        this.shareInfo = shareInfo
    }


    fun setShareContent(bitmap: Bitmap, shareType: Int) {
        this.bitmap = bitmap
        this.shareType = shareType
    }

    fun setShareContent(bitmap: Bitmap, url: String) {
        this.bitmap = bitmap
        shareUtils?.setShareImage(bitmap)
        shareUrl = url
    }


    /**
     * 设置显示几个item
     * 比如 setVisibleItems(0, 2, 4) 就显示微信好友，QQ好友， 新浪微博
     */
    fun setVisibleItems(vararg positions: Int) {
        datas.clear()
        positions.forEach { datas.add(titles[it]) }
        adapter.update()
    }

    private var onItemClickListener: ((item: String, position: Int) -> Unit?)? = null

    fun setOnItemClickListener(listener: ((item: String, position: Int) -> Unit)) {
        onItemClickListener = listener
    }


    inner class ShareAdapter(context: Context, datas: MutableList<String>) : CommonRecyclerViewAdapter<String>(context, datas) {
        override fun convert(h: CommonRecyclerViewHolder, item: String, position: Int) {
            val tvName = h.getView<TextView>(R.id.tv_item_name)
            val ivImage = h.getView<ImageView>(R.id.iv_item_img)
            tvName.text = item
            when (item) {
                titles[0] -> {
                    ivImage.setImageResource(R.drawable.share_btn_wechat)
                }
                titles[1] -> {
                    ivImage.setImageResource(R.drawable.share_btn_qq)
                }
                titles[2] -> {
                    ivImage.setImageResource(R.drawable.share_btn_circle_of_friendes)
                }
                titles[3] -> {
                    ivImage.setImageResource(R.drawable.share_btn_link)
                }
            }
        }

        override fun getLayoutViewId(viewType: Int): Int = R.layout.item_share

    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        shareUtils?.onActivityResult(requestCode, resultCode, data)
        dismiss()
    }
}