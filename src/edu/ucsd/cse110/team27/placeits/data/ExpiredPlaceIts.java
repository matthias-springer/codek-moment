package edu.ucsd.cse110.team27.placeits.data;

import edu.ucsd.cse110.team27.placeits.MapActivity;

public class ExpiredPlaceIts extends PlaceIts {
	
	public ExpiredPlaceIts(MapActivity activity) {
		super(activity);
	}

	private static ExpiredPlaceIts instance;
	
	/*
	 * Get Singleton instance of this list.
	 */
	public static ExpiredPlaceIts getInstance(MapActivity activity) {
		if (instance == null) {
			instance = new ExpiredPlaceIts(activity);
		}
		
		return instance;
	}
	
}
