package com.imooc.utils;

import org.apache.commons.codec.binary.Base64;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author 1819014975
 * @Title: MD5Utils
 * @ProjectName imooc-videos-dev
 * @Description: TODO
 * @date 2018/10/9 16:20
 */
public class MD5Utils {

    public static String getMD5Str(String strValue) throws Exception {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        return Base64.encodeBase64String(md5.digest(strValue.getBytes()));
    }

//    public static void main(String[] arge) {
//        System.out.println(getMD5Str("你好"));
//    }
}
