package edu.csuft.phonesafe.activity;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import edu.csuft.phonesafe.R;
import edu.csuft.phonesafe.adapter.VirusAdapter;
import edu.csuft.phonesafe.base.BaseActivity;
import edu.csuft.phonesafe.bean.VirusAppInfo;
import edu.csuft.phonesafe.utils.Config;
import edu.csuft.phonesafe.utils.MD5Util;

/**
 * 杀毒界面
 */
public class VirusActivity extends BaseActivity {

    @Bind(R.id.iv_virus_anim)
    ImageView iv_virus_anim;
    @Bind(R.id.btn_virus_start)
    Button btn_virus_start;
    @Bind(R.id.btn_virus_stop)
    Button btn_virus_stop;
    @Bind(R.id.btn_virus_kill)
    Button btn_virus_kill;
    @Bind(R.id.lv_virus)
    ListView lv_virus;
    @Bind(R.id.pb_virus)
    ProgressBar pb_virus;

    /** 存放所有的病毒程序的md5码 */
    private ArrayList<String> allVirusMD5 = null;
    /** 存放已经扫描到的病毒程序 */
    private ArrayList<VirusAppInfo> existVirusList = null;
    /** 存放app信息集合 */
    private ArrayList<VirusAppInfo> virusAppInfoList = null;
    /** 适配器 */
    private VirusAdapter virusAdapter = null;

    //动画相关类
    private AnimationDrawable drawable;
    private File file = null;
    /** 包管理器 */
    private PackageManager pm = null;

    /** 是否正在扫描 */
    private boolean isRunning = true;

    /** 用来处理ui */
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case Config.SUCCESS_LOAD:   //加载完成
                    finishScanVirus();  //结束杀毒
                    break;
                case Config.UPDATE_PROGRESS:    //更新
                    virusAdapter.notifyDataSetChanged();    //更新数据
                    //每次都移动到最后一个位置
                    lv_virus.setSelection(virusAppInfoList.size()-1);
                    break;
            }
        }
    };

    /** 初始化数据 */
    @Override
    public void initData() {
        //得到包管理器对象
        pm = getPackageManager();
        /*
        初始化集合数据对象
         */
        allVirusMD5 = new ArrayList<>();
        existVirusList = new ArrayList<>();
        virusAppInfoList = new ArrayList<>();
        virusAdapter = new VirusAdapter(this,virusAppInfoList);
        lv_virus.setAdapter(virusAdapter);
        //从文件中加载病毒数据
        getVirusDataFromFile();
    }

    /** 开始动画 */
    private void startAnim(){
        iv_virus_anim.setImageResource(R.drawable.anim_scan);
        drawable = (AnimationDrawable) iv_virus_anim.getDrawable();
        drawable.start();
    }

    /** 停止动画 */
    private void stopAnim(){
        drawable.stop();
    }

    /** 初始化监听器 */
    @Override
    public void initListener() {
        /*
        设置按钮的监听器
         */
        btn_virus_kill.setOnClickListener(onClickListener);
        btn_virus_start.setOnClickListener(onClickListener);
        btn_virus_stop.setOnClickListener(onClickListener);
    }

    /** 返回布局id */
    @Override
    public int getLayoutResourceId() {
        return R.layout.activity_virus;
    }

    /** 显示开始按钮 */
    private void showBtnStart(){
        btn_virus_start.setVisibility(View.VISIBLE);
    }

    /** 隐藏开始按钮 */
    private void hiddenBtnStart(){
        btn_virus_start.setVisibility(View.GONE);
    }

    /** 显示停止按钮 */
    private void showBtnStop(){
        btn_virus_stop.setVisibility(View.VISIBLE);
    }

    /** 隐藏停止按钮 */
    private void hiddenBtnStop(){
        btn_virus_stop.setVisibility(View.GONE);
    }

    /** 显示杀毒按钮 */
    private void showBtnKill(){
        btn_virus_kill.setVisibility(View.VISIBLE);
    }

    /** 隐藏杀毒按钮 */
    private void hiddenBtnKill(){
        btn_virus_kill.setVisibility(View.GONE);
    }

    /**
     * 从本地病毒数据库总把所有数据读取出来，并且放到文件中去
     */
    private void getVirusDataFromFile(){
        //构建file对象
        file = new File(getFilesDir(),"antivirus.db");
        try {
            //得到输入流
            InputStream inputStream = getAssets().open("antivirus.db");
            //文件输出流
            FileOutputStream fos = new FileOutputStream(file);
            byte[] buf = new byte[2048];
            int len = -1;
            //循环读取数据，并且写入缓存文件中
            while((len = inputStream.read(buf)) != -1){
                fos.write(buf,0,len);
            }

            //关闭流对象
            inputStream.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //加载文件数据到数据库中
        SQLiteDatabase sqLiteDatabase = SQLiteDatabase.openDatabase(file.getAbsolutePath(),null,SQLiteDatabase.OPEN_READONLY);
        if(sqLiteDatabase.isOpen()){
            //查询
            Cursor cursor = sqLiteDatabase.query("datable",new String[]{"md5"},null,null,null,null,null);
            while(cursor.moveToNext()){
                //获得数据表的数据
                String md5 = cursor.getString(cursor.getColumnIndex("md5"));
                //将查询到的md5码加入集合中
                allVirusMD5.add(md5);
            }
            //关闭
            cursor.close();
            sqLiteDatabase.close();
        }
    }

    /** 在子线程中扫描所有应用 */
    private class KillVirusThread extends Thread{
        @Override
        public void run() {
            virusAppInfoList.clear();
            //得到手机中所有的app
            List<PackageInfo> packageInfoList = pm.getInstalledPackages(PackageManager.GET_SIGNATURES);
            //设置最大值
            pb_virus.setMax(packageInfoList.size());
            for(PackageInfo packageInfo : packageInfoList){

                //如果点击停止按钮，则终止扫描
                if(!isRunning){
                    /*
                    清空集合数据
                     */
                    virusAppInfoList.clear();
                    existVirusList.clear();
                    break;
                }

                //程序包名
                String packageName = packageInfo.packageName;
                //程序的应用信息
                ApplicationInfo applicationInfo = packageInfo.applicationInfo;

                //程序图标
                Drawable icon = applicationInfo.loadIcon(pm);
                //程序名
                String appName = applicationInfo.loadLabel(pm).toString();

                //从PackageInfo中读取应用的签名
                Signature[] signatures = packageInfo.signatures;
                StringBuilder sb = new StringBuilder();
                for(Signature signature : signatures){
                    sb.append(signature.toCharsString());
                }
                //签名转化成md5格式
                String signatureMD5 = MD5Util.getMD5FromStringPwd(sb.toString());

                VirusAppInfo virusAppInfo = new VirusAppInfo(icon,appName,packageName);

                //判断病毒集合是否有包含了此签名，如果有，则把该应用程序对象加入到病毒集合中
                if(allVirusMD5.contains(signatureMD5)){
                    existVirusList.add(virusAppInfo);
                }

                //休眠300毫秒，避免加载数据过快，界面崩溃
                SystemClock.sleep(300);

                //增加1
                pb_virus.incrementProgressBy(1);

                virusAppInfoList.add(virusAppInfo);

                //发送消息
                Message msg = handler.obtainMessage();
                msg.what = Config.UPDATE_PROGRESS;
                handler.sendMessage(msg);
            }

            //ListView的最后一项放结果
            Drawable icon = getResources().getDrawable(R.drawable.ic_result);
            VirusAppInfo virusAppInfo = new VirusAppInfo(icon,"恶意应用数量:"+existVirusList.size(),null);
            virusAppInfoList.add(virusAppInfo);
            //发送消息，通知扫描完成
            Message msg = handler.obtainMessage();
            msg.what = Config.SUCCESS_LOAD;
            handler.sendMessage(msg);
        }
    }

    /** 点击事件 */
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.btn_virus_start){  //开始
                startScanVirus();
            }else if(v.getId() == R.id.btn_virus_stop){ //停止
                stopScanVirus();
            }else if(v.getId() == R.id.btn_virus_kill){ //杀毒按钮
                //卸载所有恶意软件
                for(VirusAppInfo info : existVirusList){
                    String packageName = info.getPackageName();
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_DELETE);
                    intent.setData(Uri.parse("package:"+packageName));
                    startActivity(intent);
                }

                virusAppInfoList.get(virusAppInfoList.size()-1).setAppName("恶意应用数量:0");
                //显示开始按钮
                showBtnStart();
                //引用杀毒按钮
                hiddenBtnKill();
            }
        }
    };

    /** 停止扫描 */
    private void stopScanVirus() {
        //停止扫描时，把标志设置成false表示在子线程中不再继续扫描
        isRunning = false;
        //将进度条置0
        pb_virus.setProgress(0);
        //停止动画效果
        stopAnim();
        //隐藏停止按钮
        hiddenBtnStop();
        //显示开始按钮
        showBtnStart();
        //清空
        virusAppInfoList.clear();
        existVirusList.clear();
        //刷新列表
        virusAdapter.notifyDataSetChanged();
    }

    /** 开始扫描手机中的app */
    private void startScanVirus() {
        //进度条从0开始
        pb_virus.setProgress(0);
        //开始动画
        startAnim();
        //隐藏开始按钮
        hiddenBtnStart();
        //显示停止按钮
        showBtnStop();
        //标志设为true
        isRunning = true;
        //启动线程
        new KillVirusThread().start();
    }

    /** 扫描完成 */
    private void finishScanVirus() {
        //停止动画
        stopAnim();
        //隐藏停止按钮
        hiddenBtnStop();
        if(existVirusList.size() == 0){ //如果没有病毒
            //显示开始按钮
            showBtnStart();
        }else{  //如果有病毒
            //显示杀毒按钮
            showBtnKill();
        }
    }
}
