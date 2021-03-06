package edu.csuft.phonesafe.view;

/**
 * Created by Chalmers on 2016-07-07 00:34.
 * email:qxinhai@yeah.net
 */

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewDebug.ExportedProperty;
import android.widget.TextView;

/**
 * @author Chalmers
 * @version 2015-12-15 上午12:04:18
 */

/**
 * 跑马灯TextView
 */
public class MarqueeText extends TextView {
    public MarqueeText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public MarqueeText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MarqueeText(Context context) {
        super(context);
    }

    /**
     * 最重要的部分
     * @return true表示获得焦点，即可以一直滚动
     */
    @Override
    @ExportedProperty(category = "focus")
    public boolean isFocused() {
        return true;
    }
}