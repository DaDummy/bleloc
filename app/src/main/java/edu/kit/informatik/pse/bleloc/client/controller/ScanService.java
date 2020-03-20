package edu.kit.informatik.pse.bleloc.client.controller;

import android.app.*;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import edu.kit.informatik.pse.bleloc.client.controller.activities.SettingsActivity;
import edu.kit.informatik.pse.bleloc.client.R;

public class ScanService extends Service {

	private NotificationManager notificationManager;

	@Override
	public void onCreate() {
		super.onCreate();

		Notification.Builder notificationBuilder = new Notification.Builder(this);
		if (Build.VERSION.SDK_INT >= 26) {
			NotificationChannel chan = new NotificationChannel("bleloc", "Background Scanning", NotificationManager.IMPORTANCE_NONE);
			chan.setLightColor(R.color.colorPrimary);
			chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
			NotificationManager service = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
			service.createNotificationChannel(chan);
			notificationBuilder.setChannelId("bleloc");
		}

		Notification notification = notificationBuilder
			.setContentTitle("Background scanning")
			.setSmallIcon(R.drawable.ic_bluetooth_searching_white)
			.setContentIntent(PendingIntent.getActivity(this, 0, new Intent(this, SettingsActivity.class), Notification.FLAG_ONGOING_EVENT))
			.build();

		startForeground(1, notification);

		((Context)getApplication()).getScanResultUploadManager().triggerUpload();
		((Context)getApplication()).getBackgroundScanManager().setActive(true);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		stopForeground(true);
		((Context)getApplication()).getBackgroundScanManager().setActive(false);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}
