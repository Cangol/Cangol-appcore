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

package mobi.cangol.mobile.service.download;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;

import java.util.Random;

import mobi.cangol.mobile.core.R;
import mobi.cangol.mobile.utils.FileUtils;


public class DownloadNotification {
    private static final String DOWNLOAD_NOTIFICATION_CHANNEL_ID = "notification_channel_upgrade";
    private NotificationManager notificationManager;
    private NotificationChannel channel;
    private int id;
    private String titleText, successText, failureText;
    private String savePath;
    private Context context;
    private Intent finishIntent;

    public DownloadNotification(Context context, String title, String savePath, Intent finishIntent, String successText, String failureText) {
        this.context = context;
        this.savePath = savePath;
        this.titleText = title;
        this.successText = successText;
        this.failureText = failureText;
        this.finishIntent = finishIntent;
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel = createNotificationChannel(context);
        }
    }


    public DownloadNotification(Context context, String title, String savePath, Intent finishIntent) {
        this.context = context;
        this.savePath = savePath;
        this.titleText = title;
        this.successText = context.getString(R.string.upgrade_success);
        this.failureText = context.getString(R.string.upgrade_failure);
        this.finishIntent = finishIntent;
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel = createNotificationChannel(context);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public NotificationChannel createNotificationChannel(Context context) {
        NotificationChannel channel = new NotificationChannel(DOWNLOAD_NOTIFICATION_CHANNEL_ID, context.getString(R.string.notification_channel_upgrade_name), NotificationManager.IMPORTANCE_LOW);
        channel.setDescription(context.getString(R.string.notification_channel_upgrade_desc));
        channel.enableLights(false);
        channel.enableVibration(false);
        notificationManager.createNotificationChannel(channel);
        return channel;
    }

    public int getId() {
        return id;
    }

    public void createNotification() {
        id = new Random().nextInt(10000);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, id, new Intent(), PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder = new NotificationCompat.Builder(context, DOWNLOAD_NOTIFICATION_CHANNEL_ID);
        } else {
            builder = new NotificationCompat.Builder(context);
        }
        builder.setContentTitle(titleText)
                .setContentText("")
                .setContentInfo("")
                .setProgress(100, 0, false)
                .setContentIntent(pendingIntent)
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(false)
                .setOngoing(true)
                .setSmallIcon(context.getApplicationInfo().icon);

        notificationManager.notify(id, builder.build());
    }

    public void updateNotification(int progress, int speed) {
        NotificationCompat.Builder builder = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder = new NotificationCompat.Builder(context, DOWNLOAD_NOTIFICATION_CHANNEL_ID);
        } else {
            builder = new NotificationCompat.Builder(context);
        }
        builder.setContentTitle(titleText)
                .setContentText(FileUtils.formatSize(speed) + "/s")
                .setContentInfo(progress + "%")
                .setProgress(100, progress, false)
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(false)
                .setOngoing(true)
                .setSmallIcon(android.R.drawable.stat_sys_download);
        notificationManager.notify(id, builder.build());
    }

    public void finishNotification() {
        PendingIntent pendingIntent = PendingIntent.getActivity(context, id, finishIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder = new NotificationCompat.Builder(context, DOWNLOAD_NOTIFICATION_CHANNEL_ID);
        } else {
            builder = new NotificationCompat.Builder(context);
        }
        builder.setContentTitle(titleText)
                .setContentText(successText)
                .setContentInfo("")
                .setContentIntent(pendingIntent)
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setOngoing(false)
                .setSmallIcon(android.R.drawable.stat_sys_download);
        notificationManager.notify(id, builder.build());
    }

    public void failureNotification() {
        NotificationCompat.Builder builder = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder = new NotificationCompat.Builder(context, DOWNLOAD_NOTIFICATION_CHANNEL_ID);
        } else {
            builder = new NotificationCompat.Builder(context);
        }
        builder.setContentTitle(titleText)
                .setContentText(failureText)
                .setContentInfo("")
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setOngoing(false)
                .setSmallIcon(android.R.drawable.stat_sys_download);

        notificationManager.notify(id, builder.build());
    }


    public void cancelNotification() {
        if (notificationManager != null) {
            notificationManager.cancel(id);
        }
    }
}
