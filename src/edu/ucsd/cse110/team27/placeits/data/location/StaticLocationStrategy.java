package edu.ucsd.cse110.team27.placeits.data.location;

import com.google.android.gms.maps.model.LatLng;

public class StaticLocationStrategy extends PlaceItLocationStrategy {

	private LatLng latLng;

	@Override
	public String toFileString() {
		return getLatLng().latitude + DELIM + getLatLng().longitude;
	}

	public LatLng getLatLng() {
		return latLng;
	}

	public void setLatLng(LatLng latLng) {
		this.latLng = latLng;
	}

	@Override
	public void load(String data) {
		String[] splitted = data.split(DELIM);
		this.latLng = new LatLng(Double.parseDouble(splitted[1]),
				Double.parseDouble(splitted[2]));
	}

}
