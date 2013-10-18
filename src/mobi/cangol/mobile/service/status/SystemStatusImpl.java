package mobi.cangol.mobile.service.status;

import mobi.cangol.mobile.utils.DeviceInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.util.Log;

public class SystemStatusImpl implements SystemStatus {
	private final static String TAG="SystemStatus";
	private Context mContext = null;
	private StatusListener mStatusListener;
	@Override
	public void init() {
		IntentFilter intentFileter=new IntentFilter("android.intent.action.PACKAGE_ADDED");
		intentFileter.addAction("android.intent.action.ACTION_PACKAGE_REPLACED");
		intentFileter.addAction("android.intent.action.PACKAGE_REMOVED");
		intentFileter.addDataScheme("package");
		mContext.registerReceiver(appStatusReceiver, intentFileter);
		
		IntentFilter intentFileter1=new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
		mContext.registerReceiver(networkStatusReceiver, intentFileter1);
		
		IntentFilter intentFileter2=new IntentFilter("android.intent.action.ACTION_MEDIA_MOUNTED");
		intentFileter2.addAction("android.intent.action.ACTION_MEDIA_REMOVED");
		mContext.registerReceiver(storageStatusReceiver, intentFileter2);
	}

	@Override
	public void setContext(Context context) {
		mContext=context;
	}

	@Override
	public String getName() {
		return "status";
	}

	@Override
	public void destory() {
		mContext.unregisterReceiver(appStatusReceiver);
		mContext.unregisterReceiver(networkStatusReceiver);
		mContext.unregisterReceiver(storageStatusReceiver);
	}

	@Override
	public boolean isConnection() {
		return DeviceInfo.isConnection(mContext);
	}

	@Override
	public boolean isWifiConnection() {
		return DeviceInfo.isWifiConnection(mContext);
	}

	@Override
	public void setStatusListner(StatusListener statusListener) {
		mStatusListener=statusListener;
	}
	private BroadcastReceiver appStatusReceiver=new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			String packageName = intent.getDataString();   
			String pName = packageName.substring(8,packageName.length());
			Log.d(TAG, intent.getAction()+"  "+packageName);
			if (intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED)
					||intent.getAction().equals(Intent.ACTION_PACKAGE_REPLACED)) {
				Log.d(TAG,"install:"  + pName);
				if(null!=mStatusListener)mStatusListener.apkInstall(context,pName);
			
			}else if (intent.getAction().equals(Intent.ACTION_PACKAGE_REMOVED)) {   
				Log.d(TAG,"uninstall:"  + pName);
				if(null!=mStatusListener)mStatusListener.apkUninstall(context,pName);
			}
		}
		
	};
	private BroadcastReceiver networkStatusReceiver=new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(TAG, "Action " + intent.getAction());
			State wifiState = null;
			State mobileState = null;
			ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			wifiState = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
			mobileState = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
			if (wifiState != null && mobileState != null
					&& State.CONNECTED != wifiState
					&& State.CONNECTED == mobileState) {
				// 手机网络连接成功
				Log.d(TAG, "手机网络连接成功 ");
				 if(null!=mStatusListener)mStatusListener.networkTo3G(context);
			} else if (wifiState != null && mobileState != null
					&& State.CONNECTED != wifiState
					&& State.CONNECTED != mobileState) {
				// 手机没有任何的网络
				Log.d(TAG, "手机没有任何的网络,网络中断 ");
				 if(null!=mStatusListener)mStatusListener.networkDisconnect(context);
			} else if (wifiState != null && State.CONNECTED == wifiState) {
				// 无线网络连接成功
				Log.d(TAG, " 无线网络连接成功");
				 if(null!=mStatusListener)mStatusListener.networkConnect(context);
			}
			
		}
		
	};
	/**
	 *
	 *ACTION_MEDIA_MOUNTED 扩展介质被插入，而且已经被挂载。
	 *ACTION_MEDIA_UNMOUNTED 扩展介质存在，但是还没有被挂载 (mount)。
	 *
	 *ACTION_MEDIA_EJECT  用户想要移除扩展介质(拔掉扩展卡)。
	 *ACTION_MEDIA_SHARED 扩展介质的挂载被解除 (unmount)，因为它已经作为 USB 大容量存储被共享。
	 *ACTION_MEDIA_BAD_REMOVAL 扩展介质(扩展卡)已经从 SD 卡插槽拔出，但是挂载点 (mount point) 还没解除 (unmount)。
	 *ACTION_MEDIA_REMOVED 扩展介质被移除。
	 */
	private BroadcastReceiver storageStatusReceiver=new BroadcastReceiver(){
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(TAG, "Action " + intent.getAction());
			if(intent.getAction()==Intent.ACTION_MEDIA_EJECT){
				
			}else if(intent.getAction()==Intent.ACTION_MEDIA_SHARED){
				
			}else if(intent.getAction()==Intent.ACTION_MEDIA_BAD_REMOVAL){
				
			}else if(intent.getAction()==Intent.ACTION_MEDIA_REMOVED){
				 if(null!=mStatusListener)mStatusListener.storageRemove(context);
			}else if(intent.getAction()==Intent.ACTION_MEDIA_MOUNTED){
				 if(null!=mStatusListener)mStatusListener.storageMount(context);
			}
			
		}
	};
}
