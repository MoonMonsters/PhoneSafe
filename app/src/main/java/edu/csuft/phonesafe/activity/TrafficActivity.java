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
 * 流量查看，查看该次开机的每个app消耗的流量值
 */
public class TrafficActivity extends BaseActivity {

    @Bind(R.id.tv_traffic_total)
    TextView tv_traffic_total;
    @Bind(R.id.lv_traffic)
    ListView lv_traffic;

    private ArrayList<TrafficInfo> trafficInfoList = null;
    private TrafficAdapter trafficAdapter = null;

    private ViewHelper viewHelper = null;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
           switch (msg.what){
               case Config.SUCCESS_LOAD:
                   sortByTrafficValue();

                   trafficAdapter = new TrafficAdapter(TrafficActivity.this,trafficInfoList);
                   lv_traffic.setAdapter(trafficAdapter);

                   viewHelper.hiddenPbAndTv();
                   break;
               case Config.UPDATE_PROGRESS:
                   viewHelper.updateProgressBarValue();
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

    @Override
    public void initData() {
        viewHelper = new ViewHelper(this);
        trafficInfoList = new ArrayList<>();
        tv_traffic_total.setText("总流量:"+getTotalTrafficValue());

        new TrafficInternetThread().start();
    }

    @Override
    public void initListener() {

    }

    @Override
    public int getLayoutResourceId() {
        return R.layout.activity_traffic;
    }

    /** 得到所消耗的所有流量值 */
    private String getTotalTrafficValue(){
        long totalTrafficValue = TrafficStats.getTotalRxBytes() + TrafficStats.getTotalTxBytes();

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
                String[] permissions = packageInfo.requestedPermissions;
                //判断该应用是否有权限
                if(permissions != null && permissions.length != 0){
                    for(String permission : permissions){
                        //是否具有联网权限
                        if(permission.equals(Manifest.permission.INTERNET)){
                            ApplicationInfo applicationInfo = packageInfo.applicationInfo;
                            Drawable appIcon = applicationInfo.loadIcon(pm);
                            String appName = applicationInfo.loadLabel(pm).toString();
                            int uid = applicationInfo.uid;
                            String packageName = applicationInfo.packageName;
                            long trafficValue = TrafficStats.getUidRxBytes(uid)
                                    + TrafficStats.getUidTxBytes(uid);

                            TrafficInfo trafficInfo = new TrafficInfo(appIcon,uid,packageName,appName,trafficValue);
                            trafficInfoList.add(trafficInfo);

                            break;
                        }
                    }
                }
                Message msg = handler.obtainMessage();
                msg.what = Config.UPDATE_PROGRESS;
                handler.sendMessage(msg);
            }

            Message msg = handler.obtainMessage();
            msg.what = Config.SUCCESS_LOAD;
            handler.sendMessage(msg);
        }
    }
}
