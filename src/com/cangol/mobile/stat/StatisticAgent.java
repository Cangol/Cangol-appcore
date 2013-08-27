package com.cangol.mobile.stat;

import java.util.HashMap;

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
	 * 启动游模块
	 * @param context
	 * @param action
	 * @param model
	 */
	public static void launcher(Context context, String action, String model){
		send(context,"launcher","Game");
	}
}
