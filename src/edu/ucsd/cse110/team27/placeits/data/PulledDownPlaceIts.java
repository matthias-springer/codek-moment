package edu.ucsd.cse110.team27.placeits.data;

public class PulledDownPlaceIts extends PlaceIts {
	
	private static PulledDownPlaceIts instance;
	
	/*
	 * Get Singleton instance of this list.
	 */
	public static PulledDownPlaceIts getInstance() {
		if (instance == null) {
			instance = new PulledDownPlaceIts();
		}
		
		return instance;
	}
	
}
