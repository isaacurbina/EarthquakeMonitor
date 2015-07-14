package com.brighterbrain.earthquakemonitor;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

// The definition of our task class
class RequestAsyncTask extends AsyncTask<String, String, JSONObject> {
	
	public AsyncTaskInterface delegate=null;
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		//displayProgressBar("Downloading...");
   }
 
	@Override
	protected JSONObject doInBackground(String... args) {
		JSONParser jParser = new JSONParser();
		JSONObject json;
		try {
			// Getting JSON from URL
			json = jParser.getJSONFromUrl(args[0]);
		} catch (Exception e) {
			json = new JSONObject();
		}
		return json;
	}

	@Override
	protected void onPostExecute(JSONObject json) {
      //super.onPostExecute(result);
	  //Log.d("EarthquakeMonitor", "RequestAsyncTask.onPostExecute result => "+json);
      delegate.processFinish(json);
      //dismissProgressBar();
   }
}