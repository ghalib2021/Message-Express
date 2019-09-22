package com.example.messageexpress;

import java.util.ArrayList;
import android.util.Log;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.MatrixCursor;
import android.database.SQLException;

public class DatabaseHandler extends SQLiteOpenHelper {
	private static final int DATABASE_VERSION = 1;
	// Database Name
	private static final String DATABASE_NAME = "MessageExpress";
	private static final String KEY_HOUR = "hour";
	private static final String KEY_MIN = "minute";
	private static final String KEY_MONTH = "month";
	private static final String KEY_YEAR = "year";
	private static final String KEY_DAY = "day";
	private static final String KEY_ID = "id";
	private static final String KEY_TO = "phoneno";
	private static final String KEY_MESSAGE = "message";
	private static final String KEY_REPEAT = "repeat";
	private static final String KEY_STATUS = "status";
	private static final String KEY_MSGID = "msgid";
	private static final String TABLE_MESSAGE = "messagedir";
	private static final String TABLE_ALARMS = "alarmstable";

	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		String createMessageDirectoryQuery = "CREATE TABLE " + TABLE_MESSAGE
				+ "(" + KEY_ID + " INTEGER," + KEY_TO + " TEXT," + KEY_MSGID
				+ " INTEGER," + KEY_MESSAGE + " TEXT," + KEY_REPEAT + " TEXT,"
				+ KEY_STATUS + " TEXT," + KEY_HOUR + " INTEGER," + KEY_MIN
				+ " INTEGER," + KEY_YEAR + " INTEGER," + KEY_MONTH
				+ " INTEGER, " + KEY_DAY + " INTEGER," + Shared.KEY_CNAME
				+ " TEXT,"+Shared.KEY_SIGNATURE+" INTEGER);";
		String createAlarmTableQuery = "CREATE TABLE " + TABLE_ALARMS + "("
				+ KEY_ID + " INTEGER, " + KEY_HOUR + " INTEGER," + KEY_MIN
				+ " INTEGER," + KEY_YEAR + " INTEGER," + KEY_MONTH
				+ " INTEGER, " + KEY_DAY + " INTEGER," + KEY_REPEAT + " TEXT);";
		db.execSQL(createMessageDirectoryQuery);
		db.execSQL(createAlarmTableQuery);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_ALARMS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGE);
		onCreate(db);
	}

	// All other operation needed for database operations
	void addAlarmInfo(AlarmsInfoDataStruct alarmInfo) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_ID, alarmInfo.getId());
		values.put(KEY_HOUR, alarmInfo.getHour());
		values.put(KEY_MIN, alarmInfo.getMinute());
		values.put(KEY_YEAR, alarmInfo.getYear());
		values.put(KEY_MONTH, alarmInfo.getMonth());
		values.put(KEY_DAY, alarmInfo.getDay());
		values.put(KEY_REPEAT, alarmInfo.getRepeatoption());
		db.insert(TABLE_ALARMS, null, values);
		db.close();
	}

	void addMessageToDB(MessageDataStruct messageInfo) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		for (int i = 0; i < messageInfo.getTo().size(); i++) {
			values.put(KEY_ID, messageInfo.getAlarmid());
			values.put(KEY_TO, messageInfo.getTo().get(i));
			values.put(KEY_MSGID, messageInfo.getMessageid());
			values.put(KEY_MESSAGE, messageInfo.getMessage());
			values.put(KEY_REPEAT, messageInfo.getRepeat());
			values.put(KEY_STATUS, messageInfo.getStatus());
			values.put(KEY_HOUR, messageInfo.getHour());
			values.put(KEY_MIN, messageInfo.getMinute());
			values.put(KEY_YEAR, messageInfo.getYear());
			values.put(KEY_MONTH, messageInfo.getMonth());
			values.put(KEY_DAY, messageInfo.getDay());
			values.put(Shared.KEY_CNAME, messageInfo.getCname().get(i));
			values.put(Shared.KEY_SIGNATURE, messageInfo.isSignature());
			db.insert(TABLE_MESSAGE, null, values);
		}
		db.close();
	}

	ArrayList<AlarmsInfoDataStruct> getAlarmsInfo() {

		ArrayList<AlarmsInfoDataStruct> alarmInfoModel = new ArrayList<>();
		String selectQuery = "SELECT  * FROM " + TABLE_ALARMS;
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				AlarmsInfoDataStruct alarmInfo = new AlarmsInfoDataStruct();
				alarmInfo.setId(cursor.getInt(0));
				alarmInfo.setHour(cursor.getInt(1));
				alarmInfo.setMinute(cursor.getInt(2));
				alarmInfo.setYear(cursor.getInt(3));
				alarmInfo.setMonth(cursor.getInt(4));
				alarmInfo.setDay(cursor.getInt(5));
				alarmInfo.setRepeatoption(cursor.getString(6));
				alarmInfoModel.add(alarmInfo);
			} while (cursor.moveToNext());
		}
		return alarmInfoModel;
	}

	ArrayList<MessageDataStruct> retrieveSelectedMessagesList(int alarmId) {
		ArrayList<MessageDataStruct> messageList = new ArrayList<>();
		String selectQuery = "SELECT * FROM " + TABLE_MESSAGE + " WHERE "
				+ KEY_ID + "=" + alarmId + " AND " + KEY_STATUS + "='قيد الإرسال'";
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		int messageID = 0;
		ArrayList<String> toList = null;
		ArrayList<String> cNameList = null;
		if (cursor.moveToFirst()) {
			do {
				if (messageID == 0) {
					messageID = cursor.getInt(2);
					MessageDataStruct messageDataStruct = new MessageDataStruct();
					messageDataStruct.setAlarmid(cursor.getInt(0));
					toList = new ArrayList<>();
					cNameList = new ArrayList<>();
					toList.add(cursor.getString(1));
					cNameList.add(cursor.getString(11));
					messageDataStruct.setMessageid(cursor.getInt(2));
					messageDataStruct.setMessage(cursor.getString(3));
					messageDataStruct.setRepeat(cursor.getString(4));
					messageDataStruct.setStatus(cursor.getString(5));
					messageDataStruct.setHour(cursor.getInt(6));
					messageDataStruct.setMinute(cursor.getInt(7));
					messageDataStruct.setYear(cursor.getInt(8));
					messageDataStruct.setMonth(cursor.getInt(9));
					messageDataStruct.setDay(cursor.getInt(10));
					messageDataStruct.setSignature(cursor.getInt(12));
					messageDataStruct.setCname(cNameList);
					messageDataStruct.setTo(toList);
					messageList.add(messageDataStruct);
				} else {
					if (messageID == cursor.getInt(2)) {
						toList.add(cursor.getString(1));
						cNameList.add(cursor.getString(11));
					} else {
						messageID = cursor.getInt(2);
						MessageDataStruct messageDataStruct = new MessageDataStruct();
						messageDataStruct.setAlarmid(cursor.getInt(0));
						toList = new ArrayList<>();
						cNameList = new ArrayList<>();
						toList.add(cursor.getString(1));
						cNameList.add(cursor.getString(11));
						messageDataStruct.setMessageid(cursor.getInt(2));
						messageDataStruct.setMessage(cursor.getString(3));
						messageDataStruct.setRepeat(cursor.getString(4));
						messageDataStruct.setStatus(cursor.getString(5));
						messageDataStruct.setHour(cursor.getInt(6));
						messageDataStruct.setMinute(cursor.getInt(7));
						messageDataStruct.setYear(cursor.getInt(8));
						messageDataStruct.setMonth(cursor.getInt(9));
						messageDataStruct.setDay(cursor.getInt(10));
						messageDataStruct.setSignature(cursor.getInt(12));
						messageDataStruct.setCname(cNameList);
						messageDataStruct.setTo(toList);
						messageList.add(messageDataStruct);
					}

				}

			} while (cursor.moveToNext());
		}
		return messageList;
	}

	public ArrayList<MessageDataStruct> retrieveSentMessages() {
		ArrayList<MessageDataStruct> messageList = new ArrayList<>();
		String selectQuery = "SELECT * FROM " + TABLE_MESSAGE + " WHERE "
				+ KEY_STATUS + "='تم ارسال الرسالة'";
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		int messageID = 0;
		ArrayList<String> toList = null;
		ArrayList<String> cNameList = null;
		if (cursor.moveToFirst()) {
			do {
				if (messageID == 0) {
					messageID = cursor.getInt(2);
					MessageDataStruct messageDataStruct = new MessageDataStruct();
					messageDataStruct.setAlarmid(cursor.getInt(0));
					toList = new ArrayList<>();
					cNameList = new ArrayList<>();
					toList.add(cursor.getString(1));
					cNameList.add(cursor.getString(11));
					messageDataStruct.setMessageid(cursor.getInt(2));
					messageDataStruct.setMessage(cursor.getString(3));
					messageDataStruct.setRepeat(cursor.getString(4));
					messageDataStruct.setStatus(cursor.getString(5));
					messageDataStruct.setHour(cursor.getInt(6));
					messageDataStruct.setMinute(cursor.getInt(7));
					messageDataStruct.setYear(cursor.getInt(8));
					messageDataStruct.setMonth(cursor.getInt(9));
					messageDataStruct.setDay(cursor.getInt(10));
					messageDataStruct.setSignature(cursor.getInt(12));
					messageDataStruct.setTo(toList);
					messageDataStruct.setCname(cNameList);
					messageList.add(messageDataStruct);
				} else {
					if (messageID == cursor.getInt(2)) {
						toList.add(cursor.getString(1));
						cNameList.add(cursor.getString(11));
					} else {
						messageID = cursor.getInt(2);
						MessageDataStruct messageDataStruct = new MessageDataStruct();
						messageDataStruct.setAlarmid(cursor.getInt(0));
						toList = new ArrayList<>();
						cNameList = new ArrayList<>();
						toList.add(cursor.getString(1));
						cNameList.add(cursor.getString(11));
						messageDataStruct.setMessageid(cursor.getInt(2));
						messageDataStruct.setMessage(cursor.getString(3));
						messageDataStruct.setRepeat(cursor.getString(4));
						messageDataStruct.setStatus(cursor.getString(5));
						messageDataStruct.setHour(cursor.getInt(6));
						messageDataStruct.setMinute(cursor.getInt(7));
						messageDataStruct.setYear(cursor.getInt(8));
						messageDataStruct.setMonth(cursor.getInt(9));
						messageDataStruct.setDay(cursor.getInt(10));
						messageDataStruct.setSignature(cursor.getInt(12));
						messageDataStruct.setTo(toList);
						messageDataStruct.setCname(cNameList);
						messageList.add(messageDataStruct);
					}

				}

			} while (cursor.moveToNext());
		}
		return messageList;
	}

	public ArrayList<MessageDataStruct> retrievePendingMessages() {
		ArrayList<MessageDataStruct> messageList = new ArrayList<>();
		String selectQuery = "SELECT * FROM " + TABLE_MESSAGE + " WHERE "
				+ KEY_STATUS + "='قيد الإرسال' OR "+KEY_STATUS+"='لم ترسل الرسالة'";
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		int messageID = 0;
		ArrayList<String> toList = null;
		ArrayList<String> cNameList = null;
		if (cursor.moveToFirst()) {
			do {
				if (messageID == 0) {
					messageID = cursor.getInt(2);
					MessageDataStruct messageDataStruct = new MessageDataStruct();
					messageDataStruct.setAlarmid(cursor.getInt(0));
					toList = new ArrayList<>();
					cNameList = new ArrayList<>();
					toList.add(cursor.getString(1));
					cNameList.add(cursor.getString(11));
					messageDataStruct.setMessageid(cursor.getInt(2));
					messageDataStruct.setMessage(cursor.getString(3));
					messageDataStruct.setRepeat(cursor.getString(4));
					messageDataStruct.setStatus(cursor.getString(5));
					messageDataStruct.setHour(cursor.getInt(6));
					messageDataStruct.setMinute(cursor.getInt(7));
					messageDataStruct.setYear(cursor.getInt(8));
					messageDataStruct.setMonth(cursor.getInt(9));
					messageDataStruct.setDay(cursor.getInt(10));
					messageDataStruct.setSignature(cursor.getInt(12));
					messageDataStruct.setTo(toList);
					messageDataStruct.setCname(cNameList);
					messageList.add(messageDataStruct);
				} else {
					if (messageID == cursor.getInt(2)) {
						toList.add(cursor.getString(1));
						cNameList.add(cursor.getString(11));
					} else {
						messageID = cursor.getInt(2);
						MessageDataStruct messageDataStruct = new MessageDataStruct();
						messageDataStruct.setAlarmid(cursor.getInt(0));
						toList = new ArrayList<>();
						cNameList = new ArrayList<>();
						cNameList.add(cursor.getString(11));
						toList.add(cursor.getString(1));
						messageDataStruct.setMessageid(cursor.getInt(2));
						messageDataStruct.setMessage(cursor.getString(3));
						messageDataStruct.setRepeat(cursor.getString(4));
						messageDataStruct.setStatus(cursor.getString(5));
						messageDataStruct.setHour(cursor.getInt(6));
						messageDataStruct.setMinute(cursor.getInt(7));
						messageDataStruct.setYear(cursor.getInt(8));
						messageDataStruct.setMonth(cursor.getInt(9));
						messageDataStruct.setDay(cursor.getInt(10));
						messageDataStruct.setSignature(cursor.getInt(12));
						messageDataStruct.setTo(toList);
						messageDataStruct.setCname(cNameList);
						messageList.add(messageDataStruct);
					}

				}

			} while (cursor.moveToNext());
		}
		return messageList;
	}

	ArrayList<MessageDataStruct> retrieveMessageList() {
		ArrayList<MessageDataStruct> messageList = new ArrayList<>();
		String selectQuery = "SELECT  * FROM " + TABLE_MESSAGE;
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		int messageID = 0;
		ArrayList<String> toList = null;
		ArrayList<String> cNameList = null;
		if (cursor.moveToFirst()) {
			do {
				if (messageID == 0) {
					messageID = cursor.getInt(2);
					MessageDataStruct messageDataStruct = new MessageDataStruct();
					messageDataStruct.setAlarmid(cursor.getInt(0));
					toList = new ArrayList<>();
					cNameList = new ArrayList<>();
					cNameList.add(cursor.getString(11));
					toList.add(cursor.getString(1));
					messageDataStruct.setMessageid(cursor.getInt(2));
					messageDataStruct.setMessage(cursor.getString(3));
					messageDataStruct.setRepeat(cursor.getString(4));
					messageDataStruct.setStatus(cursor.getString(5));
					messageDataStruct.setSignature(cursor.getInt(12));
					messageDataStruct.setTo(toList);
					messageList.add(messageDataStruct);
				} else {
					if (messageID == cursor.getInt(2)) {
						toList.add(cursor.getString(1));
						cNameList.add(cursor.getString(11));
					} else {
						messageID = cursor.getInt(2);
						MessageDataStruct messageDataStruct = new MessageDataStruct();
						messageDataStruct.setAlarmid(cursor.getInt(0));
						toList = new ArrayList<>();
						cNameList = new ArrayList<>();
						toList.add(cursor.getString(1));
						cNameList.add(cursor.getString(11));
						messageDataStruct.setMessageid(cursor.getInt(2));
						messageDataStruct.setMessage(cursor.getString(3));
						messageDataStruct.setRepeat(cursor.getString(4));
						messageDataStruct.setStatus(cursor.getString(5));
						messageDataStruct.setSignature(cursor.getInt(12));
						messageDataStruct.setTo(toList);
						messageList.add(messageDataStruct);
					}

				}

			} while (cursor.moveToNext());
		}
		return messageList;
	}
   public void updateAlarmInfo(AlarmsInfoDataStruct alarmInfo) {
	   SQLiteDatabase db = this.getWritableDatabase();
	   ContentValues values = new ContentValues();
	   values.put(Shared.KEY_HOUR, alarmInfo.getHour());
	   values.put(Shared.KEY_MIN, alarmInfo.getMinute());
	   values.put(Shared.KEY_DAY, alarmInfo.getDay());
	   values.put(Shared.KEY_MONTH, alarmInfo.getMonth());
	   values.put(Shared.KEY_YEAR, alarmInfo.getYear());
	   values.put(Shared.KEY_REPEAT, alarmInfo.getRepeatoption());
	   values.put(KEY_ID,alarmInfo.getId());
	   db.update(TABLE_ALARMS, values, KEY_ID+"=?", new String[] {String.valueOf(alarmInfo.getId())});
	   db.close();
	   
   }
	public void updateMessageStatus(MessageDataStruct msgDataStruct) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		for (int i = 0; i < msgDataStruct.getTo().size(); i++) {
			values.put(KEY_STATUS, "تم ارسال الرسالة");
			db.update(
					TABLE_MESSAGE,
					values,
					KEY_ID + " = ? AND " + KEY_TO + "=? AND " + KEY_HOUR
							+ "=? AND " + KEY_MIN + "=? AND " + KEY_DAY
							+ "=? AND " + KEY_MONTH + "=? AND " + KEY_YEAR
							+ "=?",
					new String[] { String.valueOf(msgDataStruct.getAlarmid()),
							msgDataStruct.getTo().get(i),
							String.valueOf(msgDataStruct.getHour()),
							String.valueOf(msgDataStruct.getMinute()),
							String.valueOf(msgDataStruct.getDay()),
							String.valueOf(msgDataStruct.getMonth()),
							String.valueOf(msgDataStruct.getYear()) });
		}
		db.close();
	}

	public void updateNotSentMessageStatus(MessageDataStruct msgDataStruct) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		for (int i = 0; i < msgDataStruct.getTo().size(); i++) {
			values.put(KEY_STATUS, "لم ترسل الرسالة");
			db.update(
					TABLE_MESSAGE,
					values,
					KEY_ID + " = ? AND " + KEY_TO + "=? AND " + KEY_HOUR
							+ "=? AND " + KEY_MIN + "=? AND " + KEY_DAY
							+ "=? AND " + KEY_MONTH + "=? AND " + KEY_YEAR
							+ "=?",
					new String[] { String.valueOf(msgDataStruct.getAlarmid()),
							msgDataStruct.getTo().get(i),
							String.valueOf(msgDataStruct.getHour()),
							String.valueOf(msgDataStruct.getMinute()),
							String.valueOf(msgDataStruct.getDay()),
							String.valueOf(msgDataStruct.getMonth()),
							String.valueOf(msgDataStruct.getYear()) });
		}
		db.close();
	}

	public void updateUserMessage(MessageDataStruct messageInfo) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_ID, messageInfo.getAlarmid());
		values.put(KEY_MSGID, messageInfo.getMessageid());
		values.put(KEY_MESSAGE, messageInfo.getMessage());
		values.put(KEY_REPEAT, messageInfo.getRepeat());
		values.put(KEY_STATUS, messageInfo.getStatus());
		values.put(KEY_HOUR, messageInfo.getHour());
		values.put(KEY_MIN, messageInfo.getMinute());
		values.put(KEY_YEAR, messageInfo.getYear());
		values.put(KEY_MONTH, messageInfo.getMonth());
		values.put(KEY_DAY, messageInfo.getDay());
		values.put(Shared.KEY_SIGNATURE, messageInfo.isSignature());
		for (int i = 0; i < messageInfo.getTo().size(); i++) {
			values.put(KEY_TO, messageInfo.getTo().get(i));		
			values.put(Shared.KEY_CNAME, messageInfo.getCname().get(i));
			db.update(TABLE_MESSAGE, values, KEY_ID + " = ? AND " + KEY_TO + "=? AND "+KEY_MSGID+"=?", new String[]{String.valueOf(messageInfo.getAlarmid()),String.valueOf(messageInfo.getTo().get(i)),String.valueOf(messageInfo.getMessageid())});
		}
		db.close();
	}

	public void deleteMessage(MessageDataStruct msgDataStruct) {
		SQLiteDatabase db = this.getWritableDatabase();

		for (int i = 0; i < msgDataStruct.getTo().size(); i++) {
			db.delete(
					TABLE_MESSAGE,
					KEY_TO + " = ? AND " + KEY_ID + " = ? AND " + KEY_STATUS
							+ "=?",
					new String[] { msgDataStruct.getTo().get(i),
							String.valueOf(msgDataStruct.getAlarmid()),
							msgDataStruct.getStatus() });
		}
		db.close();
	}

	public void deleteAlarm(int id) {
		SQLiteDatabase db = this.getWritableDatabase();
		String[] alarmArray = new String[] { String.valueOf(id) };
		db.delete(TABLE_ALARMS, KEY_ID + "=?", alarmArray);
		db.close();
	}

	public void deletePendingMessages() {
		SQLiteDatabase db = this.getWritableDatabase();
		db.execSQL("delete from " + TABLE_MESSAGE + " Where " + KEY_STATUS
				+ "='قيد الإرسال'");
		db.close();
	}
	public void deleteNotSentMessages() {
		SQLiteDatabase db = this.getWritableDatabase();
		db.execSQL("delete from " + TABLE_MESSAGE + " Where " + KEY_STATUS
				+ "='لم ترسل الرسالة'");
		db.close();
	}

	public void deleteSentMessages() {
		SQLiteDatabase db = this.getWritableDatabase();
		db.execSQL("delete from " + TABLE_MESSAGE + " Where " + KEY_STATUS
				+ "='تم ارسال الرسالة'");
		db.close();
	}

	public void deleteAlarms() {
		SQLiteDatabase db = this.getWritableDatabase();
		db.execSQL("delete from " + TABLE_ALARMS);
		db.close();
	}

	public ArrayList<Cursor> getData(String Query) {
		// get writable database
		SQLiteDatabase sqlDB = this.getWritableDatabase();
		String[] columns = new String[] { "mesage" };
		// an array list of cursor to save two cursors one has results from the
		// query
		// other cursor stores error message if any errors are triggered
		ArrayList<Cursor> alc = new ArrayList<Cursor>(2);
		MatrixCursor Cursor2 = new MatrixCursor(columns);
		alc.add(null);
		alc.add(null);

		try {
			String maxQuery = Query;
			// execute the query results will be save in Cursor c
			Cursor c = sqlDB.rawQuery(maxQuery, null);

			// add value to cursor2
			Cursor2.addRow(new Object[] { "Success" });

			alc.set(1, Cursor2);
			if (null != c && c.getCount() > 0) {

				alc.set(0, c);
				c.moveToFirst();

				return alc;
			}
			return alc;
		} catch (SQLException sqlEx) {
			Log.d("printing exception", sqlEx.getMessage());
			// if any exceptions are triggered save the error message to cursor
			// an return the arraylist
			Cursor2.addRow(new Object[] { "" + sqlEx.getMessage() });
			alc.set(1, Cursor2);
			return alc;
		} catch (Exception ex) {

			Log.d("printing exception", ex.getMessage());

			// if any exceptions are triggered save the error message to cursor
			// an return the arraylist
			Cursor2.addRow(new Object[] { "" + ex.getMessage() });
			alc.set(1, Cursor2);
			return alc;
		}

	}

}
