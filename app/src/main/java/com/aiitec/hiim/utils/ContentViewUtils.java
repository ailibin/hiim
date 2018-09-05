package com.aiitec.hiim.utils;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;

import com.aiitec.hiim.annotation.ContentView;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @Author Anthony
 * @Version 1.0
 * createTime 2017/3/17.
 */

public class ContentViewUtils {
    public static void inject(Activity activity) {
        injectObject(activity, null);
    }

    public static View inject(Fragment fragment) {
        return injectObject(fragment, null);
    }

    public static View inject(android.app.Fragment fragment) {
        return injectObject(fragment, null);
    }

    public static View inject(Fragment fragment, View view) {
        return injectObject(fragment, view);
    }

    public static View inject(android.app.Fragment fragment, View view) {
        return injectObject(fragment, view);
    }

    private static View injectObject(Object obj, View view) {

        if (view != null) {
            return view;
        }
        if (obj instanceof Activity) {
            try {
                ContentView contentView = obj.getClass().getAnnotation(ContentView.class);
                if (contentView != null && contentView.value() > 0) {
                    Method method = obj.getClass().getMethod("setContentView", int.class);
                    if (method != null) {
                        method.invoke(obj, contentView.value());
                    }
                }
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        } else if (Fragment.class.isAssignableFrom(obj.getClass())) {
            Context context = ((Fragment) obj).getActivity();
            try {
                ContentView contentView = obj.getClass().getAnnotation(ContentView.class);
                if (contentView != null && contentView.value() > 0) {
                    view = LayoutInflater.from(context).inflate(contentView.value(), null);
                }
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        } else if (android.app.Fragment.class.isAssignableFrom(obj.getClass())) {
            Context context = ((android.app.Fragment) obj).getActivity();
            try {
                 ContentView contentView = obj.getClass().getAnnotation(ContentView.class);
                if (contentView != null && contentView.value() > 0) {
                    view = LayoutInflater.from(context).inflate(contentView.value(), null);
                }
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }

        }
        return view;
    }
}
