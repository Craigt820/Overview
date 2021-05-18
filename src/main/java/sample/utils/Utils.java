package sample.utils;

import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.TimeZone;

public class Utils {

    public static String formatNumber(String num) {
        DecimalFormat formatter;
        if (StringUtils.isNumeric(num)) {
            formatter = new DecimalFormat("#,###");
            return formatter.format(Long.valueOf(num));
        }
        return num;
    }

    public static LocalDateTime formatDateTime(String dateTime) throws ParseException {
        if (!dateTime.isEmpty()) {
            Date date = new Date(LocalDateTime.parse(dateTime.replace(" ", "T")).toInstant(ZoneOffset.MIN).toEpochMilli());
            return LocalDateTime.ofInstant(date.toInstant(), TimeZone.getDefault().toZoneId());
        }
        return null;
    }

    public static String dateTextFormatter(long d, long h, long m, long s) {
        StringBuilder builder = new StringBuilder();
        if (d >= 1) {
            builder.append(d + " Days ");
        }
        if (h >= 1) {
            builder.append(h + " Hour ");
        }

        if (m >= 1) {
            builder.append(m + " Minutes ");
        }

        if (s >= 1) {
            builder.append(s + " Seconds ");
        }

        return builder.toString();
    }

    public final static DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss a");

    public static int booleanToInt(boolean bool) {
        return bool ? 1 : 0;
    }

    public static boolean intToBoolean(int value) {
        return value == 1;
    }

}
