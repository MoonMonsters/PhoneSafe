package edu.csuft.phonesafe.bean;

/**
 * Created by Chalmers on 2016-07-09 10:26.
 * email:qxinhai@yeah.net
 */

/**
 * 父项的信息
 */
public class CCleanerParentInfo {
    /**
     * 父项的描述信息
     */
    private String type;
    /**
     * 该项是否扫描完成或者选中，不同状况有不同的图片
     */
    private boolean isSelect;
    /**
     * 该项下垃圾的大小
     */
    private long value;

    public CCleanerParentInfo(String type, boolean isSelect, long value) {
        this.type = type;
        this.isSelect = isSelect;
        this.value = value;
    }

    public CCleanerParentInfo(){}

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }
}
