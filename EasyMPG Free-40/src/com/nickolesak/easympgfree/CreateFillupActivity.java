package com.nickolesak.easympgfree;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import com.nickolesak.easympgfree.R;

import android.os.Bundle;
import android.app.ActionBar;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TextView.OnEditorActionListener;
import android.content.Intent;
import android.content.SharedPreferences;

public class CreateFillupActivity extends FragmentActivity {

	/** Private Variables */
	private static Button dateBtn;
	private EditText unitCostET, totalCostET, odometerET,memoTV;
	private Calendar calendar;
	private int editPos;
	
	/*********************************************************************
	 * On Create
	 ********************************************************************/
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_fillup);
		
		editPos = getIntent().getIntExtra(FillupsActivity.EXTRA_EDIT_POSITION, -1);

		ActionBar actionBar = getActionBar();
		actionBar.setLogo(getResources().getDrawable(R.drawable.easymp_logo_highres));
		actionBar.setDisplayHomeAsUpEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setTitle(MainActivity.currentVehicle.getName() + ": New Fillup");
		if (editPos >= 0)
			actionBar.setTitle(MainActivity.currentVehicle.getName() + ": Edit Fillup");

		initComponents();
	}
	

	/*********************************************************************
	 * Initialize GUI Components
	 ********************************************************************/
	private void initComponents() {
		
		dateBtn = (Button) findViewById(R.id.dateBtn);
		unitCostET = (EditText)findViewById(R.id.unitCostTxtBox);
		unitCostET.setHint("Cost Per " + SettingsActivity.getVolumeUnit());
		totalCostET = (EditText)findViewById(R.id.totalCostTxtBox);
		odometerET = (EditText)findViewById(R.id.milesTxtBox);
		memoTV = (EditText) findViewById(R.id.memoTxtBox);
		memoTV.setOnEditorActionListener(new OnEditorActionListener() {
		    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		        if(actionId == EditorInfo.IME_ACTION_DONE)
		        	saveFillup();
		        return false;
		    }
		});
		
		if (editPos == -1)
			setCurrentDate(Calendar.getInstance());
		else {
			FillupNew editF = MainActivity.currentVehicle.getFillups().get(editPos);
			setCurrentDate(editF.getCalendar());
			unitCostET.setText(new DecimalFormat("#0.000").format(editF.getUnitCost()));
			totalCostET.setText(new DecimalFormat("#0.00").format(editF.getTotalCost()));
			odometerET.setText("" + editF.getOdometer());
			((CheckBox) findViewById(R.id.isCompleteCB)).setChecked(editF.isCompleteFillup());
			memoTV.setText(editF.getMemo());
		}
		dateBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (v == dateBtn) 
					showDatePickerDialog(v);
			}
		});
		unitCostET.requestFocus();	// start user input here
	}
	
	
	/*********************************************************************
	 * Save Fill-up and return to Records Activity
	 ********************************************************************/
	public void saveFillup() {
		if (allDataFilled()) try {
			double unitCost = Double.parseDouble(unitCostET.getText().toString());
			
			SharedPreferences sp = getSharedPreferences(SettingsActivity.SAVED_SETTINGS, MODE_PRIVATE);
			if (sp.getBoolean(SettingsActivity.AUTO_ADD_BOOLEAN, true))
				try {
					String uc = new DecimalFormat("#0.000").format(unitCost);
					if (uc.charAt( uc.indexOf('.') + 3) == '0')	
					unitCost += .009;
				} catch (Exception e) {}
			
			double totalCost = Double.parseDouble(totalCostET.getText().toString());
			int odom = Integer.parseInt(odometerET.getText().toString());
			String dateStr = dateBtn.getText().toString();
			String memo = memoTV.getText().toString();
			boolean isComplete = ((CheckBox) this.findViewById(R.id.isCompleteCB)).isChecked();
			
			SimpleDateFormat df = new SimpleDateFormat("MM/dd/yy");
			Calendar c = new GregorianCalendar();
			try {
				c.setTime(df.parse(dateStr));
			} catch (ParseException e) {
				c = Calendar.getInstance();
				Toast.makeText(getApplicationContext(), "Error parsing fill-up date\nPlease try again", 
							Toast.LENGTH_SHORT).show();
			}
			
			VehicleNew v = MainActivity.currentVehicle;
			v.setOdometer(odom);
			ArrayList<FillupNew> fillups = v.getFillups();
			
			FillupNew fillup = new FillupNew(unitCost, totalCost, odom, c, memo, isComplete);
			if (editPos == -1)
				fillups.add(fillup);
			else
				fillups.set(editPos, fillup);
			
			MainActivity.saveVehicles(this);
			refreshMPGs(v);
			onBackPressed();
			
		} catch (Exception e) {
			Toast.makeText(this, "Problem Saving Data", Toast.LENGTH_LONG).show();
		}
	}
	
	/**************************************************************
	 * ActionBar Option Selected
	 *************************************************************/
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.action_bar_create, menu);
	    return super.onCreateOptionsMenu(menu);
	}
	
	/****************************************************************
	 * Option Item Selected
	 ****************************************************************/
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_create_confirm:
				saveFillup();
				return true;
			case R.id.action_create_cancel:
				onBackPressed();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	
	
	
	/*********************************************************************
	 * On Back Button Pressed
	 ********************************************************************/
	public void onBackPressed() {
		Intent intent = new Intent(this, MainActivity.class);
		intent.putExtra(MainActivity.EXTRA_TAB_VALUE, MainActivity.FILLUPS_TAB);
		startActivity(intent);
		finish();
	}


	/*********************************************************************
	 * Verify that all necessary data for fill-up has been entered. 
	 ********************************************************************/
	public boolean allDataFilled () {
		boolean retVal = false;
		if (unitCostET.getText().length() <= 0)
			Toast.makeText(this, "Unit cost cannot be blank!", Toast.LENGTH_SHORT).show();
		else if (totalCostET.getText().length() <= 0)
			Toast.makeText(this, "Total cost cannot be blank!", Toast.LENGTH_SHORT).show();
		else if (odometerET.getText().length() <= 0)
			Toast.makeText(this, "Odometer cannot be blank!", Toast.LENGTH_SHORT).show();
		else if (odometerET.getText().length() > 6)
			Toast.makeText(this, "Check your odometer value...", Toast.LENGTH_LONG).show();
		else
			retVal = true;
		
		return retVal;
	}
	
	
	/*********************************************************** 
	 * Date Picker Dialog
	 **********************************************************/
	private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
		public void onDateSet(DatePicker view, int pYear, int pMonth, int pDay) {
			Calendar c = Calendar.getInstance();
			c.set(pYear, pMonth, pDay);
			setCurrentDate(c); 
		}
	};
	

	/*********************************************************************
	 * On Create Dialog
	 ********************************************************************/
	protected Dialog onCreateDialog(int id) {	
	    if (id == 0)
	    	return new DatePickerDialog(this, dateSetListener, calendar.get(Calendar.YEAR), 
	    			calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE)); 
	    else
	    	return null;
	}
	
	
	/***************************************
	 * Date Dialog Picker
	 **************************************/
	public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the current date as the default date in the picker
			final Calendar c = Calendar.getInstance();
			int year = c.get(Calendar.YEAR);
			int month = c.get(Calendar.MONTH);
			int day = c.get(Calendar.DAY_OF_MONTH);
			return new DatePickerDialog(getActivity(), this, year, month, day);
		}
		public void onDateSet(DatePicker view, int year, int month, int day) {
			Calendar c = Calendar.getInstance();
			c.set(year,  month, day);
			setCurrentDate(c); 
		}
	}
	

	public void showDatePickerDialog(View v) {
	    DialogFragment newFragment = new DatePickerFragment();
	    newFragment.show(getSupportFragmentManager(), "datePicker");
	}
	
	
	/*********************************************************************
	 * Set Current Date
	 ********************************************************************/
	private static void setCurrentDate(Calendar c) {
		SimpleDateFormat df = new SimpleDateFormat("MM/dd/yy");
		dateBtn.setText(df.format(c.getTime()));
	}
	
	
	/******************************************************************
	 * Refresh MPG Values
	 *****************************************************************/
	private void refreshMPGs(VehicleNew v) {
		ArrayList<FillupNew> fillups = v.getFillups();
		if (fillups.size() > 0) {
			for (int i = 0; i <fillups.size()-1; i++) {
				FillupNew f = fillups.get(i);
				if (f.isCompleteFillup()) {
					int gallonCount= 0; 
					for (int j = i+1; j < fillups.size(); j++) {
						FillupNew next = fillups.get(j);
						if (next.isCompleteFillup()) {
							f.setMPG(f.getOdometer() - (next.getOdometer()), f.getGallons() + gallonCount);
							break;
						}
						else
							gallonCount += next.getGallons();
					}
				}
				else
					f.setMPG(FillupsActivity.PARTIAL_FILLUP);
			}
			fillups.get(fillups.size()-1).setMPG(FillupsActivity.FIRST_FILLUP);
		}
	}
}