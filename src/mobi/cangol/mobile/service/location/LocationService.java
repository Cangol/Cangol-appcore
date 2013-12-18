/** 
 * Copyright (c) 2013 Cangol
 * 
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package mobi.cangol.mobile.service.location;

import mobi.cangol.mobile.service.AppService;
import android.location.Location;

public interface LocationService extends AppService{
	public final static String LOCATIONSERVICE_BETTERTIME="better_time";
	public final static String LOCATIONSERVICE_TIMEOUT="timeout";
	public final static String LOCATIONSERVICE_BAIDU_AK="baidu_ak";
	public final static String LOCATIONSERVICE_GPS_MINTIME="gps_min_time";
	public final static String LOCATIONSERVICE_GPS_MINDISTANCE="gps_min_distance";
	public final static String LOCATIONSERVICE_NETWORK_MINTIME="network_min_time";
	public final static String LOCATIONSERVICE_NETWORK_MINDISTANCE="network_min_distance";
	
	void requestLocationUpdates();
	
	void removeLocationUpdates();
	
	Location getLastKnownLocation();
	
	boolean isBetterLocation(Location mLocation);

	void setBetterLocationListener(BetterLocationListener locationListener);
	
	String getAddress();
}

