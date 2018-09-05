package com.aiitec.letar.mvp.mine.me

import android.content.Context
import com.aiitec.entitylibary.request.ArticleDetailsRequestQuery
import com.aiitec.entitylibary.request.UserDetailsRequestQuery
import com.aiitec.entitylibary.response.ArticleDetailsResponseQuery
import com.aiitec.entitylibary.response.UserDetailsResponseQuery
import com.aiitec.letar.mvp.mine.BasicPresenter
import com.aiitec.hiim.base.App
import com.aiitec.hiim.base.Constants
import com.aiitec.openapi.json.enums.AIIAction
import com.aiitec.openapi.net.AIIResponse

class MinePresenterImpl(v : MineContract.View) : BasicPresenter<MineContract.View>() , MineContract.Presenter{

    override fun getPhone() {
        requestArticleDetails()
    }

    var view: MineContract.View? = null

    override fun start() {
        super.start()
        requestUserDetail(getContext()!!)
    }

    override fun getUserInfo() {

    }

    init {
        var view = v
        super.BasicPresenter(view)
    }

    /**
     * 请求用户详情
     */
    fun requestUserDetail(context: Context) {
        val query = UserDetailsRequestQuery()
        query.setDir("Cis")
        query.id = 0
        App.aiiRequest?.send(query, object : AIIResponse<UserDetailsResponseQuery>(context, false) {
            override fun onSuccess(response: UserDetailsResponseQuery, index: Int) {
                super.onSuccess(response, index)
                Constants.user = response?.user
                getView()?.onUserInfoSuccess(response.user!!)
            }

            override fun onServiceError(content: String?, status: Int, index: Int) {
                super.onServiceError(content, status, index)
                getView()?.onUserInfoError()
            }

            override fun onFailure(content: String?, index: Int) {
                super.onFailure(content, index)
                getView()?.onUserInfoError()
            }
        })
    }

    //获取客服电话
    fun requestArticleDetails(){
        val query = ArticleDetailsRequestQuery()
        query.setDir("Cms")
        query.action = AIIAction.valueOf(10)
        query.id = 0
        App.aiiRequest?.send(query, object : AIIResponse<ArticleDetailsResponseQuery>(getContext(), false) {
            override fun onSuccess(response: ArticleDetailsResponseQuery, index: Int) {
                super.onSuccess(response, index)
                getView()?.onPhoneSuccess(response.articles!!.content)
            }

            override fun onServiceError(content: String?, status: Int, index: Int) {
                super.onServiceError(content, status, index)
                getView()?.onUserInfoError()
                //listener?.onUserDetailFailResponse()
            }

            override fun onFailure(content: String?, index: Int) {
                super.onFailure(content, index)
                //listener?.onUserDetailFailResponse()
            }
        })
    }
}