package com.campus.eventplatform.util;

public class Base62Utils {

    private static final String BASE62 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public static String encode(long value) {
        StringBuilder sb = new StringBuilder();
        do {
            sb.insert(0, BASE62.charAt((int) (value % 62)));
            value /= 62;
        } while (value > 0);
        return sb.toString();
    }

    public static long decode(String base62) {
        long value = 0;
        for (char c : base62.toCharArray()) {
            value = value * 62 + BASE62.indexOf(c);
        }
        return value;
    }
}
