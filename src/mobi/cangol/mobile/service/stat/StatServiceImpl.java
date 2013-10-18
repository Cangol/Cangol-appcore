package mobi.cangol.mobile.service.stat;

import java.util.concurrent.ThreadPoolExecutor;

import mobi.cangol.mobile.http.AsyncHttpClient;
import mobi.cangol.mobile.http.AsyncHttpResponseHandler;
import mobi.cangol.mobile.http.RequestParams;
import mobi.cangol.mobile.service.PoolManager;
import mobi.cangol.mobile.service.Service;
import android.content.Context;
import android.util.Log;
@Service("stat")
public class StatServiceImpl implements StatService {
	private final static String TAG="StatService";
	private final static  int STAT_MAX = 5;
	private Context mContext = null;
	private AsyncHttpClient asyncHttpClient;
	
	@Override
	public void init() {
		asyncHttpClient=new AsyncHttpClient();
		asyncHttpClient.setThreadool((ThreadPoolExecutor) PoolManager.buildPool("stat", STAT_MAX).getExecutorService());
	}

	@Override
	public void setContext(Context context) {
		mContext=context;
	}

	@Override
	public String getName() {
		return "stat";
	}

	@Override
	public void destory() {
		asyncHttpClient.cancelRequests(mContext, true);
	}

	@Override
	public void sendStat(StatModel statModel) {
		RequestParams params=new RequestParams(statModel.getParams());
		asyncHttpClient.get(mContext,statModel.getUrl(), params,  new AsyncHttpResponseHandler(){

			@Override
			public void onStart() {
				super.onStart();
				Log.d(TAG, "Start");
			}
			
			@Override
			public void onSuccess(String content) {
				super.onSuccess(content);
				Log.d(TAG, "Success :"+content);
			}

			@Override
			public void onFailure(Throwable error, String content) {
				super.onFailure(error, content);
				Log.d(TAG, "Failure :"+content);
			}
			
		});
	}

}
