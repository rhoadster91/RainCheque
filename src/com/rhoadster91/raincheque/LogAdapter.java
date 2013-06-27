package com.rhoadster91.raincheque;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class LogAdapter extends ArrayAdapter<Object> 
{
	private final Context context;
	private final Object[] values;	
	LogEntry s;
	
	public LogAdapter(Context context, Object[] objects) 
	{
		super(context, R.layout.session_row, objects);
		this.context = context;
		this.values = objects;
		RainChequeApplication.readAccountsFromFile(context);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
	    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    View rowView = inflater.inflate(R.layout.session_row, parent, false);
	    TextView sender = (TextView) rowView.findViewById(R.id.sessionName);
	    TextView balance = (TextView)rowView.findViewById(R.id.sessionMembers);
	    s = (LogEntry)values[position];
	    sender.setText(s.entry);    
	    sender.setTextAppearance(context, android.R.style.TextAppearance_Medium);
	    if(s.isDeleted)
	    	sender.setPaintFlags(sender.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
	    balance.setText(s.time.toString());
	    return rowView;
	  }
}
