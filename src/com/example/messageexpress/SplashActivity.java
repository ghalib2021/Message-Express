package com.example.messageexpress;


import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class SplashActivity extends Activity {
	private static int SPLASH_TIME_OUT = 1000;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_splash);
		 new Handler().postDelayed(new Runnable() {
	    	 
	            /*
	             * Showing splash screen with a timer. This will be useful when you
	             * want to show case your app logo / company
	             */
	 
	            @Override
	            public void run() {
	                // This method will be executed once the timer is over
	                // Start your app main activity
	            	readContactData();
	                Intent i = new Intent(SplashActivity.this, MainActivity.class);
	                startActivity(i);
	 
	                // close this activity
	                finish();
	            }
	        }, SPLASH_TIME_OUT);  
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.splash, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void readContactData() {

		try {

			/*********** Reading Contacts Name And Number **********/

			String phoneNumber = "";
			ContentResolver cr = getBaseContext().getContentResolver();

			// Query to get contact name

			Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null,
					null, null, null);

			// If data data found in contacts
			if (cur.getCount() > 0) {

				Log.i("AutocompleteContacts", "Reading   contacts........");

				int k = 0;
				String name = "";

				while (cur.moveToNext()) {

					String id = cur.getString(cur
							.getColumnIndex(ContactsContract.Contacts._ID));
					name = cur
							.getString(cur
									.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

					// Check contact have phone number
					if (Integer
							.parseInt(cur.getString(cur
									.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {

						// Create query to get phone number by contact id
						Cursor pCur = cr
								.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
										null,
										ContactsContract.CommonDataKinds.Phone.CONTACT_ID
												+ " = ?", new String[] { id },
										null);
						int j = 0;

						while (pCur.moveToNext()) {
							// Sometimes get multiple data
							if (j == 0) {
								// Get Phone number
								phoneNumber = ""
										+ pCur.getString(pCur
												.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

								// Add contacts names to adapter
						//		adapter.add(name);

								// Add ArrayList names to adapter
								ContactsList.phoneValueArr.add(phoneNumber.toString());
								ContactsList.nameValueArr.add(name.toString());

								j++;
								k++;
							}
						} // End while loop
						pCur.close();
					} // End if

				} // End while loop

			} // End Cursor value check
			cur.close();

		} catch (Exception e) {
			Log.i("AutocompleteContacts", "Exception : " + e);
		}

	}
}
