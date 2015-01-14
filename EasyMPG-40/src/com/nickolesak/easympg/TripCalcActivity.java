package com.nickolesak.easympg;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.DecimalFormat;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import android.widget.TextView;

public class TripCalcActivity extends Fragment {

	private EditText mpgET, toET, peopleET, fromET, milesET, gallonCostET; 
	private TextView estimateTV, individualCostTV;
	private RadioButton roundTrip;
	private Button calculateBtn;
	private View rootView;
	
	
	/****************************************************************
	 * On Create
	 ***************************************************************/	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.trip_calc, container, false);
		initGUIComponents();
		return rootView;
	}
	
	
	/****************************************************************
	 * Initialize GUI Components
	 ***************************************************************/
	private void initGUIComponents() {
		
		((TextView) rootView.findViewById(R.id.trip_calc_ratio_text_view)).setText(SettingsActivity.getRatioUnitAbbrevCaps());
		((TextView) rootView.findViewById(R.id.trip_calc_distance_text_view)).setText(SettingsActivity.getDistanceUnitNoMil() + "s");
		((TextView) rootView.findViewById(R.id.trip_calc_volume_text_view)).setText(SettingsActivity.getVolumeUnit() + " Price");
		
		VehicleNew v = MainActivity.currentVehicle;
		Statistics s = new Statistics(v);
		mpgET = (EditText) rootView.findViewById(R.id.mpg_edit_text);
		if (s.getAverageMPG() > 0)
			mpgET.setText(new DecimalFormat("#0.00").format(s.getAverageMPG()));
		gallonCostET = (EditText) rootView.findViewById(R.id.gallon_price);
		if (v.getFillups().size() > 0)
			gallonCostET.setText(new DecimalFormat("#0.000").format(v.getFillups().get(0).getUnitCost()));
		peopleET = (EditText) rootView.findViewById(R.id.number_of_people);
		peopleET.setText("1");
		toET = (EditText) rootView.findViewById(R.id.to_edit_text);
		toET.setOnEditorActionListener(new OnEditorActionListener() {
		    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		        if(actionId == EditorInfo.IME_ACTION_DONE)
		        	refreshEstimate();
		        return false;
		    }
		});
		fromET = (EditText) rootView.findViewById(R.id.from_edit_text);
		milesET = (EditText) rootView.findViewById(R.id.distance);
		milesET.setOnEditorActionListener(new OnEditorActionListener() {
		    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		        if(actionId == EditorInfo.IME_ACTION_DONE)
		        	refreshEstimate();
		        return false;
		    }
		});

		estimateTV = (TextView) rootView.findViewById(R.id.estimate_total_cost);
		individualCostTV = (TextView) rootView.findViewById(R.id.estimate_cost_per_person);
		String currency = SettingsActivity.getCurrency();
		if (currency.length() == 1) {
			estimateTV.setText(currency + new DecimalFormat("#0.00").format(0.0));
			individualCostTV.setText(currency + new DecimalFormat("#0.00").format(0.0));
		}
		else {
			estimateTV.setText(new DecimalFormat("#0.00").format(0.00) + " " + currency);
			individualCostTV.setText(new DecimalFormat("#0.00").format(0.00) + " " + currency);
		}
		
		roundTrip = (RadioButton) rootView.findViewById(R.id.estimate_radio_roundtrip);
		roundTrip.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				refreshEstimate();
			}
		});
		calculateBtn = (Button) rootView.findViewById(R.id.estimate_calc_button);
		calculateBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				refreshEstimate();
			}
		});
	}
	

	private void calculateManualEstimate() {
		double mpg = Double.parseDouble(mpgET.getText().toString());
		double miles = Double.parseDouble(milesET.getText().toString());
		double gallonPrice = Double.parseDouble(gallonCostET.getText().toString());
		int people = Integer.parseInt(peopleET.getText().toString());
		DecimalFormat df = new DecimalFormat("#0.00");
		double cost = miles * gallonPrice / mpg;
		String currency = SettingsActivity.getCurrency();
		if (roundTrip.isChecked()) {
			if (currency.length() == 1) {
				estimateTV.setText(currency + df.format(2 * cost));
				individualCostTV.setText(currency + df.format(2 * cost / people));
			}
			else {
				estimateTV.setText(df.format(2 * cost) + " " + currency);
				individualCostTV.setText(df.format(2 * cost / people) + " " + currency);
			}
		}
		else {
			if (currency.length() == 1) {
				estimateTV.setText(currency + df.format(cost));
				individualCostTV.setText(currency + df.format(cost / people));
			}
			else {
				estimateTV.setText(df.format(cost) + " " + currency);
				individualCostTV.setText(df.format(cost / people) + " " + currency);
			}
		}
	}


	private void calculateGeoEstimate() {
		new DistanceTaskClass().execute(null , null, null);
	}
	
	
	private class DistanceTaskClass extends AsyncTask<String, Void, JSONObject> 
	{
		protected JSONObject doInBackground(String... urls)
		{
			JSONObject json = fetchGoogleDirections();
			return json;
		}
		
		protected void onPostExecute(JSONObject json) {
			try 
			{
				// take the first route (assuming it's the best one)
				JSONObject route = json.getJSONArray("routes").getJSONObject(0);
				JSONObject leg = route.getJSONArray("legs").getJSONObject(0);
				
				String fullStart = leg.getString("start_address");
				String fullEnd = leg.getString("end_address");
				
				double distance = leg.getJSONObject("distance").getDouble("value");
				distance *= (SettingsActivity.getDistanceUnit().equals("Mile")) 
						? 0.00062137 	// convert meters to miles
						: .001;			// convert meters to kilometers
				
				if (distance != 0) 
				{
					// show the distance in the edit text
					milesET.setText(new DecimalFormat("#0.00").format(distance));
					
					// set the full addresses in the edit texts
					fromET.setText(fullStart);
					toET.setText(fullEnd);
					
					double mpg = Double.parseDouble(mpgET.getText().toString());
					double gallonCost = Double.parseDouble(gallonCostET.getText().toString());
					int people = Integer.parseInt(peopleET.getText().toString());
					double cost = 0;
					DecimalFormat df = new DecimalFormat("#0.00");
					
					if (mpg > 0)
						cost = distance * gallonCost / mpg; 
					if (cost > 0) 
					{
						String currency = SettingsActivity.getCurrency();
						if (roundTrip.isChecked()) 
							cost *= 2;
						
						estimateTV.setText((currency.length() == 1)
												? currency + df.format(cost)
												: df.format(cost) + " " + currency);
						
						individualCostTV.setText((currency.length() == 1)
												? currency + df.format(cost / people)
												: df.format(cost / people) + " " + currency);
					}
					else 
						throw new Exception();
				}
				else
					throw new Exception();
			}
			catch (Exception ex) 
			{ 
				estimateTV.setText(" - ");
				individualCostTV.setText(" - ");
				Toast.makeText(rootView.getContext(), "No Results Found...\nCheck your data connection!" , Toast.LENGTH_SHORT).show();
			}
	     }
	}
	
	
	private JSONObject fetchGoogleDirections() {
		String start = fromET.getText().toString().replace(" ", "_");
		String end = toET.getText().toString().replace(" ", "_");

		String url = new String("http://maps.googleapis.com/maps/api/directions/json?" 
				+ "origin=" + start
				+ "&destination=" + end
				+ "&sensor=false");
		try {
			HttpClient httpClient = new DefaultHttpClient();
			HttpContext localContext = new BasicHttpContext();
			HttpPost httpPost = new HttpPost(url);
			HttpResponse response = httpClient.execute(httpPost, localContext);
			BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
			StringBuilder builder = new StringBuilder();
			for (String line = null; (line = reader.readLine()) != null; ) {
			    builder.append(line).append("\n");
			}
			JSONTokener tokener = new JSONTokener(builder.toString());
			JSONObject json = new JSONObject(tokener);
			return json;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	

	public double readMilesFromJSON(JSONObject json) {
		try 
		{
			
			
			/*
			String result = "";
			String str = json.toString();
			int index = str.indexOf("distance", 0) + ("distance\":{\"value\":").length();
			while (!str.substring(index, index + 1).equals(",")) {
				result += str.substring(index, index + 1);
				index++;
			}
			if (SettingsActivity.getDistanceUnit().equals("Mile"))
				return (Integer.parseInt(result) * 0.00062137);
			else
				return Integer.parseInt(result) * .001;*/
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}


	/*****************************************************************
	 * Refresh Estimate
	 ****************************************************************/
	private void refreshEstimate() {
		if (mpgET.getText().toString().length() == 0)
			Toast.makeText(rootView.getContext(), "Enter " + SettingsActivity.getRatioUnitAbbrevCaps() + "!", Toast.LENGTH_SHORT).show();
		else if (gallonCostET.getText().toString().length() == 0)
			Toast.makeText(rootView.getContext(), "Enter Cost per Gallon!", Toast.LENGTH_SHORT).show();
		else if (peopleET.getText().toString().length() == 0)
			Toast.makeText(rootView.getContext(), "Enter the number of people!", Toast.LENGTH_SHORT).show();
		else if (milesET.getText().length() == 0) {
			if (toET.getText().length() == 0 && fromET.getText().length() == 0)
				Toast.makeText(rootView.getContext(), "Enter a distance!", Toast.LENGTH_SHORT).show();
			else if (toET.getText().length() == 0 && fromET.getText().length() > 0)
				Toast.makeText(rootView.getContext(), "Enter an origin address!", Toast.LENGTH_SHORT).show();
			else if (toET.getText().length() > 0 && fromET.getText().length() == 0)
				Toast.makeText(rootView.getContext(), "Enter a destination address!", Toast.LENGTH_SHORT).show();	
			else
				calculateGeoEstimate();
		}
		else if (milesET.getText().length() > 0)
			calculateManualEstimate();
		else
			Toast.makeText(rootView.getContext(), "Error Reading Data", Toast.LENGTH_SHORT).show();	
	}
}
