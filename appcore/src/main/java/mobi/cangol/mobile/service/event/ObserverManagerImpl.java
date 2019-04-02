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
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import mobi.cangol.mobile.service.Service;
import mobi.cangol.mobile.service.ServiceProperty;

/**
 * Created by xuewu.wei on 2016/8/3.
 */
@Service("ObserverManager")
class ObserverManagerImpl implements ObserverManager {
    private static final  String TAG = "ObserverManager";
    private ServiceProperty mServiceProperty = null;
    private Map<String, List<SubscriberMethod>> subscriberMaps = new HashMap<>();
    private Map<String, CopyOnWriteArrayList<Subscription>> subscriptionsByEvent = new HashMap<>();
    private Map<Object, List<String>> eventBySubscriber = new HashMap<>();
    private boolean mDebug;
    @Override
    public void onCreate(Application context){
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
    public void setDebug(boolean mDebug) {
        this.mDebug = mDebug;
    }

    @Override
    public void init(ServiceProperty serviceProperty) {
        this.mServiceProperty = serviceProperty;
    }

    @Override
    public ServiceProperty getServiceProperty() {
        return new ServiceProperty(TAG);
    }

    @Override
    public ServiceProperty defaultServiceProperty() {
        return mServiceProperty;
    }

    @Override
    public void register(Object subscriber) {
        if(mDebug)Log.d(TAG, "register subscriber=" + subscriber);
        final Class<?> subscriberClass = subscriber.getClass();
        final List<SubscriberMethod> subscriberMethods = findSubscriberMethods(subscriberClass);
        synchronized (this) {
            for (final SubscriberMethod subscriberMethod : subscriberMethods) {
                subscribe(subscriber, subscriberMethod);
            }
        }
    }

    @Override
    public void unregister(Object subscriber) {
        if(mDebug)Log.d(TAG, "unregister subscriber=" + subscriber);
        final String key = subscriber.getClass().getName();
        synchronized (subscriptionsByEvent) {
           final List<String> events = eventBySubscriber.get(subscriber);
           if(events!=null&&!events.isEmpty()){
               for (int i = 0; i < events.size(); i++) {
                   subscriptionsByEvent.remove(events.get(i));
               }
               eventBySubscriber.remove(subscriber);
           }
        }
        synchronized (subscriberMaps) {
            subscriberMaps.remove(key);
        }
    }

    @Override
    public void post(String event, Object data) {
        if(mDebug)Log.d(TAG, "post event=" + event + ",data=" + data);
        CopyOnWriteArrayList<Subscription> subscriptions;
        synchronized (this) {
            subscriptions = subscriptionsByEvent.get(event);
        }
        if (subscriptions != null && !subscriptions.isEmpty()) {
            for (final Subscription subscription : subscriptions) {
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
                                } catch (Exception e) {
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
        final List<SubscriberMethod> subscriberMethods = new ArrayList<>();
        Class<?> clazz = subscriberClass;
        final String key = clazz.getName();
        while (clazz != null) {
            final String name = clazz.getName();
            if (name.startsWith("java.") || name.startsWith("javax.") || name.startsWith("android.")) {
                // Skip system classes, this just degrades performance
                break;
            }
            final Method[] methods = clazz.getDeclaredMethods();
            for (final Method method : methods) {
                final Subscribe subscribe = method.getAnnotation(Subscribe.class);
                if (subscribe != null) {
                    final String event = subscribe.value();
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
        final String event = subscriberMethod.event;

        CopyOnWriteArrayList<Subscription> subscriptions = subscriptionsByEvent.get(event);
        final Subscription newSubscription = new Subscription(subscriber, subscriberMethod, subscriberMethod.priority);

        if (subscriptions == null) {
            subscriptions = new CopyOnWriteArrayList<>();
            subscriptionsByEvent.put(event, subscriptions);
        } else {
            for (final Subscription subscription : subscriptions) {
                if (subscription.equals(newSubscription)) {
                    throw new IllegalArgumentException("Subscriber " + subscriber.getClass() + " already registered to event " + event);
                }
            }
        }

        final int size = subscriptions.size();
        for (int i = 0; i <= size; i++) {
            if (i == size || newSubscription.priority > subscriptions.get(i).priority) {
                subscriptions.add(i, newSubscription);
                break;
            }
        }

        List<String> subscribedevents = eventBySubscriber.get(subscriber);
        if (subscribedevents == null) {
            subscribedevents = new ArrayList<>();
            eventBySubscriber.put(subscriber, subscribedevents);
        }
        subscribedevents.add(event);

    }
}
