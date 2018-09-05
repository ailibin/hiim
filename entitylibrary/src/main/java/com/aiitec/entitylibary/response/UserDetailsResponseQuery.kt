package com.aiitec.entitylibary.response

import com.aiitec.entitylibary.model.User
import com.aiitec.openapi.model.ResponseQuery

/**
 * Created by ailibin on 2017/12/7.
 * 用户详情返回数据
 */
class UserDetailsResponseQuery : ResponseQuery() {

    /**
     * 返回用户数据
     */
    var user: User? = null


}