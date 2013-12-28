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

import mobi.cangol.mobile.service.Service;
import mobi.cangol.mobile.service.ServiceProperty;
import mobi.cangol.mobile.service.download.Download;
import mobi.cangol.mobile.service.download.DownloadHttpClient;
import mobi.cangol.mobile.service.download.DownloadNotification;
import mobi.cangol.mobile.service.download.DownloadResponseHandler;
import android.content.Context;
@Service("UpgradeService")
public class UpgradeServiceImpl implements UpgradeService{
	private final static String TAG="Upgrade";
	private boolean debug=false;
	private Context mContext = null;
	private DownloadHttpClient mDownloadHttpClient;
	private DownloadNotification mDownloadNotification;
	private ServiceProperty mServiceProperty=null;
	@Override
	public void onCreate(Context context) {
		mContext=context;
	}
	@Override
	public void init(ServiceProperty serviceProperty) {
		this.mServiceProperty=serviceProperty;
	}
	@Override
	public String getName() {
		return "UpgradeService";
	}

	@Override
	public void onDestory() {
		
		if(mDownloadHttpClient!=null){
			mDownloadHttpClient.cancelRequests(mContext, true);
			mDownloadHttpClient=null;
		}
		
		if(mDownloadNotification!=null){
			mDownloadNotification.cancelNotification();
			mDownloadNotification=null;
		}
	}

	@Override
	public ServiceProperty getServiceProperty() {
		return mServiceProperty;
	}
	@Override
	public boolean isUpgrade(String version) {
		
		return false;
	}

	@Override
	public String getUpgrade(String version) {
		
		return null;
	}
	@Override
	public void downloadUpgrade(String url,String savePath) {
		mDownloadNotification=new DownloadNotification(mContext,"",savePath,Download.DownloadType.APK);
		if(mDownloadHttpClient==null)
			mDownloadHttpClient=new DownloadHttpClient();
		File saveFile=new File(savePath);
		if(saveFile.exists())saveFile.delete();
		mDownloadHttpClient.send(mContext, url, new DownloadResponseHandler(){
			@Override
			public void onWait() {
				super.onWait();
				mDownloadNotification.createNotification();
			}
			@Override
			public void onStart(long from) {
				super.onStart(from);
			}
			@Override
			public void onStop(long end) {
				super.onStop(end);
				mDownloadNotification.cancelNotification();
			}
			@Override
			public void onFinish(long end) {
				super.onFinish(end);
				mDownloadNotification.finishNotification();
			}
			@Override
			public void onProgressUpdate(long end,int progress, int speed) {
				super.onProgressUpdate(end,progress, speed);
				mDownloadNotification.updateNotification(progress,speed);
			}
			@Override
			public void onFailure(Throwable error, String content) {
				super.onFailure(error, content);
				mDownloadNotification.failureNotification();
			}
			
		}, saveFile.length(), savePath);
		
	}

	@Override
	public void setDebug(boolean debug) {
		this.debug=debug;
	}

}
