package com.xteam.raincheque;

import java.util.ArrayList;
import java.util.Calendar;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class PaymentActivity extends ThemedActivity 
{
	Button bAddParticipants;
	Button bAddPayers;
	Button bDone;	
	TextView tvList;
	TextView tvPayers;
	EditText etLabel;
	EditText etActualTotal;
	ArrayList<AccountRecord> tempAccountList;
	ArrayList<Integer> ids;
	int subtotal = 0, participantCount = 0;
	ActivityRecord thisActivity = new ActivityRecord();
	int total = 0;
	String change;
	boolean skipCheck = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.payment_activity);		
		bAddParticipants = (Button)findViewById(R.id.buttonAddInvolvedParticipants);
		bAddParticipants.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				ArrayList<String> names = new ArrayList<String>();
				for(AccountRecord ar: RainChequeApplication.currentSession.accountList)				
					names.add(ar.name);
				ArrayList<Boolean> checks = new ArrayList<Boolean>();
				for(AccountRecord ar: RainChequeApplication.currentSession.accountList)
				{
					if(thisActivity.hasPayee(ar.id))
						checks.add(new Boolean(true));
					else
						checks.add(new Boolean(false));
				}
				AlertDialog.Builder builder = new AlertDialog.Builder(PaymentActivity.this);
	        	builder.setTitle(getString(R.string.select_payee));
	        	String []mynames = new String [names.size()];
	        	Boolean []mychecks= new Boolean [names.size()];
	        	names.toArray(mynames);
	        	checks.toArray(mychecks);
	        	builder.setMultiChoiceItems(mynames, booleanFromBooleanArray(mychecks), new DialogInterface.OnMultiChoiceClickListener()
	        	{

					@Override
					public void onClick(DialogInterface dialog, int which, boolean isChecked) 
					{

					}	        		
	        	});
	        	builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() 
	        	{ 
	        	    public void onClick(DialogInterface dialog, int which) 
	        	    {
	        	    	ListView list = ((AlertDialog)dialog).getListView();	        	    	
	        	    	ArrayList<String> participants = new ArrayList<String>();
	        	    	thisActivity.payees = new ArrayList<Integer>();
	        	    	for (int i=0; i<list.getCount(); i++) 
	        	    	{
	        	    		boolean checked = list.isItemChecked(i);
	        	    		if(checked)
	        	    		{	        	    			
	        	    			participants.add(RainChequeApplication.currentSession.accountList.get(i).name);
	        	    			thisActivity.payees.add(new Integer(RainChequeApplication.currentSession.accountList.get(i).id));
	        	    		}	        	    		
	        	    	}
	        	    	String selected = new String();	        	    		        	    	
	        	    	for(int i = 0;i<participants.size();i++)
	        	    	{
	        	    		selected = selected.concat(participants.get(i));
	        	    		if(i==participants.size()-2)
	        	    		{
	        	    			selected = selected.concat(" and ");
	        	    		}
	        	    		else if(i==participants.size()-1)
	        	    		{

	        	    		}
	        	    		else
	        	    		{
	        	    			selected = selected.concat(", ");
	        	    		}	        	    			
	        	    	}
	        	    	participantCount = participants.size();
	        	    	if(selected.contentEquals(""))
	        	    		selected = new String(getString(R.string.selected_participants));
	        	    	tvList.setText(selected);
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
		tvList = (TextView)findViewById(R.id.listOfInvolvedParticipants);
		tvPayers = (TextView)findViewById(R.id.payerList);
		etLabel = (EditText)findViewById(R.id.editTextLabel);
		etActualTotal = (EditText)findViewById(R.id.actualTotal);		
		bDone = (Button)findViewById(R.id.bDonePayments);
		bDone.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v) 
			{
				if(etLabel.getText().toString().trim().contentEquals(""))
				{
					AlertDialog.Builder builder = new AlertDialog.Builder(PaymentActivity.this);
		        	builder.setTitle(getString(R.string.oops));
		        	TextView tv = new TextView(PaymentActivity.this);
		        	tv.setText(R.string.no_label_selected);
		        	tv.setTextAppearance(PaymentActivity.this, android.R.style.TextAppearance_Large);
    	        	tv.setPadding(30, 30, 30, 30);
		        	builder.setView(tv);
		        	builder.setPositiveButton(getString(R.string.yes_sure), new DialogInterface.OnClickListener() 
		        	{
		        	    public void onClick(DialogInterface dialog, int which) 
		        	    {
		        	    	etLabel.requestFocus();
		        	    	InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		        	    	imm.showSoftInput(etLabel, InputMethodManager.SHOW_IMPLICIT);
		        	        return;
		        	    }
		        	});
		        	builder.setNegativeButton(getString(R.string.no_thanks), new DialogInterface.OnClickListener() 
		        	{
		        	    public void onClick(DialogInterface dialog, int which) 
		        	    {
		        	        dialog.cancel();
		        	        doneConfirmation();
		        	    }
		        	});
		        	builder.show();
				}
				else					
					doneConfirmation();
				
			}		
			
			
		});
		bAddPayers = (Button)findViewById(R.id.buttonAddPayingParticipants);
		bAddPayers.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				ArrayList<String> names = new ArrayList<String>();
				for(AccountRecord ar: RainChequeApplication.currentSession.accountList)				
					names.add(ar.name);
				ArrayList<Boolean> checks = new ArrayList<Boolean>();
				for(AccountRecord ar: RainChequeApplication.currentSession.accountList)
				{
					if(thisActivity.hasPayer(ar.id))
						checks.add(new Boolean(true));
					else
						checks.add(new Boolean(false));
				}
				AlertDialog.Builder builder = new AlertDialog.Builder(PaymentActivity.this);
	        	builder.setTitle(getString(R.string.select_payer));
	        	builder.setCancelable(false);
	        	String []mynames = new String [names.size()];
	        	Boolean []mychecks= new Boolean [names.size()];
	        	names.toArray(mynames);
	        	checks.toArray(mychecks);
	        	builder.setMultiChoiceItems(mynames, booleanFromBooleanArray(mychecks), new DialogInterface.OnMultiChoiceClickListener()
	        	{

					@Override
					public void onClick(DialogInterface dialog, int which, boolean isChecked) 
					{
						if(isChecked)
						{
							AlertDialog.Builder builder = new AlertDialog.Builder(PaymentActivity.this);
				        	builder.setTitle(getString(R.string.enter_amount));
				        	final int whichGuy = which;
				        	final EditText input = new EditText(PaymentActivity.this);	        	
				        	input.setInputType(InputType.TYPE_CLASS_NUMBER);
				        	input.setPadding(30, 30, 30, 30);
				        	builder.setView(input);
				        	builder.setCancelable(false);
				        	builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() 
				        	{ 
				        	    public void onClick(DialogInterface dialog, int which) 
				        	    {				        	    	
				        	    	int amount; 
				        	    	if(input.getText().toString().contentEquals(""))
				        	    		amount = 0;
				        	    	else
				        	    		amount = Integer.parseInt(input.getText().toString());
				        	    	thisActivity.payers.add(new Integer(RainChequeApplication.currentSession.accountList.get(whichGuy).id));
				        	    	thisActivity.payerMoney.add(new Integer(amount));
				        	    	InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
				        	    	imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
				        	    }
				        	});				        	
				        	builder.show();
						}
						else
						{
							for(int x=0;x<thisActivity.payers.size();x++)
							{
								if(thisActivity.payers.get(x).intValue()==RainChequeApplication.currentSession.accountList.get(which).id)
								{
									thisActivity.payers.remove(x);
									thisActivity.payerMoney.remove(x);
								}
							}
						}
					}	        		
	        	});
	        	builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() 
	        	{ 
	        	    public void onClick(DialogInterface dialog, int which) 
	        	    {	        	    	
	        	    	ArrayList<String> participants = new ArrayList<String>();
	        	    	String selected = new String();	        	    		        	    	
	        	    	for(AccountRecord ar: RainChequeApplication.currentSession.accountList)
	        	    	{
	        	    		if(thisActivity.hasPayer(ar.id))	        	    		
	        	    			participants.add(new String(ar.name + " (" + thisActivity.getMoneyAtPayer(ar.id) + ")"));
	        	    		
        	    		}
	        	    	
	        	    	for(int i = 0;i<participants.size();i++)
	        	    	{
	        	    		selected = selected.concat(participants.get(i));
	        	    		if(i==participants.size()-2)
	        	    		{
	        	    			selected = selected.concat(" and ");
	        	    		}
	        	    		else if(i==participants.size()-1)
	        	    		{

	        	    		}
	        	    		else
	        	    		{
	        	    			selected = selected.concat(", ");
	        	    		}	        	    			
	        	    	}
	        	    	if(selected.contentEquals(""))
	        	    		selected = new String(getString(R.string.selected_participants));
	        	    	tvPayers.setText(selected);
	        	    	total = 0;
	        			for(Integer i:thisActivity.payerMoney)
	        				total += i.intValue();
	        			etActualTotal.setText(new String("" + total));
	        	    }
	        	});	        	
	        	builder.show();
			}			
		});
	}	
	
	private boolean []booleanFromBooleanArray(Boolean []b)
	{
		boolean []booleans = new boolean[b.length];
		for(int i=0;i<b.length;i++)		
			booleans[i] = b[i];		
		return booleans;
		
	}
	
	private void doneConfirmation()
	{
		if(!skipCheck)
		{
			total = 0;
			for(Integer i:thisActivity.payerMoney)
				total += i.intValue();
		}
		if(total!=Integer.parseInt(etActualTotal.getText().toString()))
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(PaymentActivity.this);
        	builder.setTitle(getString(R.string.who_kept_the_change));
        	builder.setAdapter(new ParticipantListAdapter(PaymentActivity.this, RainChequeApplication.currentSession.accountList.toArray()), new DialogInterface.OnClickListener() 
        	{				
				@Override
				public void onClick(DialogInterface dialog, int which) 
				{
					thisActivity.changeId = RainChequeApplication.currentSession.accountList.get(which).id;
					change = RainChequeApplication.currentSession.accountList.get(which).name;
					thisActivity.changeAmount = total - Integer.parseInt(etActualTotal.getText().toString());
					total = Integer.parseInt(etActualTotal.getText().toString());
					skipCheck = true;
					doneConfirmation();
				}
        	});
        	builder.setNegativeButton(getString(R.string.discard_change), new DialogInterface.OnClickListener() 
        	{
        	    public void onClick(DialogInterface dialog, int which) 
        	    {
        	        thisActivity.changeId = -1;
        	        thisActivity.changeAmount = 0;
        	        change = getString(R.string.no_one);
        	        skipCheck = true;
        	        doneConfirmation();
        	    }
        	});

        	builder.show();
			return;
		}
		if(tvList.getText().toString().contentEquals(getString(R.string.selected_participants)) || tvPayers.getText().toString().contentEquals(getString(R.string.selected_participants)))
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(PaymentActivity.this);
        	builder.setTitle(getString(R.string.oops));
        	TextView tv = new TextView(PaymentActivity.this);
        	tv.setText(R.string.none_selected);
        	tv.setTextAppearance(PaymentActivity.this, android.R.style.TextAppearance_Large);
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
		else
		{
			int settleWorth = total / participantCount;
			thisActivity.settleWorth = settleWorth;
			for(AccountRecord ar:RainChequeApplication.currentSession.accountList)
			{
				if(thisActivity.hasPayee(ar.id))
					ar.worth += settleWorth;
			}
			for(AccountRecord ar:RainChequeApplication.currentSession.accountList)
			{
				if(thisActivity.hasPayer(ar.id))
					ar.paid += thisActivity.getMoneyAtPayer(ar.id);
			}
			for(AccountRecord ar:RainChequeApplication.currentSession.accountList)
			{
				if(ar.id == thisActivity.changeId)
					ar.settlement += thisActivity.changeAmount;
			}
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
			String logText = String.format(getString(R.string.payment_statement), etLabel.getText().toString(),  tvList.getText().toString(), new String("" + total), tvPayers.getText().toString(), change);	    	
			LogEntry logEntry = new LogEntry();
	    	logEntry.entry = logText;
	    	Time now = new Time();
	    	now.setToNow();
	    	String date = DateFormat.format("d MMMM yyyy", Calendar.getInstance()).toString();
	    	logEntry.time = now.format(date + " at %I:%M:%S");				        	    	
	    	RainChequeApplication.currentSession.sessionLog.add(logEntry); 	
			Intent showLog = new Intent(PaymentActivity.this, LogActivity.class);
			startActivity(showLog);
			finish();
			        	    	
		
		}
	}
}