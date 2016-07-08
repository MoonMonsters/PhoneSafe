package edu.csuft.phonesafe.activity;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Debug;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import edu.csuft.phonesafe.R;
import edu.csuft.phonesafe.adapter.TaskAdapter;
import edu.csuft.phonesafe.base.BaseActivity;
import edu.csuft.phonesafe.bean.TaskInfo;
import edu.csuft.phonesafe.utils.Config;

public class TaskActivity extends BaseActivity {

    @Bind(R.id.lv_task)
    ListView lv_task;

    /** 包管理器 */
    private PackageManager pm = null;
    /** Activity管理器 */
    private ActivityManager am = null;
    /** 现在运行在内存中的应用 */
    List<RunningAppProcessInfo> runningAppProcesses = null;
    private ArrayList<TaskInfo> taskInfoList = null;
    /** 适配器 */
    private TaskAdapter taskAdapter = null;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case Config.SUCCESS_LOAD:
                    taskAdapter = new TaskAdapter(TaskActivity.this,taskInfoList);
                    lv_task.setAdapter(taskAdapter);
                    break;
            }
        }
    };

    @Override
    public void initData() {
        pm = getPackageManager();
        am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        runningAppProcesses = am.getRunningAppProcesses();
        taskInfoList = new ArrayList<>();

        new TaskThread().start();
    }

    @Override
    public void initListener() {
        lv_task.setOnItemClickListener(onItemClickListener);
    }

    @Override
    public int getLayoutResourceId() {
        return R.layout.activity_task;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_task,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.action_task_all){
            selectAll();
        }else if(item.getItemId() == R.id.action_task_cancel){
            selectNone();
        }else if(item.getItemId() == R.id.action_task_clear){
            killSelectedProgress();
        }

        return true;
    }

    /** 杀死选中的进程 */
    private void killSelectedProgress() {

        for(int i=0; i<taskInfoList.size(); i++) {
            TaskInfo taskInfo = taskInfoList.get(i);
            if(taskInfo.isSelected()){
                am.killBackgroundProcesses(taskInfo.getPackageName());
                taskInfoList.remove(taskInfo);
                i --;
            }
        }

        taskAdapter.notifyDataSetChanged();
    }

    /** 全部取消 */
    private void selectNone() {
        for(TaskInfo taskInfo : taskInfoList){
            taskInfo.setSelected(false);
        }
        taskAdapter.notifyDataSetChanged();
    }

    /** 全部选中 */
    private void selectAll() {
        for(TaskInfo taskInfo : taskInfoList){
            taskInfo.setSelected(true);
        }
        taskAdapter.notifyDataSetChanged();
    }

    AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            boolean isSelected = taskInfoList.get(position).isSelected();
            taskInfoList.get(position).setSelected(!isSelected);
            taskAdapter.notifyDataSetChanged();
        }
    };

    /** 在子线程中读取内存中的启动的应用 */
    private class TaskThread extends Thread{
        @Override
        public void run() {
            taskInfoList.clear();
            for(RunningAppProcessInfo processInfo : runningAppProcesses){
                int pid = processInfo.pid;
                String packageName = processInfo.processName;
                Drawable appIcon = null;
                String appName = null;
                try {
                    PackageInfo packageInfo = pm.getPackageInfo(packageName, 0);
                    ApplicationInfo applicationInfo = packageInfo.applicationInfo;
                    appIcon = applicationInfo.loadIcon(pm);
                    appName = applicationInfo.loadLabel(pm).toString();
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }

                Debug.MemoryInfo[] memoryInfos = am.getProcessMemoryInfo(new int[]{pid});
                Debug.MemoryInfo memoryInfo = memoryInfos[0];
                //单位为kb
                int totalPrivateDirty = memoryInfo.getTotalPrivateDirty();

                TaskInfo taskInfo = new TaskInfo(appIcon,appName,packageName,pid,totalPrivateDirty*1024);
                taskInfoList.add(taskInfo);
            }

            //遍历完成，发送消息
            Message msg = handler.obtainMessage();
            msg.what = Config.SUCCESS_LOAD;
            handler.sendMessage(msg);
        }
    }
}
