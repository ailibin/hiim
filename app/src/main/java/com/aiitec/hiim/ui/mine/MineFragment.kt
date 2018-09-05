package com.aiitec.hiim.ui.mine

import android.view.View
import com.aiitec.hiim.R
import com.aiitec.hiim.annotation.ContentView
import com.aiitec.hiim.base.BaseKtFragment
import com.aiitec.hiim.utils.StatusBarUtil
import kotlinx.android.synthetic.main.fragment_mine.*

/**
 *@author ailibin 2018/08/16
 * 我的
 */
@ContentView(R.layout.fragment_mine)
class MineFragment : BaseKtFragment() {
    override fun init(view: View) {
        //文字变成白色
        StatusBarUtil.addStatusBarView(ll_titleBar_mine, R.color.transparent)
        setListener()
    }

    private fun setListener() {

        ll_logistics_info.setOnClickListener {
        }

    }

}