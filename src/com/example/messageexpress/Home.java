package com.example.messageexpress;

import java.util.ArrayList;
import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;

public class Home extends Fragment {
	private ImageButton delayMessage;
	private final int REQUEST_CODE = 2;
	private String textMessage;
	private int hour;
	private int minute;
	private int month;
	private int year;
	private int day;
	String repeatOptionss;
	ArrayList<String> phoneList;
	ArrayList<String> cNameList;
	private ArrayList<AlarmsInfoDataStruct> alarmsList;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View homeScreen = inflater.inflate(R.layout.home, container, false);
		delayMessage = (ImageButton) homeScreen.findViewById(R.id.delayMessage);
		delayMessage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent delayMsgActivity = new Intent(getActivity(),
						DelayMsgActivity.class);
				startActivityForResult(delayMsgActivity, REQUEST_CODE);
			}
		});
		return homeScreen;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case (REQUEST_CODE): {
			if (resultCode == Activity.RESULT_OK) {
				// TODO Extract the data returned from the child Activity.
				hour = data.getIntExtra(Shared.KEY_HOUR, 0);
				minute = data.getIntExtra(Shared.KEY_MIN, 0);
				day = data.getIntExtra(Shared.KEY_DAY, 0);
				month = data.getIntExtra(Shared.KEY_MONTH, 0);
				year = data.getIntExtra(Shared.KEY_YEAR, 0);
				DatabaseHandler dbHandler = new DatabaseHandler(getActivity());
				alarmsList = new ArrayList<>();
				alarmsList = dbHandler.getAlarmsInfo();
				phoneList = new ArrayList<>();
				cNameList = new ArrayList<>();
				phoneList = data.getStringArrayListExtra(Shared.KEY_TO);
				cNameList = data.getStringArrayListExtra(Shared.KEY_CNAME);
				repeatOptionss = data.getStringExtra(Shared.KEY_REPEAT);
				int signatureChecked = data.getIntExtra(Shared.KEY_SIGNATURE, 0);
				if (alarmsList.size() == 0) {
					textMessage = data.getStringExtra(Shared.KEY_MESSAGE);
					AlarmsInfoDataStruct alarmInfoDataStruct = scheduleMessage();
					MessageDataStruct msgDataStruct = new MessageDataStruct();
					final int messageId = (int) System.currentTimeMillis();
					msgDataStruct.setMessageid(messageId);
					msgDataStruct.setMessage(textMessage);
					msgDataStruct.setTo(phoneList);
					msgDataStruct.setStatus("قيد الإرسال");
					msgDataStruct.setRepeat(repeatOptionss);
					msgDataStruct.setSignature(signatureChecked);
					msgDataStruct.setHour(alarmInfoDataStruct.getHour());
					msgDataStruct.setMinute(alarmInfoDataStruct.getMinute());
					msgDataStruct.setYear(alarmInfoDataStruct.getYear());
					msgDataStruct.setMonth(alarmInfoDataStruct.getMonth());
					msgDataStruct.setDay(alarmInfoDataStruct.getDay());
					msgDataStruct.setAlarmid(alarmInfoDataStruct.getId());
					msgDataStruct.setCname(cNameList);
					alarmsList.add(alarmInfoDataStruct);
					dbHandler.addAlarmInfo(alarmInfoDataStruct);
					dbHandler.addMessageToDB(msgDataStruct);
					phoneList.clear();
				} else {
					textMessage = data.getStringExtra(Shared.KEY_MESSAGE);
					final int messageId = (int) System.currentTimeMillis();
					MessageDataStruct msgDataStruct = new MessageDataStruct();
					boolean isAlarm = false;
					for (int k = 0; k < alarmsList.size(); k++) {
						if ((repeatOptionss.equals(alarmsList.get(k).getRepeatoption()))&& repeatOptionss.equals("Hourly")) {
							if (hour == alarmsList.get(k).getHour() && (minute == alarmsList.get(k).getMinute())) {
								msgDataStruct.setAlarmid(alarmsList.get(k)
										.getId());
								msgDataStruct.setHour(alarmsList.get(k)
										.getHour());
								msgDataStruct.setMinute(alarmsList.get(k)
										.getMinute());
								msgDataStruct.setYear(alarmsList.get(k)
										.getYear());
								msgDataStruct.setMonth(alarmsList.get(k)
										.getMonth());
								msgDataStruct
										.setDay(alarmsList.get(k).getDay());
								isAlarm = true;
								break;
							}
						} else if ((repeatOptionss.equals("Daily")) && (repeatOptionss.equals(alarmsList.get(k).getRepeatoption()))) {
							if (hour == alarmsList.get(k).getHour()  && (minute == alarmsList.get(k).getMinute())) {
								msgDataStruct.setAlarmid(alarmsList.get(k)
										.getId());
								msgDataStruct.setHour(alarmsList.get(k)
										.getHour());
								msgDataStruct.setMinute(alarmsList.get(k)
										.getMinute());
								msgDataStruct.setYear(alarmsList.get(k)
										.getYear());
								msgDataStruct.setMonth(alarmsList.get(k)
										.getMonth());
								msgDataStruct
										.setDay(alarmsList.get(k).getDay());
								isAlarm = true;
								break;
							}
						} else if ((repeatOptionss.equals("Weekly"))&&(repeatOptionss.equals(alarmsList.get(k).getRepeatoption()))) {
							if ((hour == alarmsList.get(k).getHour())
									&& (day == alarmsList.get(k).getDay())  && (minute == alarmsList.get(k).getMinute())) {
								msgDataStruct.setAlarmid(alarmsList.get(k)
										.getId());
								msgDataStruct.setHour(alarmsList.get(k)
										.getHour());
								msgDataStruct.setMinute(alarmsList.get(k)
										.getMinute());
								msgDataStruct.setYear(alarmsList.get(k)
										.getYear());
								msgDataStruct.setMonth(alarmsList.get(k)
										.getMonth());
								msgDataStruct
										.setDay(alarmsList.get(k).getDay());
								isAlarm = true;
								break;
							}

						} else if ((repeatOptionss.equals("Yearly"))&& (repeatOptionss.equals(alarmsList.get(k).getRepeatoption()))) {
							if ((hour == alarmsList.get(k).getHour())
									&& (day == alarmsList.get(k).getDay())
									&& (month == alarmsList.get(k).getMonth())  && (minute == alarmsList.get(k).getMinute())) {
								msgDataStruct.setAlarmid(alarmsList.get(k)
										.getId());
								msgDataStruct.setHour(alarmsList.get(k)
										.getHour());
								msgDataStruct.setMinute(alarmsList.get(k)
										.getMinute());
								msgDataStruct.setYear(alarmsList.get(k)
										.getYear());
								msgDataStruct.setMonth(alarmsList.get(k)
										.getMonth());
								msgDataStruct
										.setDay(alarmsList.get(k).getDay());
								isAlarm = true;
								break;
							}

						} else if ((repeatOptionss.equals("Monthly")) && (repeatOptionss.equals(alarmsList.get(k).getRepeatoption()))) {
							if ((hour == alarmsList.get(k).getHour())
									&& (day == alarmsList.get(k).getDay()) && (minute == alarmsList.get(k).getMinute())) {
								msgDataStruct.setAlarmid(alarmsList.get(k)
										.getId());
								msgDataStruct.setHour(alarmsList.get(k)
										.getHour());
								msgDataStruct.setMinute(alarmsList.get(k)
										.getMinute());
								msgDataStruct.setYear(alarmsList.get(k)
										.getYear());
								msgDataStruct.setMonth(alarmsList.get(k)
										.getMonth());
								msgDataStruct
										.setDay(alarmsList.get(k).getDay());
								isAlarm = true;
								break;
							}
						}
					}
					if (!isAlarm) {
						AlarmsInfoDataStruct alarmInfoDataStruct = scheduleMessage();
						msgDataStruct.setAlarmid(alarmInfoDataStruct.getId());
						msgDataStruct.setMessage(textMessage);
						msgDataStruct.setTo(phoneList);
						msgDataStruct.setRepeat(repeatOptionss);
						msgDataStruct.setMessageid(messageId);
						msgDataStruct.setStatus("قيد الإرسال");
						msgDataStruct.setHour(alarmInfoDataStruct.getHour());
						msgDataStruct
								.setMinute(alarmInfoDataStruct.getMinute());
						msgDataStruct.setYear(alarmInfoDataStruct.getYear());
						msgDataStruct.setMonth(alarmInfoDataStruct.getMonth());
						msgDataStruct.setDay(alarmInfoDataStruct.getDay());
						msgDataStruct.setSignature(signatureChecked);
						msgDataStruct.setCname(cNameList);
						dbHandler.addAlarmInfo(alarmInfoDataStruct);
						dbHandler.addMessageToDB(msgDataStruct);
						phoneList.clear();
					} else {
						msgDataStruct.setMessage(textMessage);
						msgDataStruct.setTo(phoneList);
						msgDataStruct.setCname(cNameList);
						msgDataStruct.setRepeat(repeatOptionss);
						msgDataStruct.setStatus("قيد الإرسال");
						msgDataStruct.setSignature(signatureChecked);
						msgDataStruct.setMessageid(messageId);
						dbHandler.addMessageToDB(msgDataStruct);
						phoneList.clear();
					}
				}

			}
			break;
		}
		}
	}

	private AlarmsInfoDataStruct scheduleMessage() {
		AlarmsInfoDataStruct alarmInfoDataStruct = new AlarmsInfoDataStruct();
		final int _id = (int) System.currentTimeMillis();
		AlarmManager alarmManager = (AlarmManager) getActivity()
				.getSystemService(Context.ALARM_SERVICE);
		Intent intentAlarm = new Intent(getActivity(), AlarmReciever.class);
		intentAlarm.putExtra("alarmid", _id);
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.DAY_OF_MONTH, day);
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minute);
		alarmInfoDataStruct.setHour(hour);
		alarmInfoDataStruct.setMinute(minute);
		alarmInfoDataStruct.setDay(day);
		alarmInfoDataStruct.setYear(year);
		alarmInfoDataStruct.setMonth(month);
		alarmInfoDataStruct.setId(_id);
		PendingIntent pintent = PendingIntent.getBroadcast(getActivity(), _id,
				intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT);

		alarmInfoDataStruct.setRepeatoption(repeatOptionss);
		switch (repeatOptionss) {
		case " ": {
			alarmManager.set(AlarmManager.RTC_WAKEUP,
					calendar.getTimeInMillis(), pintent);
			break;

		}
		case "Hourly": {
			alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
					calendar.getTimeInMillis(), AlarmManager.INTERVAL_HOUR,
					pintent);
			break;
		}
		case "Daily": {
			alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
					calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY,
					pintent);
			break;
		}
		case "Weekly": {
			alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
					calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY * 7,
					pintent);
			break;
		}
		case "Monthly": {
			alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
					calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY * 30,
					pintent);
			break;
		}
		case "Yearly": {
			alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
					calendar.getTimeInMillis(),
					AlarmManager.INTERVAL_DAY * 365, pintent);
			break;
		}
		default:
			break;
		}
		return alarmInfoDataStruct;
	}
}
