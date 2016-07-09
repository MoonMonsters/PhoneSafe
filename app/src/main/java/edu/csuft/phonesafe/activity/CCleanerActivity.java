package edu.csuft.phonesafe.activity;

import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.ExpandableListView;

import java.io.File;
import java.util.ArrayList;

import butterknife.Bind;
import edu.csuft.phonesafe.adapter.CCleanerAdapter;
import edu.csuft.phonesafe.base.BaseActivity;
import edu.csuft.phonesafe.bean.CCleanerChildInfo;
import edu.csuft.phonesafe.bean.CCleanerParentInfo;
import edu.csuft.phonesafe.R;

/**
 * 垃圾清理界面
 */
public class CCleanerActivity extends BaseActivity {

    @Bind(R.id.elv_ccleaner)
    ExpandableListView elv_ccleaner;

    /** 父项集合 */
    private ArrayList<CCleanerParentInfo> parentInfoList = null;
    /** 所有的子项集合，即字项集合的集合 */
    private ArrayList<ArrayList<CCleanerChildInfo>> allChildInfoList = null;

    @Override
    public void initData() {
        parentInfoList = new ArrayList<>();
        allChildInfoList = new ArrayList<>();
        for(int i =0; i<5; i++){
            CCleanerParentInfo parentInfo = new CCleanerParentInfo("垃圾清理",true,1024);
            parentInfoList.add(parentInfo);
        }
        for(int i=0; i<5; i++){
            /** 每一个父项下的子项的集合 */
            ArrayList<CCleanerChildInfo> childInfoList = new ArrayList<>();
            for(int j=0; j<3; j++){
                Drawable drawable = getResources().getDrawable(R.drawable.ic_main_bingdu);
                CCleanerChildInfo childInfo = new CCleanerChildInfo(drawable,"应用名称","packageName",true,1024);
                childInfoList.add(childInfo);
            }
            allChildInfoList.add(childInfoList);
        }

        CCleanerAdapter cCleanerAdapter = new CCleanerAdapter(this,parentInfoList,allChildInfoList);
        elv_ccleaner.setAdapter(cCleanerAdapter);


        Log.i("TAG","缓存的总大小："+ Formatter.formatFileSize(this,getEnvironmentSize()));
    }

    @Override
    public void initListener() {
//        elv_ccleaner.setOnClickListener();
//        elv_ccleaner.setOnChildClickListener();
    }

    @Override
    public int getLayoutResourceId() {
        return R.layout.activity_ccleaner;
    }

    private  long getEnvironmentSize()
    {
        File localFile = Environment.getDataDirectory();
        long l1;
        if (localFile == null)
            l1 = 0L;
        while (true)
        {

            String str = localFile.getPath();
            StatFs localStatFs = new StatFs(str);
            long l2 = localStatFs.getBlockSizeLong();
            l1 = localStatFs.getBlockCountLong() * l2;
            return l1;
        }
    }
}