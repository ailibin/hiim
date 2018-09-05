package com.aiitec.entitylibary.request

import com.aiitec.entitylibary.model.Where
import com.aiitec.openapi.model.RequestQuery

/**
 * Created by ailibin on 2018/5/3.
 */
class UserDetailsRequestQuery :RequestQuery(){

    var where: Where? = null
    /**
     * 用户id,登陆切换的时候用到
     */
    var id: Long = -1

}