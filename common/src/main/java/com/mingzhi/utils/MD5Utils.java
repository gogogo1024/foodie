package com.mingzhi.utils;

import org.apache.commons.codec.binary.Base64;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Utils {
    public static String getMD5Str(String strValue) throws NoSuchAlgorithmException {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        return Base64.encodeBase64String(md5.digest(strValue.getBytes()));
    }

    public static void main(String[] args) {
        try {
            String md5 = getMD5Str("mingzhi");
            System.out.println(md5);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
