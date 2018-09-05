package com.aiitec.letar.mvp.base

/**
 * Created by ailibin on 2018/5/4.
 */
interface IBaseCallbackListener {

    /**
     * 服务端请求成功的回调
     */
    fun onSuccess()

    /**
     * 请求完成(不管失败还是成功)
     */
    fun onFinish()

    /**
     * 请求开始调用
     */
    fun onStart()

    /**
     * 服务端请求失败的回调
     */
    fun onFail()

}