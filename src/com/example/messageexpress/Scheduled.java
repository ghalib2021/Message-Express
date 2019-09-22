package com.example.messageexpress;

import java.util.ArrayList;
import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

public class Scheduled extends Fragment {
	private ListView listView;
	private MessagesAdapter adapter;
	ArrayList<MessageDataStruct> messagesDataList;
	DatabaseHandler databaseHandler;
	private final int REQUEST_CODEE = 4;
	int editMessageID = 0;
	int editAlarmID = 0;
	int hour, minute, day, month, year;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View homeScreen = inflater
				.inflate(R.layout.scheduled, container, false);
		databaseHandler = new DatabaseHandler(getActivity());
		messagesDataList = new ArrayList<MessageDataStruct>();
		messagesDataList = databaseHandler.retrievePendingMessages();
		listView = (ListView) homeScreen.findViewById(R.id.list);
		adapter = new MessagesAdapter(getActivity(), messagesDataList);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@SuppressWarnings("deprecation")
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					final int position, long arg3) {
				// TODO Auto-generated method stub
				final int hour = messagesDataList.get(position).getHour();
				final int minute = messagesDataList.get(position).getMinute();
				final int day = messagesDataList.get(position).getDay();
				final int month = messagesDataList.get(position).getMonth();
				final int year = messagesDataList.get(position).getYear();
				final TextView message = (TextView) parent
						.findViewById(R.id.message);
				final TextView repeatOption = (TextView) parent
						.findViewById(R.id.repeatOption);
				final int alarmID = messagesDataList.get(position).getAlarmid();
				final String messageStatus = messagesDataList.get(position)
						.getStatus();
				// Toast.makeText(getActivity(), val,
				// Toast.LENGTH_SHORT).show();
				AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
						.create();
				alertDialog.setTitle("الخيارات");
				alertDialog.setIcon(R.drawable.icon);
				alertDialog.setButton("تعديل",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								Intent delayMsgActivity = new Intent(
										getActivity(), DelayMsgActivity.class);
								editAlarmID = messagesDataList.get(position)
										.getAlarmid();
								editMessageID = messagesDataList.get(position)
										.getMessageid();
								delayMsgActivity.putExtra(Shared.KEY_SIGNATURE,
										messagesDataList.get(position)
												.isSignature());
								delayMsgActivity.putExtra("message",
										message.getText());
								delayMsgActivity.putExtra(Shared.KEY_CNAME,
										messagesDataList.get(position)
												.getCname());
								delayMsgActivity.putExtra(Shared.KEY_TO,
										messagesDataList.get(position).getTo());
								delayMsgActivity
										.putExtra(Shared.KEY_HOUR, hour);

								delayMsgActivity.putExtra(Shared.KEY_MIN,
										minute);
								delayMsgActivity.putExtra(Shared.KEY_DAY, day);
								delayMsgActivity.putExtra(Shared.KEY_MONTH,
										month);
								delayMsgActivity
										.putExtra(Shared.KEY_YEAR, year);
								delayMsgActivity.putExtra(Shared.KEY_REPEAT,
										repeatOption.getText());
								startActivityForResult(delayMsgActivity,
										REQUEST_CODEE);

							}
						});
				alertDialog.setButton2("حذف",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								MessageDataStruct msgDataStruct = new MessageDataStruct();
								ArrayList<String> phoneList = new ArrayList<>();
								phoneList = messagesDataList.get(position)
										.getTo();
								msgDataStruct.setTo(phoneList);
								msgDataStruct.setAlarmid(alarmID);
								msgDataStruct.setStatus(messageStatus);
								databaseHandler.deleteMessage(msgDataStruct);
								Intent intent = new Intent(getActivity(),
										AlarmReciever.class);
								PendingIntent pintent = PendingIntent
										.getBroadcast(
												getActivity(),
												msgDataStruct.getAlarmid(),
												intent,
												PendingIntent.FLAG_UPDATE_CURRENT);
								AlarmManager alarmManager = (AlarmManager) getActivity()
										.getSystemService(Context.ALARM_SERVICE);
								alarmManager.cancel(pintent);
								databaseHandler.deleteAlarm(msgDataStruct
										.getAlarmid());
								messagesDataList = databaseHandler
										.retrievePendingMessages();
								adapter.setListData(messagesDataList);
								adapter.notifyDataSetChanged();
							}
						});
				alertDialog.show();

			}
		});
		return homeScreen;
	}

	@Override
	public void onResume() {
		super.onResume();
		messagesDataList = databaseHandler.retrievePendingMessages();
		adapter.setListData(messagesDataList);
		adapter.notifyDataSetChanged();
		LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
				mMessageReceiver, new IntentFilter("schedule-event"));
		LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
				mFailedMessageReceiver, new IntentFilter("sentfail-event"));
	}

	private BroadcastReceiver mFailedMessageReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			messagesDataList = databaseHandler.retrievePendingMessages();
			adapter.setListData(messagesDataList);
			adapter.notifyDataSetChanged();
	/*		if (intent.hasExtra("messageid")) {
				int messageId = intent.getIntExtra("messageid", 0);
				for (int i = 0; i < messagesDataList.size(); i++) {
					if (messagesDataList.get(i).getMessageid() == messageId) {
						listView.getChildAt(i).setBackgroundColor(Color.RED);
						break;
					}
				}
			}*/
		}

	};
	private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// Extract data included in the Intent
			if (intent.hasExtra("index")) {
				int index = intent.getIntExtra("index", -1);
				if (index != -1) {
					messagesDataList.get(index).setStatus("senting");
					adapter.setListData(messagesDataList);
					adapter.notifyDataSetChanged();
				}
			} else {
				messagesDataList = databaseHandler.retrievePendingMessages();
				adapter.setListData(messagesDataList);
				adapter.notifyDataSetChanged();
			}
		}
	};

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.sentschedule, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_deleteall: {
			databaseHandler.deleteAlarms();
			databaseHandler.deletePendingMessages();
			databaseHandler.deleteNotSentMessages();
			messagesDataList = databaseHandler.retrievePendingMessages();
			adapter.setListData(messagesDataList);
			adapter.notifyDataSetChanged();
			break;
		}
		default:
			break;
		}

		return false;
	}

	@Override
	public void onPause() {
		// Unregister since the activity is not visible
		LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(
				mMessageReceiver);
		super.onPause();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case (REQUEST_CODEE): {
			if (resultCode == Activity.RESULT_OK) {
				for (int i = 0; i < messagesDataList.size(); i++) {
					if ((messagesDataList.get(i).getAlarmid() == editAlarmID)
							&& (messagesDataList.get(i).getMessageid() == editMessageID)) {
						hour = data.getIntExtra(Shared.KEY_HOUR, 0);
						minute = data.getIntExtra(Shared.KEY_MIN, 0);
						day = data.getIntExtra(Shared.KEY_DAY, 0);
						month = data.getIntExtra(Shared.KEY_MONTH, 0);
						year = data.getIntExtra(Shared.KEY_YEAR, 0);
						String textMessage = data
								.getStringExtra(Shared.KEY_MESSAGE);
						ArrayList<String> phoneList = new ArrayList<>();
						ArrayList cNameList = new ArrayList<>();
						phoneList = data.getStringArrayListExtra(Shared.KEY_TO);
						cNameList = data
								.getStringArrayListExtra(Shared.KEY_CNAME);
						boolean isChanged = false;
						boolean isUpdateAlarm = false;
						String repeatOptionss = data
								.getStringExtra(Shared.KEY_REPEAT);
						int signChecked = data.getIntExtra(Shared.KEY_SIGNATURE, 0);
						if(messagesDataList.get(i).isSignature()!=signChecked) {
							isChanged = true;
						}
						if (!repeatOptionss.equals(messagesDataList.get(i)
								.getRepeat())) {
							messagesDataList.get(i).setRepeat(repeatOptionss);
							isChanged = true;
						}
						if ((hour != messagesDataList.get(i).getHour())
								|| (minute != messagesDataList.get(i)
										.getMinute())
								|| (day != messagesDataList.get(i).getDay())
								|| (month != messagesDataList.get(i).getMonth())
								|| (year != messagesDataList.get(i).getYear())) {
							messagesDataList.get(i).setHour(hour);
							messagesDataList.get(i).setMinute(minute);
							messagesDataList.get(i).setMonth(month);
							messagesDataList.get(i).setYear(year);
							messagesDataList.get(i).setDay(day);
							isChanged = true;
							isUpdateAlarm = true;
						}
						if (!messagesDataList.get(i).getMessage()
								.equals(textMessage)) {
							messagesDataList.get(i).setMessage(textMessage);
							isChanged = true;
						}
					
						if (isChanged) {
							databaseHandler.updateUserMessage(messagesDataList
									.get(i));
							// updates the alarm
							if (isUpdateAlarm) {
								updateAlarmInfo(repeatOptionss);
							}
						}
						if (!messagesDataList.get(i).getTo()
								.equals(phoneList)) {
							if(messagesDataList.get(i).getTo().size() < phoneList.size()) {
								for(int k=0; k< phoneList.size(); k++) {
									for(int l=0; l<messagesDataList.get(i).getTo().size(); l++) {
										if(phoneList.get(k).equals(messagesDataList.get(i).getTo().get(l))) {
											break;
										}
										if(l==messagesDataList.get(i).getTo().size()-1) {
											MessageDataStruct msgData = new MessageDataStruct();
											msgData.setAlarmid(messagesDataList.get(i).getAlarmid());
											ArrayList<String> cUpdateNList = new ArrayList<>();
											cUpdateNList.add(cNameList.get(k).toString());
											msgData.setCname(cUpdateNList);
											ArrayList<String> cUpdatePList = new ArrayList<>();
											cUpdatePList.add(phoneList.get(k).toString());
											msgData.setTo(cUpdatePList);
											msgData.setHour(hour);
											msgData.setMinute(minute);
											msgData.setDay(day);
											msgData.setMonth(month);
											msgData.setYear(year);
											msgData.setMessageid(messagesDataList.get(i).getMessageid());
											msgData.setMessage(textMessage);
											msgData.setRepeat(repeatOptionss);
											msgData.setStatus(messagesDataList.get(i).getStatus());
											msgData.setSignature(signChecked);
											databaseHandler.addMessageToDB(msgData);
										}
									}
								}
							}
							
							if(messagesDataList.get(i).getTo().size() > phoneList.size()) {
								for(int k=0; k<messagesDataList.size(); k++) {
									for(int l=0; l<phoneList.size(); l++) {
										if(phoneList.get(l).equals(messagesDataList.get(i).getTo().get(k))) {
											break;
										}
										if(l==phoneList.size()-1) {
											MessageDataStruct msgData = new MessageDataStruct();
											msgData.setAlarmid(messagesDataList.get(i).getAlarmid());
											ArrayList<String> cUpdateNList = new ArrayList<>();
											cUpdateNList.add(messagesDataList.get(i).getCname().get(k));
											msgData.setCname(cUpdateNList);
											ArrayList<String> cUpdatePList = new ArrayList<>();
											cUpdatePList.add(messagesDataList.get(i).getTo().get(k));
											msgData.setTo(cUpdatePList);
											msgData.setStatus(messagesDataList.get(i).getStatus());
											databaseHandler.deleteMessage(msgData);
										}
									}
								}
							}
							messagesDataList.get(i).setCname(cNameList);
							//isChanged = true;
						}
						
						break;
					}
				}
			}
			break;
		}
		}
	}

	public void updateAlarmInfo(String repeatOptions) {
		Intent intent = new Intent(getActivity(), AlarmReciever.class);
		AlarmsInfoDataStruct alarmInfoDataStruct = new AlarmsInfoDataStruct();

		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.DAY_OF_MONTH, day);
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minute);
		alarmInfoDataStruct.setHour(hour);
		alarmInfoDataStruct.setMinute(minute);
		alarmInfoDataStruct.setDay(day);
		alarmInfoDataStruct.setMonth(month);
		alarmInfoDataStruct.setYear(year);
		alarmInfoDataStruct.setId(editAlarmID);
		alarmInfoDataStruct.setRepeatoption(repeatOptions);
		PendingIntent pintent = PendingIntent.getBroadcast(getActivity(),
				editAlarmID, intent, 0);
		AlarmManager alarmManager = (AlarmManager) getActivity()
				.getSystemService(Context.ALARM_SERVICE);

		switch (repeatOptions) {
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
		databaseHandler.updateAlarmInfo(alarmInfoDataStruct);
	}
}
