package edu.csuft.phonesafe.helper;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import edu.csuft.phonesafe.R;

/**
 * Created by Chalmers on 2016-07-07 15:35.
 * email:qxinhai@yeah.net
 */

/**
 * 帮助类，在加载数据时显示
 */

public class ViewHelper {

    @Bind(R.id.tv_activity_progress)
    TextView tv_activity_progress;
    @Bind(R.id.pb_activity)
    ProgressBar pb_activity;

    public ViewHelper(Context context){
        ButterKnife.bind(this,(Activity) context);
    }

    /** 更新显示的TextView上的值 */
    public void updateTextViewValue(){
        String s = pb_activity.getProgress() + "/" + pb_activity.getMax();
        tv_activity_progress.setText(s);
    }

    /** 设置进度条的最大值 */
    public void setPbMaxValue(int maxValue){
        pb_activity.setMax(maxValue);
    }

    /** 更新进度条 */
    public void updateProgressBarValue(){
        pb_activity.setProgress(pb_activity.getProgress() + 1);
    }

    /** 数据加在完成，隐藏掉ProgressBar和TextView */
    public void hiddenPbAndTv(){
        tv_activity_progress.setVisibility(View.GONE);
        pb_activity.setVisibility(View.GONE);
    }

}
