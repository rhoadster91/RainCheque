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
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class LogActivity extends Activity
{

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.log_activity);
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
    	        	    	for(SessionRecord s:RainChequeApplication.sessionList)
    	        			{
    	        				if(s.sessionID==RainChequeApplication.currentSession.sessionID)
    	        					s.accountList.add(a);
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
			
		case R.id.conf_payment:
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
			
		case R.id.m_settlement:
			Toast.makeText(getApplicationContext(), "Under construction", Toast.LENGTH_LONG).show();			
			break;
			
		case R.id.p_settlement:
			Toast.makeText(getApplicationContext(), "Under construction", Toast.LENGTH_LONG).show();
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
}
