package mobi.cangol.mobile.utils;

import java.io.IOException;

import mobi.cangol.mobile.logging.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.http.AndroidHttpClient;

public class LocationUtils {
	
	/**
	 * 百度逆地理编码(需要密钥access key)
	 * <br>http://developer.baidu.com/map/webservice-geocoding.htm#.E8.BF.94.E5.9B.9E.E6.95.B0.E6.8D.AE.E8.AF.B4.E6.98.8E
	 * @param lat
	 * @param lng
	 */
	public static void getAddressByBaidu(double lat,double lng,String ak){
		String url = String  
                .format("http://api.map.baidu.com/geocoder/v2/?ak=%s&callback=renderReverse&location=%f,%f&output=json&pois=0",  
                		ak,lat, lng);
		AndroidHttpClient httpClient=AndroidHttpClient.newInstance("android");
		HttpGet request = new HttpGet(url);
		HttpResponse httpResponse;
		try {
			httpResponse = httpClient.execute(request);
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {	
				  HttpEntity httpEntity = httpResponse.getEntity();
				  String response = EntityUtils.toString(httpEntity, "UTF-8");
				  int start="renderReverse&&renderReverse(".length();
				  int end=response.lastIndexOf(")");
				  JSONObject json=new JSONObject(response.substring(start, end));	
				  Log.d("address:"+json.getJSONObject("result").getString("formatted_address")); 
			  }else{
				  Log.d("response fail :"+httpResponse.getStatusLine().getStatusCode() );
			  }
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		} finally {
			 httpClient.close();
	    }
	}
	
	/**
	 * Google逆地理编码
	 * <br>https://developers.google.com/maps/documentation/geocoding/?hl=zh-cn#ReverseGeocoding
	 * @param lat
	 * @param lng
	 */
	public static void getAddressByGoogle(double lat,double lng){
		String url = String  
                .format("http://maps.googleapis.com/maps/api/geocode/json?latlng=%f,%f&language=zh-cn&sensor=true",  
                        lat, lng);
		AndroidHttpClient httpClient=AndroidHttpClient.newInstance("android");
		HttpGet request = new HttpGet(url);
		HttpResponse httpResponse;
		try {
			httpResponse = httpClient.execute(request);
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {	
				  HttpEntity httpEntity = httpResponse.getEntity();
				  String response = EntityUtils.toString(httpEntity, "UTF-8");
				  JSONObject json=new JSONObject(response);
				  Log.d("address:"+json.getJSONArray("results").getJSONObject(0).getString("formatted_address"));
			  }else{
				  Log.d("response fail :"+httpResponse.getStatusLine().getStatusCode() );
			  }
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		} finally {
			 httpClient.close();
	    }
		
	}
}
