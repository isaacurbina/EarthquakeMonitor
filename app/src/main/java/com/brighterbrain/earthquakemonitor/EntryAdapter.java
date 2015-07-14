package com.brighterbrain.earthquakemonitor;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class EntryAdapter extends ArrayAdapter<Entry> {
    public EntryAdapter(Context context, ArrayList<Entry> entries) {
       super(context, 0, entries);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
       // Get the data item for this position
       Entry entry = getItem(position);
       // Check if an existing view is being reused, otherwise inflate the view
       if (convertView == null) {
          convertView = LayoutInflater.from(getContext()).inflate(R.layout.feed_entry, parent, false);
       }
       // Lookup view for data population
       TextView magtxt = (TextView) convertView.findViewById(R.id.mag);
       TextView placetxt = (TextView) convertView.findViewById(R.id.place);
       // Populate the data into the template view using the data object
       magtxt.setText(String.valueOf(entry.mag));
       placetxt.setText(entry.place);
       LinearLayout linearlayout = (LinearLayout) convertView.findViewById(R.id.linearlayout);
       
       linearlayout.setTag(entry.id_feed);
       
       /*
        * Calculate the color to be displayed
        */
       int red = (int) ((255 * entry.mag) / 10);
       int green = (int) ((255 * (10 - entry.mag)) / 10); 
       int blue = 0;
       if (entry.mag<10 && entry.mag>=0)
    	   linearlayout.setBackgroundColor(Color.rgb(red, green, blue));
       //linearlayout.setBackgroundColor(Color.GREEN);
       
       // Return the completed view to render on screen
       return convertView;
   }
}
