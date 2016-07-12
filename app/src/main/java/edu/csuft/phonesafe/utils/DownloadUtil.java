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
public class DownloadUtil {

    /**
     * 下载更新的apk文件
     * @param apkUrl apk的url
     * @return File对象
     */
    public static void getUpdateApk(final String apkUrl, final UpdateUtil helper, final ProgressDialog progressDialog){
        IDownload iDownload = helper;

        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    File file = new File(Environment.getExternalStorageDirectory().toString() + File.separatorChar + "downloads",
                            getFilename(apkUrl));
                    URL url = new URL(apkUrl);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setReadTimeout(5000);
                    conn.setConnectTimeout(5000);
                    if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){
                        progressDialog.setMax(conn.getContentLength());
                        int num = 0;

                        InputStream is = conn.getInputStream();
                        byte[] buf = new byte[1024];
                        int size = -1;
                        FileOutputStream fos = new FileOutputStream(file);
                        while((size = is.read(buf)) != -1){
                            fos.write(buf,0,size);
                            num += size;
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

    public interface IDownload {
        void downloadComplete(File file);
    }

}
