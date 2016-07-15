package edu.csuft.phonesafe.utils;

import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;

import edu.csuft.phonesafe.bean.UpdateInfo;

/**
 * Created by Chalmers on 2016-06-19 13:39.
 * email:qxinhai@yeah.net
 */

/**
 * 解析xml数据，使用pull解析
 */
public class XmlParseUtil {

    /**
     * 解析网络接收的字节流，装换成UpdateInfo对象
     * @param inputStream 字节流
     * @return UpdateInfo对象
     */
    public static UpdateInfo parseXmlData(InputStream inputStream) throws XmlPullParserException, IOException {
        UpdateInfo info = new UpdateInfo();

        XmlPullParser parser = Xml.newPullParser();
        parser.setInput(inputStream,"utf-8");

        int type = parser.getEventType();
        //如果type不是结束标志，就一直遍历
        while(type != XmlPullParser.END_DOCUMENT){

            switch (type){
                case XmlPullParser.START_TAG:   //开始标签
                    if("version".equals(parser.getName())){
                        info.setVersion(parser.nextText()); //直接取得标签对中的数据
                        Log.i("TAG",info.getVersion());
                    }else if("description".equals(parser.getName())){
                        info.setDescription(parser.nextText());
                    }else if("apkurl".equals(parser.getName())){
                        info.setApkurl(parser.nextText());
                    }
                    break;
            }

            //遍历下一行
            type = parser.next();
        }

        //返回对象
        return info;
    }
}
