package edu.csuft.phonesafe.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import edu.csuft.phonesafe.R;
import edu.csuft.phonesafe.activity.AppDetailsActivity;
import edu.csuft.phonesafe.adapter.AppManagerAdapter;
import edu.csuft.phonesafe.bean.AppManagerInfo;
import edu.csuft.phonesafe.utils.Config;

/**
 * A simple {@link Fragment} subclass.
 */

/**
 * 应用管理界面的Fragment，放到ViewPager中
 */
public class AppManagerFragment extends Fragment {

    @Bind(R.id.lv_fragment_app_manager)
    ListView lv_fragment_app_manager;

    /**
     * 该应用是否为用户应用
     */
    private boolean isUserApp;

    /**
     * 数据集合
     */
    private ArrayList<AppManagerInfo> appManagerInfoList = null;
    /**
     * 应用程序管理适配器
     */
    private AppManagerAdapter appManagerAdapter = null;

    public static final String FRAGMENT_LIST_VALUE = "fragment_list_value";

    public AppManagerFragment() {

    }

    /**
     * 创建视图对象
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_app_manager, container, false);
        ButterKnife.bind(this, view);

        //获得设置的数据
        Bundle bundle = getArguments();

        appManagerInfoList = bundle.getParcelableArrayList(FRAGMENT_LIST_VALUE);
        appManagerAdapter = new AppManagerAdapter(getActivity(), appManagerInfoList);
        lv_fragment_app_manager.setAdapter(appManagerAdapter);

        //是否为用户应用
        isUserApp = bundle.getBoolean(Config.IS_USER_APP);

        //初始化监听器
        initListener();

        return view;
    }

    /**
     * 初始化监听器
     */
    private void initListener() {
        lv_fragment_app_manager.setOnItemClickListener(onItemClickListener);
    }

    @Override
    public void onStart() {
        super.onStart();
        appManagerAdapter.notifyDataSetChanged();
    }

    /**
     * 得到Fragment实例对象
     *
     * @param appManagerInfoList app数据集合
     */
    public static AppManagerFragment getInstance(ArrayList<AppManagerInfo> appManagerInfoList, boolean isUserAppFlag) {

        //创建Fragment对象
        AppManagerFragment fragment = new AppManagerFragment();

        //设置传递数据
        //将数据通过setArguments保存，在创建界面时再取出
        //因为不推荐使用new Fragment(Object)方法
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(FRAGMENT_LIST_VALUE, appManagerInfoList);
        bundle.putBoolean(Config.IS_USER_APP, isUserAppFlag);
        fragment.setArguments(bundle);

        return fragment;
    }

    /**
     * ListView的点击事件
     */
    AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            //点击后，把点击的项的数据对象传递过去
            Intent intent = new Intent();
            intent.putExtra(Config.APP_MANAGER_INFO_VALUE, appManagerInfoList.get(position));
            intent.putExtra(Config.IS_USER_APP, isUserApp);
            intent.setClass(getActivity(), AppDetailsActivity.class);
            startActivity(intent);
        }
    };
}
