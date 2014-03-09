package edu.ucsd.cse110.team27.placeits.data;

import java.util.Date;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import edu.ucsd.cse110.team27.placeits.data.location.CategoryLocationStrategy;
import edu.ucsd.cse110.team27.placeits.data.location.PlaceItLocationStrategy;
import edu.ucsd.cse110.team27.placeits.data.location.StaticLocationStrategy;

public class PlaceIt {

	protected static final String DELIM = "~◙;◙~";

	private String title;

	private String description;

	protected PlaceItLocationStrategy locationStrategy;

	private Marker marker;
	
	private boolean print;
	
	private int ID; 
	
	public static final int PLACE_IT_ACTIVE = 0;
	
	public static final int PLACE_IT_PULLED = 1;
	
	public static final int PLACE_IT_PROTOTYPE = 2;
	
	public static final String PLACEIT_TYPE_KEY = "com.ucsd.edu.cse110.team27.placeits.PLACE_IT_TYPE";
	public static final String PLACEIT_POS_KEY = "com.ucsd.edu.cse110.team27.placeits.PLACE_IT_POS";
	

	public PlaceIt load(String line) {
		String[] placeitData = line.split(DELIM);
		this.title = placeitData[0];
		this.description = placeitData[1];
		this.locationStrategy = PlaceItLocationStrategy.create(placeitData[2]);
		return this;
	}

	public String toFileString() {
		return getTitle() + DELIM + getDescription() + DELIM + locationStrategy.toFileString();
	}
	
	public String toString() {
		return getTitle();
	}

	/**
	 * Creates a static location PlaceIt.
	 */
	public PlaceIt(String title, String description, LatLng latLng) {
		this.setTitle(title);
		this.setDescription(description);
		this.locationStrategy = new StaticLocationStrategy(latLng);
	}

	/**
	 * Creates a categorized PlaceIt.
	 */
	public PlaceIt(String title, String description, String cat1, String cat2, String cat3) {
		this.setTitle(title);
		this.setDescription(description);
		this.locationStrategy = new CategoryLocationStrategy(cat1, cat2, cat3);
	}
	
	public PlaceIt() {

	}
	
	public boolean isCategorizedPlaceIt() {
		return locationStrategy != null && locationStrategy.isCategorizedPlaceIt();
	}
	
	public boolean isWithinDistance(Location location, float distance) {
		if (locationStrategy != null) {
			return locationStrategy.isWithinDistance(location, distance);
		}
		else {
			return false;
		}
	}
	
	public PlaceItLocationStrategy getStrategy() {
		if (locationStrategy == null) locationStrategy = new StaticLocationStrategy(new LatLng(0, 0));
		return locationStrategy;
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
	
	/**
	 * Get location for the PlaceIt. If it is a categorized PlaceIt, then this method returns null.
	 */
	public Location getLocation() {
		return getStrategy().getLocation();
	}

	public LatLng getLatLng() {
		return getLocation() == null ? null : new LatLng(getLocation().getLatitude(), getLocation().getLongitude());
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
				&& other.getStrategy().equals(getStrategy());
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
