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
import edu.csuft.phonesafe.bean.FunctionInfo;

/**
 * Created by Chalmers on 2016-07-06 22:59.
 * email:qxinhai@yeah.net
 */

/**
 * 主界面的GridView的适配器
 */
public class MainFunctionAdapter extends BaseAdapter {

    /** 集合 */
    private ArrayList<FunctionInfo> functionInfoList = null;
    /** 上下文对象 */
    private Context context;

    public MainFunctionAdapter(Context context, ArrayList<FunctionInfo> functionInfoList){
        this.context = context;
        this.functionInfoList = functionInfoList;
    }

    /** 集合数据个数 */
    @Override
    public int getCount() {
        return functionInfoList.size();
    }

    /**
     * 子项位置
     * @param position 位置
     * @return 子项对象
     */
    @Override
    public Object getItem(int position) {
        return functionInfoList.get(position);
    }

    /** 无用 */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /** 创建View对象 */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder = null;
        if(convertView == null){    //如果View对象为空
            convertView = LayoutInflater.from(context).inflate(R.layout.item_main_function,parent,false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //绑定数据
        viewHolder.bindData(functionInfoList.get(position));

        return convertView;
    }

    class ViewHolder extends BaseViewHolder{
        @Bind(R.id.iv_main_item_function)
        ImageView iv_main_item_function;
        @Bind(R.id.tv_main_item_function)
        TextView tv_main_item_function;

        /**
         * ButterKnife绑定控件
         *
         * @param view View对象
         */
        public ViewHolder(View view) {
            super(view);
        }

        /** 绑定数据 */
        @Override
        public void bindData(Object obj) {
            FunctionInfo info = (FunctionInfo) obj;

            iv_main_item_function.setImageResource(info.getFunctionImgId());
            tv_main_item_function.setText(info.getFunctionText());
        }
    }
}
