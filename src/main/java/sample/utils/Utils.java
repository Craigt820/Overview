package sample.utils;

import org.apache.commons.lang3.StringUtils;

import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;

public class Utils {

    public static String formatNumber(String num) {
        DecimalFormat formatter;
        if (StringUtils.isNumeric(num)) {
            formatter = new DecimalFormat("#,###");
            return formatter.format(Long.valueOf(num));
        }
        return num;
    }

    public final static DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss a");

    public static int booleanToInt(boolean bool) {
        return bool ? 1 : 0;
    }

    public static boolean intToBoolean(int value) {
        return value == 1;
    }

}
