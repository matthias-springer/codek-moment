package edu.ucsd.cse110.team27.placeits.data;

import android.location.Location;

public class PlaceIt {

	private String title;
	
	private String description;
	
	private Location location;
	
	// TODO: expiration date, recurring time
	
	public PlaceIt(String title, String description, Location location) {
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

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}
	
	public float distanceTo(Location location) {
		return this.location.distanceTo(location);
	}
	
}
