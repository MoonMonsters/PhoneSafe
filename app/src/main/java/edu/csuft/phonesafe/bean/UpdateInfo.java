package edu.csuft.phonesafe.bean;

/**
 * Created by Chalmers on 2016-06-19 13:35.
 * email:qxinhai@yeah.net
 */

/**
 * 下载的xml文件中所包含的数据类型
 */
public class UpdateInfo {
    /** 版本号 */
    private String version;
    /** 描述信息 */
    private String description;
    /** 新版本下载网址 */
    private String apkurl;

    public UpdateInfo(String version, String description, String apkurl) {
        this.version = version;
        this.description = description;
        this.apkurl = apkurl;
    }

    public UpdateInfo() {
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getApkurl() {
        return apkurl;
    }

    public void setApkurl(String apkurl) {
        this.apkurl = apkurl;
    }
}
