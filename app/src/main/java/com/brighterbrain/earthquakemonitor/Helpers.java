package com.brighterbrain.earthquakemonitor;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class Helpers {

	/**
	 * Return date in specified format.
	 * @param milliSeconds Date in milliseconds
	 * @param dateFormat Date format 
	 * @return String representing date in specified format
	 */
	public static String getDate(long milliSeconds)
	{
		String dateFormat = "dd/MM/yyyy hh:mm:ss"; //.SSS";
	    // Create a DateFormatter object for displaying date in specified format.
	    SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
	    
	    // Create a calendar object that will convert the date and time value in milliseconds to date. 
	    Calendar calendar = Calendar.getInstance();
	    calendar.setTimeInMillis(milliSeconds);
	     
	    Log.d("EarthquakeMonitor", "Helpers.getDate result => " + milliSeconds + " to " + formatter.format(calendar.getTime()));
	     
	    return formatter.format(calendar.getTime());
	}
	
	public static boolean isNetworkAvailable(Context mycontext) {
	    ConnectivityManager connectivityManager 
	          = (ConnectivityManager) mycontext.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
	
	public static String[] stringtoArray(String s) {
		  s= s.substring(1, s.length()-1);
		  // convert a String s to an Array, the elements
		  // are delimited by sep
		  // NOTE : for old JDK only (<1.4).
		  //        for JDK 1.4 +, use String.split() instead
		  StringBuffer buf = new StringBuffer(s);
		  String sep = ",";
		  int arraysize = 1;
		  for ( int i = 0; i < buf.length(); i++ ) {
		    if ( sep.indexOf(buf.charAt(i) ) != -1 )
		      arraysize++;
		  }
		  String [] elements  = new String [arraysize];
		  int y,z = 0;
		  if ( buf.toString().indexOf(sep) != -1 ) {
		    while (  buf.length() > 0 ) {
		      if ( buf.toString().indexOf(sep) != -1 ) {
		        y =  buf.toString().indexOf(sep);
		        if ( y != buf.toString().lastIndexOf(sep) ) {
		          elements[z] = buf.toString().substring(0, y ); 
		          z++;
		          buf.delete(0, y + 1);
		        }
		        else if ( buf.toString().lastIndexOf(sep) == y ) {
		          elements[z] = buf.toString().substring
		            (0, buf.toString().indexOf(sep));
		          z++;
		          buf.delete(0, buf.toString().indexOf(sep) + 1);
		          elements[z] = buf.toString();z++;
		          buf.delete(0, buf.length() );
		        }
		      }
		    }
		  }
		  else {
		    elements[0] = buf.toString(); 
		  }
		  buf = null;
		  return elements;
		}
	
}
