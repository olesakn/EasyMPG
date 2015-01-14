package com.nickolesak.easympgfree;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

public class CreateVehicleActivity extends Activity {
	
	public static final String EXTRA_IS_EDIT = "com.nickolesak.easympg.EXTRA_IS_EDIT";
	
	private EditText makeET, modelET, yearET, nameET;
	private ArrayList<VehicleNew> vehicles;
	private boolean isEdit;
	
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_vehicle);

		vehicles = MainActivity.vehicles;
		isEdit = getIntent().getBooleanExtra(EXTRA_IS_EDIT, false);
		
		getActionBar().setLogo(R.drawable.easymp_logo_highres);
		getActionBar().setDisplayHomeAsUpEnabled(false);
		if (!isEdit)
			getActionBar().setTitle("Add New Vehicle");
		else
			getActionBar().setTitle("Edit " + MainActivity.currentVehicle.getName());
	
		nameET = (EditText) this.findViewById(R.id.vehicle_name);
		nameET.requestFocus();
		makeET = (EditText) this.findViewById(R.id.vehicle_make);
		modelET = (EditText) this.findViewById(R.id.vehicle_model);
		yearET = (EditText) this.findViewById(R.id.vehicle_year);
		yearET.setOnEditorActionListener(new OnEditorActionListener() {
		    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		        if(actionId == EditorInfo.IME_ACTION_DONE)
		            saveVehicle();
		        return false;
		    }
		});
		
		if (isEdit && vehicles != null && MainActivity.currentVehicle != null) {
			nameET.setText(MainActivity.currentVehicle.getName());
			makeET.setText(MainActivity.currentVehicle.getMake());
			modelET.setText(MainActivity.currentVehicle.getModel());
			yearET.setText(MainActivity.currentVehicle.getYear() + "");
		}
	}
	
	
	private void saveVehicle() {
		try {
			String name = nameET.getText().toString();
			String make = makeET.getText().toString();
			String model = modelET.getText().toString();
			String yearStr = yearET.getText().toString();
			int year = (yearStr.length() > 0) ? Integer.parseInt(yearStr) : 0;
			
			if (name.length() == 0) 
			{
				Toast.makeText(getApplicationContext(), "Name is required!", Toast.LENGTH_SHORT).show();
				return;
			}
			
			if (!isEdit)
				vehicles.add(new VehicleNew(name, make, model, year));
			
			else 
			{
				MainActivity.currentVehicle.setName(name);
				MainActivity.currentVehicle.setMake(make);
				MainActivity.currentVehicle.setModel(model);
				MainActivity.currentVehicle.setYear(year);
				
			}
			
			MainActivity.saveVehicles(getApplicationContext());
			onBackPressed();
			
		} catch (Exception e) {
			Toast.makeText(getApplicationContext(), "Error: Check Your Input Data", Toast.LENGTH_SHORT).show();
		}	
	}
	
	
	/**************************************************************
	 * ActionBar Option Selected
	 *************************************************************/
	public boolean onCreateOptionsMenu(Menu menu) {
	    // Inflate the menu items for use in the action bar
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
			saveVehicle();
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
}