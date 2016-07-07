package edu.csuft.phonesafe.base;

/**
 * Created by Chalmers on 2016-07-06 23:44.
 * email:qxinhai@yeah.net
 */

import android.view.View;

import butterknife.ButterKnife;

/**
 * ViewHolder的基类
 */
public abstract class BaseViewHolder {

    /**
     * ButterKnife绑定控件
     *
     * @param view View对象
     */
    public BaseViewHolder(View view){
        ButterKnife.bind(this,view);
    }

    /** 绑定数据 */
    public abstract void bindData(Object obj);

}
