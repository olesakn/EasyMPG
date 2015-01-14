package com.nickolesak.easympgfree;

import java.text.DecimalFormat;
import java.util.ArrayList;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class StatsActivity extends Fragment {

	private ArrayList<VehicleNew> vehicles;
	private TextView avgMPG, dayMiles, weekMiles, monthMiles, yearMiles, dayCost,  weekCost,  monthCost,  yearCost;
	private Statistics stats;
	private View rootView;

	
	/****************************************************************
	 * On Create
	 ***************************************************************/	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
					Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.statistics, container, false);
		
		vehicles = MainActivity.vehicles;
		initGUIComponents();
		refreshData();
		
		return rootView;
	}


	/****************************************************************
	 * Refresh Data
	 ***************************************************************/
	private void refreshData() {
		if (vehicles.size() > 0)
			stats = new Statistics(MainActivity.currentVehicle);
		else
			stats = new Statistics(new VehicleNew("","","",0));
		
		((TextView)rootView.findViewById(R.id.stats_distance_textview)).setText("\t\t"+SettingsActivity.getDistanceUnit()+"s");
		((TextView)rootView.findViewById(R.id.stats_cost_textview)).setText("Cost (" + SettingsActivity.getCurrency() +")");

		DecimalFormat df = new DecimalFormat("#0.0");
		double mpg = stats.getAverageMPG();
		if (mpg >= 0)
			avgMPG.setText("Average = " + df.format(mpg) + " " + SettingsActivity.getRatioUnitAbbrevCaps());
		else
			avgMPG.setText("Average = 0.0 " + SettingsActivity.getRatioUnitAbbrevCaps());
		
		dayMiles.setText(df.format(stats.getDailyMiles()));
		weekMiles.setText(df.format(stats.getWeeklyMiles()));
		monthMiles.setText(df.format(stats.getMonthlyMiles()));
		yearMiles.setText(df.format(stats.getAnnualMiles()));
		
		df = new DecimalFormat("#0.00");
		dayCost.setText(df.format(stats.getDailyFillupCost()));
		weekCost.setText(df.format(stats.getWeeklyFillupCost()));
		monthCost.setText(df.format(stats.getMonthlyFillupCost()));
		yearCost.setText(df.format(stats.getAnnualFillupCost()));
	}
	
	
	/*****************************************************************
	 * Initialize GUI Components
	 ****************************************************************/
	private void initGUIComponents() {
		int c = getResources().getColor(R.color.blue_accent);
		int c_acc = getResources().getColor(R.color.green);
		
		avgMPG = (TextView) rootView.findViewById(R.id.stats_avg_mpg);
		
		(dayMiles = (TextView) rootView.findViewById(R.id.stats_miles_day)).setTextColor(c);
		(weekMiles = (TextView) rootView.findViewById(R.id.stats_miles_week)).setTextColor(c);
		(monthMiles = (TextView) rootView.findViewById(R.id.stats_miles_month)).setTextColor(c);
		(yearMiles = (TextView) rootView.findViewById(R.id.stats_miles_year)).setTextColor(c);
		
		(dayCost = (TextView) rootView.findViewById(R.id.stats_cost_day)).setTextColor(c_acc);
		(weekCost = (TextView) rootView.findViewById(R.id.stats_cost_week)).setTextColor(c_acc);
		(monthCost = (TextView) rootView.findViewById(R.id.stats_cost_month)).setTextColor(c_acc);
		(yearCost = (TextView) rootView.findViewById(R.id.stats_cost_year)).setTextColor(c_acc);
	}
}