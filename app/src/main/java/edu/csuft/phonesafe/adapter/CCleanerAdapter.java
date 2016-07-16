package edu.csuft.phonesafe.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.format.Formatter;
import android.util.Log;
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
import edu.csuft.phonesafe.utils.Config;
import edu.csuft.phonesafe.view.MarqueeText;

/**
 * Created by Chalmers on 2016-07-09 10:32.
 * email:qxinhai@yeah.net
 */

/**
 * 应用管理界面的适配器
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
    /**
     * 上下文对象
     */
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

    /**
     * 设置父项的显示数据View对象
     */
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        //父项的ViewHolder
        ParentViewHolder viewHolder = null;

        //如果为空，则创建对象
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_ccleaner_parent, null);
            //创建ViewHolder对象
            viewHolder = new ParentViewHolder(convertView);
            //设置tag
            convertView.setTag(viewHolder);
        } else {
            //直接从convertView中获得
            viewHolder = (ParentViewHolder) convertView.getTag();
        }

        //绑定数据
        viewHolder.bindData(parentInfoList.get(groupPosition));

        return convertView;
    }

    /**
     * 获得子项的显示的View对象
     */
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ChildViewHolder viewHolder = null;

        //如果convertView对象为空，则实例化该对象
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_ccleaner_child, null);
            viewHolder = new ChildViewHolder(convertView);
            //设置tag
            convertView.setTag(viewHolder);
        } else {
            //重新获得tag，提高性能
            viewHolder = (ChildViewHolder) convertView.getTag();
        }

        //绑定数据
        viewHolder.bindData(allChildInfoList.get(groupPosition).get(childPosition));

        return convertView;
    }

    /**
     * 是否被选择
     *
     * @param groupPosition 父项位置
     * @param childPosition 子项位置
     * @return 是否被选择
     */
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
            //将Object转换成CCleanerParentInfo类型
            CCleanerParentInfo parentInfo = (CCleanerParentInfo) obj;
            /*
            设置控件数据
             */
            tv_item_ccleaner_parent_type.setText(parentInfo.getType());
            tv_item_ccleaner_parent_value.setText(Formatter.formatFileSize(context, parentInfo.getValue()));
            if (parentInfo.isSelect()) {
                iv_item_ccleaner_parent_select.setImageResource(R.drawable.ic_true);
            } else {
                iv_item_ccleaner_parent_select.setImageResource(R.drawable.ic_false);
            }

            //设置监听器，传递CCleanerParentInfo对象
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
        MarqueeText tv_item_ccleaner_child_name;
        //垃圾大小
        @Bind(R.id.tv_item_ccleaner_child_value)
        MarqueeText tv_item_ccleaner_child_value;
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

        /**
         * 绑定数据
         *
         * @param obj 需要绑定数据类型对象
         */
        @Override
        public void bindData(Object obj) {
            CCleanerChildInfo childInfo = (CCleanerChildInfo) obj;
            switch (childInfo.getType()) {
                /*
                根据文件类型设置图标
                 */
                case Config.HUAN_CUN_QING_LI:   //如果是缓存，则设置之前设置的图片
                    iv_item_ccleaner_child_icon.setImageDrawable(childInfo.getAppIcon());
                    break;
                case Config.AN_ZHUANG_BAO_QING_LI:  //安装文件
                    iv_item_ccleaner_child_icon.setImageResource(R.drawable.ic_apk);
                    break;
                case Config.RI_ZHI_QING_LI: //日志文件
                    iv_item_ccleaner_child_icon.setImageResource(R.drawable.ic_log);
                    break;
                case Config.DA_WEN_JIAN_QING_LI:    //大文件
                    iv_item_ccleaner_child_icon.setImageResource(R.drawable.ic_big_file);
                    break;
                case Config.KONG_WEN_JIAN_JIA_QING_LI:  //空文件夹
                    iv_item_ccleaner_child_icon.setImageResource(R.drawable.ic_dir);
                    break;
            }

            /*
            设置Text值
             */
            tv_item_ccleaner_child_name.setText(childInfo.getAppNameOrfileName());
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

            //如果是取消，则取负值
            long size = childInfo.getValue();
            if (!childInfo.isSelected()) {
                size = -size;
            }
            //改变顶部的显示大小的值
//            sendBroadcast(size);

            /*
             该段代码的意思是，如果子项不是全选状态，那么父项就不能是全选状态
              在改变子项状态后，进行判断是否全选了，如果没有，那么找到子项对应的父项的位置
              把它的状态改成取消
             */
            //父项的位置
            int position = 0;
            //状态首先置为true，即子项是否为全选状态
            boolean flag = true;
            //遍历子项,找到该子项对应的父项
            for (int i = 0; i < allChildInfoList.size(); i++) {
                //得到子项中每一项的集合
                ArrayList<CCleanerChildInfo> childInfoList = allChildInfoList.get(i);
                //得到点击的子项的位置
                if (childInfoList.contains(childInfo)) {
                    //得到父项的位置
                    position = i;
                    //判断该子项中的所有数据是否都被选中
                    for (CCleanerChildInfo childInfo : childInfoList) {
                        //如果有某一项未选中，则把flag置为false，并且退出循环
                        if (!childInfo.isSelected()) {
                            flag = false;
                            break;
                        }
                    }
                    break;
                }
            }

            /*****************************************************/
            //根据点击子项位置得到父项
            CCleanerParentInfo parentInfo = parentInfoList.get(position);
            //设置父项的值
            parentInfo.setValue(parentInfo.getValue() + size);

            //去改变父项的选中的图片
            //false表示不需要改变子项状态
            //flag表示父项当前的选中状态，即显示的图片类型
            setSelectedItem(flag, position, false);

            Log.i("TAG", "parentInfo.getValue = " + parentInfo.getValue());

            sendBroadcast(childInfo.isSelected() ? childInfo.getValue() : -childInfo.getValue());

            //刷新界面
            CCleanerAdapter.this.notifyDataSetChanged();
        }
    }

    /**
     * 父项图片的点击事件，也就是控制全选与全不选的开关
     */
    private class IVListener implements View.OnClickListener {

        /**
         * 父项对象
         */
        private CCleanerParentInfo parentInfo = null;

        public IVListener(CCleanerParentInfo parentInfo) {
            this.parentInfo = parentInfo;
        }

        @Override
        public void onClick(View v) {

            Log.i("TAG", "" + parentInfo.getType());

            //得到当前的状态
            boolean select = parentInfo.isSelect();
            //点击后置反
            select = !select;
            //重新设置
            parentInfo.setSelect(select);
            //得到父项的位置
            int position = parentInfoList.indexOf(parentInfo);
            //设置父项的图片，true表示会根据select来改变子项的选中状态
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
        //选中的文件的总大小
        long totalSize = 0;
        //根据position得到父项
        CCleanerParentInfo parentInfo = parentInfoList.get(position);
        //如果是需要改变子项的选中状态
        if (isSelectedAll) {
            Log.i("TAG", "Before:parentInfo.getValue = " + parentInfo.getValue());
            //得到该父项下所有子项
            //因为父项的position对于着子项集合中的position个位置的子项
            ArrayList<CCleanerChildInfo> childInfoList = allChildInfoList.get(position);
            //全部设置成跟父项相同的状态
            Log.i("TAG", "childInfoList.size() = " + childInfoList.size());
            for (int i = 0; i < childInfoList.size(); i++) {
                //得到子项对象
                CCleanerChildInfo childInfo = childInfoList.get(i);
                /*
                这段代码特别重要，也有点难以理解
                大致意思是说，改变父项状态时，如果是全选，那么之前选中的话，它的值就不再累加了
                如果没有选中，那么则全部累加计算
                主要是在测试的时候，发现通过改变不同的子项父项状态，总的值一直在增加，所以加上这部分代码
                 */
                if (select) { //如果父项是选择状态
                    //如果子项之前是未选中状态，那么值就累加，否则不用管
                    if (!childInfo.isSelected()) {
                        //选中的值累加
                        totalSize += childInfo.getValue();
                    }
                } else {    //如果当前的父项是未选中状态，则累加所有子项的值
                    if (childInfo.isSelected()) {
                        totalSize += childInfo.getValue();
                    }
                }

                Log.i("TAG", "size = " + totalSize);
                //将子项的选中状态改成和父项相同
                childInfo.setSelected(select);
            }
            //如果不需要改变，则直接改变父类的状态即可
        } else {
            parentInfoList.get(position).setSelect(select);
        }

        //如果是全部取消，那么取负值
        if (!select) {
            totalSize = -totalSize;
        }
        Log.i("TAG", "totalSize = " + totalSize);
        //改变父项的显示的值
        parentInfo.setValue(parentInfo.getValue() + totalSize);

        Log.i("TAG", "After:parentInfo.getValue = " + parentInfo.getValue());

        sendBroadcast(totalSize);
    }

    /**
     * 发送广播，改变Activity中顶部的显示
     *
     * @param size 改变了的值
     */
    private void sendBroadcast(long size) {
        Intent intent = new Intent();
        //广播的动作
        intent.setAction(Config.CHANGE_SELECTION_BROADCAST);
        //传递的值
        intent.putExtra(Config.TOTAL_SIZE_DATA, size);
        context.sendBroadcast(intent);
    }
}