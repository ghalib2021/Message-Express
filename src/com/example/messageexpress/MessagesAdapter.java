package com.example.messageexpress;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MessagesAdapter extends BaseAdapter {
	private Activity activity;
	private ArrayList<MessageDataStruct> messageDataList;
	private LayoutInflater inflater;
	private SimpleDateFormat dateFormatter;
	private SimpleDateFormat timeFormatter;

	public MessagesAdapter(Activity activity,
			ArrayList<MessageDataStruct> messageDataList) {
		// TODO Auto-generated constructor stub
		this.activity = activity;
		this.messageDataList = messageDataList;
		dateFormatter = new SimpleDateFormat("dd-MMM-yyyy");
		timeFormatter = new SimpleDateFormat("hh:mm a");
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return messageDataList.size();
	}

	@Override
	public Object getItem(int pos) {
		// TODO Auto-generated method stub
		return messageDataList.get(pos);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	public void setListData(ArrayList<MessageDataStruct> messagesDataList) {
		this.messageDataList = messagesDataList;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (inflater == null)
			inflater = (LayoutInflater) activity
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (convertView == null)
			convertView = inflater.inflate(R.layout.list_row, null);
		TextView phoneNo = (TextView) convertView.findViewById(R.id.phoneno);
		TextView message = (TextView) convertView.findViewById(R.id.message);
		TextView status = (TextView) convertView.findViewById(R.id.status);
		TextView repeatOption = (TextView) convertView
				.findViewById(R.id.repeatOption);
		TextView msgTime = (TextView) convertView.findViewById(R.id.msgTime);
		TextView msgTime0 = (TextView) convertView.findViewById(R.id.msgTime0);
		MessageDataStruct messageDataStruct = messageDataList.get(position);
		int hour = messageDataStruct.getHour();
		int minute = messageDataStruct.getMinute();
		int day = messageDataStruct.getDay();
		int month = messageDataStruct.getMonth();
		int year = messageDataStruct.getYear();
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.DAY_OF_MONTH, day);
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minute);
		String phoneContactName = "";
		if (messageDataStruct.getTo().size() == 1) {
			if (messageDataStruct.getCname().get(0).equals("null")) {
				phoneContactName = messageDataStruct.getTo().get(0);
			} else {
				phoneContactName = messageDataStruct.getCname().get(0);
			}
		} else {
			
			for (int i = 0; i < messageDataStruct.getTo().size() - 1; i++) {
				if (messageDataStruct.getCname().get(i).equals("null")) {
					phoneContactName = phoneContactName.concat(messageDataStruct.getTo().get(i) + ",");
				} else {
					phoneContactName = phoneContactName.concat(messageDataStruct.getCname().get(i) + ",");
				}
			}
			if (messageDataStruct.getCname()
					.get(messageDataStruct.getCname().size() - 1)
					.equals("null")) {
				phoneContactName = phoneContactName.concat(messageDataStruct.getTo().get(
						messageDataStruct.getTo().size() - 1));
			} else {
				phoneContactName = phoneContactName.concat(messageDataStruct.getCname().get(
						messageDataStruct.getCname().size() - 1));
			}
			
		}
		if(phoneContactName != null) {
			phoneNo.setText(phoneContactName);
		}
		if(messageDataStruct.getStatus().equals("لم ترسل الرسالة")) {
			convertView.setBackgroundColor(Color.parseColor("#E26A6A"));
		} else {
			convertView.setBackgroundColor(Color.WHITE);
		}
		message.setText(messageDataStruct.getMessage());
		status.setText(messageDataStruct.getStatus());
		repeatOption.setText(messageDataStruct.getRepeat());
		msgTime.setText(dateFormatter.format(calendar.getTime()));
		msgTime0.setText(timeFormatter.format(calendar.getTime()));
		return convertView;
	}

}
