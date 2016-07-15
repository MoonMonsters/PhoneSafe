package edu.csuft.phonesafe.bean;

import android.graphics.drawable.Drawable;

/**
 * Created by Chalmers on 2016-07-03 21:21.
 * email:qxinhai@yeah.net
 */

/**
 * 任务管理界面需要用到的bean文件
 */
public class TaskInfo {
    /**
     * 图标
     */
    private Drawable appIcon;
    /**
     * 应用名
     */
    private String appName;
    /**
     * 包名
     */
    private String packageName;
    /**
     * pid，应用的标志
     */
    private int pid;
    /**
     * 该程序占用的内存
     */
    private long appMemory;
    /**
     * 是否被选中
     */
    private boolean isSelected;

    public TaskInfo(Drawable appIcon, String appName, String packageName,
                    int pid, long appMemory) {
        this.appIcon = appIcon;
        this.appName = appName;
        this.packageName = packageName;
        this.pid = pid;
        this.appMemory = appMemory;
        this.isSelected = false;
    }

    public TaskInfo() {
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

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public long getAppMemory() {
        return appMemory;
    }

    public void setAppMemory(long appMemory) {
        this.appMemory = appMemory;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
