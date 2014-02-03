package edu.ucsd.cse110.team27.placeits.data;

public class ExpiredPlaceIts extends PlaceIts {
	
	private static ExpiredPlaceIts instance;
	
	/*
	 * Get Singleton instance of this list.
	 */
	public static ExpiredPlaceIts getInstance() {
		if (instance == null) {
			instance = new ExpiredPlaceIts();
		}
		
		return instance;
	}
	
}
