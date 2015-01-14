package com.nickolesak.easympgfree;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Currency;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.os.Environment;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class SettingsActivity extends Activity {
	
	public static final String SAVED_SETTINGS = "com.nickolesak.easympg.SAVED_SETTINGS";
	public static final String AUTO_ADD_BOOLEAN = "com.nickolesak.easympg.AUTO_ADD_BOOLEAN";

	private final static CharSequence[] unitsList = {"Miles/Gallon", "Kilometers/Liter", "Liters/mil"};
	private final String[] listViewTextON  = {" Auto-Add 9/10ths Penny: ON", " Export Data", " Import Data", " Change Units", " Change Currency", " Suggest New Features"," Report A Bug", " About"}; 
	private final String[] listViewTextOFF = {" Auto-Add 9/10ths Penny: OFF"," Export Data", " Import Data", " Change Units", " Change Currency", " Suggest New Features"," Report A Bug", " About"}; 
	private boolean autoAdd;
	private ListView listView;
	

	/****************************************************************
	 * On Create
	 ***************************************************************/
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 
		this.setContentView(R.layout.settings);
		
		getActionBar().setTitle("Options");
		getActionBar().setLogo(getResources().getDrawable(R.drawable.easymp_logo_highres));
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		autoAdd = getSharedPreferences(SAVED_SETTINGS, MODE_PRIVATE).getBoolean(AUTO_ADD_BOOLEAN, true);
		
		ArrayAdapter<String> adapter;
		if (autoAdd)
			adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listViewTextON);
		else
			adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listViewTextOFF);
		listView = (ListView) this.findViewById(R.id.settings_list_view);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener(){
			public void onItemClick(AdapterView<?> a, View view, int pos, long id) {
				switch (pos) {
					case 0: autoAddClicked(); 	break;
					case 1: exportData();	 	break;
					case 2: importData(); 		break;
					case 3: changeUnits();		break;
					case 4:	changeCurrency(); 	break;
					case 5: suggestFeatures(); 	break;
					case 6: reportBug(); 		break;
					case 7: about(view.getContext()).show(); break;
				}
			}
		});
	}
	
	/**************************************************
	 * Get Volume Unit
	 *************************************************/
	public static String getVolumeUnit() {
		switch (MainActivity.unitSelectionIndex) {
			case 0: return "Gallon";
			case 1:
			case 2: return "Liter";
			default: return "";
		}
	}
	

	/**************************************************
	 * Get Distance Unit
	 *************************************************/
	public static String getDistanceUnit() {
		switch (MainActivity.unitSelectionIndex) {
			case 0: return "Mile";
			case 1:return "Kilometer";
			case 2: return "Mil";
			default: return "distance unit";
		}
	}

	
	/**************************************************
	 * Get Distance Unit without including Mils
	 *************************************************/
	public static String getDistanceUnitNoMil() {
		switch (MainActivity.unitSelectionIndex) {
			case 0: return "Mile";
			default: return "Kilometer";
		}
	}
	

	/**************************************************
	 * Get Ratio Unit
	 *************************************************/
	public static String getRatioUnit() {
		return (unitsList[MainActivity.unitSelectionIndex]).toString();
	}
	

	/**************************************************
	 * Get Abbreviated Unit Ratios
	 *************************************************/
	public static String getRatioUnitAbbrev() {
		switch (MainActivity.unitSelectionIndex) {
			case 0: return "mpg";
			case 1: return "km/L";
			case 2: return "L/mil";
			default: return "";
		}
	}
	
	/**************************************************
	 * Get Abbreviated Unit Ratios
	 *************************************************/
	public static String getRatioUnitAbbrevCaps() {
		switch (MainActivity.unitSelectionIndex) {
			case 0: return "MPG";
			case 1: return "Km/L";
			case 2: return "L/mil";
			default: return "";
		}
	}

	/**********************************************************************
	 * Dialog used to change units
	 *********************************************************************/
	private void changeUnits() {
		AlertDialog.Builder adb = new AlertDialog.Builder(this);
		adb.setTitle("Choose your Units");
		adb.setSingleChoiceItems(unitsList, MainActivity.unitSelectionIndex, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int index) {
            	MainActivity.unitSelectionIndex = index;
            	getSharedPreferences(MainActivity.SAVED_SETTINGS, MODE_PRIVATE).edit().putInt(MainActivity.UNIT_SELECTION_INDEX, index).commit();
            }
        });
		adb.setPositiveButton("OK", new OnClickListener() {
			public void onClick(DialogInterface d, int arg1) {
				d.cancel();
			}
		});
		adb.create().show();
	}
	
	public static String getCurrency() {
		CharSequence[] currencies = getCurrencyList();
		return currencies[MainActivity.currencySelectionIndex].toString();
	}
	
	private static CharSequence[] getCurrencyList() {
		ArrayList<String> localeList = new ArrayList<String>();
		String defaultSymbol = Currency.getInstance(Locale.getDefault()).getSymbol();
		
		// put default & common symbols at top of list
		localeList.add(defaultSymbol);
		if (!defaultSymbol.equals("$"))
			localeList.add("$");
		if (!defaultSymbol.equals("€"))
			localeList.add("€");
		if (!defaultSymbol.equals("£"))
			localeList.add("£");
		
		for (Locale l : Locale.getAvailableLocales()) {
			try {
				Currency c = Currency.getInstance(l);
				if (!localeList.contains(c.getSymbol()))
					localeList.add(c.getSymbol());
			} catch (Exception e) {}
		}
		
		Collections.sort(localeList, new Comparator<String>() {
			public int compare(String one, String two) {
				if (two.length() == 1)
					return 1;
				if (two.length() == 2)
					return 0;
				return -1;
			}
		});
		
		CharSequence[] locales = new CharSequence[localeList.size()];
		for (int i = 0; i < localeList.size(); i++)
			locales[i] = localeList.get(i);
		
		return locales;
	}
	
	
	/**********************************************************************
	 * Dialog used to change currency
	 *********************************************************************/
	private void changeCurrency() {
		AlertDialog.Builder adb = new AlertDialog.Builder(this);
		adb.setTitle("Choose your currency");
		adb.setSingleChoiceItems(getCurrencyList(), MainActivity.currencySelectionIndex, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int index) {
            	MainActivity.currencySelectionIndex = index;
            	getSharedPreferences(MainActivity.SAVED_SETTINGS, MODE_PRIVATE).edit().putInt(MainActivity.CURRENCY_SELECTION_INDEX, index).commit();
            }
        });
		adb.setPositiveButton("OK", new OnClickListener() {
			public void onClick(DialogInterface d, int arg1) {
				d.cancel();
				CharSequence[] list = getCurrencyList();
				String s = list[MainActivity.currencySelectionIndex].toString();
				if (!s.equals("$") && autoAdd)
					showPennyDialog();
			}
		});
		adb.create().show();
	}
	
	
	/**********************************************************************
	 * Dialog used to change currency
	 *********************************************************************/
	private void showPennyDialog() {
		AlertDialog.Builder adb = new AlertDialog.Builder(this);
		adb.setTitle("Auto-Add 9/10ths?");
		adb.setMessage("If your local gas stations do not charge an extra 9/10ths of a unit of currency, then you should " +
				"disable the \"Auto-Add 9/10ths Penny\" option");
		adb.setPositiveButton("OK", new OnClickListener() {
			public void onClick(DialogInterface d, int arg1) {
				d.cancel();
			}
		});
		adb.create().show();
	}
	

	/**************************************************
	 * Take Action when click Auto-Add
	 *************************************************/
	private void autoAddClicked() {
		ArrayAdapter<String> newAdapter;
		autoAdd = !autoAdd;
		if (autoAdd)
			newAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listViewTextON);
		else
			newAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listViewTextOFF);
		listView.setAdapter(newAdapter);
		listView.refreshDrawableState();
		SharedPreferences sp = getSharedPreferences(SAVED_SETTINGS, MODE_PRIVATE);
		sp.edit().putBoolean(AUTO_ADD_BOOLEAN, autoAdd).commit();
	}
		

	/**************************************************
	 * On Options Item Selected
	 *************************************************/
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home)
			onBackPressed();
		return true;
	}
	

	/**************************************************
	 * On Back Pressed
	 *************************************************/
	public void onBackPressed() {
		startActivity(new Intent(getApplicationContext(), MainActivity.class));
		finish();
	}
	

	/**************************************************
	 * Show About Dialog
	 *************************************************/
	public static AlertDialog.Builder about(Context context) 
	{
		try {
		
			AlertDialog.Builder adb = new AlertDialog.Builder(context);
			adb.setTitle("Welcome to EasyMPG!");
			adb.setMessage(
					"Version: " + context.getString(R.string.app_version_name) + "\n" +
					"Release: " + context.getString(R.string.app_version_date) + "\n" +
					"Author: Nicholas Olesak\n\n" +
					
					"EasyMPG offers an easy way to keep a record of your vehicle's fuel expenses and other costs. " +
					"Using information from your gas fillups, EasyMPG calculates your vehicle's gas mileage and " +
					"provides statistics about your mileage and expenses. It keeps a record of your vehicle expenses (gas, " +
					"oil changes, etc) all in one convenient place and can even remind you when you have an upcoming expense.\n\n\n" +
					
					"* How To Use *\n" +
					"When you get gas, just add a fillup for the vehicle that you're using. You only need the " +
					"odometer, price per gallon, and the total fillup cost - EasyMPG will do the " +
					"rest! Note: In order to calculate your fuel economy, you need to fill your tank completely (if you don't, you " +
					"need to uncheck the \"Filled Tank Completely\" checkbox).\nAfter every complete fillup, you will immedietely " +
					"see your latest gas mileage.\n\n" +
					
					"There are 5 main aspects of the application:\n" +
					"\t\t- Vehicles\n" +
					"\t\t- Fillups\n" +
					"\t\t- Expenses\n" +
					"\t\t- Statistics\n" +
					"\t\t- Trip Calculator\n\n" +
					
					"The FILLUPS tab is where you can view your recent fillup information and add new fillups.\n\n" +
					"The EXPENSES tab is used to records other costs for your vehicle (such as oil changes)\n\n" +
					"The STATS tab displays statistics about your vehicle mileage and expenses.\n\n" +
					"The TRIP CALC tab is used to calculate roughly how much a trip will cost you. You can enter starting and " +
					"ending addresses and the program will look up the driving distance & calculate the estimated cost " +
					"to drive there using your vehicle's average gas mileage.\n\n" +
					
					"Other features include:\n" +
					"\t\t- Multiple currencies and units\n" +
					"\t\t- Designed for ICS standards\n" +
					"\t\t- Swipe to change tabs\n" + 
					"\t\t- Import/Export Data\n" + 
					"\t\t- Auto-Add 9/10ths of penny\n" + 
					"\t\t- Simple & Colorful UI\n" + 
					"\t\t- Unlimited Vehicles (paid \n\t\t\t\tversion only)\n" + 
					"\t\t- Unlimited Fillups\n" +
					"\t\t- Unlimited Expenses\n" +
					"\t\t- Expense Reminders\n" +
					"\t\t- Stats & Graphs\n" +
					"\t\t- And more to come!\n\n" +
					
					"If you have any questions or want to submit an idea for a new feature, choose 'Suggest Feature' " +
					"in the options menu. You can also report a bug from the options menu\n\n" +
					
					"Thank you for using EasyMPG!\nI will do my best to keep the app bug-free, up-to-date, and as feature-packed " +
					"as possible.\nI am more than willing to listen to your ideas and incorporate them into the app!\n" +
					"Enjoy using it and don't forget to suggest it to your friends and RATE IT on the Google Play store!\n\n" +
					
					"Credits:\n" +
					"The Android robot is reproduced or modified from work created and shared by Google and used according to terms " +
					"described in the Creative Commons 3.0 Attribution License.");
			
			adb.setCancelable(true);
			adb.setPositiveButton("Awesome!", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
				}
			});
			adb.create();
			return adb;

		}
		catch (Exception ex) { return null; }
	}

	/***********************************************************
	 * Report Bug
	 **********************************************************/
	private void reportBug() {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("message/rfc822");
		intent.putExtra(Intent.EXTRA_EMAIL, new String[] {"olesakn@gmail.com"});
		intent.putExtra(Intent.EXTRA_SUBJECT, "EASYMPG Bug Report");
		intent.putExtra(Intent.EXTRA_TEXT, "Please include a detailed description of the bug. Thank you!\n\n");
		try {
			startActivity(Intent.createChooser(intent, "Choose Your E-mail Client"));
		} catch (Exception e) {
			Toast.makeText(getApplicationContext(), "There are no e-mail clients installed", Toast.LENGTH_SHORT).show();
		}
	}


	/***********************************************************
	 * Suggest Features
	 **********************************************************/
	private void suggestFeatures() {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("message/rfc822");
		intent.putExtra(Intent.EXTRA_EMAIL, new String[] {"olesakn@gmail.com"});
		intent.putExtra(Intent.EXTRA_SUBJECT, "EASYMPG Feature Suggestion");
		try {
			startActivity(Intent.createChooser(intent, "Choose Your E-mail Client"));
		} catch (Exception e) {
			Toast.makeText(getApplicationContext(), "There are no e-mail clients installed", Toast.LENGTH_SHORT).show();
		}
	}


	/***********************************************************
	 * Export Data
	 **********************************************************/
	private void exportData() {
		AlertDialog.Builder adb = new AlertDialog.Builder(this);
		adb.setTitle("Export Data");
		adb.setMessage("This will export your data to\n/sdcard/easympg/");
		adb.setCancelable(true);

		adb.setPositiveButton("Export", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				String str = "EasyMPG Version 2.0\n";
				ArrayList<VehicleNew> vehicles = MainActivity.vehicles;
				for (VehicleNew v : vehicles) {
					str +=  "<vehicle>\n" +
							v.getName() + "\n" + 
							v.getMake() + "\n" + 
							v.getModel() + "\n" + 
							v.getYear() + "\n" +
							v.getOdometer() + "\n" +
							v.getFillups().size() + "\n" + 
							v.getCosts().size() + "\n";
					for (FillupNew f: v.getFillups()) {
						str += "<fillup>\n" +
								f.getUnitCost() + "\n" + 
								f.getTotalCost() + "\n" + 
								f.getOdometer() + "\n" + 
								f.getMemo() + "\n" + 
								f.getCalendar().getTime() + "\n" + 
								f.isCompleteFillup() + "\n" +
								f.getMPG() + "\n";
					}
					for (Cost c : v.getCosts()) {
						str += "<cost>\n" + 
								c.getType() + "\n" + 
								c.getDescription() + "\n" + 
								c.getDate().getTime() + "\n" + 
								c.getCost() + "\n" + 
								c.getOdometer() + "\n" +
								c.getReminder() + "\n" +
								c.isReminded() + "\n" +
								c.isNotified() + "\n";
					}
					
				}
				if (Environment.getExternalStorageState() != null)
					try {
						File root = new File(Environment.getExternalStorageDirectory(), "EasyMPG");
				        if (!root.exists())
				            root.mkdirs();
				        File file = new File(root, "export_data.txt");
				        FileWriter writer = new FileWriter(file);
				        writer.write(str);
				        writer.close();
				        Toast.makeText(getApplicationContext(), "Export Successful!", Toast.LENGTH_SHORT).show();
				} catch (IOException e) {
					Toast.makeText(getApplicationContext(), "Failed to Export", Toast.LENGTH_SHORT).show();
				}
			}
		});
		adb.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		adb.create().show();	// display the dialog
	}

	
	
	/***********************************************************
	 * Import Data
	 **********************************************************/
	private void importData() {
		MainActivity.showDenyDialog(this, "Import Data", 
				"Only the full version of the app can import data.\n\nIt only costs 99 cents!");
	}
}
