package com.aiitec.hiim.ui.home

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.TextUtils
import android.view.*
import android.widget.AdapterView
import android.widget.ListView
import android.widget.PopupWindow
import android.widget.RelativeLayout
import com.aiitec.hiim.R
import com.aiitec.hiim.annotation.ContentView
import com.aiitec.hiim.base.BaseKtFragment
import com.aiitec.hiim.base.Constants
import com.aiitec.hiim.base.Event
import com.aiitec.hiim.im.adapter.ConversationAdapter
import com.aiitec.hiim.im.adapter.MemberOperationAdapter
import com.aiitec.hiim.im.entity.OperationContent
import com.aiitec.hiim.im.model.*
import com.aiitec.hiim.im.model.CustomMessage.Type.TYPING
import com.aiitec.hiim.im.utils.AiiUtil
import com.aiitec.hiim.im.utils.LogUtil
import com.aiitec.hiim.im.utils.PushUtil
import com.aiitec.hiim.utils.BaseUtil
import com.aiitec.hiim.utils.ScreenUtils
import com.aiitec.hiim.utils.StatusBarUtil
import com.aiitec.imlibrary.presentation.presenter.ConversationPresenter
import com.aiitec.imlibrary.presentation.presenter.FriendshipManagerPresenter
import com.aiitec.imlibrary.presentation.presenter.GroupManagerPresenter
import com.aiitec.imlibrary.presentation.viewfeatures.ConversationView
import com.aiitec.imlibrary.presentation.viewfeatures.FriendshipMessageView
import com.aiitec.imlibrary.presentation.viewfeatures.GroupManageMessageView
import com.aiitec.widgets.CommonDialog
import com.daimajia.swipe.util.Attributes
import com.tencent.imsdk.TIMConversation
import com.tencent.imsdk.TIMConversationType
import com.tencent.imsdk.TIMMessage
import com.tencent.imsdk.ext.group.TIMGroupCacheInfo
import com.tencent.imsdk.ext.group.TIMGroupPendencyItem
import com.tencent.imsdk.ext.sns.TIMFriendFutureItem
import kotlinx.android.synthetic.main.fragment_conversation.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*
import kotlin.collections.ArrayList

/**
 * @author ailibin
 * @time 2018/08/16
 * 聊天界面
 */
@ContentView(R.layout.fragment_conversation)
class ConversationFragment : BaseKtFragment(),
        ConversationView, FriendshipMessageView,
        GroupManageMessageView {

    private val conversationList = LinkedList<Conversation>()
    //c2c未读消息数量
    private val c2cConversationList = LinkedList<Conversation>()
    private var groupList = ArrayList<String>()
    private var adapter: ConversationAdapter? = null
    private var friendshipManagerPresenter: FriendshipManagerPresenter? = null
    private var groupManagerPresenter: GroupManagerPresenter? = null
    private var friendshipConversation: FriendshipConversation? = null
    private var groupManageConversation: GroupManageConversation? = null
    /**
     * 长按选择的条目
     */
    private var longClickSelectPosition = -1

    companion object {
        //选择群成员请求码(发起群聊跳过来的)
        var presenter: ConversationPresenter? = null
    }

    override fun init(view: View) {
        StatusBarUtil.addStatusBarView(ll_titlebar_conversation, R.color.transparent)
        initGlobalView()
        initOperationData(0)
        initListener()
        EventBus.getDefault().register(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //注册弹出对话框列表,只能在onCreateView方法中注册,才起作用,不然会有问题
        registerForContextMenu(list)
    }


    private fun initGlobalView() {

        adapter = ConversationAdapter(activity!!, conversationList)
        adapter?.mode = Attributes.Mode.Single
        list?.adapter = adapter
        friendshipManagerPresenter = FriendshipManagerPresenter(this)
        groupManagerPresenter = GroupManagerPresenter(this)
        presenter = ConversationPresenter(this)
        presenter?.getConversation()
        adapter?.notifyDataSetChanged()

    }

    /**
     * 展示删除会话的dialog
     */
    private fun showDeleteConversationDialog(deleteConversation: Conversation) {
        val deleteDialog = CommonDialog(activity!!)
        deleteDialog.setTitle("确认删除")
        deleteDialog.setContent("删除后，将清空该聊天的消息记录")
        deleteDialog.show()

        deleteDialog.setOnConfirmClickListener {
            //确认
            if (deleteDialog.isShowing) {
                deleteDialog.dismiss()
                if (deleteConversation != null) {
                    removeConversation(deleteConversation)
                }
            }
        }
        deleteDialog.setOnCancelClickListener {
            //取消
            if (deleteDialog.isShowing) {
                deleteDialog.dismiss()
            }
        }
    }

    /**
     * 创建弹出对话框菜单 menu.add方法的第一个参数为groupid，第二个就是每个item的唯一标识符id，第三个参数就是优先顺序
     */
    override fun onCreateContextMenu(menu: ContextMenu, v: View,
                                     menuInfo: ContextMenu.ContextMenuInfo) {
        val info = menuInfo as AdapterView.AdapterContextMenuInfo
//        menu.setHeaderTitle("请选择操作")
        val conversation = conversationList[info.position]
        if (conversation.type == TIMConversationType.C2C || conversation.type == TIMConversationType.Group) {
            menu.add(0, 1, Menu.NONE, getString(R.string.conversation_sign_unread))
        }
//        menu.add(0, 1, Menu.NONE, getString(R.string.conversation_sign_unread))
        menu.add(0, 2, Menu.NONE, getString(R.string.conversation_stick))
        menu.add(0, 3, Menu.NONE, getString(R.string.conversation_delete))
    }


    /**
     * 选择菜单中的某个子类
     */
    override fun onContextItemSelected(item: MenuItem): Boolean {
        val info = item.menuInfo as AdapterView.AdapterContextMenuInfo
        val conversation = conversationList[info.position]
        when (item.itemId) {
            1 -> {
                //标记未读,腾讯云暂时不支持
                BaseUtil.showToast("标记未读")
                adapter?.notifyDataSetChanged()
            }
            2 -> {
                //置顶聊天
                toStickConversation(conversation)
            }
            3 -> {
                //删除聊天
                showDeleteConversationDialog(conversation)
            }
            else -> {

            }
        }
        return super.onContextItemSelected(item)
    }


    /**
     * 根据id移除会话
     */
    private fun removeConversation(id: Long) {
        for (conversation in conversationList) {
            if (conversation.identify == id.toString()) {
                removeConversation(conversation)
                break
            }
        }
    }

    /**
     * 直接移除会话对象
     */
    private fun removeConversation(conversation: Conversation?) {
        if (conversation != null) {
            if (conversation is NormalConversation) {
                if (presenter?.delConversation(conversation.getType(), conversation.identify)!!) {
                    conversationList.remove(conversation)
                    adapter?.notifyDataSetChanged()
                    //设置消息已读
                    conversation.readAllMessage()
                }
            }
            //系统会话消息也应该可以删除啊,难道腾讯云IM不支持吗？
//            if (presenter?.delConversation(conversation.getType(), conversation.identify)!!) {
//                conversationList.remove(conversation)
//                adapter?.notifyDataSetChanged()
//                //设置消息已读
//                conversation.readAllMessage()
//            }
        }
    }

    /**
     * 双击底部的嗨图标,发送过来的通知滚动
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onScrollEvent(event: Event.OnScrollChangeEvent) {
        if (Constants.ORG_ONSCROLL_EVENT == event.tag) {
            if (conversationList.size > 0) {
                list.smoothScrollToPosition(lastestMessagePos)
            }
        }
    }

    //上次点击的时间
    private var mLastTime: Long = 0
    //当前点击的时间
    private var mCurTime: Long = 0

    private fun initListener() {

        list.setOnItemClickListener { adapterView, view, position, id ->
            //点击会话item
            conversationList[position].navToDetail(activity)
            if (conversationList[position] is GroupManageConversation) {
                groupManagerPresenter?.getGroupManageLastMessage()
            }
        }

//        adapter?.setOnRecyclerViewItemClickListener { v, position ->
//            //点击会话item
//            conversationList[position].navToDetail(activity)
//            if (conversationList[position] is GroupManageConversation) {
//                groupManagerPresenter?.getGroupManageLastMessage()
//            }
//        }

//        list.setOnItemLongClickListener { adapterView, view, position, id ->
//            //长按某个会话弹出对话框,有三个选项标记未读，置顶，删除该会话,记录当前选中的位置
//            longClickSelectPosition = position
////            showMoreOperationPop(view)
//            false
//        }

        //双击标题栏滚动到会话最顶层
        ll_titlebar_conversation.setOnClickListener {
            mLastTime = mCurTime
            mCurTime = System.currentTimeMillis()
            if (mCurTime - mLastTime < 500) {
                //双击事件
                if (conversationList.size > 0) {
                    list.smoothScrollToPosition(0)
                }
            }
        }

    }

    private var stickIdentify = ""
    /**
     * 初始化会话列表数据信息
     */
    override fun initView(conversationList: MutableList<TIMConversation>?) {
        LogUtil.e("initView")
        this.conversationList.clear()
        c2cConversationList.clear()
        var stickConversion: NormalConversation? = null
        stickIdentify = AiiUtil.getString(activity, Constants.ORG_STICK_IDENTIFY, "")
        for (item in conversationList!!) {
            if (item.peer == stickIdentify) {
                //过滤掉相同的数据
                stickConversion = NormalConversation(item)
                continue
            }
            when (item.type) {
                TIMConversationType.C2C -> {
                    //单聊
                    this.conversationList.add(NormalConversation(item))
                    c2cConversationList.add(NormalConversation(item))
                    checkIsEmpty()
                }
                TIMConversationType.Group -> {
                    //群聊
                    this.conversationList.add(NormalConversation(item))
                    groupList.add(item.peer)
                    checkIsEmpty()
                }
                else -> {
                }
            }
        }
        //添加一个置顶的消息
        if (stickConversion != null) {
            this.conversationList.add(0, stickConversion)
        }
        LogUtil.e("size: " + conversationList.size)
        friendshipManagerPresenter?.getFriendshipLastMessage()
        groupManagerPresenter?.getGroupManageLastMessage()

    }

    /**
     * 刷新单聊未读消息数量
     */
    private fun refreshMsgUnread() {
        var num: Long = 0
        for (conversation in c2cConversationList) {
            num += conversation.unreadNum
        }
        //设置标题
        if (tv_title_for_conversation != null) {
            tv_title_for_conversation.text = "嗨($num)"
        }

    }


    /**
     * 最新的一条消息所在的位置
     */
    private var lastestMessagePos = 0

    /**
     * 更新最新消息显示
     *
     * @param message 最后一条消息
     */
    override fun updateMessage(message: TIMMessage?) {

        LogUtil.e("ailibin", "updateMessage")
        if (message == null) {
            adapter?.notifyDataSetChanged()
            refreshMsgUnread()
            return
        }
        if (message.conversation.type == TIMConversationType.System) {
            //系统消息不显示
            groupManagerPresenter?.getGroupManageLastMessage()
            return
        }
        val message1 = MessageFactory.getMessage(message)
        if (MessageFactory.getMessage(message) is CustomMessage) {
            //如果是正在输入的消息类型
            val customMessage = message1 as CustomMessage
            if (customMessage.type == TYPING) {
                adapter?.notifyDataSetChanged()
                return
            }
        }
        var conversation = NormalConversation(message.conversation)
        val iterator = conversationList.iterator()
        while (iterator.hasNext()) {
            val c = iterator.next()
            if (conversation == c) {
                conversation = c as NormalConversation
                iterator.remove()
                break
            }
        }

        //c2c未读消息计数
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
        //来了新消息原来置顶的会话无效了
        if (conversation.unreadNum > 0) {
            //这里不能设置空值
            try {
                lastestMessagePos = 0
                AiiUtil.putString(activity, Constants.ORG_STICK_IDENTIFY, "test")
                stickIdentify = AiiUtil.getString(activity, Constants.ORG_STICK_IDENTIFY, "")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        conversation.setLastMessage(MessageFactory.getMessage(message))
        conversationList.add(conversation)
        refresh()

    }

    /**
     * 更新好友关系链消息
     */
    override fun updateFriendshipMessage() {

    }

    /**
     * 删除会话
     *
     * @param identify
     */
    override fun removeConversation(identify: String?) {
        val iterator = conversationList.iterator()
        while (iterator.hasNext()) {
            val conversation = iterator.next()
            if (conversation.identify != null && conversation.identify == identify) {
                iterator.remove()
                adapter?.notifyDataSetChanged()
                return
            }
        }
    }

    /**
     * 更新群信息
     *
     * @param info
     */
    override fun updateGroupInfo(info: TIMGroupCacheInfo?) {
        for (conversation in conversationList) {
            if (conversation.identify != null && conversation.identify == info?.groupInfo?.groupId) {
                adapter?.notifyDataSetChanged()
                return
            }
        }
    }

    override fun refresh() {
        LogUtil.e("refresh")
        conversationList.sort()
        //排序好了之后,还需要对置顶的子项进行排序,排在集合的第一个位置
        if (!TextUtils.isEmpty(stickIdentify)) {
            val tempConversationList = ArrayList<Conversation>()
            tempConversationList.clear()
            var stickConversation: Conversation? = null
            for (item in conversationList) {
                if (stickIdentify == item.identify) {
                    stickConversation = item
                    continue
                } else {
                    tempConversationList.add(item)
                }
            }
            if (stickConversation != null) {
                tempConversationList.add(0, stickConversation!!)
            }
            conversationList.clear()
            conversationList.addAll(tempConversationList)
        }
        //刷新未读消息数量
        refreshMsgUnread()
        adapter?.notifyDataSetChanged()

    }

    /**
     * 获取好友关系链管理系统最后一条消息的回调
     *
     * @param message 最后一条消息
     * @param unreadCount 未读数
     */
    override fun onGetFriendshipLastMessage(message: TIMFriendFutureItem, unreadCount: Long) {

        if (friendshipConversation == null) {
            friendshipConversation = FriendshipConversation(message)
            conversationList?.add(friendshipConversation!!)
        } else {
            friendshipConversation?.setLastMessage(message)
        }
        friendshipConversation?.setUnreadCount(unreadCount)
        refresh()

    }


    /**
     * 获取好友关系链管理最后一条系统消息的回调
     * @param message 消息列表
     */
    override fun onGetFriendshipMessage(message: List<TIMFriendFutureItem>) {
//        friendshipManagerPresenter?.getFriendshipLastMessage()
    }

    /**
     * 获取群管理最后一条系统消息的回调
     *
     * @param message     最后一条消息
     * @param unreadCount 未读数
     */
    override fun onGetGroupManageLastMessage(message: TIMGroupPendencyItem, unreadCount: Long) {
        if (groupManageConversation == null) {
            groupManageConversation = GroupManageConversation(message)
            conversationList.add(groupManageConversation!!)
        } else {
            groupManageConversation?.setLastMessage(message)
        }
        groupManageConversation?.setUnreadCount(unreadCount)
        refresh()
    }

    /**
     * 获取群管理系统消息的回调
     *
     * @param message 分页的消息列表
     */
    override fun onGetGroupManageMessage(message: List<TIMGroupPendencyItem>) {
    }

    override fun onResume() {
        super.onResume()
        refresh()
        PushUtil.getInstance().reset()
    }


    /**
     * 弹窗宽度
     */
    private var userOperationPopWidth: Int = 0
    private var popOperation: PopupWindow? = null
    private var operationList = ArrayList<OperationContent>()
    private var userOperationAdapter: MemberOperationAdapter? = null
    /**
     * 初始化操作的窗口
     */
    private fun initPopupwindow() {
        //初始化视图
        val userOperationView = layoutInflater.inflate(R.layout.popupwindow_for_member_operation, null)
        val lvOperationListView = userOperationView.findViewById<ListView>(R.id.lv_content_for_user_operation_pop)
        //获取item宽度
        val item = layoutInflater.inflate(R.layout.item_for_member_operation_pop, null)
        val itemView = item.findViewById<RelativeLayout>(R.id.rl_item_for_user_operation)
        //获取item的宽度
        userOperationPopWidth = ScreenUtils.getViewWidthAndHeight(itemView)[0]
        //适配
        userOperationAdapter = MemberOperationAdapter(activity, operationList)
        lvOperationListView.adapter = userOperationAdapter
        //设置分割线的颜色
        lvOperationListView.divider = ColorDrawable(Color.parseColor("#CCCCCC"))
        lvOperationListView.dividerHeight = 1
        //创建弹窗
        popOperation = PopupWindow(userOperationView, userOperationPopWidth, ViewGroup.LayoutParams.WRAP_CONTENT, true)
        //响应事件
        lvOperationListView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            when (position) {
                0 -> {
                    //标记未读
                    BaseUtil.showToast("点击了标记未读")
                }
                1 -> {
                    //置顶
                }
                2 -> {
                    //删除该聊天
                }
            }
            popOperation?.dismiss()
        }
        //点击弹窗外部弹窗消失
        popOperation?.setBackgroundDrawable(ColorDrawable())
        popOperation?.isOutsideTouchable = true
        //设置进出场动画
        popOperation?.animationStyle = R.style.animation_for_user_operation
        popOperation?.setOnDismissListener {
            //透明度设置回来
            BaseUtil.setBackgroundAlpha(activity, 1f)
            //把选中的item颜色设置设置会来
        }
    }

    /**
     * 置顶会话
     */
    private fun toStickConversation(selectConversation: Conversation) {
        AiiUtil.putString(activity, Constants.ORG_STICK_IDENTIFY, selectConversation.identify)
        //删除相同的会话,然后置顶当前的会话
        val tempConversationList = ArrayList<Conversation>()
        tempConversationList.clear()
        for (item in conversationList) {
            if (selectConversation.identify == item.identify) {
                continue
            } else {
                tempConversationList.add(item)
            }
        }
        conversationList.clear()
        tempConversationList.add(0, selectConversation)
        //添加到第一的位置
        conversationList.addAll(tempConversationList)
        adapter?.notifyDataSetChanged()
    }

    private fun initOperationData(type: Int) {
        //弹窗数据
        if (type == 0) {
            operationList.add(OperationContent("标记未读"))
            operationList.add(OperationContent("置顶该聊天"))
            operationList.add(OperationContent("删除该聊天"))
        }
        userOperationAdapter?.notifyDataSetChanged()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        EventBus.getDefault().unregister(this)
    }

    //如果没有数据,就用空布局
    private fun checkIsEmpty() {
        if (conversationList.size > 0) {
            empty_view?.visibility = View.GONE
        } else {
            empty_view?.visibility = View.VISIBLE
        }
    }

    /**
     * 更多操作弹窗
     */
    private fun showMoreOperationPop(itemView: View) {
        val ivUserOperationWidth = resources.getDimensionPixelOffset(R.dimen.title_bar_height)
        val popwindowWidth = userOperationPopWidth
        val location = intArrayOf(2)
//        val xOffset = userOperationPopWidth - ivUserOperationWidth + ScreenUtils.dip2px(activity, 7F)
//        pwForUserOperation?.showAsDropDown(itemView, -xOffset, ScreenUtils.dip2px(activity, 4F))
//        pwForUserOperation?.showAsDropDown(itemView, 0, 0, Gravity.NO_GRAVITY)
        popOperation?.showAsDropDown(itemView, (location[0] + itemView.width / 2) - popwindowWidth / 2, 0)

    }

}