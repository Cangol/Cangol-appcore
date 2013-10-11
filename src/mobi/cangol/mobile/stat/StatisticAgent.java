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
package mobi.cangol.mobile.stat;

import java.util.HashMap;

import mobi.cangol.mobile.utils.DeviceInfo;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;

/**
 * @Description StatisticAgent.java 
 * @author xuewu.wei
 * @date 2013-3-3
 */
public  class StatisticAgent {
	private static StatisticAgent agent;
	private static String url="http://stat.cangol.mobi";
	private HashMap<String,String> params=new HashMap<String,String>();
	private final Handler mHandler;
	private StatisticPost statisticPost;
	private Context context;
	private StatisticAgent(Context context){
		HandlerThread localHandlerThread = new HandlerThread("StatisticAgent");
	    localHandlerThread.start();
	    this.mHandler = new Handler(localHandlerThread.getLooper());
	    this.statisticPost=new StatisticPost();
	    initCommonParam();
	}
	private void  initCommonParam(){
		params.put("appversion",DeviceInfo.getAppVersion(context));
		params.put("os",DeviceInfo.getOS());
		params.put("osversion",DeviceInfo.getOSVersion());
		params.put("deviceid",DeviceInfo.getDeviceId(context));
		params.put("device",DeviceInfo.getDevice());
		params.put("carrier",DeviceInfo.getCarrier(context));
		params.put("resolution",DeviceInfo.getResolution(context));
		params.put("cpu",DeviceInfo.getCPUInfo());
		params.put("country",DeviceInfo.getCountry());
		params.put("language",DeviceInfo.getLanguage());
		params.put("ip",DeviceInfo.getIpStr(context));
		params.put("network",DeviceInfo.getNetworkType(context));
		//....
	}
	public static void init(Context context){
		agent=new StatisticAgent(context);
	}
	public static void cancle(Context context, boolean mayInterruptIfRunning){
		agent.statisticPost.cancelRequests(context,  mayInterruptIfRunning);
	}
	public static void send(Context context, HashMap<String,String> params){
		params.putAll(agent.params);
		agent.statisticPost.send(context,url, params);
	}
	public static void send(Context context, String action, HashMap<String,String> map){
		HashMap<String,String> params=new HashMap<String,String>();
		params.put("action", action);
		if(map!=null)params.putAll(map);
		params.putAll(agent.params);
		send(context, params);
	}
	public static void install(Context context){
		send(context,"install",null);
	}
	public static void start(Context context){
		send(context,"launcher",null);
	}
	public static void exit(Context context){
		send(context,"exit",null);
	}
	public static void launcher(Context context, String module){
		HashMap<String,String> params=new HashMap<String,String>();
		params.put("module", module);
		send(context,"launcher",params);
	}
	public static void show(Context context, String module, String view){
		HashMap<String,String> params=new HashMap<String,String>();
		params.put("module", module);
		params.put("view", view);
		send(context,"show",params);
	}
	public static void event(Context context, String module, String view, String opt){
		HashMap<String,String> params=new HashMap<String,String>();
		params.put("module", module);
		params.put("view", view);
		params.put("opt", opt);
		send(context,"event",params);
	}
	public static void click(Context context, String module, String view, String button){
		HashMap<String,String> params=new HashMap<String,String>();
		params.put("module", module);
		params.put("view", view);
		params.put("button", button);
		send(context,"click",params);
	}
}
