package edu.csuft.phonesafe.utils;

import android.app.ProgressDialog;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Chalmers on 2016-06-19 15:56.
 * email:qxinhai@yeah.net
 */

/**
 * 下载文件的工具类
 */
public class DownloadUtil {

    /**
     * 下载更新的apk文件
     * @param apkUrl apk的url
     * @return File对象
     */
    public static void getUpdateApk(final String apkUrl, final UpdateUtil helper, final ProgressDialog progressDialog){
        //接口对象
        IDownload iDownload = helper;

        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    //下载文件存放位置
                    File file = new File(Environment.getExternalStorageDirectory().toString() + File.separatorChar + "downloads",
                            getFilename(apkUrl));
                    //联网url
                    URL url = new URL(apkUrl);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    //读取超时
                    conn.setReadTimeout(5000);
                    //连接超时
                    conn.setConnectTimeout(5000);
                    //返回码
                    if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){
                        //设置进度条的值
                        progressDialog.setMax(conn.getContentLength());
                        int num = 0;

                        InputStream is = conn.getInputStream();
                        byte[] buf = new byte[1024];
                        int size = -1;
                        FileOutputStream fos = new FileOutputStream(file);
                        //下载数据
                        while((size = is.read(buf)) != -1){
                            fos.write(buf,0,size);
                            num += size;
                            //更新进度条的值
                            progressDialog.setProgress(num);
                        }
                        fos.flush();
                        fos.close();
                        is.close();
                        //隐藏下载框
                        progressDialog.dismiss();

                        /** 回调方法，结束下载程序 */
                        helper.downloadComplete(file);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 获得文件名
     * @param apkUrl url
     * @return 文件名
     */
    private static String getFilename(String apkUrl){
        int index = apkUrl.lastIndexOf('/');

        return apkUrl.substring(index+1);
    }

    /** 接口，通过回调方法调用 */
    public interface IDownload {
        void downloadComplete(File file);
    }

}
