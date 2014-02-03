package edu.ucsd.cse110.team27.placeits.data;

import java.util.ArrayList;
import java.util.List;

public abstract class PlaceIts {

		protected List<PlaceIt> placeIts = new ArrayList<PlaceIt>();
		
		public void add(PlaceIt placeIt) {
			placeIts.add(placeIt);
		}
		
		public void remove(PlaceIt placeIt) {
			placeIts.remove(placeIt);
		}
		
}
