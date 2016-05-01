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
package mobi.cangol.mobile.service.upgrade;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dalvik.system.DexClassLoader;
import mobi.cangol.mobile.CoreApplication;
import mobi.cangol.mobile.http.download.DownloadHttpClient;
import mobi.cangol.mobile.http.download.DownloadResponseHandler;
import mobi.cangol.mobile.logging.Log;
import mobi.cangol.mobile.service.AppService;
import mobi.cangol.mobile.service.Service;
import mobi.cangol.mobile.service.ServiceProperty;
import mobi.cangol.mobile.service.conf.ConfigService;
import mobi.cangol.mobile.service.download.DownloadNotification;
import mobi.cangol.mobile.utils.AppUtils;

/**
 * @author Cangol
 */
@Service("UpgradeService")
public class UpgradeServiceImpl implements UpgradeService{
	private final static String TAG="UpgradeService";
	private boolean debug=false;
	private Context mContext = null;
	private ServiceProperty mServiceProperty=null;
	private ConfigService mConfigService;
	private List<Integer> mIds=new ArrayList<Integer>();
	private Map<String,OnUpgradeListener> mOnUpgradeListeners;
	private DownloadHttpClient mDownloadHttpClient;
	@Override
	public void onCreate(Context context) {
		mContext=context;
		CoreApplication app=(CoreApplication) mContext.getApplicationContext();
		mConfigService=(ConfigService) app.getAppService(AppService.CONFIG_SERVICE);
		mDownloadHttpClient=DownloadHttpClient.build(TAG);
		mOnUpgradeListeners=new HashMap<String,OnUpgradeListener>();
	}
	@Override
	public void init(ServiceProperty serviceProperty) {
		this.mServiceProperty=serviceProperty;
	}
	@Override
	public String getName() {
		return TAG;
	}

	@Override
	public void onDestroy() {
		if(debug)Log.d("onDestory");
		DownloadHttpClient.cancel(TAG, true);
		NotificationManager notificationManager=(NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
		for(Integer id:mIds){
			notificationManager.cancel(id);
			if(debug)Log.d("notification cancel "+id);
		}
	}
	
	@Override
	public ServiceProperty getServiceProperty() {
		return mServiceProperty;
	}
	@Override
	public ServiceProperty defaultServiceProperty() {
		ServiceProperty sp=new ServiceProperty(TAG);
		return sp;
	}
	@Override
	public void setDebug(boolean debug) {
		this.debug=debug;
	}

//	@Override
//	public void upgradeRes(String filename, String url,boolean notification, boolean load) {
//		upgrade(filename,url,notification, UpgradeType.RES,load);
//	}
//
//	@Override
//	public void upgradeDex(String filename, String url,boolean notification, boolean load) {
//		upgrade(filename,url,notification, UpgradeType.DEX,load);
//	}
//
//	@Override
//	public void upgradeSo(String filename, String url,boolean notification, boolean load) {
//		upgrade(filename,url,notification, UpgradeType.SO,load);
//	}
//
//	@Override
//	public void upgradeApk(String filename, String url,boolean notification, boolean install) {
//		upgrade(filename,url,notification, UpgradeType.APK,install);
//	}
	@Override
	public void upgrade(final String filename,String url,final boolean notification){
		upgrade(filename,url,notification, UpgradeType.OTHER,false);
	}

	private void upgrade(final String filename,String url,final boolean notification,final UpgradeType upgradeType,final boolean load){
		final String savePath=mConfigService.getUpgradeDir()+File.separator +filename;
		if(debug)Log.d("upgrade savePath:"+savePath);
		File saveFile=new File(savePath);
		if(saveFile.exists())saveFile.delete();
		final DownloadNotification  downloadNotification=new DownloadNotification(mContext,filename,savePath,createFinishIntent(savePath,upgradeType));
		if (notification){
			mIds.add(downloadNotification.getId());
		}
		mDownloadHttpClient.send(filename, url, new DownloadResponseHandler(){
			@Override
			public void onWait() {
				super.onWait();
				if (notification)downloadNotification.createNotification();
			}
			@Override
			public void onStart(long from) {
				super.onStart(from);
			}
			@Override
			public void onStop(long end) {
				super.onStop(end);
				if (notification){
					downloadNotification.cancelNotification();
					mIds.remove(Integer.valueOf(downloadNotification.getId()));
				}
				notifyUpgradeFailure(filename,"stop");
			}
			@Override
			public void onFinish(long end) {
				super.onFinish(end);
				if (notification)
					downloadNotification.finishNotification();
				if(load)
					makeLoad(savePath,upgradeType);

				notifyUpgradeFinish(filename,savePath);
			}
			@Override
			public void onProgressUpdate(long end,int progress, int speed) {
				super.onProgressUpdate(end,progress, speed);
				if (notification)
					downloadNotification.updateNotification(progress,speed);

				notifyUpgradeProgress(filename,speed,progress);
			}
			@Override
			public void onFailure(Throwable error, String content) {
				super.onFailure(error, content);
				if (notification)
					downloadNotification.failureNotification();
				notifyUpgradeFailure(filename,content);
			}

		}, saveFile.length(), savePath);
	}
	private void makeLoad(String savePath,UpgradeType upgradeType){
		switch (upgradeType){
			case APK:
				AppUtils.install(mContext,savePath);
				break;
			case RES:

				break;
			case DEX:
//				DexClassLoader dexClassLoader = new DexClassLoader(savePath,mConfigService.getTempDir().getAbsolutePath(), null, mContext.getClassLoader());
//				try {
//					Class clazz = dexClassLoader.loadClass("className");
//				} catch (ClassNotFoundException e) {
//					e.printStackTrace();
//				}
				break;
			case SO:
				System.load(savePath);
				break;
			case OTHER:
				new Intent();
				break;
		}
	}
	private Intent createFinishIntent(String savePath,UpgradeType upgradeType){
		Intent intent =null;
		switch (upgradeType){
			case APK:
				Uri uri = Uri.fromFile(new File(savePath));
				intent = new Intent(Intent.ACTION_VIEW);
				intent.setDataAndType(uri, "application/vnd.android.package-archive");
				break;
			case RES:

				break;
			case DEX:

				break;
			case SO:

				break;
			case OTHER:
				new Intent();
				break;
			default:
				new Intent();
				break;
		}
		return  intent;
	}
	@Override
	public void cancel(String filename) {
		mDownloadHttpClient.cancelRequests(filename,true);
	}

	public void notifyUpgradeFinish(String filename,String filepath) {
		if(mOnUpgradeListeners.containsKey(filename)){
			mOnUpgradeListeners.get(filename).onFinish(filepath);
		}
	}
	public void notifyUpgradeProgress(String filename,int speed,int progress) {
		if(mOnUpgradeListeners.containsKey(filename)){
			mOnUpgradeListeners.get(filename).progress(speed,progress);
		}
	}
	public void notifyUpgradeFailure(String filename,String error) {
		if(mOnUpgradeListeners.containsKey(filename)){
			mOnUpgradeListeners.get(filename).onFailure(error);
		}
	}
	@Override
	public void setOnUpgradeListener(String filename,OnUpgradeListener onUpgradeListener) {
		if(!mOnUpgradeListeners.containsKey(filename)){
			mOnUpgradeListeners.put(filename,onUpgradeListener);
		}
	}
}
