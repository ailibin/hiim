package com.aiitec.hiim.ui.circle

import android.view.View
import com.aiitec.hiim.R
import com.aiitec.hiim.annotation.ContentView
import com.aiitec.hiim.base.BaseKtFragment
import com.aiitec.hiim.utils.StatusBarUtil
import kotlinx.android.synthetic.main.fragment_circle.*

/**
 *@author ailibin 2018/08/16
 * 新鲜事情(圈子)
 */
@ContentView(R.layout.fragment_circle)
class CircleFragment : BaseKtFragment() {
    override fun init(view: View) {
        StatusBarUtil.addStatusBarView(ll_titlebar_circle, R.color.transparent)
    }
}