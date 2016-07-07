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
public class StorageUtil {

    /** 得到手机剩余可用空间 */
    public static long getAvailableMemorySize(){
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSizeLong();
        long availableBlocks = stat.getAvailableBlocksLong();

        return availableBlocks * blockSize;
    }

    /**
     * 获取手机内部总的存储空间
     */
    public static long getTotalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSizeLong();
        long totalBlocks = stat.getBlockCountLong();
        return totalBlocks * blockSize;
    }

    /** 得到可用内存空间 */
    public static long getAvailableRAMSize(Context context){
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo info = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(info);

        return info.availMem;
    }

    /** 得到总的内存空间 */
    public static long getTotalRAMSize(Context context){
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo info = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(info);

        return info.totalMem;
    }
}
