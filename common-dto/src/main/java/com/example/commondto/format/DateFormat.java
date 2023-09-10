package com.example.commondto.format;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class DateFormat {
    public static String LocalDateTimeFormat = "yyyy-MM-dd HH:mm:ss.SSSSSS";
    public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");
    public static ChronoUnit truncation = ChronoUnit.MICROS;    // nano seconds removal

    public static LocalDateTime getCurrentTime(){
        return LocalDateTime.now().truncatedTo(truncation);
    }


    public static String formatToSix(LocalDateTime localDateTime){
        return localDateTime.format(DateTimeFormatter.ofPattern(LocalDateTimeFormat));
    }
}
