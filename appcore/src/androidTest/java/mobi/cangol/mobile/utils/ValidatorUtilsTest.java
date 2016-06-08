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
 * Created by xuewu.wei on 2016/6/8.
 */
public class ValidatorUtilsTest extends InstrumentationTestCase {

    
    public void testValidateNull() throws Exception {
        assertEquals(false,ValidatorUtils.validateNull(null));
        assertEquals(false,ValidatorUtils.validateNull(""));
    }

   
    public void testValidateContent() throws Exception {
        assertEquals(false,ValidatorUtils.validateContent(null));
        assertEquals(false,ValidatorUtils.validateContent(""));
        assertEquals(false,ValidatorUtils.validateContent("1234"));
        assertEquals(true,ValidatorUtils.validateContent("0123456789"));
        assertEquals(false,ValidatorUtils.validateContent("0123456789" +
                "0123456789" +
                "0123456789" +
                "0123456789" +
                "0123456789" +
                "0123456789" +
                "0123456789" +
                "0123456789" +
                "0123456789" +
                "0123456789" +
                "0123456789" +
                "0123456789" +
                "0123456789" +
                "0123456789" +
                "1"));
    }

   
    public void testValidateNickname() throws Exception {
        assertEquals(false,ValidatorUtils.validateNickname(null));
        assertEquals(false,ValidatorUtils.validateNickname(""));
        System.out.print("ab".length());
        assertEquals(false,ValidatorUtils.validateNickname("abcd"));
        assertEquals(true,ValidatorUtils.validateNickname("abcdefghij"));
        assertEquals(true,ValidatorUtils.validateNickname("王小二"));
        assertEquals(true,ValidatorUtils.validateNickname("一二三四五六七八九十"));
        assertEquals(false,ValidatorUtils.validateNickname("零一二三四五六七八九十"));
        assertEquals(false,ValidatorUtils.validateNickname("0123456789" +
                "0123456789" +
                "0123456789" +
                "1"));
    }

   
    public void testValidateAccount() throws Exception {

        assertEquals(false,ValidatorUtils.validateAccount(null));
        assertEquals(false,ValidatorUtils.validateAccount(""));
        assertEquals(false,ValidatorUtils.validateAccount("12345"));
        assertEquals(false,ValidatorUtils.validateAccount("12345abcdefg"));
        assertEquals(false,ValidatorUtils.validateAccount("abcdefghijk"));
        assertEquals(false,ValidatorUtils.validateAccount("01234567891"));
        assertEquals(false,ValidatorUtils.validateAccount("11000000000"));
        assertEquals(true,ValidatorUtils.validateAccount("13200000000"));

        assertEquals(false,ValidatorUtils.validateAccount("123g"));
        assertEquals(false,ValidatorUtils.validateAccount("123@g"));
        assertEquals(true,ValidatorUtils.validateAccount("123@g.cn"));
        assertEquals(true,ValidatorUtils.validateAccount("wxw@g.cn"));
    }

   
    public void testValidatePassword() throws Exception {
        assertEquals(false,ValidatorUtils.validatePassword(null));
        assertEquals(false,ValidatorUtils.validatePassword(""));
        assertEquals(false,ValidatorUtils.validatePassword("1234"));
        assertEquals(false,ValidatorUtils.validatePassword("0123456789" +
                "0123456789" +
                "1"));
        assertEquals(true,ValidatorUtils.validatePassword("0123456789"));
        assertEquals(true,ValidatorUtils.validatePassword("abcdefghijk"));
    }

   
    public void testValidatePhone() throws Exception {
        assertEquals(false,ValidatorUtils.validatePhone(null));
        assertEquals(false,ValidatorUtils.validatePhone(""));
        assertEquals(true,ValidatorUtils.validatePhone("12345"));
        assertEquals(false,ValidatorUtils.validatePhone("12345abcdefg"));
        assertEquals(false,ValidatorUtils.validatePhone("abcdefghijk"));
        assertEquals(true,ValidatorUtils.validatePhone("01234567891"));
        assertEquals(true,ValidatorUtils.validatePhone("11000000000"));
        assertEquals(true,ValidatorUtils.validatePhone("13200000000"));
    }

   
    public void testValidateMobile() throws Exception {
        assertEquals(false,ValidatorUtils.validateMobile(null));
        assertEquals(false,ValidatorUtils.validateMobile(""));
        assertEquals(false,ValidatorUtils.validateMobile("12345"));
        assertEquals(false,ValidatorUtils.validateMobile("12345abcdefg"));
        assertEquals(false,ValidatorUtils.validateMobile("abcdefghijk"));
        assertEquals(false,ValidatorUtils.validateMobile("01234567891"));
        assertEquals(false,ValidatorUtils.validateMobile("11000000000"));
        assertEquals(true,ValidatorUtils.validateMobile("13200000000"));
    }

   
    public void testValidateEmail() throws Exception {
        assertEquals(false,ValidatorUtils.validateEmail(null));
        assertEquals(false,ValidatorUtils.validateEmail(""));
        assertEquals(false,ValidatorUtils.validateEmail("123g"));
        assertEquals(false,ValidatorUtils.validateEmail("123@g"));
        assertEquals(true,ValidatorUtils.validateEmail("123@g.cn"));
        assertEquals(true,ValidatorUtils.validateEmail("wxw@g.cn"));
    }

   
    public void testValidateURL() throws Exception {
        assertEquals(false,ValidatorUtils.validateURL(null));
        assertEquals(false,ValidatorUtils.validateURL(""));
        assertEquals(false,ValidatorUtils.validateURL("123g"));
        assertEquals(false,ValidatorUtils.validateURL("123@g.cn"));
        assertEquals(false,ValidatorUtils.validateURL("wxw@g.cn"));
        assertEquals(true,ValidatorUtils.validateURL("https://cangol.mobi"));
        assertEquals(true,ValidatorUtils.validateURL("rtsp://cangol.mobi"));
        assertEquals(true,ValidatorUtils.validateURL("http://cangol.mobi"));
    }

   
    public void testValidateIP() throws Exception {
        assertEquals(false,ValidatorUtils.validateIP(null));
        assertEquals(false,ValidatorUtils.validateIP(""));
        assertEquals(false,ValidatorUtils.validateIP("266@g"));
        assertEquals(false,ValidatorUtils.validateIP("1.1.1"));
        assertEquals(true,ValidatorUtils.validateIP("255.255.255.255"));
        assertEquals(true,ValidatorUtils.validateIP("1.0.0.0"));
    }
}