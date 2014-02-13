package edu.ucsd.cse110.team27.placeits.data;

import edu.ucsd.cse110.team27.placeits.MapActivity;

public class PulledDownPlaceIts extends PlaceIts<PlaceIt> {
	
	public PulledDownPlaceIts(MapActivity activity) {
		super(activity);
	}

	private static PulledDownPlaceIts instance;
	
	/*
	 * Get Singleton instance of this list.
	 */
	public static PulledDownPlaceIts getInstance(MapActivity activity) {
		if (instance == null) {
			instance = new PulledDownPlaceIts(activity);
		}
		
		return instance;
	}
	
	@Override
	protected String getFileName() {
		return "PULLED_DOWN_PLACE_ITS";
	}
	
	@Override
	protected PlaceIt newInstance() {
		return new PlaceIt();
	}
	
}
