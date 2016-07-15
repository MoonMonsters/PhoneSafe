package edu.csuft.phonesafe.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import edu.csuft.phonesafe.activity.MainActivity;
import edu.csuft.phonesafe.bean.UpdateInfo;

/**
 * Created by Chalmers on 2016-06-19 13:29.
 * email:qxinhai@yeah.net
 */

/**
 * 更新程序工具类
 */
public class UpdateUtil implements DownloadUtil.IDownload{
    private Context context = null;
    private static UpdateUtil updateUtil = null;

    /** 需要更新 */
    private final int DO_UPDATE = 1;
    /** 不需要更新 */
    private final int NOT_UPDATE = 0;
    /** 网络错误 */
    private final int NET_ERROR = 2;
    /**安装下载更新的apk文件*/
    private final int INSTALL_APK = 3;

    private UpdateInfo updateInfo = null;

    private ProgressDialog progressDialog = null;

    public UpdateUtil(Context context) {
        this.context = context;
    }

    /**
     * 实例化，单例模式
     *
     * @param context 上下文对象
     * @return LoginHelper对象
     */
    public static UpdateUtil getInstance(Context context) {
        if (updateUtil == null) {
            updateUtil = new UpdateUtil(context);
        }

        return updateUtil;
    }

    /**
     * 用来处理主线程UI
     */
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case DO_UPDATE: //更新
                    //弹出对话框，是否需要更新
                    showDialogWhetherUpdate();
                    break;
                case NOT_UPDATE:   //不更新
                    enterMainActivity();
                    break;
                case NET_ERROR:     //网络问题
                    Toast.makeText(context,"网络错误",Toast.LENGTH_SHORT).show();
                    //但也需要进入主界面
                    enterMainActivity();
                    break;
                case INSTALL_APK:       //安装更新文件
                    File file = (File) msg.obj;
                    installUpdateApk(file);
                    break;
            }
        }
    };

    /**
     * 进入MainActivity
     */
    private void enterMainActivity() {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
        ((Activity)context).finish();

        //进入主界面后，销毁该UpdateHelper对象
        destroyUpdateHelperInstance();
    }

    /**
     * 安装apk文件
     * @param file 下载apk文件
     */
    private void installUpdateApk(File file){
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        //设置数据和类型，该处不能拆成两个方法使用
        //file表示要安装的程序
        intent.setDataAndType(Uri.fromFile(file),"application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    /**
     * 弹出对话框来选择是否更新
     */
    private void showDialogWhetherUpdate(){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("更新提示")
                .setMessage(updateInfo.getDescription())
                .setPositiveButton("更新",onClickListener)
                .setNegativeButton("取消",onClickListener)
                .setCancelable(false)
                .create()
                .show();
    }

    /**
     * 销毁LoginHelper对象
     * 因为它是static变量，第一次启动没问题，但第二次启动会报错
     */
    private void destroyUpdateHelperInstance(){
        updateUtil = null;
    }

    /**
     * 获得更新信息
     */
    public void getUpdateInfo() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //Message对象，发送消息到Handler，在主线程中更新数据
                Message msg = handler.obtainMessage();
                try {
                    /** 从Tomcat服务器中接收数据，判断是否需要更新软件 */
                    URL url = new URL(Config.UPDATE_URL);
                    Log.i("TAG",Config.UPDATE_URL);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setReadTimeout(5000);
                    connection.setConnectTimeout(5000);
                    if (connection.getResponseCode() == 200) {
                        InputStream inputStream = connection.getInputStream();

                        //解析数据
                        updateInfo = XmlParseUtil.parseXmlData(inputStream);
                        //如果版本相同，则需要更新
                        if (updateInfo.getVersion().equals(ViewUtil.getAppVersion(context))) { //如果版本一样，就不需要更新
                            msg.what = NOT_UPDATE;
                        } else {
                            msg.what = DO_UPDATE;
                        }
                    }else{  //连接网络失败
                        msg.what = NOT_UPDATE;
                    }
                } catch (Exception e) {
                    //网络连接失败（无网络状态），不需要更新
                    msg.what = NOT_UPDATE;
                    Log.i("TAG","网络连接错误...");
                }

                //发送消息
                handler.sendMessage(msg);
            }
        }).start();
    }

    /**
     * 对话框的点击事件
     */
    DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            //如果点击了更新，那么便开始下载，并且显示下载进度条
            if(which == DialogInterface.BUTTON_POSITIVE){   //更新
                progressDialog = new ProgressDialog(context);
                setProgressDialog(progressDialog);
                //下载文件
                DownloadUtil.getUpdateApk(updateInfo.getApkurl(), updateUtil,progressDialog);
            } else if (which == DialogInterface.BUTTON_NEGATIVE) {  //不更新
                Message msg = handler.obtainMessage();
                msg.what = NOT_UPDATE;
                handler.sendMessage(msg);
            }
        }
    };

    /**
     * 显示进度条对话框
     * @param dialog
     */
    private void setProgressDialog(ProgressDialog dialog){
        //不可取消
        dialog.setCancelable(false);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.show();
    }

    /**
     * 接口，回调方法，当在DownloadHelper中文件下载完成后，会调用此方法
     * @param file 下载完成后文件对象
     */
    @Override
    public void downloadComplete(File file) {
        Message msg = handler.obtainMessage();
        if(file == null){   //如果文件为null，则说明网络有问题
            msg.what = NET_ERROR;
        }else{      //否则表示下载成功，则安装更新文件
            msg.what = INSTALL_APK;
            msg.obj = file;
        }

        //发送消息
        handler.sendMessage(msg);
    }
}