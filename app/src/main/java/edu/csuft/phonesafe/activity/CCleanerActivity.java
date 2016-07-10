package edu.csuft.phonesafe.activity;

import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ExpandableListView;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import edu.csuft.phonesafe.R;
import edu.csuft.phonesafe.adapter.CCleanerAdapter;
import edu.csuft.phonesafe.base.BaseActivity;
import edu.csuft.phonesafe.bean.CCleanerChildInfo;
import edu.csuft.phonesafe.bean.CCleanerParentInfo;
import edu.csuft.phonesafe.utils.Config;

/**
 * 垃圾清理界面
 */
public class CCleanerActivity extends BaseActivity {

    @Bind(R.id.elv_ccleaner)
    ExpandableListView elv_ccleaner;

    /**
     * 父项集合
     */
    private ArrayList<CCleanerParentInfo> parentInfoList = null;
    /**
     * 所有的子项集合，即字项集合的集合
     */
    private ArrayList<ArrayList<CCleanerChildInfo>> allChildInfoList = null;
    //
    ArrayList<CCleanerChildInfo> childInfoList = null;

    private PackageManager mPackageManager = null;
    private CCleanerAdapter cCleanerAdapter = null;

    private long totalCacheSize = 0;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Config.SUCCESS_LOAD:
                    cCleanerAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };

    @Override
    public void initData() {
        parentInfoList = new ArrayList<>();
        allChildInfoList = new ArrayList<>();
        childInfoList = new ArrayList<>();
        allChildInfoList.add(childInfoList);

        cCleanerAdapter = new CCleanerAdapter(CCleanerActivity.this, parentInfoList, allChildInfoList);
        elv_ccleaner.setAdapter(cCleanerAdapter);

        String []title = new String[]{"缓存清理","apk清理","大文件清理","日志清理"};

        for (int i = 0; i < 5; i++) {
            CCleanerParentInfo parentInfo = new CCleanerParentInfo(title[i], true, 0);
            parentInfoList.add(parentInfo);
        }

        new AppManagerThread().start();
    }

    @Override
    public void initListener() {

    }

    @Override
    public int getLayoutResourceId() {
        return R.layout.activity_ccleaner;
    }

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
                        packageName, true, 0l);

                try {
                    queryPkgCacheSize(childInfo);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            //发送消息，应用加载完毕
            Message msg = handler.obtainMessage();
            msg.what = Config.SUCCESS_LOAD;
            handler.sendMessage(msg);
        }
    }

    /**
     * 取得指定包名的程序的缓存大小
     */
    private void queryPkgCacheSize(CCleanerChildInfo childInfo) throws Exception {
        if (!TextUtils.isEmpty(childInfo.getPackageName())) {// pkgName不能为空
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
                        childInfo.getPackageName(), new MyIPStub(childInfo));// 方法使用的参数

            } catch (Exception ex) {
                ex.printStackTrace();
                throw ex; // 抛出异常
            }
        }
    }

    private class MyIPStub extends IPackageStatsObserver.Stub {

        private CCleanerChildInfo childInfo = null;

        public MyIPStub(CCleanerChildInfo childInfo) {
            this.childInfo = childInfo;
        }

        @Override
        public void onGetStatsCompleted(PackageStats pStats, boolean succeeded) throws RemoteException {
            if (pStats.cacheSize >= 1024) {
                childInfo.setValue(pStats.cacheSize+pStats.dataSize);
                childInfoList.add(childInfo);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_ccleaner, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_ccleaner_clear) {
            cleanAppCache();
        }
        return true;
    }

    private void cleanAppCache() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for(CCleanerChildInfo childInfo : childInfoList){
                    if(childInfo.isSelected()){
                        try {
                            String command1 = "rm -r /" + childInfo.getPackageName() + "/cache";
                            String command2 = "rm -r /" + childInfo.getPackageName() + "/files";
                            String command3 = "rm -r /" + childInfo.getPackageName() + "/database/webview.db";
                            String command4 = "rm -r /" + childInfo.getPackageName() + "/database/webviewCache.db";


                            Runtime.getRuntime().exec(command1);
                            Runtime.getRuntime().exec(command2);
                            Runtime.getRuntime().exec(command3);
                            Runtime.getRuntime().exec(command4);

                        } catch (Exception ex) {
                            System.err.println("IOException " + ex.getMessage());
                        }
                    }
                }

            }
        }).start();
    }
}