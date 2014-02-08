package edu.ucsd.cse110.team27.placeits.data;

import java.util.ArrayList;
import java.util.List;

import edu.ucsd.cse110.team27.placeits.MapActivity;

public abstract class PlaceIts {

		protected List<PlaceIt> placeIts = new ArrayList<PlaceIt>();
		
		protected MapActivity activity;
		
		public void add(PlaceIt placeIt) {
			placeIts.add(placeIt);
		}
		
		public void remove(PlaceIt placeIt) {
			placeIts.remove(placeIt);
		}
		
		public PlaceIts(MapActivity activity) {
			this.activity = activity;
		}
		
}
