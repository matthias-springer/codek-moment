package edu.ucsd.cse110.team27.placeits.data.location;

import java.util.Date;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

public class StaticLocationStrategy extends PlaceItLocationStrategy {

	private LatLng latLng;
	
	public StaticLocationStrategy () {
		
	}
	
	public StaticLocationStrategy(LatLng latLng) {
		this.latLng = latLng;	
	}
	
	@Override
	public String toFileString() {
		return "STATIC" + DELIM + getLatLng().latitude + DELIM + getLatLng().longitude;
	}

	public LatLng getLatLng() {
		return latLng;
	}

	public void setLatLng(LatLng latLng) {
		this.latLng = latLng;
	}

	@Override
	public boolean isCategorizedPlaceIt() {
		return false;
	}
	
	@Override
	public void load(String data) {
		String[] splitted = data.split(DELIM);
		this.latLng = new LatLng(Double.parseDouble(splitted[1]),
				Double.parseDouble(splitted[2]));
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof StaticLocationStrategy)) return false;
		
		StaticLocationStrategy other = (StaticLocationStrategy) o;
		return other.getLatLng().equals(latLng);
	}
	
	public Location getLocation() {
		Location location = new Location("");
		location.setLatitude(latLng.latitude);
		location.setLongitude(latLng.longitude);
		location.setTime(new Date().getTime());
		
		return location;
	}

	@Override
	public boolean isWithinDistance(Location location, float distance) {
		return getLocation().distanceTo(location) <= distance;
	}
}
