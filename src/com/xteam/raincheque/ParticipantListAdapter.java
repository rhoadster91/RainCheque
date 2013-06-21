package com.xteam.raincheque;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ParticipantListAdapter extends ArrayAdapter<Object> 
{
	private final Context context;
	private final Object[] values;	
	AccountRecord s;
	
	public ParticipantListAdapter(Context context, Object[] objects) 
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
	    s = (AccountRecord)values[position];
	    sender.setText(s.name);	    	    
	    return rowView;
	  }
}
