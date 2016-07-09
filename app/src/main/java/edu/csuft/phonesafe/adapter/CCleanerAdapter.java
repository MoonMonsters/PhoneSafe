package edu.csuft.phonesafe.adapter;

import android.content.Context;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.Bind;
import edu.csuft.phonesafe.R;
import edu.csuft.phonesafe.base.BaseViewHolder;
import edu.csuft.phonesafe.bean.CCleanerChildInfo;
import edu.csuft.phonesafe.bean.CCleanerParentInfo;

/**
 * Created by Chalmers on 2016-07-09 10:32.
 * email:qxinhai@yeah.net
 */
public class CCleanerAdapter extends BaseExpandableListAdapter {

    /**
     * 父项集合
     */
    private ArrayList<CCleanerParentInfo> parentInfoList = null;
    /**
     * 所有的子项集合，即字项集合的集合
     */
    private ArrayList<ArrayList<CCleanerChildInfo>> allChildInfoList = null;
    private Context context = null;

    public CCleanerAdapter(Context context, ArrayList<CCleanerParentInfo> parentInfoList,
                           ArrayList<ArrayList<CCleanerChildInfo>> allChildInfoList) {
        this.context = context;
        this.parentInfoList = parentInfoList;
        this.allChildInfoList = allChildInfoList;
    }

    /**
     * 返回父项的个数
     */
    @Override
    public int getGroupCount() {
        return parentInfoList.size();
    }

    /**
     * 返回对应父项下的子项的个数
     *
     * @param groupPosition 父项位置
     */
    @Override
    public int getChildrenCount(int groupPosition) {
        return allChildInfoList.get(groupPosition).size();
    }

    /**
     * 返回父项对象
     *
     * @param groupPosition 父项位置
     */
    @Override
    public Object getGroup(int groupPosition) {
        return parentInfoList.get(groupPosition);
    }

    /**
     * 返回子项位置
     *
     * @param groupPosition 父项位置
     * @param childPosition 子项位置
     */
    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return allChildInfoList.get(groupPosition).get(childPosition);
    }

    /**
     * 不需要
     */
    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    /**
     * 不需要
     */
    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        ParentViewHolder viewHolder = null;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_ccleaner_parent, null);
            viewHolder = new ParentViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ParentViewHolder) convertView.getTag();
        }

        viewHolder.bindData(parentInfoList.get(groupPosition));

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ChildViewHolder viewHolder = null;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_ccleaner_child, null);
            viewHolder = new ChildViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ChildViewHolder) convertView.getTag();
        }

        viewHolder.bindData(allChildInfoList.get(groupPosition).get(childPosition));

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        // TODO
        return true;
    }

    /**
     * 父项的帮助类
     */
    class ParentViewHolder extends BaseViewHolder {

        //垃圾类型
        @Bind(R.id.tv_item_ccleaner_parent_type)
        TextView tv_item_ccleaner_parent_type;
        //该类型下垃圾的大小
        @Bind(R.id.tv_item_ccleaner_parent_value)
        TextView tv_item_ccleaner_parent_value;
        //是否全部选中该下的所有子项,用图片来代替RadioButton
        @Bind(R.id.iv_item_ccleaner_parent_select)
        ImageView iv_item_ccleaner_parent_select;

        /**
         * ButterKnife绑定控件
         *
         * @param view View对象
         */
        public ParentViewHolder(View view) {
            super(view);
        }

        @Override
        public void bindData(Object obj) {
            CCleanerParentInfo parentInfo = (CCleanerParentInfo) obj;
            tv_item_ccleaner_parent_type.setText(parentInfo.getType());
            tv_item_ccleaner_parent_value.setText(Formatter.formatFileSize(context, parentInfo.getValue()));
            if (parentInfo.isSelect()) {
                iv_item_ccleaner_parent_select.setImageResource(R.drawable.ic_true);
            } else {
                iv_item_ccleaner_parent_select.setImageResource(R.drawable.ic_false);
            }

            iv_item_ccleaner_parent_select.setOnClickListener(new IVListener(parentInfo));
        }
    }

    /**
     * 子项的帮助类
     */
    class ChildViewHolder extends BaseViewHolder {

        //应用图标
        @Bind(R.id.iv_item_ccleaner_child_icon)
        ImageView iv_item_ccleaner_child_icon;
        //应用名称
        @Bind(R.id.tv_item_ccleaner_child_name)
        TextView tv_item_ccleaner_child_name;
        //垃圾大小
        @Bind(R.id.tv_item_ccleaner_child_value)
        TextView tv_item_ccleaner_child_value;
        //是否被选中清理
        @Bind(R.id.cb_item_ccleaner_child_select)
        RadioButton cb_item_ccleaner_child_select;

        /**
         * ButterKnife绑定控件
         *
         * @param view View对象
         */
        public ChildViewHolder(View view) {
            super(view);
        }

        @Override
        public void bindData(Object obj) {
            CCleanerChildInfo childInfo = (CCleanerChildInfo) obj;
            iv_item_ccleaner_child_icon.setImageDrawable(childInfo.getAppIcon());
            tv_item_ccleaner_child_name.setText(childInfo.getAppName());
            tv_item_ccleaner_child_value.setText(Formatter.formatFileSize(context, childInfo.getValue()));
            cb_item_ccleaner_child_select.setChecked(childInfo.isSelected());

            cb_item_ccleaner_child_select.setOnClickListener(new RBListener(childInfo));
        }
    }

    /**
     * RadioButton的点击事件，控制某一个子项的开关
     */
    private class RBListener implements View.OnClickListener {

        private CCleanerChildInfo childInfo = null;

        public RBListener(CCleanerChildInfo childInfo) {
            this.childInfo = childInfo;
        }

        @Override
        public void onClick(View v) {
            //当前状态
            boolean selected = childInfo.isSelected();
            //点击后置反
            childInfo.setSelected(!selected);

            //父项的位置
            int position = 0;
            //状态首先置为true
            boolean flag = true;
            //遍历子项
            for (int i = 0; i < allChildInfoList.size(); i++) {
                //得到子项中每一项的集合
                ArrayList<CCleanerChildInfo> childInfoList = allChildInfoList.get(i);
                //得到点击的子项的位置
                if (childInfoList.contains(childInfo)) {
                    //得到父项的位置
                    position = i;
                    //判断该子项中的所有数据是否都被选中
                    for (CCleanerChildInfo childInfo : childInfoList) {
                        //如果没有，则把flag置为false，并且推出循环
                        if (childInfo.isSelected() != flag) {
                            flag = false;
                            break;
                        }
                    }
                    break;
                }
            }

            setSelectedItem(flag, position, false);

            //刷新界面
            CCleanerAdapter.this.notifyDataSetChanged();
        }
    }

    /**
     * 图片的点击事件，也就是控制全选也全不选的开关
     */
    private class IVListener implements View.OnClickListener {

        private CCleanerParentInfo parentInfo = null;

        public IVListener(CCleanerParentInfo parentInfo) {
            this.parentInfo = parentInfo;
        }

        @Override
        public void onClick(View v) {
            //得到当前的状态
            boolean select = parentInfo.isSelect();
            //点击后置反
            select = !select;
            //重新设置
            parentInfo.setSelect(select);
            //得到父项的位置
            int position = parentInfoList.indexOf(parentInfo);
            setSelectedItem(select, position, true);
            //刷新界面
            CCleanerAdapter.this.notifyDataSetChanged();
        }
    }

    /**
     * 是否需要全部改变状态，如果是点击ImageView改变状态的话，就改变所有子项的选中状态
     * 如果是点击RadioButton改变状态的话，则就只改变父项的显示，则不改变子项的状态
     * 根据父项是否选中来更新子项是否应该被全选
     *
     * @param select        父项的选中状态
     * @param position      父项的位置
     * @param isSelectedAll 是否需要全部子项的状态
     */
    private void setSelectedItem(boolean select, int position, boolean isSelectedAll) {
        if (isSelectedAll) {
            //得到该父项下所有子项
            ArrayList<CCleanerChildInfo> childInfoList = allChildInfoList.get(position);
            //全部设置成跟父项相同的状态
            for (int i = 0; i < childInfoList.size(); i++) {
                CCleanerChildInfo childInfo = childInfoList.get(i);
                childInfo.setSelected(select);
            }
        } else {
            parentInfoList.get(position).setSelect(select);
        }
    }
}