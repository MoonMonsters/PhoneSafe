package edu.csuft.phonesafe.bean;

import android.graphics.drawable.Drawable;

/**
 * Created by Chalmers on 2016-07-06 23:35.
 * email:qxinhai@yeah.net
 */
public class AppManagerInfo implements Comparable<AppManagerInfo>{

    /**
     * 应用图标
     */
    private Drawable appIcon;
    /**
     * 应用名称
     */
    private String appName;
    /**
     * 应用版本
     */
    private String appVersion;
    /** 应用程序的包名 */
    private String packageName;

    public AppManagerInfo(){}

    public AppManagerInfo(Drawable appIcon, String appName,
                          String appVersion, String packageName) {
        this.appIcon = appIcon;
        this.appName = appName;
        this.appVersion = appVersion;
        this.packageName = packageName;
    }

    public Drawable getAppIcon() {
        return appIcon;
    }

    public void setAppIcon(Drawable appIcon) {
        this.appIcon = appIcon;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    /** 按应用程序的首字母进行排序，从小到大的顺序 */
    @Override
    public int compareTo(AppManagerInfo another) {

        return this.appName.charAt(0) - another.getAppName().charAt(0);
    }
}
