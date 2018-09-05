package com.aiitec.letar.mvp.mine

import android.content.Context
import java.lang.ref.WeakReference

abstract class BasicPresenter<V : IAppView> : IAppPresenter {

    private var mView : V? = null
    private var mTag: String? = null
    private var mContextWeakReference: WeakReference<Context>? = null

    fun BasicPresenter(view: V) {
        mView = view
        mContextWeakReference = WeakReference(view.getContext())
        mTag = javaClass.simpleName
    }

    fun getView() : V? {
        return mView
    }

    protected fun getContext(): Context? {
        return if (mContextWeakReference == null) null else mContextWeakReference!!.get()
    }

    override fun start() {

    }

    override fun destroy() {

    }

}