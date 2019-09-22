package com.example.messageexpress;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
public class TabPageAdapter extends FragmentStatePagerAdapter {
	public TabPageAdapter(FragmentManager fm) {
		super(fm);
		// TODO Auto-generated constructor stub
	}

	  @Override
	  public Fragment getItem(int i) {
	    switch (i) {
	        case 0:
	            return new Home();
	        case 1:
	            return new Scheduled();
	        case 2: 
	        	return new Sent();
	        }
	    return null;
	  }
	  @Override
	  public int getCount() {
	    // TODO Auto-generated method stub
	    return 3; //No of Tabs
	  }
}
