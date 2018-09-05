package com.aiitec.letar.mvp.base

/**
 * @author ailibin
 * @time 2018/5/4.
 */
interface IBasePresenter {

    /**
     * activity销毁的时候,相应的对象引用置为空,内存空间大小要保证适中
     */
    fun onDestroy()
}