package edu.csuft.phonesafe.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.Bind;
import edu.csuft.phonesafe.R;
import edu.csuft.phonesafe.base.BaseViewHolder;
import edu.csuft.phonesafe.bean.AppManagerInfo;

/**
 * Created by Chalmers on 2016-07-06 23:38.
 * email:qxinhai@yeah.net
 */

/**
 * 应用管理界面的适配器
 */
public class AppManagerAdapter extends BaseAdapter {

    /** 集合 */
    private ArrayList<AppManagerInfo> appManagerInfoList = null;
    /** 上下文对象 */
    private Context context = null;

    /**
     * 构造方法
     * @param context 上下文对象
     * @param appManagerInfoList 集合
     */
    public AppManagerAdapter(Context context, ArrayList<AppManagerInfo> appManagerInfoList) {
        this.context = context;
        this.appManagerInfoList = appManagerInfoList;
    }

    /** 返回集合个数 */
    @Override
    public int getCount() {
        return appManagerInfoList.size();
    }

    /**
     * 返回集合中的对象
     * @param position 位置
     * @return 对象
     */
    @Override
    public Object getItem(int position) {
        return appManagerInfoList.get(position);
    }

    /** 无用，数据库才用到 */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /** 构建View对象 */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;

        //如果为空，则创建该对象
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_app_manager, parent, false);
            //创建ViewHolder对象
            viewHolder = new ViewHolder(convertView);
            //保存该对象
            convertView.setTag(viewHolder);
        } else {
            //取出
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //绑定数据
        viewHolder.bindData(appManagerInfoList.get(position));

        return convertView;
    }

    /** 帮助类 */
    class ViewHolder extends BaseViewHolder {
        @Bind(R.id.iv_app_manager_icon)
        ImageView iv_app_manager_icon;
        @Bind(R.id.tv_app_manager_name)
        TextView tv_app_manager_name;
        @Bind(R.id.tv_app_manager_version)
        TextView tv_app_manager_version;

        public ViewHolder(View view) {
            super(view);
        }

        /**
         * 绑定数据
         * @param obj 绑定数据的对象
         */
        @Override
        public void bindData(Object obj) {
            AppManagerInfo info = (AppManagerInfo) obj;

            //设置控件的数据
            iv_app_manager_icon.setImageDrawable(info.getAppIcon());
            tv_app_manager_name.setText(info.getAppName());
            tv_app_manager_version.setText(info.getAppVersion());
        }
    }
}