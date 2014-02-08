package edu.ucsd.cse110.team27.placeits.data;

import com.google.android.gms.maps.model.LatLng;

public class PlaceIt {

	private String title;
	
	private String description;
	
	private LatLng location;
	
	// TODO: expiration date, recurring time
	
	public PlaceIt(String title, String description, LatLng location) {
		this.setTitle(title);
		this.setDescription(description);
		this.setLocation(location);
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
	
	/*
	public float distanceTo(Location location) {
		return this.location.distanceTo(location);
	}
	*/
}
