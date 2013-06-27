package com.rhoadster91.raincheque;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class SessionListAdapter extends ArrayAdapter<Object> 
{
	private final Context context;
	private final Object[] values;	
	SessionRecord s;
	
	public SessionListAdapter(Context context, Object[] objects) 
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
	    TextView partics = (TextView) rowView.findViewById(R.id.sessionMembers);	    
	    s = (SessionRecord)values[position];
	    sender.setText(s.label);
	    int x = s.accountList.size();
	    String participants = new String();
	    for(int i=0;i<x;i++)
	    {
	    	participants = participants.concat(s.accountList.get(i).name);
	    	if(i<x-2)
	    		participants = participants.concat(", ");
	    	if(i==x-2)
	    		participants = participants.concat(" and ");
	    }
	    partics.setText(participants);
	    return rowView;
	  }
}
