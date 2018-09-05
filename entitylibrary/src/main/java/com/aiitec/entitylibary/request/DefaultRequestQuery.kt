package com.aiitec.entitylibary.request

import com.aiitec.openapi.model.RequestQuery

/**
 * @author  Anthony
 * @version 1.0
 * createTime 2017/12/18.
 */
class DefaultRequestQuery() : RequestQuery(){

    constructor(namespace : String) : this() {
        this.namespace = namespace
    }
    var id : Long = 0
}