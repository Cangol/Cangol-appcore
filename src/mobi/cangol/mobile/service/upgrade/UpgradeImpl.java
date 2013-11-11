package mobi.cangol.mobile.service.upgrade;

import java.io.File;

import mobi.cangol.mobile.service.Service;
import mobi.cangol.mobile.service.download.Download;
import mobi.cangol.mobile.service.download.DownloadHttpClient;
import mobi.cangol.mobile.service.download.DownloadNotification;
import mobi.cangol.mobile.service.download.DownloadResponseHandler;
import android.content.Context;
@Service("upgrade")
public class UpgradeImpl implements Upgrade{
	private final static String TAG="Upgrade";
	private boolean debug=false;
	private Context mContext = null;
	private DownloadHttpClient mDownloadHttpClient;
	private DownloadNotification mDownloadNotification;
	@Override
	public void create(Context context) {
		mContext=context;
		
	}

	@Override
	public String getName() {
		return "upgrade";
	}

	@Override
	public void destory() {
		
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
