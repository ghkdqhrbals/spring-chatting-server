package com.example.commondto.format;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class DateFormat {
    public static String LocalDateTimeFormat = "yyyy-MM-dd HH:mm:ss.SSSSSS";
    public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");
    public static ChronoUnit truncation = ChronoUnit.NANOS;    // still has nano-second

    public static LocalDateTime getCurrentTime(){
        LocalDateTime localDateTime = LocalDateTime.now().truncatedTo(truncation);
        return localDateTime;
    }


    public static String formatToSix(LocalDateTime localDateTime){
        String format = localDateTime.format(DateTimeFormatter.ofPattern(LocalDateTimeFormat));
        return format;
    }
}
