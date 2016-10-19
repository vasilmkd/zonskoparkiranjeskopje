package com.vasilev.zonskoparkiranjeskopje.util;

public final class LicenceUtil {

    public static boolean checkLicence(String licence) {
        if (licence.length() != 7 && licence.length() != 8) {
            return false;
        }
        if (licence.length() == 7) {
            if (!isChar(licence.charAt(0)) || !isChar(licence.charAt(1)) ||
                    !isChar(licence.charAt(5)) || !isChar(licence.charAt(6))) {
                return false;
            }
            if (!isDigit(licence.charAt(2)) || !isDigit(licence.charAt(3)) ||
                    !isDigit(licence.charAt(4))) {
                return false;
            }
        } else {
            if (!isChar(licence.charAt(0)) || !isChar(licence.charAt(1)) ||
                    !isChar(licence.charAt(6)) || !isChar(licence.charAt(7))) {
                return false;
            }
            if (!isDigit(licence.charAt(2)) || !isDigit(licence.charAt(3)) ||
                    !isDigit(licence.charAt(4)) || !isDigit(licence.charAt(5))) {
                return false;
            }
        }
        return true;
    }

    private static boolean isChar(char c) {
        return 'A' <= c && c <= 'Z';
    }

    private static boolean isDigit(char c) {
        return '0' <= c && c <= '9';
    }
}
