package com.example.messageexpress;

import java.util.ArrayList;

public class MessageDataStruct {
	private int alarmid;
	private int messageid;
	private ArrayList<String> to;
	private ArrayList<String> cname;
	private String message;
	private String repeat;
	private String status;
	private int hour;
	private int signature;
	private int minute;
	private int day;
	private int month;
	private int year;

	public int isSignature() {
		return signature;
	}

	public void setSignature(int i) {
		this.signature = i;
	}
	
	public ArrayList<String> getCname() {
		return cname;
	}

	public void setCname(ArrayList<String> cname) {
		this.cname = cname;
	}

	public int getHour() {
		return hour;
	}

	public void setHour(int hour) {
		this.hour = hour;
	}

	public int getMinute() {
		return minute;
	}

	public void setMinute(int minute) {
		this.minute = minute;
	}

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public int getMessageid() {
		return messageid;
	}

	public void setMessageid(int messageid) {
		this.messageid = messageid;
	}

	public int getAlarmid() {
		return alarmid;
	}

	public void setAlarmid(int alarmid) {
		this.alarmid = alarmid;
	}

	public ArrayList<String> getTo() {
		return to;
	}

	public void setTo(ArrayList<String> to) {
		this.to = to;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getRepeat() {
		return repeat;
	}

	public void setRepeat(String repeat) {
		this.repeat = repeat;
	}

}
