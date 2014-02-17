package edu.ucsd.cse110.team27.placeits;

import android.content.Context;
import edu.ucsd.cse110.team27.placeits.data.PlaceIt;

public interface PlaceItsChangeListener {
	
	public void addPlaceIt(PlaceIt placeIt);

	public void removePlaceIt(PlaceIt placeIt);
	
	public Context getApplicationContext();
	
}
