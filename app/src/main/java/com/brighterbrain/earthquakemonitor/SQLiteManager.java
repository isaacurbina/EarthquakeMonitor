package com.brighterbrain.earthquakemonitor;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public final class SQLiteManager extends SQLiteOpenHelper {

	// If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "FeedReader.db";

    	private static final String SQL_DELETE_ENTRIES =
    	    "DROP TABLE IF EXISTS " + FeedEntry.TABLE_NAME;
	
	public SQLiteManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

	/* Inner class that defines the table contents */
	public static abstract class FeedEntry implements BaseColumns {
		public static final String TABLE_NAME = "earthquakefeed";
		public static final String COLUMN_NAME_ID = "id_feed"; // event id
		public static final String COLUMN_TYPE_ID = "VARCHAR(15)";
		public static final String COLUMN_NAME_MAG = "mag"; // magnitude
		public static final String COLUMN_TYPE_MAG = "FLOAT";
		public static final String COLUMN_NAME_PLACE = "place"; // place of the event
		public static final String COLUMN_TYPE_PLACE = "VARCHAR(64)";
		public static final String COLUMN_NAME_TIME= "time"; // date and time
		public static final String COLUMN_TYPE_TIME= "DATETIME";
		public static final String COLUMN_NAME_LAT = "lat";
		public static final String COLUMN_TYPE_LAT = "FLOAT";
		public static final String COLUMN_NAME_LNG = "lng";
		public static final String COLUMN_TYPE_LNG = "FLOAT";
		public static final String COLUMN_NAME_DPT = "dpt";
		public static final String COLUMN_TYPE_DPT = "FLOAT";
		
		// Queries
		public static final String SQL_CREATE_ENTRIES =
		    	    "CREATE TABLE IF NOT EXISTS " + FeedEntry.TABLE_NAME + " (" +
		    	    FeedEntry.COLUMN_NAME_ID + " " + FeedEntry.COLUMN_TYPE_ID+" PRIMARY KEY," +
		    	    FeedEntry.COLUMN_NAME_MAG + " " +  FeedEntry.COLUMN_TYPE_MAG + ", " +
		    	    FeedEntry.COLUMN_NAME_PLACE + " " +  FeedEntry.COLUMN_TYPE_PLACE + ", " +
		    	    FeedEntry.COLUMN_NAME_TIME + " " +  FeedEntry.COLUMN_TYPE_TIME + ", " +
		    	    FeedEntry.COLUMN_NAME_LAT + " " +  FeedEntry.COLUMN_TYPE_LAT + ", " +
		    	    FeedEntry.COLUMN_NAME_LNG + " " +  FeedEntry.COLUMN_TYPE_LNG + ", " +
		    	    FeedEntry.COLUMN_NAME_DPT + " " +  FeedEntry.COLUMN_TYPE_DPT + " " +
		    	    " )";
		public static final String SQL_DELETE_ENTRIES =
			    "DELETE FROM " + FeedEntry.TABLE_NAME;

		public static final String SQL_FIND_ENTRY_BY_ID =
				"SELECT * FROM "+FeedEntry.TABLE_NAME+
				"WHERE id_feed=";
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		Log.d("EarthquakeMonitor", "SQLiteManager.onCreate() => "+FeedEntry.SQL_CREATE_ENTRIES);
		db.execSQL(FeedEntry.SQL_CREATE_ENTRIES);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		// This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(FeedEntry.SQL_DELETE_ENTRIES);
        Log.d("EarthquakeMonitor", "SQLiteManager.onUpgrade() => "+FeedEntry.SQL_DELETE_ENTRIES);
        onCreate(db);
	}
	
	public void deleteRecords(SQLiteDatabase db) {
		Log.d("EarthquakeMonitor", "SQLiteManager.deleteRecords() => "+FeedEntry.SQL_DELETE_ENTRIES);
		db.execSQL(FeedEntry.SQL_DELETE_ENTRIES);
	}
	
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

	public static JSONArray storeInDB(SQLiteManager dbHelper, JSONObject jsonobj) {
		// Gets the data repository in write mode
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		dbHelper.onCreate(db);

		try {
			JSONArray jsonarray = jsonobj.getJSONArray("features");

			Log.d("EarthquakeMonitor", "MainActivity.storeInDB() deleted old records");
			dbHelper.deleteRecords(db);

			Log.d("EarthquakeMonitor", "MainActivity.storeInDB() length is => "+jsonarray.length());

			for (int i = 0; i < jsonarray.length(); i++) {
				JSONObject row = jsonarray.getJSONObject(i);
				//Log.d("EarthquakeMonitor", "** "+row.getString("id")+", "+row.getJSONObject("properties").getDouble("mag")+", "+Helpers.getDate(row.getJSONObject("properties").getInt("time"))+", "+row.getJSONObject("properties").getString("place"));

				// Create a new map of values, where column names are the keys
				ContentValues values = new ContentValues();
				values.put(FeedEntry.COLUMN_NAME_ID, row.getString("id"));
				values.put(FeedEntry.COLUMN_NAME_MAG, row.getJSONObject("properties").getDouble("mag"));
				values.put(FeedEntry.COLUMN_NAME_PLACE, row.getJSONObject("properties").getString("place"));
				values.put(FeedEntry.COLUMN_NAME_TIME, row.getJSONObject("properties").getLong("time"));
				values.put(FeedEntry.COLUMN_NAME_LAT, Helpers.stringtoArray(row.getJSONObject("geometry").getString("coordinates"))[1]);
				values.put(FeedEntry.COLUMN_NAME_LNG, Helpers.stringtoArray(row.getJSONObject("geometry").getString("coordinates"))[0]);
				values.put(FeedEntry.COLUMN_NAME_DPT, Helpers.stringtoArray(row.getJSONObject("geometry").getString("coordinates"))[2]);

				// Insert the new row, returning the primary key value of the new row
				long newRowId;
				newRowId = db.insert(
				         FeedEntry.TABLE_NAME,
				         FeedEntry.COLUMN_NAME_PLACE,
				         values);
			}
			Log.d("EarthquakeMonitor", "MainActivity.storeInDB() jsonarray length stored = > "+jsonarray.length());
			return jsonarray;
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return null;
		}
	}

	public static JSONArray readFromDB(SQLiteManager dbHelper) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();

		// Define a projection that specifies which columns from the database
		// you will actually use after this query.
		String[] projection = {
				FeedEntry.COLUMN_NAME_ID,
				FeedEntry.COLUMN_NAME_MAG,
				FeedEntry.COLUMN_NAME_PLACE,
				FeedEntry.COLUMN_NAME_TIME,
				FeedEntry.COLUMN_NAME_LAT,
				FeedEntry.COLUMN_NAME_LNG,
				FeedEntry.COLUMN_NAME_DPT
		};

		// How you want the results sorted in the resulting Cursor
		String sortOrder = "time DESC";

		Cursor c = db.query(
				FeedEntry.TABLE_NAME,  // The table to query
				projection,		// The columns to return
				null,           // The columns for the WHERE clause
				null,           // The values for the WHERE clause
				null,           // don't group the rows
				null,           // don't filter by row groups
				sortOrder       // The sort order
		);

		JSONArray resultSet = new JSONArray();
		JSONObject returnObj = new JSONObject();
		c.moveToFirst();
		while (c.isAfterLast() == false) {

			int totalColumn = c.getColumnCount();
			JSONObject rowObject = new JSONObject();

			for( int i=0 ;  i< totalColumn ; i++ ) {
				if( c.getColumnName(i) != null ) {
					try {

						if( c.getString(i) != null ) {
							Log.d("TAG_NAME", c.getString(i) );
							rowObject.put(c.getColumnName(i) ,  c.getString(i) );
						} else {
							rowObject.put( c.getColumnName(i) ,  "" );
						}
					}
					catch( Exception e ) {
						Log.d("TAG_NAME", e.getMessage()  );
					}
				}

			}

			resultSet.put(rowObject);
			c.moveToNext();
		}

		c.close();
		Log.d("EarthquakeMonitor", "MainActivity.readFromDB() resultSet length = > "+resultSet.length());
		Log.d("EarthquakeMonitor", "MainActivity.readFromDB() resultSet = > "+resultSet.toString());
		return resultSet;

	}

	public static JSONArray findEntryById(SQLiteManager dbHelper, String id_feed) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Log.d("EarthquakeMonitor", "SQLiteManager findEntryById() id_feed is "+id_feed);

		// Define a projection that specifies which columns from the database
		// you will actually use after this query.
		String[] projection = {
				FeedEntry.COLUMN_NAME_ID,
				FeedEntry.COLUMN_NAME_MAG,
				FeedEntry.COLUMN_NAME_PLACE,
				FeedEntry.COLUMN_NAME_TIME,
				FeedEntry.COLUMN_NAME_LAT,
				FeedEntry.COLUMN_NAME_LNG,
				FeedEntry.COLUMN_NAME_DPT
		};

		// How you want the results sorted in the resulting Cursor
		String sortOrder = "time DESC";

		Cursor c = db.rawQuery("SELECT * FROM "+FeedEntry.TABLE_NAME+" WHERE id_feed='"+id_feed+"'", null);
		/*Cursor c = db.query(
				FeedEntry.TABLE_NAME,  // The table to query
				projection,				// The columns to return
				"id_feed=",  // The columns for the WHERE clause
				new String[] {id_feed},           // The values for the WHERE clause
				null,           // don't group the rows
				null,           // don't filter by row groups
				sortOrder       // The sort order
		);*/

		JSONArray resultSet = new JSONArray();
		JSONObject returnObj = new JSONObject();
		c.moveToFirst();
		while (c.isAfterLast() == false) {

			int totalColumn = c.getColumnCount();
			JSONObject rowObject = new JSONObject();

			for( int i=0 ;  i< totalColumn ; i++ ) {
				if( c.getColumnName(i) != null ) {
					try {

						if( c.getString(i) != null ) {
							Log.d("TAG_NAME", c.getString(i) );
							rowObject.put(c.getColumnName(i) ,  c.getString(i) );
						} else {
							rowObject.put( c.getColumnName(i) ,  "" );
						}
					}
					catch( Exception e ) {
						Log.d("TAG_NAME", e.getMessage()  );
					}
				}

			}

			resultSet.put(rowObject);
			c.moveToNext();
		}

		c.close();
		Log.d("EarthquakeMonitor", "MainActivity.findEntryById() resultSet length = > "+resultSet.length());
		Log.d("EarthquakeMonitor", "MainActivity.findEntryById() resultSet = > "+resultSet.toString());
		return resultSet;

	}

}
