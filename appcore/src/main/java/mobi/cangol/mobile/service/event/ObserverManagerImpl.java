/*
 *
 *  Copyright (c) 2013 Cangol
 *   <p/>
 *   Licensed under the Apache License, Version 2.0 (the "License")
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *  <p/>
 *  http://www.apache.org/licenses/LICENSE-2.0
 *  <p/>
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package mobi.cangol.mobile.service.event;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import mobi.cangol.mobile.service.Service;
import mobi.cangol.mobile.service.ServiceProperty;

/**
 * Created by xuewu.wei on 2016/8/3.
 */
@Service("ObserverManager")
class ObserverManagerImpl implements ObserverManager {
    private final static String TAG = "ObserverManager";
    private  boolean debug = true;
    private Application mContext;
    private ServiceProperty mServiceProperty = null;
    private Application context;
    private HashMap<String, List<SubscriberMethod>> subscriberMaps = new HashMap<String, List<SubscriberMethod>>();
    private HashMap<String, CopyOnWriteArrayList<Subscription>> subscriptionsByEvent = new HashMap<String, CopyOnWriteArrayList<Subscription>>();
    private HashMap<Object, List<String>> eventBySubscriber = new HashMap<Object, List<String>>();

    @Override
    public void onCreate(Application context) {
        mContext = context;
    }

    @Override
    public String getName() {
        return TAG;
    }

    @Override
    public void onDestroy() {
        subscriptionsByEvent.clear();
        eventBySubscriber.clear();
        subscriberMaps.clear();
    }

    @Override
    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    @Override
    public void init(ServiceProperty serviceProperty) {
        this.mServiceProperty = serviceProperty;
    }

    @Override
    public ServiceProperty getServiceProperty() {
        ServiceProperty sp = new ServiceProperty(TAG);
        return sp;
    }

    @Override
    public ServiceProperty defaultServiceProperty() {
        return mServiceProperty;
    }

    @Override
    public void register(Object subscriber) {
        Log.d(TAG, "register subscriber=" + subscriber);
        Class<?> subscriberClass = subscriber.getClass();
        List<SubscriberMethod> subscriberMethods = findSubscriberMethods(subscriberClass);
        synchronized (this) {
            for (SubscriberMethod subscriberMethod : subscriberMethods) {
                subscribe(subscriber, subscriberMethod);
            }
        }
    }

    @Override
    public void unregister(Object subscriber) {
        Log.d(TAG, "unregister subscriber=" + subscriber);
        String key = subscriber.getClass().getName();
        synchronized (subscriptionsByEvent) {
            List<String> events = eventBySubscriber.get(subscriber);
            for (int i = 0; i < events.size(); i++) {
                subscriptionsByEvent.remove(events.get(i));
            }
            eventBySubscriber.remove(subscriber);
        }
        synchronized (subscriberMaps) {
            subscriberMaps.remove(key);
        }
    }

    @Override
    public void post(String event, Object data) {
        Log.d(TAG, "post event=" + event + ",data=" + data);
        CopyOnWriteArrayList<Subscription> subscriptions;
        synchronized (this) {
            subscriptions = subscriptionsByEvent.get(event);
        }
        if (subscriptions != null && !subscriptions.isEmpty()) {
            for (Subscription subscription : subscriptions) {
                invokeSubscriber(subscription, data, Looper.getMainLooper() == Looper.myLooper());
            }
        } else {
            Log.d(TAG, "No subscribers registered for event " + event);
        }
    }

    private void invokeSubscriber(Subscription subscription, Object data, boolean curIsMainThread) {
        final Object subscriber = subscription.subscriber;
        final Method method = subscription.subscriberMethod.method;
        try {
            Log.d(TAG, "invokeSubscriber curIsMainThread=" + curIsMainThread);
            if (curIsMainThread) {
                if (subscription.subscriberMethod.threadType == ThreadType.MAIN) {
                    method.invoke(subscriber, data);
                } else {
                    final Handler handler = new Handler(Looper.myLooper()) {

                        @Override
                        public void handleMessage(Message msg) {
                            super.handleMessage(msg);
                            if (msg.what == 1) {
                                try {
                                    method.invoke(subscriber, msg.obj);
                                } catch (IllegalAccessException e) {
                                    Log.e(TAG, "No subscribers IllegalAccessException " + e.getMessage());
                                } catch (InvocationTargetException e) {
                                    Log.e(TAG, "No subscribers InvocationTargetException " + e.getMessage());
                                }
                            }
                        }
                    };
                    handler.sendMessage(Message.obtain(handler, 1, data));
                }
            } else {
                if (subscription.subscriberMethod.threadType == ThreadType.BACKGROUND) {
                    method.invoke(subscriber, data);
                } else {
                    final Handler handler = new Handler(Looper.getMainLooper()) {

                        @Override
                        public void handleMessage(Message msg) {
                            super.handleMessage(msg);
                            if (msg.what == 1) {
                                try {
                                    method.invoke(subscriber, msg.obj);
                                } catch (IllegalAccessException e) {
                                    Log.e(TAG, "No subscribers IllegalAccessException " + e.getMessage());
                                } catch (InvocationTargetException e) {
                                    Log.e(TAG, "No subscribers InvocationTargetException " + e.getMessage());
                                }
                            }
                        }
                    };
                    handler.sendMessage(Message.obtain(handler, 1, data));
                }
            }
        } catch (IllegalAccessException e) {
            Log.e(TAG, "No subscribers IllegalAccessException " + e.getMessage());
        } catch (InvocationTargetException e) {
            Log.e(TAG, "No subscribers InvocationTargetException " + e.getMessage());
        }
    }

    private List<SubscriberMethod> findSubscriberMethods(Class<?> subscriberClass) {
        List<SubscriberMethod> subscriberMethods = new ArrayList<SubscriberMethod>();
        Class<?> clazz = subscriberClass;
        String key = clazz.getName();
        while (clazz != null) {
            String name = clazz.getName();
            if (name.startsWith("java.") || name.startsWith("javax.") || name.startsWith("android.")) {
                // Skip system classes, this just degrades performance
                break;
            }
            Method[] methods = clazz.getDeclaredMethods();
            for (Method method : methods) {
                Subscribe subscribe = method.getAnnotation(Subscribe.class);
                if (subscribe != null) {
                    String event = subscribe.value();
                    subscriberMethods.add(new SubscriberMethod(method, subscribe.threadType(), subscribe.priority(), event));
                }
            }
            clazz = clazz.getSuperclass();
        }
        if (subscriberMethods.isEmpty()) {
            throw new IllegalArgumentException("Subscriber " + subscriberClass + " has no  subscriber's methods");
        } else {
            synchronized (subscriberMaps) {
                subscriberMaps.put(key, subscriberMethods);
            }
            return subscriberMethods;
        }
    }

    private void subscribe(Object subscriber, SubscriberMethod subscriberMethod) {
        String event = subscriberMethod.event;

        CopyOnWriteArrayList<Subscription> subscriptions = subscriptionsByEvent.get(event);
        Subscription newSubscription = new Subscription(subscriber, subscriberMethod, subscriberMethod.priority);

        if (subscriptions == null) {
            subscriptions = new CopyOnWriteArrayList<Subscription>();
            subscriptionsByEvent.put(event, subscriptions);
        } else {
            for (Subscription subscription : subscriptions) {
                if (subscription.equals(newSubscription)) {
                    throw new IllegalArgumentException("Subscriber " + subscriber.getClass() + " already registered to event " + event);
                }
            }
        }

        int size = subscriptions.size();
        for (int i = 0; i <= size; i++) {
            if (i == size || newSubscription.priority > subscriptions.get(i).priority) {
                subscriptions.add(i, newSubscription);
                break;
            }
        }

        List<String> subscribedevents = eventBySubscriber.get(subscriber);
        if (subscribedevents == null) {
            subscribedevents = new ArrayList<String>();
            eventBySubscriber.put(subscriber, subscribedevents);
        }
        subscribedevents.add(event);

    }
}
