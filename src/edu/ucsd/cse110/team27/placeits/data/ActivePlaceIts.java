package edu.ucsd.cse110.team27.placeits.data;

public class ActivePlaceIts extends PlaceIts<PlaceIt> {

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

	@Override
	protected PlaceIt newInstance() {
		return new PlaceIt();
	}

}
