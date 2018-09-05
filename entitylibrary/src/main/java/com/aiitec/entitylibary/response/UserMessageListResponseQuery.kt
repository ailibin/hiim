package com.aiitec.entitylibary.response

import com.aiitec.entitylibary.model.Message
import com.aiitec.openapi.model.ListResponseQuery


/**
 * 消息中心列表
 */
class UserMessageListResponseQuery : ListResponseQuery() {

    var messages: List<Message>? = null
}