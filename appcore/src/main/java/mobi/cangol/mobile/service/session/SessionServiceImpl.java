package mobi.cangol.mobile.service.session;

import android.app.Application;
import android.content.Context;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import mobi.cangol.mobile.CoreApplication;
import mobi.cangol.mobile.logging.Log;
import mobi.cangol.mobile.service.AppService;
import mobi.cangol.mobile.service.Service;
import mobi.cangol.mobile.service.ServiceProperty;
import mobi.cangol.mobile.service.conf.ConfigService;

/**
 * Created by weixuewu on 15/10/24.
 */
@Service("SessionService")
class SessionServiceImpl implements SessionService {
    private static final String TAG = "SessionService";
    private CoreApplication mContext = null;
    private ServiceProperty mServiceProperty = null;
    private boolean mDebug = false;
    private Map<String, Session> mSessionMap = new ConcurrentHashMap<>();
    private Session mSession = null;

    @Override
    public void onCreate(Application context) {
        mContext = (CoreApplication) context;
        final ConfigService configService = mContext.getAppService(AppService.CONFIG_SERVICE);
        mSession = newSession(mContext, configService.getSharedName());
    }

    @Override
    public String getName() {
        return TAG;
    }

    @Override
    public void onDestroy() {
        for (final Map.Entry<String, Session> entry : mSessionMap.entrySet()) {
           final Session session = entry.getValue();
            if (session != null) {
                session.clear();
            }
        }
        mSessionMap.clear();
    }

    @Override
    public void setDebug(boolean mDebug) {
        this.mDebug = mDebug;
    }

    @Override
    public void init(ServiceProperty serviceProperty) {
        this.mServiceProperty = serviceProperty;
    }

    @Override
    public ServiceProperty getServiceProperty() {
        return mServiceProperty;
    }

    @Override
    public ServiceProperty defaultServiceProperty() {
        return new ServiceProperty(TAG);
    }

    @Override
    public Session getSession() {
        return mSession;
    }

    @Override
    public Session getSession(String name) {
        if (mDebug) Log.d(TAG, "getUserSession " + name);
        if (mSessionMap.containsKey(name)) {
            return mSessionMap.get(name);
        } else {
            return newSession(mContext, name);
        }
    }

    private Session newSession(Context context, String name) {
        if (mDebug) Log.d(TAG, "newSession " + name);
        final Session session = new Session(context, name);
        mSessionMap.put(name, session);
        session.refresh();
        return session;
    }

    @Override
    public void refreshAll() {
        for (final Map.Entry<String, Session> entry : mSessionMap.entrySet()) {
            final Session session = entry.getValue();
            if (session != null) {
                session.refresh();
            }
        }
    }

    @Override
    public void clearAll() {
        for (final Map.Entry<String, Session> entry : mSessionMap.entrySet()) {
            final Session session = entry.getValue();
            if (session != null) {
                session.clearAll();
            }
        }
        mSessionMap.clear();
    }
}
