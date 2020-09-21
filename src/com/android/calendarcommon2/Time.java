/*
 * Copyright (C) 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.android.calendarcommon2;

/**
 * Helper class to make migration out of android.text.format.Time smoother.
 */
public class Time {

    public static final String TIMEZONE_UTC = "UTC";

    /*
     * Define symbolic constants for accessing the fields in this class. Used in
     * getActualMaximum().
     */
    public static final int SECOND = android.text.format.Time.SECOND;
    public static final int MINUTE = android.text.format.Time.MINUTE;
    public static final int HOUR = android.text.format.Time.HOUR;
    public static final int MONTH_DAY = android.text.format.Time.MONTH_DAY;
    public static final int MONTH = android.text.format.Time.MONTH;
    public static final int YEAR = android.text.format.Time.YEAR;
    public static final int WEEK_DAY = android.text.format.Time.WEEK_DAY;
    public static final int YEAR_DAY = android.text.format.Time.YEAR_DAY;
    public static final int WEEK_NUM = android.text.format.Time.WEEK_NUM;

    public static final int SUNDAY = android.text.format.Time.SUNDAY;
    public static final int MONDAY = android.text.format.Time.MONDAY;
    public static final int TUESDAY = android.text.format.Time.TUESDAY;
    public static final int WEDNESDAY = android.text.format.Time.WEDNESDAY;
    public static final int THURSDAY = android.text.format.Time.THURSDAY;
    public static final int FRIDAY = android.text.format.Time.FRIDAY;
    public static final int SATURDAY = android.text.format.Time.SATURDAY;

    private final android.text.format.Time mInstance;

    public int year;
    public int month;
    public int hour;
    public int minute;
    public int second;

    public int yearDay;
    public int monthDay;
    public int weekDay;

    public String timezone;
    public int isDst;
    public long gmtoff;
    public boolean allDay;

    public Time() {
        mInstance = new android.text.format.Time();
        readFields();
    }

    public Time(String timezone) {
        mInstance = new android.text.format.Time(timezone);
        readFields();
    }

    private void readFields() {
        year = mInstance.year;
        month = mInstance.month;
        hour = mInstance.hour;
        minute = mInstance.minute;
        second = mInstance.second;

        yearDay = mInstance.yearDay;
        monthDay = mInstance.monthDay;
        weekDay = mInstance.weekDay;

        timezone = mInstance.timezone;
        isDst = mInstance.isDst;
        gmtoff = mInstance.gmtoff;
        allDay = mInstance.allDay;
    }

    private void writeFields() {
        mInstance.year = year;
        mInstance.month = month;
        mInstance.hour = hour;
        mInstance.minute = minute;
        mInstance.second = second;

        mInstance.yearDay = yearDay;
        mInstance.monthDay = monthDay;
        mInstance.weekDay = weekDay;

        mInstance.timezone = timezone;
        mInstance.isDst = isDst;
        mInstance.gmtoff = gmtoff;
        mInstance.allDay = allDay;
    }

    public void set(long millis) {
        writeFields();
        mInstance.set(millis);
        readFields();
    }

    public void set(Time other) {
        other.writeFields();
        mInstance.set(other.mInstance);
        readFields();
    }

    public void set(int day, int month, int year) {
        writeFields();
        mInstance.set(day, month, year);
        readFields();
    }

    public void set(int second, int minute, int hour, int day, int month, int year) {
        writeFields();
        mInstance.set(second, minute, hour, day, month, year);
        readFields();
    }

    public void setToNow() {
        writeFields();
        mInstance.setToNow();
        readFields();
    }

    public long setJulianDay(int julianDay) {
        writeFields();
        final long ms = mInstance.setJulianDay(julianDay);
        readFields();
        return ms;
    }

    public static int getJulianDay(long begin, long gmtOff) {
        return android.text.format.Time.getJulianDay(begin, gmtOff);
    }

    public int getWeekNumber() {
        writeFields();
        final int num = mInstance.getWeekNumber();
        readFields();
        return num;
    }

    public int getActualMaximum(int field) {
        writeFields();
        return mInstance.getActualMaximum(field);
    }

    public void switchTimezone(String timezone) {
        writeFields();
        mInstance.switchTimezone(timezone);
        readFields();
    }

    public long normalize(boolean ignoreDst) {
        writeFields();
        final long ms = mInstance.normalize(ignoreDst);
        readFields();
        return ms;
    }

    public boolean parse(String time) {
        writeFields();
        boolean success = mInstance.parse(time);
        readFields();
        return success;
    }

    public boolean parse3339(String time) {
        writeFields();
        boolean success = mInstance.parse3339(time);
        readFields();
        return success;
    }

    public String format(String format) {
        writeFields();
        return (new android.text.format.Time(mInstance)).format(format);
    }

    public String format2445() {
        writeFields();
        return (new android.text.format.Time(mInstance)).format2445();
    }

    public String format3339(boolean allDay) {
        writeFields();
        return (new android.text.format.Time(mInstance)).format3339(allDay);
    }

    public long toMillis(boolean ignoreDst) {
        writeFields();
        return mInstance.toMillis(ignoreDst);
    }

    public static int compare(Time a, Time b) {
        a.writeFields();
        b.writeFields();
        return android.text.format.Time.compare(a.mInstance, b.mInstance);
    }

    public void clear(String timezoneId) {
        mInstance.clear(timezoneId);
        readFields();
    }
}
