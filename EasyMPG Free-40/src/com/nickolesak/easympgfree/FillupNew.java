package com.nickolesak.easympgfree;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class FillupNew implements Serializable, Comparable<FillupNew> {

	private static final long serialVersionUID = 1L;
	private int odometer;
	private double unitCost, totalCost, gallons, mpg; 
	private Calendar calendar;
	private String memo;
	private boolean completeFillup;

	public FillupNew(double e, double f, int odom, Calendar c, String pmemo, boolean complete) {
			
		unitCost = e;
		totalCost = f;
		odometer = odom;
		calendar = c;
		memo = pmemo;
		completeFillup = complete;
		gallons = totalCost / unitCost;
		mpg = 0;
	}
	
	
	public FillupNew() {
		unitCost = 0;
		totalCost = 0;
		odometer = 0;
		calendar = null;
		mpg = 0; 
		completeFillup = false;
	}
	
	
	@Override
	public String toString() {
		SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
		df.setCalendar(calendar);
		return df.format(calendar.getTime()) + ". " + totalCost + ". " + memo; 
	}

	public int getOdometer() {
		return odometer;
	}
	
	public double getGallons() {
		return gallons;
	}

	public double getUnitCost() {
		return unitCost;
	}

	public double getTotalCost() {
		return totalCost;
	}
	
	public Calendar getCalendar() {
		return calendar;
		
	}
	
	public String getStrDate() {
		SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
		df.setCalendar(calendar);
		return df.format(calendar.getTime());
	}
	
	public double getMPG() {
		return mpg;
	}
	
	public int getYear() {
		return calendar.get(Calendar.YEAR);
	}
	
	public String getMemo() {
		return memo;
	}
	
	public void setMPG (double mpg) {
		this.mpg = mpg;
	}
	
	public void setMPG (int miles, double gallons) {
		mpg = ((double) miles) / gallons;
	}
	
	public void calcMPG (int prevOdom) {
		double gallons = totalCost / unitCost;	// instance variables
		int miles = odometer - prevOdom; 
		mpg = ((double) miles) / gallons; 
		
	}
	
	public void calcMPG (int prevOdom, double totalCost, double unitCost) {
		totalCost = this.totalCost;
		unitCost = this.unitCost;
		double gallons = totalCost / unitCost;	// parameters
		int miles = odometer - prevOdom; 
		mpg = ((double) miles) / gallons; 
	}

	public void setOdometer(int odometer) {
		this.odometer = odometer;
	}

	public void setUnitCost(float unitCost) {
		this.unitCost = unitCost;
	}

	public void setTotalCost(float totalCost) {
		this.totalCost = totalCost;
	}
	
	public boolean isCompleteFillup() {
		return completeFillup;
	}
	
	public void setCompleteFillup(boolean p) {
		completeFillup = p; 
	}
	
	public int compareTo(FillupNew f) {
		int otherOdom = f.getOdometer();
		if (otherOdom == odometer)
			return 0;
		if (otherOdom > odometer)
			return 1;
		return -1;
	}
}