package com.nickolesak.easympgfree;

import java.util.Calendar;
import java.text.DecimalFormat;
import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CustomAdapterFillup extends BaseAdapter {

	private ArrayList<FillupNew> fillups;
    Context context;
    
    CustomAdapterFillup (Context c, ArrayList<FillupNew>f) {
        fillups = f;
        context = c;
    }
	
	@Override
	public int getCount() {
		return fillups.size();
	}

	@Override
	public Object getItem(int index) {
		return fillups.get(index);
	}

	@Override
	public long getItemId(int id) {
		return id;
	}

	@Override
	public View getView(int index, View v, ViewGroup parent) {

		if (v == null) {
			LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.list_row_fillup, null);
		}

		if (index % 2 == 1)
			v.setBackgroundColor(v.getResources().getColor(R.color.dim_foreground_inverse_holo_light));
		else
			v.setBackgroundColor(v.getResources().getColor(R.color.background_holo_light));
		
		TextView date = (TextView) v.findViewById(R.id.list_row_fillup_date);
		TextView other = (TextView) v.findViewById(R.id.list_row_fillup_other);
		TextView mpg = (TextView) v.findViewById(R.id.list_row_fillup_mpg);

		FillupNew f = fillups.get(index);
		
		Calendar c = f.getCalendar();
		//if (c != null)
		String month = getNameOfMonth(c.get(Calendar.MONTH));
		date.setText(month + " " + c.get(Calendar.DAY_OF_MONTH) + getDayOfMonthEnding(c.get(Calendar.DAY_OF_MONTH)));
		date.setTextColor(v.getResources().getColor(R.color.blue));
		
		other.setText("Odometer:\t" + f.getOdometer()); 
		String currency = SettingsActivity.getCurrency();
		if (currency.length() == 1) {
			other.append("\nUnit Price:\t\t\b" + currency + new DecimalFormat("#0.00").format(f.getUnitCost()));
			other.append("\nTotal Cost:\t\b" + currency + new DecimalFormat("#0.00").format(f.getTotalCost()));
		}
		else {
			other.append("\nUnit Price:\t\t\b" + new DecimalFormat("#0.00").format(f.getUnitCost()) + " " + currency);
			other.append("\nTotal Cost:\t\b" + new DecimalFormat("#0.00").format(f.getTotalCost()) + " " + currency);
		}
		if (f.getMemo().length() > 0)
			other.append("\nMemo:\t\t" + f.getMemo());

		mpg.setTextSize(18);
		Statistics stats = new Statistics (MainActivity.currentVehicle);
		
		if (f.getMPG() == FillupsActivity.PARTIAL_FILLUP) 
		{
			mpg.setText("Partial");
			mpg.setBackgroundDrawable(v.getResources().getDrawable(R.drawable.rounded_blue));
		}
		else if (f.getMPG() == FillupsActivity.FIRST_FILLUP) 
		{
			mpg.setText("First");
			mpg.setBackgroundDrawable(v.getResources().getDrawable(R.drawable.rounded_blue));
		}
		
		else 
		{
			mpg.setText(new DecimalFormat("#0.0").format(f.getMPG()) + "\n" + SettingsActivity.getRatioUnitAbbrev());
			double diff = f.getMPG() - stats.getAverageMPG();
			
			if (diff >= 0) 
				mpg.setBackgroundDrawable(v.getResources().getDrawable(R.drawable.rounded_green));
			else if (diff >= -2) 
				mpg.setBackgroundDrawable(v.getResources().getDrawable(R.drawable.rounded_orange));
			else
				mpg.setBackgroundDrawable(v.getResources().getDrawable(R.drawable.rounded_red));
		}
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




