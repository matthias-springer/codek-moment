package edu.ucsd.cse110.team27.placeits.data.location;

import java.util.ArrayList;
import java.util.List;

public class CategoryLocationStrategy extends PlaceItLocationStrategy {

	private List<String> categories;

	@Override
	public String toFileString() {
		String returnValue = "";
		for (int i = 0; i < categories.size(); i++) {
			returnValue += categories.get(i);
			if (i < categories.size() - 1) {
				returnValue += DELIM;
			}
		}

		return returnValue;
	}

	@Override
	public void load(String data) {
		String[] splitted = data.split(DELIM);
		categories = new ArrayList<String>();

		for (int i = 1; i < splitted.length; i++) {
			categories.add(splitted[i]);
		}
	}

}
