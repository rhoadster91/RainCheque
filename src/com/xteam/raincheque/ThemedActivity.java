package com.xteam.raincheque;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

public class ThemedActivity extends Activity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		if(sharedPref.getBoolean("black theme", false))
			setTheme(R.style.AppTheme);
		else
			setTheme(R.style.AppThemeLight);
		super.onCreate(savedInstanceState);
	}

}
