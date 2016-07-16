package edu.csuft.phonesafe.activity;

import android.Manifest;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.TrafficStats;
import android.os.Handler;
import android.os.Message;
import android.text.format.Formatter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import edu.csuft.phonesafe.R;
import edu.csuft.phonesafe.adapter.TrafficAdapter;
import edu.csuft.phonesafe.base.BaseActivity;
import edu.csuft.phonesafe.bean.TrafficInfo;
import edu.csuft.phonesafe.helper.ViewHelper;
import edu.csuft.phonesafe.utils.Config;

/**
 * 流量查看界面，查看该次开机的每个app消耗的流量值
 */
public class TrafficActivity extends BaseActivity {

    @Bind(R.id.tv_traffic_total)
    TextView tv_traffic_total;
    @Bind(R.id.lv_traffic)
    ListView lv_traffic;

    private ArrayList<TrafficInfo> trafficInfoList = null;
    //ListView的适配器
    private TrafficAdapter trafficAdapter = null;

    //ViewHelper，工具类，在加载数据前，显示进度条
    private ViewHelper viewHelper = null;
    /** 处理子线程中发送的数据，主要用来跟新主线程的ui */
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
           switch (msg.what){
               case Config.SUCCESS_LOAD:    //加载完成
                   //排序
                   sortByTrafficValue();
                   trafficAdapter = new TrafficAdapter(TrafficActivity.this,trafficInfoList);
                   lv_traffic.setAdapter(trafficAdapter);

                   //隐藏进度条
                   viewHelper.hiddenPbAndTv();
                   break;
               case Config.UPDATE_PROGRESS: //更新
                   //更新进度条
                   viewHelper.updateProgressBarValue();
                   //更新进度条上方显示的文字
                   viewHelper.updateTextViewValue();
                   break;
           }
        }
    };

    /** 按应用消耗的流量值排序 */
    private void sortByTrafficValue() {
        //排序
        Collections.sort(trafficInfoList);
    }

    /** c初始化数据 */
    @Override
    public void initData() {
        viewHelper = new ViewHelper(this);
        trafficInfoList = new ArrayList<>();
        tv_traffic_total.setText("总流量:"+getTotalTrafficValue());

        //开启子线程
        new TrafficInternetThread().start();
    }

    /** 初始化监听器 */
    @Override
    public void initListener() {

    }

    /** 返回布局id */
    @Override
    public int getLayoutResourceId() {
        return R.layout.activity_traffic;
    }

    /** 得到所消耗的所有流量值 */
    private String getTotalTrafficValue(){
        //得到总流量数据
        //总数据流量由总的上传流量加上总的下载流量组成
        long totalTrafficValue = TrafficStats.getTotalRxBytes() + TrafficStats.getTotalTxBytes();
        //转换格式
        String strValue = Formatter.formatFileSize(this, totalTrafficValue);

        return strValue;
    }

    /** 在子线程中查找具有联网权限的app */
    private class TrafficInternetThread extends Thread{
        @Override
        public void run() {
            PackageManager pm = getPackageManager();
            List<PackageInfo> packageInfos = pm.getInstalledPackages(PackageManager.GET_PERMISSIONS);
            //设置最大值
            viewHelper.setPbMaxValue(packageInfos.size());
            for(PackageInfo packageInfo : packageInfos){
                //得到应用的权限
                String[] permissions = packageInfo.requestedPermissions;
                //判断该应用是否有权限
                if(permissions != null && permissions.length != 0){
                    for(String permission : permissions){

                        //是否具有联网权限
                        if(permission.equals(Manifest.permission.INTERNET)){
                            ApplicationInfo applicationInfo = packageInfo.applicationInfo;
                            //图标
                            Drawable appIcon = applicationInfo.loadIcon(pm);
                            //应用名称
                            String appName = applicationInfo.loadLabel(pm).toString();
                            //应用唯一标识
                            int uid = applicationInfo.uid;
                            //包名
                            String packageName = applicationInfo.packageName;
                            //根据uid得到该应用消耗的流量值
                            long trafficValue = TrafficStats.getUidRxBytes(uid)
                                    + TrafficStats.getUidTxBytes(uid);

                            TrafficInfo trafficInfo = new TrafficInfo(appIcon,uid,packageName,appName,trafficValue);
                            trafficInfoList.add(trafficInfo);

                            break;
                        }
                    }
                }
                //更新数据
                Message msg = handler.obtainMessage();
                msg.what = Config.UPDATE_PROGRESS;
                handler.sendMessage(msg);
            }

            //加载完成
            Message msg = handler.obtainMessage();
            msg.what = Config.SUCCESS_LOAD;
            handler.sendMessage(msg);
        }
    }
}
