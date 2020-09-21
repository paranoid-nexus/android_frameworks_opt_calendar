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

import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.SmallTest;
import android.util.TimeFormatException;

import junit.framework.TestCase;

/**
 * Tests for com.android.calendarcommon2.Time.
 *
 * Some of these tests are borrowed from android.text.format.TimeTest.
 */
public class TimeTest extends TestCase {

    @SmallTest
    public void testTimezone() {
        Time t = new Time(Time.TIMEZONE_UTC);
        assertEquals(Time.TIMEZONE_UTC, t.timezone);
    }

    @SmallTest
    public void testSwitchTimezone() {
        Time t = new Time(Time.TIMEZONE_UTC);
        String newTimezone = "America/Los_Angeles";
        t.switchTimezone(newTimezone);
        assertEquals(newTimezone, t.timezone);
    }

    @SmallTest
    public void testGetActualMaximum() {
        Time t = new Time(Time.TIMEZONE_UTC);
        t.set(0, 0, 2020);
        assertEquals(59, t.getActualMaximum(Time.SECOND));
        assertEquals(59, t.getActualMaximum(Time.MINUTE));
        assertEquals(23, t.getActualMaximum(Time.HOUR));
        assertEquals(31, t.getActualMaximum(Time.MONTH_DAY));
        assertEquals(11, t.getActualMaximum(Time.MONTH));
        assertEquals(2037, t.getActualMaximum(Time.YEAR));
        assertEquals(6, t.getActualMaximum(Time.WEEK_DAY));
        assertEquals(365, t.getActualMaximum(Time.YEAR_DAY)); // 2020 is a leap year
        t.set(0, 0, 2019);
        assertEquals(364, t.getActualMaximum(Time.YEAR_DAY));
    }

    @SmallTest
    public void testClear() {
        Time t = new Time(Time.TIMEZONE_UTC);
        t.clear(Time.TIMEZONE_UTC);

        assertEquals(Time.TIMEZONE_UTC, t.timezone);
        assertFalse(t.allDay);
        assertEquals(0, t.second);
        assertEquals(0, t.minute);
        assertEquals(0, t.hour);
        assertEquals(0, t.monthDay);
        assertEquals(0, t.month);
        assertEquals(0, t.year);
        assertEquals(0, t.weekDay);
        assertEquals(0, t.yearDay);
        assertEquals(0, t.gmtoff);
        assertEquals(-1, t.isDst);
    }

    @SmallTest
    public void testCompare() {
        Time a = new Time(Time.TIMEZONE_UTC);
        Time b = new Time("America/Los_Angeles");
        assertTrue(Time.compare(a, b) < 0);
    }

    @SmallTest
    public void testFormat() {
        Time t = new Time(Time.TIMEZONE_UTC);
        assertEquals("19700101T000000", t.format("%Y%m%dT%H%M%S"));
    }

    @SmallTest
    public void testFormat2445() {
        Time t = new Time(Time.TIMEZONE_UTC);
        assertEquals("19700101T000000Z", t.format2445());
    }

    @SmallTest
    public void testFormat3339() {
        Time t = new Time(Time.TIMEZONE_UTC);
        assertEquals("1970-01-01", t.format3339(true));
        t.set(29, 1, 2020);
        assertEquals("2020-02-29", t.format3339(true));
    }

    @SmallTest
    public void testMillis0() {
        Time t = new Time(Time.TIMEZONE_UTC);
        t.set(0, 0, 0, 1, 1, 2020);
        assertEquals(1580515200000L, t.toMillis(true));
        t.set(1, 0, 0, 1, 1, 2020);
        assertEquals(1580515201000L, t.toMillis(true));
    }

    @SmallTest
    public void testMillis1() {
        Time t = new Time(Time.TIMEZONE_UTC);
        t.set(1, 0, 0, 1, 0, 1970);
        assertEquals(1000L, t.toMillis(true));
    }

    @SmallTest
    public void testParse() {
        Time t = new Time(Time.TIMEZONE_UTC);
        assertTrue(t.parse("20201010T160000Z"));
        assertFalse(t.parse("12345678T901234"));
    }

    @SmallTest
    public void testParse3339() {
        Time t = new Time(Time.TIMEZONE_UTC);

        t.parse3339("1980-05-23");
        if (!t.allDay || t.year != 1980 || t.month != 4 || t.monthDay != 23) {
            fail("Did not parse all-day date correctly");
        }

        t.parse3339("1980-05-23T09:50:50");
        if (t.allDay || t.year != 1980 || t.month != 4 || t.monthDay != 23 || t.hour != 9
                || t.minute != 50 || t.second != 50 || t.gmtoff != 0) {
            fail("Did not parse timezone-offset-less date correctly");
        }

        t.parse3339("1980-05-23T09:50:50Z");
        if (t.allDay || t.year != 1980 || t.month != 4 || t.monthDay != 23 || t.hour != 9
                || t.minute != 50 || t.second != 50 || t.gmtoff != 0) {
            fail("Did not parse UTC date correctly");
        }

        t.parse3339("1980-05-23T09:50:50.0Z");
        if (t.allDay || t.year != 1980 || t.month != 4 || t.monthDay != 23 || t.hour != 9
                || t.minute != 50 || t.second != 50 || t.gmtoff != 0) {
            fail("Did not parse UTC date correctly");
        }

        t.parse3339("1980-05-23T09:50:50.12Z");
        if (t.allDay || t.year != 1980 || t.month != 4 || t.monthDay != 23 || t.hour != 9
                || t.minute != 50 || t.second != 50 || t.gmtoff != 0) {
            fail("Did not parse UTC date correctly");
        }

        t.parse3339("1980-05-23T09:50:50.123Z");
        if (t.allDay || t.year != 1980 || t.month != 4 || t.monthDay != 23 || t.hour != 9
                || t.minute != 50 || t.second != 50 || t.gmtoff != 0) {
            fail("Did not parse UTC date correctly");
        }

        // the time should be normalized to UTC
        t.parse3339("1980-05-23T09:50:50-01:05");
        if (t.allDay || t.year != 1980 || t.month != 4 || t.monthDay != 23 || t.hour != 10
                || t.minute != 55 || t.second != 50 || t.gmtoff != 0) {
            fail("Did not parse timezone-offset date correctly");
        }

        // the time should be normalized to UTC
        t.parse3339("1980-05-23T09:50:50.123-01:05");
        if (t.allDay || t.year != 1980 || t.month != 4 || t.monthDay != 23 || t.hour != 10
                || t.minute != 55 || t.second != 50 || t.gmtoff != 0) {
            fail("Did not parse timezone-offset date correctly");
        }

        try {
            t.parse3339("1980");
            fail("Did not throw error on truncated input length");
        } catch (TimeFormatException e) {
            // successful
        }

        try {
            t.parse3339("1980-05-23T09:50:50.123+");
            fail("Did not throw error on truncated timezone offset");
        } catch (TimeFormatException e1) {
            // successful
        }

        try {
            t.parse3339("1980-05-23T09:50:50.123+05:0");
            fail("Did not throw error on truncated timezone offset");
        } catch (TimeFormatException e1) {
            // successful
        }
    }

    @SmallTest
    public void testSet0() {
        Time t = new Time(Time.TIMEZONE_UTC);

        t.set(1000L);
        assertEquals(1970, t.year);
        assertEquals(1, t.second);

        t.set(2000L);
        assertEquals(2, t.second);
        assertEquals(0, t.minute);

        t.set(1000L * 60);
        assertEquals(1, t.minute);
        assertEquals(0, t.hour);

        t.set(1000L * 60 * 60);
        assertEquals(1, t.hour);
        assertEquals(1, t.monthDay);

        t.set((1000L * 60 * 60 * 24) + 1000L);
        assertEquals(2, t.monthDay);
        assertEquals(1970, t.year);
    }

    @SmallTest
    public void testSet1() {
        Time t = new Time(Time.TIMEZONE_UTC);
        t.set(1, 2, 2021);
        assertEquals(1, t.monthDay);
        assertEquals(2, t.month);
        assertEquals(2021, t.year);
    }

    @SmallTest
    public void testSet2() {
        Time t = new Time(Time.TIMEZONE_UTC);
        t.set(1, 2, 3, 4, 5, 2021);
        assertEquals(1, t.second);
        assertEquals(2, t.minute);
        assertEquals(3, t.hour);
        assertEquals(4, t.monthDay);
        assertEquals(5, t.month);
        assertEquals(2021, t.year);
    }

    @SmallTest
    public void testSet3() {
        Time t = new Time(Time.TIMEZONE_UTC);
        t.set(1, 2, 3, 4, 5, 2021);
        Time t2 = new Time();
        t2.set(t);
        assertEquals(Time.TIMEZONE_UTC, t2.timezone);
        assertEquals(1, t2.second);
        assertEquals(2, t2.minute);
        assertEquals(3, t2.hour);
        assertEquals(4, t2.monthDay);
        assertEquals(5, t2.month);
        assertEquals(2021, t2.year);
    }

    @SmallTest
    public void testSetToNow() {
        Time t = new Time(Time.TIMEZONE_UTC);
        t.setToNow();
        long ms = t.toMillis(true);
        long now = System.currentTimeMillis();
        // ensure millis returned are within 1 second of when they were set
        assertTrue(ms < now && ms > (now - 1000));
    }

    @SmallTest
    public void testGetWeekNumber() {
        Time t = new Time(Time.TIMEZONE_UTC);
        t.set(1000L);
        assertEquals(1, t.getWeekNumber());
        t.set(1, 1, 2020);
        assertEquals(5, t.getWeekNumber());
    }

    private static class DateTest {
        public int year1;
        public int month1;
        public int day1;
        public int hour1;
        public int minute1;
        public int dst1;

        public int offset;

        public int year2;
        public int month2;
        public int day2;
        public int hour2;
        public int minute2;
        public int dst2;

        public DateTest(int year1, int month1, int day1, int hour1, int minute1,
                int offset, int year2, int month2, int day2, int hour2, int minute2) {
            this.year1 = year1;
            this.month1 = month1;
            this.day1 = day1;
            this.hour1 = hour1;
            this.minute1 = minute1;
            this.dst1 = -1;
            this.offset = offset;
            this.year2 = year2;
            this.month2 = month2;
            this.day2 = day2;
            this.hour2 = hour2;
            this.minute2 = minute2;
            this.dst2 = -1;
        }

        public DateTest(int year1, int month1, int day1, int hour1, int minute1, int dst1,
                int offset, int year2, int month2, int day2, int hour2, int minute2,
                int dst2) {
            this.year1 = year1;
            this.month1 = month1;
            this.day1 = day1;
            this.hour1 = hour1;
            this.minute1 = minute1;
            this.dst1 = dst1;
            this.offset = offset;
            this.year2 = year2;
            this.month2 = month2;
            this.day2 = day2;
            this.hour2 = hour2;
            this.minute2 = minute2;
            this.dst2 = dst2;
        }

        public boolean equals(Time time) {
            return time.year == year2 && time.month == month2 && time.monthDay == day2
                    && time.hour == hour2 && time.minute == minute2;
        }

        public boolean equalsWithDst(Time time) {
            return time.year == year2 && time.month == month2 && time.monthDay == day2
                    && time.hour == hour2 && time.minute == minute2 && time.isDst == dst2;
        }
    }

    /* These tests assume that DST changes on Nov 4, 2007 at 2am (to 1am). */

    // The "offset" field in "dayTests" represents days.
    // Use normalize(true) with these tests to change the date by 1 day.
    // Note: the month numbers are 0-relative, so Jan=0, Feb=1,...Dec=11
    private DateTest[] dayTests = {
            // Nov 4, 12am + 0 day = Nov 4, 12am
            new DateTest(2007, 10, 4, 0, 0, 0, 2007, 10, 4, 0, 0),
            // Nov 5, 12am + 0 day = Nov 5, 12am
            new DateTest(2007, 10, 5, 0, 0, 0, 2007, 10, 5, 0, 0),
            // Nov 3, 12am + 1 day = Nov 4, 12am
            new DateTest(2007, 10, 3, 0, 0, 1, 2007, 10, 4, 0, 0),
            // Nov 4, 12am + 1 day = Nov 5, 12am
            new DateTest(2007, 10, 4, 0, 0, 1, 2007, 10, 5, 0, 0),
            // Nov 5, 12am + 1 day = Nov 6, 12am
            new DateTest(2007, 10, 5, 0, 0, 1, 2007, 10, 6, 0, 0),
            // Nov 3, 1am + 1 day = Nov 4, 1am
            new DateTest(2007, 10, 3, 1, 0, 1, 2007, 10, 4, 1, 0),
            // Nov 4, 1am + 1 day = Nov 5, 1am
            new DateTest(2007, 10, 4, 1, 0, 1, 2007, 10, 5, 1, 0),
            // Nov 5, 1am + 1 day = Nov 6, 1am
            new DateTest(2007, 10, 5, 1, 0, 1, 2007, 10, 6, 1, 0),
            // Nov 3, 2am + 1 day = Nov 4, 2am
            new DateTest(2007, 10, 3, 2, 0, 1, 2007, 10, 4, 2, 0),
            // Nov 4, 2am + 1 day = Nov 5, 2am
            new DateTest(2007, 10, 4, 2, 0, 1, 2007, 10, 5, 2, 0),
            // Nov 5, 2am + 1 day = Nov 6, 2am
            new DateTest(2007, 10, 5, 2, 0, 1, 2007, 10, 6, 2, 0),
    };

    // The "offset" field in "minuteTests" represents minutes.
    // Use normalize(false) with these tests.
    // Note: the month numbers are 0-relative, so Jan=0, Feb=1,...Dec=11
    private DateTest[] minuteTests = {
            // Nov 4, 12am + 0 minutes = Nov 4, 12am
            new DateTest(2007, 10, 4, 0, 0, 0, 2007, 10, 4, 0, 0),
            // Nov 5, 12am + 0 minutes = Nov 5, 12am
            new DateTest(2007, 10, 5, 0, 0, 0, 2007, 10, 5, 0, 0),
            // Nov 3, 12am + 60 minutes = Nov 3, 1am
            new DateTest(2007, 10, 3, 0, 0, 60, 2007, 10, 3, 1, 0),
            // Nov 4, 12am + 60 minutes = Nov 4, 1am
            new DateTest(2007, 10, 4, 0, 0, 60, 2007, 10, 4, 1, 0),
            // Nov 5, 12am + 60 minutes = Nov 5, 1am
            new DateTest(2007, 10, 5, 0, 0, 60, 2007, 10, 5, 1, 0),
            // Nov 3, 1am + 60 minutes = Nov 3, 2am
            new DateTest(2007, 10, 3, 1, 0, 60, 2007, 10, 3, 2, 0),
            // Nov 4, 1am (PDT) + 30 minutes = Nov 4, 1:30am (PDT)
            new DateTest(2007, 10, 4, 1, 0, 1, 30, 2007, 10, 4, 1, 30, 1),
            // Nov 4, 1am (PDT) + 60 minutes = Nov 4, 1am (PST)
            new DateTest(2007, 10, 4, 1, 0, 1, 60, 2007, 10, 4, 1, 0, 0),
            // Nov 4, 1:30am (PDT) + 15 minutes = Nov 4, 1:45am (PDT)
            new DateTest(2007, 10, 4, 1, 30, 1, 15, 2007, 10, 4, 1, 45, 1),
            // Nov 4, 1:30am (PDT) + 30 minutes = Nov 4, 1:00am (PST)
            new DateTest(2007, 10, 4, 1, 30, 1, 30, 2007, 10, 4, 1, 0, 0),
            // Nov 4, 1:30am (PDT) + 60 minutes = Nov 4, 1:30am (PST)
            new DateTest(2007, 10, 4, 1, 30, 1, 60, 2007, 10, 4, 1, 30, 0),
            // Nov 4, 1:30am (PST) + 15 minutes = Nov 4, 1:45am (PST)
            new DateTest(2007, 10, 4, 1, 30, 0, 15, 2007, 10, 4, 1, 45, 0),
            // Nov 4, 1:30am (PST) + 30 minutes = Nov 4, 2:00am (PST)
            new DateTest(2007, 10, 4, 1, 30, 0, 30, 2007, 10, 4, 2, 0, 0),
            // Nov 5, 1am + 60 minutes = Nov 5, 2am
            new DateTest(2007, 10, 5, 1, 0, 60, 2007, 10, 5, 2, 0),
            // Nov 3, 2am + 60 minutes = Nov 3, 3am
            new DateTest(2007, 10, 3, 2, 0, 60, 2007, 10, 3, 3, 0),
            // Nov 4, 2am + 30 minutes = Nov 4, 2:30am
            new DateTest(2007, 10, 4, 2, 0, 30, 2007, 10, 4, 2, 30),
            // Nov 4, 2am + 60 minutes = Nov 4, 3am
            new DateTest(2007, 10, 4, 2, 0, 60, 2007, 10, 4, 3, 0),
            // Nov 5, 2am + 60 minutes = Nov 5, 3am
            new DateTest(2007, 10, 5, 2, 0, 60, 2007, 10, 5, 3, 0),
    };

    @SmallTest
    public void testNormalize0() {
        Time t = new Time(Time.TIMEZONE_UTC);
        t.parse("20060432T010203");
        assertEquals(1146531723000L, t.normalize(false));
    }

    @MediumTest
    public void testNormalize1() {
        Time local = new Time("America/Los_Angeles");

        int len = dayTests.length;
        for (int index = 0; index < len; index++) {
            DateTest test = dayTests[index];
            local.set(0, test.minute1, test.hour1, test.day1, test.month1, test.year1);
            // call normalize() to make sure that isDst is set
            local.normalize(false);
            local.monthDay += test.offset;
            local.normalize(true);
            if (!test.equals(local)) {
                String expectedTime = String.format("%d-%02d-%02d %02d:%02d",
                        test.year2, test.month2, test.day2, test.hour2, test.minute2);
                String actualTime = String.format("%d-%02d-%02d %02d:%02d",
                        local.year, local.month, local.monthDay, local.hour, local.minute);
                fail("Expected: " + expectedTime + "; Actual: " + actualTime);
            }

            local.set(0, test.minute1, test.hour1, test.day1, test.month1, test.year1);
            // call normalize() to make sure that isDst is set
            local.normalize(false);
            local.monthDay += test.offset;
            long millis = local.toMillis(true);
            local.set(millis);
            if (!test.equals(local)) {
                String expectedTime = String.format("%d-%02d-%02d %02d:%02d",
                        test.year2, test.month2, test.day2, test.hour2, test.minute2);
                String actualTime = String.format("%d-%02d-%02d %02d:%02d",
                        local.year, local.month, local.monthDay, local.hour, local.minute);
                fail("Expected: " + expectedTime + "; Actual: " + actualTime);
            }
        }

        len = minuteTests.length;
        for (int index = 0; index < len; index++) {
            DateTest test = minuteTests[index];
            local.set(0, test.minute1, test.hour1, test.day1, test.month1, test.year1);
            local.isDst = test.dst1;
            // call normalize() to make sure that isDst is set
            local.normalize(false);
            if (test.dst2 == -1) test.dst2 = local.isDst;
            local.minute += test.offset;
            local.normalize(false);
            if (!test.equalsWithDst(local)) {
                String expectedTime = String.format("%d-%02d-%02d %02d:%02d isDst: %d",
                        test.year2, test.month2, test.day2, test.hour2, test.minute2, test.dst2);
                String actualTime = String.format("%d-%02d-%02d %02d:%02d isDst: %d",
                        local.year, local.month, local.monthDay, local.hour, local.minute,
                        local.isDst);
                fail("Expected: " + expectedTime + "; Actual: " + actualTime);
            }

            local.set(0, test.minute1, test.hour1, test.day1, test.month1, test.year1);
            local.isDst = test.dst1;
            // call normalize() to make sure that isDst is set
            local.normalize(false);
            if (test.dst2 == -1) test.dst2 = local.isDst;
            local.minute += test.offset;
            long millis = local.toMillis(false);
            local.set(millis);
            if (!test.equalsWithDst(local)) {
                String expectedTime = String.format("%d-%02d-%02d %02d:%02d isDst: %d",
                        test.year2, test.month2, test.day2, test.hour2, test.minute2, test.dst2);
                String actualTime = String.format("%d-%02d-%02d %02d:%02d isDst: %d",
                        local.year, local.month, local.monthDay, local.hour, local.minute,
                        local.isDst);
                fail("Expected: " + expectedTime + "; Actual: " + actualTime);
            }
        }
    }

    // Timezones that cover the world.
    // Some GMT offsets occur more than once in case some cities decide to change their GMT offset.
    private static final String[] mTimeZones = {
            "Pacific/Kiritimati",
            "Pacific/Enderbury",
            "Pacific/Fiji",
            "Antarctica/South_Pole",
            "Pacific/Norfolk",
            "Pacific/Ponape",
            "Asia/Magadan",
            "Australia/Lord_Howe",
            "Australia/Sydney",
            "Australia/Adelaide",
            "Asia/Tokyo",
            "Asia/Seoul",
            "Asia/Taipei",
            "Asia/Singapore",
            "Asia/Hong_Kong",
            "Asia/Saigon",
            "Asia/Bangkok",
            "Indian/Cocos",
            "Asia/Rangoon",
            "Asia/Omsk",
            "Antarctica/Mawson",
            "Asia/Colombo",
            "Asia/Calcutta",
            "Asia/Oral",
            "Asia/Kabul",
            "Asia/Dubai",
            "Asia/Tehran",
            "Europe/Moscow",
            "Asia/Baghdad",
            "Africa/Mogadishu",
            "Europe/Athens",
            "Africa/Cairo",
            "Europe/Rome",
            "Europe/Berlin",
            "Europe/Amsterdam",
            "Africa/Tunis",
            "Europe/London",
            "Europe/Dublin",
            "Atlantic/St_Helena",
            "Africa/Monrovia",
            "Africa/Accra",
            "Atlantic/Azores",
            "Atlantic/South_Georgia",
            "America/Noronha",
            "America/Sao_Paulo",
            "America/Cayenne",
            "America/St_Johns",
            "America/Puerto_Rico",
            "America/Aruba",
            "America/New_York",
            "America/Chicago",
            "America/Denver",
            "America/Los_Angeles",
            "America/Anchorage",
            "Pacific/Marquesas",
            "America/Adak",
            "Pacific/Honolulu",
            "Pacific/Midway",
    };

    @MediumTest
    public void testGetJulianDay() {
        Time time = new Time();

        // for a random day in the year 2020 and for a random timezone, get the Julian day for 12am
        // and then check that if we change the time we get the same Julian day.
        int monthDay = (int) (Math.random() * 365) + 1;
        int zoneIndex = (int) (Math.random() * mTimeZones.length);
        time.set(0, 0, 0, monthDay, 0, 2020);
        time.timezone = mTimeZones[zoneIndex];
        long millis = time.normalize(true);

        int julianDay = Time.getJulianDay(millis, time.gmtoff);

        // change the time during the day and check that we get the same Julian day.
        for (int hour = 0; hour < 24; hour++) {
            for (int minute = 0; minute < 60; minute += 15) {
                time.set(0, minute, hour, monthDay, 0, 2020);
                millis = time.normalize(true);
                int day = Time.getJulianDay(millis, time.gmtoff);
                assertEquals(day, julianDay);
            }
        }
    }

    @MediumTest
    public void testSetJulianDay() {
        Time time = new Time();

        // for each day in the year 2020, pick a random timezone, and verify that we can
        // set the Julian day correctly.
        for (int monthDay = 1; monthDay <= 366; monthDay++) {
            int zoneIndex = (int) (Math.random() * mTimeZones.length);
            // leave the "month" as zero because we are changing the "monthDay" from 1 to 366.
            // the call to normalize() will then change the "month" (but we don't really care).
            time.set(0, 0, 0, monthDay, 0, 2020);
            time.timezone = mTimeZones[zoneIndex];
            long millis = time.normalize(true);
            int julianDay = Time.getJulianDay(millis, time.gmtoff);

            time.setJulianDay(julianDay);

            // some places change daylight saving time at 12am and so there is no 12am on some days
            // in some timezones - in those cases, the time is set to 1am.
            // some examples: Africa/Cairo, America/Sao_Paulo, Atlantic/Azores
            assertTrue(time.hour == 0 || time.hour == 1);
            assertEquals(0, time.minute);
            assertEquals(0, time.second);

            millis = time.toMillis(false);
            int day = Time.getJulianDay(millis, time.gmtoff);
            assertEquals(day, julianDay);
        }
    }
}
