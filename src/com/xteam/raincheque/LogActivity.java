package com.xteam.raincheque;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.InputType;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class LogActivity extends Activity
{
	int payerID, payeeID;
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
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		if(RainChequeApplication.currentSession.isActive)
		{
			getMenuInflater().inflate(R.menu.log, menu);
			return true;
		}
		else
			return false;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item)
	{
		AlertDialog.Builder builder;
		switch(item.getItemId())
		{
		case R.id.a_participant:
			builder = new AlertDialog.Builder(LogActivity.this);
        	builder.setTitle(getString(R.string.choose_session_action));	        	
        	builder.setPositiveButton(getString(R.string.from_contacts), new DialogInterface.OnClickListener() 
        	{ 
        	    public void onClick(DialogInterface dialog, int which) 
        	    {
        	    	Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);  
        	    	startActivityForResult(intent, 1);
        	    }
        	});
        	builder.setNeutralButton(getString(R.string.from_text), new DialogInterface.OnClickListener() 
        	{ 
        	    public void onClick(DialogInterface dialog, int which) 
        	    {
        	    	AlertDialog.Builder builder = new AlertDialog.Builder(LogActivity.this);
    	        	builder.setTitle(getString(R.string.add_participant));
    	        	LinearLayout lila = new LinearLayout(getApplicationContext());
    	        	lila.setOrientation(LinearLayout.VERTICAL);
    	        	TextView tv = new TextView(LogActivity.this);
    	        	tv.setText(getString(R.string.participant_name));
    	        	tv.setTextAppearance(LogActivity.this, android.R.style.TextAppearance_Large);
    	        	tv.setPadding(30, 30, 30, 30);	        	
    	        	lila.addView(tv);        	
    	        	final EditText input = new EditText(LogActivity.this);	        	
    	        	input.setInputType(InputType.TYPE_CLASS_TEXT);
    	        	input.setPadding(30, 30, 30, 30);
    	        	lila.addView(input);
    	        	builder.setView(lila);
    	        	builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() 
    	        	{ 
    	        	    public void onClick(DialogInterface dialog, int which) 
    	        	    {
    	        	    	AccountRecord a = new AccountRecord();
    	        	    	a.name = input.getText().toString();
    	        	    	RainChequeApplication.currentSession.accountList.add(a);
    	        	    	for(SessionRecord s:RainChequeApplication.sessionList)
    	        			{
    	        				if(s.sessionID==RainChequeApplication.currentSession.sessionID)    	        				
    	        					s.accountList = RainChequeApplication.currentSession.accountList;    	        				
    	        			}
    	        	    	RainChequeApplication.writeAccountsToFile(getApplicationContext());    	        	    	
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
        	builder.show();
			break;
			
		case R.id.m_settlement:
			builder = new AlertDialog.Builder(LogActivity.this);
        	builder.setTitle(getString(R.string.select_payer));
        	builder.setAdapter(new ParticipantListAdapter(getApplicationContext(), RainChequeApplication.currentSession.accountList.toArray()), new DialogInterface.OnClickListener() 
        	{				
				@Override
				public void onClick(DialogInterface dialog, int which) 
				{
					Object []payeeList = new Object[RainChequeApplication.currentSession.accountList.size() - 1];
					int j = 0;
					for(int i=0;i<RainChequeApplication.currentSession.accountList.size();i++)
					{
						if(i!=which)
						{
							payeeList[j++] = RainChequeApplication.currentSession.accountList.get(i);
						}
					}		
					payerID = which;
					AlertDialog.Builder builder = new AlertDialog.Builder(LogActivity.this);
    	        	builder.setTitle(getString(R.string.select_payee));
    	        	builder.setAdapter(new ParticipantListAdapter(getApplicationContext(), payeeList), new DialogInterface.OnClickListener()
    	        	{

						@Override
						public void onClick(DialogInterface dialog, int which) 
						{
							payeeID = which;
							if(payeeID>=payerID)
								payeeID++;
							AlertDialog.Builder builder = new AlertDialog.Builder(LogActivity.this);
				        	builder.setTitle(getString(R.string.enter_amount));
				        	final EditText input = new EditText(LogActivity.this);	        	
				        	input.setInputType(InputType.TYPE_CLASS_NUMBER);
				        	input.setPadding(30, 30, 30, 30);
				        	builder.setView(input);
				        	builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() 
				        	{ 
				        	    public void onClick(DialogInterface dialog, int which) 
				        	    {
				        	    	if(input.getText().toString().trim().contentEquals(""))
				        	    		return;			        	    	
				        	    	String payer = RainChequeApplication.currentSession.accountList.get(payerID).name;
				        	    	String payee = RainChequeApplication.currentSession.accountList.get(payeeID).name;
				        	    	String amount = input.getText().toString();
				        	    	RainChequeApplication.currentSession.accountList.get(payerID).paid += Integer.parseInt(amount); 
				        	    	RainChequeApplication.currentSession.accountList.get(payeeID).settlement += Integer.parseInt(amount); 
				        	    	String logText = String.format(getString(R.string.settlement_statement), payer, amount, payee);
				        	    	LogEntry logEntry = new LogEntry();
				        	    	logEntry.entry = logText;
				        	    	Time now = new Time();
				        	    	now.setToNow();
				        	    	String date = DateFormat.format("d MMMM yyyy", Calendar.getInstance()).toString();
				        	    	logEntry.time = now.format(date + " at %I:%M:%S");				        	    	
				        	    	RainChequeApplication.currentSession.sessionLog.add(logEntry);
				        	    	for(SessionRecord s:RainChequeApplication.sessionList)
				        			{
				        				if(s.sessionID==RainChequeApplication.currentSession.sessionID)
				        				{
				        					s.sessionLog = RainChequeApplication.currentSession.sessionLog;
				        					s.accountList = RainChequeApplication.currentSession.accountList;
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
        	builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() 
        	{
        	    public void onClick(DialogInterface dialog, int which) 
        	    {
        	        dialog.cancel();
        	    }
        	});

        	builder.show();
			break;
			
		case R.id.e_session:
			builder = new AlertDialog.Builder(LogActivity.this);
        	builder.setTitle(getString(R.string.edit_session));
        	LinearLayout lila = new LinearLayout(getApplicationContext());
        	lila.setOrientation(LinearLayout.VERTICAL);
        	TextView tv = new TextView(LogActivity.this);
        	tv.setText(getString(R.string.session_name));
        	tv.setTextAppearance(LogActivity.this, android.R.style.TextAppearance_Large);
        	tv.setPadding(30, 30, 30, 30);	        	
        	lila.addView(tv);        	
        	final EditText input = new EditText(LogActivity.this);	        	
        	input.setInputType(InputType.TYPE_CLASS_TEXT);
        	input.setPadding(30, 30, 30, 30);
        	input.setText(RainChequeApplication.currentSession.label);
        	input.selectAll();
        	lila.addView(input);
        	builder.setView(lila);
        	builder.setPositiveButton(getString(R.string.done), new DialogInterface.OnClickListener() 
        	{ 
        	    public void onClick(DialogInterface dialog, int which) 
        	    {
        	    	for(SessionRecord s:RainChequeApplication.sessionList)
        			{
        				if(s.sessionID==RainChequeApplication.currentSession.sessionID)
        					s.label = input.getText().toString();
        			}
        	    	RainChequeApplication.writeAccountsToFile(getApplicationContext());
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
			break;
			
		case R.id.x_session:
			for(SessionRecord s:RainChequeApplication.sessionList)
			{
				if(s.sessionID==RainChequeApplication.currentSession.sessionID)
					s.isActive = false;
			}
			RainChequeApplication.writeAccountsToFile(getApplicationContext());
			onBackPressed();
			break;
			
		case R.id.conf_payment:
			Intent showPayments = new Intent(LogActivity.this, PaymentActivity.class);
			startActivity(showPayments);
			break;
			
		case R.id.p_settlement:
			Intent showSuggestions = new Intent(LogActivity.this, SuggestionActivity.class);
			startActivity(showSuggestions);
			break;
			
			
		}
		return super.onMenuItemSelected(featureId, item);
	}
	
	@Override  
	public void onActivityResult(int reqCode, int resultCode, Intent data) 
	{  
	    super.onActivityResult(reqCode, resultCode, data);  
	    if (resultCode == Activity.RESULT_OK) 
	    {  
	        Uri contactUri = data.getData();  
	        Cursor cursor = getContentResolver().query(contactUri, null, null, null, null);
	        if (cursor.moveToFirst()) 
	        {
	            int idx = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
	            String name = cursor.getString(idx);
	            AccountRecord a = new AccountRecord();
    	    	a.name = name;
    	    	for(SessionRecord s:RainChequeApplication.sessionList)
    			{
    				if(s.sessionID==RainChequeApplication.currentSession.sessionID)
    					s.accountList.add(a);
    			}
    	    	RainChequeApplication.writeAccountsToFile(getApplicationContext());
    	    	
	        }	        
	    }  
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
		logAdapter = new LogAdapter(getApplicationContext(), RainChequeApplication.currentSession.sessionLog.toArray());
		logList.setAdapter(logAdapter);			
	}
}
