package edu.csuft.phonesafe.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import edu.csuft.phonesafe.R;
import edu.csuft.phonesafe.adapter.AppManagerAdapter;
import edu.csuft.phonesafe.base.BaseActivity;
import edu.csuft.phonesafe.bean.AppManagerInfo;

/**
 * 软件管理界面
 */
public class AppManagerActivity extends BaseActivity {

    @Bind(R.id.lv_app_manager)
    ListView lv_app_manager;
    @Bind(R.id.pb_app_manager)
    ProgressBar pb_app_manager;
    @Bind(R.id.tv_app_manager_progress)
    TextView tv_app_manager_progress;

    private PopupWindow popupWindow;

    /** 存储系统应用程序集合 */
    private ArrayList<AppManagerInfo> systemAppList = null;
    /** 存储用户应用程序集合 */
    private ArrayList<AppManagerInfo> userAppList = null;
    /** 应用程序管理适配器 */
    private AppManagerAdapter appManagerAdapter = null;

    /** 加载完成 */
    public static final int SUCCESS_LOAD = 0;
    /** 进度条的值 */
    public static final int PROGRESS_VALUE = 1;

    /** 判断当前显示的是系统应用程序还是用户应用程序 */
    private boolean isUserAppList = true;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case SUCCESS_LOAD:
                    sortByAppName();
                    showAppInfo();
                    break;
                case PROGRESS_VALUE:
                    String value = (String) msg.obj;
                    tv_app_manager_progress.setText(value);
                    break;
            }
        }
    };

    /** 按应用程序的首字母进行排序 */
    private void sortByAppName() {
        Collections.sort(systemAppList);
        Collections.sort(userAppList);
    }

    @Override
    public void initData() {
        systemAppList = new ArrayList<>();
        userAppList = new ArrayList<>();
        new AppManagerThread().start();
    }

    @Override
    public void initListener() {
        lv_app_manager.setOnItemClickListener(onItemClickListener);
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
            pb_app_manager.setMax(applications.size());
            //从0开始
            pb_app_manager.setProgress(0);
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
                pb_app_manager.setProgress(pb_app_manager.getProgress() + 1);
                Message msg = handler.obtainMessage();
                msg.what = PROGRESS_VALUE;
                msg.obj = setProgressValue();
                handler.sendMessage(msg);
            }

            //发送消息，应用加载完毕
            Message msg = handler.obtainMessage();
            msg.what = SUCCESS_LOAD;
            handler.sendMessage(msg);
        }
    }

    /** 设置TextView上显示的Progress进度条的值 */
    private String setProgressValue(){

        return pb_app_manager.getProgress() + "/" + pb_app_manager.getMax();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_app_manager,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        showAppInfo();

        return true;
    }

    /** 显示应用程序在界面上 */
    private void showAppInfo() {

        hiddenPbAndTv();

        if(!isUserAppList){
            appManagerAdapter = new AppManagerAdapter(this,systemAppList);
            Toast.makeText(this,"现在显示的是系统应用程序",Toast.LENGTH_SHORT).show();
        }else{
            appManagerAdapter = new AppManagerAdapter(this,userAppList);
            Toast.makeText(this,"现在显示的是用户应用程序",Toast.LENGTH_SHORT).show();
        }

        lv_app_manager.setAdapter(appManagerAdapter);

        isUserAppList = !isUserAppList;
    }

    /** 在加载完成后，隐藏进度条和文字提示 */
    private void hiddenPbAndTv() {
        pb_app_manager.setVisibility(View.GONE);
        tv_app_manager_progress.setVisibility(View.GONE);
    }

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

    /** ListView的点击事件 */
    AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            View v = LayoutInflater.from(AppManagerActivity.this).inflate(R.layout.item_app_manager_dialog,null,false);
            popupWindow = new PopupWindow(v, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
            popupWindow.setTouchable(true);
            int[] arrayOfInt = new int[2];
            view.getLocationInWindow(arrayOfInt);
            int x = arrayOfInt[0] + 100;
            int y = arrayOfInt[1];

            popupWindow.showAtLocation(view, Gravity.LEFT|Gravity.TOP,x,y);
            initPopupWindow(position,v);
        }
    };

    /**
     * 初始化PopupWindow上的TextView，并且添加点击事件
     */
    private void initPopupWindow(int position, View v) {
        PopupOnClickListener onClickListener = new PopupOnClickListener(position);
        Button btn_app_manager_popup_startup = (Button) v.findViewById(R.id.btn_app_manager_popup_startup);
        Button btn_app_manager_popup_uninstall = (Button) v.findViewById(R.id.btn_app_manager_popup_uninstall);
        Button btn_app_manager_popup_share = (Button) v.findViewById(R.id.btn_app_manager_popup_share);

        btn_app_manager_popup_startup.setOnClickListener(onClickListener);
        btn_app_manager_popup_uninstall.setOnClickListener(onClickListener);
        btn_app_manager_popup_share.setOnClickListener(onClickListener);
    }

    private class PopupOnClickListener implements View.OnClickListener{

        private int position;

        public PopupOnClickListener(int position){
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            AppManagerInfo info = null;
            if(!isUserAppList){
                info = userAppList.get(position);
            }else{
                info = systemAppList.get(position);
            }

            PackageManager pm = getPackageManager();
            //包名
            String packageName = info.getPackageName();

            if(v.getId() == R.id.btn_app_manager_popup_startup){    //启动程序
                try {
                    PackageInfo packageInfo = pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
                    //得到该程序下的所有Activity
                    ActivityInfo[] activities = packageInfo.activities;
                    //如果为空或者长度为0，则表示该程序没有界面，不能被启动
                    if(activities == null || activities.length == 0){
                        Toast.makeText(AppManagerActivity.this,"该应用不能被启动",Toast.LENGTH_SHORT).show();

                        return ;
                    }
                    //得到主界面
                    ActivityInfo activityInfo = activities[0];
                    //包名
                    String name = activityInfo.name;
                    ComponentName componentName = new ComponentName(packageName,name);
                    Intent startup_intent = new Intent();
                    startup_intent.setComponent(componentName);
                    //启动
                    startActivity(startup_intent);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }else if(v.getId() == R.id.btn_app_manager_popup_uninstall){    //卸载程序
                if(!isUserAppList){
                    if(info.getPackageName().equals(getPackageName())){ //如果是本应用，也不能卸载
                        Toast.makeText(AppManagerActivity.this,"系统应用，不能卸载本应用",Toast.LENGTH_SHORT).show();
                    }else{
                        Intent intent_uninstall = new Intent();
                        intent_uninstall.setAction(Intent.ACTION_DELETE);
                        //包名
                        intent_uninstall.setData(Uri.parse("package:"+packageName));
                        startActivity(intent_uninstall);

                        //从列表中移除掉
                        userAppList.remove(info);
                        appManagerAdapter.notifyDataSetChanged();
                    }
                }else{
                    Toast.makeText(AppManagerActivity.this,"系统应用，不推荐卸载",Toast.LENGTH_SHORT).show();
                }
            }else if(v.getId() == R.id.btn_app_manager_popup_share){    //分享应用名
                Intent share_intent = new Intent();
                share_intent.setAction(Intent.ACTION_SEND);
                share_intent.setType("text/plain");
                share_intent.putExtra(Intent.EXTRA_SUBJECT,"分享");
                share_intent.putExtra(Intent.EXTRA_TEXT,"Hi,向你分享一款软件: "+info.getAppName());
                share_intent = Intent.createChooser(share_intent,"分享");
                startActivity(share_intent);
            }

            //隐藏PopupWindow
            popupWindow.dismiss();

        }
    }
}
