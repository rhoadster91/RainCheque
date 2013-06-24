package com.xteam.raincheque;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.InputType;
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

public class ListOfParticipantsActivity<CurrentActivity> extends ThemedActivity
{
	ListView participantList = null;
	ParticipantListAdapter participantAdapter = null;
	Button bAddParticipant = null;
	Button bDone = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_participants);
		bAddParticipant = (Button)findViewById(R.id.buttonAddParticipant);
		bDone = (Button)findViewById(R.id.buttonDoneAddingParticipants);		
		refreshList();
		bDone.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				RainChequeApplication.sessionList.add(RainChequeApplication.currentSession);
				RainChequeApplication.writeAccountsToFile(getApplicationContext());
				onBackPressed();
			}			
		});
		bAddParticipant.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				AlertDialog.Builder builder = new AlertDialog.Builder(ListOfParticipantsActivity.this);
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
	        	    	AlertDialog.Builder builder = new AlertDialog.Builder(ListOfParticipantsActivity.this);
	    	        	builder.setTitle(getString(R.string.add_participant));
	    	        	LinearLayout lila = new LinearLayout(getApplicationContext());
	    	        	lila.setOrientation(LinearLayout.VERTICAL);
	    	        	TextView tv = new TextView(ListOfParticipantsActivity.this);
	    	        	tv.setText(getString(R.string.participant_name));
	    	        	tv.setTextAppearance(ListOfParticipantsActivity.this, android.R.style.TextAppearance_Large);
	    	        	tv.setPadding(30, 30, 30, 30);	        	
	    	        	lila.addView(tv);        	
	    	        	final EditText input = new EditText(ListOfParticipantsActivity.this);	        	
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
	}
	
	private void refreshList()
	{
		participantList = (ListView)findViewById(R.id.listOfParticipants);
		participantAdapter = new ParticipantListAdapter(this, RainChequeApplication.currentSession.accountList.toArray());
		participantList.setAdapter(participantAdapter);	
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
				return false;
			}

		});
		if(RainChequeApplication.currentSession.accountList.size() > 1)
			bDone.setEnabled(true);
		else
			bDone.setEnabled(false);
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
    	    	refreshList();
	        }	        
	    }  
	}

}
