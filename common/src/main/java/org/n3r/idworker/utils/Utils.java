package org.n3r.idworker.utils;

import java.io.File;
import java.util.Calendar;

public class Utils {

    public static final String DOT_ID_WORKERS = ".idworkers";
    static String defaultRange = "0123456789ABCDFGHKMNPRSTWXYZ";

    public static File createIdWorkerHome() {
        String userHome = System.getProperty("user.home");
        File idWorkerHome = new File(userHome + File.separator + DOT_ID_WORKERS);
        idWorkerHome.mkdirs();
        if (idWorkerHome.isDirectory()) return idWorkerHome;

        throw new RuntimeException("failed to create .idworkers at user home");
    }

    public static long decode(String s, String symbols) {
        final int B = symbols.length();
        long num = 0;
        for (char ch : s.toCharArray()) {
            num *= B;
            num += symbols.indexOf(ch);
        }
        return num;
    }

    /*
    获取当前的0时0分0秒-午时
     */
    public static long midnightMillis() {
        Calendar date = Calendar.getInstance();
        date.set(Calendar.HOUR_OF_DAY, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);
        return date.getTimeInMillis();
    }

    public static String encode(long num) {
        return encode(num, defaultRange);
    }

    public static String encode(long num, String symbols) {
        final int B = symbols.length();
        StringBuilder sb = new StringBuilder();
        while (num != 0) {
            sb.append(symbols.charAt((int) (num % B)));
            num /= B;
        }
        return sb.reverse().toString();
    }

    /**
     * 限定长度字符窜，不够填充padChar
     *
     * @param str     传入的字符串
     * @param size    限定长度
     * @param padChar 填充的字符
     * @return 限定长度的字符串
     */
    public static String padLeft(String str, int size, char padChar) {
        if (str.length() >= size) return str;

        StringBuilder s = new StringBuilder();
        for (int i = size - str.length(); i > 0; --i) {
            s.append(padChar);
        }
        s.append(str);

        return s.toString();
    }

}
