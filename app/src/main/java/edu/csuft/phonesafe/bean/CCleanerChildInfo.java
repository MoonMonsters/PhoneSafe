package edu.csuft.phonesafe.bean;

/**
 * Created by Chalmers on 2016-07-09 10:28.
 * email:qxinhai@yeah.net
 */

import android.graphics.drawable.Drawable;

/**
 * 子项的信息
 */
public class CCleanerChildInfo {

    /**
     * 应用图标
     */
    private Drawable appIcon;
    /**
     * 应用名称
     */
    private String appName;
    /**
     * 包名
     */
    private String packageName;
    /**
     * 是否被选中，如果选中，则需要清理
     */
    private boolean isSelected;
    /**
     * 垃圾大小
     */
    private long value;

    public CCleanerChildInfo(Drawable appIcon, String appName,
                             String packageName, boolean isSelected,
                             long value) {
        this.appIcon = appIcon;
        this.appName = appName;
        this.packageName = packageName;
        this.isSelected = isSelected;
        this.value = value;
    }

    public CCleanerChildInfo() {
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

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }
}
