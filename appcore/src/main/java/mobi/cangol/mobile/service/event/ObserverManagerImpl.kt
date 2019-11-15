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

package mobi.cangol.mobile.service.event

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import mobi.cangol.mobile.service.Service
import mobi.cangol.mobile.service.ServiceProperty
import java.lang.reflect.InvocationTargetException
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Created by xuewu.wei on 2016/8/3.
 */
@Service("ObserverManager")
internal class ObserverManagerImpl : ObserverManager {
    private var mServiceProperty = ServiceProperty(TAG)
    private val subscriberMaps = HashMap<String, MutableList<SubscriberMethod>>()
    private val subscriptionsByEvent = HashMap<String, CopyOnWriteArrayList<Subscription>>()
    private val eventBySubscriber = HashMap<Any, MutableList<String>>()
    private var mDebug: Boolean = false
    override fun onCreate(context: Application) {}

    override fun getName(): String {
        return TAG
    }

    override fun onDestroy() {
        subscriptionsByEvent.clear()
        eventBySubscriber.clear()
        subscriberMaps.clear()
    }

    override fun setDebug(mDebug: Boolean) {
        this.mDebug = mDebug
    }

    override fun init(serviceProperty: ServiceProperty) {
        this.mServiceProperty = serviceProperty
    }

    override fun getServiceProperty(): ServiceProperty {
        return mServiceProperty
    }

    override fun defaultServiceProperty(): ServiceProperty {
        return mServiceProperty
    }

    override fun register(subscriber: Any) {
        if (mDebug) Log.d(TAG, "register subscriber=$subscriber")
        val subscriberClass = subscriber.javaClass
        val subscriberMethods = findSubscriberMethods(subscriberClass)
        synchronized(this) {
            for (subscriberMethod in subscriberMethods) {
                subscribe(subscriber, subscriberMethod)
            }
        }
    }

    override fun unregister(subscriber: Any) {
        if (mDebug) Log.d(TAG, "unregister subscriber=$subscriber")
        val key = subscriber.javaClass.name
        synchronized(subscriptionsByEvent) {
            val events = eventBySubscriber[subscriber]
            if (events != null && !events.isEmpty()) {
                for (i in events.indices) {
                    subscriptionsByEvent.remove(events[i])
                }
                eventBySubscriber.remove(subscriber)
            }
        }
        synchronized(subscriberMaps) {
            subscriberMaps.remove(key)
        }
    }

    override fun post(event: String, data: Any) {
        if (mDebug) Log.d(TAG, "post event=$event,data=$data")
        val subscriptions: CopyOnWriteArrayList<Subscription>?
        synchronized(this) {
            subscriptions = subscriptionsByEvent[event]
        }
        if (subscriptions != null && !subscriptions.isEmpty()) {
            for (subscription in subscriptions) {
                invokeSubscriber(subscription, data, Looper.getMainLooper() == Looper.myLooper())
            }
        } else {
            Log.d(TAG, "No subscribers registered for event $event")
        }
    }

    private fun invokeSubscriber(subscription: Subscription, data: Any, curIsMainThread: Boolean) {
        val subscriber = subscription.subscriber
        val method = subscription.subscriberMethod.method
        try {
            Log.d(TAG, "invokeSubscriber curIsMainThread=$curIsMainThread")
            if (curIsMainThread) {
                if (subscription.subscriberMethod.threadType === ThreadType.MAIN) {
                    method.invoke(subscriber, data)
                } else {
                    val handler = object : Handler(Looper.myLooper()) {

                        override fun handleMessage(msg: Message) {
                            super.handleMessage(msg)
                            if (msg.what == 1) {
                                try {
                                    method.invoke(subscriber, msg.obj)
                                } catch (e: Exception) {
                                    Log.e(TAG, "No subscribers InvocationTargetException " + e.message)
                                }

                            }
                        }
                    }
                    handler.sendMessage(Message.obtain(handler, 1, data))
                }
            } else {
                if (subscription.subscriberMethod.threadType === ThreadType.BACKGROUND) {
                    method.invoke(subscriber, data)
                } else {
                    val handler = object : Handler(Looper.getMainLooper()) {

                        override fun handleMessage(msg: Message) {
                            super.handleMessage(msg)
                            if (msg.what == 1) {
                                try {
                                    method.invoke(subscriber, msg.obj)
                                } catch (e: IllegalAccessException) {
                                    Log.e(TAG, "No subscribers IllegalAccessException " + e.message)
                                } catch (e: InvocationTargetException) {
                                    Log.e(TAG, "No subscribers InvocationTargetException " + e.message)
                                }

                            }
                        }
                    }
                    handler.sendMessage(Message.obtain(handler, 1, data))
                }
            }
        } catch (e: IllegalAccessException) {
            Log.e(TAG, "No subscribers IllegalAccessException " + e.message)
        } catch (e: InvocationTargetException) {
            Log.e(TAG, "No subscribers InvocationTargetException " + e.message)
        }

    }

    private fun findSubscriberMethods(subscriberClass: Class<*>): List<SubscriberMethod> {
        val subscriberMethods = ArrayList<SubscriberMethod>()
        var clazz: Class<*>? = subscriberClass
        val key = clazz!!.name
        while (clazz != null) {
            val name = clazz.name
            if (name.startsWith("java.") || name.startsWith("javax.") || name.startsWith("android.")) {
                // Skip system classes, this just degrades performance
                break
            }
            val methods = clazz.declaredMethods
            for (method in methods) {
                val subscribe = method.getAnnotation(Subscribe::class.java)
                if (subscribe != null) {
                    val event = subscribe.value
                    subscriberMethods.add(SubscriberMethod(method, subscribe.threadType, subscribe.priority, event))
                }
            }
            clazz = clazz.superclass
        }
        require(!subscriberMethods.isEmpty()) { "Subscriber $subscriberClass has no  subscriber's methods" }
        synchronized(subscriberMaps) {
            subscriberMaps.put(key, subscriberMethods)
        }
        return subscriberMethods
    }

    private fun subscribe(subscriber: Any, subscriberMethod: SubscriberMethod) {
        val event = subscriberMethod.event

        var subscriptions = subscriptionsByEvent[event]
        val newSubscription = Subscription(subscriber, subscriberMethod, subscriberMethod.priority)

        if (subscriptions == null) {
            subscriptions = CopyOnWriteArrayList()
            subscriptionsByEvent[event] = subscriptions
        } else {
            for (subscription in subscriptions) {
                require(subscription != newSubscription) { "Subscriber " + subscriber.javaClass + " already registered to event " + event }
            }
        }

        val size = subscriptions.size
        for (i in 0..size) {
            if (i == size || newSubscription.priority > subscriptions[i].priority) {
                subscriptions.add(i, newSubscription)
                break
            }
        }

        var subscribedevents: MutableList<String>? = eventBySubscriber[subscriber]
        if (subscribedevents == null) {
            subscribedevents = ArrayList()
            eventBySubscriber[subscriber] = subscribedevents
        }
        subscribedevents.add(event)

    }

    companion object {
        private const val TAG = "ObserverManager"
    }
}
