package edu.ucsd.cse110.team27.placeits.data.location;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.location.Location;

public class CategoryLocationStrategy extends PlaceItLocationStrategy {

	private List<String> categories = new ArrayList<String>();

	public CategoryLocationStrategy() {

	}

	public CategoryLocationStrategy(String cat1, String cat2, String cat3) {
		if (!cat1.equals("(none)"))
			categories.add(cat1);
		if (!cat2.equals("(none)"))
			categories.add(cat2);
		if (!cat3.equals("(none)"))
			categories.add(cat3);
	}

	public List<String> getCategories() {
		return categories;
	}

	@Override
	public String toFileString() {
		String returnValue = "CATEGORY";
		for (int i = 0; i < categories.size(); i++) {
			returnValue += DELIM + categories.get(i);
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

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof CategoryLocationStrategy))
			return false;

		CategoryLocationStrategy other = (CategoryLocationStrategy) o;
		return other.getCategories().equals(categories);
	}

	/**
	 * Categorized PlaceIts don't have a location.
	 */
	@Override
	public Location getLocation() {
		return null;
	}

}
