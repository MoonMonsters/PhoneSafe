package edu.csuft.phonesafe.bean;

/**
 * Created by Chalmers on 2016-07-06 22:59.
 * email:qxinhai@yeah.net
 */
public class FunctionInfo {
    /**
     * 功能对应图片
     */
    private int functionImgId;
    /**
     * 功能名称
     */
    private String functionText;

    public FunctionInfo(int functionImgId, String functionText) {
        this.functionImgId = functionImgId;
        this.functionText = functionText;
    }

    public int getFunctionImgId() {
        return functionImgId;
    }

    public void setFunctionImgId(int functionImgId) {
        this.functionImgId = functionImgId;
    }

    public String getFunctionText() {
        return functionText;
    }

    public void setFunctionText(String functionText) {
        this.functionText = functionText;
    }
}
