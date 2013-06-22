package com.xteam.raincheque;

import java.io.Serializable;

public class AccountRecord implements Serializable
{
	private static final long serialVersionUID = -8693621364691235101L;
	String name;
	int paid = 0;
	int worth = 0;
	int settlement = 0;
	
	int getBalance()
	{
		return (paid - worth - settlement);
	}
}
