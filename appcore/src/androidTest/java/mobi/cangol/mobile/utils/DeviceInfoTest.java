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

import android.test.AndroidTestCase;
import android.test.mock.MockContext;

import java.util.concurrent.atomic.AtomicInteger;


/**
 * Created by xuewu.wei on 2016/6/8.
 */
public class DeviceInfoTest extends AndroidTestCase {
    public void setUp() throws Exception {
        super.setUp();
    }

    public void testGetOS() {
        assertNotNull(DeviceInfo.getOS());
    }

    public void testGetOSVersion() {
        assertNotNull(DeviceInfo.getOSVersion());
    }

    public void testGetDeviceModel() {
        assertNotNull(DeviceInfo.getDeviceModel());
    }

    public void testGetDeviceBrand() {
        assertNotNull(DeviceInfo.getDeviceBrand());
    }

    public void testGetMobileInfo() {
        assertNotNull(DeviceInfo.getMobileInfo());
    }

    public void testGetMemSize() {
        assertNotNull(DeviceInfo.getMemTotalSize());
    }

    public void testGetMemInfo() {
        assertNotNull(DeviceInfo.getMemInfo());
    }

    public void testGetCPUInfo() {
        assertNotNull(DeviceInfo.getCPUInfo());
    }

    public void testGetCPUABI() {
        assertNotNull(DeviceInfo.getCPUABI());
    }

    public void testGetResolution() {
        assertNotNull(DeviceInfo.getResolution(getContext()));
    }

    public void testGetStatusBarHeight() {
        //assertNotNull(DeviceInfo.getStatusBarHeight(getContext()));
    }

    public void testGetDisplayMetrics() {
        assertNotNull(DeviceInfo.getDisplayMetrics(getContext()));
    }

    public void testGetDensity() {
        assertNotNull(DeviceInfo.getDensity(getContext()));
    }

    public void testGetDensityDpi() {
        assertNotNull(DeviceInfo.getDensityDpi(getContext()));
    }

    public void testGetDensityDpiStr() {
        assertNotNull(DeviceInfo.getDensityDpiStr(getContext()));
    }

    public void testGetScreenSize() {
        assertNotNull(DeviceInfo.getScreenSize(getContext()));
    }

    public void testGetOperator() {
        assertNotNull(DeviceInfo.getNetworkOperatorName(getContext()));
    }

    public void testGetLocale() {
        assertNotNull(DeviceInfo.getLocale());
    }

    public void testGetLanguage() {
        assertNotNull(DeviceInfo.getLanguage());
    }

    public void testGetCountry() {
        assertNotNull(DeviceInfo.getCountry());
    }

    public void testGetAppVersion() {
        assertNotNull(DeviceInfo.getAppVersion(getContext()));
    }

    public void testGetAppVersionCode() {
       DeviceInfo.getAppVersionCode(getContext());
    }

    public void testGetAppMetaData() {
        assertNull(DeviceInfo.getAppMetaData(getContext(),"test"));
    }

    public void testGetAppStringMetaData() {
        DeviceInfo.getAppStringMetaData(getContext(),"test");
    }

    public void testGetMacAddress() {
        DeviceInfo.getMacAddress(getContext());
    }

    public void testGetIpAddress() {
        assertNotNull(DeviceInfo.getIpAddress(getContext()));
    }

    public void testGetIpStr() {
        assertNotNull(DeviceInfo.getIpStr(getContext()));
    }

    public void testGetCharset() {
        assertNotNull(DeviceInfo.getCharset());
    }

    public void testGetOpenUDID() {
        DeviceInfo.syncOpenUDID(getContext());
        DeviceInfo.getOpenUDID(getContext());
    }

    public void testSyncOpenUDID() {
       DeviceInfo.syncOpenUDID(getContext());
    }

    public void testGetDeviceId() {
        assertNotNull(DeviceInfo.getDeviceId(getContext()));
    }

    public void testGetNetworkType() {
        assertNotNull(DeviceInfo.getNetworkType(getContext()));
    }

    public void testIsWifiConnection() {
        assertNotNull(DeviceInfo.isWifiConnection(getContext()));
    }

    public void testIsConnection() {
        assertNotNull(DeviceInfo.isConnection(getContext()));
    }

    public void testIsGPSLocation() {
        assertNotNull(DeviceInfo.isGPSLocation(getContext()));
    }

    public void testIsNetworkLocation() {
        assertNotNull(DeviceInfo.isNetworkLocation(getContext()));
    }

    public void testGetMD5Fingerprint() {
        assertNotNull(DeviceInfo.getMD5Fingerprint(getContext()));
    }

    public void testGetSHA1Fingerprint() {
        assertNotNull(DeviceInfo.getSHA1Fingerprint(getContext()));
    }

    public void testIsForegroundActivity() {
        //assertNotNull(DeviceInfo.isForegroundActivity("mobi.cangol.mobile.MainActivity",getContext()));
    }

    public void testIsForegroundApplication() {
        //assertNotNull(DeviceInfo.isForegroundApplication(getContext().getPackageName(),getContext()));
    }
}