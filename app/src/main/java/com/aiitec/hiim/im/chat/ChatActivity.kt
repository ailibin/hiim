package com.aiitec.hiim.im.chat

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.support.v4.content.ContextCompat
import android.support.v7.widget.GridLayoutManager
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.*
import com.aiitec.hiim.R
import com.aiitec.hiim.annotation.ContentView
import com.aiitec.hiim.base.BaseKtActivity
import com.aiitec.hiim.im.adapter.ChatAdapter
import com.aiitec.hiim.im.adapter.ChatOperationAdapter
import com.aiitec.hiim.im.entity.Item
import com.aiitec.hiim.im.location.LocationActivity
import com.aiitec.hiim.im.location.util.ToastUtil
import com.aiitec.hiim.im.model.*
import com.aiitec.hiim.im.other.EmotionKeyboard
import com.aiitec.hiim.im.other.PhotoActivity
import com.aiitec.hiim.im.utils.AiiUtil
import com.aiitec.hiim.im.utils.LogUtil
import com.aiitec.hiim.im.utils.MediaUtil
import com.aiitec.hiim.utils.BaseUtil
import com.aiitec.hiim.utils.PermissionsUtils
import com.aiitec.hiim.utils.SoftKeyboardStateHelper
import com.aiitec.imlibrary.emojicon.EmojiconEditText
import com.aiitec.imlibrary.emojicon.EmojiconGridFragment
import com.aiitec.imlibrary.emojicon.EmojiconsFragment
import com.aiitec.imlibrary.emojicon.emoji.Emojicon
import com.aiitec.imlibrary.presentation.event.GroupEvent
import com.aiitec.imlibrary.presentation.presenter.ChatPresenter
import com.aiitec.imlibrary.presentation.presenter.GroupInfoPresenter
import com.aiitec.imlibrary.presentation.viewfeatures.ChatView
import com.aiitec.imlibrary.presentation.viewfeatures.GroupInfoView
import com.aiitec.imlibrary.ui.PathUtil
import com.aiitec.imlibrary.ui.VoiceRecorderView
import com.herentan.giftfly.ui.location.entity.Area
import com.tencent.imsdk.*
import com.tencent.imsdk.ext.group.TIMGroupDetailInfo
import com.tencent.imsdk.ext.message.TIMMessageDraft
import com.tencent.imsdk.ext.message.TIMMessageExt
import com.tencent.imsdk.ext.message.TIMMessageLocator
import kotlinx.android.synthetic.main.activity_chat_input.*
import java.io.File
import java.util.*

/**
 * Created by ailibin on 2018/1/4.
 * 聊天主界面
 */
@ContentView(R.layout.activity_chat)
class ChatActivity : BaseKtActivity(), ChatView,
        GroupInfoView, Observer,
        EmojiconsFragment.OnEmojiconBackspaceClickedListener,
        EmojiconGridFragment.OnEmojiconClickedListener {

    private val TAG = "ChatActivity"
    val ARG_IDENTIFY = "identify"

    private val REQUEST_CAMERA = 0x03
    private val REQUEST_IMAGE = 0x100
    private val REQUEST_LOCATION = 0x04
    private val REQUEST_GIFT = 0x110
    private val PERMISSIONS_REQUEST_CAMERA = 0x01
    private val PERMISION_REQUEST_RECORD_VOICE = 0x02

    private var input: EmojiconEditText? = null
    private var chat_fl_actions: FrameLayout? = null
    private var voiceRecorderView: VoiceRecorderView? = null
    private var btnSend: TextView? = null
    private val messageList = ArrayList<Message>()

    private var adapter: ChatAdapter? = null
    private var listView: ListView? = null
    private var identify: String? = null
    private var messageId: String? = null
    private var type: TIMConversationType? = null
    private var index: Int = -1
    /**
     * 这里为了测试模拟一下群聊和单聊类型(默认单聊)
     */
    private var titleStr: String? = null
    private val handler = Handler()
    private var softKeyboardStateHelper: SoftKeyboardStateHelper? = null
    private var permissionsUtils: PermissionsUtils? = null
    private var cameraFilePath: String? = null
    private var nickname: String? = null
    private var hasRecordVoicePermission: Boolean = false

    //是否打开表情图标
    private var isOpenEmojiIcon = false
    //底部四个图标操作
    private var isShowOperationIcon = false
    //打开语音
    private var isOpenVoiceView = false

    //绑定输入EditTextView和表情面板
    private var mEmotionKeyboard: EmotionKeyboard? = null
    private var operationData = ArrayList<Item>()
    private var operationAdapter: ChatOperationAdapter? = null
    private var groupInfoPresenter: GroupInfoPresenter? = null
    private var groupIds = ArrayList<String>()

    //聊天室消息总数量
    private var messageNums = 0

    companion object {
        var presenter: ChatPresenter? = null
        //图片消息列表，这个是为了显示图片时滚动看上下一条用的
        var imageMessageList = ArrayList<TIMMessage>()
        var imageResId = arrayListOf(
                R.drawable.chat_btn_pic2x,
                R.drawable.chat_btn_takephoto2x,
                R.drawable.chat_btn_place2x)
        var contentStr = arrayListOf("相册", "拍照", "位置")

        val TAG = "ailibin"

        fun navToChat(context: Context, identify: String, nickname: String, type: TIMConversationType) {
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra("identify", identify)
            intent.putExtra("nickname", nickname)
            intent.putExtra("type", type)
            LogUtil.e("identify:$identify   nickname:$nickname")
            context.startActivity(intent)
        }

        //搜索出来内容,定位到聊天室消息位置
        fun navToChat(context: Context, identify: String, type: TIMConversationType, messageId: String) {
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra("identify", identify)
            intent.putExtra("type", type)
            intent.putExtra("messageId", messageId)
            LogUtil.e("identify1: $identify" + "messageId :$messageId")
            context.startActivity(intent)
        }

    }

    override fun init(savedInstanceState: Bundle?) {
        //配置左滑finish界面
//        Slidr.attach(this)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        imageMessageList.clear()
        identify = intent.getStringExtra(ARG_IDENTIFY)
        messageId = intent.getStringExtra("messageId")
        PathUtil.getInstance().initDirs(null, identify, this)
        titleStr = identify
        type = intent.getSerializableExtra("type") as TIMConversationType?
        nickname = if (type == TIMConversationType.Group) {
            intent.getStringExtra("nickname")
        } else {
            AiiUtil.getString(this, "nickname_" + identify!!, identify)
        }

        LogUtil.d(TAG, "nickname1: $nickname")
        if (!TextUtils.isEmpty(nickname)) {
            titleStr = nickname
        }

        //加上这句设置标题,不然标题显示名称的很混乱
        LogUtil.d(TAG, "titleStr: $titleStr")
        initView()
        initOperationData()
        initOperationView()
        setOperationListener()
        initPermission()
        initSoftKeyboard()
        //群资料presenter
        groupInfoPresenter = GroupInfoPresenter(this, groupIds, true)
        groupInfoPresenter?.getGroupDetailInfo()
        adapter = ChatAdapter(this, R.layout.item_message, messageList)
        adapter?.setConversationType(type)

        val otherAvatar = AiiUtil.getString(this, "avatar_$identify")
        LogUtil.d(TAG, "otherAvatar: $otherAvatar")
        if (type == TIMConversationType.C2C) {
            //单聊才设置另外一个人的头像
            if (!TextUtils.isEmpty(otherAvatar)) {
                adapter?.setOtherAvatar(otherAvatar)
            } else {
                //请求用户详情
                TIMFriendshipManager.getInstance().getUsersProfile(arrayListOf(identify), object : TIMValueCallBack<List<TIMUserProfile>> {
                    override fun onError(i: Int, s: String) {

                    }

                    override fun onSuccess(timUserProfiles: List<TIMUserProfile>) {
                        if (timUserProfiles.isNotEmpty()) {
                            val imagePath = timUserProfiles[0].faceUrl
                            val nickname = timUserProfiles[0].nickName
                            AiiUtil.putString(this@ChatActivity, "avatar_$identify", imagePath)
                            AiiUtil.putString(this@ChatActivity, "nickname_$identify", nickname)
                        }
                    }
                })
            }
        }
        listView?.adapter = adapter
        presenter = ChatPresenter(this, identify, type)
        listView?.transcriptMode = ListView.TRANSCRIPT_MODE_NORMAL

        //注册弹出对话框列表
        registerForContextMenu(listView)
        if (!TextUtils.isEmpty(messageId)) {
            if (messageList != null && messageList.size > 0) {
                for (i in 0 until messageList.size) {
                    val nMessageId = messageList[i].message.msgId
                    if (messageId == nMessageId) {
                        index = i
                        LogUtil.d(TAG, "index: $index")
                        return
                    }
                }
                //滚动到指定的消息位置
                listView?.smoothScrollToPosition(index)
            }
        }
        setEmojiconFragment()
        presenter?.start()
        adapter?.notifyDataSetChanged()
        setListener()

    }


    private fun initOperationData() {
        operationData.clear()
        for (i in 0 until imageResId.size) {
            val item = Item()
            item.imagePath = imageResId[i]
            item.content = contentStr[i]
            operationData.add(item)
        }
    }

    /**
     * 初始化底部四个图标的操作界面(相册、拍照、位置、选礼四个功能)
     */
    private fun initOperationView() {
        operationAdapter = ChatOperationAdapter(this, operationData)
        val gridLayoutManager = GridLayoutManager(this, 4)
        recyclerView_for_chat_operation.adapter = operationAdapter
        recyclerView_for_chat_operation.layoutManager = gridLayoutManager

    }

    //操作面板
    private var operationView: View? = null

    private fun setOperationListener() {
        //发送消息点击监听
        btnSend?.setOnClickListener {
            sendText()
        }
        setRightBtnClickListener(View.OnClickListener {
            //设置右边图标的点击事件
            if (type == TIMConversationType.C2C) {
                //传一个用户带前缀IMid过去
            } else {
                //传一个群id过去
            }
        })

        iv_emojicons_switch?.setOnClickListener {
            //表情面板,这里要设置语音面板
            iv_chat_voice.setImageResource(R.drawable.chat_btn_voice2x)
            isOpenVoiceView = false
            //还原操作面板相关数据
            isShowOperationIcon = false
            if (!isOpenEmojiIcon) {
                val emojiFragmentView = chat_fl_actions?.getChildAt(0)
                hideOtherMenu(0)
                BaseUtil.hideKeyboard(this)
                emojiFragmentView?.visibility = View.VISIBLE
                isOpenEmojiIcon = true
                iv_emojicons_switch.setImageResource(R.drawable.chat_btn_keyboard2x)
            } else {
                val emojiFragmentView = chat_fl_actions?.getChildAt(0)
                hideOtherMenu(0)
                BaseUtil.openKeyboard(this, input)
                emojiFragmentView?.visibility = View.GONE
                isOpenEmojiIcon = false
                iv_emojicons_switch.setImageResource(R.drawable.chat_btn_expression2x)
            }
        }


        operationAdapter?.setOnRecyclerViewItemClickListener { v, position ->
            when (position) {
                0 -> {
                    //相册
                    val intent = Intent(this, PhotoActivity::class.java)
                    startActivityForResult(intent, REQUEST_IMAGE)
                }
                1 -> {
                    //拍照
                    permissionsUtils?.requestPermissions(PERMISSIONS_REQUEST_CAMERA, Manifest.permission.CAMERA)
                }
                2 -> {
                    //位置
                    switchToActivityForResult(LocationActivity::class.java, REQUEST_LOCATION)
                }
            }
        }

        iv_chat_voice.setOnClickListener {
            //打开语音面板
            val voiceView = chat_fl_actions?.getChildAt(2)
            BaseUtil.hideKeyboard(this)
            hideOtherMenu(2)
            //这里要还原表情面板
            isOpenEmojiIcon = false
            iv_emojicons_switch.setImageResource(R.drawable.chat_btn_expression2x)
            //还原操作面板相关数据
            isShowOperationIcon = false
            if (!isOpenVoiceView) {
                iv_chat_voice.setImageResource(R.drawable.chat_btn_keyboard2x)
                BaseUtil.hideKeyboard(this)
                voiceView?.visibility = View.VISIBLE
                isOpenVoiceView = true
            } else {
                iv_chat_voice.setImageResource(R.drawable.chat_btn_voice2x)
                BaseUtil.openKeyboard(this, input)
                voiceView?.visibility = View.GONE
                isOpenVoiceView = false
            }
        }

        iv_chat_operation.setOnClickListener {
            //打开操作面板
            BaseUtil.hideKeyboard(this)
            hideOtherMenu(1)
            if (!isShowOperationIcon) {
                operationView?.visibility = View.VISIBLE
                isShowOperationIcon = true
            } else {
                operationView?.visibility = View.GONE
                isShowOperationIcon = false
            }
        }

        //点击发送按钮
        input?.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                sendText()
                return@OnEditorActionListener true
            }
            return@OnEditorActionListener false
        })

        input?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                //监听内容的变化来控制操作面板和发送按钮的切换
                val text = p0.toString().trim()
                if (!TextUtils.isEmpty(text)) {
                    //内容不为空就显示发送按钮,否则显示操作按钮(这里最好弄个动画)
                    iv_chat_operation.visibility = View.GONE
                    btnSend?.visibility = View.VISIBLE
                    //发送自定消息
//                    val customMessage = CustomMessage(CustomMessage.Type.TYPING)
//                    sendCustomMessage(customMessage)
                    sending()
                } else {
                    iv_chat_operation.visibility = View.VISIBLE
                    btnSend?.visibility = View.GONE
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

        })
    }

    /**
     * 发送位置,这个只是文字没有图片
     */
    @Override
    fun sendLocation() {
//        val message = LocationMessage(113.93, 22.54, "我的位置")
//        presenter?.sendMessage(message.message)
    }

    /**
     * 隐藏掉其它的menu,传一个position=-1就是隐藏全部
     */
    private fun hideOtherMenu(position: Int) {
        for (i in 0 until chat_fl_actions?.childCount!!) {
            if (position != i) {
                val view = chat_fl_actions!!.getChildAt(i)
                if (view.isShown) {
                    view.visibility = View.GONE
                }
            }
        }
    }


    private fun initView() {
        initTitleBarView()
        input = findViewById(R.id.chat_et_message)
        chat_fl_actions = findViewById(R.id.chat_fl_actions)
        voiceRecorderView = findViewById(R.id.chat_voicerecorderview)
        btnSend = findViewById(R.id.btn_send_message)
        listView = findViewById(R.id.list)
        //这里需要初始化操作面板
        operationView = chat_fl_actions?.getChildAt(1)
    }

    private fun initTitleBarView() {

        addBaseStatusBarView()
        setRightBtnVisible(true)
        setRightBtnText("", ContextCompat.getColor(this, R.color.white))
        //这里设置单聊和群聊的标题名称
        if (type == TIMConversationType.C2C) {
            //单聊类型,显示对方的名称
            setColumnTitle(titleStr!!)
        } else {
            groupIds.clear()
            identify.let {
                groupIds.add(it!!)
            }
        }
    }

    private var groupName: String? = null
    private var num: Long = 0
    /**
     * 展示群组消息
     */
    override fun showGroupInfo(groupInfos: MutableList<TIMGroupDetailInfo>?) {
        if (groupInfos != null) {
            for (info in groupInfos) {
                groupName = info.groupName
                num = info.memberNum
                //获取群成员人数
                if (TextUtils.isEmpty(groupName)) {
                    setColumnTitle("$nickname($num)")
                } else {
                    setColumnTitle("$groupName($num)")
                }
            }
        } else {
            groupName = GroupInfo.getInstance().getGroupName(identify)
            //这里要用腾讯云的api数量显示有问题
            num = GroupInfo.getInstance().groupNums
            val numInt = num.toInt()
            if (TextUtils.isEmpty(groupName)) {
                if (numInt == 0) {
                    setColumnTitle("$nickname")
                } else {
                    setColumnTitle("$nickname($num)")
                }
            } else {
                if (numInt == 0) {
                    setColumnTitle("$groupName")
                } else {
                    setColumnTitle("$groupName($num)")
                }
            }
        }
    }

    override fun update(observable: Observable, data: Any) {
        if (observable is GroupEvent) {
            if (data is GroupEvent.NotifyCmd) {
                when (data.type) {
                    GroupEvent.NotifyType.DEL -> {
                        //群信息变更的时候界面要刷新
                        groupInfoPresenter?.getGroupDetailInfo()
                    }
                    GroupEvent.NotifyType.ADD -> {
                        groupInfoPresenter?.getGroupDetailInfo()
                    }
                    GroupEvent.NotifyType.UPDATE -> {
                        groupInfoPresenter?.getGroupDetailInfo()
                    }
                }
            }
        }
    }

    /**
     * 软键盘键盘监听器初始化
     */
    private fun initSoftKeyboard() {
        softKeyboardStateHelper = SoftKeyboardStateHelper(window.decorView, this)
        softKeyboardStateHelper!!.addSoftKeyboardStateListener(object : SoftKeyboardStateHelper.SoftKeyboardStateListener {
            override fun onSoftKeyboardOpened(keyboardHeightInPx: Int) {
                //键盘打开,有内容发送按钮才可见
                hideOtherMenu(-1)
                iv_emojicons_switch.setImageResource(R.drawable.chat_btn_expression2x)
                view2.visibility = View.VISIBLE
                view2.layoutParams.height = keyboardHeightInPx
                isOpenEmojiIcon = false
            }

            override fun onSoftKeyboardClosed(keyboardHeightInPx: Int) {
                view2.visibility = View.GONE
            }
        })
    }

    /**
     * 监听按下返回键表情栏的隐藏(不能销毁当前界面)
     */
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (KeyEvent.KEYCODE_BACK == keyCode) {
            if (emojicons.isShown) {
                emojicons.visibility = View.GONE
                btnSend?.visibility = View.GONE
                iv_chat_operation.visibility = View.VISIBLE
                return true
            } else {
                return super.onKeyDown(keyCode, event)
            }

        } else {
            return super.onKeyDown(keyCode, event)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setListener() {
        listView?.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    hideOtherMenu(-1)
                    if (softKeyboardStateHelper!!.isSoftKeyboardOpened) {
                        AiiUtil.hideKeyboard(this@ChatActivity, input)
                    }
                }
                else -> {
                }
            }
            return@setOnTouchListener false
        }

        listView?.setOnScrollListener(object : AbsListView.OnScrollListener {
            private var firstItem: Int = 0
            override fun onScrollStateChanged(view: AbsListView, scrollState: Int) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && firstItem == 0) {
                    //如果拉到顶端读取更多消息
                    presenter!!.getMessage(if (messageList.size > 0) messageList[0].message else null)
                }
            }

            override fun onScroll(view: AbsListView, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {
                firstItem = firstVisibleItem
            }
        })

        voiceRecorderView?.setEaseVoiceRecorderCallback(VoiceRecorderView.EaseVoiceRecorderCallback { voiceFilePath, voiceTimeLength ->
            if (voiceTimeLength < 1) {
                ToastUtil.show(this@ChatActivity, "录音太短")
            } else if (voiceTimeLength > 60) {
                ToastUtil.show(this@ChatActivity, "录音太长")
            } else {
                val message = VoiceMessage(voiceTimeLength.toLong(), voiceFilePath)
                presenter!!.sendMessage(message.message)
            }
        })

        voiceRecorderView?.setOnTouchListener { v, _ ->
            return@setOnTouchListener if (hasRecordVoicePermission) {
                permissionsUtils!!.requestPermissions(PERMISION_REQUEST_RECORD_VOICE, Manifest.permission.RECORD_AUDIO)
                true
            } else {
                false
            }
        }
        adapter?.setOnResendListener { data ->
            messageList.remove(data)
            presenter!!.sendMessage(data.message)
            adapter!!.notifyDataSetChanged()
        }
        adapter?.setOnAvatarClickListener(object : ChatAdapter.OnAvatarClickListener {
            override fun onClickSelfAvatar() {
                //点击自己的头像(这里还是要传一个id过去,因为contactInfo协议需要一个用户id)
            }

            override fun onClickOtherAvatar(identify: String) {
                //点击其他人的头像(传一个其他人的ID过去),这里不能是群id,不然就崩了
            }
        })
    }

    /**
     * 跳转用户详情
     */
    fun switchToUserDetailsAct(userId: Long) {
//        val bundle = Bundle()
//        bundle.putLong(UserDetailActivity.PARAM_ID, userId!!)
//        switchToActivity(UserDetailActivity::class.java, bundle)
    }


    /**
     * 权限请求初始化
     */
    private fun initPermission() {
        permissionsUtils = PermissionsUtils(this)
        permissionsUtils!!.setOnPermissionsListener(object : PermissionsUtils.OnPermissionsListener {
            override fun onPermissionsSuccess(requestCode: Int) {
                if (requestCode == PERMISSIONS_REQUEST_CAMERA) {
                    startCamera()
                } else if (requestCode == PERMISION_REQUEST_RECORD_VOICE) {
                    hasRecordVoicePermission = true
                }
            }

            override fun onPermissionsFailure(requestCode: Int) {
                if (requestCode == PERMISSIONS_REQUEST_CAMERA) {
                    BaseUtil.showToast("请开启拍照权限")
                }
            }
        })
    }

    private fun sendCustomMessage(message: Message) {
        presenter?.sendMessage(message.message)
    }

    private fun setEmojiconFragment() {
        val emojiconsFragment = EmojiconsFragment.newInstance(false)
        supportFragmentManager.beginTransaction().replace(R.id.emojicons, emojiconsFragment).commit()
    }


    override fun onPause() {
        super.onPause()
        //退出聊天界面时输入框有内容，保存草稿
        if (input!!.text!!.isNotEmpty()) {
            val message = TextMessage(input?.text)
            presenter?.saveDraft(message.message)
        } else {
            presenter?.saveDraft(null)
        }
        presenter?.readMessages()
        MediaUtil.getInstance().stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter?.stop()
    }


    /**
     * 将标题设置为对象名称
     */
    private val resetTitle = Runnable {
        if (!TextUtils.isEmpty(titleStr)) {
            setColumnTitle(titleStr!!)
        }
    }

    /**
     * 显示对方发送单条的消息
     *
     * @param message
     */
    override fun showMessage(message: TIMMessage?) {
        //这里设置是否接受并显示消息(默认接受)
        if (message == null) {
            adapter?.notifyDataSetChanged()
        } else {
            val mMessage = MessageFactory.getMessage(message)
            if (mMessage != null) {
                if (mMessage is CustomMessage) {
                    val messageType = mMessage.type
                    LogUtil.e("messageType:$messageType")
                    //如果是正在输入自定义消息类型或者无效就不用加入集合中
                    if (mMessage?.isSelf) {
                        if (messageType == CustomMessage.Type.TYPING || messageType == CustomMessage.Type.INVALID) {
                            return
                        }
                        if (messageList.size == 0) {
                            mMessage?.setHasTime(null)
                        } else {
                            mMessage?.setHasTime(messageList[messageList.size - 1].message)
                        }
                        messageList.add(mMessage)
                        adapter?.notifyDataSetChanged()
                        listView?.setSelection(adapter?.count!! - 1)
                    } else {
                        when (messageType) {
                            CustomMessage.Type.TYPING -> {
                                title = getString(R.string.chat_typing)
                                handler.removeCallbacks(resetTitle)
                                handler.postDelayed(resetTitle, 2000)
                            }
                            //这里双方都要展示位置消息(在自定义消息里面)
                            CustomMessage.Type.LOCATION -> {
                                //地理位置消息
                                val customMessage = mMessage
                                if (messageList.size == 0) {
                                    mMessage?.setHasTime(null)
                                } else {
                                    mMessage?.setHasTime(messageList[messageList.size - 1].message)
                                }
                                messageList.add(mMessage)
                                adapter?.notifyDataSetChanged()
                                listView?.setSelection(adapter?.count!! - 1)
                            }
                        }
                    }

                } else {
                    if (messageList.size == 0) {
                        mMessage?.setHasTime(null)
                    } else {
                        mMessage?.setHasTime(messageList[messageList.size - 1].message)
                    }
                    messageList.add(mMessage)
                    adapter?.notifyDataSetChanged()
                    listView?.setSelection(adapter!!.count - 1)

                    if (mMessage?.message.getElement(0).type == TIMElemType.Image) {
                        imageMessageList.add(mMessage?.message)
                    }
                }
                messageNums = messageList.size
            }
        }

    }

    /**
     * 显示集合消息总列表
     *
     * @param messages
     */
    override fun showMessage(messages: List<TIMMessage>) {
        var newMsgNum = 0

        for (i in messages.indices) {
            val mMessage = MessageFactory.getMessage(messages[i])
            if (mMessage == null || messages[i].status() == TIMMessageStatus.HasDeleted) {
                continue
            }
            if (mMessage is CustomMessage) {
                val customMessage = mMessage
                val type = mMessage.type
                if (customMessage.type === CustomMessage.Type.TYPING || customMessage.type === CustomMessage.Type.INVALID) {
                    continue
                }
                if (!mMessage.isSelf()) {
//                    if (type == CustomMessage.Type.GIFT) {
//                        //发送礼物消息(这里还是发送者)
//                        val receiverName = AiiUtil.getString(this, "nickname_" + identify, identify)
//                        val mSender = AiiUtil.getString(this, "nickname_" + mMessage.sender, mMessage.sender)
//                        customMessage.setOnAcceptListener { packageId, packageType, senderName, action ->
//                            //action=1查看  action=2查看并接受
//                            requestAcceptGiftPackage(packageId, packageType, mSender, receiverName, action)
//                        }
//                    }
                }
            }
            ++newMsgNum
            if (i != messages.size - 1) {
                mMessage?.setHasTime(messages[i + 1])
                messageList.add(0, mMessage)
            } else {
                mMessage?.setHasTime(null)
                messageList.add(0, mMessage)
            }

            if (messages[i].getElement(0).type == TIMElemType.Image) {
                imageMessageList.add(messages[i])
            }
        }
        //这里的消息数量应该要包括系统消息
        messageNums = messageList.size
        adapter?.notifyDataSetChanged()
        listView?.setSelection(newMsgNum)
    }

    override fun showRevokeMessage(timMessageLocator: TIMMessageLocator) {
        for (msg in messageList) {
            val ext = TIMMessageExt(msg.message)
            if (ext.checkEquals(timMessageLocator)) {
                adapter?.notifyDataSetChanged()
            }
        }
    }

    /**
     * 清除所有消息，等待刷新
     */
    override fun clearAllMessage() {
        messageList.clear()
        imageMessageList.clear()
    }

    /**
     * 发送消息成功
     *
     * @param message 返回的消息
     */
    override fun onSendMessageSuccess(message: TIMMessage) {
        showMessage(message)
    }

    /**
     * 发送消息失败
     *
     * @param code 返回码
     * @param desc 返回描述
     */
    override fun onSendMessageFail(code: Int, desc: String, message: TIMMessage) {
        val id = message.msgUniqueId
        for (msg in messageList) {
            if (msg.message.msgUniqueId === id) {
                when (code) {
                    80001 -> {
                        //发送内容包含敏感词
                        msg.desc = getString(R.string.chat_content_bad)
                        adapter!!.notifyDataSetChanged()
                    }
                    else -> {
                    }
                }
            }
        }
        adapter?.notifyDataSetChanged()

    }


    /**
     * 发送图片消息
     */
    private fun sendImage(path: String?) {
        val imageMessage = ImageMessage(path)
        presenter?.sendMessage(imageMessage.message)
    }

    /**
     * 发送文本消息
     */
    private fun sendText() {
        val content = input!!.text.toString()
        if (TextUtils.isEmpty(content)) {
            return
        }
        val message = TextMessage(input!!.text)
        presenter?.sendMessage(message.message)
        input?.setText("")
    }


    /**
     * 发送小视频消息
     *
     * @param fileName 文件名
     */
    private fun sendVideo(fileName: String) {
        val message = VideoMessage(fileName)
        presenter?.sendMessage(message.message)
    }

    /**
     * 发送位置信息消息
     */
    private fun sendLocation(area: Area?) {
        val message = CustomMessage(area, CustomMessage.Type.LOCATION)
        LogUtil.d("ailibin", "sendLocation--area: " + area.toString())
        sendCustomMessage(message)
    }

    /**
     * 正在发送
     */
    override fun sending() {
        if (type == TIMConversationType.C2C) {
            val message = CustomMessage(CustomMessage.Type.TYPING)
            presenter?.sendOnlineMessage(message.message)
        }
    }

    /**
     * 显示草稿
     */
    override fun showDraft(draft: TIMMessageDraft) {
        input?.text?.append(TextMessage.getString(draft.elems, this))
    }

    override fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View,
                                     menuInfo: ContextMenu.ContextMenuInfo) {
        val info = menuInfo as AdapterView.AdapterContextMenuInfo
        val message = messageList[info.position]
        menu.add(0, 1, Menu.NONE, getString(R.string.chat_del))
        if (message.isSendFail) {
            menu.add(0, 2, Menu.NONE, getString(R.string.chat_resend))
        } else if (message.message.isSelf) {
            menu.add(0, 4, Menu.NONE, getString(R.string.chat_pullback))
        }
        if (message is ImageMessage || message is FileMessage) {
            menu.add(0, 3, Menu.NONE, getString(R.string.chat_save))
        }
    }


    override fun onContextItemSelected(item: MenuItem): Boolean {
        val info = item.menuInfo as AdapterView.AdapterContextMenuInfo
        val message = messageList[info.position]
        when (item.itemId) {
            1 -> {
                message.remove()
                messageList.removeAt(info.position)
                adapter?.notifyDataSetChanged()
            }
            2 -> {
                messageList.remove(message)
                presenter?.sendMessage(message.message)
            }
            3 -> message.save()
            4 -> presenter?.revokeMessage(message.message)
            else -> {

            }
        }
        return super.onContextItemSelected(item)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE && resultCode == RESULT_OK) {
            // 获取返回的图片列表
            val paths = data?.getStringArrayListExtra(PhotoActivity.IMAGE_RESULT)
            if (paths != null && paths.size > 0) {
                for (path in paths) {
                    sendImage(path)
                }
            }
        } else if (REQUEST_CAMERA == requestCode && resultCode == Activity.RESULT_OK) {
            sendImage(cameraFilePath)
        } else if (REQUEST_LOCATION == requestCode && resultCode == Activity.RESULT_OK) {
            //发送位置消息
            val area = data?.getParcelableExtra<Area>("location")
            sendLocation(area)
        }
    }


    private fun startCamera() {
        if (!AiiUtil.isSDCardEnable()) {
            ToastUtil.show(this@ChatActivity, "SD卡不可用")
            return
        }
        val cameraPath = AiiUtil.getSDCardPath() + "/giftfly"
        val dir = File(cameraPath)
        if (!dir.exists()) {
            dir.mkdir()
        }
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val file = File(cameraPath, System.currentTimeMillis().toString() + ".jpg")
        cameraFilePath = file.absolutePath
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file))
        startActivityForResult(intent, REQUEST_CAMERA)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionsUtils!!.onRequestPermissionsResult(requestCode, permissions, grantResults)

    }


    override fun onEmojiconBackspaceClicked(v: View) {
        EmojiconsFragment.backspace(input)
    }

    override fun onEmojiconClicked(emojicon: Emojicon) {
        EmojiconsFragment.input(input, emojicon)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
    }
}