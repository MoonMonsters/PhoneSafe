package edu.csuft.phonesafe.bean;

import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Chalmers on 2016-07-06 23:35.
 * email:qxinhai@yeah.net
 */
public class AppManagerInfo implements Comparable<AppManagerInfo>,Parcelable {

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

    protected AppManagerInfo(Parcel in) {
        appName = in.readString();
        appVersion = in.readString();
        packageName = in.readString();
    }

    public static final Creator<AppManagerInfo> CREATOR = new Creator<AppManagerInfo>() {
        @Override
        public AppManagerInfo createFromParcel(Parcel in) {
            return new AppManagerInfo(in);
        }

        @Override
        public AppManagerInfo[] newArray(int size) {
            return new AppManagerInfo[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(appName);
        dest.writeString(appVersion);
        dest.writeString(packageName);
    }
}
