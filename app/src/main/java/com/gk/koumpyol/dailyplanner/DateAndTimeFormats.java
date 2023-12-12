package com.gk.koumpyol.dailyplanner;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class DateAndTimeFormats
{
    public static DateTimeFormatter formatterForDate()
    {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");
        return formatter;
    }

    public static DateTimeFormatter formatterForTime()
    {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return formatter;
    }

    public static String formattedDate(LocalDate date)
    {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");
        return date.format(formatter);
    }

    public static String formattedTime(LocalTime time)
    {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return time.format(formatter);
    }

    public static long localDateToLong(LocalDate date)
    {
        LocalDateTime dateTime = date.atTime(LocalTime.MIDNIGHT);
        long millis = dateTime.toInstant(ZoneOffset.UTC).toEpochMilli();
        return millis;
    }
}
