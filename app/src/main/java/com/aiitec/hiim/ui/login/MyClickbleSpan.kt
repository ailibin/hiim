package com.aiitec.hiim.ui.login

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View
import com.aiitec.letar.ui.web.CommonWebViewActivity
import com.aiitec.hiim.base.Constants
import com.aiitec.hiim.utils.BaseUtil

class MyClickbleSpan(str: String, context: Context, type: Int) : ClickableSpan() {

    internal var string: String
    internal var context: Context
    internal var type: Int = 0

    init {
        this.string = str
        this.context = context
        this.type = type
    }

    override fun updateDrawState(ds: TextPaint) {
        ds.color = Color.parseColor("#FF9130")
        ds.isUnderlineText = false
    }


    override fun onClick(widget: View) {
        val bundle: Bundle
        val intent: Intent
        when (type) {
            1 -> {
                //用户使用协议
                bundle = Bundle()
                bundle.putString(Constants.ORG_TITLE, "用户使用协议")
                bundle.putInt(Constants.ORG_ACTION, 4)
                //需要h5对返回键处理
                bundle.putString(CommonWebViewActivity.ARG_URL, "")
                intent = Intent()
                intent.putExtras(bundle)
                intent.setClass(context, CommonWebViewActivity::class.java!!)
                context.startActivity(intent)
            }
            2 -> {
                //隐私政策
                bundle = Bundle()
                BaseUtil.showToast("隐私政策")
            }
            3 -> {
                //跳转到提现记录页面
                bundle = Bundle()
//                val intent = Intent(context, WithdrawDetailActivity::class.java)
//                context.startActivity(intent)
            }
            else -> {

            }
        }
    }


}