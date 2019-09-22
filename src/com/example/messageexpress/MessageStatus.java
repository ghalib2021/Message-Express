package com.example.messageexpress;

import java.util.ArrayList;
import java.util.Calendar;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;

public class MessageStatus extends BroadcastReceiver {
	int alarmId = 0;
	String repeatOption;
	String phoneNumberReciver = "";
	String SENTSMS = "SMSMESSAGE";
	ArrayList<String> phoneList;
	int hour;
	int minute;
	int day;
	int month;
	int year;

	@Override
	public void onReceive(Context context, Intent intent) {
		DatabaseHandler dbHandler = new DatabaseHandler(context);
		MessageDataStruct messageDataStruct = new MessageDataStruct();
		hour = intent.getIntExtra(Shared.KEY_HOUR, 0);
		minute = intent.getIntExtra(Shared.KEY_MIN, 0);
		day = intent.getIntExtra(Shared.KEY_DAY, 0);
		month = intent.getIntExtra(Shared.KEY_MONTH, 0);
		year = intent.getIntExtra(Shared.KEY_YEAR, 0);
		phoneNumberReciver = intent.getStringExtra(Shared.KEY_TO);
		repeatOption = intent.getStringExtra(Shared.KEY_REPEAT);
		alarmId = intent.getIntExtra("alarmid", 0);
		messageDataStruct.setAlarmid(alarmId);
		messageDataStruct.setRepeat(repeatOption);
		phoneList = new ArrayList<String>();
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR, hour);
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.DAY_OF_MONTH, day);
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month);
		boolean isNeverRepeated = false;
		messageDataStruct.setHour(calendar.get(Calendar.HOUR));
		messageDataStruct.setMinute(calendar.get(Calendar.MINUTE));
		messageDataStruct.setDay(calendar.get(Calendar.DAY_OF_MONTH));
		messageDataStruct.setMonth(calendar.get(Calendar.MONTH));
		messageDataStruct.setYear(calendar.get(Calendar.YEAR));
		phoneList.clear();
		phoneList.add(phoneNumberReciver);
		messageDataStruct.setTo(phoneList);
		if (repeatOption.equals(" ")) {
			isNeverRepeated = true;

		}
		switch (getResultCode()) {
		case Activity.RESULT_OK: {
			if (isNeverRepeated) {
				messageDataStruct.setStatus("Pending");
				dbHandler.updateMessageStatus(messageDataStruct);
			} else {
				messageDataStruct.setStatus("Pending");
				dbHandler.updateMessageStatus(messageDataStruct);
				if (messageDataStruct.getRepeat().equals("Monthly")) {
					calendar.add(Calendar.MONTH, 1);
				} else if (messageDataStruct.getRepeat().equals("Yearly")) {
					calendar.add(Calendar.YEAR, 1);
				} else if (messageDataStruct.getRepeat().equals("Weekly")) {
					calendar.add(Calendar.DATE, 7);
				} else if (messageDataStruct.getRepeat().equals("Daily")) {
					calendar.add(Calendar.DATE, 1);
				} else if (messageDataStruct.getRepeat().equals("Hourly")) {
					calendar.add(Calendar.HOUR, 1);
				}
				messageDataStruct.setHour(calendar.get(Calendar.HOUR));
				messageDataStruct.setMinute(calendar.get(Calendar.MINUTE));
				messageDataStruct.setDay(calendar.get(Calendar.DAY_OF_MONTH));
				messageDataStruct.setMonth(calendar.get(Calendar.MONTH));
				messageDataStruct.setYear(calendar.get(Calendar.YEAR));
				dbHandler.addMessageToDB(messageDataStruct);
			}
			context.sendBroadcast(new Intent(SENTSMS));
			break;
		}
		case SmsManager.RESULT_ERROR_GENERIC_FAILURE: {

			break;
		}
		case SmsManager.RESULT_ERROR_NO_SERVICE:

			break;
		case SmsManager.RESULT_ERROR_NULL_PDU:

			break;
		case SmsManager.RESULT_ERROR_RADIO_OFF:

			break;
		}
	}

}
