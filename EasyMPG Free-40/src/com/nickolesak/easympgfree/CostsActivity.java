package com.nickolesak.easympgfree;

import java.util.ArrayList;

import com.nickolesak.easympgfree.R;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class CostsActivity extends Fragment {

	public static final String EXTRA_EDIT_POSITION = "com.nickolesak.easympg.EXTRA_EDIT_COST_POSITION";
	private ArrayList<VehicleNew> vehicles;
	private CustomAdapterCost adapter;
	private ArrayList<Cost> costs;
	private Button addCostBtn;
	private ListView listView;
	private View rootView; 
	
	
	/****************************************************************
	 * On Create
	 ***************************************************************/	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.costs, container, false);
		vehicles = MainActivity.vehicles;
		costs = MainActivity.currentVehicle.getCosts();
		initializeGUI();
		return rootView;
	}
	
	
	/****************************************************************
	 * Confirm Clear Data Dialog 
	 ***************************************************************/
	private void showConfirmDeleteAllCostsDialog() {
		AlertDialog.Builder adb = new AlertDialog.Builder(rootView.getContext());
		adb.setTitle("Are You Sure?");
		adb.setMessage("This will delete all of your expenses for the current vehicle. " +
				 	   "This option cannot be undone. ");
		adb.setCancelable(true);

		adb.setPositiveButton("Delete All Expenses", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				deleteAllCosts();
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
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		getActivity().getMenuInflater().inflate(R.menu.cost_listview_context_menu, menu);
	}
	
	
	/****************************************************************
	 * On List Item Selected Context Menu
	 ****************************************************************/
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		switch (item.getItemId()) {
			case R.id.cost_item_delete: 
				deleteCost(info.position);
				return true;
			case R.id.cost_item_edit:
				editCost(info.position); 
				return true;
			case R.id.cost_item_delete_all:
				showConfirmDeleteAllCostsDialog();
				return true;
			case R.id.cost_item_clear_reminder:
				costs.get(info.position).setReminded(true);
				refreshListView();
				MainActivity.saveVehicles(rootView.getContext());
				return true;
			default:
				return super.onContextItemSelected(item);
		}
	}


	/*****************************************************************
	 * Delete Cost
	 ****************************************************************/
	private void deleteCost(int index) {
		costs.remove(index);
		if (costs.size() == 0)
			((TextView) rootView.findViewById(R.id.costs_empty_text)).setText("No Expenses");
		MainActivity.saveVehicles(getActivity().getApplicationContext());
		adapter.notifyDataSetChanged();
	}
	
	
	/*****************************************************************
	 * Edit Existing Cost
	 ****************************************************************/
	private void editCost(int pos) {
		Intent intent = new Intent(rootView.getContext(), CreateCostActivity.class);
		intent.putExtra(EXTRA_EDIT_POSITION, pos);
		startActivity(intent);
		getActivity().finish();
	}
	
	
	/*****************************************************************
	 * Add New Cost
	 ****************************************************************/
	private void addCost() {
		if (vehicles.size() > 0) {
			startActivity(new Intent(rootView.getContext(), CreateCostActivity.class));
			getActivity().finish();
		}
		else {
			AlertDialog.Builder adb = new AlertDialog.Builder(rootView.getContext());
			adb.setTitle("No Vehicle");
			adb.setMessage("You need to create a vehicle first!");
			adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
				}
			});
			adb.setCancelable(true);
			adb.create().show();
		}
	}
	
	
	/*****************************************************************
	 * Clear data from file 
	 ****************************************************************/
	private boolean deleteAllCosts() {
		costs.clear();
		MainActivity.saveVehicles(getActivity());
		refreshListView();
		((TextView) rootView.findViewById(R.id.costs_empty_text)).setText("No Expenses");
		Toast.makeText(rootView.getContext(), "Expenses Successfully Removed", Toast.LENGTH_SHORT).show();
		return true;
	}
	
	
	/*****************************************************************
	 * Refresh the List View
	 ****************************************************************/
	private void refreshListView() {
		if (vehicles.size() == 0)
			adapter = new CustomAdapterCost(rootView.getContext(), null);
		else
			adapter = new CustomAdapterCost(rootView.getContext(), MainActivity.currentVehicle.getCosts());
		
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
	
		if (costs.size() == 0)
			((TextView) rootView.findViewById(R.id.costs_empty_text)).setText("No Expenses");
		else
			((TextView) rootView.findViewById(R.id.costs_empty_text)).setText("");
		
		addCostBtn = (Button) rootView.findViewById(R.id.costs_add_button);
		addCostBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				addCost();
			}
		});
		listView = (ListView) rootView.findViewById(R.id.costs_list_view);
		listView.setHorizontalFadingEdgeEnabled(true);
		listView.setVerticalFadingEdgeEnabled(true);
		refreshListView();
		/*listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> adapter, View view, int pos, long id) {
				editCost(pos);
			}		
		});*/
		registerForContextMenu(listView);
	}
}


