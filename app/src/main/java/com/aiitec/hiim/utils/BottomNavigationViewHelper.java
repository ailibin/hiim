package com.aiitec.hiim.utils;

import android.annotation.SuppressLint;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;

import com.aiitec.hiim.im.utils.LogUtil;

import java.lang.reflect.Field;

/**
 * 本类主要是为了去掉 BottomNavigationMenuView 的动画效果
 * @author Anthony
 * @version 1.0
 * createTime 2018/4/28.
 */

public class BottomNavigationViewHelper {
    @SuppressLint("RestrictedApi")
    public static void disableShiftMode(BottomNavigationView view) {
        if(view != null && view.getChildCount() > 0){
            BottomNavigationMenuView menuView = (BottomNavigationMenuView) view.getChildAt(0);
            try {
                Field shiftingMode = menuView.getClass().getDeclaredField("mShiftingMode");
                shiftingMode.setAccessible(true);
                shiftingMode.setBoolean(menuView, false);
                shiftingMode.setAccessible(false);
                for (int i = 0; i < menuView.getChildCount(); i++) {
                    BottomNavigationItemView item = (BottomNavigationItemView) menuView.getChildAt(i);
                    //noinspection RestrictedApi
//                    item.setShiftingMode(false);
//                    item.setShifting(false);
                    // set once again checked value, so view will be updated
                    //noinspection RestrictedApi
                    item.setChecked(item.getItemData().isChecked());
                }
            } catch (NoSuchFieldException e) {
                LogUtil.e("BNVHelper", "Unable to get shift mode field");
            } catch (IllegalAccessException e) {
                LogUtil.e("BNVHelper", "Unable to change value of shift mode");
            }
        }

    }
}
