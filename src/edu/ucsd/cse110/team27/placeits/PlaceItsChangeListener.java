package edu.ucsd.cse110.team27.placeits;

import edu.ucsd.cse110.team27.placeits.data.PlaceIt;
import android.content.Context;

interface PlaceItsChangeListener {
	  public Context getApplicationContext();
	  
	  public void removePlaceIt(PlaceIt placeIt);
	  
	  public void addPlaceIt(PlaceIt placeIt);
	  
	}