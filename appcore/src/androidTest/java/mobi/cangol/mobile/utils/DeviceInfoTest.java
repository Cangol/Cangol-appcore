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

    public void testGetOS() throws Exception {
        assertNotNull(DeviceInfo.getOS());
    }

    public void testGetOSVersion() throws Exception {
        assertNotNull(DeviceInfo.getOSVersion());
    }

    public void testGetDeviceModel() throws Exception {
        assertNotNull(DeviceInfo.getDeviceModel());
    }

    public void testGetDeviceBrand() throws Exception {
        assertNotNull(DeviceInfo.getDeviceBrand());
    }

    public void testGetMobileInfo() throws Exception {
        assertNotNull(DeviceInfo.getMobileInfo());
    }

    public void testGetMemSize() throws Exception {
        assertNotNull(DeviceInfo.getMemSize());
    }

    public void testGetMemInfo() throws Exception {
        assertNotNull(DeviceInfo.getMemInfo());
    }

    public void testGetCPUInfo() throws Exception {
        assertNotNull(DeviceInfo.getCPUInfo());
    }

    public void testGetCPUABI() throws Exception {
        assertNotNull(DeviceInfo.getCPUABI());
    }

    public void testGetResolution() throws Exception {
        assertNotNull(DeviceInfo.getResolution(getContext()));
    }

    public void testGetStatusBarHeight() throws Exception {
        //assertNotNull(DeviceInfo.getStatusBarHeight(getContext()));
    }

    public void testGetDisplayMetrics() throws Exception {
        assertNotNull(DeviceInfo.getDisplayMetrics(getContext()));
    }

    public void testGetDensity() throws Exception {
        assertNotNull(DeviceInfo.getDensity(getContext()));
    }

    public void testGetDensityDpi() throws Exception {
        assertNotNull(DeviceInfo.getDensityDpi(getContext()));
    }

    public void testGetDensityDpiStr() throws Exception {
        assertNotNull(DeviceInfo.getDensityDpiStr(getContext()));
    }

    public void testGetScreenSize() throws Exception {
        assertNotNull(DeviceInfo.getScreenSize(getContext()));
    }

    public void testGetOperator() throws Exception {
        assertNotNull(DeviceInfo.getNetworkOperatorName(getContext()));
    }

    public void testGetLocale() throws Exception {
        assertNotNull(DeviceInfo.getLocale());
    }

    public void testGetLanguage() throws Exception {
        assertNotNull(DeviceInfo.getLanguage());
    }

    public void testGetCountry() throws Exception {
        assertNotNull(DeviceInfo.getCountry());
    }

    public void testGetAppVersion() throws Exception {
        DeviceInfo.getAppVersion(getContext());
    }

    public void testGetAppVersionCode() throws Exception {
        assertNotNull(DeviceInfo.getAppVersionCode(getContext()));
    }

    public void testGetAppMetaData() throws Exception {
        //DeviceInfo.getAppMetaData(getContext(),"test");
    }

    public void testGetAppStringMetaData() throws Exception {
        DeviceInfo.getAppStringMetaData(getContext(),"test");
    }

    public void testGetMacAddress() throws Exception {
        DeviceInfo.getMacAddress(getContext());
    }

    public void testGetIpAddress() throws Exception {
        assertNotNull(DeviceInfo.getIpAddress(getContext()));
    }

    public void testGetIpStr() throws Exception {
        assertNotNull(DeviceInfo.getIpStr(getContext()));
    }

    public void testGetCharset() throws Exception {
        assertNotNull(DeviceInfo.getCharset());
    }

    public void testGetOpenUDID() throws Exception {
        DeviceInfo.syncOpenUDID(getContext());
        DeviceInfo.getOpenUDID(getContext());
    }

    public void testSyncOpenUDID() throws Exception {
       DeviceInfo.syncOpenUDID(getContext());
    }

    public void testGetDeviceId() throws Exception {
        assertNotNull(DeviceInfo.getDeviceId(getContext()));
    }

    public void testGetNetworkType() throws Exception {
        assertNotNull(DeviceInfo.getNetworkType(getContext()));
    }

    public void testIsWifiConnection() throws Exception {
        assertNotNull(DeviceInfo.isWifiConnection(getContext()));
    }

    public void testIsConnection() throws Exception {
        assertNotNull(DeviceInfo.isConnection(getContext()));
    }

    public void testIsGPSLocation() throws Exception {
        assertNotNull(DeviceInfo.isGPSLocation(getContext()));
    }

    public void testIsNetworkLocation() throws Exception {
        assertNotNull(DeviceInfo.isNetworkLocation(getContext()));
    }

    public void testGetMD5Fingerprint() throws Exception {
        assertNotNull(DeviceInfo.getMD5Fingerprint(getContext()));
    }

    public void testGetSHA1Fingerprint() throws Exception {
        assertNotNull(DeviceInfo.getSHA1Fingerprint(getContext()));
    }

    public void testIsForegroundActivity() throws Exception {
        //assertNotNull(DeviceInfo.isForegroundActivity("mobi.cangol.mobile.MainActivity",getContext()));
    }

    public void testIsForegroundApplication() throws Exception {
        //assertNotNull(DeviceInfo.isForegroundApplication(getContext().getPackageName(),getContext()));
    }
}