package com.nickolesak.easympg;

import java.io.Serializable;
import java.util.ArrayList;

public class VehicleNew implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String name, make, model;
	private int year;
	private ArrayList<FillupNew> fillups;
	private ArrayList<Cost> costs;
	
	public VehicleNew (String name, String make, String model, int year) {
		this.name = name;
		this.year = year;
		this.make = make;
		this.model = model;
		fillups = new ArrayList<FillupNew>();
		costs = new ArrayList<Cost>();
	}
	
	
	public void setFillups(ArrayList<FillupNew> fillups) {
		this.fillups = fillups;
	}
	
	public ArrayList<Cost> getCosts() {
		return costs;
	}
	
	public void setCosts(ArrayList<Cost> costs) {
		this.costs = costs;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String str) {
		name = str;
	}
	
	public int getOdometer() {
		int maxFillupOdom = 0, maxCostOdom = 0;
		if (fillups.size() > 0)
			maxFillupOdom = fillups.get(0).getOdometer();
		if (costs.size() > 0)
			maxCostOdom = costs.get(0).getOdometer();
		
		if (maxFillupOdom > maxCostOdom)
			return maxFillupOdom;
		else
			return maxCostOdom;
	}
	
	public ArrayList<FillupNew> getFillups() {
		return fillups;
	}
	
	public void setOdometer(int o) {}
	
	public void addFillup(FillupNew f) {
		fillups.add(f);
	}


	public String getMake() {
		return make;
	}


	public void setMake(String make) {
		this.make = make;
	}


	public String getModel() {
		return model;
	}


	public void setModel(String model) {
		this.model = model;
	}


	public int getYear() {
		return year;
	}


	public void setYear(int year) {
		this.year = year;
	}
	
	public String toString() {
		return year + " " + make + " " + model;
	}
}
