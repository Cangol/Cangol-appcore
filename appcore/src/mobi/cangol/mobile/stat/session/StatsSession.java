package mobi.cangol.mobile.stat.session;

import android.content.Context;

import java.util.Timer;
import java.util.TimerTask;

import mobi.cangol.mobile.stat.StatAgent;

public class StatsSession
{
	public static final String TAG="StatsSession";
	private static StatsSession instance;
	private Timer mTimer;
	private ConnectionQueue mQueue;
	private long mLastTime;
	private long unSentSessionLength=0;
	static public StatsSession instance(Context context)
	{
		if (instance == null)
			instance = new StatsSession(context);
		
		return instance;
	}
	
	private StatsSession(Context context)
	{
        mLastTime=System.currentTimeMillis() / 1000;
		mQueue = new ConnectionQueue(StatAgent.getInstance(context));
		mTimer = new Timer();
		mTimer.schedule(new TimerTask()
		{
			@Override
			public void run()
			{
				onTimer();
			}
		}, 30 * 1000,  30 * 1000);
		unSentSessionLength=0;
	}
	public void onStart(String page){
		mQueue.beginSession(page);
	}
	public void onStop(String page){
		long currTime = System.currentTimeMillis() / 1000;
		unSentSessionLength += currTime - mLastTime;
		
		int duration = (int)unSentSessionLength;
		mQueue.endSession(page,duration);
		unSentSessionLength -= duration;
	}
	
	private void onTimer(){
		long currTime = System.currentTimeMillis() / 1000;
		unSentSessionLength += currTime - mLastTime;
		mLastTime=currTime;
		
		int duration = (int)unSentSessionLength;
		mQueue.updateSession(duration);
		unSentSessionLength -= duration;
	}
}

