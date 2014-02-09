package edu.ucsd.cse110.team27.placeits.data;

import edu.ucsd.cse110.team27.placeits.MapActivity;

public class ActivePlaceIts extends PlaceIts {
	
	public ActivePlaceIts(MapActivity activity) {
		super(activity);
	}

	private static ActivePlaceIts instance;
	
	/*
	 * Get Singleton instance of this list.
	 */
	public static ActivePlaceIts getInstance(MapActivity activity) {
		if (instance == null) {
			instance = new ActivePlaceIts(activity);
		}
		
		return instance;
	}
	
	@Override
	public void add(PlaceIt placeIt) {
		super.add(placeIt);
		activity.addPlaceIt(placeIt);
	}
	
	@Override 
	public void remove(PlaceIt placeIt) {
		super.remove(placeIt);
		activity.removePlaceIt(placeIt);
	}
	
	@Override
	protected String getFileName() {
		return "ACTIVE_PLACE_ITS";
	}
}
