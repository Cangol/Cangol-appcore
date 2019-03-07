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

/**
 * Created by xuewu.wei on 2016/6/12.
 */
public class StringUtilsTest extends InstrumentationTestCase {

    public void testByte2String() {
        String str="abcdef";
        StringUtils.byte2hex(str.getBytes());
    }

    public void testByte2hex() {
        String str="abcdef";
        StringUtils.byte2hex(str.getBytes());
    }

    public void testReverse() {
        String str="abcdef";
        StringUtils.reverse(str);
    }

    public void testFormatZhNum() {
        StringUtils.formatZhNum(100);
        StringUtils.formatZhNum(100000);
        StringUtils.formatZhNum(1000000000);
    }

    public void testFormatSpeed() {
        StringUtils.formatSpeed(120);
    }

    public void testFormatSize() {
        StringUtils.formatSize(4096*100);
    }

    public void testFormatTime() {
        StringUtils.formatTime((int) (System.currentTimeMillis()/1000));
    }

    public void testMd5() {
        StringUtils.md5("111111");
    }

    public void testMd51() {
        StringUtils.md5("111111".getBytes());
    }

    public void testIsBlank() {
        assertTrue(StringUtils.isBlank(""));
    }

    public void testIsEmpty() {
        assertTrue(StringUtils.isEmpty(null));
        assertTrue(StringUtils.isEmpty(""));
        assertFalse(StringUtils.isEmpty(" "));
    }

    public void testIsNotEmpty() {
        assertFalse(StringUtils.isNotEmpty(null));
        assertFalse(StringUtils.isNotEmpty(""));
        assertTrue(StringUtils.isNotEmpty(" "));
        assertTrue( StringUtils.isNotEmpty("1 "));
    }

    public void testIsNotBlank() {
        assertFalse(StringUtils.isNotBlank(null));
        assertFalse(StringUtils.isNotBlank(""));
        assertFalse(StringUtils.isNotBlank(" "));
        assertTrue(StringUtils.isNotBlank("1 "));
    }

    public void testNull2Zero() {
        StringUtils.null2Zero(null);
        StringUtils.null2Zero("");
    }

    public void testReplaceWhiteSpace() {
        StringUtils.replaceWhiteSpace(" ");
        StringUtils.replaceWhiteSpace(" ");
    }

    public void testReplaceTableSpace() {
        StringUtils.replaceTableSpace(" ");
    }

    public void testTrimForFront() {
        StringUtils.trimForFront(" 123");
        StringUtils.trimForFront(" 123 123 ");

    }

    public void testTrimToEmpty() {
        StringUtils.trimToEmpty(" 123");
        StringUtils.trimToEmpty(" 123 123 ");
    }

    public void testTrimAllWhitespace() {
        StringUtils.trimAllWhitespace(" 123");
        StringUtils.trimAllWhitespace(" 123 123 ");
    }

    public void testStrip() {
        StringUtils.strip(" 123");
    }

    public void testStripStart() {
        StringUtils.stripStart("123abc","12");
    }

    public void testStripEnd() {
        StringUtils.stripEnd("123abc","bc");
    }

    public void testStrip1() {
        StringUtils.strip("123abc","2c");
    }

    public void testIsNumeric() {
        assertTrue(StringUtils.isNumeric("11"));
        assertFalse(StringUtils.isNumeric("1a"));
        assertTrue(StringUtils.isNumeric("01"));
    }

    public void testIsNumericSpace() {
        assertTrue(StringUtils.isNumericSpace("100 0000"));
    }

    public void testByteXorInt() {
        StringUtils.byteXorInt("abcdef".getBytes(),1);
    }

    public void testByteArray2int() {
        StringUtils.byteArray2int("2".getBytes());
    }

    public void testInt2byteArray() {
        StringUtils.int2byteArray(2);
    }

    public void testNull2Empty() {
        StringUtils.null2Empty(null);
        StringUtils.null2Empty("");
        StringUtils.null2Empty(" ");
        StringUtils.null2Empty(" 1");
    }
}