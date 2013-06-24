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
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ToggleButton;

public class SessionActivity extends ThemedActivity 
{
	int payerID, payeeID;
	ListView participantList = null;
	ParticipantListAdapter participantAdapter = null;
		
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.session_activity);
		refreshList();
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) 
	{		
		getMenuInflater().inflate(R.menu.log, menu);
		menu.findItem(R.id.m_settlement).setVisible(false);			
		menu.findItem(R.id.a_participant).setVisible(false);
		menu.findItem(R.id.e_session).setVisible(false);
		menu.findItem(R.id.conf_payment).setVisible(false);
		menu.findItem(R.id.p_settlement).setVisible(true);
		menu.findItem(R.id.s_log).setVisible(true);
		if(RainChequeApplication.currentSession.isActive)
		{
			menu.findItem(R.id.m_settlement).setVisible(true);			
			menu.findItem(R.id.a_participant).setVisible(true);
			menu.findItem(R.id.e_session).setVisible(true);
			menu.findItem(R.id.conf_payment).setVisible(true);			
		}
		return true;
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item)
	{
		AlertDialog.Builder builder;
		switch(item.getItemId())
		{
		case R.id.a_participant:
			builder = new AlertDialog.Builder(SessionActivity.this);
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
        	    	AlertDialog.Builder builder = new AlertDialog.Builder(SessionActivity.this);
    	        	builder.setTitle(getString(R.string.add_participant));
    	        	LinearLayout lila = new LinearLayout(getApplicationContext());
    	        	lila.setOrientation(LinearLayout.VERTICAL);
    	        	TextView tv = new TextView(SessionActivity.this);
    	        	tv.setText(getString(R.string.participant_name));
    	        	tv.setTextAppearance(SessionActivity.this, android.R.style.TextAppearance_Large);
    	        	tv.setPadding(30, 30, 30, 30);	        	
    	        	lila.addView(tv);        	
    	        	final EditText input = new EditText(SessionActivity.this);	        	
    	        	input.setInputType(InputType.TYPE_CLASS_TEXT);
    	        	input.setPadding(30, 30, 30, 30);
    	        	lila.addView(input);
    	        	builder.setView(lila);
    	        	builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() 
    	        	{ 
    	        	    public void onClick(DialogInterface dialog, int which) 
    	        	    {
    	        	    	if(input.getText().toString().trim().contentEquals(""))
		        	    		return;	        	    	
    	        	    	AccountRecord a = new AccountRecord();
    	        	    	a.name = input.getText().toString();
    	        	    	RainChequeApplication.currentSession.accountList.add(a);
    	        	    	for(SessionRecord s:RainChequeApplication.sessionList)
    	        			{
    	        				if(s.sessionID==RainChequeApplication.currentSession.sessionID)    	        				
    	        					s.accountList = RainChequeApplication.currentSession.accountList;    	        				
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
        	builder.show();
			break;
			
		case R.id.m_settlement:
			builder = new AlertDialog.Builder(SessionActivity.this);
        	builder.setTitle(getString(R.string.select_payer));
        	builder.setAdapter(new ParticipantListAdapter(SessionActivity.this, RainChequeApplication.currentSession.accountList.toArray()), new DialogInterface.OnClickListener() 
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
					AlertDialog.Builder builder = new AlertDialog.Builder(SessionActivity.this);
    	        	builder.setTitle(getString(R.string.select_payee));
    	        	builder.setAdapter(new ParticipantListAdapter(SessionActivity.this, payeeList), new DialogInterface.OnClickListener()
    	        	{

						@Override
						public void onClick(DialogInterface dialog, int which) 
						{
							payeeID = which;
							if(payeeID>=payerID)
								payeeID++;
							AlertDialog.Builder builder = new AlertDialog.Builder(SessionActivity.this);
				        	builder.setTitle(getString(R.string.enter_amount));
				        	final EditText input = new EditText(SessionActivity.this);	        	
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
				        	    	Intent showLog = new Intent(SessionActivity.this, LogActivity.class);
				    				startActivity(showLog);
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
			builder = new AlertDialog.Builder(SessionActivity.this);
        	builder.setTitle(getString(R.string.edit_session));
        	LinearLayout lila = new LinearLayout(SessionActivity.this);
        	lila.setOrientation(LinearLayout.VERTICAL);
        	TextView tv = new TextView(SessionActivity.this);
        	tv.setText(getString(R.string.session_name));
        	tv.setTextAppearance(SessionActivity.this, android.R.style.TextAppearance_Large);
        	tv.setPadding(30, 30, 30, 30);	        	
        	lila.addView(tv);        	
        	final EditText input = new EditText(SessionActivity.this);	        	
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
							
		case R.id.conf_payment:
			Intent showPayments = new Intent(SessionActivity.this, PaymentActivity.class);
			startActivity(showPayments);
			break;
			
		case R.id.p_settlement:
			Intent showSuggestions = new Intent(SessionActivity.this, SuggestionActivity.class);
			startActivity(showSuggestions);
			break;
		
		case R.id.s_log:
			Intent showLog = new Intent(SessionActivity.this, LogActivity.class);
			startActivity(showLog);
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
    	    	RainChequeApplication.currentSession.accountList.add(a);
    	    	for(SessionRecord s:RainChequeApplication.sessionList)
    			{
    				if(s.sessionID==RainChequeApplication.currentSession.sessionID)
    				{
    					s.accountList.add(a);
    					if(s.accountList.size()>=2)
    					{
    						s.isActive = true;
    						RainChequeApplication.currentSession.isActive = true;
    					}
    				}
    			}    	    	
    	    	RainChequeApplication.writeAccountsToFile(getApplicationContext());
    	    	refreshList();
	        }	        
	    }  
	}
	
	private void refreshList()
	{
		TextView tvSession = (TextView)findViewById(R.id.tvSessionName);
		TextView tvTotal = (TextView)findViewById(R.id.tvSessionTotal);
		tvSession.setText(new String(getString(R.string.session)) + ": " + RainChequeApplication.currentSession.label);
		int total = 0;
		for(AccountRecord a: RainChequeApplication.currentSession.accountList)
			total += a.worth;
		tvTotal.setText(new String(getString(R.string.total)) + ": " + total);
		ToggleButton tbActive = (ToggleButton)findViewById(R.id.toggleButton1);
		tbActive.setChecked(RainChequeApplication.currentSession.isActive);
		tbActive.setOnCheckedChangeListener(new OnCheckedChangeListener()
		{

			@Override
			public void onCheckedChanged(CompoundButton buttonView,	boolean isChecked) 
			{					
				if(!isChecked)
				{				
					
					for(SessionRecord s:RainChequeApplication.sessionList)
					{
						if(s.sessionID==RainChequeApplication.currentSession.sessionID)
							s.isActive = false;
					}					
					RainChequeApplication.currentSession.isActive = false;
					RainChequeApplication.writeAccountsToFile(getApplicationContext());					
				}
				else
				{
					if(RainChequeApplication.currentSession.accountList.size()<2)
					{
						final CompoundButton cb = buttonView;
						
						AlertDialog.Builder builder = new AlertDialog.Builder(SessionActivity.this);
			        	builder.setTitle(getString(R.string.too_few_participants));
			        	TextView tvTFP = new TextView(SessionActivity.this);
			        	tvTFP.setText(getString(R.string.too_few_participants_description_2));
			        	tvTFP.setTextAppearance(SessionActivity.this, android.R.style.TextAppearance_Medium);
			        	tvTFP.setPadding(30, 30, 30, 30);        	
			        	builder.setView(tvTFP);
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
			        	    	AlertDialog.Builder builder = new AlertDialog.Builder(SessionActivity.this);
			    	        	builder.setTitle(getString(R.string.add_participant));
			    	        	LinearLayout lila = new LinearLayout(getApplicationContext());
			    	        	lila.setOrientation(LinearLayout.VERTICAL);
			    	        	TextView tv = new TextView(SessionActivity.this);
			    	        	tv.setText(getString(R.string.participant_name));
			    	        	tv.setTextAppearance(SessionActivity.this, android.R.style.TextAppearance_Large);
			    	        	tv.setPadding(30, 30, 30, 30);	        	
			    	        	lila.addView(tv);        	
			    	        	final EditText input = new EditText(SessionActivity.this);	        	
			    	        	input.setInputType(InputType.TYPE_CLASS_TEXT);
			    	        	input.setPadding(30, 30, 30, 30);
			    	        	lila.addView(input);
			    	        	builder.setView(lila);
			    	        	builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() 
			    	        	{ 
			    	        	    public void onClick(DialogInterface dialog, int which) 
			    	        	    {
			    	        	    	if(input.getText().toString().trim().contentEquals(""))
			    	        	    	{
			    	        	    		dialog.dismiss();
			    	        	    		refreshList();
			    	        	    		return;
			    	        	    	}			    	        	    						        	    	
			    	        	    	AccountRecord a = new AccountRecord();
			    	        	    	a.name = input.getText().toString();
			    	        	    	RainChequeApplication.currentSession.accountList.add(a);
			    	        	    	for(SessionRecord s:RainChequeApplication.sessionList)
			    	        			{
			    	        				if(s.sessionID==RainChequeApplication.currentSession.sessionID)
			    	        				{
			    	        					s.isActive = true;
			    	        					s.accountList = RainChequeApplication.currentSession.accountList;
			    	        				}
			    	        			}
			    	        	    	RainChequeApplication.currentSession.isActive = true;
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
			        	builder.setNegativeButton(getString(R.string.keep_closed), new DialogInterface.OnClickListener() 
			        	{
			        	    public void onClick(DialogInterface dialog, int which) 
			        	    {			        	    	
			        			dialog.cancel();			
			        			cb.setChecked(false);
			        	    }
			        	});
			        	builder.show();
					}
					else
					{
						for(SessionRecord s:RainChequeApplication.sessionList)
						{
							if(s.sessionID==RainChequeApplication.currentSession.sessionID)
								s.isActive = true;
						}
						RainChequeApplication.currentSession.isActive = true;
						RainChequeApplication.writeAccountsToFile(getApplicationContext());
					}
				}				
			}

			
			
		});
		participantList = (ListView)findViewById(R.id.lvAllParticipants);
		participantAdapter = new ParticipantListAdapter(this, RainChequeApplication.currentSession.accountList.toArray());
		participantList.setAdapter(participantAdapter);
		if(!RainChequeApplication.currentSession.isActive)
			return;
		participantList.setClickable(true);
		participantList.setOnItemClickListener(new OnItemClickListener()
		{
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) 
			{

							
			}	

		});
		participantList.setOnItemLongClickListener(new OnItemLongClickListener()
		{
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)			
			{	
				if(RainChequeApplication.currentSession.accountList.get(arg2).getBalance()==0)
				{
					AlertDialog.Builder builder = new AlertDialog.Builder(SessionActivity.this);
		        	builder.setTitle(getString(R.string.choose_session_action));	        	
		        	final int i = arg2; 
		        	builder.setPositiveButton(getString(R.string.remove_participant), new DialogInterface.OnClickListener() 
		        	{ 
		        	    public void onClick(DialogInterface dialog, int which) 
		        	    {
		        	    	RainChequeApplication.currentSession.accountList.remove(i);
		        	    	for(SessionRecord sr: RainChequeApplication.sessionList)
		        	    	{
		        	    		if(sr.sessionID==RainChequeApplication.currentSession.sessionID)
		        	    			sr.accountList = RainChequeApplication.currentSession.accountList;
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
				else
				{
					AlertDialog.Builder builder = new AlertDialog.Builder(SessionActivity.this);
		        	builder.setTitle(getString(R.string.oops));
		        	TextView tv = new TextView(SessionActivity.this);
		        	tv.setText(R.string.cant_remove_participant);
		        	tv.setTextAppearance(SessionActivity.this, android.R.style.TextAppearance_Large);
		        	tv.setPadding(30, 30, 30, 30);
		        	builder.setView(tv);
		        	builder.setNegativeButton(getString(R.string.ok), new DialogInterface.OnClickListener() 
		        	{
		        	    public void onClick(DialogInterface dialog, int which) 
		        	    {
		        	        dialog.cancel();
		        	    }
		        	});
		        	builder.show();
				}
				return false;
			}

		});	
		if(RainChequeApplication.currentSession.accountList.size()<2)
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(SessionActivity.this);
        	builder.setTitle(getString(R.string.too_few_participants));
        	TextView tvTFP = new TextView(this);
        	tvTFP.setText(getString(R.string.too_few_participants_description));
        	tvTFP.setTextAppearance(SessionActivity.this, android.R.style.TextAppearance_Medium);
        	tvTFP.setPadding(30, 30, 30, 30);        	
        	builder.setView(tvTFP);
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
        	    	AlertDialog.Builder builder = new AlertDialog.Builder(SessionActivity.this);
    	        	builder.setTitle(getString(R.string.add_participant));
    	        	LinearLayout lila = new LinearLayout(getApplicationContext());
    	        	lila.setOrientation(LinearLayout.VERTICAL);
    	        	TextView tv = new TextView(SessionActivity.this);
    	        	tv.setText(getString(R.string.participant_name));
    	        	tv.setTextAppearance(SessionActivity.this, android.R.style.TextAppearance_Large);
    	        	tv.setPadding(30, 30, 30, 30);	        	
    	        	lila.addView(tv);        	
    	        	final EditText input = new EditText(SessionActivity.this);	        	
    	        	input.setInputType(InputType.TYPE_CLASS_TEXT);
    	        	input.setPadding(30, 30, 30, 30);
    	        	lila.addView(input);
    	        	builder.setView(lila);
    	        	builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() 
    	        	{ 
    	        	    public void onClick(DialogInterface dialog, int which) 
    	        	    {
    	        	    	if(input.getText().toString().trim().contentEquals(""))
    	        	    	{
    	        	    		dialog.dismiss();
    	        	    		refreshList();
    	        	    		return;
    	        	    	}
    	        	    	AccountRecord a = new AccountRecord();
    	        	    	a.name = input.getText().toString();
    	        	    	RainChequeApplication.currentSession.accountList.add(a);
    	        	    	for(SessionRecord s:RainChequeApplication.sessionList)
    	        			{
    	        				if(s.sessionID==RainChequeApplication.currentSession.sessionID)    	        				
    	        					s.accountList = RainChequeApplication.currentSession.accountList;    	        				
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
        	builder.setNegativeButton(getString(R.string.end_session), new DialogInterface.OnClickListener() 
        	{
        	    public void onClick(DialogInterface dialog, int which) 
        	    {
        	    	for(SessionRecord s:RainChequeApplication.sessionList)
        			{
        				if(s.sessionID==RainChequeApplication.currentSession.sessionID)
        					s.isActive = false;
        			}
        			RainChequeApplication.writeAccountsToFile(getApplicationContext());
        			dialog.cancel();
        			onBackPressed();        	        
        	    }
        	});
        	builder.show();
		}
	}

	@Override
	protected void onResume()
	{
		refreshList();
		super.onResume();
	}

	
}
