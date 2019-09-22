package com.example.messageexpress;

import java.util.ArrayList;
import java.util.Calendar;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.SmsManager;

public class AlarmReciever extends BroadcastReceiver {
	//String SENTSMS = "SMSMESSAGE";
	//String DELIVERED = "SMS_DELIVERED";
	String SENT = "SMSSENT";
	DatabaseHandler dbHandler;
	MessageDataStruct msgData;
	boolean isNotificationEnabled = false;
	boolean isLoggingEnabled = false;
	boolean isSignatured=false;
	String signature = "";
	String messageSent = "تم ارسال الرسالة";
	String messageNotSent = "لم ترسل الرسالة";
	String message;

	// messageLogging
	@Override
	public void onReceive(Context context, Intent intent) {

		msgData = new MessageDataStruct();
		msgData.setTo(new ArrayList<String>());
		msgData.setCname(new ArrayList<String>());
		dbHandler = new DatabaseHandler(context);
		SharedPreferences sharedPrefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		isLoggingEnabled = sharedPrefs.getBoolean(Shared.KEY_LOGENABLED, false);
		isNotificationEnabled = sharedPrefs.getBoolean(Shared.KEY_NOTENABLED,
				false);
	
		int alarmId = intent.getIntExtra("alarmid", 0);
		Intent msgStatusIntent = new Intent(SENT);
		ArrayList<MessageDataStruct> messageList = dbHandler
				.retrieveSelectedMessagesList(alarmId);
		
		boolean isNeverRepeated = false;
		Calendar calendar = Calendar.getInstance();

		if (messageList.size() != 0) {
			calendar.set(Calendar.MONTH, messageList.get(0).getMonth());
			calendar.set(Calendar.YEAR, messageList.get(0).getYear());
			calendar.set(Calendar.DAY_OF_MONTH, messageList.get(0).getDay());
			calendar.set(Calendar.HOUR_OF_DAY, messageList.get(0).getHour());
			calendar.set(Calendar.MINUTE, messageList.get(0).getMinute());
			msgStatusIntent.putExtra(Shared.KEY_HOUR, messageList.get(0)
					.getHour());
			msgStatusIntent.putExtra(Shared.KEY_MIN, messageList.get(0)
					.getMinute());
			msgStatusIntent.putExtra(Shared.KEY_DAY, messageList.get(0)
					.getDay());
			msgStatusIntent.putExtra(Shared.KEY_MONTH, messageList.get(0)
					.getMonth());
			msgStatusIntent.putExtra(Shared.KEY_YEAR, messageList.get(0)
					.getYear());
			msgStatusIntent.putExtra(Shared.KEY_REPEAT, messageList.get(0)
					.getRepeat());
			msgStatusIntent
					.putExtra("alarmid", messageList.get(0).getAlarmid());
			if(messageList.get(0).isSignature()==1) {
				signature = sharedPrefs.getString(Shared.KEY_SIGNATURE, "");
			}
		}


		for (int i = 0; i < messageList.size(); i++) {
			if (messageList.get(i).getRepeat().equals("Never")) {
				isNeverRepeated = true;
			}
			msgData.setAlarmid(messageList.get(i).getAlarmid());
			msgData.setDay(messageList.get(i).getDay());
			msgData.setMonth(messageList.get(i).getMonth());
			msgData.setYear(messageList.get(i).getYear());
			msgData.setMinute(messageList.get(i).getMinute());
			msgData.setHour(messageList.get(i).getHour());
			msgData.setMessage(messageList.get(i).getMessage());
			msgData.setMessageid(messageList.get(i).getMessageid());
			msgData.setRepeat(" ");
			msgData.setStatus(messageList.get(i).getStatus());
			msgData.setTo(messageList.get(i).getTo());
			msgData.setCname(messageList.get(i).getCname());
			Intent scheduleMsgIntent = new Intent("schedule-event");
			scheduleMsgIntent.putExtra("index", i);
			LocalBroadcastManager.getInstance(context.getApplicationContext())
					.sendBroadcast(scheduleMsgIntent);
			for (int k = 0; k < messageList.get(i).getTo().size(); k++) {
				String phoneNumberReciver = messageList.get(i).getTo().get(k);// phone
				 message = messageList.get(i).getMessage(); // message //
				if (signature != "") {
					message = message.concat("-" + signature);
					//messageList.get(i).setMessage(message);
				}
				SmsManager sms = SmsManager.getDefault();
				msgStatusIntent.putExtra(Shared.KEY_TO, phoneNumberReciver);
				msgStatusIntent.putExtra(Shared.KEY_CNAME, messageList.get(i)
						.getCname().get(k));
			
				PendingIntent sentPI = PendingIntent.getBroadcast(
						context.getApplicationContext(), 0, msgStatusIntent, 0);
                try {
                boolean isAirPlaneMode = isAirplaneModeOn(context);
                if(!isAirPlaneMode) {
				sms.sendTextMessage(
						phoneNumberReciver.replaceAll("[^+0-9]", ""), null,
						message, sentPI, null);
                } else {
                	if (isNotificationEnabled) {
						Intent service1 = new Intent(context,
								MyAlarmService.class);
						service1.putExtra("NotifEnabled", true);
						service1.putExtra("notifmsg", messageNotSent);
						context.startService(service1);

					}
					if (isNeverRepeated) {
						msgData.setStatus("قيد الإنتظار");
						dbHandler
								.updateNotSentMessageStatus(msgData);
					} else {
						msgData.setStatus("قيد الإنتظار");
						dbHandler
								.updateNotSentMessageStatus(msgData);
						if (msgData.getRepeat().equals(
								"Monthly")) {
							calendar.add(Calendar.MONTH, 1);
						} else if (msgData.getRepeat()
								.equals("Yearly")) {
							calendar.add(Calendar.YEAR, 1);
						} else if (msgData.getRepeat()
								.equals("Weekly")) {
							calendar.add(Calendar.DATE, 7);
						} else if (msgData.getRepeat()
								.equals("Daily")) {
							calendar.add(Calendar.DATE, 1);
						} else if (msgData.getRepeat()
								.equals("Hourly")) {
							calendar.add(Calendar.HOUR_OF_DAY, 1);
						}
						msgData.setHour(calendar
								.get(Calendar.HOUR_OF_DAY));
						msgData.setMinute(calendar
								.get(Calendar.MINUTE));
						msgData.setDay(calendar
								.get(Calendar.DAY_OF_MONTH));
						msgData.setMonth(calendar
								.get(Calendar.MONTH));
						msgData.setYear(calendar
								.get(Calendar.YEAR));
						dbHandler.addMessageToDB(msgData);
					}

					Intent failMessageIntent = new Intent(
							"sentfail-event");
					failMessageIntent.putExtra("messageid", msgData.getMessageid());
					LocalBroadcastManager.getInstance(
							context.getApplicationContext())
							.sendBroadcast(failMessageIntent);

                }
                } catch(Exception e) {
                	e.printStackTrace();
                }

			}

		}

		if (isNeverRepeated) {
			dbHandler.deleteAlarm(alarmId);
		}
		context.getApplicationContext().registerReceiver(
				new BroadcastReceiver() {

					@Override
					public void onReceive(Context context, Intent intent) {

						MessageDataStruct messageDataStruct = new MessageDataStruct();
						messageDataStruct = msgData;
						Calendar calendar = Calendar.getInstance();
						calendar.set(Calendar.HOUR_OF_DAY,
								messageDataStruct.getHour());
						calendar.set(Calendar.MINUTE,
								messageDataStruct.getMinute());
						calendar.set(Calendar.DAY_OF_MONTH,
								messageDataStruct.getDay());
						calendar.set(Calendar.YEAR, messageDataStruct.getYear());
						calendar.set(Calendar.MONTH,
								messageDataStruct.getMonth());
						boolean isNeverRepeated = false;
						if (messageDataStruct.getRepeat().equals("Never")) {
							isNeverRepeated = true;

						}
						
						switch (getResultCode()) {
						case Activity.RESULT_OK: {
							if (isNotificationEnabled || isLoggingEnabled) {
								Intent service1 = new Intent(context,
										MyAlarmService.class);
								if (isNotificationEnabled && isLoggingEnabled) {
									service1.putExtra("LogingEnabled", true);
									service1.putExtra("NotifEnabled", true);
									service1.putExtra("TO", messageDataStruct
											.getTo().get(0));
									service1.putExtra("name", messageDataStruct
											.getCname().get(0));
									service1.putExtra("message",
											message);
								}
								if (isNotificationEnabled) {
									service1.putExtra("LogingEnabled", false);
									service1.putExtra("NotifEnabled", true);
									service1.putExtra("notifmsg", messageSent);
								}
								if (isLoggingEnabled) {
									service1.putExtra("LogingEnabled", true);
									service1.putExtra("NotifEnabled", false);
									service1.putExtra("TO", messageDataStruct
											.getTo().get(0));
									service1.putExtra("name", messageDataStruct
											.getCname().get(0));
									service1.putExtra("message",
											message);
								}
								context.startService(service1);
							}
							if (isNeverRepeated) {
								messageDataStruct.setStatus("قيد الإنتظار");
								dbHandler
										.updateMessageStatus(messageDataStruct);
							} else {
								messageDataStruct.setStatus("قيد الإنتظار");
								dbHandler
										.updateMessageStatus(messageDataStruct);
								if (messageDataStruct.getRepeat().equals(
										"Monthly")) {
									calendar.add(Calendar.MONTH, 1);
								} else if (messageDataStruct.getRepeat()
										.equals("Yearly")) {
									calendar.add(Calendar.YEAR, 1);
								} else if (messageDataStruct.getRepeat()
										.equals("Weekly")) {
									calendar.add(Calendar.DATE, 7);
								} else if (messageDataStruct.getRepeat()
										.equals("Daily")) {
									calendar.add(Calendar.DATE, 1);
								} else if (messageDataStruct.getRepeat()
										.equals("Hourly")) {
									calendar.add(Calendar.HOUR_OF_DAY, 1);
								}
								messageDataStruct.setHour(calendar
										.get(Calendar.HOUR_OF_DAY));
								messageDataStruct.setMinute(calendar
										.get(Calendar.MINUTE));
								messageDataStruct.setDay(calendar
										.get(Calendar.DAY_OF_MONTH));
								messageDataStruct.setMonth(calendar
										.get(Calendar.MONTH));
								messageDataStruct.setYear(calendar
										.get(Calendar.YEAR));
								dbHandler.addMessageToDB(messageDataStruct);
							}
							Intent scheduleMsgIntent = new Intent(
									"schedule-event");
							LocalBroadcastManager.getInstance(
									context.getApplicationContext())
									.sendBroadcast(scheduleMsgIntent);
							Intent sentMessageIntent = new Intent("sent-event");
							LocalBroadcastManager.getInstance(
									context.getApplicationContext())
									.sendBroadcast(sentMessageIntent);
							break;
						}
						case SmsManager.RESULT_ERROR_GENERIC_FAILURE: {
							if (isNotificationEnabled) {
								Intent service1 = new Intent(context,
										MyAlarmService.class);
								service1.putExtra("NotifEnabled", true);
								service1.putExtra("notifmsg", messageNotSent);
								context.startService(service1);

							}
							if (isNeverRepeated) {
								messageDataStruct.setStatus("قيد الإنتظار");
								dbHandler
										.updateNotSentMessageStatus(messageDataStruct);
							} else {
								messageDataStruct.setStatus("قيد الإنتظار");
								dbHandler
										.updateNotSentMessageStatus(messageDataStruct);
								if (messageDataStruct.getRepeat().equals(
										"Monthly")) {
									calendar.add(Calendar.MONTH, 1);
								} else if (messageDataStruct.getRepeat()
										.equals("Yearly")) {
									calendar.add(Calendar.YEAR, 1);
								} else if (messageDataStruct.getRepeat()
										.equals("Weekly")) {
									calendar.add(Calendar.DATE, 7);
								} else if (messageDataStruct.getRepeat()
										.equals("Daily")) {
									calendar.add(Calendar.DATE, 1);
								} else if (messageDataStruct.getRepeat()
										.equals("Hourly")) {
									calendar.add(Calendar.HOUR_OF_DAY, 1);
								}
								messageDataStruct.setHour(calendar
										.get(Calendar.HOUR_OF_DAY));
								messageDataStruct.setMinute(calendar
										.get(Calendar.MINUTE));
								messageDataStruct.setDay(calendar
										.get(Calendar.DAY_OF_MONTH));
								messageDataStruct.setMonth(calendar
										.get(Calendar.MONTH));
								messageDataStruct.setYear(calendar
										.get(Calendar.YEAR));
								dbHandler.addMessageToDB(messageDataStruct);
							}

							Intent failMessageIntent = new Intent(
									"sentfail-event");
							failMessageIntent.putExtra("messageid", messageDataStruct.getMessageid());
							LocalBroadcastManager.getInstance(
									context.getApplicationContext())
									.sendBroadcast(failMessageIntent);
							break;
						}
						case SmsManager.RESULT_ERROR_NO_SERVICE: {
							if (isNotificationEnabled) {
								Intent service1 = new Intent(context,
										MyAlarmService.class);
								service1.putExtra("NotifEnabled", true);
								service1.putExtra("notifmsg", messageNotSent);
								context.startService(service1);
							}
							if (isNeverRepeated) {
								messageDataStruct.setStatus("قيد الإنتظار");
								dbHandler
										.updateNotSentMessageStatus(messageDataStruct);
							} else {
								messageDataStruct.setStatus("قيد الإنتظار");
								dbHandler
										.updateNotSentMessageStatus(messageDataStruct);
								if (messageDataStruct.getRepeat().equals(
										"Monthly")) {
									calendar.add(Calendar.MONTH, 1);
								} else if (messageDataStruct.getRepeat()
										.equals("Yearly")) {
									calendar.add(Calendar.YEAR, 1);
								} else if (messageDataStruct.getRepeat()
										.equals("Weekly")) {
									calendar.add(Calendar.DATE, 7);
								} else if (messageDataStruct.getRepeat()
										.equals("Daily")) {
									calendar.add(Calendar.DATE, 1);
								} else if (messageDataStruct.getRepeat()
										.equals("Hourly")) {
									calendar.add(Calendar.HOUR_OF_DAY, 1);
								}
								messageDataStruct.setHour(calendar
										.get(Calendar.HOUR_OF_DAY));
								messageDataStruct.setMinute(calendar
										.get(Calendar.MINUTE));
								messageDataStruct.setDay(calendar
										.get(Calendar.DAY_OF_MONTH));
								messageDataStruct.setMonth(calendar
										.get(Calendar.MONTH));
								messageDataStruct.setYear(calendar
										.get(Calendar.YEAR));
								dbHandler.addMessageToDB(messageDataStruct);
							}
							Intent failMessageIntent = new Intent(
									"sentfail-event");
							failMessageIntent.putExtra("messageid", messageDataStruct.getMessageid());
							LocalBroadcastManager.getInstance(
									context.getApplicationContext())
									.sendBroadcast(failMessageIntent);
							break;
						}
						case SmsManager.RESULT_ERROR_NULL_PDU: {
							if (isNotificationEnabled) {
								Intent service1 = new Intent(context,
										MyAlarmService.class);
								service1.putExtra("NotifEnabled", true);
								service1.putExtra("notifmsg", messageNotSent);
								context.startService(service1);
							}
							if (isNeverRepeated) {
								messageDataStruct.setStatus("قيد الإنتظار");
								dbHandler
										.updateNotSentMessageStatus(messageDataStruct);
							} else {
								messageDataStruct.setStatus("قيد الإنتظار");
								dbHandler
										.updateNotSentMessageStatus(messageDataStruct);
								if (messageDataStruct.getRepeat().equals(
										"Monthly")) {
									calendar.add(Calendar.MONTH, 1);
								} else if (messageDataStruct.getRepeat()
										.equals("Yearly")) {
									calendar.add(Calendar.YEAR, 1);
								} else if (messageDataStruct.getRepeat()
										.equals("Weekly")) {
									calendar.add(Calendar.DATE, 7);
								} else if (messageDataStruct.getRepeat()
										.equals("Daily")) {
									calendar.add(Calendar.DATE, 1);
								} else if (messageDataStruct.getRepeat()
										.equals("Hourly")) {
									calendar.add(Calendar.HOUR_OF_DAY, 1);
								}
								messageDataStruct.setHour(calendar
										.get(Calendar.HOUR_OF_DAY));
								messageDataStruct.setMinute(calendar
										.get(Calendar.MINUTE));
								messageDataStruct.setDay(calendar
										.get(Calendar.DAY_OF_MONTH));
								messageDataStruct.setMonth(calendar
										.get(Calendar.MONTH));
								messageDataStruct.setYear(calendar
										.get(Calendar.YEAR));
								dbHandler.addMessageToDB(messageDataStruct);
							}
							Intent failMessageIntent = new Intent(
									"sentfail-event");
							failMessageIntent.putExtra("messageid", messageDataStruct.getMessageid());
							LocalBroadcastManager.getInstance(
									context.getApplicationContext())
									.sendBroadcast(failMessageIntent);
							break;
						}
						case SmsManager.RESULT_ERROR_RADIO_OFF: {
							if (isNotificationEnabled) {
								Intent service1 = new Intent(context,
										MyAlarmService.class);
								service1.putExtra("NotifEnabled", true);
								service1.putExtra("notifmsg", messageNotSent);
								context.startService(service1);
							}
							if (isNeverRepeated) {
								messageDataStruct.setStatus("قيد الإنتظار");
								dbHandler
										.updateNotSentMessageStatus(messageDataStruct);
							} else {
								messageDataStruct.setStatus("قيد الإنتظار");
								dbHandler
										.updateNotSentMessageStatus(messageDataStruct);
								if (messageDataStruct.getRepeat().equals(
										"Monthly")) {
									calendar.add(Calendar.MONTH, 1);
								} else if (messageDataStruct.getRepeat()
										.equals("Yearly")) {
									calendar.add(Calendar.YEAR, 1);
								} else if (messageDataStruct.getRepeat()
										.equals("Weekly")) {
									calendar.add(Calendar.DATE, 7);
								} else if (messageDataStruct.getRepeat()
										.equals("Daily")) {
									calendar.add(Calendar.DATE, 1);
								} else if (messageDataStruct.getRepeat()
										.equals("Hourly")) {
									calendar.add(Calendar.HOUR_OF_DAY, 1);
								}
								messageDataStruct.setHour(calendar
										.get(Calendar.HOUR_OF_DAY));
								messageDataStruct.setMinute(calendar
										.get(Calendar.MINUTE));
								messageDataStruct.setDay(calendar
										.get(Calendar.DAY_OF_MONTH));
								messageDataStruct.setMonth(calendar
										.get(Calendar.MONTH));
								messageDataStruct.setYear(calendar
										.get(Calendar.YEAR));
								dbHandler.addMessageToDB(messageDataStruct);
							}
							Intent failMessageIntent = new Intent(
									"sentfail-event");
							failMessageIntent.putExtra("messageid", messageDataStruct.getMessageid());
							LocalBroadcastManager.getInstance(
									context.getApplicationContext())
									.sendBroadcast(failMessageIntent);
							break;
						}
						}
						context.unregisterReceiver(this);
					}

				}, new IntentFilter(SENT));

	}
	@SuppressWarnings("deprecation")
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
	public static boolean isAirplaneModeOn(Context context) {        
	    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
	        return Settings.System.getInt(context.getContentResolver(), 
	                Settings.System.AIRPLANE_MODE_ON, 0) != 0;          
	    } else {
	        return Settings.Global.getInt(context.getContentResolver(), 
	                Settings.Global.AIRPLANE_MODE_ON, 0) != 0;
	    }       
	}
}
