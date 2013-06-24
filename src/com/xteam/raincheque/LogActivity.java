package com.xteam.raincheque;

import android.os.Bundle;
import android.widget.ListView;

public class LogActivity extends ThemedActivity
{
	ListView logList = null;
	LogAdapter logAdapter = null;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.log_activity);
		refreshList();
	}
	
	
	
	
	@Override
	protected void onResume() 
	{
		super.onResume();
		refreshList();
	}

	private void refreshList()
	{
		logList = (ListView)findViewById(R.id.listLog);
		logAdapter = new LogAdapter(LogActivity.this, RainChequeApplication.currentSession.sessionLog.toArray());
		logList.setAdapter(logAdapter);			
	}
}
