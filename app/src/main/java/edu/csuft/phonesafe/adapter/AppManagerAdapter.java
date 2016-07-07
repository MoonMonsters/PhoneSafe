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

    private ArrayList<AppManagerInfo> appManagerInfoList = null;
    private Context context = null;

    public AppManagerAdapter(Context context, ArrayList<AppManagerInfo> appManagerInfoList) {
        this.context = context;
        this.appManagerInfoList = appManagerInfoList;
    }

    @Override
    public int getCount() {
        return appManagerInfoList.size();
    }

    @Override
    public Object getItem(int position) {
        return appManagerInfoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_app_manager, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.bindData(appManagerInfoList.get(position));

        return convertView;
    }

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

        @Override
        public void bindData(Object obj) {
            AppManagerInfo info = (AppManagerInfo) obj;

            iv_app_manager_icon.setImageDrawable(info.getAppIcon());
            tv_app_manager_name.setText(info.getAppName());
            tv_app_manager_version.setText(info.getAppVersion());
        }
    }
}