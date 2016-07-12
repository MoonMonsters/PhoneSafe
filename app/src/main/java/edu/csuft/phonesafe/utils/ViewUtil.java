package edu.csuft.phonesafe.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.telephony.TelephonyManager;

/**
 * Created by Chalmers on 2016-06-19 12:09.
 * email:qxinhai@yeah.net
 */
public class ViewUtil {

    /**
     * 获得程序的版本号
     * @param context 上下文对象
     * @return 版本号
     */
    public static String getAppVersion(Context context){
        String version = "1.0";

        PackageManager packageManager = context.getPackageManager();
        try {
            PackageInfo info = packageManager.getPackageInfo(context.getPackageName(),
                    PackageManager.COMPONENT_ENABLED_STATE_DEFAULT);
            version = info.versionName;

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return version;
    }

    /**
     * 得到手机卡的Serial值
     * @param context 上下文对象
     * @return 手机卡的Serial值
     */
    public static String getSimSerialValue(Context context){
        TelephonyManager tm = (TelephonyManager) context
                .getSystemService(context.TELEPHONY_SERVICE);
        String simSerialNumber = tm.getSimSerialNumber();

        return simSerialNumber;
    }
}


