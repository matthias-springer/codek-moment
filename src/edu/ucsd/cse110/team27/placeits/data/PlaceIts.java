package edu.ucsd.cse110.team27.placeits.data;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import edu.ucsd.cse110.team27.placeits.MapActivity;

public abstract class PlaceIts<T extends PlaceIt> {
	
	public static MapActivity activity;

	protected List<T> placeIts = new ArrayList<T>();

	private static final String NL = System.getProperty("line.separator");
	
	protected abstract String getFileName();
	
	protected abstract PlaceIt newInstance();
	
	public void save() throws IOException {
		OutputStreamWriter fileOut = new OutputStreamWriter(
				activity.getApplicationContext().openFileOutput(getFileName(), Context.MODE_PRIVATE));

		for (PlaceIt placeit : placeIts) {
			fileOut.write(placeit.toString() + NL);
		}

		fileOut.close();
	}

	public void load() throws IOException {
		clear();
		
		try {
			BufferedReader fileIn = new BufferedReader(new InputStreamReader(
					activity.getApplicationContext().openFileInput(getFileName())));

			String line = "";
			while ((line = fileIn.readLine()) != null) {
				add((T) newInstance().load(line));
			}

			fileIn.close();
		} catch (FileNotFoundException ex) {
			// No Place-Its file found, probably starting for the first time
		}
	}
	
	public void add(T placeIt) {
		placeIts.add(placeIt);
	}

	public void remove(T placeIt) {
		placeIts.remove(placeIt);
	}

	public void removeAll() {
		clear();
	}

	public PlaceIts() {
	}
	
	public boolean contains(Object o) {
		return placeIts.contains(o);
	}

	public void clear() {
		while (!placeIts.isEmpty()) {
			remove(placeIts.get(0));
		}
	}
}
