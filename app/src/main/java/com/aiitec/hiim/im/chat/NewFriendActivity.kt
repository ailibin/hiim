package com.aiitec.hiim.im.chat

import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.TextView
import com.aiitec.imlibrary.presentation.presenter.FriendshipManagerPresenter
import com.aiitec.hiim.Event
import com.aiitec.hiim.R
import com.aiitec.hiim.adapter.CommonRecyclerViewAdapter
import com.aiitec.hiim.annotation.ContentView
import com.aiitec.hiim.base.BaseKtActivity
import com.aiitec.hiim.im.adapter.NewFriendAdapter
import com.aiitec.hiim.im.entity.ListUser
import com.aiitec.openapi.utils.LogUtil
import kotlinx.android.synthetic.main.activity_new_fiend.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * Created by ailibin on 2018/1/4.
 * 新朋友activity
 */
@ContentView(R.layout.activity_new_fiend)
class NewFriendActivity : BaseKtActivity(){

    private var newFriendAdapter: NewFriendAdapter? = null
    private var friendshipPresenter: FriendshipManagerPresenter? = null
    private var newFriendDatas = ArrayList<ListUser>()

    override fun init(savedInstanceState: Bundle?) {

        addBaseStatusBarView()
        setColumnTitle("新的朋友")
        setRightBtnVisible(true)
        setRightBtnText("添加朋友", ContextCompat.getColor(this, R.color.white))
//        friendshipPresenter = FriendshipManagerPresenter(this)
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimaryDark)
        swipeRefreshLayout.setOnRefreshListener {
            requestUserList(true)
        }
        requestUserList(false)
        initNewFriendView()
        setListener()
        EventBus.getDefault().register(this)

    }


    /**
     * 初始化新朋友列表界面
     */
    private fun initNewFriendView() {
        newFriendAdapter = NewFriendAdapter(this, newFriendDatas)
        val layoutManager = LinearLayoutManager(this)
        recyclerView_for_new_friend.adapter = newFriendAdapter
        recyclerView_for_new_friend.layoutManager = layoutManager

    }


    private fun setListener() {
        setRightBtnClickListener(View.OnClickListener {
            //点击添加朋友按钮
//            switchToActivity(AddFriendActivity::class.java)
        })


        //点击每个条目
        newFriendAdapter?.setOnViewInItemClickListener(CommonRecyclerViewAdapter.OnViewInItemClickListener { v, position ->

            //            var id = newFriendDatas[position].id
//            val name = newFriendDatas[position].nickname
//            if (v.id == R.id.avatar) {
//                val bundle = Bundle()
//                bundle.putString(UserDetailActivity.ARG_NICKNAME, name)
//                //这里需要加上前缀,不然无法聊天(id不正确)
//                bundle.putString(UserDetailActivity.ARG_IDENTIFY, Constants.IM_PREFIX + id.toString())
//                bundle.putLong(UserDetailActivity.PARAM_ID, id)
//                switchToActivityForResult(UserDetailActivity::class.java, bundle, 1)
//            } else if (v.id == R.id.status) {
//                requestAddFriend(position, id, newFriendDatas[position].relationship)
//                LogUtil.d("ailibin", "relationship: " + newFriendDatas[position].relationship)
//            }

        }, R.id.avatar, R.id.status)
    }


    /**
     * 请求好友列表(1全部；2通讯录（我的好友）；3新的朋友；4黑名单)
     */
    private fun requestUserList(isPullRefresh: Boolean) {
//        val query = ListRequestQuery()
//        query.setNamespace("UserList")
//        query.action = AIIAction.THREE
//        query.setDir("sns")
//        val table = query.table
//        table.limit = 1000
//        if (!isPullRefresh) {
//            customDialogShow()
//        }
//        App.aiiRequest?.send(query, object : AIIResponse<UserListResponseQuery>(this, false) {
//            override fun onSuccess(response: UserListResponseQuery, index: Int) {
//                super.onSuccess(response, index)
//                if (response.users != null) {
//                    getUserList(response.users)
//                }
////                LogUtil.d("ailibin", "newFriend: user" + response.users.toString())
//            }
//
//            override fun onCache(content: UserListResponseQuery, index: Int) {
//                super.onCache(content, index)
//                if (content.users != null) {
//                    getUserList(content.users)
//                }
//            }
//
//            override fun onFinish(index: Int) {
//                super.onFinish(index)
//                swipeRefreshLayout.isRefreshing = false
//                customDialogDismiss()
//            }
//        })
    }

    private fun requestAddFriend(position: Int, id: Long, relationship: Int) {
//        val query = AddFriendRequestQuery()
//        query.setDir("sns")
//        query.id = id
//        //这里不需要这个字段,后台附带加上的
//        query.msg = "我是玉皇大帝"
//        //这里是接受(这里永远是接受)
//        query.action = AIIAction.TWO
//        App.aiiRequest?.send(query, object : AIIResponse<ResponseQuery>(this, progressDialog) {
//            override fun onSuccess(response: ResponseQuery, index: Int) {
//                super.onSuccess(response, index)
//                FriendshipManagerPresenter.acceptFriendRequest(Constants.IM_PREFIX + id, object : TIMValueCallBack<TIMFriendResult> {
//                    override fun onError(i: Int, s: String) {
//                        LogUtil.i("接受朋友出错$s   状态码：$s")
//                    }
//
//                    override fun onSuccess(timFriendResult: TIMFriendResult) {
//                        if (newFriendDatas[position] != null) {
//                            newFriendDatas[position].relationship = 3
//                        }
//                        BaseUtil.showToast("好友添加成功")
//                        newFriendAdapter?.update()
//                        //发一个广播过去通知好友列表数据刷新
//                        val intent = Intent()
//                        setResult(Activity.RESULT_OK, intent)
//                        if (!supportFragmentManager.isDestroyed) {
//                            finish()
//                        }
//                    }
//                })
//            }
//        })
    }

    /**
     * 对方接受了好友邀请之后,通知这个界面刷新(这个方法名不能和好友列表名称相同)
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun acceptNewFriend(event: Event.OnReceiveFriendEvent) {
//        if (event.tag == Constant.ON_RECEIVE_FRIEND_KEY) {
//            //对方接受之后,当前用户好友列表界面需要刷新
//            requestUserList(false)
//        }
    }

    /**
     * 当前好友在搜索页面发送邀请之后,通知这个界面刷新
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun postInvite(event: Event.OnPostInviteEvent) {
//        if (event.tag == Constant.ON_POST_INVITE_KEY) {
//            //对方接受之后,当前用户好友列表界面需要刷新
//            requestUserList(false)
//        }
    }

    private fun getUserList(users: List<ListUser>?) {
        newFriendDatas.clear()
        if (users != null) {
            newFriendDatas.addAll(users)
        }
        checkIsEmpty()
        LogUtil.d("ailibin", "newFriend: getUserList" + newFriendDatas.toString())
        newFriendAdapter?.update()
    }

    //如果没有数据,就用空布局
    private fun checkIsEmpty() {
        val tvNoData = empty_view.findViewById<TextView>(R.id.tv_no_data)
        if (newFriendDatas.size == 0) {
            empty_view?.visibility = View.VISIBLE
            tvNoData.visibility = View.VISIBLE
        } else {
            empty_view?.visibility = View.GONE
            tvNoData.visibility = View.GONE
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        requestUserList(false)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }
}