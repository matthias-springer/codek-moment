package edu.ucsd.cse110.team27.placeits.data;

public class RecurringPlaceIts extends PlaceIts<PlaceItPrototype> {

	private static RecurringPlaceIts instance;

	/*
	 * Get Singleton instance of this list.
	 */
	public static RecurringPlaceIts getInstance() {
		if (instance == null) {
			instance = new RecurringPlaceIts();
		}

		return instance;
	}

	@Override
	protected String getFileName() {
		return "RECURRING_PLACE_ITS";
	}

	protected PlaceIt newInstance() {
		return new PlaceItPrototype();
	}

	public void createPlaceIts() {
		for (PlaceItPrototype prototype : placeIts) {
			if (prototype.isDue()
					&& !ActivePlaceIts.getInstance().contains(prototype)) {
				prototype.createNew();
			}
		}
	}
}
