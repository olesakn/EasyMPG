package com.nickolesak.easympg;

import java.util.ArrayList;
import java.util.Calendar;

public class Statistics {

	private ArrayList<FillupNew> fillups;
	private ArrayList<Cost> costs;
	private VehicleNew vehicle;
	
	
	public Statistics (VehicleNew v) {
		vehicle = v;
		fillups = vehicle.getFillups();
		costs = vehicle.getCosts();
	}


	public double getDailyMiles() {
		if (fillups.size() >= 2) {
			Calendar firstDate = fillups.get(fillups.size()-1).getCalendar();
			Calendar recentDate = fillups.get(0).getCalendar();
			
			int numOfDays = ((recentDate.get(Calendar.YEAR) - firstDate.get(Calendar.YEAR)) * 365)
					+ ((recentDate.get(Calendar.MONTH) - firstDate.get(Calendar.MONTH)) * 30)
					+ ((recentDate.get(Calendar.DAY_OF_MONTH) - firstDate.get(Calendar.DAY_OF_MONTH)));
					
			int totalMiles = fillups.get(0).getOdometer() - fillups.get(fillups.size()-1).getOdometer();
			
			if (numOfDays > 0)
				return ((double) totalMiles) / ((double) numOfDays);
		}
		return 0;
	}
	
	
	public double getLastGallonPrice() {
		if (fillups.size() > 0)
				return fillups.get(0).getUnitCost();
		else
			return 0.0;
	}
	
	
	public double getWeeklyMiles() {
		return getDailyMiles() * 7;
	}
	
	
	public double getMonthlyMiles() {
		return getDailyMiles() * 30;
	}
	
	
	public double getAnnualMiles() {
		return getDailyMiles() * 365;
	}
	
	
	public double getAverageMPG() {
		double totalMPG = 0;
		int numFillups = 0;
		for (FillupNew f : fillups) {
			if (f.getMPG() > 0) {
				totalMPG += f.getMPG();
				numFillups++;
			}
		}
		return  totalMPG / ((double) numFillups);
	}
	
	
	public double getDailyFillupCost() {
		if (fillups.size() >= 2) {
			Calendar firstDate = fillups.get(fillups.size()-1).getCalendar();
			Calendar lastDate = fillups.get(0).getCalendar();
			
			int numOfDays = (Math.abs(firstDate.get(Calendar.YEAR) - lastDate.get(Calendar.YEAR)) * 365)
					 + (Math.abs(firstDate.get(Calendar.MONTH) - lastDate.get(Calendar.MONTH)) * 30)
					 + (Math.abs(firstDate.get(Calendar.DAY_OF_MONTH) - lastDate.get(Calendar.DAY_OF_MONTH)));
					
			double totalCost = 0;
			for (FillupNew f : fillups)
				totalCost += f.getTotalCost();
			
			if (numOfDays > 0)
				return totalCost / ((double) numOfDays); 
		}
		return 0;
	}
	
	
	public double getAnnualFillupCost() {
		return getDailyFillupCost() * 365;
	}
	
	
	public double getMonthlyFillupCost() {
		return getDailyFillupCost() * 30;
	}
	
	
	public double getWeeklyFillupCost() {
		return getDailyFillupCost() * 7;
	}
	
	
	public double getDailyExpenseCost() {
		if (costs.size() >= 2) {
			Calendar firstDate = costs.get(costs.size()-1).getDate();
			Calendar lastDate = costs.get(0).getDate();
			
			int numOfDays = (Math.abs(firstDate.get(Calendar.YEAR) - lastDate.get(Calendar.YEAR)) * 365)
					 + (Math.abs(firstDate.get(Calendar.MONTH) - lastDate.get(Calendar.MONTH)) * 30)
					 + (Math.abs(firstDate.get(Calendar.DAY_OF_MONTH) - lastDate.get(Calendar.DAY_OF_MONTH)));
					
			double totalCost = 0;
			for (Cost c : costs)
				totalCost += c.getCost();
			
			if (numOfDays > 0)
				return totalCost / ((double) numOfDays); 
		}
		return 0;
	}
	
	
	public double getAnnualExpenseCost() {
		return getDailyExpenseCost() * 365;
	}
	
	
	public double getMonthlyExpenseCost() {
		return getDailyExpenseCost() * 30;
	}
	
	
	public double getWeeklyExpenseCost() {
		return getDailyExpenseCost() * 7;
	}
}