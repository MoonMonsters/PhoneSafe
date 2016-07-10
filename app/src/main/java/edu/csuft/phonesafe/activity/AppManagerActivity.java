package edu.csuft.phonesafe.activity;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import edu.csuft.phonesafe.R;
import edu.csuft.phonesafe.base.BaseActivity;
import edu.csuft.phonesafe.bean.AppManagerInfo;
import edu.csuft.phonesafe.fragment.AppManagerFragment;
import edu.csuft.phonesafe.helper.ViewHelper;
import edu.csuft.phonesafe.utils.Config;

/**
 * 软件管理界面
 */
public class AppManagerActivity extends BaseActivity {
    /** 存储系统应用程序集合 */
    private ArrayList<AppManagerInfo> systemAppList = null;
    /** 存储用户应用程序集合 */
    private ArrayList<AppManagerInfo> userAppList = null;
    /** 帮助类，用来显示加载数据时的进度条 */
    private ViewHelper viewHelper = null;
    private ArrayList<Fragment> fragmentList = null;

    @Bind(R.id.tl_app_manager)
    TabLayout tl_app_manager;
    @Bind(R.id.vp_app_manager)
    ViewPager vp_app_manager;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case Config.SUCCESS_LOAD:
                    //排序
                    sortByAppName();
                    //显示app
                    showAppInfo();
                    //隐藏进度条
                    viewHelper.hiddenPbAndTv();
                    break;
                case Config.UPDATE_PROGRESS:
                    //更新进度条
                    viewHelper.updateProgressBarValue();
                    //更新TextView的进度条的值
                    viewHelper.updateTextViewValue();
                    break;
            }
        }
    };

    private void showAppInfo(){

        fragmentList.add(AppManagerFragment.getInstance(userAppList,true));
        fragmentList.add(AppManagerFragment.getInstance(systemAppList,false));
        vp_app_manager.setAdapter(fragmentPagerAdapter);
        tl_app_manager.setupWithViewPager(vp_app_manager);
    }

    /** 按应用程序的首字母进行排序 */
    private void sortByAppName() {
        Collections.sort(systemAppList);
        Collections.sort(userAppList);
    }

    @Override
    public void initData() {
        viewHelper = new ViewHelper(this);
        systemAppList = new ArrayList<>();
        userAppList = new ArrayList<>();
        fragmentList = new ArrayList<>();
        new AppManagerThread().start();
    }

    @Override
    public void initListener() {
    }

    @Override
    public int getLayoutResourceId() {
        return R.layout.activity_app_manager;
    }

    private class AppManagerThread extends Thread{
        @Override
        public void run() {

            //得到包管理器
            PackageManager pm = getPackageManager();

            //得到所有的应用程序
            List<ApplicationInfo> applications = pm.getInstalledApplications(PackageManager.GET_META_DATA |
                    PackageManager.GET_SHARED_LIBRARY_FILES);
            // 设置最大值为所用应用程序的值
            viewHelper.setPbMaxValue(applications.size());
            for(ApplicationInfo info : applications){
                //应用图标
                Drawable appIcon = info.loadIcon(pm);
                //应用名称
                CharSequence appName = info.loadLabel(pm);
                //应用包名
                String packageName = info.packageName;

                PackageInfo packageInfo;
                String versionName = null;
                try {
                    packageInfo = pm.getPackageInfo(packageName, 0);
                    //应用版本号
                    versionName = packageInfo.versionName;
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }

                AppManagerInfo appManagerInfo = new AppManagerInfo(appIcon,(String)appName,
                        versionName,packageName);
                //判断是不是用户程序
                if(isUserApp(info)){
                    userAppList.add(appManagerInfo);
                }else{
                    systemAppList.add(appManagerInfo);
                }

                //发送消息，应用加载完成一个
                Message msg = handler.obtainMessage();
                msg.what = Config.UPDATE_PROGRESS;
                handler.sendMessage(msg);
            }

            //发送消息，应用加载完毕
            Message msg = handler.obtainMessage();
            msg.what = Config.SUCCESS_LOAD;
            handler.sendMessage(msg);
        }
    }

    FragmentPagerAdapter fragmentPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
        String[] titles = {"用户应用","系统应用"};
        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }
        @Override
        public int getCount() {
            return fragmentList.size();
        }
        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }
    };

    /** 判断应用程序是否是用户程序 */
    private boolean isUserApp(ApplicationInfo info) {
        //原来是系统应用，用户手动升级
        if ((info.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
            return true;
            //用户自己安装的应用程序
        } else if ((info.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
            return true;
        }

        return false;
    }
}
