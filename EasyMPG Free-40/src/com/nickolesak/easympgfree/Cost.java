package com.nickolesak.easympgfree;

import java.io.Serializable;
import java.util.Calendar;

public class Cost implements Serializable, Comparable<Cost> {
	
	private static final long serialVersionUID = 1L;
	private String type, description; 
	private int odometer, reminder;
	private double cost;
	private Calendar date;
	private boolean reminded, notified;

	
	public Cost(String type, String descrip, double cost, Calendar c, int odometer) {
		this.type = type;
		this.description = descrip;
		this.cost = cost;
		this.date = c;
		this.odometer = odometer;
		this.reminder = 0;
		reminded = true;
		notified = true;
	}

	
	
	public boolean isNotified() {
		return notified;
	}



	public void setNotified(boolean notified) {
		this.notified = notified;
	}



	public boolean isReminded() {
		return reminded;
	}


	public void setReminded(boolean reminded) {
		this.reminded = reminded;
	}


	public int getReminder() {
		return reminder;
	}
	
	
	public int getOdomReminder() {
		if (reminder > 0 && !reminded)
			return odometer + reminder;
		else
			return 999999999;
	}


	public void setReminder(int reminder) {
		this.reminder = reminder;
		this.reminded = false;
		this.notified = false;
	}


	public int getOdometer() {
		return odometer;
	}


	public void setOdometer(int odometer) {
		this.odometer = odometer;
	}


	public Calendar getDate() {
		return date;
	}

	
	public void setDate(Calendar date) {
		this.date = date;
	}


	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}


	public double getCost() {
		return cost;
	}


	public void setCost(double cost) {
		this.cost = cost;
	}


	public int compareTo(Cost c) {
		int otherOdom = c.getOdometer();
		if (otherOdom == this.odometer)
			return 0;
		if (otherOdom > this.odometer)
			return 1;
		return -1;
	}
}
