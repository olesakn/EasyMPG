package com.nickolesak.easympg;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.ArrayList;

import com.nickolesak.easympg.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CustomAdapterCost extends BaseAdapter {

	private ArrayList<Cost> costs;
	Context context;

	CustomAdapterCost (Context c, ArrayList<Cost> costs) {
		this.costs = costs;
		context = c;
	}

	public int getCount() {
		return costs.size();
	}

	public Object getItem(int index) {
		return costs.get(index);
	}

	public long getItemId(int id) {
		return id;
	}

	public View getView(int index, View v, ViewGroup parent) {

		if (v == null) {
			LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.list_row_cost, null);
		}

		if (index % 2 == 1)
			v.setBackgroundColor(v.getResources().getColor(R.color.dim_foreground_inverse_holo_light));
		else
			v.setBackgroundColor(v.getResources().getColor(R.color.background_holo_light));
		
		TextView date = (TextView) v.findViewById(R.id.list_row_cost_date);
		TextView other = (TextView) v.findViewById(R.id.list_row_cost_other);
		TextView cost = (TextView) v.findViewById(R.id.list_row_cost_cost);
		
		Cost c = costs.get(index);
		
		Calendar cal = c.getDate();
		int day = cal.get(Calendar.DAY_OF_MONTH);
		int month = cal.get(Calendar.MONTH);
		int year = cal.get(Calendar.YEAR);
		date.setText(getNameOfMonth(month) + " " + day + getDayOfMonthEnding(day));
		if (Calendar.getInstance().get(Calendar.YEAR) != year)
			date.append(", " + year);
		date.setTextColor(v.getResources().getColor(R.color.blue));
		
		if (c.getOdomReminder() <= MainActivity.currentVehicle.getOdometer() && !c.isReminded()) {
			cost.setText("");
			cost.setBackgroundResource(R.drawable.ic_action_warning);
			cost.setMinimumWidth(100);
			cost.setWidth(100);
			cost.setMaxWidth(100);
			cost.setHeight(100);
			cost.setMaxHeight(100);
			cost.setMinHeight(100);
		}
		else 
		{
			String currency = SettingsActivity.getCurrency();
			if (currency.length() == 1)
				cost.setText(currency + new DecimalFormat("#0.00").format(c.getCost()));	// singular currencies like $
			else
				cost.setText(new DecimalFormat("#0.00").format(c.getCost()) + " " + currency);	// post-fix currencies
				
		}
		
		other.setText(c.getType() + "\n" + c.getDescription() + "\n");
		if (c.getOdometer() > 0)
			other.append("Odometer: " + c.getOdometer());
		
		return v;
	}


	private String getNameOfMonth(int num) {
		switch (num) {
			case 0: return "January";
			case 1: return "Febuary";
			case 2: return "March";
			case 3: return "April";
			case 4: return "May";
			case 5: return "June";
			case 6: return "July";
			case 7: return "August";
			case 8: return "September";
			case 9: return "October";
			case 10: return "November";
			case 11: return "December";
			default: return null; 
		}
	}

	private String getDayOfMonthEnding(int num) {
		if (num > 10)
			num %= 10;
		switch (num) {
			case 1: return "st";
			case 2: return "nd";
			case 3: return "rd";
			default: return "th"; 
		}
	}
}
