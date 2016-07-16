package edu.csuft.phonesafe.bean;

/**
 * Created by Chalmers on 2016-07-15 22:14.
 * email:qxinhai@yeah.net
 */

/**
 * 对权限的描述和功能介绍
 */
public class PermissionInfo {

    /**
     * 权限名称
     */
    private String permission;
    /**
     * 对权限的描述
     */
    private String describe;

    public PermissionInfo(String permission, String describe) {
        this.permission = permission;
        this.describe = describe;
    }

    public PermissionInfo() {
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }
}
