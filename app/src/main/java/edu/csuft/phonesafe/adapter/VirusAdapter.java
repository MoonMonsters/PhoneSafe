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
import edu.csuft.phonesafe.bean.VirusAppInfo;

/**
 * Created by Chalmers on 2016-07-07 18:28.
 * email:qxinhai@yeah.net
 */

/**
 * 病毒查杀界面
 */
public class VirusAdapter extends BaseAdapter {

    private ArrayList<VirusAppInfo> virusAppInfoList = null;
    /**
     * 上下文对象
     */
    private Context context = null;

    public VirusAdapter(Context context, ArrayList<VirusAppInfo> virusAppInfoList) {
        this.context = context;
        this.virusAppInfoList = virusAppInfoList;
    }

    /**
     * 集合数据个数
     */
    @Override
    public int getCount() {
        return virusAppInfoList.size();
    }

    /**
     * 子项对象
     */
    @Override
    public Object getItem(int position) {
        return virusAppInfoList.get(position);
    }

    /**
     * 无用
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * 得到View对象
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_virus, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //绑定数据
        viewHolder.bindData(virusAppInfoList.get(position));


        return convertView;
    }

    /**
     * 帮助类
     */
    class ViewHolder extends BaseViewHolder {

        @Bind(R.id.iv_virus_icon)
        ImageView iv_virus_icon;
        @Bind(R.id.tv_virus_name)
        TextView tv_virus_name;

        /**
         * ButterKnife绑定控件
         *
         * @param view View对象
         */
        public ViewHolder(View view) {
            super(view);
        }

        /**
         * 绑定数据
         *
         * @param obj 需要绑定的对象
         */
        @Override
        public void bindData(Object obj) {
            VirusAppInfo info = (VirusAppInfo) obj;
            iv_virus_icon.setImageDrawable(info.getAppIcon());
            tv_virus_name.setText(info.getAppName());
        }
    }
}
