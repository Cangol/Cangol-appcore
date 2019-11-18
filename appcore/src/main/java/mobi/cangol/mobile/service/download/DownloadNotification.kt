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

package mobi.cangol.mobile.service.download

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.v4.app.NotificationCompat
import mobi.cangol.mobile.core.R
import mobi.cangol.mobile.utils.FileUtils
import java.util.*


class DownloadNotification {
    private var notificationManager: NotificationManager? = null
    var id: Int = 0
        private set
    private var titleText: String? = null
    private var successText: String? = null
    private var failureText: String? = null
    private var savePath: String? = null
    private var context: Context? = null
    private var finishIntent: Intent? = null

    constructor(context: Context, title: String, savePath: String, finishIntent: Intent, successText: String, failureText: String) {
        this.context = context
        this.savePath = savePath
        this.titleText = title
        this.successText = successText
        this.failureText = failureText
        this.finishIntent = finishIntent
        notificationManager = context.applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager!!.cancelAll()
        createNotificationChannel(context)
    }


    constructor(context: Context, title: String, savePath: String, finishIntent: Intent) {
        this.context = context
        this.savePath = savePath
        this.titleText = title
        this.successText = context.getString(R.string.upgrade_success)
        this.failureText = context.getString(R.string.upgrade_failure)
        this.finishIntent = finishIntent
        notificationManager = context.applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager!!.cancelAll()
        createNotificationChannel(context)
    }

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (notificationManager!!.getNotificationChannel(DOWNLOAD_NOTIFICATION_CHANNEL_ID) == null) {
                val channel = NotificationChannel(DOWNLOAD_NOTIFICATION_CHANNEL_ID, context.getString(R.string.notification_channel_1_name), NotificationManager.IMPORTANCE_LOW)
                channel.description = context.getString(R.string.notification_channel_1_desc)
                channel.enableLights(false)
                channel.enableVibration(false)
                notificationManager!!.createNotificationChannel(channel)
            }
        }
    }

    fun createNotification() {
        id = Random().nextInt(10000)
        val pendingIntent = PendingIntent.getActivity(context, id, Intent(), PendingIntent.FLAG_UPDATE_CURRENT)
        var builder: NotificationCompat.Builder? = null
        builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationCompat.Builder(context!!, DOWNLOAD_NOTIFICATION_CHANNEL_ID)
        } else {
            NotificationCompat.Builder(context)
        }
        builder.setContentTitle(titleText)
                .setContentText("")
                .setContentInfo("")
                .setProgress(100, 0, false)
                .setContentIntent(pendingIntent)
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(false)
                .setOngoing(true)
                .setSmallIcon(context!!.applicationInfo.icon)

        notificationManager!!.notify(id, builder.build())
    }

    fun updateNotification(progress: Int, speed: Int) {
        var builder: NotificationCompat.Builder? = null
        builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationCompat.Builder(context!!, DOWNLOAD_NOTIFICATION_CHANNEL_ID)
        } else {
            NotificationCompat.Builder(context)
        }
        builder.setContentTitle(titleText)
                .setContentText(FileUtils.formatSize(speed.toLong()) + "/s")
                .setContentInfo("$progress%")
                .setProgress(100, progress, false)
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(false)
                .setOngoing(true)
                .setSmallIcon(android.R.drawable.stat_sys_download)
        notificationManager!!.notify(id, builder.build())
    }

    fun finishNotification() {
        val pendingIntent = PendingIntent.getActivity(context, id, finishIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        var builder: NotificationCompat.Builder? = null
        builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationCompat.Builder(context!!, DOWNLOAD_NOTIFICATION_CHANNEL_ID)
        } else {
            NotificationCompat.Builder(context)
        }
        builder.setContentTitle(titleText)
                .setContentText(successText)
                .setContentInfo("")
                .setContentIntent(pendingIntent)
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setOngoing(false)
                .setSmallIcon(android.R.drawable.stat_sys_download)
        notificationManager!!.notify(id, builder.build())
    }

    fun failureNotification() {
        var builder: NotificationCompat.Builder? = null
        builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationCompat.Builder(context!!, DOWNLOAD_NOTIFICATION_CHANNEL_ID)
        } else {
            NotificationCompat.Builder(context)
        }
        builder.setContentTitle(titleText)
                .setContentText(failureText)
                .setContentInfo("")
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setOngoing(false)
                .setSmallIcon(android.R.drawable.stat_sys_download)

        notificationManager!!.notify(id, builder.build())
    }


    fun cancelNotification() {
        if (notificationManager != null) {
            notificationManager!!.cancel(id)
        }
    }

    companion object {
        private val DOWNLOAD_NOTIFICATION_CHANNEL_ID = "101"
    }
}
