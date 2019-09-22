package com.example.messageexpress;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;

public class MyAlarmService extends Service

{
	private static final String TELEPHON_NUMBER_FIELD_NAME = "address";
	private static final String MESSAGE_BODY_FIELD_NAME = "body";
	private static final Uri SENT_MSGS_CONTET_PROVIDER = Uri
			.parse("content://sms/sent");
	private NotificationManager mManager;

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
	}

	@SuppressWarnings({ "static-access", "deprecation" })
	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		if (intent != null) {
			boolean isLogingEnabled = intent.getBooleanExtra("LogingEnabled",
					false);
			boolean isNotificationEnabled = intent.getBooleanExtra("NotifEnabled", false);
			String notificationMessage = intent.getStringExtra("notifmsg");
			//if (isNotificationEnabled) {
				mManager = (NotificationManager) this
						.getApplicationContext()
						.getSystemService(
								this.getApplicationContext().NOTIFICATION_SERVICE);
				Intent intent1 = new Intent(this.getApplicationContext(),
						MainActivity.class);
				@SuppressWarnings("deprecation")
				Notification notification = new Notification(
						R.drawable.iconmessageexpress,
						notificationMessage, System.currentTimeMillis());

				intent1.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP
						| Intent.FLAG_ACTIVITY_CLEAR_TOP);

				PendingIntent pendingNotificationIntent = PendingIntent
						.getActivity(this.getApplicationContext(), 0, intent1,
								PendingIntent.FLAG_UPDATE_CURRENT);
				notification.flags |= Notification.FLAG_AUTO_CANCEL;
				notification.setLatestEventInfo(this.getApplicationContext(),
						"مرسل الرسائل", notificationMessage,
						pendingNotificationIntent);

				mManager.notify(0, notification);
		//	}
			if (isLogingEnabled) {
				String telNumber = intent.getStringExtra("TO");
				String messageBody = intent.getStringExtra("message");
				addMessageToSentIfPossible(telNumber, messageBody);
			}

		}
		stopSelf();
	}

	private void addMessageToSentIfPossible(String telNumb, String msgBody) {

		String telNumber = telNumb;
		String messageBody = msgBody;
		if (telNumber != null && messageBody != null) {
			addMessageToSent(telNumber, messageBody);
		}
	}

	private void addMessageToSent(String telNumber, String messageBody) {
		ContentValues sentSms = new ContentValues();
		sentSms.put(TELEPHON_NUMBER_FIELD_NAME, telNumber);
		sentSms.put(MESSAGE_BODY_FIELD_NAME, messageBody);

		ContentResolver contentResolver = getContentResolver();
		contentResolver.insert(SENT_MSGS_CONTET_PROVIDER, sentSms);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

}
