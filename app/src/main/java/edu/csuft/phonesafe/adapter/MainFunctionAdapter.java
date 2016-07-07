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
public class MainFunctionAdapter extends BaseAdapter {

    private ArrayList<FunctionInfo> functionInfoList = null;
    private Context context;

    public MainFunctionAdapter(Context context, ArrayList<FunctionInfo> functionInfoList){
        this.context = context;
        this.functionInfoList = functionInfoList;
    }

    @Override
    public int getCount() {
        return functionInfoList.size();
    }

    @Override
    public Object getItem(int position) {
        return functionInfoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder = null;
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.item_main_function,parent,false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

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

        @Override
        public void bindData(Object obj) {
            FunctionInfo info = (FunctionInfo) obj;

            iv_main_item_function.setImageResource(info.getFunctionImgId());
            tv_main_item_function.setText(info.getFunctionText());
        }
    }
}
