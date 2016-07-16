package edu.csuft.phonesafe.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import edu.csuft.phonesafe.R;
import edu.csuft.phonesafe.base.BaseActivity;
import edu.csuft.phonesafe.bean.AppManagerInfo;
import edu.csuft.phonesafe.bean.PermissionInfo;
import edu.csuft.phonesafe.bean.Permissions;
import edu.csuft.phonesafe.utils.Config;

/**
 * 应用详细信息界面
 */
public class AppDetailsActivity extends BaseActivity {

    @Bind(R.id.lv_appDetails_permissions)
    ListView lv_appDetails_permissions;

    /**
     * 包裹着Map的集合，Map中存着值对
     */
    private List<Map<String, String>> permissionInfoList = null;
    /**
     * 传递过来的APPManagerInfo对象
     */
    private AppManagerInfo appManagerInfo;
    /**
     * 是否为用户应用
     */
    boolean isUserApp = true;

    @Override
    public void initData() {
        //接收传递过来的APPManagerInfo对象
        appManagerInfo = getIntent().getParcelableExtra(Config.APP_MANAGER_INFO_VALUE);
        //是否为用户程序
        isUserApp = getIntent().getBooleanExtra(Config.IS_USER_APP, false);
        //初始化对象
        permissionInfoList = new ArrayList<>();
        //初始化权限数据
        initPermissionListData();

        //构建适配器
        //参数:1.上下文对象 2.数据集合 3.布局 4.引用 5.控件id
        SimpleAdapter adapter = new SimpleAdapter(this, permissionInfoList, R.layout.item_details,
                new String[]{"Permission", "Describe"}, new int[]{R.id.tv_details_permission, R.id.tv_details_describe});
        //设置适配器
        lv_appDetails_permissions.setAdapter(adapter);
    }

    /**
     * 初始化集合数据
     */
    private void initPermissionListData() {
        //通过包名得到该应用所有的权限信息
        String[] permissionArray = getPermissionsFromPackageInfo();

        try {
            /*
            构建gson参数
             */
            InputStream inputStream = getResources().getAssets().open("permissions.json");
            //Gson对象，用来解析Json数据
            Gson gson = new Gson();
            //包装成Reader对象
            Reader reader = new BufferedReader(new InputStreamReader(inputStream));
            //解析
            Permissions permissions = gson.fromJson(reader, Permissions.class);
            //从Permissions对象中得到PermissionInfo集合
            ArrayList<PermissionInfo> permissionInfos = permissions.getPermissionInfos();
            //遍历权限，加入描述信息
            for (String p : permissionArray) {
                //遍历权限信息，得到描述信息
                for (PermissionInfo info : permissionInfos) {
                    //如果符合，则加入到集合中
                    if (info.getPermission().equals(p)) {
                        Map<String, String> map = new HashMap<>();
                        map.put("Permission", getLowcasePermission(info.getPermission()));
                        map.put("Describe", info.getDescribe());
                        permissionInfoList.add(map);

                        //因为只会有一组，所以直接跳出
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 从包名得到该应用的所有申请的权限
     *
     * @return 字符串数组，所有的权限信息
     */
    private String[] getPermissionsFromPackageInfo() {
        String[] permissions = null;
        try {
            //得到所有的权限，注意后面的参数要穿Permission相关的
            permissions = getPackageManager().getPackageInfo(appManagerInfo.getPackageName(), PackageManager.PERMISSION_DENIED)
                    .requestedPermissions;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return permissions;
    }

    /**
     * 提取权限中的主要部分，并且转成小写
     *
     * @param permissionStr 完整的权限名称
     * @return 小写的主要部分权限名称
     */
    private String getLowcasePermission(String permissionStr) {
        //以最后一个'.'为分隔符
        int index = permissionStr.lastIndexOf(".");
        //提取最主要的部分
        String str = permissionStr.substring(index + 1);
        //返回小写
        return str.toLowerCase();
    }

    @Override
    public void initListener() {

    }

    /**
     * 返回布局id
     */
    @Override
    public int getLayoutResourceId() {
        return R.layout.activity_app_details;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_app_details, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_details_startup) {
            try {
                PackageInfo packageInfo = getPackageManager().getPackageInfo(appManagerInfo.getPackageName(), PackageManager.GET_ACTIVITIES);
                //得到该程序下的所有Activity
                ActivityInfo[] activities = packageInfo.activities;
                //如果为空或者长度为0，则表示该程序没有界面，不能被启动
                if (activities == null || activities.length == 0) {
                    Toast.makeText(AppDetailsActivity.this, "该应用不能被启动", Toast.LENGTH_SHORT).show();

                    return true;
                }
                //得到主界面
                ActivityInfo activityInfo = activities[0];
                //包名
                String name = activityInfo.name;
                ComponentName componentName = new ComponentName(appManagerInfo.getPackageName(), name);
                Intent startup_intent = new Intent();
                startup_intent.setComponent(componentName);
                //启动
                startActivity(startup_intent);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        } else if (item.getItemId() == R.id.action_details_uninstall) {
            if (isUserApp) {
                if (appManagerInfo.getPackageName().equals(getPackageName())) { //如果是本应用，也不能卸载
                    Toast.makeText(AppDetailsActivity.this, "系统应用，不能卸载本应用", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent_uninstall = new Intent();
                    //删除命令
                    intent_uninstall.setAction(Intent.ACTION_DELETE);
                    //包名
                    intent_uninstall.setData(Uri.parse("package:" + appManagerInfo.getPackageName()));
                    startActivity(intent_uninstall);
                }
            } else {
                Toast.makeText(AppDetailsActivity.this, "系统应用，不推荐卸载", Toast.LENGTH_SHORT).show();
            }
        } else if (item.getItemId() == R.id.action_details_share) {
            Intent share_intent = new Intent();
            //发送命令
            share_intent.setAction(Intent.ACTION_SEND);
            //发送类型
            share_intent.setType("text/plain");
            //发送主题
            share_intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
            //发送内容
            share_intent.putExtra(Intent.EXTRA_TEXT, "Hi,向你分享一款软件: " + appManagerInfo.getAppName());
            //创建intent对象
            share_intent = Intent.createChooser(share_intent, "分享");
            //启动
            startActivity(share_intent);
        }

        return true;
    }
}
