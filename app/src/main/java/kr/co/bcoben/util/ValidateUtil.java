package kr.co.bcoben.util;

import java.util.Calendar;
import java.util.Locale;
import java.util.regex.Pattern;

public class ValidateUtil {

    public enum StringPattern {
        ALPHA_NUM, ALPHA_NUM_SPECIAL, CASE_ALPHA_NUM, CASE_ALPHA_NUM_SPECIAL
    }
    public static boolean stringPatternCheck(String str, StringPattern pattern, int minLen, int maxLen) {
        if (minLen < 0) {
            throw new IllegalArgumentException("minLen is greater than 0");
        }
        if (maxLen < -1) {
            throw new IllegalArgumentException("maxLen is greater than -1");
        }
        if (maxLen > -1 && minLen > maxLen) {
            throw new IllegalArgumentException("minLen is greater than maxLen");
        }

        String regex = "";
        switch (pattern) {
            case ALPHA_NUM:					// 영문, 숫자 포함
                regex = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d$@$!%*#?&]{"+minLen+","+(maxLen == -1 ? "" : maxLen)+"}$";
                break;
            case ALPHA_NUM_SPECIAL:			// 영문, 숫자, 특수문자 포함
                regex = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[$@$!%*#?&])[A-Za-z\\d$@$!%*#?&]{"+minLen+","+(maxLen == -1 ? "" : maxLen)+"}$";
                break;
            case CASE_ALPHA_NUM:			// 영문 대,소문자, 숫자 포함
                regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[A-Za-z\\d$@$!%*#?&]{"+minLen+","+(maxLen == -1 ? "" : maxLen)+"}$";
                break;
            case CASE_ALPHA_NUM_SPECIAL:	// 영문 대,소문자, 숫자, 특수문자 포함
                regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[$@$!%*?&])[A-Za-z\\d$@$!%*?&]{"+minLen+","+(maxLen == -1 ? "" : maxLen)+"}$";
                break;
        }
        return Pattern.matches(regex, str);
    }

    public static boolean emailCheck(String in) {
        String regex = "^[_a-z0-9-]+(.[_a-z0-9-]+)*@(?:\\w+\\.)+\\w+$";
        return Pattern.matches(regex, in);
    }

    public static boolean phoneCheck(String in) {
        String regex = "^01(?:0|1|[6-9])(\\d{3}|\\d{4})(\\d{4})$";
        return Pattern.matches(regex, in);
    }

    public static boolean dateCheck(String in) {
        in = in.replaceAll("\\.", "").replaceAll("-", "").replaceAll("/", "");
        if (in.length() != 8 || !intCheck(in)) {
            return false;
        }

        int year = Integer.parseInt(in.substring(0, 4));
        int month = Integer.parseInt(in.substring(4, 6));
        int date = Integer.parseInt(in.substring(6, 8));

        Calendar birth = Calendar.getInstance(Locale.getDefault());
        birth.set(year, month - 1, date);
        return birth.get(Calendar.YEAR) == year && birth.get(Calendar.MONTH) == month - 1 && birth.get(Calendar.DAY_OF_MONTH) == date;
    }


    public static boolean doubleCheck(String in) {
        try {
            Double value = Double.parseDouble(in);
            return true;
        } catch(Exception e) {
            return false;
        }
    }

    public static boolean intCheck(String in) {
        try {
            Integer value = Integer.parseInt(in);
            return true;
        } catch(Exception e) {
            return false;
        }
    }
}
