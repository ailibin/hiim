package com.aiitec.letar.mvp.mine.me

import com.aiitec.entitylibary.model.User
import com.aiitec.letar.mvp.mine.IAppPresenter
import com.aiitec.letar.mvp.mine.IAppView

interface MineContract {

    interface Presenter : IAppPresenter {

        fun getUserInfo()

        fun getPhone()
    }

    interface View : IAppView {

        fun onUserInfoSuccess(user : User)

        fun onUserInfoError()

        fun onPhoneSuccess(phone : String?)

    }

}