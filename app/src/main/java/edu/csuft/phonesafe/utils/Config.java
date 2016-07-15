package edu.csuft.phonesafe.utils;

/**
 * Created by Chalmers on 2016-07-07 15:55.
 * email:qxinhai@yeah.net
 */

/**
 * 常量值的类
 */
public class Config {

    /** 加载完成 */
    public static final int SUCCESS_LOAD = 0;
    /** 进度条的值 */
    public static final int UPDATE_PROGRESS = 1;
    /** 改变选择，选择不清理，或者选择清理 ,或者已经清理完成*/
    public static final int CHANGE_SELECTED_OR_CLEAR = 2;

    /** 缓存清理 */
    public static final int HUAN_CUN_QING_LI = 0;
    /** 日志清理 */
    public static final int RI_ZHI_QING_LI = 3;
    /** 大文件清理 */
    public static final int DA_WEN_JIAN_QING_LI = 2;
    /** 安装包清理 */
    public static final int AN_ZHUANG_BAO_QING_LI = 1;
    /** 空文件夹清理 */
    public static final int KONG_WEN_JIAN_JIA_QING_LI = 4;
    /** 定义超过10M就成为大文件 */
    public static final long BIG_FILE_SIZE = 10 * 1024 * 1024;

    /** 改变选择的广播 */
    public static final String CHANGE_SELECTION_BROADCAST = "android.intent.action_change_selection_broadcast";
    /** 所有的数据 */
    public static final String TOTAL_SIZE_DATA = "total_size_data";

    /** 自动联网更新 */
    public static final String KEY_AUTO_UPDATE = "key_switch_autoUpdate";

    /** 更新app的网址 */
  public static final String UPDATE_URL = "http://192.168.191.1:8080/PhoneSafe/update.xml";
//  public static final String UPDATE_URL = "http://10.0.2.2:8080/PhoneSafe/update.xml";
}
