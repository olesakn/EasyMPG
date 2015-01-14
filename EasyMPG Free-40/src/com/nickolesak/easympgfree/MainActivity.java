package com.nickolesak.easympgfree;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.nickolesak.easympgfree.R;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;
/*****************************************************************
 * EasyMPG Android Application
 * Designed for Android 4.0 and up
 * 
 * Author: Nicholas Olesak
 * Date: December 2013
 * Version: 2.0
 ****************************************************************/
public class MainActivity extends FragmentActivity implements ActionBar.TabListener {

	/** Static Constants */
	public static final String EXTRA_TAB_VALUE = "com.nickolesak.easympg.EXTRA_TAB";
	public static final String SAVED_VEHICLES = "com.nickolesak.easympg.SAVE_VEHICLES";
	public static final String SAVED_SETTINGS = "com.nickolesak.easympg.SAVED_SETTINGS";
	public static final String CURRENCY_SELECTION_INDEX = "com.nickolesak.easympg.CURRENCY_SELECTION_INDEX";
	public static final String UNIT_SELECTION_INDEX = "com.nickolesak.easympg.UNIT_SELECTION_INDEX";
	public static final String FIRST_RUN = "com.nickolesak.easympg.FIRST_RUN";
	public static final String FIRST_CURRENCY_AND_UNITS_1 = "com.nickolesak.easympg.FIRST_CURRENCY_AND_UNITS_1";
	public static final int FILLUPS_TAB = 0, COSTS_TAB
= 1, STATS_TAB = 2, ESTIMATE_TAB = 3;
	
	/** Static Variables */
	public static int currentTab, unitSelectionIndex, currencySelectionIndex;
	public static ArrayList<VehicleNew> vehicles;
	public static VehicleNew currentVehicle;
	
	/** Private Variables */
	private ActionBar actionBar;
	private SectionsPagerAdapter mSectionsPagerAdapter;
	private ViewPager viewPager;
	private Spinner spinner;
	private ArrayAdapter<String> adapter;
	
	/***************************************************************************
	 * On Create
	 **************************************************************************/
	public void onCreate (Bundle savedInstanceState) { 
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// Look up the AdView as a resource and load a request.
        AdView adView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    
        // Load unit and currency from shared prefs
		SharedPreferences sp = getSharedPreferences(SAVED_SETTINGS, Context.MODE_PRIVATE);
		unitSelectionIndex = sp.getInt(UNIT_SELECTION_INDEX, 0);
		currencySelectionIndex = sp.getInt(CURRENCY_SELECTION_INDEX, 0);

		if (vehicles == null)
			loadVehicles(this);
		
		//RESET DATA: vehicles = new ArrayList<Vehicle>(); saveVehicles(this);
		
		if (vehicles.size() == 0) 
		{
			AlertDialog.Builder adb = SettingsActivity.about(this);
			adb.setPositiveButton("OK", new DialogInterface.OnClickListener() 
			{
				public void onClick(DialogInterface dialog, int which) 
				{
						dialog.cancel();
						if (MainActivity.vehicles.size() == 0)
							showCreateFirstVehicleDialog();
				}
			});
			adb.create().show();
			sp.edit().putBoolean(FIRST_RUN, false).commit();
		}
		else 
		{
			showNewUIPageIfFirstRun();
			showNewCurrencyAndUnitPageIfNeeded();
			currentVehicle = vehicles.get(0);
			initActionBar();
			currentTab = getIntent().getIntExtra(EXTRA_TAB_VALUE, 0);
			viewPager.setCurrentItem(currentTab);
		}
	}
	
	
	/***************************************************************
	 * Initialize Tabs
	 **************************************************************/
	private void initActionBar() {
		actionBar = getActionBar();
		actionBar.setLogo(R.drawable.easymp_logo_highres);
		actionBar.setCustomView(R.layout.action_bar);
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayUseLogoEnabled(true);
		//actionBar.setBackgroundDrawable(getResources().getDrawable(R.color.blue));
		//actionBar.setStackedBackgroundDrawable(getResources().getDrawable(R.color.blue_accent));
		//actionBar.setStackedBackgroundDrawable(new ColorDrawable(Color.WHITE));
		
		spinner = (Spinner) this.findViewById(R.id.action_bar_spinner);
		List<String> list = getVehicleList();
		adapter = new ArrayAdapter<String>(getApplicationContext(),
					R.layout.spinner_drop_down, list);
		adapter.setDropDownViewResource(R.layout.spinner_drop_down);
		spinner.setAdapter(adapter);
		spinner.setSelection(0);
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				if (pos == 1) {
					spinner.setSelection(0);
					showDenyDialog(MainActivity.this, 
							"Sorry!", "You need the full version to keep logs for more than one vehicle.\n\nIt only costs 99 cents!");
				}
			}
			public void onNothingSelected(AdapterView<?> arg0) {}
		});
		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

		viewPager = (ViewPager) findViewById(R.id.pager);
		viewPager.setAdapter(mSectionsPagerAdapter);
		viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			public void onPageSelected(int position) {
				actionBar.setSelectedNavigationItem(position);
			}
		});
		
		/* Create tabs */
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.addTab(actionBar.newTab().setText("Fillups").setTabListener(this));
		actionBar.addTab(actionBar.newTab().setText("Expenses").setTabListener(this));
		actionBar.addTab(actionBar.newTab().setText("Stats").setTabListener(this));
		actionBar.addTab(actionBar.newTab().setText("Trip Calc").setTabListener(this));
	}
	

	/**************************************************************
	 * ActionBar Option Selected
	 *************************************************************/
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.action_bar_main, menu);
	    return super.onCreateOptionsMenu(menu);
	}
	
	
	/****************************************************************
	 * Option Item Selected
	 ****************************************************************/
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_add:
			if (vehicles.size() > 0)
				startActivity(new Intent(this, CreateFillupActivity.class));
			else
				makeDialog(this, "No Vehicles", "You need to create a vehicle first!").show();
			return true;
			
		case R.id.action_edit_vehicle:
			Intent intent = new Intent(this, CreateVehicleActivity.class);
			intent.putExtra(CreateVehicleActivity.EXTRA_IS_EDIT, true);
			startActivity(intent);
			return true;
			
		case R.id.action_delete_vehicle:
			if (vehicles.size() > 0) {
				AlertDialog.Builder adb = makeDialog(this, "Are You Sure?", "Do you want to delete "
						+ currentVehicle.getName() + "?");
				adb.setPositiveButton("Delete", new OnClickListener() {
					public void onClick(DialogInterface d, int arg1) {
						d.cancel();
						deleteVehicle();
					}
				});
				adb.setNegativeButton("Cancel", new OnClickListener() {
					public void onClick(DialogInterface d, int arg1) {
						d.cancel();
					}
				});
				adb.create().show();
			}
			return true;
			
		case R.id.action_settings:
			startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
			return true;
			
		case R.id.action_upgrade_to_pro:
			Intent i = new Intent(Intent.ACTION_VIEW);
			i.setData(Uri.parse("market://details?id=com.nickolesak.easympg"));
			this.startActivity(i);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	
	/***************************************************************
	 * Delete Current Vehicle
	 **************************************************************/
	private void deleteVehicle() {
		vehicles.remove(currentVehicle);
		if (vehicles.size() > 0) {
			adapter = new ArrayAdapter<String>(getApplicationContext(),
						R.layout.spinner_drop_down, getVehicleList());
			adapter.setDropDownViewResource(R.layout.spinner_drop_down);
			spinner.setAdapter(adapter);
			spinner.setSelection(0);
		}
		else if (vehicles.size() == 0) {
			vehicles.add(new VehicleNew("My Vehicle","","",0));
			adapter = new ArrayAdapter<String>(getApplicationContext(),
						R.layout.spinner_drop_down, getVehicleList());
			adapter.setDropDownViewResource(R.layout.spinner_drop_down);
			spinner.setAdapter(adapter);
			spinner.setSelection(0);
		}
		saveVehicles(this);
	}
	
	
	/****************************************************************
	 * Get Vehicle Spinner List
	 ***************************************************************/
	private ArrayList<String> getVehicleList() {
		ArrayList<String> names = new ArrayList<String>();
		if (vehicles.size() > 0)
			for (VehicleNew v : vehicles) 
				names.add(v.getName()); //year + make + model
		names.add("+ Add New Vehicle");
		return names;
	}
	
	
	/****************************************************************
	 * Show Dialog
	 ***************************************************************/
	private AlertDialog.Builder makeDialog(Context c, String title, String message) {
		AlertDialog.Builder adb = new AlertDialog.Builder(c);
		adb.setTitle(title);
		adb.setMessage(message);
		adb.setPositiveButton("OK", new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		adb.setCancelable(true);
		adb.create();
		return adb;
	}
	
	
	/***************************************************************
	 * Tell users to create vehicle first
	 **************************************************************/
	private void showCreateFirstVehicleDialog() {
		AlertDialog.Builder adb = new AlertDialog.Builder(this);
		adb.setTitle("Get Started");
		adb.setMessage("The first step is to create a vehicle!");
		adb.setCancelable(false);
		adb.setPositiveButton("Start", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
				startActivity(new Intent(getApplicationContext(), CreateVehicleActivity.class));
				finish();
			}
		});
		adb.create().show();
	}
	

	/***************************************************************
	 * Back Button Override
	 **************************************************************/
	public void onBackPressed() {
		if (viewPager.getCurrentItem() == 0)
			finish();
		else
			viewPager.setCurrentItem(0);
	}
	
	
	/****************************************************************
	 * Load Vehicles from device memory
	 ***************************************************************/
	@SuppressWarnings("unchecked")
	public static void loadVehicles(Context context) {
		vehicles = new ArrayList<VehicleNew>();
		SharedPreferences sp1 = context.getSharedPreferences(SAVED_SETTINGS, Context.MODE_PRIVATE);
		// sp2 is a bug fix. It it used to prevent a complete vehicle-erase. Do not delete for a while (after July 2014 should be fine)
		SharedPreferences sp2 = context.getSharedPreferences("com.nickolesak.easympg.FIRST_RUN", Context.MODE_PRIVATE);
		if (sp1.getBoolean(FIRST_RUN, true) && sp2.getBoolean("com.nickolesak.easympg.FIST_RUN", true)) {
			try {
				FileInputStream fis = context.openFileInput(SAVED_VEHICLES);
				ObjectInputStream ois = new ObjectInputStream(fis);
				ArrayList<Vehicle> oldVehicles = (ArrayList<Vehicle>) ois.readObject();
				vehicles = new ArrayList<VehicleNew>();
				for (Vehicle v : oldVehicles)
					vehicles.add(v.convertToNewVehicleFormat());
				saveVehicles(context);
				ois.close();			
			} catch (Exception e) {}
		}
		else {
			vehicles = new ArrayList<VehicleNew>();
			try {
				FileInputStream fis = context.openFileInput(SAVED_VEHICLES);
				ObjectInputStream ois = new ObjectInputStream(fis);
				vehicles = (ArrayList<VehicleNew>) ois.readObject();
				for (VehicleNew v: vehicles)
					if (v.getCosts() == null) {
						v.setCosts( new ArrayList<Cost>());
					}
				ois.close();			
			} catch (Exception e) {}
		}
	}
	
	
	/****************************************************************
	 * Save Vehicles to device memory
	 ***************************************************************/
	public static void saveVehicles(Context context) {
		try {
			for (VehicleNew v : vehicles) {
				Collections.sort(v.getCosts());
				refreshMPGs(v);
			}
			createReminderNotifications(context, (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE));
			FileOutputStream fos = context.openFileOutput(MainActivity.SAVED_VEHICLES, Context.MODE_PRIVATE);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(vehicles);
			oos.close();
		} catch (Exception e) {}
	}
	
	
	/******************************************************************
	 * Refresh MPG Values
	 *****************************************************************/
	public static void refreshMPGs(VehicleNew v) {
		ArrayList<FillupNew> fillups = v.getFillups();
		if (fillups.size() > 0) {
			Collections.sort(fillups);
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
	
	
	/*************************************************************
	 * Create Notifications for Expense Reminders
	 ************************************************************/
	public static void createReminderNotifications (Context context, NotificationManager nManager) {
		String message = "";
		int count = 0;
		for (VehicleNew vehicle :vehicles) {
			for (Cost c : vehicle.getCosts()) {
				if (c.getOdomReminder() <= currentVehicle.getOdometer() && !c.isNotified()) {
					message = vehicle.getName() + ": " + c.getType() + " at " + c.getOdomReminder() + " miles";
					count++;
					c.setNotified(true);
				}
			}
		}
		if (count > 0) {		
			NotificationCompat.Builder n = new NotificationCompat.Builder(context);
		    n.setSmallIcon(R.drawable.easymp_logo_highres);
		    n.setContentTitle("Expense Reminder");
		    if (count == 1)
		    	n.setContentText(message);
		    else
		    	n.setContentText(count + " reminders. Tap to view");
		    
		    Intent resultIntent = new Intent(context, MainActivity.class);
		    resultIntent.putExtra(MainActivity.EXTRA_TAB_VALUE, MainActivity.COSTS_TAB);
		    PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		    n.setContentIntent(resultPendingIntent);
		    nManager.notify(483, n.build());
		}
	}

	
	/**********************************************************************
	 * Display info about the new UI if user is running for the first time
	 *********************************************************************/
	private void showNewUIPageIfFirstRun () {
		if (getSharedPreferences(SAVED_SETTINGS, Context.MODE_PRIVATE).getBoolean(FIRST_RUN, true)
		&& getSharedPreferences(FIRST_RUN, Context.MODE_PRIVATE).getBoolean("com.nickolesak.easymp.FIST_RUN", true)) {
			AlertDialog.Builder adb = new AlertDialog.Builder(this);
			adb.setTitle("EasyMPG 2.0 is here!");
			adb.setMessage("Welcome to the new version!\n\n"
					+ "We have updated our layout to match the new standards "
					+ "of Android 4.0 and up!\n\n"
					+ "Changes:\n"
					+ " - More Units and Currencies\n"
					+ " - New Color Scheme\n"
					+ " - Simpler Design\n"
					+ " - Action bar\n"
					+ " - Addition of Expenses tab\n"
					+ " - Expense Reminders\n\n"
					+ "Enjoy using the app and remember to rate it in the Google Play Store if you like it!");
			adb.setCancelable(true);
			adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
						getSharedPreferences(SAVED_SETTINGS, Context.MODE_PRIVATE)
							.edit().putBoolean(FIRST_RUN, false).commit();
				}
			});
			adb.create().show();
		}
	}
	
	
	/****************************************************
	 * Show Deny Dialog For Upgrade
	 ***************************************************/
	public static void showDenyDialog(final Context context, String title, String message) {
		AlertDialog.Builder adb = new AlertDialog.Builder(context);
		adb.setTitle(title);
		adb.setMessage(message);
		adb.setPositiveButton("Get Full Version", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse("market://details?id=com.nickolesak.easympg"));
				context.startActivity(intent);
				dialog.cancel();
			}
		});
		adb.setNegativeButton("No Thanks", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		adb.setCancelable(true);
		adb.create().show();
	}
	
	
	
	/***************************************************************
	 * Show New Currency & Units Dialog
	 **************************************************************/
	private void showNewCurrencyAndUnitPageIfNeeded() {
		if (!getSharedPreferences(SAVED_SETTINGS, Context.MODE_PRIVATE).getBoolean(FIRST_RUN, true)) {
			SharedPreferences sp = this.getSharedPreferences(SAVED_SETTINGS, Context.MODE_PRIVATE);
			if (sp.getBoolean(FIRST_CURRENCY_AND_UNITS_1, true)) {
				AlertDialog.Builder adb = new AlertDialog.Builder(this);
				adb.setTitle("New Feature!");
				adb.setMessage("We have now added currency and unit options!\n\n" +
						"You can change your preferences in the 'Options' menu.\n");
				adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
							getSharedPreferences(SAVED_SETTINGS, Context.MODE_PRIVATE)
								.edit().putBoolean(FIRST_CURRENCY_AND_UNITS_1, false).commit();
					}
				});
				adb.create().show();
			}
		}
	}
	
	
	public static boolean canReceiveIntent (Intent intent, Context c) {

	    PackageManager packageManager = c.getPackageManager();
	    List<ResolveInfo> activities = packageManager.queryIntentActivities(intent, 0);
	    boolean isIntentSafe = activities.size() > 0;

	    return isIntentSafe;
	}
	
	/***************************************************************
	 * Tab Listeners
	 **************************************************************/
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		viewPager.setCurrentItem(tab.getPosition());
		currentTab = tab.getPosition();
	}
	public void onTabReselected(Tab tab, FragmentTransaction ft) {}
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {}
	
	
	/*******************************************************
	 * Sections Page Adapter
	 * returns a fragment corresponding to one of the tabs
	 ******************************************************/
	public class SectionsPagerAdapter extends FragmentPagerAdapter {
		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}
		
		/* Get Fragment */
		public Fragment getItem(int position) {
			switch(position) {
			case 0:
				return new FillupsActivity();
			case 1:
				return new CostsActivity();
			case 2:
				return new StatsActivity();
			default:
				return new TripCalcActivity();
			}
		}

		/* Tab Count */
		public int getCount() {
			return 4;
		}

		/* Tab Titles */
		public CharSequence getPageTitle(int position) {
			if (position == 0) 
				return "Fillups";
			if (position == 1)
				return "Expenses";
			if (position == 2)
				return "Stats";
			if (position == 3)
				return "Trip Calc";
			return null;
		}
	}
}