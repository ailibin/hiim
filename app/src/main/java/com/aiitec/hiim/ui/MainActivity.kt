package com.aiitec.hiim.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.aiitec.hiim.R
import com.aiitec.hiim.annotation.ContentView
import com.aiitec.hiim.base.App
import com.aiitec.hiim.base.BaseKtActivity
import com.aiitec.hiim.base.Constants
import com.aiitec.hiim.base.Constants.ARG_TYPE
import com.aiitec.hiim.base.Event
import com.aiitec.hiim.im.model.*
import com.aiitec.hiim.im.utils.AiiUtil
import com.aiitec.hiim.im.utils.LogUtil
import com.aiitec.hiim.im.utils.PushUtil
import com.aiitec.hiim.ui.friend.FriendListFragment
import com.aiitec.hiim.ui.home.ConversationFragment
import com.aiitec.hiim.ui.mine.MineFragment
import com.aiitec.hiim.utils.LocationUtils
import com.aiitec.hiim.utils.PermissionsUtils
import com.aiitec.hiim.utils.ScreenUtils
import com.aiitec.imlibrary.presentation.presenter.ConversationPresenter
import com.aiitec.imlibrary.presentation.presenter.FriendshipManagerPresenter
import com.aiitec.imlibrary.presentation.viewfeatures.ConversationView
import com.aiitec.imlibrary.presentation.viewfeatures.FriendshipMessageView
import com.aiitec.widgets.CommonDialog
import com.tencent.imsdk.*
import com.tencent.imsdk.ext.group.TIMGroupCacheInfo
import com.tencent.imsdk.ext.sns.TIMFriendFutureItem
import kotlinx.android.synthetic.main.activity_tabhost.*
import org.greenrobot.eventbus.EventBus
import java.util.*

/**
 * 主页
 * 包含 首页  商品 购物车 我的 四个模块
 * @author ailibin
 * @time 2018/08/17
 *
 */
@ContentView(R.layout.activity_main)
class MainActivity : BaseKtActivity(), Observer, ConversationView {


    var msgUnreads = arrayListOf<TextView>()
    private val fragmentArray = arrayOf<Class<*>>(
            ConversationFragment::class.java,
            FriendListFragment::class.java,
            MineFragment::class.java)
    private val mImageViewArray = intArrayOf(
            R.drawable.tab_2_selector,
            R.drawable.tab_1_selector,
            R.drawable.tab_4_selector)
    private var titleArray = arrayOf("聊天", "好友", "我的")

    lateinit var commonDialog: CommonDialog
    private var tabIndexLast = 0
    private var onLogoutReceiver: OnLogoutReceiver? = null
    private var friendshipManagerPresenter: FriendshipManagerPresenter? = null
    private var c2cConversationList = ArrayList<Conversation>()
    private var presenter: ConversationPresenter? = null
    private var currentIndex = 0
    /**
     * 未读消息数量
     */
    private var unReadMsgNum = 190
    //圈子消息未读数量
    private var unReadMsgNum1 = 190
    private var sendName: String? = null
    private var permissionsUtils: PermissionsUtils? = null
    private var locationUtils: LocationUtils? = null
    private var REQUEST_LOCATION = 0x110
    private var lastTime: Long = 0
    private var currentTime: Long = 0

    override fun init(savedInstanceState: Bundle?) {

        tabhost.setup(this, supportFragmentManager, R.id.contentPanel)
        val fragmentCount = fragmentArray.size
        for (i in 0 until fragmentCount) {
            //为每一个Tab按钮设置图标、文字和内容
            val tabSpec = tabhost.newTabSpec(titleArray[i]).setIndicator(getTabItemView(i))
            //将Tab按钮添加进Tab选项卡中
            val bundle = Bundle()
            bundle.putInt(ARG_TYPE, i)
            tabhost.addTab(tabSpec, fragmentArray[i], bundle)
            tabhost.tabWidget.dividerDrawable = null
        }

        for (i in 0 until tabhost.tabWidget.childCount) {
            tabhost.tabWidget.getChildAt(i).setOnClickListener(View.OnClickListener {
                tabhost.currentTab = i
                if (i == 0) {
                    //聊天界面,底部操作栏需要双击
                    lastTime = currentTime
                    currentTime = System.currentTimeMillis()
                    if (currentTime - lastTime < 500) {
                        //双击,依次滚动到相应的未读消息
                        postScrollEvent()
                    }
                }
            })
        }


        //使用这个控件点击事件才有效,这个事件和setOnTabChangedListener有冲突啊
//        icon.setOnClickListener {
//            //聊天界面,底部操作栏需要双击
//            if (currentIndex == 0) {
//                lastTime = currentTime
//                currentTime = System.currentTimeMillis()
//                if (currentTime - lastTime < 500) {
//                    //双击,依次滚动到相应的未读消息
//                    postScrollEvent()
//                }
//            }
//        }

        locationUtils = LocationUtils(this)
        permissionsUtils?.setOnPermissionsListener(object : PermissionsUtils.OnPermissionsListener {
            override fun onPermissionsSuccess(requestCode: Int) {
                if (requestCode == REQUEST_LOCATION) {
                    locationUtils?.startLocation()
                }
            }

            override fun onPermissionsFailure(requestCode: Int) {

            }

        })
        permissionsUtils?.requestPermissions(REQUEST_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION)
        setMsgUnread(2, unReadMsgNum1)
        tabhost.setOnTabChangedListener { s ->
            when (s) {
                titleArray[0] -> {
                    currentIndex = 0
//                    val homeFragment = supportFragmentManager.findFragmentByTag(s) as ConversationFragment?
//                    //文字变黑色
//                    if (homeFragment != null) {
//                        homeFragment?.switchStatusMode(2)
//                    }
                }
                titleArray[1] -> {
                    currentIndex = 1
//                    val commodityFragment = supportFragmentManager.findFragmentByTag(s) as FriendListFragment?
//                    //文字变黑色
//                    if (commodityFragment != null) {
//                        commodityFragment?.switchStatusMode(2)
//                    }
                }
                titleArray[2] -> {
                    currentIndex = 2
//                    val cartFragment = supportFragmentManager.findFragmentByTag(s) as CircleFragment?
//                    //文字变黑色
//                    if (cartFragment != null) {
//                        cartFragment?.switchStatusMode(2)
//                    }
                }
            }
        }

        getFriendMessageInfo()
        commonDialog = CommonDialog(this)
        commonDialog.setTitle("退出嗨APP？")
        onLogoutReceiver = OnLogoutReceiver()
    }

    /**
     * 发送一个通知会话界面滚动
     */
    private fun postScrollEvent() {
        val event = Event.OnScrollChangeEvent()
        event.tag = Constants.ORG_ONSCROLL_EVENT
        EventBus.getDefault().post(event)
    }


    private fun getTabItemView(index: Int): View {

        val view = layoutInflater.inflate(R.layout.home_tab, null)
        val icon = view.findViewById<ImageView>(R.id.icon)
        icon.setImageResource(mImageViewArray[index])
        val title = view.findViewById<TextView>(R.id.title)
        title.text = titleArray[index]

        if (msgUnreads.size > index) {
            msgUnreads[index] = view.findViewById(R.id.tabUnread)
        } else {
            msgUnreads.add(view.findViewById(R.id.tabUnread))
        }
        return view
    }

    fun setMsgUnread(index: Int, number: Int) {
        if (msgUnreads.size > index) {
            msgUnreads[index].let {
                if (index == 2) {
                    //只显示是否有未读的消息,不显示未读消息的数量,朋友圈
                    if (number <= 0) {
                        it.visibility = View.GONE
                    } else {
                        it.visibility = View.VISIBLE
                        //只显示一个红点
                        val width = ScreenUtils.dip2px(this, 10f)
                        val params = it?.layoutParams as RelativeLayout.LayoutParams
                        params.width = width
                        params.height = width
                        it.layoutParams = params
                        it.text = ""
                    }
                } else {
                    refreshMessageNumber(it, number)
                }
            }
        }


    }

    /**
     * 根据消息未读数量显示不同布局
     */
    fun refreshMessageNumber(tv: TextView, number: Int) {

        if (number <= 0) {
            tv.visibility = View.GONE
            return
        }
        tv.visibility = View.VISIBLE
        if (number < 10) {
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11f)
            tv.text = "$number"
        } else {
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 9f)
            if (number > 99) {
                tv.text = "99+"
            } else {
                tv.text = "$number"
            }
        }
    }

    fun getMsgUnread(index: Int): Int {
        if (msgUnreads.size > index) {
            msgUnreads[index].let {
                try {
                    return Integer.parseInt(it.text.toString())
                } catch (e: NumberFormatException) {
                }
            }
        }
        return 0
    }


    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        // 必须要调用这句
        setIntent(intent)
    }

    override fun onResume() {
        super.onResume()
        val id = intent.getIntExtra("tabHost", tabIndexLast)
        tabhost.currentTab = id
        intent = Intent()
        updateC2cUnRead()
    }

    override fun onStop() {
        tabIndexLast = tabhost.currentTab
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(onLogoutReceiver)
//        EventBus.getDefault().unregister(this)
    }

    //点返回键退出
    override fun onBackPressed() {
        commonDialog.show()
        commonDialog.setOnConfirmClickListener {
            commonDialog.dismiss()
            //这行代码必须加,否则一直退不出去
            App.getInstance().exitHome()
            super.onBackPressed()
        }
    }

    /**
     * 从IM中获取新朋友的资料
     */
    private fun getFriendMessageInfo() {
        presenter = ConversationPresenter(this)
        presenter?.getConversation()
        friendshipManagerPresenter = FriendshipManagerPresenter(object : FriendshipMessageView {
            override fun onGetFriendshipLastMessage(message: TIMFriendFutureItem, unreadCount: Long) {
                LogUtil.e("onGetFriendshipLastMessage:   $unreadCount")
                if (unreadCount > 0) {
                    val isFirst = AiiUtil.getBoolean(this@MainActivity, "firstPushContactNotify" + message.identifier, true)
                    if (isFirst) {
                        AiiUtil.putBoolean(this@MainActivity, "firstPushContactNotify" + message.identifier, false)
                        val friendCon = FriendshipConversation(message)
                        //推送标题
                        sendName = friendCon.name
                        //获取用户资料
                        val identifiers = ArrayList<String>()
                        identifiers.add(message.identifier)
                        TIMFriendshipManager.getInstance().getUsersProfile(identifiers, object : TIMValueCallBack<List<TIMUserProfile>> {
                            override fun onError(i: Int, s: String) {
                                PushUtil.getInstance().pushNotify(sendName, friendCon.lastMessageSummary)
                            }

                            override fun onSuccess(timUserProfiles: List<TIMUserProfile>) {
                                sendName = timUserProfiles[0].nickName
                                if (TextUtils.isEmpty(sendName)) {
//                                    requestUserDetails(2, message.identifier, friendCon.lastMessageSummary)
                                } else {
                                    PushUtil.getInstance().pushNotify(sendName, friendCon.lastMessageSummary)
                                }
                            }
                        })

                    }
                }

            }

            override fun onGetFriendshipMessage(message: List<TIMFriendFutureItem>) {
            }
        })
        FriendshipInfo.getInstance().addObserver(this)
        friendshipManagerPresenter?.getFriendshipLastMessage()
    }

    private fun updateC2cUnRead() {
        setMsgUnread(0, getTotalC2cUnreadNum().toInt())
    }


    /**
     * 新朋友消息刷新
     */
    override fun update(observable: Observable?, p1: Any?) {

        if (observable is FriendshipInfo) {
            friendshipManagerPresenter?.getFriendshipLastMessage()
        }

        try {
            //这里暂时不知道为什么会发生异常,先try,catch
            if (tabhost.currentTab == 1) {
                val conversationFragment = supportFragmentManager.findFragmentByTag(titleArray[1]) as ConversationFragment
                if (conversationFragment != null) {
                    //这里是更新新朋友的数量
//                    conversationFragment?.refreshNewFriendNumber()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    private fun getTotalC2cUnreadNum(): Long {
        var num: Long = 0
        for (conversation in c2cConversationList) {
            num += conversation.unreadNum
        }
        return num
    }

    /**
     * 通知新消息未读数量
     */
    override fun initView(conversationList: MutableList<TIMConversation>?) {
        this.c2cConversationList.clear()
        if (conversationList != null && conversationList?.size!! > 0) {
            for (item in conversationList!!) {
                when (item.type) {
                    TIMConversationType.C2C -> {
                        //这里把群消息过滤掉
                        this.c2cConversationList.add(NormalConversation(item))
                    }
                    TIMConversationType.Group -> {
//                        this.groupConversationList.add(NormalConversation(item))
                    }
                    else -> {

                    }
                }
            }
        }
        refresh()

    }

    /**
     * 更新消息数量
     */
    override fun updateMessage(message: TIMMessage?) {

        LogUtil.e("ailibin", "updateMessage123")
        if (message == null) {
            updateC2cUnRead()
            return
        }
        if (message.conversation.type == TIMConversationType.System) {
            // groupManagerPresenter.getGroupManageLastMessage();
            return
        }
        val message1 = MessageFactory.getMessage(message)
        if (MessageFactory.getMessage(message) is CustomMessage) {
        }
        var conversation = NormalConversation(message.conversation)
        if (conversation.type === TIMConversationType.C2C) {
            val iterator = c2cConversationList.iterator()
            while (iterator.hasNext()) {
                val c = iterator.next()
                if (conversation == c) {
                    conversation = c as NormalConversation
                    iterator.remove()
                    break
                }
            }
            conversation.setLastMessage(MessageFactory.getMessage(message))
            c2cConversationList.add(conversation)
        }
        refresh()
    }

    override fun updateFriendshipMessage() {

    }

    override fun removeConversation(identify: String?) {

        val iterator = c2cConversationList.iterator()
        while (iterator.hasNext()) {
            val conversation = iterator.next()
            if (conversation.identify != null && conversation.identify == identify) {
                iterator.remove()
                updateC2cUnRead()
                return
            }
        }

    }

    override fun updateGroupInfo(info: TIMGroupCacheInfo?) {

    }

    override fun refresh() {
        updateC2cUnRead()
    }


    /**
     * 用户在其它设备登录
     */
    inner class OnLogoutReceiver : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionsUtils?.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

}
