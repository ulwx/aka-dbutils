package com.github.ulwx.aka.dbutils.tool.support;

import java.time.*;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateTime implements Cloneable {

    private Calendar date = null;

    public DateTime() {
        TimeZone tz = TimeZone.getTimeZone("GMT+8:00");
        date = Calendar.getInstance(tz);
    }

    /**
     * @param dt yyyy-MM-dd HH:mm:ss
     */
    public DateTime(Date dt) {
        this();
        date.setTime(dt);
    }

    public DateTime(String datetime) {
        this();
        Date d = CTime.parseWholeDate(datetime);
        date.setTime(d);
    }

    public DateTime(int year, int month, int day, int hourOfDay, int minute,
                    int second) {
        this();
        this.date.set(year, month - 1, day, hourOfDay, minute, second);
    }

    public DateTime(int year, int month, int day) {
        this(year, month, day, 0, 0, 0);

    }

    public int getDay() {
        return this.date.get(Calendar.DAY_OF_MONTH);
    }

    public DateTime setDay(int day) {
        this.date.set(Calendar.DAY_OF_MONTH, day);
        return this;
    }

    public int getYear() {

        return this.date.get(Calendar.YEAR);
    }

    public void setYear(int year) {
        this.date.set(Calendar.YEAR, year);
    }

    public int getMonth() {
        return this.date.get(Calendar.MONTH) + 1;
    }

    public DateTime setMonth(int month) {
        this.date.set(Calendar.MONTH, month - 1);
        return this;
    }

    public int getHours() {
        return this.date.get(Calendar.HOUR_OF_DAY);
    }

    public DateTime setHours(int hour) {
        this.date.set(Calendar.HOUR_OF_DAY, hour);
        return this;
    }

    public int getSeconds() {
        return this.date.get(Calendar.SECOND);
    }

    public DateTime setSeconds(int seconds) {
        this.date.set(Calendar.SECOND, seconds);
        return this;
    }

    public int getMinutes() {
        return this.date.get(Calendar.MINUTE);
    }

    public DateTime setMinutes(int minutes) {
        this.date.set(Calendar.MINUTE, minutes);
        return this;
    }

    public Date getDate() {
        return this.date.getTime();
    }

    public DateTime addDays(int i) {
        date.add(Calendar.DAY_OF_MONTH, i);
        return this;
    }

    public DateTime addMonths(int i) {
        date.add(Calendar.MONTH, i);
        return this;
    }

    public DateTime addYears(int i) {
        date.add(Calendar.YEAR, i);
        return this;
    }

    public DateTime addHours(int i) {
        date.add(Calendar.HOUR_OF_DAY, i);
        return this;
    }

    public DateTime addMinutes(int i) {
        date.add(Calendar.MINUTE, i);
        return this;
    }

    public DateTime addSeconds(int i) {
        date.add(Calendar.SECOND, i);
        return this;
    }

    public long getTimeInMillis() {
        return this.date.getTimeInMillis();
    }

    public String toString() {
        return CTime.formatWholeDate(date.getTime());
    }

    public static Date getNowDate() {
        return (new DateTime()).getDate();
    }

    public DateTime setToLastDayInMonth() {
        this.date.set(Calendar.DAY_OF_MONTH,
                this.date.getActualMaximum(Calendar.DAY_OF_MONTH));
        return this;

    }


    // 01. java.util.Date --> java.time.LocalDateTime
    public LocalDateTime DateToLocalDateTime() {
        Date date = new Date();
        Instant instant = date.toInstant();
        ZoneId zone = ZoneId.systemDefault();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zone);
        return localDateTime;
    }

    // 02. java.util.Date --> java.time.LocalDate
    public LocalDate DateToLocalDate() {
        Date date = new Date();
        Instant instant = date.toInstant();
        ZoneId zone = ZoneId.systemDefault();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zone);
        LocalDate localDate = localDateTime.toLocalDate();
        return localDate;
    }

    // 03. java.util.Date --> java.time.LocalTime
    public LocalTime DateToLocalTime() {
        Date date = new Date();
        Instant instant = date.toInstant();
        ZoneId zone = ZoneId.systemDefault();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zone);
        LocalTime localTime = localDateTime.toLocalTime();
        return localTime;
    }


    // 04. java.time.LocalDateTime --> java.util.Date
    public Date LocalDateTimeToDate() {
        LocalDateTime localDateTime = LocalDateTime.now();
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = localDateTime.atZone(zone).toInstant();
        Date date = Date.from(instant);
        return date;
    }


    // 05. java.time.LocalDate --> java.util.Date
    public Date LocalDateToDate() {
        LocalDate localDate = LocalDate.now();
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = localDate.atStartOfDay().atZone(zone).toInstant();
        Date date = Date.from(instant);
        return date;
    }

    // 06. java.time.LocalTime --> java.util.Date
    public Date LocalTimeToDate() {
        LocalTime localTime = LocalTime.now();
        LocalDate localDate = LocalDate.now();
        LocalDateTime localDateTime = LocalDateTime.of(localDate, localTime);
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = localDateTime.atZone(zone).toInstant();
        Date date = Date.from(instant);
        return date;
    }

    /**
     * 比较两个日期是否在同一月份
     *
     * @param dt
     * @return
     */
    public boolean equalWithSameMonth(DateTime dt) {
        if (this.getYear() == dt.getYear() && this.getMonth() == dt.getMonth()) {
            return true;
        }
        return false;
    }

    /**
     * 比较两个日期是否在同一天
     *
     * @param dt
     * @return
     */
    public boolean equalWithSameDay(DateTime dt) {
        if (this.getYear() == dt.getYear() && this.getMonth() == dt.getMonth()
                && this.getDay() == dt.getDay()) {
            return true;
        }
        return false;
    }

    /**
     * 比较两个日期是否相等
     *
     * @param dt
     * @return
     */
    public boolean equals(DateTime dt) {
        if (this.getTimeInMillis() == dt.getTimeInMillis())
            return true;
        return false;
    }

    public Date getDateTime() {
        return date.getTime();
    }

    public String toString(String format) {
        return CTime.formatDate(this.getDateTime(), format);
    }

    protected DateTime clone() throws CloneNotSupportedException {
        // TODO Auto-generated method stub
        DateTime dt = (DateTime) super.clone();
        dt.date = (Calendar) this.date.clone();
        return dt;
    }

    public static void main(String[] args) throws Exception {
        DateTime dt = new DateTime();
        DateTime dt2 = new DateTime();
        dt2.addHours(-2);
        dt2.setMinutes(59);
        dt2.setSeconds(59);
        System.out.println(CTime.formatWholeDate(dt.getDate()));
        DateTime[] luckyNumbers = {dt, dt2};
        DateTime[] sss = luckyNumbers.clone();
        sss[0].addHours(2);

        DateTime dt3 = dt.clone();
        dt3.addHours(5);
        System.out.println(CTime.formatWholeDate(dt.getDate()));

        // System.out.println(CTime.formatWholeDate(dt2.getDate()));
        // DateTime dtt=(DateTime)dt.clone();

    }

}