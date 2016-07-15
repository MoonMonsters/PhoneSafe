package edu.csuft.phonesafe.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Environment;
import android.os.StatFs;

import java.io.File;

/**
 * Created by Chalmers on 2016-07-06 18:19.
 * email:qxinhai@yeah.net
 */

/**
 * 得到存储空间数据和内存空间数据的工具类
 */
public class StorageUtil {

    /** 得到手机剩余可用空间 */
    public static long getAvailableMemorySize(){
        //根目录
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        //每一块的大小
        long blockSize = stat.getBlockSizeLong();
        //整个存储空间的有多少块
        long availableBlocks = stat.getAvailableBlocksLong();

        //返回他们的乘积即可以用大小
        return availableBlocks * blockSize;
    }

    /**
     * 获取手机内部总的存储空间
     */
    public static long getTotalMemorySize() {
        //跟目录
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        //每一块的大小
        long blockSize = stat.getBlockSizeLong();
        //总共的块数
        long totalBlocks = stat.getBlockCountLong();

        //返回总的大小
        return totalBlocks * blockSize;
    }

    /** 得到可用内存空间 */
    public static long getAvailableRAMSize(Context context){
        //包管理器
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        //创建MemoryInfo对象
        ActivityManager.MemoryInfo info = new ActivityManager.MemoryInfo();
        //将内存数据放入info对象中
        am.getMemoryInfo(info);

        //返回可用内存大小
        return info.availMem;
    }

    /**
     *  得到总的内存空间
     *  @param context 上下文对象
     */
    public static long getTotalRAMSize(Context context){
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo info = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(info);

        //返回总的内存大小
        return info.totalMem;
    }
}
