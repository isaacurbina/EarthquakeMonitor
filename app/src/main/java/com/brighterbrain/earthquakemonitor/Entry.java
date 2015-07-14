package com.brighterbrain.earthquakemonitor;

public class Entry {
	public String id_feed;
	public double mag;
	public String place;
	public long time;
	public double lat;
	public double lng;
	public double dpt;
	
	public Entry(String id_feed, double mag, String place, long time, double lat, double lng, double dpt) {
		this.id_feed = id_feed;
		this.mag = mag;
		this.place = place;
		this.time = time;
		this.lat = lat;
		this.lng = lng;
		this.dpt = dpt;
	}
}
