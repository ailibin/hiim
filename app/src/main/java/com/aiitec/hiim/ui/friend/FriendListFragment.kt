package com.aiitec.hiim.ui.friend

import android.app.Activity
import android.content.Intent
import android.os.Handler
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.View
import com.aiitec.hiim.Event
import com.aiitec.hiim.R
import com.aiitec.hiim.adapter.CommonRecyclerViewAdapter
import com.aiitec.hiim.annotation.ContentView
import com.aiitec.hiim.base.BaseKtFragment
import com.aiitec.hiim.base.Constants
import com.aiitec.hiim.im.adapter.ContactAdapter
import com.aiitec.hiim.im.entity.Contact
import com.aiitec.hiim.im.entity.ListUser
import com.aiitec.hiim.im.model.FriendProfile
import com.aiitec.hiim.im.model.FriendshipInfo
import com.aiitec.hiim.ui.NetErrDescActivity
import com.aiitec.hiim.utils.StatusBarUtil
import com.aiitec.imlibrary.presentation.presenter.FriendshipManagerPresenter
import com.aiitec.imlibrary.presentation.viewfeatures.FriendshipManageView
import com.aiitec.openapi.utils.AiiUtil
import com.aiitec.openapi.utils.LogUtil
import com.aiitec.widgets.CharacterParser
import com.aiitec.widgets.SideBar
import com.tencent.imsdk.ext.sns.TIMFriendStatus
import kotlinx.android.synthetic.main.fragment_friendlist.*
import kotlinx.android.synthetic.main.layout_empty.*
import kotlinx.android.synthetic.main.layout_nonet.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*
import kotlin.collections.ArrayList

/**
 *@author ailibin 2018/08/16
 * 好友列表
 */
@ContentView(R.layout.fragment_friendlist)
class FriendListFragment : BaseKtFragment(), Observer {

    /**
     * 好友列表
     */
    private var friends: Map<String, List<FriendProfile>>? = null
    private var friendList = ArrayList<FriendProfile>()

    companion object {
        /**
         * 新朋友请求码
         */
        val REQUEST_NEW_FRIEND = 0x01
        /**
         * 搜索好友请求码
         */
        val REQUEST_FRIEND_SEARCH = 0x02
    }

    /**
     * 联系人数据
     */
    private var datas: ArrayList<Contact>? = null
    private var pinyinComparator: PinyinComparator? = null
    private var contactAdapter: ContactAdapter? = null
    private var friendshipManagerPresenter: FriendshipManagerPresenter? = null

    override fun init(view: View) {
        StatusBarUtil.addStatusBarView(ll_titlebar_friendList, R.color.transparent)
        initConfiguration()
//        initTestData()
        //添加好友状态信息变化的监听
        FriendshipInfo.getInstance().addObserver(this)
        refreshData()
        updateData(false)
        setListener()
        EventBus.getDefault().register(this)
    }

    private fun initFriendList() {
        friends = FriendshipInfo.getInstance().friends
        friendList.clear()
        if (friends != null) {
            if (friends!!.isNotEmpty()) {
                for (key in friends!!.keys) {
                    for (profile in friends!![key]!!) {
                        friendList.add(profile)
                    }
                }
            }
            formatFriendList(friendList)
        }
    }

    /**
     * 格式化成我们本地的数据集合格式
     */
    private fun formatFriendList(friendList: List<FriendProfile>) {
        var listUser: ListUser?
        val datasTemp = ArrayList<ListUser>()
        datasTemp.clear()
        for (i in 0 until friendList.size) {
            listUser = ListUser()
            listUser.nickname = friendList[i].name
            //因为这里本地的id和腾讯云IM的identify类型不同所以不写进去了
//            listUser.id = friendList[i].identify
            listUser.imagePath = friendList[i].avatarUrl
            datasTemp.add(listUser)
        }
        getUserList(datasTemp)
    }

    override fun onResume() {
        super.onResume()
        initFriendList()
    }


    /**
     * 刷新
     */
    fun refreshData() {
        initFriendList()
    }

    /**
     * 添加测试数据,这个好友列表数据是自己添加的数据,方便测试用
     */
    private fun initTestData() {
        var listUser: ListUser?
        val datasTemp = ArrayList<ListUser>()
        datasTemp.clear()
        for (i in 0 until 6) {
            listUser = ListUser()
            when (i) {
                0 -> {
                    listUser.nickname = "南帝"
                }
                1 -> {
                    listUser.nickname = "北丐"
                }
                2 -> {
                    listUser.nickname = "中神通"
                }
                3 -> {
                    listUser.nickname = "东邪"
                }
                4 -> {
                    listUser.nickname = "西毒"
                }
                5 -> {
                    listUser.nickname = "123"
                }
                else -> {
                    listUser.nickname = "456"
                }
            }
            datasTemp.add(listUser)
        }
        getUserList(datasTemp)
    }

    /**
     * 初始化配置信息
     */
    private fun initConfiguration() {
        //比较器初始化
        pinyinComparator = PinyinComparator()
        //联系人数据初始化
        datas = ArrayList()
        contactAdapter = ContactAdapter(activity, datas)
        //这里还是要设置布局资源id
        contactAdapter?.setItemLayoutId(R.layout.item_contact)
        side_bar.setTextView(tv_contact_select_letter)
        recycler_contact.layoutManager = LinearLayoutManager(activity)
        recycler_contact.adapter = contactAdapter

        friendshipManagerPresenter = FriendshipManagerPresenter(FriendManager())
        friendshipManagerPresenter?.getFriendshipLastMessage()

        //swipeRefreshLayout刷新条的颜色
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimaryDark)
    }

    /**
     * 好友信息改变之后(比如添加成功好友,或者删除好友之后)这个页面要进行刷新
     */
    override fun update(observable: Observable?, p1: Any?) {
        if (observable is FriendshipInfo) {
            refreshData()
        }
    }


    private fun setListener() {

        //设置右侧触摸监听
        side_bar.setOnTouchingLetterChangedListener(SideBar.OnTouchingLetterChangedListener { s ->
            //该字母首次出现的位置
            val position = contactAdapter?.getPositionForSection(s[0].toInt())
            if (position != -1) {
                recycler_contact.smoothScrollToPosition(position!!)
            }
        })

        contactAdapter?.setOnRecyclerViewItemClickListener { v, position ->
            //点击好友列表,跳转到用户详情
        }

        contactAdapter?.setOnViewInItemClickListener(CommonRecyclerViewAdapter.OnViewInItemClickListener { v, position ->
            when (v.id) {
                R.id.civ_item_contact_avatar -> {
                    //点击用户头像,这里跳转到用户详情
                }
            }

        }, R.id.civ_item_contact_avatar)

        //这里为了测试,长按好友列表弹出对话框进行删除好友
        contactAdapter?.setOnRecyclerViewItemLongClickListener { v, position ->
        }

        swipeRefreshLayout.setOnRefreshListener {
            //下拉刷新
//            updateData(true)
            refreshData()
            Handler().postDelayed(Runnable {
                //这里不管数据有没有请求成功,三秒后都消失掉
                swipeRefreshLayout.isRefreshing = false
            }, 2000)
        }

        //点击网络设置的引导栏
        tv_net_guide.setOnClickListener {
            switchToActivity(NetErrDescActivity::class.java)
        }
    }

    //删除好友(这里为了测试方便)
//    private fun requestFriendSwitch(id: Long) {
//        val query = FriendSwitchRequestQuery()
//        query.setDir("sns")
//        query.action = AIIAction.THREE
//        query.open = 1
//        query.id = id
//        App.aiiRequest?.send(query, object : AIIResponse<ResponseQuery>(this, false) {
//            override fun onSuccess(response: ResponseQuery?, index: Int) {
//                super.onSuccess(response, index)
//                requestUserList(false)
//                //请求腾讯云im的删除好友(这里删除好友,对方好友界面没有刷新,不知道是哪个方法刷新的)
//                friendshipManagerPresenter?.delFriend(Constants.IM_PREFIX + id)
//            }
//        })
//    }

//    private fun showConfirmDialog(userName: String, userId: Long) {
//        val confirmDialog = CommonSelectorDialog(this)
//        confirmDialog.setTitle("确认删除")
//        confirmDialog.setContent("是否删除:$userName?")
//        confirmDialog.setAttributes()
//        confirmDialog.show()
//
//        confirmDialog.setOnPositiveListener {
//            if (confirmDialog.isShowing) {
//                confirmDialog.dismiss()
//            }
//            requestFriendSwitch(userId)
//        }
//
//        confirmDialog.setOnNegativeListener {
//            if (confirmDialog.isShowing) {
//                confirmDialog.dismiss()
//            }
//        }
//    }


    fun updateData(isPullRefresh: Boolean) {
        if (activity?.supportFragmentManager!!.isDestroyed) {
            return
        }
        //请求新朋友的数量
        requestUnReadUserNumber()
        requestUserList(isPullRefresh)
        Handler().postDelayed(Runnable {
            //这里不管数据有没有请求成功,三秒后都消失掉
            swipeRefreshLayout.isRefreshing = false
        }, 2000)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun acceptFriend(event: Event.OnReceiveFriendEvent) {
//        if (event.tag == Constant.ON_RECEIVE_FRIEND_KEY) {
//            //对方接受之后,当前用户好友列表界面需要刷新
//            updateData(false)
//        }
    }

    /**
     * 请求好友请求数字，因为推送的老是不准，所以请求协议(新朋友的数量)
     */
    private fun requestUnReadUserNumber() {
//        val query = ListRequestQuery()
//        query.setNamespace("UserList")
//        query.action = AIIAction.THREE
//        query.setDir("sns")
//        val table = query.table
//        table.limit = 1000
//        App.aiiRequest?.send(query, object : AIIResponse<UserListResponseQuery>(this, false) {
//            override fun onSuccess(response: UserListResponseQuery, index: Int) {
//                super.onSuccess(response, index)
//                //新朋友的数量
//                val number = response.total
//                var unRead = 0
//                if (number != 0) {
//                    (0 until number)
//                            .filter { response.users?.get(it)?.relationship === 2 }
//                            .forEach {
//                                //申请加我为好友(接受状态)
//                                unRead++
//                            }
//                }
//                tv_contact_unread.text = unRead.toString()
//                if (unRead > 0) {
//                    tv_contact_unread.visibility = View.VISIBLE
//                } else {
//                    tv_contact_unread.visibility = View.GONE
//                }
//                LogUtil.d("ailibin", "user: " + response.users.toString())
//
//            }
//
//            override fun onServiceError(content: String, status: Int, index: Int) {
//                super.onServiceError(content, status, index)
//            }
//
//            override fun onFinish(index: Int) {
//                super.onFinish(index)
//            }
//        })
    }


    /**
     * 1全部；2通讯录（我的好友）；3新的朋友；4黑名单,这里是用本地协议请求我的好友列表,如果是用腾讯云内部的好友,这个不用管
     */
    private fun requestUserList(pullRefresh: Boolean) {

    }


    internal inner class FriendManager : FriendshipManageView {

        override fun onAddFriend(status: TIMFriendStatus) {
            if (status == TIMFriendStatus.TIM_FRIEND_STATUS_SUCC) {
//                updateData(false)
                refreshData()
            }
        }

        override fun onDelFriend(status: TIMFriendStatus) {
            if (status == TIMFriendStatus.TIM_FRIEND_STATUS_SUCC) {
//                updateData(false)
                refreshData()
            }

        }

        override fun onChangeGroup(status: TIMFriendStatus, groupName: String) {

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //这里要处理刷新接受成功后的界面刷新
        if (requestCode == REQUEST_NEW_FRIEND && resultCode == Activity.RESULT_OK) {
            //请求添加新的好友
//            updateData(false)
            refreshData()
        }

    }

    private fun getUserList(users: List<ListUser>?) {
        datas?.clear()
        LogUtil.d("ListUser: " + users.toString())
        if (users != null) {
            for (user in users) {
                val contact = Contact()
                contact.identify = Constants.IM_PREFIX + user.id
                contact.imagePath = user.imagePath
                contact.name = user.nickname
                contact.userId = user.id
                //加入备注信息
                contact.alias = user.alias
                AiiUtil.putString(activity, "avatar_" + Constants.IM_PREFIX + user.id, user.imagePath)
                AiiUtil.putString(activity, "nickname_" + Constants.IM_PREFIX + user.id, user.nickname)
                datas?.add(contact)
                LogUtil.d("getUserList: " + datas.toString())

            }
        }
        filledData(datas!!)
        // 根据a-z进行排序源数据
        Collections.sort(datas, pinyinComparator)
        checkIsEmpty()
        contactAdapter?.update()
    }


    /**
     * 为ListView填充数据
     *
     * @param data
     * @return
     */
    private fun filledData(data: List<Contact>) {
        SideBar.b.clear()
        val characterParser = CharacterParser()
        for (i in data.indices) {
            val contact = data[i]
            //汉字转换成拼音
            var pinyin = ""
            pinyin = if (!TextUtils.isEmpty(contact.alias)) {
                //备注不为空,就按照备注来
                characterParser.getSelling(contact.alias)
            } else {
                characterParser.getSelling(contact.name)
            }
//            val pinyin = characterParser.getSelling(contact.name)
            val sortString = pinyin.substring(0, 1).toUpperCase()

            // 正则表达式，判断首字母是否是英文字母
            if (sortString.matches("[A-Z]".toRegex())) {
                contact.sortLetters = sortString.toUpperCase()
            } else {
                contact.sortLetters = "#"
            }
            if (!SideBar.b.contains(contact.sortLetters)) {
                SideBar.b.add(contact.sortLetters)
            }
        }
        Collections.sort(SideBar.b, PinyinComparator2())
        side_bar.postInvalidate()
    }


    internal inner class PinyinComparator : Comparator<Contact> {

        override fun compare(o1: Contact, o2: Contact): Int {
            //这里主要是用来对ListView里面的数据根据ABCDEFG...来排序
            return if ("#" == o2.sortLetters) {
                -1
            } else if ("#" == o1.sortLetters) {
                1
            } else {
                o1.sortLetters.compareTo(o2.sortLetters)
            }
        }
    }

    internal inner class PinyinComparator2 : Comparator<String> {

        override fun compare(o1: String, o2: String): Int {
            //这里主要是用来对ListView里面的数据根据ABCDEFG...来排序
            return if ("#" == o2) {
                -1
            } else if ("#" == o1) {
                1
            } else {
                o1.compareTo(o2)
            }
        }
    }

    /**
     * 无数据
     */
    protected fun checkIsEmpty() {
        if (datas?.size == 0 && tv_no_data != null) {
            if (tv_no_data != null) {
                tv_no_data.visibility = View.GONE
            }
            tv_no_data?.visibility = View.VISIBLE
            recycler_contact.visibility = View.GONE
        } else {
            if (tv_no_data != null) {
                tv_no_data.visibility = View.GONE
            }
            recycler_contact.visibility = View.VISIBLE
        }
    }

    /**
     * 无网络
     */
    protected fun onNetError() {
        if (datas?.size == 0 && ll_no_net != null) {
            if (tv_no_data != null) {
                tv_no_data.visibility = View.GONE
            }
            recycler_contact.visibility = View.GONE
            ll_no_net?.visibility = View.VISIBLE
        } else {
            ll_no_net?.visibility = View.GONE
            recycler_contact.visibility = View.VISIBLE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

}