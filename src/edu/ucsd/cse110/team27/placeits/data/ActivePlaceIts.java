package edu.ucsd.cse110.team27.placeits.data;

public class ActivePlaceIts extends PlaceIts {
	
	private static ActivePlaceIts instance;
	
	/*
	 * Get Singleton instance of this list.
	 */
	public static ActivePlaceIts getInstance() {
		if (instance == null) {
			instance = new ActivePlaceIts();
		}
		
		return instance;
	}
	
}
