package edu.csuft.phonesafe.bean;

import android.graphics.drawable.Drawable;

/**
 * Created by Chalmers on 2016-06-29 19:14.
 * email:qxinhai@yeah.net
 */

/**
 * 流量信息的JavaBean
 */
public class TrafficInfo implements Comparable{
    /** app图标 */
    private Drawable appIcon;
    /** app唯一标识 */
    private int uid;
    /** app所在包名 */
    private String appPackage;
    /** app程序名 */
    private String appName;
    /** 流量使用量 */
    private long trafficTotal;

    public TrafficInfo(Drawable appIcon, int uid, String appPackage, String appName,
                       long trafficTotal) {
        this.appIcon = appIcon;
        this.uid = uid;
        this.appPackage = appPackage;
        this.appName = appName;
        this.trafficTotal = trafficTotal;
    }

    public TrafficInfo() {
    }

    public Drawable getAppIcon() {
        return appIcon;
    }

    public void setAppIcon(Drawable appIcon) {
        this.appIcon = appIcon;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getAppPackage() {
        return appPackage;
    }

    public void setAppPackage(String appPackage) {
        this.appPackage = appPackage;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public long getTrafficTotal() {
        return trafficTotal;
    }

    public void setTrafficTotal(long trafficTotal) {
        this.trafficTotal = trafficTotal;
    }

    @Override
    public int compareTo(Object another) {

        TrafficInfo that = (TrafficInfo)another;

        int result = 0;
        if(this.trafficTotal < that.getTrafficTotal()){
            result = 1;
        }else{
            result = -1;
        }

        return result;
    }
}
