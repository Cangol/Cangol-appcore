package mobi.cangol.mobile.service.status;

import java.util.ArrayList;

import mobi.cangol.mobile.logging.Log;
import mobi.cangol.mobile.utils.DeviceInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
public class StatusServiceImpl implements StatusService {
	private final static String TAG="StatusService";
	private boolean debug=false;
	private Context mContext = null;
	private TelephonyManager mTelephonyManager;
	private boolean mCallingState=true;
	protected ArrayList<StatusListener> listeners = new ArrayList<StatusListener>();  
	@Override

	public void create(Context context) {
		mContext=context;
		
		IntentFilter intentFileter1=new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
		mContext.registerReceiver(networkStatusReceiver, intentFileter1);
		
		IntentFilter intentFileter2=new IntentFilter("android.intent.action.ACTION_MEDIA_MOUNTED");
		intentFileter2.addAction("android.intent.action.ACTION_MEDIA_REMOVED");
		mContext.registerReceiver(storageStatusReceiver, intentFileter2);
		
		mTelephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		mTelephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
	}

	@Override
	public void destory() {
		mContext.unregisterReceiver(networkStatusReceiver);
		mContext.unregisterReceiver(storageStatusReceiver);
		mTelephonyManager.listen(null, PhoneStateListener.LISTEN_NONE);
	}
	@Override
	public String getName() {
		return "StatusService";
	}

	@Override
	public void setDebug(boolean debug) {
		this.debug=debug;
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
	public boolean isGPSLocation() {
		return DeviceInfo.isGPSLocation(mContext);
	}
	@Override
	public boolean isNetworkLocation() {
		return DeviceInfo.isNetworkLocation(mContext);
	}
	
	@Override
	public void registerStatusListener(StatusListener statusListener) {
		if (statusListener == null) {  
	        throw new IllegalArgumentException("The StatusListener is null.");  
	    }  
        synchronized(listeners) {  
            if (listeners.contains(statusListener)) {  
                throw new IllegalStateException("StatusListener " + statusListener + " is already registered.");  
            }  
            listeners.add(statusListener);  
            
        }  
	}
	
	public void unregisterStatusListener(StatusListener statusListener) {
		if (statusListener == null) {  
	        throw new IllegalArgumentException("The StatusListener is null.");  
	    }  
        synchronized(listeners) {  
        	 if (listeners.contains(statusListener)) {  
        		 listeners.remove(statusListener);  
            }else{
            	 throw new IllegalStateException("StatusListener " + statusListener + " is not exist.");  
            }
        }
	}
	
	public void notifyNetworkConnect(Context context){
		for(StatusListener listener:listeners){
			if(listener!=null)listener.networkConnect(context);
		}
	}
	
	public void notifyNetworkDisconnect(Context context){
		for(StatusListener listener:listeners){
			if(listener!=null){
				listener.networkDisconnect(context);
			}else{
				Log.e("null="+listener);
			}
		}
	}
	public void notifyNetworkTo3G(Context context){
		for(StatusListener listener:listeners){
			if(listener!=null)listener.networkTo3G(context);
		}
	}
	public void notifyStorageRemove(Context context){
		for(StatusListener listener:listeners){
			if(listener!=null)listener.storageRemove(context);
		}
	}
	
	public void notifyStorageMount(Context context){
		for(StatusListener listener:listeners){
			if(listener!=null)listener.storageMount(context);
		}
	}
	
	public void notifyCallStateIdle(){
		for(StatusListener listener:listeners){
			if(listener!=null)listener.callStateIdle();
		}
	}
	
	public void notifyCallStateOffhook(){
		for(StatusListener listener:listeners){
			if(listener!=null)listener.callStateOffhook();
		}
	}
	public void notifyCallStateRinging(){
		for(StatusListener listener:listeners){
			if(listener!=null)listener.callStateRinging();
		}
	}
	
	private BroadcastReceiver networkStatusReceiver=new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			if(debug)Log.d(TAG, "Action " + intent.getAction());
			State wifiState = null;
			State mobileState = null;
			ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfoWifi =cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			if(networkInfoWifi!=null)wifiState = networkInfoWifi.getState();
			NetworkInfo networkInfo =cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			if(networkInfo!=null)mobileState = networkInfo.getState();
			
			if (wifiState != null && mobileState != null
					&& State.CONNECTED != wifiState
					&& State.CONNECTED == mobileState) {
				// 手机网络连接成功
				if(debug)Log.d(TAG, "手机网络连接成功 ");
				 if(listeners.size()>0)notifyNetworkTo3G(context);
			} else if (wifiState != null && mobileState != null
					&& State.CONNECTED != wifiState
					&& State.CONNECTED != mobileState) {
				// 手机没有任何的网络
				if(debug)Log.d(TAG, "手机没有任何的网络,网络中断 ");
				 if(listeners.size()>0)notifyNetworkDisconnect(context);
			} else if (wifiState != null && State.CONNECTED == wifiState) {
				// 无线网络连接成功
				if(debug)Log.d(TAG, " 无线网络连接成功");
				 if(listeners.size()>0)notifyNetworkConnect(context);
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
			if(debug)Log.d(TAG, "Action " + intent.getAction());
			if(intent.getAction()==Intent.ACTION_MEDIA_EJECT){
				
			}else if(intent.getAction()==Intent.ACTION_MEDIA_SHARED){
				
			}else if(intent.getAction()==Intent.ACTION_MEDIA_BAD_REMOVAL){
				
			}else if(intent.getAction()==Intent.ACTION_MEDIA_REMOVED){
				 if(listeners.size()>0)notifyStorageRemove(context);
			}else if(intent.getAction()==Intent.ACTION_MEDIA_MOUNTED){
				 if(listeners.size()>0)notifyStorageMount(context);
			}
			
		}
	};
	private PhoneStateListener phoneStateListener=new PhoneStateListener() {

	    public Boolean phoneRinging = false;

	    public void onCallStateChanged(int state, String incomingNumber) {

	        switch (state) {
	        case TelephonyManager.CALL_STATE_IDLE:
	        	//闲置 挂起
	            if(debug)Log.d(TAG, "CALL_STATE_IDLE");
	            mCallingState=false;
	            if(listeners.size()>0)notifyCallStateIdle();
	            break;
	        case TelephonyManager.CALL_STATE_OFFHOOK:
	        	//摘机
	            if(debug)Log.d(TAG, "CALL_STATE_OFFHOOK");
	            mCallingState=true;
	            if(listeners.size()>0)notifyCallStateOffhook();
	            break;
	        case TelephonyManager.CALL_STATE_RINGING:
	        	//响铃
	            if(debug)Log.d(TAG, "CALL_STATE_RINGING");
	            mCallingState=true;
	            if(listeners.size()>0)notifyCallStateRinging();
	            break;
	        }
	    }

	};
	@Override
	public boolean isCallingState() {
		return mCallingState;
	}
}
