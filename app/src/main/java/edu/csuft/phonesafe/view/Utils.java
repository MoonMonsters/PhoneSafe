package edu.csuft.phonesafe.view;

import android.content.res.Resources;

/**
 * Created by bruce on 14-11-6.
 */

/**
 * 转化单位的工具类
 */
public class Utils {
    public static float dp2px(Resources resources, float dp) {
        final float scale = resources.getDisplayMetrics().density;
        return  dp * scale + 0.5f;
    }

    public static float sp2px(Resources resources, float sp){
        final float scale = resources.getDisplayMetrics().scaledDensity;
        return sp * scale;
    }
}
