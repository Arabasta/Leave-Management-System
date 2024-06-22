package com.team4.leaveprocessingsystem.util;

public class StringCleaningUtil {

    public static String forDatabase(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        // Replace all unwanted characters that are not a letter, digit, comma, or space, with an empty string.
        return input.replaceAll("/[^.,a-zA-Z ]/g","").trim();
    }

    public static String forCSV(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        // Add escaped double quotes at front and back, so the string remains in 1 cell later in CSV.
        return "\""+input.trim()+"\"";
    }
}
