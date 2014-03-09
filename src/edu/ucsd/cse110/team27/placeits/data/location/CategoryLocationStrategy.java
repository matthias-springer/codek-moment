package edu.ucsd.cse110.team27.placeits.data.location;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.location.Location;
import android.util.Log;

public class CategoryLocationStrategy extends PlaceItLocationStrategy {

	private List<String> categories = new ArrayList<String>();

	public CategoryLocationStrategy() {

	}

	@Override
	public boolean isCategorizedPlaceIt() {
		return true;
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

	private String generateRequestString(Location location, float distance,
			String type) {
		String result = "https://maps.googleapis.com/maps/api/place/nearbysearch/json";

		result += "?location=" + location.getLatitude() + "%2C"
				+ location.getLongitude();
		result += "&radius=" + distance;
		result += "&types=" + type;
		result += "&sensor=false&key=AIzaSyCg8IzY5cUuURaSuoM4HpfF8HU7neHNPKk";
		
		Log.d("my location", result);
		
		return result;
	}

	@Override
	public boolean isWithinDistance(Location location, float distance) {
		Log.d("my location", location.getLatitude() + "," + location.getLongitude());
		
		try {
			for (String cat : categories) {
				HttpClient client = new DefaultHttpClient();

				HttpGet request = new HttpGet(generateRequestString(location,
						distance, cat));

				HttpResponse response = client.execute(request);

				BufferedReader reader = new BufferedReader(
						new InputStreamReader(
								response.getEntity().getContent(), "UTF-8"));
				
				StringBuilder builder = new StringBuilder();
				String aux = "";

				while ((aux = reader.readLine()) != null) {
				    builder.append(aux);
				}

				String json = builder.toString().replaceAll("\n", "");
				
				Log.d("my location", "RESULT::");
				Log.d("my location", json);
				
				JSONTokener tokener = new JSONTokener(json);
				JSONObject finalResult = new JSONObject(tokener);

				if (finalResult.getJSONArray("results").length() > 0) {
					return true;
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		// TODO Auto-generated method stub
		return false;
	}

}
