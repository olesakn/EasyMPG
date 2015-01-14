package com.nickolesak.easympg;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import com.nickolesak.easympg.R;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TextView.OnEditorActionListener;

public class CreateCostActivity extends FragmentActivity implements OnClickListener {

	/** Private Variables */
	private static Button dateBtn, reminderBtn;
	private EditText typeET, descriptionET, costET, odometerET, reminderET;
	private Calendar calendar;
	private int editPos;
	
	/*********************************************************************
	 * On Create
	 ********************************************************************/
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_cost);
		
		editPos = getIntent().getIntExtra(CostsActivity.EXTRA_EDIT_POSITION, -1);

		ActionBar actionBar = getActionBar();
		actionBar.setLogo(getResources().getDrawable(R.drawable.easymp_logo_highres));
		actionBar.setDisplayHomeAsUpEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setTitle(MainActivity.currentVehicle.getName() + ": New Expense");
		if (editPos >= 0)
			actionBar.setTitle(MainActivity.currentVehicle.getName() + ": Edit Expense");
		initComponents();
	}
	

	/*********************************************************************
	 * Initialize GUI Components
	 ********************************************************************/
	private void initComponents() {
		
		typeET = (EditText)findViewById(R.id.cost_type);
		typeET.requestFocus();
		descriptionET = (EditText)findViewById(R.id.cost_description);
		costET = (EditText)findViewById(R.id.cost_cost);
		odometerET = (EditText) findViewById(R.id.cost_odometer);
		reminderET= (EditText) findViewById(R.id.cost_reminder);
		reminderET.setOnEditorActionListener(new OnEditorActionListener() {
		    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		        if(actionId == EditorInfo.IME_ACTION_DONE)
		        	saveCost();
		        return false;
		    }
		});

		dateBtn = (Button) findViewById(R.id.cost_date_button);
		dateBtn.setOnClickListener(this);
		reminderBtn = (Button) findViewById(R.id.cost_reminder_button);
		reminderBtn.setOnClickListener(this);
		
		if (editPos == -1)
			setCurrentDate(Calendar.getInstance());
		else {
			Cost c = MainActivity.currentVehicle.getCosts().get(editPos);
			setCurrentDate(c.getDate());
			typeET.setText(c.getType());
			descriptionET.setText(c.getDescription());
			costET.setText(new DecimalFormat("#0.00").format(c.getCost()));
			odometerET.setText("" + c.getOdometer());
			if (c.getReminder() > 0)
				reminderET.setText("" + c.getReminder());
		}
		
	}
	
	
	/*********************************************************************
	 * Save Cost and return to Main Activity
	 ********************************************************************/
	public void saveCost() {
		if (allDataFilled()) try {

			String dateStr = dateBtn.getText().toString();
			String type = typeET.getText().toString();
			int odom = Integer.parseInt(odometerET.getText().toString());
			double cost = Double.parseDouble(costET.getText().toString());
			
			String descrip = "";
			if (descriptionET.getText().length() > 0)
				descrip = descriptionET.getText().toString();
			int reminder = 0;
			if (reminderET.getText().length() > 0)
				reminder = Integer.parseInt(reminderET.getText().toString());
			
			Calendar cal = Calendar.getInstance();;
			try {
				cal.setTime(new SimpleDateFormat("MM/dd/yy").parse(dateStr));
			} catch (ParseException e) {
				Toast.makeText(getApplicationContext(), "Error parsing fill-up date\nPlease try again", Toast.LENGTH_SHORT).show();
			}
			
			ArrayList<Cost> costs = MainActivity.currentVehicle.getCosts();
			Cost c = new Cost(type, descrip, cost, cal, odom);
			c.setReminder(reminder);
			if (editPos == -1)
				costs.add(c);
			else
				costs.set(editPos, c);
			
			MainActivity.saveVehicles(this);
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
				saveCost();
				return true;
			case R.id.action_create_cancel:
				onBackPressed();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	
	/****************************************************************
	 * On Click Listener
	 ***************************************************************/
	public void onClick(View v) {
		if (v == dateBtn) 
			showDatePickerDialog(v);
		if (v == reminderBtn)
			showAboutReminderDialog();
		
	}
	
	
	/***************************************************************
	 * Show Dialog that explains Reminders
	 **************************************************************/
	private void showAboutReminderDialog() {
		AlertDialog.Builder adb = new AlertDialog.Builder(this);
		adb.setTitle("Set Reminder");
		String distanceUnit = SettingsActivity.getDistanceUnit();
		adb.setMessage("Use this optional feature to remind you about upcoming vehicle maintenance.\n\n"
				+ "Just enter how many " + distanceUnit + "s you want travel until you get reminded about the cost. "
				+ "Once you record a fillup or expense that exceeds that distance, the app will notify you about it.\n\n"
				+ "Ex: Set an oil change reminder for 5,000 " + distanceUnit + "s from now");
		adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		adb.setCancelable(true);
		adb.create().show();
		
	}
	
	
	/*********************************************************************
	 * On Back Button Pressed
	 ********************************************************************/
	public void onBackPressed() {
		Intent intent = new Intent(this, MainActivity.class);
		intent.putExtra(MainActivity.EXTRA_TAB_VALUE, MainActivity.COSTS_TAB);
		startActivity(intent);
		finish();
	}


	/*********************************************************************
	 * Verify that all necessary data for fill-up has been entered. 
	 ********************************************************************/
	public boolean allDataFilled () {
		boolean retVal = false;
		if (typeET.getText().length() <= 0)
			Toast.makeText(this, "Please provide the type", Toast.LENGTH_SHORT).show();
		else if (costET.getText().length() <= 0)
			Toast.makeText(this, "Please provide the cost", Toast.LENGTH_SHORT).show();
		else if (odometerET.getText().length() == 0)
			Toast.makeText(this, "Please provide the odometer", Toast.LENGTH_SHORT).show();
		else if (odometerET.getText().length() > 6)
			Toast.makeText(this, "Your odometer value is too high!", Toast.LENGTH_SHORT).show();
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
	

	/***************************************
	 * Show Date Picker Dialog
	 **************************************/
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
}
