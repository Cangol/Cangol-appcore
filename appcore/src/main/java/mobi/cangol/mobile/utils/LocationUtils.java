/**
 * Copyright (c) 2013 Cangol
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package mobi.cangol.mobile.utils;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;
import java.util.TimeZone;

import mobi.cangol.mobile.http.HttpClientFactory;
import mobi.cangol.mobile.logging.Log;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class LocationUtils {
    private LocationUtils() {
    }
	/**
	 * 获取偏移值
	 * @return
	 */
	public static double[] adjustLoction(double lng, double lat) {
		String offsetString = getOffset(lat,lng);
		if(offsetString==null)return new double[]{lng,lat};
		int index = offsetString.indexOf(',');
		if (index > 0) {
			// 将坐标值转为18级相应的像素值
			double lngPixel = lonToPixel(lng, 18);
			double latPixel = latToPixel(lat, 18);
			// 获取偏移值
			String offsetX = offsetString.substring(0, index).trim();
			String offsetY = offsetString.substring(index + 1).trim();
			//加上偏移值
			double adjustLngPixel = lngPixel + Double.valueOf(offsetX);
			double adjustLatPixel = latPixel + Double.valueOf(offsetY);
			//由像素值再转为经纬度
			double adjustLng = pixelToLon(adjustLngPixel, 18);
			double adjustLat = pixelToLat(adjustLatPixel, 18);

			return new double[]{(int) (adjustLat * 1000000),(int) (adjustLng * 1000000)};
		}
		//经验公式
		return new double[]{(int) ((lat - 0.0025) * 1000000),(int) ((lng + 0.0045) * 1000000)};
	}

    /**
     * 获取偏移变量
     *
     * @param lat
     * @param lng
     * @return
     */
    public static String getOffset(double lat, double lng) {
        String url = String.format("http://www.mapdigit.com/guidebeemap/offsetinchina.php?lng=%f&lat=%f",lat, lng);
        String response = null;
        OkHttpClient httpClient = HttpClientFactory.createDefaultHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        Response httpResponse = null;
        try {
            httpResponse = httpClient.newCall(request).execute();
            if (httpResponse.isSuccessful()) {
                response = httpResponse.body().string();
            }
            return response;
        } catch (IOException e) {
            Log.d(e.getMessage());
            return null;
        } finally {
            if (httpResponse != null)
                httpResponse.close();
        }
    }

    /**
     * 经度到像素X值
     *
     * @param lng
     * @param zoom
     * @return
     */
    public static double lonToPixel(double lng, int zoom) {
        return (lng + 180) * (256L << zoom) / 360;
    }

    /**
     * 像素X到经度
     *
     * @param pixelX
     * @param zoom
     * @return
     */
    public static double pixelToLon(double pixelX, int zoom) {
        return pixelX * 360 / (256L << zoom) - 180;
    }

    /**
     * 纬度到像素Y
     *
     * @param lat
     * @param zoom
     * @return
     */
    public static double latToPixel(double lat, int zoom) {
        double siny = Math.sin(lat * Math.PI / 180);
        double y = Math.log((1 + siny) / (1 - siny));
        return (128 << zoom) * (1 - y / (2 * Math.PI));
    }

    /**
     * 像素Y到纬度
     *
     * @param pixelY
     * @param zoom
     * @return
     */
    public static double pixelToLat(double pixelY, int zoom) {
        double y = 2 * Math.PI * (1 - pixelY / (128 << zoom));
        double z = Math.pow(Math.E, y);
        double siny = (z - 1) / (z + 1);
        return Math.asin(siny) * 180 / Math.PI;
    }

    /**
     * 获取UTC时间
     *
     * @return
     */
    public static Long getUTCTime() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("gmt"));
        return cal.getTimeInMillis();
    }

    /**
     * 获取本地时间
     *
     * @return
     */
    public static Long getLocalTime() {
        Calendar cal = Calendar.getInstance();
        return cal.getTimeInMillis();
    }

    /**
     * 百度逆地理编码(需要密钥access key)
     * <br>http://developer.baidu.com/map/webservice-geocoding.htm#.E8.BF.94.E5.9B.9E.E6.95.B0.E6.8D.AE.E8.AF.B4.E6.98.8E
     *
     * @param lat
     * @param lng
     * @return
     */
    public static String getAddressByBaidu(double lat, double lng, String ak) {
        String url = String
                .format("http://api.map.baidu.com/geocoder/v2/?ak=%s&callback=renderReverse&location=%f,%f&output=json&pois=0",
                        ak, lat, lng);
        String address = null;
        OkHttpClient httpClient = HttpClientFactory.createDefaultHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        Response httpResponse = null;
        try {
            httpResponse = httpClient.newCall(request).execute();
            if (httpResponse.isSuccessful()) {
                String response = httpResponse.body().string();
                int start = "renderReverse&&renderReverse(".length();
                int end = response.lastIndexOf(')');
                JSONObject json = new JSONObject(response.substring(start, end));
                address = json.getJSONObject("result").getString("formatted_address");
            }
            return address;
        }catch (Exception e) {
            Log.d(e.getMessage());
            return null;
        } finally {
            if (httpResponse != null)
                httpResponse.close();
        }
    }

    /**
     * Google逆地理编码
     * <br>https://developers.google.com/maps/documentation/geocoding/?hl=zh-cn#ReverseGeocoding
     *
     * @param lat
     * @param lng
     * @return
     */
    public static String getAddressByGoogle(double lat, double lng) {
        String url = String
                .format("http://maps.googleapis.com/maps/api/geocode/json?latlng=%f,%f&language=zh-cn&sensor=true",
                        lat, lng);
        String address = null;
        OkHttpClient httpClient = HttpClientFactory.createDefaultHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        Response httpResponse = null;
        try {
            httpResponse = httpClient.newCall(request).execute();
            if (httpResponse.isSuccessful()) {
                String response = httpResponse.body().string();
                JSONObject json = new JSONObject(response);
                address = json.getJSONArray("results").getJSONObject(0).getString("formatted_address");
            }
            return address;
        }catch (Exception e) {
            Log.d(e.getMessage());
            return null;
        } finally {
            if (httpResponse != null)
                httpResponse.close();
        }

    }
}
