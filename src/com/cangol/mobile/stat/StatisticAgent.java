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
package com.cangol.mobile.stat;

import java.util.HashMap;

import com.cangol.mobile.utils.DeviceInfo;

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
	private static String url="http://www.stat.com";
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
		params.put("deviceid",DeviceInfo.getDeviceId(context));
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
	public static void send(Context context, String action, String model){
		HashMap<String,String> params=new HashMap<String,String>();
		params.put("action", action);
		params.put("model", model);
		//....
		params.putAll(agent.params);
		send(context, params);
	}
	/**
	 * 启动
	 * @param context
	 * @param action
	 * @param model
	 */
	public static void launcher(Context context, String action, String model){
		send(context,action,model);
	}
}
