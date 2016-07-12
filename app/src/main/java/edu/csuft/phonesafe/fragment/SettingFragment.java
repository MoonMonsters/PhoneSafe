package edu.csuft.phonesafe.fragment;


import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.text.format.Formatter;
import android.widget.Toast;

import java.io.File;

import edu.csuft.phonesafe.R;

/**
 * A simple {@link Fragment} subclass.
 */

/**
 * 设置中心界面
 */
public class SettingFragment extends PreferenceFragment {

    Preference key_preference_clear_cache;

    //缓存大小
    private long totalCacheSize = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.setting_preference);

        key_preference_clear_cache = findPreference("key_preference_clear_cache");
        key_preference_clear_cache.setOnPreferenceClickListener(onPreferenceClickListener);
    }

    private void clearAppCache() {

        totalCacheSize = 0;
        //缓存文件夹
        File cacheDir = getActivity().getCacheDir();
        //代码缓存文件夹
        File codeCacheDir = getActivity().getCodeCacheDir();
        //外部存储缓存文件夹
        File externalCacheDir = getActivity().getExternalCacheDir();

        clearFile(cacheDir);
        clearFile(codeCacheDir);
        clearFile(externalCacheDir);

        //弹出提示
        Toast.makeText(getActivity(), "清楚缓存数据:" + Formatter.formatFileSize(getActivity(),
                totalCacheSize), Toast.LENGTH_SHORT).show();
    }

    /**
     * 删除缓存文件夹中的文件
     *
     * @param file 文件
     */
    private void clearFile(File file) {
        //如果是文件，则直接删除
        if (file.isFile()) {
            //累加大小
            totalCacheSize += file.length();
            file.delete();
        } else if (file.isDirectory()) {    //文件夹
            //空文件夹直接删除
            if (file.listFiles() == null || file.listFiles().length == 0) {
                totalCacheSize += file.length();
                file.delete();
            } else {
                //递归
                for (File f : file.listFiles()) {
                    clearFile(f);
                }
            }
        }
    }

    /**
     * 点击事件
     */
    Preference.OnPreferenceClickListener onPreferenceClickListener = new Preference.OnPreferenceClickListener() {
        @Override
        public boolean onPreferenceClick(Preference preference) {
            clearAppCache();
            return true;
        }
    };
}
