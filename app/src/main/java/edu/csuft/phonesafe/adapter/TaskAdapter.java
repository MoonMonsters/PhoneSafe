package edu.csuft.phonesafe.adapter;

import android.content.Context;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.Bind;
import edu.csuft.phonesafe.R;
import edu.csuft.phonesafe.base.BaseViewHolder;
import edu.csuft.phonesafe.bean.TaskInfo;

/**
 * Created by Chalmers on 2016-07-03 21:28.
 * email:qxinhai@yeah.net
 */

/**
 * 任务管理适配器
 */
public class TaskAdapter extends BaseAdapter {

    private ArrayList<TaskInfo> taskInfoList = null;
    /** 上下文对象 */
    private Context context = null;

    public TaskAdapter(Context context, ArrayList<TaskInfo> taskInfoList){
        this.context = context;
        this.taskInfoList = taskInfoList;
    }

    /** 集合数据个数 */
    @Override
    public int getCount() {

        return taskInfoList.size();
    }

    /** 得到子项对象 */
    @Override
    public Object getItem(int position) {

        return taskInfoList.get(position);
    }

    /** 无用 */
    @Override
    public long getItemId(int position) {

        return position;
    }

    /** 得到View对象 */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder = null;

        if(convertView == null){    //如果View对象为空
            convertView = LayoutInflater.from(context).inflate(R.layout.item_task,parent,false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //绑定数据
        viewHolder.bindData(taskInfoList.get(position));

        return convertView;
    }

    class ViewHolder extends BaseViewHolder{
        @Bind(R.id.iv_item_task_icon)
        ImageView iv_item_task_icon;
        @Bind(R.id.tv_item_task_name)
        TextView tv_item_task_name;
        @Bind(R.id.tv_item_task_memory)
        TextView tv_item_task_memory;
        @Bind(R.id.cb_item_task_selected)
        CheckBox cb_item_task_selected;

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
         * @param obj 需要绑定数据对象
         */
        @Override
        public void bindData(Object obj) {
            TaskInfo taskInfo = (TaskInfo) obj;
            iv_item_task_icon.setImageDrawable(taskInfo.getAppIcon());
            tv_item_task_name.setText(taskInfo.getAppName());
            tv_item_task_memory.setText(Formatter.formatFileSize(context,taskInfo.getAppMemory()));
            cb_item_task_selected.setChecked(taskInfo.isSelected());
            //不能清除本应用的内存数据
            if(taskInfo.getPackageName().equals(context.getPackageName())){
                cb_item_task_selected.setVisibility(View.GONE);
                taskInfo.setSelected(false);
            }
        }
    }
}
