package edu.ucsd.cse110.team27.placeits.data;

public class PulledDownPlaceIts extends PlaceIts<PlaceIt> {

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

	@Override
	protected String getFileName() {
		return "PULLED_DOWN_PLACE_ITS";
	}

	@Override
	protected PlaceIt newInstance() {
		return new PlaceIt();
	}

}
