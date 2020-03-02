package com.aprouxdev.mabibliotheque.tools.general;

import com.aprouxdev.mabibliotheque.R;
import com.aprouxdev.mabibliotheque.util.Constants;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Tools {

    public static String createNewId() {
       String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
       StringBuilder autoId = new StringBuilder();
        for (int i = 0; i < 20; i++) {
            autoId.append(chars.charAt((int) Math.floor(Math.random() * chars.length())));
        }
        return String.valueOf(autoId);
    }


    public static String convertDateToHour(Date date){
        DateFormat dfTime = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return dfTime.format(date);
    }

    /**
     * If
     *  the converted date is today
     *      return Hours:Minutes
     * else return "Yesterday"
     * or Date dd-MM-yyyy
     * @param currentDate Date today
     * @param date Date convertedDate
     * @return
     */
    public static String convertDateToFormattedDate(Date currentDate, Date date){
        DateFormat dateFormatDay = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        // Check if it's the same Date
        if (dateFormatDay.format(currentDate).equals(dateFormatDay.format(date))){
           return convertDateToHour(date);
        } else if (Integer.parseInt(dateFormatDay.format(currentDate)) - Integer.parseInt(dateFormatDay.format(date)) == 1) {
            return Constants.YESTERDAY;
        } else {
            DateFormat dateDayFormatted = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            return  dateDayFormatted.format(date);
        }
    }
}
