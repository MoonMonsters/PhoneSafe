package edu.csuft.phonesafe.bean;

import java.util.ArrayList;

/**
 * Created by Chalmers on 2016-07-15 22:21.
 * email:qxinhai@yeah.net
 */

/**
 * 权限集合，用来存放解析json数据后
 */
public class Permissions {

    /** PermissionInfo集合 */
    private ArrayList<PermissionInfo> permissionInfos;

    public Permissions(ArrayList<PermissionInfo> permissionInfos) {
        this.permissionInfos = permissionInfos;
    }

    public Permissions() {
    }

    public ArrayList<PermissionInfo> getPermissionInfos() {
        return permissionInfos;
    }

    public void setPermissionInfos(ArrayList<PermissionInfo> permissionInfos) {
        this.permissionInfos = permissionInfos;
    }
}
