package edu.ucsd.cse110.team27.placeits.data;

import java.util.Date;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

public class PlaceIt {

	protected static final String DELIM = "~◙;◙~";

	private String title;

	private String description;

	private LatLng latLng;

	private Marker marker;
	
	private boolean print;
	
	private int ID; 
	
	public static final int PLACE_IT_ACTIVE = 0;
	
	public static final int PLACE_IT_PULLED = 1;
	
	public static final String PLACEIT_TYPE_KEY = "com.ucsd.edu.cse110.team27.placeits.PLACE_IT_TYPE";
	public static final String PLACEIT_POS_KEY = "com.ucsd.edu.cse110.team27.placeits.PLACE_IT_POS";
	
	// TODO: expiration date, recurring time

	public PlaceIt load(String line) {
		String[] placeitData = line.split(DELIM);
		this.title = placeitData[0];
		this.description = placeitData[1];
		this.latLng = new LatLng(Double.parseDouble(placeitData[2]),
				Double.parseDouble(placeitData[3]));

		return this;
	}

	public String toString() {
		return getTitle() + DELIM + getDescription() + DELIM
				+ getLatLng().latitude + DELIM + getLatLng().longitude;
	}

	public PlaceIt(String title, String description, LatLng latLng) {
		this.setTitle(title);
		this.setDescription(description);
		this.setLatLng(latLng);
	}

	public PlaceIt() {

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

	public LatLng getLatLng() {
		return latLng;
	}

	public void setLatLng(LatLng latLng) {
		this.latLng = latLng;
	}
	
	public Location getLocation() {
		Location location = new Location("");
		location.setLatitude(latLng.latitude);
		location.setLongitude(latLng.longitude);
		location.setTime(new Date().getTime());
		
		return location;
	}

	public Marker getMarker() {
		return marker;
	}

	public void setMarker(Marker marker) {
		this.marker = marker;
	}

	@Override
	public boolean equals(Object o) {
		if (!((o instanceof PlaceIt) || (o instanceof PlaceItPrototype))) return false;
		
		PlaceIt other = (PlaceIt) o;
		return other.getTitle().equals(getTitle())
				&& other.getDescription().equals(getDescription())
				&& other.getLatLng().equals(getLatLng());
	}

	@Override
	public int hashCode() {
		return getTitle().hashCode() + getDescription().hashCode() + getLocation().hashCode();
	}
	
	public int getType() {
		// TODO: why do we need this? Law of Demeter
		if(ActivePlaceIts.getInstance().contains(this)) {
			return PLACE_IT_ACTIVE;
		} else if (PulledDownPlaceIts.getInstance().contains(this)) {
			return PLACE_IT_PULLED;
		} else {
			return -1;
		}
	}
	
	public float distanceTo(Location location) {
		return getLocation().distanceTo(location);
	}

}
