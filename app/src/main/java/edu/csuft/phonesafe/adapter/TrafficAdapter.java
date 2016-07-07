package edu.csuft.phonesafe.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.Bind;
import edu.csuft.phonesafe.R;
import edu.csuft.phonesafe.base.BaseViewHolder;
import edu.csuft.phonesafe.bean.TrafficInfo;

/**
 * Created by Chalmers on 2016-06-29 19:22.
 * email:qxinhai@yeah.net
 */
public class TrafficAdapter extends BaseAdapter {

    private ArrayList<TrafficInfo> trafficInfoList = null;
    private Context context = null;

    /** 传进来的数据的最大值，用来设置Progress的当前进度 */
    private long maxValue = 0;

    public TrafficAdapter(Context context, ArrayList<TrafficInfo> trafficInfoList){
        this.context = context;
        this.trafficInfoList = trafficInfoList;

        //将耗费流量最多的值当做最大值
        maxValue = trafficInfoList.get(0).getTrafficTotal();
    }

    @Override
    public int getCount() {
        return trafficInfoList.size();
    }

    @Override
    public Object getItem(int position) {
        return trafficInfoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder = null;

        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.item_traffice,parent,false);
            viewHolder = new ViewHolder(convertView);

            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.bindData(trafficInfoList.get(position));

        return convertView;
    }

    class ViewHolder extends BaseViewHolder{
        @Bind(R.id.iv_traffic_app_pic)
        ImageView iv_traffic_app_pic;
        @Bind(R.id.tv_traffic_app_name)
        TextView tv_traffic_app_name;
        @Bind(R.id.pb_traffic_app)
        ProgressBar pb_traffic_app;
        @Bind(R.id.tv_traffic_total)
        TextView tv_traffic_total;

        public ViewHolder(View view){
            super(view);
        }

        /** 绑定数据 */
        public void bindData(Object obj){
            TrafficInfo trafficInfo = (TrafficInfo) obj;
            iv_traffic_app_pic.setImageDrawable(trafficInfo.getAppIcon());
            tv_traffic_app_name.setText(trafficInfo.getAppName());
            pb_traffic_app.setProgress(getProgress(trafficInfo.getTrafficTotal()));
            tv_traffic_total.setText(android.text.format.Formatter.formatFileSize(context,trafficInfo.getTrafficTotal()));
        }
    }

    private int getProgress(long value){
        int progress = 0;

        progress = (int)(value * 1.0 / maxValue * 100);

        return progress;
    }
}