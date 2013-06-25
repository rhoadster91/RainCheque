package com.xteam.raincheque;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

public class SplashActivity extends Activity
{

	Button yes;
	Button no;
	ImageView []indicator = new ImageView[5];	
	SplashPagerAdapter adapter;
	ViewPager viewPager;
	boolean calledByHelp = false;
	boolean isBlackSelected = false;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		String action = getIntent().getAction();		
		if(action!=null & action.contentEquals(RainChequeApplication.ACTION_SHOW_TOUR))
		{			
			super.onCreate(savedInstanceState);
			calledByHelp = true;
			showTour();		
			return;
		}
		else if(!sharedPref.getBoolean("first time", true))
		{
			Intent toMainActivity = new Intent(SplashActivity.this, ControlBoardActivity.class);
			startActivity(toMainActivity);
			finish();
		}		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);		
		yes = (Button)findViewById(R.id.buttonShowTour);
		yes.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				showTour();				
			}
			
		});
		no = (Button)findViewById(R.id.buttonSkipTour);
		no.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				promptToSelectTheme();				
				SharedPreferences.Editor prefEditor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
				prefEditor.putBoolean("first time", false);
				prefEditor.commit();				
			}
			
		});
	}

	private void showTour()
	{
		setContentView(R.layout.tour);
		
		adapter = new SplashPagerAdapter();
		viewPager = (ViewPager)findViewById(R.id.viewPager);
		viewPager.setAdapter(adapter);
		indicator[0] = (ImageView)findViewById(R.id.pageIndicator1);
		indicator[1] = (ImageView)findViewById(R.id.pageIndicator2);
		indicator[2] = (ImageView)findViewById(R.id.pageIndicator3);
		indicator[3] = (ImageView)findViewById(R.id.pageIndicator4);
		indicator[4] = (ImageView)findViewById(R.id.pageIndicator5);
		for(int i=0;i<5;i++)
		{
			final int x = i;
			indicator[i].setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					viewPager.setCurrentItem(x);
				}
				
			});
		}
		viewPager.setOnPageChangeListener(new OnPageChangeListener()
		{

			@Override
			public void onPageScrollStateChanged(int arg0) 
			{
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) 
			{
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onPageSelected(int arg0) 
			{
				int imageResourceOff = getResources().getIdentifier("@drawable/ring_off", null, getPackageName());						
				int imageResourceOn = getResources().getIdentifier("@drawable/ring_on", null, getPackageName());
				Drawable resOn = getResources().getDrawable(imageResourceOn);
				Drawable resOff = getResources().getDrawable(imageResourceOff);
				indicator[arg0].setImageDrawable(resOn);
				for(int i=0;i<5;i++)
				{
					if(i!=arg0)							
						indicator[i].setImageDrawable(resOff);							
				}				
				if(arg0==4)
				{
					Button gotIt = (Button)findViewById(R.id.buttonGotIt);
					gotIt.setVisibility(Button.VISIBLE);
					gotIt.setOnClickListener(new OnClickListener()
					{
						@Override
						public void onClick(View v) 
						{
							if(calledByHelp)
								onBackPressed();
							else
							{
								promptToSelectTheme();
								SharedPreferences.Editor prefEditor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
								prefEditor.putBoolean("first time", false);
								prefEditor.commit();																								
							}
						}
						
					});
				}
			}
		});
	}

	private void promptToSelectTheme()
	{
		final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		if(sharedPref.getBoolean("first time", true))
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(SplashActivity.this);
	    	builder.setTitle(getString(R.string.select_theme));
	    	String []themeList = new String[2];
	    	themeList[0] = getString(R.string.white);
	    	themeList[1] = getString(R.string.dark);
	    	builder.setSingleChoiceItems(themeList, -1, new DialogInterface.OnClickListener()
	    	{
				@Override
				public void onClick(DialogInterface dialog, int which) 
				{
					if(which==0)
						isBlackSelected = false;
					else
						isBlackSelected = true;
					SharedPreferences.Editor prefEditor = sharedPref.edit();    	    	
					prefEditor.putBoolean("black theme", isBlackSelected);
					prefEditor.commit();
					Intent toMainActivity = new Intent(SplashActivity.this, ControlBoardActivity.class);
					startActivity(toMainActivity);	
					finish();
				}    		
	    	});
	    	builder.show();
		}
	}
	
	

}
