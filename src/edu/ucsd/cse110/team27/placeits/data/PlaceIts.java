package edu.ucsd.cse110.team27.placeits.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;

import edu.ucsd.cse110.team27.placeits.MapActivity;

public abstract class PlaceIts<T extends PlaceIt> {

	public static MapActivity activity;

	protected List<T> placeIts = new ArrayList<T>();

	private static final String NL = System.getProperty("line.separator");

	protected abstract String getFileName();

	protected abstract PlaceIt newInstance();

	private boolean autoSave = true;

	public int size() {
		return placeIts.size();
	}

	public void save() throws IOException {
		OutputStreamWriter fileOut = new OutputStreamWriter(activity
				.getApplicationContext().openFileOutput(getFileName(),
						Context.MODE_PRIVATE));

		for (PlaceIt placeit : placeIts) {
			fileOut.write(placeit.toString() + NL);
		}

		fileOut.close();
	}

	@SuppressWarnings("unchecked")
	public void load() throws IOException {
		autoSave = false;
		clear();

		try {
			BufferedReader fileIn = new BufferedReader(new InputStreamReader(
					activity.getApplicationContext().openFileInput(
							getFileName())));

			String line = "";
			while ((line = fileIn.readLine()) != null) {
				add((T) newInstance().load(line));
			}

			fileIn.close();
		} catch (IOException ex) {
			// No Place-Its file found, probably starting for the first time
		}
		autoSave = true;
	}

	public List<T> getList() {
		return this.placeIts;
	}

	public boolean contains(PlaceIt placeit) {
		return placeIts.contains(placeit);
	}

	public PlaceIt getAtPosition(int pos) {
		return placeIts.get(pos);
	}

	public void add(T placeIt) {
		placeIts.add(placeIt);

		if (autoSave) {
			try {
				save();
			} catch (IOException e) {
			}
		}
	}

	public void remove(T placeIt) {
		// Log.d("TEST", "REMOVING " + placeIt.hashCode());
		placeIts.remove(placeIt);
		
		if (autoSave) {
			try {
				save();
			} catch (IOException e) {
			}
		}
	}

	public boolean contains(Object o) {
		return placeIts.contains(o);
	}

	public void clear() {
		while (!placeIts.isEmpty()) {
			remove(placeIts.get(0));
		}
	}

	public int getPosition(T placeit) {
		return placeIts.indexOf(placeit);
	}
}
