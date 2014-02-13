package edu.ucsd.cse110.team27.placeits.data;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

public class PlaceIt {

	protected static final String DELIM = "~◙;◙~";

	private String title;

	private String description;

	private LatLng location;

	private Marker marker;

	// TODO: expiration date, recurring time

	public PlaceIt load(String line) {
		String[] placeitData = line.split(DELIM);
		this.title = placeitData[0];
		this.description = placeitData[1];
		this.location = new LatLng(Double.parseDouble(placeitData[2]),
				Double.parseDouble(placeitData[3]));

		return this;
	}

	public String toString() {
		return getTitle() + DELIM + getDescription() + DELIM
				+ getLocation().latitude + DELIM + getLocation().longitude;
	}

	public PlaceIt(String title, String description, LatLng location) {
		this.setTitle(title);
		this.setDescription(description);
		this.setLocation(location);
	}

	public PlaceIt() {

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

	@Override
	public boolean equals(Object o) {
		if (!((o instanceof PlaceIt) || (o instanceof PlaceItPrototype))) return false;
		
		PlaceIt other = (PlaceIt) o;
		return other.getTitle().equals(getTitle())
				&& other.getDescription().equals(getDescription())
				&& other.getLocation().equals(getLocation());
	}

	@Override
	public int hashCode() {
		return getTitle().hashCode() + getDescription().hashCode() + getLocation().hashCode();
	}
	
	/*
	 * public float distanceTo(Location location) { return
	 * this.location.distanceTo(location); }
	 */
}
