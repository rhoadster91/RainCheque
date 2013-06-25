package com.xteam.raincheque;

import java.util.Calendar;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

public class ActivityActivity extends ThemedActivity 
{
	ListView activityList = null;
	ActivityAdapter activityAdapter = null;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_activity);
		refreshList();
	}
	
	private void refreshList()
	{
		activityList = (ListView)findViewById(R.id.listActivities);
		activityAdapter = new ActivityAdapter(ActivityActivity.this, RainChequeApplication.currentSession.activityList.toArray());
		activityList.setAdapter(activityAdapter);	
		activityList.setOnItemLongClickListener(new OnItemLongClickListener()
		{

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) 
			{
				AlertDialog.Builder builder = new AlertDialog.Builder(ActivityActivity.this);
				final int which = arg2;
	        	builder.setTitle(getString(R.string.choose_session_action));	        	
	        	builder.setPositiveButton(getString(R.string.delete_activity), new DialogInterface.OnClickListener() 
	        	{ 
	        	    public void onClick(DialogInterface dialog, int whic) 
	        	    {
	        	    	int id = RainChequeApplication.currentSession.activityList.get(which).activityId;
	    				for(LogEntry le:RainChequeApplication.currentSession.sessionLog)
	    				{
	    					if(le.activityReference==id)
	    						le.isDeleted = true;
	    				}
	    				LogEntry logEntry = new LogEntry();
	    				logEntry.entry = String.format(getString(R.string.activity), RainChequeApplication.currentSession.activityList.get(which).label);
	    				Time now = new Time();
	    		    	now.setToNow();
	    		    	String date = DateFormat.format("d MMMM yyyy", Calendar.getInstance()).toString();
	    		    	logEntry.time = now.format(date + " at %I:%M:%S");	
	    		    	ActivityRecord activity = RainChequeApplication.currentSession.activityList.get(which);
	    		    	for(AccountRecord ar:RainChequeApplication.currentSession.accountList)
	    		    	{
	    		    		if(activity.hasPayer(ar.id))	    		    		
	    		    			ar.paid -= activity.getMoneyAtPayer(ar.id);
	    		    		if(activity.hasPayee(ar.id))
	    		    			ar.worth -= activity.settleWorth;
	    		    		if(activity.changeId==ar.id)
	    		    			ar.settlement -= activity.changeAmount;
	    		    	}
	    		    	RainChequeApplication.currentSession.sessionLog.add(logEntry);
	    		    	RainChequeApplication.currentSession.activityList.remove(which);	    				
	    				for(SessionRecord sr : RainChequeApplication.sessionList)
	    				{
	    					if(sr.sessionID==RainChequeApplication.currentSession.sessionID)
	    					{
	    						sr.activityList = RainChequeApplication.currentSession.activityList;
	    						sr.sessionLog = RainChequeApplication.currentSession.sessionLog;
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
	protected void onResume() 
	{
		super.onResume();
		refreshList();
	}

}
