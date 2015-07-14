package com.brighterbrain.earthquakemonitor;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DetailedView extends FragmentActivity {

	private GoogleMap googleMap;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detailed_view);
		setTitle("Details");
		Bundle extras = getIntent().getExtras();
        createMapView();
		if (extras != null) {
		    String value = extras.getString("ID_FEED");
		    //Toast.makeText(this,  "id_feed received is "+value, Toast.LENGTH_SHORT).show();
            Log.d("EarthquakeMonitor", "id_feed received is " + value);
            JSONArray jsonarray= SQLiteManager.findEntryById(MainActivity.dbHelper, value);
            JSONObject entry= null;
            try {
                entry = jsonarray.getJSONObject(0);
                TextView magtxt = (TextView) findViewById(R.id.det_mag);
                TextView placetxt = (TextView) findViewById(R.id.det_location);
                TextView datetimetxt = (TextView) findViewById(R.id.det_time);
                TextView xyztxt = (TextView) findViewById(R.id.det_mag);
                // Populate the data into the template view using the data object
                magtxt.setText("Magnitude: "+String.valueOf(entry.getDouble("mag")));
                placetxt.setText("Location: \n"+
                        "("+entry.getString("lat")+
                        ", "+entry.getString("lng")+
                        ", "+entry.getString("dpt")+
                        ") \n"+entry.getString("place")
                );
                datetimetxt.setText("Time: " + Helpers.getDate(entry.getLong("time")));
                addMarker(entry.getDouble("lat"), entry.getDouble("lng"), entry.getString("place"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.detailed_view, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

    /**
     * Initialises the mapview
     */
    private void createMapView(){
        /**
         * Catch the null pointer exception that
         * may be thrown when initialising the map
         */
        try {
            if(null == googleMap){
                googleMap = ((MapFragment) getFragmentManager().findFragmentById(
                        R.id.mapView)).getMap();

                /**
                 * If the map is still null after attempted initialisation,
                 * show an error to the user
                 */
                if(null == googleMap) {
                    //Toast.makeText(getApplicationContext(), "Error creating map",Toast.LENGTH_SHORT).show();
                }
            }
        } catch (NullPointerException exception){
            Log.e("mapApp", exception.toString());
        }
    }

    /**
     * Adds a marker to the map
     */
    private void addMarker(double lat, double lng, String title){
            googleMap.addMarker(new MarkerOptions()
                            .position(new LatLng(lat,lng))
                            .title("Epicenter")
                            .draggable(false));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(lat, lng ), 4));
    }

}
