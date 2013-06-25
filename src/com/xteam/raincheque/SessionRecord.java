package com.xteam.raincheque;

import java.io.Serializable;
import java.util.ArrayList;

public class SessionRecord implements Serializable
{
	private static final long serialVersionUID = 3627507367084333941L;
	int total;
	int sessionID;
	String label;
	boolean isActive = true;
	ArrayList<AccountRecord> accountList;
	ArrayList<LogEntry> sessionLog = new ArrayList<LogEntry>();
	ArrayList<ActivityRecord> activityList = new ArrayList<ActivityRecord>();
}
