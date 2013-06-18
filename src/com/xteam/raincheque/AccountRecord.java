package com.xteam.raincheque;

import java.io.Serializable;

public class AccountRecord implements Serializable
{
	private static final long serialVersionUID = -8693621364691235101L;
	String name;
	int paid;
	int worth;
	int settlement;
	public String getName() 
	{
		return name;
	}
	
	public void setName(String name) 
	{
		this.name = name;
	}
	
	public int getPaid() 
	{
		return paid;
	}
	
	public void setPaid(int paid) 
	{
		this.paid = paid;
	}
	
	public int getSettlement() 
	{
		return settlement;
	}
	
	public void setSettlement(int settlement) 
	{
		this.settlement = settlement;
	}

}
