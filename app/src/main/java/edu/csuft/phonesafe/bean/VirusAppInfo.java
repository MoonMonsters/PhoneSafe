package edu.csuft.phonesafe.bean;

import android.graphics.drawable.Drawable;

/**
 * Created by Chalmers on 2016-07-07 18:21.
 * email:qxinhai@yeah.net
 */
/**
 * 在病毒查杀界面需要用到的bean对象
 */
public class VirusAppInfo {
    /** 应用图标 */
    private Drawable appIcon;
    /** 应用名称 */
    private String appName;
    /** 包名 */
    private String packageName;

    public VirusAppInfo(Drawable appIcon, String appName, String packageName) {
        this.appIcon = appIcon;
        this.appName = appName;
        this.packageName = packageName;
    }

    public VirusAppInfo(){}

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

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
}
