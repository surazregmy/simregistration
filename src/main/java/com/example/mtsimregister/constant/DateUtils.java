package com.example.mtsimregister.constant;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateUtils {
    public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static LocalDate getDateFromString(String string) {
        try {
            return LocalDate.parse(string, formatter);
        } catch (DateTimeParseException e) {
            System.out.println("Date format is not valid");
            return null;
        }
    }

}

