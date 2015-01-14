package com.nickolesak.easympgfree;

import java.io.Serializable;
import java.util.Date;


/********************************************************
 * THIS IS THE OLD FILLUP!!!!!
 * DONT TOUCH IT
 *******************************************************/

















public class Fillup implements Serializable {

	private static final long serialVersionUID = 1L;
	private int year, month, day, odometer;
	private double unitCost, totalCost, gallons, mpg; 
	private String memo;
	private boolean completeFillup;

	public Fillup(double e, double f, int odom, Date d, String pmemo, boolean complete) {
			
		unitCost = e;
		totalCost = f;
		odometer = odom;
		year  = d.getYear();
		month = d.getMonth();
		day   = d.getDate();
		memo = pmemo;
		completeFillup = complete;
		gallons = totalCost / unitCost;
		// calculate mpg
		//if (prevodom > 0) {
		//	int miles = odom - prevodom;
		//	mpg = ((double) miles) / gallons;
		//}
		//else {
		mpg = 0;
		//}
	}
	
	
	public Fillup() {
		unitCost = 0;
		totalCost = 0;
		odometer = 0;
		year  = 0;
		month = 0;
		day   = 0;
		mpg = 0; 
		completeFillup = false;
	}
	
	
	@Override
	public String toString() {
		return "month" + "/" + day + "/" + year + ". " + totalCost + ". " + memo; 
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

	public Date getDate() {
		return new Date(year, month, day);
	}
	
	public String getStrDate() {
		return month + "-" + day + "-" + year;
	}
	
	public double getMPG() {
		return mpg;
	}
	
	public int getYear() {
		return year;
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
	
	public int compareTo(Object other) {
		int otherOdom = ((Fillup) other).getOdometer();
		if (otherOdom == odometer)
			return 0;
		if (otherOdom > odometer)
			return 1;
		
		return -1;
		
		
	}
}