package com.brighterbrain.earthquakemonitor;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.brighterbrain.earthquakemonitor.SQLiteManager.FeedEntry;

public class MainActivity extends Activity implements AsyncTaskInterface {

	RequestAsyncTask async_task= new RequestAsyncTask();
	public static SQLiteManager dbHelper = null;
	private Menu optionsMenu;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		async_task.delegate = this;
		dbHelper = new SQLiteManager(getApplicationContext());
		refresh();
	}

	public void refresh() {
		setRefreshActionButtonState(true);
		if (Helpers.isNetworkAvailable(this))
			onlineRequest();
		else
			createListViewDB(SQLiteManager.readFromDB(dbHelper));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		this.optionsMenu = menu;
		getMenuInflater().inflate(R.menu.feed_refresh_menu, menu);
		return true;
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.feed_refresh_menu) {
			Log.d("EarthquakeMonitor", "click on refresh");
			//Toast.makeText(this, R.string.menuitem_refresh, Toast.LENGTH_LONG);
			refresh();
			return true;
		} else if (id == R.id.summary_map) {
			Log.d("EarthquakeMonitor", "click on summary map");
			Intent intent = new Intent(getBaseContext(), SummaryMap.class);
			startActivity(intent);
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void onlineRequest() {
		/*
		 * Set up variables to call the webservice
		 */
		String request_url= "http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_hour.geojson";
		
		Log.d("EarthquakeMonitor", "MainActivity async_task status => "+async_task.getStatus());
		if (async_task.getStatus() != AsyncTask.Status.PENDING) {
			async_task = new RequestAsyncTask();
			async_task.delegate = this;
		}
		async_task.execute(request_url);
	}

	@Override
	public void processFinish(JSONObject result) {
		// TODO Auto-generated method stub
		JSONArray dataArray = new JSONArray();
		if (result != null) {
			Log.d("EarthquakeMonitor", "received from internet and storing in database");
			dataArray = SQLiteManager.storeInDB(dbHelper, result);
			createListView(dataArray);
		} else {
			Log.d("EarthquakeMonitor", "no internet or error, reading from database");
			dataArray = SQLiteManager.readFromDB(dbHelper);
			createListViewDB(dataArray);
		}

	}
	
	public void createListView(JSONArray jsonarray) {
		// Create list of items
		ArrayList<Entry> items = new ArrayList<Entry>();
		// Build adapter
		ArrayList<Entry> entriesArray = new ArrayList<Entry>();
		EntryAdapter adapter = new EntryAdapter(this, entriesArray);
		// Configure the list view
		ListView listview = (ListView) findViewById(R.id.listview);
		listview.setAdapter(adapter);

		//Add entries to adapter
		for (int i = 0; i < jsonarray.length(); i++) {
			JSONObject row;
			try {
				row = jsonarray.getJSONObject(i);
				Entry newEntry = new Entry(
						row.getString("id"),
						row.getJSONObject("properties").getDouble("mag"),
						row.getJSONObject("properties").getString("place"),
						row.getJSONObject("properties").getLong("time"),
						Float.parseFloat(Helpers.stringtoArray(row.getJSONObject("geometry").getString("coordinates"))[0]),
						Float.parseFloat(Helpers.stringtoArray(row.getJSONObject("geometry").getString("coordinates"))[1]),
						Float.parseFloat(Helpers.stringtoArray(row.getJSONObject("geometry").getString("coordinates"))[2])
				);
				adapter.add(newEntry);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			/*
		 * Set click listener
		 */
			OnItemClickListener listener = new OnItemClickListener (){
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
										long arg3) {
					// TODO Auto-generated method stub
					LinearLayout linearlayout = (LinearLayout) arg1.findViewById(R.id.linearlayout);
					Log.d("EarthquakeMonitor", "click en item "+linearlayout.getTag());
					//Toast.makeText(getApplicationContext(), "ID_FEED= "+linearlayout.getTag().toString(),Toast.LENGTH_LONG).show();
					Intent intent = new Intent(getBaseContext(), DetailedView.class);
					intent.putExtra("ID_FEED", linearlayout.getTag().toString());
					startActivity(intent);

				}

			};
			listview.setOnItemClickListener (listener);
			setRefreshActionButtonState(false);
		}
	}

	public void createListViewDB(JSONArray jsonarray) {
		// Create list of items
		ArrayList<Entry> items = new ArrayList<Entry>();
		// Build adapter
		ArrayList<Entry> entriesArray = new ArrayList<Entry>();
		EntryAdapter adapter =new EntryAdapter(this, entriesArray);
		// Configure the list view
		ListView listview = (ListView) findViewById(R.id.listview);
		listview.setAdapter(adapter);

		//Add entries to adapter
		for (int i = 0; i < jsonarray.length(); i++) {
			JSONObject row;
			try {
				row = jsonarray.getJSONObject(i);
				Entry newEntry = new Entry(
						row.getString("id_feed"),
						Float.parseFloat(row.getString("mag")),
						row.getString("place"),
						row.getLong("time"),
						Float.parseFloat(row.getString("lat")),
						Float.parseFloat(row.getString("lng")),
						Float.parseFloat(row.getString("dpt"))
				);
				adapter.add(newEntry);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		/*
		 * Set click listener
		 */
		OnItemClickListener listener = new OnItemClickListener (){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				LinearLayout linearlayout = (LinearLayout) arg1.findViewById(R.id.linearlayout);
				Log.d("EarthquakeMonitor", "click en item "+linearlayout.getTag());
				//Toast.makeText(getApplicationContext(), "ID_FEED= "+linearlayout.getTag().toString(),Toast.LENGTH_LONG).show();
				Intent intent = new Intent(getBaseContext(), DetailedView.class);
				intent.putExtra("ID_FEED", linearlayout.getTag().toString());
				startActivity(intent);
				
			}

		};
		listview.setOnItemClickListener (listener);
		setRefreshActionButtonState(false);
	}

	public void setRefreshActionButtonState(final boolean refreshing) {
		if (optionsMenu != null) {
			final MenuItem refreshItem = optionsMenu
					.findItem(R.id.feed_refresh_menu);
			if (refreshItem != null) {
				if (refreshing) {
					refreshItem.setActionView(R.layout.actionbar_indeterminate_progress);
				} else {
					refreshItem.setActionView(null);
				}
			}
		}
	}
	
}
