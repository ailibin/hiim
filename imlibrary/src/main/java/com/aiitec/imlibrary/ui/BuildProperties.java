package com.aiitec.imlibrary.ui;

/**
 * @author Anthony
 * @version 1.0
 *          createTime 2017/10/27.
 */

import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

//引用到的工具类

public class BuildProperties {

    // 检测MIUI
    private static final String KEY_MIUI_VERSION_CODE = "ro.miui.ui.version.code";
    private static final String KEY_MIUI_VERSION_NAME = "ro.miui.ui.version.name";
    private static final String KEY_MIUI_INTERNAL_STORAGE = "ro.miui.internal.storage";
    private static final String IS_MIUI = "com.aiitec.hexagear.IS_MIUI";
    private final Properties properties;
    public static boolean isMIUI;

    private BuildProperties()  {

        properties = new Properties();
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    properties.load(new FileInputStream(new File(Environment.getRootDirectory(), "build.prop")));

                    isMIUI = getProperty(KEY_MIUI_VERSION_CODE, null) != null
                            || getProperty(KEY_MIUI_VERSION_NAME, null) != null
                            || getProperty(KEY_MIUI_INTERNAL_STORAGE, null) != null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }

    public boolean containsKey(final Object key) {
        return properties.containsKey(key);
    }

    public boolean containsValue(final Object value) {
        return properties.containsValue(value);
    }

    public Set<Entry<Object, Object>> entrySet() {
        return properties.entrySet();
    }

    public String getProperty(final String name) {
        return properties.getProperty(name);
    }

    public String getProperty(final String name, final String defaultValue) {
        return properties.getProperty(name, defaultValue);
    }

    public boolean isEmpty() {
        return properties.isEmpty();
    }

    public Enumeration<Object> keys() {
        return properties.keys();
    }

    public Set<Object> keySet() {
        return properties.keySet();
    }

    public int size() {
        return properties.size();
    }

    public Collection<Object> values() {
        return properties.values();
    }

    public static BuildProperties newInstance()  {
        return new BuildProperties();
    }

}