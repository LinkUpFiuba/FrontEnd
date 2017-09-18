package linkup.linkup.helper;

import android.content.Context;
import android.text.format.DateUtils;

import java.util.Calendar;
import java.util.Date;

import linkup.linkup.R;


public class DateHelper {

    public static String convertTimeToDateString(Context context, long time, boolean showInsideWeekString, boolean showYear, boolean showMonthShort, boolean showTime, boolean showTimeShort, boolean showWeekday, boolean showWeekdayShort) {
        if (time == -1 || time == 0 || context == null) {
            return "";
        }
        Date date;
        Calendar calendarTime;
        long when;
        int flags;
        if (showInsideWeekString) {
            Calendar todayCal = Calendar.getInstance();
            Calendar yesterdayCal = Calendar.getInstance();
            yesterdayCal.add(Calendar.DAY_OF_YEAR, -1);
            Calendar before2DayCal = Calendar.getInstance();
            before2DayCal.add(Calendar.DAY_OF_YEAR, -2);
            Calendar before3DayCal = Calendar.getInstance();
            before3DayCal.add(Calendar.DAY_OF_YEAR, -3);
            Calendar before4DayCal = Calendar.getInstance();
            before4DayCal.add(Calendar.DAY_OF_YEAR, -4);
            Calendar before5DayCal = Calendar.getInstance();
            before5DayCal.add(Calendar.DAY_OF_YEAR, -5);
            Calendar before6DayCal = Calendar.getInstance();
            before6DayCal.add(Calendar.DAY_OF_YEAR, -6);
            Calendar tomorrowCal = Calendar.getInstance();
            tomorrowCal.add(Calendar.DAY_OF_YEAR, 1);
            date = new Date(1000 * time);
            calendarTime = Calendar.getInstance();
            calendarTime.setTime(date);
            if (calendarTime.get(Calendar.YEAR) == todayCal.get(Calendar.YEAR) && calendarTime.get(Calendar.DAY_OF_YEAR) == todayCal.get(Calendar.DAY_OF_YEAR)) {
                return DateUtils.formatDateTime(context, calendarTime.getTimeInMillis(), 0 | 1);
            }
            String timeString;
            if (calendarTime.get(Calendar.YEAR) == yesterdayCal.get(Calendar.YEAR) && calendarTime.get(Calendar.DAY_OF_YEAR) == yesterdayCal.get(Calendar.DAY_OF_YEAR)) {
                timeString = context.getText(R.string.label_yesterday).toString();
                if (!showTime) {
                    return timeString;
                }
                return timeString + " " + DateUtils.formatDateTime(context, calendarTime.getTimeInMillis(), 0 | 1);
            }
            if ((calendarTime.get(Calendar.YEAR) == before2DayCal.get(Calendar.YEAR) && calendarTime.get(Calendar.DAY_OF_YEAR) == before2DayCal.get(Calendar.DAY_OF_YEAR)) || ((calendarTime.get(Calendar.YEAR) == before3DayCal.get(Calendar.YEAR) && calendarTime.get(Calendar.DAY_OF_YEAR) == before3DayCal.get(Calendar.DAY_OF_YEAR)) || ((calendarTime.get(Calendar.YEAR) == before4DayCal.get(Calendar.YEAR) && calendarTime.get(Calendar.DAY_OF_YEAR) == before4DayCal.get(Calendar.DAY_OF_YEAR)) || ((calendarTime.get(Calendar.YEAR) == before5DayCal.get(Calendar.YEAR) && calendarTime.get(Calendar.YEAR) == before5DayCal.get(Calendar.YEAR)) || (calendarTime.get(Calendar.YEAR) == before6DayCal.get(Calendar.YEAR) && calendarTime.get(Calendar.DAY_OF_YEAR) == before6DayCal.get(Calendar.DAY_OF_YEAR)))))) {
                when = calendarTime.getTimeInMillis();
                flags = 0 | 2;
                if (showTime) {
                    flags |= 1;
                }
                return DateUtils.formatDateTime(context, when, flags);
            }
        }
        date = new Date(1000 * time);
        calendarTime = Calendar.getInstance();
        calendarTime.setTime(date);
        when = calendarTime.getTimeInMillis();
        flags = 0 | 16;
        if (showYear) {
            flags |= 4;
        }
        if (showMonthShort) {
            flags |= 65536;
        }
        if (showTime) {
            flags |= 1;
        }
        if (showTimeShort) {
            flags |= 16384;
        }
        if (showWeekday) {
            flags |= 2;
        }
        if (showWeekdayShort) {
            flags |= 32768;
        }
        return DateUtils.formatDateTime(context, when, flags);
    }
}
