package mobi.cangol.mobile.stat.session;

/**
 * Created by weixuewu on 16/1/23.
 */
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
