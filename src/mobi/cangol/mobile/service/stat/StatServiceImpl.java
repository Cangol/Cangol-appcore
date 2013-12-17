package mobi.cangol.mobile.service.stat;

import mobi.cangol.mobile.http.AsyncHttpClient;
import mobi.cangol.mobile.http.AsyncHttpResponseHandler;
import mobi.cangol.mobile.http.RequestParams;
import mobi.cangol.mobile.service.PoolManager;
import mobi.cangol.mobile.service.Service;
import mobi.cangol.mobile.service.ServiceProperty;
import android.content.Context;
import android.util.Log;
@Service("StatService")
public class StatServiceImpl implements StatService {
	private final static String TAG="StatService";
	private boolean debug=false;
	private Context mContext = null;
	private AsyncHttpClient asyncHttpClient= null;
	private ServiceProperty mServiceProperty=null;
	@Override
	public void onCreate(Context context) {
		mContext=context;
	}
	private void init(){
		PoolManager.buildPool(mServiceProperty.getString(STATSERVICE_THREADPOOL_NAME),mServiceProperty.getInt(STATSERVICE_THREAD_MAX));
		asyncHttpClient=AsyncHttpClient.build(mServiceProperty.getString(STATSERVICE_THREADPOOL_NAME));
	}
	@Override
	public String getName() {
		return "StatService";
	}

	@Override
	public void onDestory() {
		asyncHttpClient.cancelRequests(mContext, true);
	}
	@Override
	public void setServiceProperty(ServiceProperty serviceProperty) {
		this.mServiceProperty=serviceProperty;
		init();
	}

	@Override
	public ServiceProperty getServiceProperty() {
		return mServiceProperty;
	}
	@Override
	public void sendStat(StatModel statModel) {
		RequestParams params=new RequestParams(statModel.getParams());
		if(debug)Log.d(TAG, statModel.toString());
		asyncHttpClient.get(mContext,statModel.getUrl(), params,  new AsyncHttpResponseHandler(){

			@Override
			public void onStart() {
				super.onStart();
				if(debug)Log.d(TAG, "Start");
			}
			
			@Override
			public void onSuccess(String content) {
				super.onSuccess(content);
				if(debug)Log.d(TAG, "Success :"+content);
			}

			@Override
			public void onFailure(Throwable error, String content) {
				super.onFailure(error, content);
				if(debug)Log.d(TAG, "Failure :"+content);
			}
			
		});
	}

	@Override
	public void setDebug(boolean debug) {
		this.debug=debug;
	}

}
