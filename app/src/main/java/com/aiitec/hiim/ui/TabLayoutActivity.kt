package com.aiitec.hiim.ui

import android.os.Bundle
import android.support.design.widget.TabLayout
import com.aiitec.hiim.R
import com.aiitec.hiim.adapter.SimpleFragmentPagerAdapter
import com.aiitec.hiim.annotation.ContentView
import com.aiitec.hiim.base.App
import com.aiitec.hiim.base.BaseKtActivity
import com.aiitec.hiim.ui.home.ConversationFragment
import com.aiitec.widgets.CommonDialog
import kotlinx.android.synthetic.main.activity_tablayout.*

/**
 * @author  Anthony
 * @version 1.0
 * createTime 2017/11/18.
 */
@ContentView(R.layout.activity_tablayout)
class TabLayoutActivity : BaseKtActivity() {

    lateinit var mPagerAdapter: SimpleFragmentPagerAdapter
    private var position: Int = 0
    lateinit var exitDialog : CommonDialog

    override fun init(savedInstanceState: Bundle?) {
        position = bundle.getInt("position")
        mPagerAdapter = SimpleFragmentPagerAdapter(supportFragmentManager, this)
        mPagerAdapter.addFragment(ConversationFragment(), "请假审批")
        mPagerAdapter.addFragment(ConversationFragment(), "课程表审批")
        mPagerAdapter.addFragment(ConversationFragment(), "请假审批2")
        mPagerAdapter.addFragment(ConversationFragment(), "课程表审批2")
        mPagerAdapter.addFragment(ConversationFragment(), "请假审批3")
        mPagerAdapter.addFragment(ConversationFragment(), "课程表审批3")

        viewpager.adapter = mPagerAdapter
        tablayout.setupWithViewPager(viewpager)
        tablayout.tabMode = TabLayout.MODE_SCROLLABLE


        exitDialog = CommonDialog(this)
        exitDialog.setTitle("退出程序")
        val appName = resources.getString(R.string.app_name)
        exitDialog.setContent("确定退出$appName")
        exitDialog.setOnConfirmClickListener {
            exitDialog.dismiss()
            App.getInstance().closeAllActivity()
        }

    }

    override fun onBackPressed() {
        if(exitDialog.isShowing){
            exitDialog.show()
        }
    }


}
