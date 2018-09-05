package com.aiitec.widgets

import android.content.Context
import com.aiitec.hiim.R

/**
 * @author  Anthony
 * @version 1.0
 * createTime 2017/11/13.
 */
class CommonDialog(context: Context) : AbsCommonDialog(context) {

    override fun animStyle(): Int = R.style.dialogAnimationStyle

    override fun widthScale(): Float = 0.7f

    override fun layoutId(): Int = R.layout.dialog_common

}