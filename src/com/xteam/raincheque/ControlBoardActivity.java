package com.xteam.raincheque;

import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.widget.SlidingPaneLayout;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;


public class ControlBoardActivity extends Activity 
{
	ListView activeSessionList = null;
	ListView inactiveSessionList = null;
	SessionListAdapter activeSessionListAdapter = null;
	SessionListAdapter inactiveSessionListAdapter = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.control_board);
		RainChequeApplication.readAccountsFromFile(getApplicationContext());		
		refreshList();
		SlidingPaneLayout slidingPane = (SlidingPaneLayout)findViewById(R.id.sliding_pane_layout);
		slidingPane.setSliderFadeColor(Color.BLACK);
		Button bCreateSession = (Button)findViewById(R.id.button1);
		bCreateSession.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v) 
			{
				AlertDialog.Builder builder = new AlertDialog.Builder(ControlBoardActivity.this);
	        	builder.setTitle(getString(R.string.new_session_title));
	        	LinearLayout lila = new LinearLayout(getApplicationContext());
	        	lila.setOrientation(LinearLayout.VERTICAL);
	        	TextView tv = new TextView(ControlBoardActivity.this);
	        	tv.setText(getString(R.string.session_name));
	        	tv.setTextAppearance(ControlBoardActivity.this, android.R.style.TextAppearance_Large);
	        	tv.setPadding(30, 30, 30, 30);	        	
	        	lila.addView(tv);        	
	        	final EditText input = new EditText(ControlBoardActivity.this);	        	
	        	input.setInputType(InputType.TYPE_CLASS_TEXT);
	        	input.setPadding(30, 30, 30, 30);
	        	lila.addView(input);
	        	builder.setView(lila);
	        	builder.setPositiveButton(getString(R.string.new_session), new DialogInterface.OnClickListener() 
	        	{ 
	        	    public void onClick(DialogInterface dialog, int which) 
	        	    {
	        	    	SessionRecord s = new SessionRecord();
	        	    	s.label = input.getText().toString();
	        	    	if(RainChequeApplication.sessionList.size()>0)	        	    	
	        	    		s.sessionID = RainChequeApplication.sessionList.get(RainChequeApplication.sessionList.size() - 1).sessionID + 1;	        	    	
	        	    	else	        	    	
	        	    		s.sessionID = 1;
	        	    	s.accountList = new ArrayList<AccountRecord>();
	        	    	RainChequeApplication.currentSession = s;
	        	    	Intent startListOfParticipantsActivity = new Intent(ControlBoardActivity.this, ListOfParticipantsActivity.class);
	        	    	startActivity(startListOfParticipantsActivity);
	        	    }
	        	});
	        	builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() 
	        	{
	        	    public void onClick(DialogInterface dialog, int which) 
	        	    {
	        	        dialog.cancel();
	        	    }
	        	});

	        	builder.show();
			}
			
		});
	}
	
	private void refreshList()
	{
		activeSessionList = (ListView)findViewById(R.id.listView1);
		inactiveSessionList = (ListView)findViewById(R.id.listView2);
		
		ArrayList<SessionRecord> activeSessions = new ArrayList<SessionRecord>();
		ArrayList<SessionRecord> inactiveSessions = new ArrayList<SessionRecord>();
		for(SessionRecord s:RainChequeApplication.sessionList)
		{
			if(s.isActive)
				activeSessions.add(s);
			else
				inactiveSessions.add(s);
		}
		activeSessionListAdapter = new SessionListAdapter(getApplicationContext(), activeSessions.toArray());
		activeSessionList.setAdapter(activeSessionListAdapter);	
		activeSessionList.setClickable(true);
		activeSessionList.setOnItemClickListener(new OnItemClickListener()
		{
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) 
			{				
				RainChequeApplication.currentSession = (SessionRecord)activeSessionList.getItemAtPosition(arg2);
				Intent showLog = new Intent(ControlBoardActivity.this, LogActivity.class);
				startActivity(showLog);
			}

			

		});
		activeSessionList.setOnItemLongClickListener(new OnItemLongClickListener()
		{
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)			
			{
				AlertDialog.Builder builder = new AlertDialog.Builder(ControlBoardActivity.this);
	        	builder.setTitle(getString(R.string.choose_session_action));	        	
	        	final int i = arg2; 
	        	builder.setPositiveButton(getString(R.string.end_session), new DialogInterface.OnClickListener() 
	        	{ 
	        	    public void onClick(DialogInterface dialog, int which) 
	        	    {
	        	    	SessionRecord s = (SessionRecord)activeSessionList.getItemAtPosition(i);
	        	    	for(SessionRecord sr: RainChequeApplication.sessionList)
	        	    	{
	        	    		if(sr.sessionID == s.sessionID)
	        	    			sr.isActive = false;
	        	    	}
	        	    	RainChequeApplication.writeAccountsToFile(getApplicationContext());
	        	    	refreshList();
	        	    }
	        	});
	        	builder.setNeutralButton(getString(R.string.delete_session), new DialogInterface.OnClickListener() 
	        	{ 
	        	    public void onClick(DialogInterface dialog, int which) 
	        	    {
	        	    	SessionRecord s = (SessionRecord)activeSessionList.getItemAtPosition(i);
	        	    	for(SessionRecord sr: RainChequeApplication.sessionList)
	        	    	{
	        	    		if(sr.sessionID == s.sessionID)
	        	    		{
	        	    			RainChequeApplication.sessionList.remove(sr);
	        	    			break;
	        	    		}
	        	    	}
	        	    	RainChequeApplication.writeAccountsToFile(getApplicationContext());
	        	    	refreshList();
	        	    }
	        	});
	        	builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() 
	        	{
	        	    public void onClick(DialogInterface dialog, int which) 
	        	    {
	        	        dialog.cancel();
	        	    }
	        	});

	        	builder.show();
				return false;
				
			}

		});
		
		inactiveSessionListAdapter = new SessionListAdapter(getApplicationContext(), inactiveSessions.toArray());
		inactiveSessionList.setAdapter(inactiveSessionListAdapter);	
		inactiveSessionList.setClickable(true);
		inactiveSessionList.setOnItemClickListener(new OnItemClickListener()
		{
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) 
			{				
				RainChequeApplication.currentSession = (SessionRecord)inactiveSessionList.getItemAtPosition(arg2);
				Intent showLog = new Intent(ControlBoardActivity.this, LogActivity.class);
				startActivity(showLog);
			}

			

		});
		inactiveSessionList.setOnItemLongClickListener(new OnItemLongClickListener()
		{
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)			
			{
				AlertDialog.Builder builder = new AlertDialog.Builder(ControlBoardActivity.this);
	        	builder.setTitle(getString(R.string.choose_session_action));	        	
	        	final int i = arg2; 	        	
	        	builder.setPositiveButton(getString(R.string.delete_session), new DialogInterface.OnClickListener() 
	        	{ 
	        	    public void onClick(DialogInterface dialog, int which) 
	        	    {
	        	    	SessionRecord s = (SessionRecord)inactiveSessionList.getItemAtPosition(i);
	        	    	for(SessionRecord sr: RainChequeApplication.sessionList)
	        	    	{
	        	    		if(sr.sessionID == s.sessionID)
	        	    		{
	        	    			RainChequeApplication.sessionList.remove(sr);
	        	    			break;
	        	    		}
	        	    	}
	        	    	RainChequeApplication.writeAccountsToFile(getApplicationContext());
	        	    	refreshList();
	        	    }
	        	});
	        	builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() 
	        	{
	        	    public void onClick(DialogInterface dialog, int which) 
	        	    {
	        	        dialog.cancel();
	        	    }
	        	});

	        	builder.show();
				return false;
				
			}

		});
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		getMenuInflater().inflate(R.menu.control_board, menu);
		return true;
	}

	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) 
	{
		switch(item.getItemId())
		{
		case R.id.action_help:
			Intent toSplashActivity = new Intent(ControlBoardActivity.this, SplashActivity.class);
			toSplashActivity.setAction(RainChequeApplication.ACTION_SHOW_TOUR);
			startActivity(toSplashActivity);
			break;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	protected void onResume() 
	{
		super.onResume();
		refreshList();
	}
	
	

}
