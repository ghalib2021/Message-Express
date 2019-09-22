package com.example.messageexpress;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;

public class Sent extends Fragment {
	private ListView listView;
	private MessagesAdapter adapter;
	ArrayList<MessageDataStruct> messagesDataList;
	DatabaseHandler databaseHandler;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View homeScreen = inflater.inflate(R.layout.sent, container, false);
		databaseHandler = new DatabaseHandler(getActivity());
		messagesDataList = new ArrayList<MessageDataStruct>();
		messagesDataList = databaseHandler.retrieveSentMessages();
		listView = (ListView) homeScreen.findViewById(R.id.list);
		adapter = new MessagesAdapter(getActivity(), messagesDataList);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@SuppressWarnings("deprecation")
			@Override
			public void onItemClick(AdapterView<?> parent, View arg1,
					final int position, long arg3) {
				// TODO Auto-generated method stub
				AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
						.create();
				final int alarmID = messagesDataList.get(position).getAlarmid();
				final String messageStatus = messagesDataList.get(position)
						.getStatus();
				alertDialog.setTitle("هل تريد حذف الرسالة ؟");
				alertDialog.setButton("نعم",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								MessageDataStruct msgDataStruct = new MessageDataStruct();
								ArrayList<String> phoneList = new ArrayList<>();
								phoneList = messagesDataList.get(position).getTo();
								msgDataStruct.setTo(phoneList);
								msgDataStruct.setAlarmid(alarmID);
								msgDataStruct.setStatus(messageStatus);
								databaseHandler.deleteMessage(msgDataStruct);
								messagesDataList = databaseHandler
										.retrieveSentMessages();
								adapter.setListData(messagesDataList);
								adapter.notifyDataSetChanged();
							}
						});
				alertDialog.setIcon(R.drawable.icon);
				alertDialog.show();

			}
		});
		return homeScreen;
	}

	@Override
	public void onResume() {
		super.onResume();
		messagesDataList = databaseHandler.retrieveSentMessages();
		adapter.notifyDataSetChanged();
		LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
				mMessageReceiver, new IntentFilter("sent-event"));
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.sentschedule, menu);
		super.onCreateOptionsMenu(menu, inflater);
		messagesDataList = databaseHandler.retrieveSentMessages();
		adapter.setListData(messagesDataList);
		adapter.notifyDataSetChanged();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_deleteall: {
			databaseHandler.deleteSentMessages();
			messagesDataList = databaseHandler.retrieveSentMessages();
			adapter.setListData(messagesDataList);
			adapter.notifyDataSetChanged();
			break;
		}
		default:
			break;
		}

		return false;
	}

	private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// Extract data included in the Intent
			String message = intent.getStringExtra("message");
			Log.d("receiver", "Got message: " + message);
			messagesDataList = databaseHandler.retrieveSentMessages();
			adapter.setListData(messagesDataList);
			adapter.notifyDataSetChanged();
		}
	};

	@Override
	public void onPause() {
		// Unregister since the activity is not visible
		LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(
				mMessageReceiver);
		super.onPause();
	}
}
