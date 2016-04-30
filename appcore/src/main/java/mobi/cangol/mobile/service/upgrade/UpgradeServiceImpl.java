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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import mobi.cangol.mobile.CoreApplication;
import mobi.cangol.mobile.http.download.DownloadHttpClient;
import mobi.cangol.mobile.http.download.DownloadResponseHandler;
import mobi.cangol.mobile.logging.Log;
import mobi.cangol.mobile.service.AppService;
import mobi.cangol.mobile.service.Service;
import mobi.cangol.mobile.service.ServiceProperty;
import mobi.cangol.mobile.service.conf.ConfigService;
import mobi.cangol.mobile.service.download.Download;
import mobi.cangol.mobile.service.download.DownloadNotification;
import mobi.cangol.mobile.utils.AppUtils;

import android.app.NotificationManager;
import android.content.Context;

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
	private UpgradeListener mUpgradeListener;
	@Override
	public void onCreate(Context context) {
		mContext=context;
		CoreApplication app=(CoreApplication) mContext.getApplicationContext();
		mConfigService=(ConfigService) app.getAppService(AppService.CONFIG_SERVICE);
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
	@Override
	public void setOnUpgradeListener(UpgradeListener upgradeListener) {
		mUpgradeListener=upgradeListener;
	}
	@Override
	public void upgrade(String name,String url,final boolean constraint){
		if(mUpgradeListener!=null)mUpgradeListener.upgrade(constraint);
		upgradeApk(name,url,true,true);
	}
	@Override
	public void upgradeApk(String name,String url,final boolean install){
		upgradeApk(name,url,true,false);
		
	}
	private void upgradeApk(String name,String url,final boolean install,final boolean constraint){
		final String savePath=mConfigService.getUpgradeDir()+File.separator +name+".apk";
		if(debug)Log.d("upgradeApk savePath:"+savePath);
		final DownloadNotification  downloadNotification=new DownloadNotification(mContext,name,savePath,Download.DownloadType.APK);
		mIds.add(downloadNotification.getId());
		File saveFile=new File(savePath);
		if(saveFile.exists())saveFile.delete();
		DownloadHttpClient downloadHttpClient=DownloadHttpClient.build(TAG);
		downloadHttpClient.send(mContext, url, new DownloadResponseHandler(){
			@Override
			public void onWait() {
				super.onWait();
				downloadNotification.createNotification();
			}
			@Override
			public void onStart(long from) {
				super.onStart(from);
			}
			@Override
			public void onStop(long end) {
				super.onStop(end);
				downloadNotification.cancelNotification();	
				mIds.remove(Integer.valueOf(downloadNotification.getId()));
			}
			@Override
			public void onFinish(long end) {
				super.onFinish(end);
				downloadNotification.finishNotification();
				if(constraint&&mUpgradeListener!=null)mUpgradeListener.onFinish();
				if(install){
					AppUtils.install(mContext, savePath);
				}
			}
			@Override
			public void onProgressUpdate(long end,int progress, int speed) {
				super.onProgressUpdate(end,progress, speed);
				downloadNotification.updateNotification(progress,speed);
			}
			@Override
			public void onFailure(Throwable error, String content) {
				super.onFailure(error, content);
				downloadNotification.failureNotification();
			}
			
		}, saveFile.length(), savePath);
	}
	@Override
	public void upgradeRes(String name, String url,boolean load) {
		
	}
	@Override
	public void upgradeDex(String name, String url,boolean launch) {
		
	}
	@Override
	public void upgradeSo(String name, String url, boolean load) {
		
	}

}
