package com.project01.common.core.utils;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

public class DateUtils {
    public static String FORMAT_DDMMYYYY = "dd/MM/yyyy";
    public static String FORMAT_YYYYMMDD = "yyyyMMdd";

    public static Date currentDate() {
        return new Date(System.currentTimeMillis());
    }

    public static String date2Str(Date date) {
        if (date == null) {
            return null;
        } else {
            String format = "yyyy-MM-dd";
            DateFormat dateFormat = new SimpleDateFormat(format);
            return dateFormat.format(date);
        }
    }

    public static String date2StrFormat(Date date, String format) {
        DateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(date);
    }

    public static String date2StrFormat(Timestamp date, String format) {
        DateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(date);
    }

    public static Date str2Date(String dateStr) throws ParseException {
        String format = "yyyy-MM-dd";
        return (new SimpleDateFormat(format)).parse(dateStr);
    }

    public static Date str2DateFormat(String dateStr, String format) throws ParseException {
        return (new SimpleDateFormat(format)).parse(dateStr);
    }

    public static Calendar calendarFor(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(1, year);
        cal.set(2, month);
        cal.set(5, day);
        return cal;
    }

    public static int getYear() {
        Calendar cal = Calendar.getInstance();
        return cal.get(1);
    }

    public static int getYear(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(1);
    }

    public static Date plusDate(Date startDate, int date) {
        if (startDate == null) {
            startDate = new Date();
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(startDate);
        cal.add(5, date);
        return cal.getTime();
    }

    public static Date plusMinute(Date startDate, int minute) {
        if (startDate == null) {
            startDate = new Date();
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(startDate);
        cal.add(12, minute);
        return cal.getTime();
    }

    public static Date plusMonth(Date startDate, int month) {
        if (startDate == null) {
            startDate = new Date();
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(startDate);
        cal.add(2, month);
        return cal.getTime();
    }

    public static Calendar getLastDayPreviousMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int month = cal.get(2);
        int year = cal.get(1);
        int day = 1;
        Calendar lastMonthCal = calendarFor(year, month, day);
        lastMonthCal.add(5, -1);
        return lastMonthCal;
    }

    public static String getMonthVn(int month) {
        return month < 10 ? "0" + month : "" + month;
    }

    public static Date CalendarToDate(Calendar cal) {
        Date date = cal.getTime();
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
        String date1 = format1.format(date);
        Date inActiveDate = null;

        try {
            inActiveDate = format1.parse(date1);
        } catch (ParseException e1) {
            e1.printStackTrace();
        }

        return inActiveDate;
    }

    public static String CalendarToString(Calendar cal) {
        Date date = cal.getTime();
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
        return format1.format(date);
    }

    public static Date ngayDauNam(Integer nam) {
        return CalendarToDate(calendarFor(nam, 0, 1));
    }

    public static LocalDate convertToLocalDate(Date dateToConvert) {
        return dateToConvert.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public static Integer subtractDate(Date date1, Date date2) {
        LocalDate localDate1 = convertToLocalDate(date1);
        LocalDate localDate2 = convertToLocalDate(date2);
        Period period = Period.between(localDate2, localDate1);
        return period.getDays();
    }

    public static Integer subtract(Date dt1, Date dt2) {
        long diff = Math.abs(dt1.getTime() - dt2.getTime());
        long diffDays = diff / 86400000L;
        return Integer.valueOf(String.valueOf(diffDays));
    }

    public static Long minusDate(Date date1, Date date2) {
        return date1 != null && date2 != null ? TimeUnit.DAYS.convert(date1.getTime() - date2.getTime(), TimeUnit.MILLISECONDS) : null;
    }

    public static XMLGregorianCalendar getXMLGregorianCalendar(String date, String format) throws ParseException, DatatypeConfigurationException {
        DateFormat dformat = new SimpleDateFormat(format);
        Date dDate = dformat.parse(date);
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(dDate);
        XMLGregorianCalendar xmlGregCal = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
        return xmlGregCal;
    }

    public static Date convertStringToDate(String strDate, String pattern) {
        if (CommonUtil.isNullOrEmpty(strDate)) {
            return null;
        } else {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
                return dateFormat.parse(strDate);
            } catch (ParseException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    public static int calculateAge(Date birthDate) {
        if (birthDate == null) {
            return 0;
        } else {
            LocalDate currentDate = convertToLocalDate(new Date());
            LocalDate birthday = convertToLocalDate(birthDate);
            return Period.between(birthday, currentDate).getYears();
        }
    }

    public static Date getDateWithoutTime(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(11, 0);
        calendar.set(12, 0);
        calendar.set(13, 0);
        calendar.set(14, 0);
        return calendar.getTime();
    }

    public static String date2StringByPattern(Date date, String pattern) {
        if (date != null && !CommonUtil.isNullOrEmpty(pattern)) {
            DateFormat df = new SimpleDateFormat(pattern);
            return df.format(date);
        } else {
            return null;
        }
    }

    public static Timestamp getStartTimeOfTheDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(11, 0);
        calendar.set(12, 0);
        calendar.set(13, 0);
        calendar.set(14, 0);
        return new Timestamp(calendar.getTime().getTime());
    }

    public static Timestamp getEndTimeOfTheDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(11, 23);
        calendar.set(12, 59);
        calendar.set(13, 59);
        calendar.set(14, 0);
        return new Timestamp(calendar.getTime().getTime());
    }

    public static String getStringDateByFormat(Date date, String format) {
        if (date == null) {
            return null;
        } else {
            SimpleDateFormat formatter = new SimpleDateFormat(format);
            return formatter.format(date);
        }
    }

    public static Timestamp plusMonth(Timestamp date, int months) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(2, months);
        return new Timestamp(cal.getTime().getTime());
    }

    public static Timestamp plusDay(Timestamp date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(5, days);
        return new Timestamp(cal.getTime().getTime());
    }

    public static Timestamp plusYear(Timestamp date, int years) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(1, years);
        return new Timestamp(cal.getTime().getTime());
    }

    public static XMLGregorianCalendar toXmlGregorianCalendar(final Date date) {
        return toXmlGregorianCalendar(date.getTime());
    }

    public static XMLGregorianCalendar toXmlGregorianCalendar(final Timestamp date) {
        return toXmlGregorianCalendar(date.getTime());
    }

    public static XMLGregorianCalendar toXmlGregorianCalendar(final long date) {
        try {
            GregorianCalendar calendar = new GregorianCalendar();
            calendar.setTimeInMillis(date);
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);
        } catch (DatatypeConfigurationException var3) {
            System.out.println("Unable to convert date '%s' to an XMLGregorianCalendar object");
            return null;
        }
    }

    public static Timestamp toTimestamp(java.sql.Date date) {
        return new Timestamp(date.getTime());
    }

    public static Timestamp toTimestamp(Date date) {
        return new Timestamp(date.getTime());
    }
}
