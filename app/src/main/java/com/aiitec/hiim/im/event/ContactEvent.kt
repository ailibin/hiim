package com.herentan.giftfly.ui.chat.event

/**
 * Created by ailibin on 2018/1/22.
 * 有关联系人的事件
 */
class ContactEvent {

    var ADD = 1
    var DELETE = 2
    fun ContactEvent(type: Int, id: String) {
        this.type = type
        this.id = id
    }

    private var id: String? = null
    private var type: Int = 0

}