package edu.csuft.phonesafe.utils;

import android.content.Context;
import android.preference.PreferenceManager;

/**
 * Created by Chalmers on 2016-07-12 14:10.
 * email:qxinhai@yeah.net
 */

/**
 * 轻量级存储工具类
 */
public class SharedUtil {
    /**
     * 获得设置界面中的数据
     *
     * @param context      上下文对象
     * @param keyValue     key值
     * @param defaultValue 默认值
     * @return 保存的数据，需要自行转换
     */
    public static Object getSavedDataFromSetting(Context context, String keyValue,
                                                 Object defaultValue) {
        Object result = null;
        //如果传入的默认值是boolean类型
        if (defaultValue instanceof Boolean) {        //如果是Boolean类型
            result = PreferenceManager.getDefaultSharedPreferences(context)
                    .getBoolean(keyValue, (Boolean) defaultValue);
        } else if (defaultValue instanceof String) {   //如果是String类型
            result = PreferenceManager.getDefaultSharedPreferences(context)
                    .getString(keyValue, (String) defaultValue);
        }

        return result;
    }
}
