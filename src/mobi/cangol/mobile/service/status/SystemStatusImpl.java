package mobi.cangol.mobile.service.status;

import mobi.cangol.mobile.utils.DeviceInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
		mContext.registerReceiver(appInstallReceiver, intentFileter);
		
		IntentFilter intentFileter1=new IntentFilter("android.intent.action.PACKAGE_ADDED");
		intentFileter1.addAction("android.intent.action.ACTION_PACKAGE_REPLACED");
		intentFileter1.addAction("android.intent.action.PACKAGE_REMOVED");
		intentFileter1.addDataScheme("package");
		mContext.registerReceiver(networkReceiver, intentFileter1);
		
		IntentFilter intentFileter2=new IntentFilter("android.intent.action.PACKAGE_ADDED");
		intentFileter2.addAction("android.intent.action.ACTION_PACKAGE_REPLACED");
		intentFileter2.addAction("android.intent.action.PACKAGE_REMOVED");
		intentFileter2.addDataScheme("package");
		mContext.registerReceiver(storageReceiver, intentFileter2);
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
		mContext.unregisterReceiver(appInstallReceiver);
		mContext.unregisterReceiver(networkReceiver);
		mContext.unregisterReceiver(storageReceiver);
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
	private BroadcastReceiver appInstallReceiver=new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			String packageName = intent.getDataString();   
			String pName = packageName.substring(8,packageName.length());
			Log.d(TAG, intent.getAction()+"  "+packageName);
			if (intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED)
					||intent.getAction().equals(Intent.ACTION_PACKAGE_REPLACED)) {
				Log.d(TAG,"install:"  + pName);
				if(null!=mStatusListener)mStatusListener.apkInstall(pName);
			
			}else if (intent.getAction().equals(Intent.ACTION_PACKAGE_REMOVED)) {   
				Log.d(TAG,"uninstall:"  + pName);
				if(null!=mStatusListener)mStatusListener.apkUninstall(pName);
			}
		}
		
	};
	private BroadcastReceiver networkReceiver=new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			
			
		}
		
	};
	private BroadcastReceiver storageReceiver=new BroadcastReceiver(){
		@Override
		public void onReceive(Context context, Intent intent) {
			
			
		}
	};
}
