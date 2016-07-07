package edu.csuft.phonesafe.base;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import butterknife.ButterKnife;

/**
 * Created by Chalmers on 2016-07-06 17:56.
 * email:qxinhai@yeah.net
 */
public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResourceId());

        initView();
        initData();
        initListener();
    }

    /** 初始化控件信息 */
    public void initView(){
        ButterKnife.bind(this);
    }
    /** 初始化数据信息 */
    public abstract void initData();
    /** 初始化监听器信息 */
    public abstract void initListener();
    /** 返回布局id */
    public abstract int getLayoutResourceId();
}
