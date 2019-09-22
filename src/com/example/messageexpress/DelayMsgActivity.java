package com.example.messageexpress;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class DelayMsgActivity extends ActionBarActivity implements
		OnClickListener, OnItemSelectedListener, OnItemClickListener {
	private EditText datePickerEText;
	private EditText timePickerEText;
	private DatePickerDialog datePickerDialog;
	private TimePickerDialog timePickerDialog;
	private SimpleDateFormat dateFormatter;
//	private Spinner repeatOption;
	private EditText eTextMessage;
	private CheckBox signatureCheckBox;
	private MultiAutoCompleteTextView ephoneNo;
	String repeatOptionss;
	private ImageButton delayMessage;
	String message, phoneNo;
	private int hour;
	private int minute;
	private int month;
	private int year;
	private int day;
	final int SETTING_CODE = 3;
	PhoneNumberFormattingTextWatcher mPhoneWatcher = new PhoneNumberFormattingTextWatcher();
//	public static ArrayList<String> phoneValueArr = new ArrayList<String>();
//	public static ArrayList<String> nameValueArr = new ArrayList<String>();
	ArrayList<String> phoneList;
	ArrayList<String> contactNames;
	String toNumberValue = "";
	private ArrayAdapter<String> adapter;
	SimpleDateFormat timeFormatter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_delay_msg);
		phoneList = new ArrayList<>();
		contactNames = new ArrayList<>();
		dateFormatter = new SimpleDateFormat("dd-MMM-yyyy");
	//	repeatOption = (Spinner) findViewById(R.id.repeatOption);
		signatureCheckBox = (CheckBox)findViewById(R.id.signatureCheckBox);
		ephoneNo = (MultiAutoCompleteTextView) findViewById(R.id.phoneNo);
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_dropdown_item_1line,
				new ArrayList<String>());
		ephoneNo.setThreshold(2);
		ephoneNo.setAdapter(adapter);
		ephoneNo.setOnItemSelectedListener(this);
		ephoneNo.setOnItemClickListener(this);
		timeFormatter = new SimpleDateFormat("hh:mm a");
		ephoneNo.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
		if ((ContactsList.phoneValueArr.size() == 0) || (ContactsList.nameValueArr.size() == 0)) {
			adapter.addAll(ContactsList.nameValueArr);
		} else {
			adapter.addAll(ContactsList.nameValueArr);
		}
		eTextMessage = (EditText) findViewById(R.id.textMessage);
		findViewsById();
		setDateTimeField();
		// ephoneNo.addTextChangedListener(mPhoneWatcher);
		Intent intent = getIntent();
		if (intent.hasExtra("message")) {
			eTextMessage.setText(intent.getStringExtra("message"));
		}
		if (intent.hasExtra(Shared.KEY_TO)) {
			// ephoneNo.setText(intent.getStringExtra(Shared.KEY_TO));
			phoneList = intent.getStringArrayListExtra(Shared.KEY_TO);

		}
		if (intent.hasExtra(Shared.KEY_CNAME)) {
			contactNames = intent.getStringArrayListExtra(Shared.KEY_CNAME);
			if (contactNames.size() != 0) {
				String usercontacts = "";
				if((contactNames.size() == 1) && (!contactNames.get(0).equals("null"))) {
					usercontacts = usercontacts.concat(contactNames.get(0));
				} else {
				for (int i = 0; i < contactNames.size(); i++) {
					if (!contactNames.get(i).equals("null")) {
						usercontacts = usercontacts.concat(contactNames.get(i));
						usercontacts = usercontacts.concat(",");
					}
				}
				}
				ephoneNo.setText(usercontacts);
			}
		}
		if (intent.hasExtra(Shared.KEY_REPEAT)) {

		}
		if(intent.hasExtra(Shared.KEY_SIGNATURE)) {
			int checked = intent.getIntExtra(Shared.KEY_SIGNATURE, 0);
			if(checked == 1) {
				signatureCheckBox.setChecked(true);
			} else {
				signatureCheckBox.setChecked(false);
			}
		}
		delayMessage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				if (ephoneNo.getText().toString().trim() == null){
					Toast.makeText(DelayMsgActivity.this, "يرجى كتابة رقم او اسم المرسل", Toast.LENGTH_SHORT).show();
					return;
				}else{
				
				// TODO Auto-generated method stub
				
				Calendar calTime = Calendar.getInstance();
				long currentTime = calTime.getTime().getTime();
				calTime.set(year, month, day, hour, minute);
				long setTime = calTime.getTime().getTime();
				long duration = setTime - currentTime;
				if (duration > 0) {
					long d = 0, h = 0, m = 0;
					d = TimeUnit.MILLISECONDS.toDays(duration);
					if (d > 0) {
						long totalDays = TimeUnit.MILLISECONDS.convert(
								d, TimeUnit.DAYS);
						duration = duration - totalDays;
					}
					if (duration != 0 && duration > 0) {
						h = TimeUnit.MILLISECONDS.toHours(duration);
						long totalHours = TimeUnit.MILLISECONDS
								.convert(h, TimeUnit.HOURS);
						duration = duration - totalHours;
					}
					if (duration != 0 && duration > 0) {
						m = TimeUnit.MILLISECONDS.toMinutes(duration);

					}
					String message = "سيتم ارسال الرساله بعد " + d  + " يوم و" + h + "ساعه و " + m + "  دقيقة " ;
					Toast.makeText(getApplicationContext(), message,
							Toast.LENGTH_SHORT).show();

				} else {
					Toast.makeText(getApplicationContext(),
							"سيتم ارسال الرسالة الآن",
							Toast.LENGTH_SHORT).show();
				}

				
				/////////////////////////////////////////
				message = eTextMessage.getText().toString();
				repeatOptionss = " ";// repeatOption.getSelectedItem().toString();
				if (phoneList.size() == 0) {
					phoneNo = ephoneNo.getText().toString();
					phoneList.add(phoneNo);
					contactNames.add("null");
				}
				Intent msgDataIntent = new Intent();
				msgDataIntent.putExtra(Shared.KEY_HOUR, hour);
				msgDataIntent.putExtra(Shared.KEY_MIN, minute);
				msgDataIntent.putExtra(Shared.KEY_YEAR, year);
				msgDataIntent.putExtra(Shared.KEY_MONTH, month);
				msgDataIntent.putExtra(Shared.KEY_DAY, day);
				msgDataIntent.putExtra(Shared.KEY_REPEAT, repeatOptionss);
				msgDataIntent.putExtra(Shared.KEY_MESSAGE, message);
				msgDataIntent.putStringArrayListExtra(Shared.KEY_TO, phoneList);
				msgDataIntent.putStringArrayListExtra(Shared.KEY_CNAME,
						contactNames);
				signatureCheckBox.isChecked();
				int issigned = (signatureCheckBox.isChecked()) ? 1:0;
				msgDataIntent.putExtra(Shared.KEY_SIGNATURE,issigned);
				setResult(RESULT_OK, msgDataIntent);
				finish();
			}
				
			}
		});
	}

	private void findViewsById() {
		datePickerEText = (EditText) findViewById(R.id.dfield);
		datePickerEText.setInputType(InputType.TYPE_NULL);
		datePickerEText.requestFocus();
		timePickerEText = (EditText) findViewById(R.id.tfield);
		timePickerEText.setInputType(InputType.TYPE_NULL);
		delayMessage = (ImageButton) findViewById(R.id.delayButton);

	}

	private void setDateTimeField() {
		datePickerEText.setOnClickListener(this);
		timePickerEText.setOnClickListener(this);
		Intent intent = getIntent();
		final Calendar newCalendar = Calendar.getInstance();
		if (intent.hasExtra(Shared.KEY_DAY)
				&& intent.hasExtra(Shared.KEY_MONTH)
				&& intent.hasExtra(Shared.KEY_YEAR)
				&& intent.hasExtra(Shared.KEY_MIN)
				&& intent.hasExtra(Shared.KEY_HOUR)) {
			year = intent.getIntExtra(Shared.KEY_YEAR, 0);
			month = intent.getIntExtra(Shared.KEY_MONTH, 0);
			day = intent.getIntExtra(Shared.KEY_DAY, 0);
			hour = intent.getIntExtra(Shared.KEY_HOUR, 0);
			minute = intent.getIntExtra(Shared.KEY_MIN, 0);
			newCalendar.set(Calendar.YEAR, year);
			newCalendar.set(Calendar.MONTH, month);
			newCalendar.set(Calendar.DAY_OF_MONTH, day);
			newCalendar.set(Calendar.HOUR_OF_DAY, hour);
			newCalendar.set(Calendar.MINUTE, minute);
		} else {
			year = newCalendar.get(Calendar.YEAR);
			month = newCalendar.get(Calendar.MONTH);
			day = newCalendar.get(Calendar.DAY_OF_MONTH);
			hour = newCalendar.get(Calendar.HOUR_OF_DAY);
			minute = newCalendar.get(Calendar.MINUTE);
		}
		datePickerEText.setText(dateFormatter.format(newCalendar.getTime()));
		datePickerDialog = new DatePickerDialog(this, 0,
				new OnDateSetListener() {

					@Override
					public void onDateSet(DatePicker view, int yearr,
							int monthOfYear, int dayOfMonth) {
						// TODO Auto-generated method stub
						year = yearr;
						month = monthOfYear;
						day = dayOfMonth;
						Calendar calTime = Calendar.getInstance();
						long currentTime = calTime.getTime().getTime();
						calTime.set(year, month, day, hour, minute);
						long setTime = calTime.getTime().getTime();
						long duration = setTime - currentTime;
						if (duration > 0) {
							long d = 0, h = 0, m = 0;
							d = TimeUnit.MILLISECONDS.toDays(duration);
							if (d > 0) {
								long totalDays = TimeUnit.MILLISECONDS.convert(
										d, TimeUnit.DAYS);
								duration = duration - totalDays;
							}
							if (duration != 0 && duration > 0) {
								h = TimeUnit.MILLISECONDS.toHours(duration);
								long totalHours = TimeUnit.MILLISECONDS
										.convert(h, TimeUnit.HOURS);
								duration = duration - totalHours;
							}
							if (duration != 0 && duration > 0) {
								m = TimeUnit.MILLISECONDS.toMinutes(duration);

							}
							//String message = "سيتم ارسال الرساله بعد " + d  + " يوم و" + h + "ساعه و " + m + "  دقيقة " ;
						/*	Toast.makeText(getApplicationContext(), message,
									Toast.LENGTH_SHORT).show();*/

						} else {
						/*	Toast.makeText(getApplicationContext(),
									"Sending The Message Right Away",
									Toast.LENGTH_SHORT).show();*/
						}
						datePickerEText.setText(dateFormatter.format(calTime
								.getTime()));
					}

				}, year, month, day);

		timePickerEText.setText(timeFormatter.format(newCalendar.getTime()));
		timePickerDialog = new TimePickerDialog(this,
				new TimePickerDialog.OnTimeSetListener() {

					@Override
					public void onTimeSet(TimePicker view, int hourOfDay,
							int minuteee) {

						// TODO Auto-generated method stub
						hour = hourOfDay;
						minute = minuteee;
						Calendar calTime = Calendar.getInstance();
						long currentTime = calTime.getTime().getTime();
						calTime.set(year, month, day, hourOfDay, minuteee);
						long setTime = calTime.getTime().getTime();
						long duration = setTime - currentTime;
						if (duration > 0) {
							long d = 0, h = 0, m = 0;
							d = TimeUnit.MILLISECONDS.toDays(duration);
							if (d > 0) {
								long totalDays = TimeUnit.MILLISECONDS.convert(
										d, TimeUnit.DAYS);
								duration = duration - totalDays;
							}
							if (duration != 0 && duration > 0) {
								h = TimeUnit.MILLISECONDS.toHours(duration);
								long totalHours = TimeUnit.MILLISECONDS
										.convert(h, TimeUnit.HOURS);
								duration = duration - totalHours;
							}
							if (duration != 0 && duration > 0) {
								m = TimeUnit.MILLISECONDS.toMinutes(duration);

							}
							//String message = "سيتم ارسال الرساله بعد " + d  + " يوم و" + h + "ساعه و " + m + "  دقيقة " ;
							/*Toast.makeText(getApplicationContext(), message,
									Toast.LENGTH_SHORT).show();*/

						} else {
						/*	Toast.makeText(getApplicationContext(),
									"Sending The Message Right Away",
									Toast.LENGTH_SHORT).show();*/
						}
						timePickerEText.setText(timeFormatter.format(calTime
								.getTime()));
					}
				}, hour, minute, false);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.delay_msg, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			Intent i = new Intent(this, UserSettingActivity.class);
			startActivityForResult(i, SETTING_CODE);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == datePickerEText) {
			datePickerDialog.show();
		} else if (v == timePickerEText) {
			timePickerDialog.show();
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		int i = ContactsList.nameValueArr.indexOf("" + arg0.getItemAtPosition(arg2));

		// If name exist in name ArrayList
		if (i >= 0) {

			// Get Phone Number
			toNumberValue = ContactsList.phoneValueArr.get(i);
			toNumberValue = toNumberValue.replaceAll("\\s", "");
			toNumberValue = removeChar(toNumberValue, '-');
			phoneList.add(toNumberValue);
			contactNames.add(ContactsList.nameValueArr.get(i));
			InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
			/*
			 * Toast.makeText( getBaseContext(), "Position:" + arg2 + " Name:" +
			 * arg0.getItemAtPosition(arg2) + " Number:" + toNumberValue,
			 * Toast.LENGTH_LONG).show();
			 * 
			 * Log.d("AutocompleteContacts", "Position:" + arg2 + " Name:" +
			 * arg0.getItemAtPosition(arg2) + " Number:" + toNumberValue);
			 */
		}
	}

	// Read phone contact name and phone numbers

	

	private static String removeChar(String s, char c) {
		StringBuffer buf = new StringBuffer(s.length());
		buf.setLength(s.length());
		int current = 0;
		for (int i = 0; i < s.length(); i++) {
			char cur = s.charAt(i);
			if (cur != c)
				buf.setCharAt(current++, cur);
		}
		return buf.toString();
	}

}
