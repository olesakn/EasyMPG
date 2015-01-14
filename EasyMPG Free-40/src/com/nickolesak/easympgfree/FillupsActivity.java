package com.nickolesak.easympgfree;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import com.nickolesak.easympgfree.R;

import android.os.Bundle;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/*****************************************************************
 * Fillup Activity
 ****************************************************************/
public class FillupsActivity extends Fragment {

	/** Static Keys */
	public static final String EXTRA_EDIT_POSITION = "com.nickolesak.easympg.EXTRA_EDIT_FILLUP_POSITION";
	public static final double FIRST_FILLUP = -99.8, PARTIAL_FILLUP = -99.9;
	
	/** Private instance variables */
	private ArrayList<VehicleNew> vehicles;
	private CustomAdapterFillup adapter;
	private ArrayList<FillupNew> fillups;
	private Button addFillupBtn;
	private ListView listView;
	private View rootView;
	
	
	/****************************************************************
	 * On Create
	 ***************************************************************/	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fillups, container, false);

		vehicles = MainActivity.vehicles;
		fillups = MainActivity.currentVehicle.getFillups();
		initializeGUI();
		return rootView;
	}
	
	
	/****************************************************************
	 * Confirm Clear Data Dialog 
	 ***************************************************************/
	private void showConfirmDeleteAllFillupsDialog() {
		AlertDialog.Builder adb = new AlertDialog.Builder(rootView.getContext());
		adb.setTitle("Are You Sure?");
		adb.setMessage("This will delete all of your fillups for the current vehicle. " +
				 	   "This option cannot be undone. ");
		adb.setCancelable(true);

		adb.setPositiveButton("Delete Fillups", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				removeAllFillups();
			}
		});
		adb.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		
		adb.create().show();	// display the dialog
	}
	

	/****************************************************************
	 * Create Context Menu
	 ****************************************************************/
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getActivity().getMenuInflater();
		inflater.inflate(R.menu.fillup_listview_context_menu, menu);
	}
	
	
	/****************************************************************
	 * On List Item Selected Context Menu
	 ****************************************************************/
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		switch(item.getItemId()) {
			case R.id.fillup_item_delete :
				fillups.remove(info.position);
				MainActivity.saveVehicles(getActivity().getApplicationContext());
				adapter.notifyDataSetChanged();
				return true;
			case R.id.fillup_item_edit:
				editFillup(info.position); 
				return true;
			case R.id.fillup_item_delete_all:
				showConfirmDeleteAllFillupsDialog();
				return true;
			case R.id.fillup_item_share_on_twitter:
				postToTwitter(info.position);
				return true;
			default:
				return super.onContextItemSelected(item);
		}
	}
	
	/***********************************************************
	 * Post to Twitter
	 **********************************************************/
	private void postToTwitter(int fillupPos) {
		VehicleNew v = MainActivity.currentVehicle;
		FillupNew f = v.getFillups().get(fillupPos);
		String message = ""; 
		if (f.getMPG() == FillupsActivity.FIRST_FILLUP)
			message = "Just recorded my first fillup for my " + v.getYear() + " " + v.getMake() + " " + v.getModel() + " using the android app @EasyMPG!";
		
		else if (f.getMPG() == FillupsActivity.PARTIAL_FILLUP)
			message = "Just recorded a partial fillup for my " + v.getYear() + " " + v.getMake() + " " + v.getModel() + " using the android app @EasyMPG!";
		
		else
			message = "Just recorded " + (new DecimalFormat("0.0").format(f.getMPG())) + " mpg for my " + v.getYear() + " " + v.getMake() + " " + v.getModel() + " according to the android app @EasyMPG!";
		
		
		Intent tweetIntent = new Intent(Intent.ACTION_SEND);
		tweetIntent.putExtra(Intent.EXTRA_TEXT, message);
		tweetIntent.setType("text/plain");

		PackageManager packManager = getActivity().getPackageManager();
		List<ResolveInfo> resolvedInfoList = packManager.queryIntentActivities(tweetIntent, PackageManager.MATCH_DEFAULT_ONLY);

		boolean resolved = false;
		for(ResolveInfo resolveInfo: resolvedInfoList){
		    if(resolveInfo.activityInfo.packageName.startsWith("com.twitter.android")){
		        tweetIntent.setClassName(
		            resolveInfo.activityInfo.packageName, 
		            resolveInfo.activityInfo.name );
		        resolved = true;
		        break;
		    }
		}
		if(resolved){
			startActivity(tweetIntent);
		}else{
		    Toast.makeText(getActivity().getApplicationContext(), "Twitter app could not be found", Toast.LENGTH_SHORT).show();
		}
	}

	
	/*****************************************************************
	 * Edit Existing Fill-up.
	 ****************************************************************/
	private void editFillup(int pos) {
		Intent intent = new Intent(rootView.getContext(), CreateFillupActivity.class);
		intent.putExtra(EXTRA_EDIT_POSITION, pos);
		startActivity(intent);
		getActivity().finish();
	}
	
	
	/*****************************************************************
	 * Add New Fill-up
	 ****************************************************************/
	private void addFillup() {
		if (vehicles.size() > 0) {
			startActivity(new Intent(rootView.getContext(), CreateFillupActivity.class));
			getActivity().finish();
		}
		else {
			AlertDialog.Builder adb = new AlertDialog.Builder(rootView.getContext());
			adb.setTitle("No Vehicle");
			adb.setMessage("You need to create a vehicle first!");
			adb.setCancelable(true);
			adb.create().show();
		}
	}
	
	/*****************************************************************
	 * Clear data from file 
	 ****************************************************************/
	private boolean removeAllFillups() {
		fillups.clear();
		MainActivity.saveVehicles(getActivity());
		refreshListView();
		Toast.makeText(rootView.getContext(), "Fillups Successfully Removed", Toast.LENGTH_SHORT).show();
		return true;
	}
	
	
	/*****************************************************************
	 * Refresh the List View
	 ****************************************************************/
	private void refreshListView() {
		if (vehicles.size() == 0)
			adapter = new CustomAdapterFillup(rootView.getContext(), null);
		else
			adapter = new CustomAdapterFillup(rootView.getContext(), MainActivity.currentVehicle.getFillups());
		
		if (vehicles.size() > 0) {
			MainActivity.refreshMPGs(MainActivity.currentVehicle);
			listView.setAdapter(adapter);
			adapter.notifyDataSetChanged();
		}
	}
	

	/*****************************************************************
	 * Create the GUI Components
	 ****************************************************************/
	private void initializeGUI() {
	
		TextView noFillups= (TextView) rootView.findViewById(R.id.fillups_empty_text);
		if (fillups.size() == 0)
			noFillups.setText("No Fillups");
		else
			noFillups.setText("");
		
		addFillupBtn = (Button) rootView.findViewById(R.id.addFillupBtn);
		addFillupBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				addFillup();
			}
		});
		listView = (ListView) rootView.findViewById(R.id.fillups_list_view);
		listView.setHorizontalFadingEdgeEnabled(true);
		listView.setVerticalFadingEdgeEnabled(true);
		refreshListView();
		/*listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> adapter, View view, int pos, long id) {
				editFillup(pos);
			}		
		});*/
		registerForContextMenu(listView);
	}
}


