package com.jyh.gxcjzbs.common.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.Set;

public class SPUtils {

    private static SharedPreferences sp;
    private static String NAME = "spsave";

    /**
     * 保存数据
     *
     * @param mContext
     * @param key
     * @param value
     */
    public static void save(Context mContext, String key, Object value) {
        initial(mContext);
        if (value == null) {
            sp.edit().putStringSet(key, null).commit();
            sp.edit().putString(key, null).commit();
        } else {
            if (value instanceof String) {
                sp.edit().putString(key, (String) value).commit();
            } else if (value instanceof Boolean) {
                sp.edit().putBoolean(key, (Boolean) value).commit();
            } else if (value instanceof Integer) {
                sp.edit().putInt(key, (Integer) value).commit();
            } else if (value instanceof Float) {
                sp.edit().putFloat(key, (Float) value).commit();
            } else if (value instanceof Long) {
                sp.edit().putLong(key, (Long) value).commit();
            } else if (value instanceof Set) {
                sp.edit().putStringSet(key, (Set<String>) value).commit();
            }
        }
    }

    public static int getInteger(Context mContext, String key) {
        initial(mContext);
        return sp.getInt(key, 0);
    }

    public static Float getFloat(Context mContext, String key) {
        initial(mContext);
        return sp.getFloat(key, 0F);
    }

    public static Long getLong(Context mContext, String key) {
        initial(mContext);
        return sp.getLong(key, 0L);
    }

    public static Boolean getBoolean(Context mContext, String key) {
        initial(mContext);
        return sp.getBoolean(key, false);
    }

    public static String getString(Context mContext, String key) {
        initial(mContext);
        if (sp == null)
            return "";
        return sp.getString(key, "");
    }

    public static Set<String> getStringSet(Context mContext, String key) {
        initial(mContext);
        return sp.getStringSet(key, null);
    }

    public static Set<String> getStringSet(Context mContext, String fileName, String key) {
        initial2(mContext, fileName);
        return sp.getStringSet(key, null);
    }

    private static void initial(Context mContext) {
        sp = mContext.getSharedPreferences(NAME, 0);
    }

    /**
     * 第二种存储文件的方式
     *
     * @param mContext 传回comtext
     * @param fileName 存储的文件名
     * @param key      所要存储的key值
     * @param value    所要存储的key值得value
     */
    public static void save2(Context mContext, String fileName, String key, Object value) {
        initial2(mContext, fileName);
        if (value instanceof String) {
            sp.edit().putString(key, (String) value).commit();
        } else if (value instanceof Boolean) {
            sp.edit().putBoolean(key, (Boolean) value).commit();
        } else if (value instanceof Integer) {
            sp.edit().putInt(key, (Integer) value).commit();
        } else if (value instanceof Float) {
            sp.edit().putFloat(key, (Float) value).commit();
        } else if (value instanceof Long) {
            sp.edit().putLong(key, (Long) value).commit();
        }else if(value instanceof Set){
            sp.edit().putStringSet(key, (Set<String>) value).commit();
        }
    }

    // 第二种方式进行存储和获取
    public static Boolean getBoolean2(Context mContext, String fileName, String key) {
        initial2(mContext, fileName);
        return sp.getBoolean(key, false);
    }

    public static String getString2(Context mContext, String fileName, String key) {
        initial2(mContext, fileName);
        return sp.getString(key, "");
    }

    /**
     * 第二种情况下清空某个文件下的所有的数据
     *
     * @param mContext
     */
    public static void removeFile(Context mContext) {
        initial(mContext);
        Editor editor = sp.edit();
        editor.clear();
        editor.commit();
    }

    /**
     * 第二种情况下清空某个文件下的所有的数据
     *
     * @param mContext
     * @param fileName
     */
    public static void removeFile2(Context mContext, String fileName) {
        initial2(mContext, fileName);
        Editor editor = sp.edit();
        editor.clear();
        editor.commit();
    }

    public static void initial2(Context mContext, String fileName) {
        sp = mContext.getSharedPreferences(fileName, 0);
    }


}