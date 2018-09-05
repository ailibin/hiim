package com.aiitec.entitylibary.response

import com.aiitec.entitylibary.model.Item
import com.aiitec.openapi.model.ListResponseQuery

/**
 * Created by ailibin on 2018/6/28.
 */
class SettingResponseQuery : ListResponseQuery() {

    var public: List<Item>? = null
}