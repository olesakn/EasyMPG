package com.nickolesak.easympg;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;


/********************************************************
 * THIS IS THE OLD VEHICLE CLASS!!!!!
 * DONT TOUCH IT
 *******************************************************/


















public class Vehicle implements Serializable {
	
	

	private static final long serialVersionUID = 1L;
	
	private String name, make, model;
	private int year, odometer;
	private ArrayList<Fillup> fillups;
	
	public Vehicle (String name, String make, String model, int year) {
		this.name = name;
		this.year = year;
		this.make = make;
		this.model = model;
		odometer = 0;
		fillups = new ArrayList<Fillup>();
	}
	
	public VehicleNew convertToNewVehicleFormat () {
		VehicleNew v = new VehicleNew(name, make, model, year);
		v.setOdometer(odometer);
		v.setCosts(new ArrayList<Cost>());
		ArrayList<FillupNew> newFillups = new ArrayList<FillupNew>();
		for (Fillup f : fillups) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(f.getDate());
			FillupNew newFillup = new FillupNew(f.getUnitCost(), f.getTotalCost(), f.getOdometer(), 
					cal, f.getMemo(), f.isCompleteFillup());
			newFillups.add(newFillup);
		}
		v.setFillups(newFillups);
		return v;
	}
	
	
	public String getName() {
		return name;
	}
	
	public void setName(String str) {
		name = str;
	}
	
	public int getOdometer() {
		return odometer;
	}
	
	public ArrayList<Fillup> getFillups() {
		return fillups;
	}
	
	public void setOdometer(int o) {
		odometer = o;
	}
	
	public void addFillup(Fillup f) {
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
