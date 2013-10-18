package mobi.cangol.mobile.service.download;

import java.io.File;
import java.util.Random;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.RemoteViews;

public class DownloadNotification {
	private NotificationManager notificationManager;
	private Notification notificaion;
	private int noid;
	private String title;
	private String savePath;
	private Context context;
	private Download.DownloadType downloadType;
	public DownloadNotification(Context context,String title,String savePath,Download.DownloadType downloadType){
		this.context=context;
		this.title=title;
		this.savePath=savePath;
		this.downloadType=downloadType;
		notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancelAll();
	}
	public void initNotification() {
		
	}
	private void testNotification() {
		if(update_notification_layout==0){
			throw new IllegalStateException("please set this value " +
					"[update_notification_layout," +
					"update_notification_progressblock," +
					"update_notification_progresstext," +
					"update_notification_titletext," +
					"update_notification_progressbar," +
					"download_failure_text," +
					"download_success_text]");
		}
	}
	public void createNotification() {
		testNotification();
		noid = new Random().nextInt(10000);
		notificaion = new Notification();
		notificaion.flags |= Notification.FLAG_ONGOING_EVENT;
		notificaion.icon = android.R.drawable.stat_sys_download;
		notificaion.when = System.currentTimeMillis();
		notificaion.tickerText = title;
		notificaion.contentIntent =PendingIntent.getActivity(context, 0, new Intent(), 0);
		notificaion.contentIntent.cancel();
		notificaion.contentView = new RemoteViews(context.getPackageName(),update_notification_layout);
		notificaion.contentView.setProgressBar(
				update_notification_progressbar, 100, 0, false);
		notificaion.contentView.setTextViewText(
				update_notification_progresstext, "");
		notificaion.contentView.setTextViewText(update_notification_titletext,title);
		notificationManager.notify(noid,notificaion);
	}

	public void cancelNotification() {
		testNotification();
		if(notificationManager!=null)
		notificationManager.cancel(noid);
	}

	public void finishNotification() {
		testNotification();
		PendingIntent pendingIntent =null;
		if(Download.DownloadType.APK==downloadType){
			Uri uri = Uri.fromFile(new File(savePath));
			Intent installIntent = new Intent(Intent.ACTION_VIEW);
			installIntent.setDataAndType(uri,"application/vnd.android.package-archive");
			pendingIntent = PendingIntent.getActivity(context, 0, installIntent, 0);
		}else{
			pendingIntent=null;
		}
		notificaion.tickerText = title;
		notificaion.when = System.currentTimeMillis();
		notificaion.contentIntent = pendingIntent;
		notificaion.flags = Notification.FLAG_AUTO_CANCEL;
		notificaion.contentView.setViewVisibility(update_notification_progressblock, View.GONE);
		notificaion.contentView.setTextViewText(update_notification_progresstext,context.getText(download_success_text));
		notificaion.defaults = Notification.DEFAULT_SOUND;
		notificationManager.notify(noid, notificaion);
	}

	public void updateNotification(int progress, String speed) {
		testNotification();
		notificaion.contentView.setProgressBar(update_notification_progressbar, 100, progress, false);
		
		notificaion.contentView.setTextViewText(update_notification_progresstext, progress+"% "+speed);
						
		notificationManager.notify(noid,notificaion);
	}

	public void failureNotification() {
		testNotification();
		notificaion.tickerText = title;
		notificaion.when = System.currentTimeMillis();
		notificaion.contentView.setViewVisibility(update_notification_progressblock, View.GONE);
		notificaion.contentView.setTextViewText(update_notification_progresstext,context.getText(download_failure_text));
		notificaion.flags = Notification.FLAG_AUTO_CANCEL;
		notificaion.defaults = Notification.DEFAULT_SOUND;
		notificationManager.notify(noid, notificaion);
	}
	
	//please set this value
	
	int update_notification_progressbar;
	int update_notification_progressblock;
	int update_notification_progresstext;
	int update_notification_titletext;
	int update_notification_layout;
	int download_failure_text;
	int download_success_text;
}
