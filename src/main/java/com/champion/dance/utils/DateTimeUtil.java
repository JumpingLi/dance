package com.champion.dance.utils;

import java.sql.Date;
import java.time.*;
import java.time.format.DateTimeFormatter;

/**
 * Created with dance
 * Author: jiangping.li
 * Date: 2018/2/26 16:47
 * Description：
 */
public class DateTimeUtil {
    public static LocalDate parseDate(String text){
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return LocalDate.parse(text,df);
    }
    public static long dateDifferenceSeconds(LocalDateTime begin,LocalDateTime end){
        return Duration.between(begin,end).toMillis()/1000;
    }

    public static long dateDifferenceMinutes(LocalTime begin,LocalTime end){
        return Duration.between(begin,end).toMillis()/600000;
    }
    public static LocalTime transferSqlDatToLocalTime(Date date){
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()).toLocalTime();
    }
    public static String formatLocalTime(LocalTime localTime){
        return localTime.format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    public static String formatLocalDate(LocalDate localDate){
        return localDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
}
