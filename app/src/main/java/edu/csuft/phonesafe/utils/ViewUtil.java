package edu.csuft.phonesafe.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * Created by Chalmers on 2016-06-19 12:09.
 * email:qxinhai@yeah.net
 */

/**
 * 视图工具类
 */
public class ViewUtil {

    /**
     * 获得程序的版本号
     * @param context 上下文对象
     * @return 版本号
     */
    public static String getAppVersion(Context context){
        String version = "1.0";

        //得到包管理器
        PackageManager packageManager = context.getPackageManager();
        try {
            //得到PackageInfo对象
            PackageInfo info = packageManager.getPackageInfo(context.getPackageName(),
                    PackageManager.COMPONENT_ENABLED_STATE_DEFAULT);
            //当前版本号
            version = info.versionName;

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        //返回版本号
        return version;
    }
}


