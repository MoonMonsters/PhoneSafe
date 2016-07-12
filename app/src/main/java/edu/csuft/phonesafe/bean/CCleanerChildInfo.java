package edu.csuft.phonesafe.bean;

/**
 * Created by Chalmers on 2016-07-09 10:28.
 * email:qxinhai@yeah.net
 */

import android.graphics.drawable.Drawable;

/**
 * 子项的信息，因为该类可以适用于缓存清理和文件清理，所以放到一起来处理
 */
public class CCleanerChildInfo {

    /**
     * 应用图标
     */
    private Drawable appIcon;
    /**
     * 应用名称，或者文件名
     */
    private String appNameOrfileName;
    /**
     * 包名，或者文件
     */
    private String packageNameOrFilePath;
    /**
     * 是否被选中，如果选中，则需要清理
     */
    private boolean isSelected;
    /**
     * 垃圾大小,或者文件大小
     */
    private long value;
    /**
     * 文件类型，有4种类型，包括
     * 缓存清理，安装包清理，大文件清理，日志清理，用来设置图片的
     */
    private int type;

    public CCleanerChildInfo(Drawable appIcon, String appNameOrfileName,
                             String packageNameOrFilePath, boolean isSelected,
                             long value, int type) {
        this.appIcon = appIcon;
        this.appNameOrfileName = appNameOrfileName;
        this.packageNameOrFilePath = packageNameOrFilePath;
        this.isSelected = isSelected;
        this.value = value;
        this.type = type;
    }

    public CCleanerChildInfo() {
    }

    public Drawable getAppIcon() {
        return appIcon;
    }

    public void setAppIcon(Drawable appIcon) {
        this.appIcon = appIcon;
    }

    public String getAppNameOrfileName() {
        return appNameOrfileName;
    }

    public void setAppNameOrfileName(String appNameOrfileName) {
        this.appNameOrfileName = appNameOrfileName;
    }

    public String getPackageNameOrFilePath() {
        return packageNameOrFilePath;
    }

    public void setPackageNameOrFilePath(String packageNameOrFilePath) {
        this.packageNameOrFilePath = packageNameOrFilePath;
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
