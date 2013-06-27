package com.rhoadster91.raincheque;

import java.util.ArrayList;

import android.os.Bundle;
import android.widget.ListView;

public class SuggestionActivity extends ThemedActivity 
{

	ArrayList<LogEntry> logEntries = new ArrayList<LogEntry>();
	ListView logList;
	LogAdapter logAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.suggestions_activity);
		calculateSettlements();
		logList = (ListView)findViewById(R.id.listSuggestions);
		logAdapter = new LogAdapter(this, logEntries.toArray());
		logList.setAdapter(logAdapter);
	}
	
	private void calculateSettlements()
	{
		int maxBalance = 0, closestBalance = 0, iteration = 0, correctiveChange = 0;
		AccountRecord maxRecord = null, closestRecord = null;
		ArrayList<AccountRecord> tempAccountList = new ArrayList<AccountRecord>(); 
		for(AccountRecord a:RainChequeApplication.currentSession.accountList)
		{
			AccountRecord temp = new AccountRecord();
			temp.name = a.name;
			temp.paid = a.paid;
			temp.settlement = a.settlement;
			temp.worth = a.worth;
			tempAccountList.add(temp);
		}
		while(true)
		{		
			iteration++;
			if(iteration>RainChequeApplication.currentSession.accountList.size())
				correctiveChange++;
			maxBalance = 0;
			for(AccountRecord a: tempAccountList)
			{
				if(Math.abs(a.getBalance()) > Math.abs(maxBalance))
				{
					maxBalance = a.getBalance();
					maxRecord = a;
				}				
			}
			if(Math.abs(maxBalance)<correctiveChange)
				return;			
			closestBalance = 0;
			for(AccountRecord a: tempAccountList)
			{
				if(maxBalance > 0)
				{
					if(a.getBalance() < closestBalance)
					{
						closestBalance = a.getBalance();
						closestRecord = a;
					}					
				}
				else
				{
					if(a.getBalance() > closestBalance)
					{
						closestBalance = a.getBalance();
						closestRecord = a;
					}
				}
				
			}
			if(closestBalance==0)
				continue;
			String logText;			
			if(maxBalance > 0)
			{
				closestRecord.paid -= closestBalance;
				maxRecord.settlement -= closestBalance;
				logText = String.format(getString(R.string.settlement_proposal), closestRecord.name, (-closestBalance), maxRecord.name);
			}
			else
			{
				maxRecord.paid += closestBalance;
				closestRecord.settlement += closestBalance;
				logText = String.format(getString(R.string.settlement_proposal), maxRecord.name, closestBalance, closestRecord.name);
			}			
			LogEntry temp = new LogEntry();				    	
			temp.entry = logText;
			logEntries.add(temp);			
		}
	}	
}
