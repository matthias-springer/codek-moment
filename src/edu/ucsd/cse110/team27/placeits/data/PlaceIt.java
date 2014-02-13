package edu.ucsd.cse110.team27.placeits.data;

import java.util.Random;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

public class PlaceIt {

	private String title;
	
	private String description;
	
	private LatLng location;
	
	private Marker marker;
	
	private boolean print;
	
	private int ID; 
	
	public static final int PLACE_IT_ACTIVE = 0;
	
	public static final int PLACE_IT_PULLED = 1;
	
	public static final String PLACEIT_TYPE_KEY = "com.ucsd.edu.cse110.team27.placeits.PLACE_IT_TYPE";
	public static final String PLACEIT_POS_KEY = "com.ucsd.edu.cse110.team27.placeits.PLACE_IT_POS";
	
	// TODO: expiration date, recurring time
	
	public PlaceIt(String title, String description, LatLng location) {
		this.setTitle(title);
		this.setDescription(description);
		this.setLocation(location);
	}
	
	public int getID() {
		return this.ID;
	}
	
	public void setID(int id) {
		this.ID = id; 
	}
	
	public boolean getPrint() {
		return this.print;
	}
	
	public void setPrint(boolean bool) {
		this.print = bool;
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public LatLng getLocation() {
		return location;
	}
	
	public void setLocation(LatLng location) {
		this.location = location;
	}

	public Marker getMarker() {
		return marker;
	}

	public void setMarker(Marker marker) {
		this.marker = marker;
	}
	
	public int getType() {
		if(ActivePlaceIts.getInstance(null).contains(this)) {
			return PLACE_IT_ACTIVE;
		} else if (PulledDownPlaceIts.getInstance(null).contains(this)) {
			return PLACE_IT_PULLED;
		} else {
			return -1;
		}
	}
	
	@Override
	public String toString() {
		return title;
	}
	/*
	public float distanceTo(Location location) {
		return this.location.distanceTo(location);
	}
	*/

}
