package com.dispenser.adapter;

import com.dispenser.DiagnosticMode;
import com.dispenser.Preferences;
import com.dispenser.SetMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class TabsPagerAdapter extends FragmentPagerAdapter {

	public TabsPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int index) {

		switch (index) {
		case 0:
			// Top Rated fragment activity
			return new DiagnosticMode();
		case 1:
			// Games fragment activity
			return new SetMode();
		case 2:
			// Movies fragment activity
			return new Preferences();
		}

		return null;
	}

	@Override
	public int getCount() {
		// get item count - equal to number of tabs
		return 3;
	}

}
