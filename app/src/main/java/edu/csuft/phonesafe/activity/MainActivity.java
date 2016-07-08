package edu.csuft.phonesafe.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.format.Formatter;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import edu.csuft.phonesafe.adapter.MainFunctionAdapter;
import edu.csuft.phonesafe.R;
import edu.csuft.phonesafe.base.BaseActivity;
import edu.csuft.phonesafe.bean.FunctionInfo;
import edu.csuft.phonesafe.utils.StorageUtil;
import edu.csuft.phonesafe.view.ArcProgress;

public class MainActivity extends BaseActivity {

    @Bind(R.id.arc_main_rom)
    ArcProgress arc_main_rom = null;
    @Bind(R.id.arc_main_ram)
    ArcProgress arc_main_ram = null;
    @Bind(R.id.tv_main_rom)
    TextView tv_main_rom;
    @Bind(R.id.tv_main_ram)
    TextView tv_main_ram;
    @Bind(R.id.gv_main_function)
    GridView gv_main_function;

    /** 应用管理 */
    public static final int YING_YONG_GUAN_LI = 0;
    /** 流量查看 */
    public static final int LIU_LIANG_CHA_KAN = 2;
    /** 手机杀毒 */
    public static final int SHOU_JI_SHA_DU = 3;
    /** 任务管理 */
    public static final int REN_WU_GUAN_LI = 1;

    private ArrayList<FunctionInfo> functionInfoList = null;
    private MainFunctionAdapter functionAdapter = null;

    public static final int REFRESH_DISPLAY = 0;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case REFRESH_DISPLAY:
                    initRomData();
                    initRamData();
                    break;
            }
        }
    };

    private TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            Message msg = handler.obtainMessage();
            msg.what = REFRESH_DISPLAY;
            handler.sendMessage(msg);
        }
    };

    @Override
    public void initData() {
        Timer timer = new Timer();
        timer.schedule(timerTask,0,2000);
        initFunctionData();
    }

    /**
     * 设置存储空间的百分比以及数据显示
     */
    private void initRomData() {
        //得到剩余可用空间大小
        long availableMemory = StorageUtil.getAvailableMemorySize();
        //得到总存储空间大小
        long totalMemory = StorageUtil.getTotalMemorySize();
        //得到已用存储空间大小
        long usedMemory = totalMemory - availableMemory;

        String sizeStr = Formatter.formatFileSize(this, usedMemory)
                + "/" + Formatter.formatFileSize(this, totalMemory);
        tv_main_rom.setText(sizeStr);

        int progress = (int) ((usedMemory * 1.0 / totalMemory) * 100);
        arc_main_rom.setProgress(progress);
    }

    /**
     * 设置Ram数据的百分比
     */
    private void initRamData() {
        long availMem = StorageUtil.getAvailableRAMSize(this);
        long totalMem = StorageUtil.getTotalRAMSize(this);
        long usedMem = totalMem - availMem;

        String sizeStr = Formatter.formatFileSize(this, usedMem)
                + "/" + Formatter.formatFileSize(this, totalMem);
        tv_main_ram.setText(sizeStr);

        int progress = (int) ((usedMem * 1.0 / totalMem) * 100);

        arc_main_ram.setProgress(progress);
    }

    /** 初始化GridView的数据信息 */
    private void initFunctionData() {
        String text[] = {"应用管理", "任务管理", "流量查看", "手机杀毒"};
        int imgId[] = {R.drawable.ic_main_ruanjian, R.drawable.ic_main_renwu,
                R.drawable.ic_main_liuliang, R.drawable.ic_main_bingdu};

        functionInfoList = new ArrayList<>();
        for(int i=0; i<text.length; i++){
            FunctionInfo info = new FunctionInfo(imgId[i],text[i]);
            functionInfoList.add(info);
        }
        functionAdapter = new MainFunctionAdapter(this,functionInfoList);
        gv_main_function.setAdapter(functionAdapter);
    }

    @Override
    public void initListener() {
        gv_main_function.setOnItemClickListener(onItemClickListener);
    }

    @Override
    public int getLayoutResourceId() {
        return R.layout.activity_main;
    }

    /** GridView的点击事件 */
    AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = null;
            switch (position){
                case YING_YONG_GUAN_LI: //应用管理
                    intent = new Intent(MainActivity.this, AppManagerActivity.class);
                    break;
                case LIU_LIANG_CHA_KAN:
                    intent = new Intent(MainActivity.this,TrafficActivity.class);
                    break;
                case SHOU_JI_SHA_DU:
                    intent = new Intent(MainActivity.this, VirusActivity.class);
                    break;
                case REN_WU_GUAN_LI:
                    intent = new Intent(MainActivity.this, TaskActivity.class);
                    break;
            }

            startActivity(intent);
        }
    };
}
