package com.brighterbrain.earthquakemonitor;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SummaryMap extends FragmentActivity {

    private GoogleMap googleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary_map);
        setTitle("Summary Map");
        Bundle extras = getIntent().getExtras();
        createMapView();

        JSONArray jsonarray= SQLiteManager.readFromDB(MainActivity.dbHelper);
        //Add entries to adapter
        for (int i = 0; i < jsonarray.length(); i++) {
            JSONObject entry;
            try {
                entry = jsonarray.getJSONObject(i);
                addMarker(entry.getDouble("lat"), entry.getDouble("lng"), entry.getString("place"), entry.getDouble("mag"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_summary_map, menu);
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
                        R.id.mapView2)).getMap();

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
    private void addMarker(double lat, double lng, String title, double mag){
        /*
        * Calculate the color for the marker
        */
        float hue= (float)mag*120/10;
        googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(lat,lng))
                .title(title)
                .draggable(false)
                .icon(BitmapDescriptorFactory.defaultMarker(hue)));
        //icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(lat, lng ), 1));
    }

}
