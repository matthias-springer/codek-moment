package edu.ucsd.cse110.team27.placeits.data;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.maps.model.LatLng;

import android.content.Context;

import edu.ucsd.cse110.team27.placeits.MapActivity;

public abstract class PlaceIts {

	private static final String DELIM = "~◙;◙~";
	private static final String NL = System.getProperty("line.separator");
	
	private Context appContext;

	protected List<PlaceIt> placeIts = new ArrayList<PlaceIt>();

	protected MapActivity activity;

	protected abstract String getFileName();

	public void add(PlaceIt placeIt) {
		placeIts.add(placeIt);
	}

	public void remove(PlaceIt placeIt) {
		placeIts.remove(placeIt);
	}

	public void removeAll() {
		for (PlaceIt placeIt : placeIts) {
			remove(placeIt);
		}
	}

	public PlaceIts(MapActivity activity) {
		this.activity = activity;
		this.appContext = activity.getApplicationContext();
	}

	public void save() throws IOException {
		OutputStreamWriter fileOut = new OutputStreamWriter(
				appContext.openFileOutput(getFileName(), Context.MODE_PRIVATE));

		for (PlaceIt placeit : placeIts) {
			String placeItData = placeit.getTitle() + DELIM
					+ placeit.getDescription() + DELIM
					+ placeit.getLocation().latitude + DELIM
					+ placeit.getLocation().longitude + NL;
			fileOut.write(placeItData);
		}

		fileOut.close();
	}

	public void load() throws IOException {
		try {
			BufferedReader fileIn = new BufferedReader(new InputStreamReader(
					appContext.openFileInput(getFileName())));

			String line = "";
			while ((line = fileIn.readLine()) != null) {
				String[] placeitData = line.split(DELIM);
				LatLng location = new LatLng(
						Double.parseDouble(placeitData[2]),
						Double.parseDouble(placeitData[3]));
				add(new PlaceIt(placeitData[0], placeitData[1], location));
			}

			fileIn.close();
		} catch (FileNotFoundException ex) {
			// No Place-Its file found, probably starting for the first time
		}
	}
}
