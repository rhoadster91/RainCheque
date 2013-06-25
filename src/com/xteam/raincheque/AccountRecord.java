package com.xteam.raincheque;

import java.io.Serializable;

public class AccountRecord implements Serializable
{
	private static final long serialVersionUID = -8693621364691235101L;
	String name;
	int id = 0;
	int paid = 0;
	int worth = 0;
	int settlement = 0;
	
	public AccountRecord()
	{
		super();
		int x = RainChequeApplication.currentSession.accountList.size();
		if(x==0)
			this.id = 1;
		else
			this.id = RainChequeApplication.currentSession.accountList.get(x-1).id + 1;		
	}
	
	int getBalance()
	{
		return (paid - worth - settlement);
	}
}
