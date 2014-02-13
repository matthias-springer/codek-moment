package edu.ucsd.cse110.team27.placeits.data;

import edu.ucsd.cse110.team27.placeits.MapActivity;

public class RecurringPlaceIts extends PlaceIts<PlaceItPrototype> {

	// what if the phone is powered down?
	
	public RecurringPlaceIts(MapActivity activity) {
		super(activity);
	}
	
	private static RecurringPlaceIts instance;
	
	/*
	 * Get Singleton instance of this list.
	 */
	public static RecurringPlaceIts getInstance(MapActivity activity) {
		if (instance == null) {
			instance = new RecurringPlaceIts(activity);
		}
		
		return instance;
	}

	@Override
	protected String getFileName() {
		// TODO Auto-generated method stub
		return null;
	}
	
	protected PlaceIt newInstance() {
		return new PlaceItPrototype();
	}
	
	public void createPlaceIts() {
		for (PlaceItPrototype prototype : placeIts) {
			if (prototype.isDue() && !ActivePlaceIts.getInstance(null).contains(prototype)) {
				prototype.createNew();
			}
		}
	}
}
