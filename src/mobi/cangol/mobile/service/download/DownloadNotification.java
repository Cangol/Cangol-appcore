package mobi.cangol.mobile.service.download;

import java.io.File;
import java.util.Random;

import mobi.cangol.mobile.utils.AppUtils;
import mobi.cangol.mobile.utils.FileUtils;
import mobi.cangol.mobile.utils.TimeUtils;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
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
		initNotification(context);
		
	}
	private void initNotification(Context context){
		Resources resources=context.getResources();
		this.update_notification_icon=resources.getIdentifier("update_notification_icon", "id", context.getPackageName());
		this.update_notification_progressbar =resources.getIdentifier("update_notification_progressbar", "id", context.getPackageName());
		this.update_notification_progressblock =resources.getIdentifier("update_notification_progressblock", "id", context.getPackageName());
		this.update_notification_progresstext =resources.getIdentifier("update_notification_progresstext", "id", context.getPackageName());
		this.update_notification_titletext =resources.getIdentifier("update_notification_titletext", "id", context.getPackageName());
		this.update_notification_timetext =resources.getIdentifier("update_notification_timetext", "id", context.getPackageName());
		this.update_notification_infotext =resources.getIdentifier("update_notification_infotext", "id", context.getPackageName());
		this.update_notification_layout =resources.getIdentifier("update_notification_layout", "layout", context.getPackageName());
		this.download_failure_text =resources.getIdentifier("download_failure_text", "string", context.getPackageName());
		this.download_success_text =resources.getIdentifier("download_success_text", "string", context.getPackageName());
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
		notificaion.contentView.setProgressBar(update_notification_progressbar, 100, 0, false);
		notificaion.contentView.setTextViewText(update_notification_progresstext, "");
		notificaion.contentView.setTextViewText(update_notification_titletext,title);
		notificaion.contentView.setTextViewText(update_notification_timetext,TimeUtils.getCurrentHoursMinutes());
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
		notificaion.contentView.setViewVisibility(update_notification_progressbar, View.GONE);
		notificaion.contentView.setViewVisibility(update_notification_progresstext,View.GONE);
		notificaion.contentView.setViewVisibility(update_notification_infotext,View.VISIBLE);
		notificaion.contentView.setImageViewBitmap(update_notification_icon, ((BitmapDrawable) AppUtils.getApplicationIcon(context, savePath)).getBitmap());
		notificaion.contentView.setTextViewText(update_notification_infotext,context.getString(download_success_text));
		notificaion.defaults = Notification.DEFAULT_SOUND;
		notificationManager.notify(noid, notificaion);
	}

	public void updateNotification(int progress, int speed) {
		testNotification();
		notificaion.contentView.setProgressBar(update_notification_progressbar, 100, progress, false);
		
		notificaion.contentView.setTextViewText(update_notification_progresstext, progress+"% "+FileUtils.getReadableSize(speed)+"/s");
						
		notificationManager.notify(noid,notificaion);
	}

	public void failureNotification() {
		testNotification();
		notificaion.tickerText = title;
		notificaion.when = System.currentTimeMillis();
		notificaion.contentView.setViewVisibility(update_notification_progressbar, View.GONE);
		notificaion.contentView.setViewVisibility(update_notification_progresstext,View.GONE);
		notificaion.contentView.setViewVisibility(update_notification_infotext,View.VISIBLE);
		notificaion.contentView.setTextViewText(update_notification_infotext,context.getString (download_failure_text));
		notificaion.flags = Notification.FLAG_AUTO_CANCEL;
		notificaion.defaults = Notification.DEFAULT_SOUND;
		notificationManager.notify(noid, notificaion);
	}
	
	//please set this value
	int update_notification_icon;
	int update_notification_progressbar;
	int update_notification_progressblock;
	int update_notification_progresstext;
	int update_notification_titletext;
	int update_notification_timetext;
	int update_notification_infotext;
	int update_notification_layout;
	int download_failure_text;
	int download_success_text;
}
