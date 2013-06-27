package com.rhoadster91.raincheque;

import java.io.Serializable;

public class LogEntry implements Serializable
{
	private static final long serialVersionUID = -665876734076534496L;
	String entry = new String();
	String time = new String();
	int activityReference = -1;
	boolean isDeleted = false;
}
