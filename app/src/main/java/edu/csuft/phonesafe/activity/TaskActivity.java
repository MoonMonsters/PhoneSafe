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

/**
 * 任务管理界面
 */
public class TaskActivity extends BaseActivity {

    @Bind(R.id.lv_task)
    ListView lv_task;

    /**
     * 包管理器
     */
    private PackageManager pm = null;
    /**
     * Activity管理器
     */
    private ActivityManager am = null;
    /**
     * 现在运行在内存中的应用
     */
    List<RunningAppProcessInfo> runningAppProcesses = null;
    /**
     * 任务集合
     */
    private ArrayList<TaskInfo> taskInfoList = null;
    /**
     * 适配器
     */
    private TaskAdapter taskAdapter = null;

    /**
     * 处理ui
     */
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                //加载完成
                case Config.SUCCESS_LOAD:
                    //创建适配器对象
                    taskAdapter = new TaskAdapter(TaskActivity.this, taskInfoList);
                    lv_task.setAdapter(taskAdapter);
                    break;
            }
        }
    };

    /**
     * 初始化数据
     */
    @Override
    public void initData() {

        //获得包管理器
        pm = getPackageManager();
        //获得Activity管理器
        am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        //得到运行中的程序
        runningAppProcesses = am.getRunningAppProcesses();
        taskInfoList = new ArrayList<>();

        new TaskThread().start();
    }

    /**
     * 初始化监听器
     */
    @Override
    public void initListener() {
        lv_task.setOnItemClickListener(onItemClickListener);
    }

    /**
     * 返回布局id
     */
    @Override
    public int getLayoutResourceId() {
        return R.layout.activity_task;
    }

    /**
     * 创建菜单
     *
     * @param menu 菜单对象
     * @return 是否处理完成，采用默认值即可
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        //创建菜单
        getMenuInflater().inflate(R.menu.menu_task, menu);

        return super.onCreateOptionsMenu(menu);
    }

    /**
     * 菜单的点击事件
     *
     * @param item 菜单项
     * @return 是否处理完成，true表示完成
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_task_all) {   //全选
            selectAll();
        } else if (item.getItemId() == R.id.action_task_cancel) { //全部取消
            selectNone();
        } else if (item.getItemId() == R.id.action_task_clear) {   //一键清理
            killSelectedProgress();
        }

        return true;
    }

    /**
     * 杀死选中的进程
     */
    private void killSelectedProgress() {

        for (int i = 0; i < taskInfoList.size(); i++) {
            //从集合中得到对象
            TaskInfo taskInfo = taskInfoList.get(i);
            if (taskInfo.isSelected()) {
                //杀死后台进程，传入的参数是包名
                am.killBackgroundProcesses(taskInfo.getPackageName());
                //被杀死后，从列表中移除掉
                taskInfoList.remove(taskInfo);
                i--;
            }
        }

        //更新数据
        taskAdapter.notifyDataSetChanged();
    }

    /**
     * 全部取消
     */
    private void selectNone() {
        for (TaskInfo taskInfo : taskInfoList) {
            taskInfo.setSelected(false);
        }
        //刷新界面
        taskAdapter.notifyDataSetChanged();
    }

    /**
     * 全部选中
     */
    private void selectAll() {
        for (TaskInfo taskInfo : taskInfoList) {
            taskInfo.setSelected(true);
        }
        //刷新界面
        taskAdapter.notifyDataSetChanged();
    }

    /**
     * ListView的点击事件，单独设置子项是否被选中需要清理
     */
    AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            boolean isSelected = taskInfoList.get(position).isSelected();
            taskInfoList.get(position).setSelected(!isSelected);
            taskAdapter.notifyDataSetChanged();
        }
    };

    /**
     * 在子线程中读取内存中的启动的应用
     */
    private class TaskThread extends Thread {
        @Override
        public void run() {
            //在扫描之前，清空数据
            taskInfoList.clear();

            //遍历内存中运行的程序进程
            for (RunningAppProcessInfo processInfo : runningAppProcesses) {
                //pid，进程的唯一标识
                int pid = processInfo.pid;
                //包名
                String packageName = processInfo.processName;
                //图标
                Drawable appIcon = null;
                //应用名称
                String appName = null;
                try {
                    //得到PackageInfo对象
                    PackageInfo packageInfo = pm.getPackageInfo(packageName, 0);
                    //从PackageInfo对象中得到ApplicationInfo对象
                    ApplicationInfo applicationInfo = packageInfo.applicationInfo;
                    //应用图标
                    appIcon = applicationInfo.loadIcon(pm);
                    //应用名称
                    appName = applicationInfo.loadLabel(pm).toString();
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }

                //根据进程pid得到MemoryInfo集合
                Debug.MemoryInfo[] memoryInfos = am.getProcessMemoryInfo(new int[]{pid});
                Debug.MemoryInfo memoryInfo = memoryInfos[0];
                //单位为kb
                int totalPrivateDirty = memoryInfo.getTotalPrivateDirty();

                //创建TaskInfo对象
                TaskInfo taskInfo = new TaskInfo(appIcon, appName, packageName, pid, totalPrivateDirty * 1024);
                taskInfoList.add(taskInfo);
            }

            //遍历完成，发送消息
            Message msg = handler.obtainMessage();
            msg.what = Config.SUCCESS_LOAD;
            handler.sendMessage(msg);
        }
    }
}
