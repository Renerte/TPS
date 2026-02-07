package de.shiirroo.tps.helper;

import java.security.SecureRandom;

public class Utilities {

    private static final SecureRandom RNG = new SecureRandom();

    public static char[] randomPassword(int len) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(chars.charAt(RNG.nextInt(chars.length())));
        }
        return sb.toString().toCharArray();
    }

}
