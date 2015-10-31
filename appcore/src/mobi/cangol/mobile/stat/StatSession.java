package mobi.cangol.mobile.stat;

import android.content.Context;
import android.util.Log;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;

import mobi.cangol.mobile.utils.StringUtils;

public class StatSession
{
	public static final String TAG="StatSession";
	private static StatSession instance;
	private Timer mTimer;
	private ConnectionQueue mQueue;
	private long mLastTime;
	private long unSentSessionLength;
	static public StatSession instance(Context context)
	{
		if (instance == null)
			instance = new StatSession(context);
		
		return instance;
	}
	
	private StatSession(Context context)
	{
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

class SessionEntity  implements Cloneable{
	String sessionId;
	long beginSession;
	long sessionDuration;
	long endSession;
	String activityId;
	@Override
	protected Object clone() throws CloneNotSupportedException {
		Object o = null;
		try {
			o = (SessionEntity) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return o;
	}
	
}

class ConnectionQueue
{
	public static final String TAG="ConnectionQueue";
	private ConcurrentLinkedQueue<SessionEntity> queue_ = new ConcurrentLinkedQueue<SessionEntity>();
	private Hashtable<String, SessionEntity> entitys = new Hashtable<String, SessionEntity>();
	
	private Thread mThread = null;
	private StatAgent mStatTracker;
	public ConnectionQueue(StatAgent tracker){
		mStatTracker=tracker;
	}
	public void beginSession(String page)
	{
		//long currTime = System.currentTimeMillis() / 1000;
		SessionEntity data=new SessionEntity();
		data.sessionId=StringUtils.md5(String.valueOf(page.hashCode()));
		data.beginSession=1;//currTime
		data.endSession=0;
		data.activityId=page;
		
		entitys.put(page, data);
		
		queue_.offer(data);	
		
		tick();
		
	}
	
	public void updateSession(long duration){
		SessionEntity data=null;
		try {
			for(Iterator<String> itr = entitys.keySet().iterator(); itr.hasNext();){
				String page = (String) itr.next();
				data=(SessionEntity) entitys.get(page).clone();
				data.beginSession=0;
				data.sessionDuration=duration;
				data.endSession=0;
				queue_.offer(data);	
				tick();
			}
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
	}
	public void endSession(String page,long duration){
		//long currTime = System.currentTimeMillis() / 1000;
		SessionEntity data=null;
		try {
			data = (SessionEntity) entitys.get(page).clone();
			data.beginSession=0;
			data.sessionDuration=duration;
			data.endSession=1;//currTime
			queue_.offer(data);	
			
			tick();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
	}
	
	
	private void tick()
	{
		if (mThread != null && mThread.isAlive())
			return;
		
		if (queue_.isEmpty())
			return;
				
		mThread = new Thread() 
		{
			@Override
			public void run()
			{
				while (true)
				{
					SessionEntity data = queue_.peek();

					if (data == null)
						break;
					try{
						//提交
						mStatTracker.send(StatAgent.Builder.createSession(
                                data.sessionId,
                                String.valueOf(data.beginSession),
                                String.valueOf(data.sessionDuration),
                                String.valueOf(data.endSession),
                                data.activityId));
						queue_.poll();
					}catch (Exception e){
						Log.d(TAG, e.toString());
						break;
					}
				}
			}
		};
		mThread.start();
	}
}
