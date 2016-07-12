package edu.csuft.phonesafe.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import butterknife.Bind;
import edu.csuft.phonesafe.R;
import edu.csuft.phonesafe.adapter.CCleanerAdapter;
import edu.csuft.phonesafe.base.BaseActivity;
import edu.csuft.phonesafe.bean.CCleanerChildInfo;
import edu.csuft.phonesafe.bean.CCleanerParentInfo;
import edu.csuft.phonesafe.utils.Config;
import edu.csuft.phonesafe.view.MarqueeText;

/**
 * 垃圾清理界面
 */
public class CCleanerActivity extends BaseActivity {

    @Bind(R.id.elv_ccleaner)
    ExpandableListView elv_ccleaner;
    //垃圾大小
    @Bind(R.id.tv_ccleaner_file_size)
    TextView tv_ccleaner_file_size;
    //单位
    @Bind(R.id.tv_ccleaner_file_unit)
    TextView tv_ccleaner_file_unit;
    //文件名
    @Bind(R.id.tv_cleaner_file_name)
    MarqueeText tv_cleaner_file_name;
    @Bind(R.id.btn_ccleaner_clear)
    Button btn_ccleaner_clear;

    /**
     * 传递的文件名
     */
    public static final String FILE_NAME = "file_name";
    /**
     * 传递的文件大小
     */
    public static final String FILE_SIZE = "file_size";

    /**
     * 父项集合
     */
    private ArrayList<CCleanerParentInfo> parentInfoList = null;
    /**
     * 所有的子项集合，即字项集合的集合
     */
    private ArrayList<ArrayList<CCleanerChildInfo>> allChildInfoList = null;
    /**
     * 缓存文件集合
     */
    private ArrayList<CCleanerChildInfo> cacheList = null;
    /**
     * apk文件集合
     */
    private ArrayList<CCleanerChildInfo> apkList = null;
    /**
     * 日志文件集合
     */
    private ArrayList<CCleanerChildInfo> logList = null;
    /**
     * 大文件集合
     */
    private ArrayList<CCleanerChildInfo> bigFileList = null;
    /**
     * 空文件夹集合
     */
    private ArrayList<CCleanerChildInfo> noneDirList = null;

    private PackageManager mPackageManager = null;
    /**
     * 适配器
     */
    private CCleanerAdapter cCleanerAdapter = null;
    /**
     * 线程池
     */
    Executor threadPool = Executors.newFixedThreadPool(10);
    /**
     * 判断扫描是否完成
     */
    TimerTask timerTask;
    /**
     * 要清理的垃圾的总大小
     */
    private long totalSize = 0;
    /**
     * 广播
     */
    CCleanerBroadCast cCleanerBroadCast = null;
    /**
     * 正在扫描的文件名，当10秒后文件名没有发生改变的话，就说明扫描完成
     */
    private String fileNameText = null;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Config.UPDATE_PROGRESS:
                    Bundle data = msg.getData();
                    //文件名
                    String fileName = data.getString(FILE_NAME, null);
                    //总大小
                    totalSize += data.getLong(FILE_SIZE);
                    //设置文件名
                    tv_cleaner_file_name.setText("正在扫描.." + fileName);
                    setFileTotalSize();
                    break;
                case Config.SUCCESS_LOAD:
                    tv_cleaner_file_name.setText("扫描完成");
                    break;
                case Config.CHANGE_SELECTED_OR_CLEAR:
                    Bundle b = msg.getData();
                    long size = b.getLong(Config.TOTAL_SIZE_DATA);
                    totalSize += size;
                    //将总的大小转换成合适的单位格式
                    setFileTotalSize();
                    break;
            }
        }
    };

    private void setFileTotalSize() {
        //将总的大小转换成合适的单位格式
        String fileSizeStr = Formatter.formatFileSize(CCleanerActivity.this, totalSize);
        String numStr = fileSizeStr.substring(0, fileSizeStr.length() - 2);
        String unitStr = fileSizeStr.substring(fileSizeStr.length() - 2);
        //设置文件大小
        tv_ccleaner_file_size.setText(numStr);
        //设置文件大小的单位
        tv_ccleaner_file_unit.setText(unitStr);
        cCleanerAdapter.notifyDataSetChanged();
    }

    @Override
    public void initData() {
        initArrayListData();

        cCleanerAdapter = new CCleanerAdapter(CCleanerActivity.this, parentInfoList, allChildInfoList);
        elv_ccleaner.setAdapter(cCleanerAdapter);

        //设置父项的标题
        setParentTitle();
        new AppManagerThread().start();
        new FileThread().start();
        setTimerTask();

        IntentFilter filter = new IntentFilter();
        filter.addAction(Config.CHANGE_SELECTION_BROADCAST);
        cCleanerBroadCast = new CCleanerBroadCast();
        registerReceiver(cCleanerBroadCast,filter);
    }

    /** 设置时间任务 */
    private void setTimerTask() {
        Timer timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                String tempText = tv_cleaner_file_name.getText().toString();
                //如果当前扫描的文件名和临时保存的文件名不同，则说明正在扫描
                if (!tempText.equals(fileNameText)) {
                    fileNameText = tempText;
                } else {  //否则扫描完成，发送通知
                    Message msg = handler.obtainMessage();
                    msg.what = Config.SUCCESS_LOAD;
                    handler.sendMessage(msg);
                }
            }
        };
        //3秒钟执行一次任务
        timer.schedule(timerTask, 0, 10000);
    }

    /** 初始化集合数据 */
    private void initArrayListData() {
        parentInfoList = new ArrayList<>();
        allChildInfoList = new ArrayList<>();
        cacheList = new ArrayList<>();
        apkList = new ArrayList<>();
        bigFileList = new ArrayList<>();
        logList = new ArrayList<>();
        noneDirList = new ArrayList<>();

        allChildInfoList.add(cacheList);
        allChildInfoList.add(apkList);
        allChildInfoList.add(bigFileList);
        allChildInfoList.add(logList);
        allChildInfoList.add(noneDirList);
    }

    /**
     * 设置父项的标题
     */
    private void setParentTitle() {
        String[] title = new String[]{"缓存清理", "安装包清理", "大文件清理", "日志清理", "空文件夹清理"};

        for (int i = 0; i < title.length; i++) {
            CCleanerParentInfo parentInfo = new CCleanerParentInfo(title[i], true, 0);
            parentInfoList.add(parentInfo);
        }
    }

    @Override
    public void initListener() {
        btn_ccleaner_clear.setOnClickListener(onClickListener);
    }

    @Override
    public int getLayoutResourceId() {
        return R.layout.activity_ccleaner;
    }


    private class FileThread extends Thread {
        @Override
        public void run() {
            File rootFile = Environment.getExternalStorageDirectory();
            findAllFile(rootFile);
        }
    }

    private void findAllFile(File file) {
        CCleanerChildInfo childInfo = new CCleanerChildInfo(null, file.getName(), file.getAbsolutePath(), true,
                file.length(), Config.AN_ZHUANG_BAO_QING_LI);
        if (file.isFile()) {  //如果该File为文件
            int type = 0;
            if (isCleanType(file)) {
                if (file.getName().endsWith(".apk")) {    //如果apk文件
                    apkList.add(childInfo);
                    type = Config.AN_ZHUANG_BAO_QING_LI;
                } else if (file.getName().endsWith(".log") ||
                        file.getName().endsWith(".qlog")) {  //如果日志文件
                    type = Config.RI_ZHI_QING_LI;
                    childInfo.setType(Config.RI_ZHI_QING_LI);
                    logList.add(childInfo);
                } else if (file.length() >= Config.BIG_FILE_SIZE) {    //如果文件大于50M
                    type = Config.DA_WEN_JIAN_QING_LI;
                    childInfo.setType(Config.DA_WEN_JIAN_QING_LI);
                    bigFileList.add(childInfo);
                }
                CCleanerParentInfo parentInfo = parentInfoList.get(type);
                parentInfo.setValue(parentInfo.getValue() + file.length());
                sendUpdateMsg(childInfo);
            }

        } else {  //如果是文件夹
            if (file.listFiles() == null || file.listFiles().length == 0) {   //如果是空文件夹
                childInfo.setType(Config.KONG_WEN_JIAN_JIA_QING_LI);
                noneDirList.add(childInfo);
                CCleanerParentInfo parentInfo = parentInfoList.get(Config.KONG_WEN_JIAN_JIA_QING_LI);
                parentInfo.setValue(parentInfo.getValue() + file.length());
                sendUpdateMsg(childInfo);

            } else {  //如果还有子文件夹
                for (final File f : file.listFiles()) {

                    threadPool.execute(new Runnable() {
                        @Override
                        public void run() {
                            findAllFile(f);
                        }
                    });
                }
            }
        }
    }

    /**
     * 发送更新通知，改变顶部显示
     */
    private void sendUpdateMsg(CCleanerChildInfo childInfo) {
        Message msg = handler.obtainMessage();
        Bundle data = new Bundle();
        //文件名
        data.putString(FILE_NAME, childInfo.getAppNameOrfileName());
        //该文件大小
        data.putLong(FILE_SIZE, childInfo.getValue());
        msg.setData(data);
        //消息类型
        msg.what = Config.UPDATE_PROGRESS;
        handler.sendMessage(msg);
    }

    /**
     * 判断文件是否为清理类型
     */
    private boolean isCleanType(File file) {
        boolean b = false;

        if (file.getName().endsWith(".apk")
                || file.getName().endsWith(".log")
                || file.getName().endsWith(".qlog")
                || file.length() >= Config.BIG_FILE_SIZE) {
            b = true;
        }


        return b;
    }


    /**
     * 在线程中获得app的信息
     */
    private class AppManagerThread extends Thread {
        @Override
        public void run() {

            //得到包管理器
            PackageManager pm = getPackageManager();

            //得到所有的应用程序
            List<ApplicationInfo> applications = pm.getInstalledApplications(PackageManager.GET_META_DATA |
                    PackageManager.GET_SHARED_LIBRARY_FILES);

            for (ApplicationInfo info : applications) {
                //应用图标
                Drawable appIcon = info.loadIcon(pm);
                //应用名称
                String appName = info.loadLabel(pm).toString();
                //应用包名
                String packageName = info.packageName;

                CCleanerChildInfo childInfo = new CCleanerChildInfo(appIcon, appName,
                        packageName, true, 0l, Config.HUAN_CUN_QING_LI);

                try {
                    queryPkgCacheSize(childInfo);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 取得指定包名的程序的缓存大小
     */
    private void queryPkgCacheSize(CCleanerChildInfo childInfo) throws Exception {
        if (!TextUtils.isEmpty(childInfo.getPackageNameOrFilePath())) {// pkgName不能为空
            // 使用放射机制得到PackageManager类的隐藏函数getPackageSizeInfo
            if (mPackageManager == null) {
                mPackageManager = getBaseContext().getPackageManager();// 得到被反射调用函数所在的类对象
            }
            try {
                String methodName = "getPackageSizeInfo";// 想通过反射机制调用的方法名
                Class<?> parameterType1 = String.class;// 被反射的方法的第一个参数的类型
                Class<?> parameterType2 = IPackageStatsObserver.class;// 被反射的方法的第二个参数的类型
                Method getPackageSizeInfo = mPackageManager.getClass().getMethod(
                        methodName, parameterType1, parameterType2);
                getPackageSizeInfo.invoke(mPackageManager,
                        childInfo.getPackageNameOrFilePath(), new MyIPStub(childInfo));// 方法使用的参数

            } catch (Exception ex) {
                ex.printStackTrace();
                throw ex; // 抛出异常
            }
        }
    }

    /**
     * 自定义 IPackageStatsObserver.Stub 类
     */
    private class MyIPStub extends IPackageStatsObserver.Stub {

        private CCleanerChildInfo childInfo = null;

        public MyIPStub(CCleanerChildInfo childInfo) {
            this.childInfo = childInfo;
        }

        @Override
        public void onGetStatsCompleted(PackageStats pStats, boolean succeeded) throws RemoteException {
            if (pStats.cacheSize >= 1024) {
                childInfo.setValue(pStats.cacheSize);
                cacheList.add(childInfo);

                CCleanerParentInfo parentInfo = parentInfoList.get(Config.HUAN_CUN_QING_LI);
                parentInfo.setValue(parentInfo.getValue() + childInfo.getValue());

                //发送更新消息
                sendUpdateMsg(childInfo);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //注销广播
        unregisterReceiver(cCleanerBroadCast);
    }

    /** 广播，判断选项是否被选中 */
    public class CCleanerBroadCast extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {

            //得到传递的数据
            long size = intent.getLongExtra(Config.TOTAL_SIZE_DATA, 0);
            sendClearDataMsg(size);
        }
    }

    /**
     * 发送清理数据消息
     * @param size 清理的数据大小
     */
    private void sendClearDataMsg(long size) {
        //封装
        Bundle bundle = new Bundle();
        bundle.putLong(Config.TOTAL_SIZE_DATA,size);

        //发送消息
        Message msg = handler.obtainMessage();
        msg.what = Config.CHANGE_SELECTED_OR_CLEAR;
        msg.setData(bundle);
        handler.sendMessage(msg);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.btn_ccleaner_clear){
                cleanAppCache();
                clearApkData();
                clearBigFileData();
                clearLogData();
                clearNoneDirData();
            }
        }
    };

    /**
     * 清理缓存
     */
    private void cleanAppCache() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                CCleanerParentInfo parentInfo = parentInfoList.get(Config.HUAN_CUN_QING_LI);
                for (int i=0; i<cacheList.size(); i++) {
                    CCleanerChildInfo childInfo = cacheList.get(i);
                    if (childInfo.isSelected()) {
                        try {
                            parentInfo.setValue(parentInfo.getValue() - childInfo.getValue());

                            //需要root权限，直接利用linux命令删除文件夹或文件
                            String command1 = "rm -r /" + childInfo.getPackageNameOrFilePath() + "/cache";
                            String command2 = "rm -r /" + childInfo.getPackageNameOrFilePath() + "/files";
                            String command3 = "rm -r /" + childInfo.getPackageNameOrFilePath() + "/database/webview.db";
                            String command4 = "rm -r /" + childInfo.getPackageNameOrFilePath() + "/database/webviewCache.db";

                            Runtime.getRuntime().exec(command1);
                            Runtime.getRuntime().exec(command2);
                            Runtime.getRuntime().exec(command3);
                            Runtime.getRuntime().exec(command4);

                            sendClearDataMsg(-childInfo.getValue());

                        } catch (Exception ex) {
                            System.err.println("IOException " + ex.getMessage());
                        }
                    }
                }

            }
        }).start();
    }

    /** 清理选中的apk文件 */
    private void clearApkData() {
        new Thread(){
            @Override
            public void run() {
                CCleanerParentInfo parentInfo = parentInfoList.get(Config.AN_ZHUANG_BAO_QING_LI);
                for(int i=0; i<apkList.size(); i++){
                    CCleanerChildInfo childInfo = apkList.get(i);
                    if(childInfo.isSelected()){
                        parentInfo.setValue(parentInfo.getValue() - childInfo.getValue());

                        File file = new File(childInfo.getPackageNameOrFilePath());
                        file.delete();
                        apkList.remove(childInfo);
                        i --;

                        //发送消息
                        sendClearDataMsg(-childInfo.getValue());
                    }
                }
            }
        }.start();
    }

    /** 清理选中大文件 */
    private void clearBigFileData() {
        new Thread(){
            @Override
            public void run() {
                CCleanerParentInfo parentInfo = parentInfoList.get(Config.DA_WEN_JIAN_QING_LI);
                for(int i=0; i<bigFileList.size(); i++){
                    CCleanerChildInfo childInfo = bigFileList.get(i);
                    if(childInfo.isSelected()){
                        parentInfo.setValue(parentInfo.getValue() - childInfo.getValue());
                        File file = new File(childInfo.getPackageNameOrFilePath());
                        file.delete();
                        apkList.remove(childInfo);
                        i --;

                        //发送消息
                        sendClearDataMsg(-childInfo.getValue());
                    }
                }
            }
        }.start();
    }

    /** 清理选中的日志文件 */
    private void clearLogData() {
        new Thread(){
            @Override
            public void run() {
                CCleanerParentInfo parentInfo = parentInfoList.get(Config.RI_ZHI_QING_LI);
                for(int i=0; i<logList.size(); i++){
                    CCleanerChildInfo childInfo = logList.get(i);
                    if(childInfo.isSelected()){
                        parentInfo.setValue(parentInfo.getValue() - childInfo.getValue());
                        File file = new File(childInfo.getPackageNameOrFilePath());
                        file.delete();
                        apkList.remove(childInfo);
                        i --;

                        //发送消息
                        sendClearDataMsg(-childInfo.getValue());
                    }
                }
            }
        }.start();
    }

    /** 清理选中的空文件夹 */
    private void clearNoneDirData() {
        new Thread(){
            @Override
            public void run() {
                CCleanerParentInfo parentInfo = parentInfoList.get(Config.KONG_WEN_JIAN_JIA_QING_LI);
                for(int i=0; i<noneDirList.size(); i++){
                    CCleanerChildInfo childInfo = noneDirList.get(i);
                    if(childInfo.isSelected()){
                        parentInfo.setValue(parentInfo.getValue() - childInfo.getValue());
                        File file = new File(childInfo.getPackageNameOrFilePath());
                        if(file!=null){
                            file.delete();
                        }

                        //发送消息
                        sendClearDataMsg(-childInfo.getValue());

                        apkList.remove(childInfo);
                        i --;
                    }
                }
            }
        }.start();
    }
}