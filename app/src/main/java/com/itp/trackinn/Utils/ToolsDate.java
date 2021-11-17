package com.itp.trackinn.Utils;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

public class ToolsDate {


    public static String getFormattedDateSimple(Long dateTime) {
        SimpleDateFormat newFormat = new SimpleDateFormat("MM-dd-yyyy", Locale.getDefault());
        return newFormat.format(new Date(dateTime));
    }

    public static String getFormattedDateSimple(Long dateTime, String format, String timeZone) {
        SimpleDateFormat newFormat = new SimpleDateFormat(format, Locale.getDefault());
        if(timeZone != null && !"".equals(timeZone)){
            newFormat.setTimeZone(TimeZone.getTimeZone(timeZone));
        }
        return newFormat.format(new Date(dateTime));
    }


    public static Calendar getCalendarFromTimeInMillis(Long timeStamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeStamp);
        return calendar;
    }

    public static Calendar getCalendarFromDateStr(String dateStr){
        try{
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date date = formatter.parse(dateStr);
            Calendar myCal = new GregorianCalendar();
            myCal.setTime(date);
            return myCal;
        }catch (ParseException e){
            return null;
        }
    }


}
