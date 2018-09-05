package com.aiitec.letar.mvp.base

/**
 * Created by ailibin on 2018/5/4.
 */
interface IBaseView {

    /**
     * 展示加载数据中的进度条
     */
    fun showProgress()

    /**
     * 隐藏加载数据中的进度条
     */
    fun hideProgress()

}