package com.xteam.raincheque;

import java.io.Serializable;
import java.util.ArrayList;

public class ActivityRecord implements Serializable
{
	private static final long serialVersionUID = -1142469630772284239L;
	
	int activityId = 0;
	String label;
	ArrayList<Integer> payers = new ArrayList<Integer>();
	ArrayList<Integer> payees = new ArrayList<Integer>();
	ArrayList<Integer> payerMoney = new ArrayList<Integer>();
	int settleWorth = 0;
	int changeAmount = 0, changeId = -1; 
	
	ActivityRecord()
	{
		super();
		if(RainChequeApplication.currentSession.activityList.size()==0)
			activityId = 1;
		else
			activityId = RainChequeApplication.currentSession.activityList.get(RainChequeApplication.currentSession.activityList.size() - 1).activityId + 1;
	}
	
	public boolean hasPayee(int id)
	{
		Integer myId = new Integer(id);		
		for(int x = 0;x < payees.size();x++)
		{
			if(myId.intValue()==payees.get(x).intValue())
				return true;
		}
		return false;
	}
	
	public boolean hasPayer(int id)
	{
		Integer myId = new Integer(id);		
		for(int x = 0;x < payers.size();x++)
		{
			if(myId.intValue()==payers.get(x).intValue())
				return true;
		}
		return false;
	}
	
	public int getMoneyAtPayer(int id)
	{
		Integer myId = new Integer(id);		
		for(int x = 0;x < payers.size();x++)
		{
			if(myId.intValue()==payers.get(x).intValue())
				return payerMoney.get(x).intValue();
		}
		return -1;
	}
}
