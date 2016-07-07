package edu.csuft.phonesafe.utils;

/**
 * Created by Chalmers on 2016-06-25 14:45.
 * email:qxinhai@yeah.net
 */

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 将密码转换成MD5保存工具类
 */
public class MD5Util {

    /**
     * 将密码转换成MD5保存
     * @param pwd
     * @return
     */
    public static String getMD5FromStringPwd(String pwd){
        String md5Pwd = null;
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] bytes = digest.digest(pwd.getBytes());
            //转换成大整数
            BigInteger bigInteger = new BigInteger(bytes);
            //将大整数转成16进制的字符串
            md5Pwd = bigInteger.toString(16);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return md5Pwd;
    }
}
