/**
 * Copyright (c) 2013 Cangol
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package mobi.cangol.mobile.utils

import mobi.cangol.mobile.http.HttpClientFactory
import mobi.cangol.mobile.logging.Log
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import java.util.*


object LocationUtils {

    /**
     * 获取UTC时间
     *
     * @return
     */
    @JvmStatic
    fun utcTime(): Long {
        val cal = Calendar.getInstance()
        cal.timeZone = TimeZone.getTimeZone("gmt")
        return cal.timeInMillis
    }

    /**
     * 获取本地时间
     *
     * @return
     */
    @JvmStatic
    fun localTime(): Long {
        return Calendar.getInstance().timeInMillis
    }

    /**
     * 获取偏移值
     *
     * @return
     */
    @JvmStatic
    fun adjustLoction(lng: Double, lat: Double): DoubleArray {
        val offsetString = getOffset(lat, lng) ?: return doubleArrayOf(lng, lat)
        val index = offsetString.indexOf(',')
        if (index > 0) {
            // 将坐标值转为18级相应的像素值
            val lngPixel = lonToPixel(lng, 18)
            val latPixel = latToPixel(lat, 18)
            // 获取偏移值
            val offsetX = offsetString.substring(0, index).trim { it <= ' ' }
            val offsetY = offsetString.substring(index + 1).trim { it <= ' ' }
            //加上偏移值
            val adjustLngPixel = lngPixel + java.lang.Double.valueOf(offsetX)
            val adjustLatPixel = latPixel + java.lang.Double.valueOf(offsetY)
            //由像素值再转为经纬度
            val adjustLng = pixelToLon(adjustLngPixel, 18)
            val adjustLat = pixelToLat(adjustLatPixel, 18)

            return doubleArrayOf((adjustLat * 1000000).toInt().toDouble(), (adjustLng * 1000000).toInt().toDouble())
        }
        //经验公式
        return doubleArrayOf(((lat - 0.0025) * 1000000).toInt().toDouble(), ((lng + 0.0045) * 1000000).toInt().toDouble())
    }

    /**
     * 获取偏移变量
     *
     * @param lat
     * @param lng
     * @return
     */
    @JvmStatic
    fun getOffset(lat: Double, lng: Double): String? {
        val url = String.format("http://www.mapdigit.com/guidebeemap/offsetinchina.php?lng=%f&lat=%f", lat, lng)
        var response: String? = null
        val httpClient = HttpClientFactory.createDefaultHttpClient()
        val request = Request.Builder()
                .url(url)
                .get()
                .build()
        var httpResponse: Response? = null
        try {
            httpResponse = httpClient.newCall(request).execute()
            if (httpResponse!!.isSuccessful) {
                response = httpResponse.body()!!.string()
            }
            return response
        } catch (e: IOException) {
            Log.d(e.message)
            return null
        } finally {
            httpResponse?.close()
        }
    }

    /**
     * 经度到像素X值
     *
     * @param lng
     * @param zoom
     * @return
     */
    @JvmStatic
    fun lonToPixel(lng: Double, zoom: Int): Double {
        return (lng + 180) * (256L shl zoom) / 360
    }

    /**
     * 像素X到经度
     *
     * @param pixelX
     * @param zoom
     * @return
     */
    @JvmStatic
    fun pixelToLon(pixelX: Double, zoom: Int): Double {
        return pixelX * 360 / (256L shl zoom) - 180
    }

    /**
     * 纬度到像素Y
     *
     * @param lat
     * @param zoom
     * @return
     */
    @JvmStatic
    fun latToPixel(lat: Double, zoom: Int): Double {
        val siny = Math.sin(lat * Math.PI / 180)
        val y = Math.log((1 + siny) / (1 - siny))
        return (128 shl zoom) * (1 - y / (2 * Math.PI))
    }

    /**
     * 像素Y到纬度
     *
     * @param pixelY
     * @param zoom
     * @return
     */
    @JvmStatic
    fun pixelToLat(pixelY: Double, zoom: Int): Double {
        val y = 2.0 * Math.PI * (1 - pixelY / (128 shl zoom))
        val z = Math.pow(Math.E, y)
        val siny = (z - 1) / (z + 1)
        return Math.asin(siny) * 180 / Math.PI
    }

    /**
     * 百度逆地理编码(需要密钥access key)
     * <br></br>http://developer.baidu.com/map/webservice-geocoding.htm#.E8.BF.94.E5.9B.9E.E6.95.B0.E6.8D.AE.E8.AF.B4.E6.98.8E
     *
     * @param lat
     * @param lng
     * @return
     */
    @JvmStatic
    fun getAddressByBaidu(lat: Double, lng: Double, ak: String): String? {
        val url = String
                .format("http://api.map.baidu.com/geocoder/v2/?ak=%s&callback=renderReverse&location=%f,%f&output=json&pois=0",
                        ak, lat, lng)
        var address: String? = null
        val httpClient = HttpClientFactory.createDefaultHttpClient()
        val request = Request.Builder()
                .url(url)
                .get()
                .build()
        var httpResponse: Response? = null
        try {
            httpResponse = httpClient.newCall(request).execute()
            if (httpResponse!!.isSuccessful) {
                val response = httpResponse.body()!!.string()
                val start = "renderReverse&&renderReverse(".length
                val end = response.lastIndexOf(')')
                val json = JSONObject(response.substring(start, end))
                address = json.getJSONObject("result").getString("formatted_address")
            }
            return address
        } catch (e: Exception) {
            Log.d(e.message)
            return null
        } finally {
            httpResponse?.close()
        }
    }

    /**
     * Google逆地理编码
     * <br></br>https://developers.google.com/maps/documentation/geocoding/?hl=zh-cn#ReverseGeocoding
     *
     * @param lat
     * @param lng
     * @return
     */
    @JvmStatic
    fun getAddressByGoogle(lat: Double, lng: Double): String? {
        val url = String
                .format("http://maps.googleapis.com/maps/api/geocode/json?latlng=%f,%f&language=zh-cn&sensor=true",
                        lat, lng)
        var address: String? = null
        val httpClient = HttpClientFactory.createDefaultHttpClient()
        val request = Request.Builder()
                .url(url)
                .get()
                .build()
        var httpResponse: Response? = null
        try {
            httpResponse = httpClient.newCall(request).execute()
            if (httpResponse!!.isSuccessful) {
                val response = httpResponse.body()!!.string()
                val json = JSONObject(response)
                address = json.getJSONArray("results").getJSONObject(0).getString("formatted_address")
            }
            return address
        } catch (e: Exception) {
            Log.d(e.message)
            return null
        } finally {
            httpResponse?.close()
        }

    }
}
