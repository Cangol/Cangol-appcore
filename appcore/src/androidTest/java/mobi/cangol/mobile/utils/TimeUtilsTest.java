/*
 *
 *  Copyright (c) 2013 Cangol
 *   <p/>
 *   Licensed under the Apache License, Version 2.0 (the "License")
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *  <p/>
 *  http://www.apache.org/licenses/LICENSE-2.0
 *  <p/>
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package mobi.cangol.mobile.utils;


import android.test.InstrumentationTestCase;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by xuewu.wei on 2016/6/8.
 */
public class TimeUtilsTest extends InstrumentationTestCase {

    
    public void testGetCurrentYear() throws Exception {
        Calendar calendar=Calendar.getInstance();
        assertNotNull(TimeUtils.getCurrentYear());
        assertEquals(calendar.get(Calendar.YEAR),Integer.parseInt(TimeUtils.getCurrentYear()));
    }

    
    public void testGetCurrentMonth() throws Exception {
        Calendar calendar=Calendar.getInstance();
        assertNotNull(TimeUtils.getCurrentMonth());
        assertEquals(calendar.get(Calendar.MONTH)+1,Integer.parseInt(TimeUtils.getCurrentMonth()));
    }

    public void testGetCurrentDay() throws Exception {
        Calendar calendar=Calendar.getInstance();
        assertNotNull(TimeUtils.getCurrentDay());
        assertEquals(calendar.get(Calendar.DAY_OF_MONTH),Integer.parseInt(TimeUtils.getCurrentDay()));
    }

    public void testGetCurrentHoursMinutes() throws Exception {
        Calendar calendar=Calendar.getInstance();
        assertNotNull(TimeUtils.getCurrentHoursMinutes());
        //assertEquals(calendar.get(Calendar.HOUR_OF_DAY)+":"+calendar.get(Calendar.MINUTE),TimeUtils.getCurrentHoursMinutes());
    }

    public void testGetCurrentTime() throws Exception {
        Calendar calendar=Calendar.getInstance();
        assertNotNull(TimeUtils.getCurrentTime());
    }

    public void testGetCurrentTime2() throws Exception {
        Calendar calendar=Calendar.getInstance();
        assertNotNull(TimeUtils.getCurrentTime2());
    }

    public void testConvertToDate() throws Exception {
        assertNull(TimeUtils.convertToDate("20121229"));
        assertNotNull(TimeUtils.convertToDate("2012-12-29"));
    }

    public void testConvertToString() throws Exception {
        assertNotNull(TimeUtils.convertToString(new Date()));
    }

    public void testGetCurrentTimeAddYear() throws Exception {
        assertNotNull(TimeUtils.getCurrentTimeAddYear(-1));
    }

    public void testGetCurrentDate() throws Exception {
        Calendar calendar=Calendar.getInstance();
        assertNotNull(TimeUtils.getCurrentDate());
    }

    public void testGetDate8Bit() throws Exception {
        assertNotNull(TimeUtils.getDate8Bit());
    }

    public void testAddDay() throws Exception {
        assertNotNull(TimeUtils.addDay(TimeUtils.getCurrentDate(),1));
    }

    public void testGetStartDateInPeriod() throws Exception {
        assertNull(TimeUtils.getStartDateInPeriod("201212"));
        assertNotNull(TimeUtils.getStartDateInPeriod("2012-12"));
        assertNotNull(TimeUtils.getStartDateInPeriod("2012-12-12"));
    }

    public void testGetEndDateInPeriod() throws Exception {
        assertNull(TimeUtils.getEndDateInPeriod("201212"));
        assertNotNull(TimeUtils.getEndDateInPeriod("2012-12"));
        assertNotNull(TimeUtils.getEndDateInPeriod("2012-02-12"));
    }

    public void testConvertStandard() throws Exception {
        assertNotNull(TimeUtils.convertStandard("20151212"));
    }

    public void testConvertString() throws Exception {
        assertNotNull(TimeUtils.formatHm(System.currentTimeMillis()));
    }

    public void testConvert8Bit() throws Exception {
        assertNotNull(TimeUtils.convert8Bit("2015-12-12"));
    }

    public void testFormatRecentTime() throws Exception {
        assertNull(TimeUtils.formatRecentTime("2015-12-12"));
        assertNotNull(TimeUtils.formatRecentTime("2016-05-08 15:33:00"));
        assertNotNull(TimeUtils.formatRecentTime("2016-06-07 15:33:00"));
        assertNotNull(TimeUtils.formatRecentTime("2016-06-08 15:33:00"));
    }

    public void testGetZhTimeString() throws Exception {
        assertNotNull(TimeUtils.getZhTimeString("00"));
        assertNotNull(TimeUtils.getZhTimeString("00:00:"));
        assertNotNull(TimeUtils.getZhTimeString("00:00:00"));
    }

    public void testConvertLong() throws Exception {
        assertNotNull(TimeUtils.convertLong("2012"));
        assertNotNull(TimeUtils.convertLong("2012-12"));
        assertNotNull(TimeUtils.convertLong("2012-12-12"));
        assertNotNull(TimeUtils.convertLong("2012-12-12 00"));
        assertNotNull(TimeUtils.convertLong("2012-12-12 00:00:"));
        assertNotNull(TimeUtils.convertLong("2012-12-12 00:00:00"));
    }
    public void testFormatDateString() throws Exception {
        assertNotNull(TimeUtils.formatDateString("2012-12-12 00:00:00"));
        assertNotNull(TimeUtils.formatTimeString("2012-12-12 00:00:00"));
        assertNotNull(TimeUtils.formatTimeString2("2012-12-12 00:00:00"));
    }
    public void testGetFormatDate() throws Exception {
        assertNotNull(TimeUtils.getFormatDate(2012,12-1,12));
        assertEquals("2012-12-12",TimeUtils.getFormatDate(2012,12-1,12));
    }

    public void testGetFormatTime() throws Exception {
        assertNotNull(TimeUtils.getFormatTime(12,12));
        assertEquals("12:12",TimeUtils.getFormatTime(12,12));
    }

    public void testFormatLatelyTime() throws Exception {
        assertNotNull(TimeUtils.formatLatelyTime("2012-12-12 00:00:00"));
        assertNotNull(TimeUtils.formatLatelyTime(TimeUtils.getCurrentTime()));

    }
}