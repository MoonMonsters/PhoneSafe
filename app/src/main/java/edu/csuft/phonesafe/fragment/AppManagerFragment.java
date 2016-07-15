package edu.csuft.phonesafe.fragment;


import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import edu.csuft.phonesafe.R;
import edu.csuft.phonesafe.adapter.AppManagerAdapter;
import edu.csuft.phonesafe.bean.AppManagerInfo;

/**
 * A simple {@link Fragment} subclass.
 */

/**
 * 应用管理界面的Fragment，放到ViewPager中
 */
public class AppManagerFragment extends Fragment {

    @Bind(R.id.lv_fragment_app_manager)
    ListView lv_fragment_app_manager;
    //弹出窗口
    PopupWindow popupWindow;

    /** 该应用是否为用户应用 */
    private boolean isUserApp;

    /** 数据集合 */
    private ArrayList<AppManagerInfo> appManagerInfoList = null;
    /** 应用程序管理适配器 */
    private AppManagerAdapter appManagerAdapter = null;

    public static final String FRAGMENT_LIST_VALUE = "fragment_list_value";
    public static final String IS_USER_APP = "is_user_app";

    public AppManagerFragment() {

    }

    /** 创建视图对象 */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_app_manager, container, false);
        ButterKnife.bind(this,view);

        //获得设置的数据
        Bundle bundle = getArguments();

        appManagerInfoList = bundle.getParcelableArrayList(FRAGMENT_LIST_VALUE);
        appManagerAdapter = new AppManagerAdapter(getActivity(),appManagerInfoList);
        lv_fragment_app_manager.setAdapter(appManagerAdapter);

        //是否为用户应用
        isUserApp = bundle.getBoolean(IS_USER_APP);

        //初始化监听器
        initListener();

        return view;
    }

    /** 初始化监听器 */
    private void initListener(){
        lv_fragment_app_manager.setOnItemClickListener(onItemClickListener);
    }

    /**
     * 得到Fragment实例对象
     * @param appManagerInfoList app数据集合
     */
    public static AppManagerFragment getInstance(ArrayList<AppManagerInfo> appManagerInfoList, boolean isUserAppFlag){

        //创建Fragment对象
        AppManagerFragment fragment = new AppManagerFragment();

        //设置传递数据
        //将数据通过setArguments保存，在创建界面时再取出
        //因为不推荐使用new Fragment(Object)方法
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(FRAGMENT_LIST_VALUE,appManagerInfoList);
        bundle.putBoolean(IS_USER_APP,isUserAppFlag);
        fragment.setArguments(bundle);

        return fragment;
    }

    /** ListView的点击事件 */
    AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            View v = LayoutInflater.from(getActivity()).inflate(R.layout.item_app_manager_dialog,null,false);
            //设置弹出窗口的位置
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

        /*
        设置弹出窗口中的按钮的监听器
         */
        btn_app_manager_popup_startup.setOnClickListener(onClickListener);
        btn_app_manager_popup_uninstall.setOnClickListener(onClickListener);
        btn_app_manager_popup_share.setOnClickListener(onClickListener);
    }

    /** 弹出窗口的点击事件*/
    private class PopupOnClickListener implements View.OnClickListener{

        private int position;

        public PopupOnClickListener(int position){
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            AppManagerInfo info = appManagerInfoList.get(position);

            //包管理器
            PackageManager pm = getActivity().getPackageManager();
            //包名
            String packageName = info.getPackageName();

            if(v.getId() == R.id.btn_app_manager_popup_startup){    //启动程序
                try {
                    PackageInfo packageInfo = pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
                    //得到该程序下的所有Activity
                    ActivityInfo[] activities = packageInfo.activities;
                    //如果为空或者长度为0，则表示该程序没有界面，不能被启动
                    if(activities == null || activities.length == 0){
                        Toast.makeText(getActivity(),"该应用不能被启动",Toast.LENGTH_SHORT).show();

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
                if(isUserApp){
                    if(info.getPackageName().equals(getActivity().getPackageName())){ //如果是本应用，也不能卸载
                        Toast.makeText(getActivity(),"系统应用，不能卸载本应用",Toast.LENGTH_SHORT).show();
                    }else{
                        Intent intent_uninstall = new Intent();
                        intent_uninstall.setAction(Intent.ACTION_DELETE);
                        //包名
                        intent_uninstall.setData(Uri.parse("package:"+packageName));
                        startActivity(intent_uninstall);

                        //从列表中移除掉
                        appManagerInfoList.remove(info);
                        appManagerAdapter.notifyDataSetChanged();
                    }
                }else{
                    Toast.makeText(getActivity(),"系统应用，不推荐卸载",Toast.LENGTH_SHORT).show();
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
